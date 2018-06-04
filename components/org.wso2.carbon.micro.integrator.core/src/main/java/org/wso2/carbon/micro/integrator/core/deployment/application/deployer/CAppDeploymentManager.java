/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.micro.integrator.core.deployment.application.deployer;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.application.deployer.AppDeployerConstants;
import org.wso2.carbon.application.deployer.AppDeployerUtils;
import org.wso2.carbon.application.deployer.CarbonApplication;
import org.wso2.carbon.application.deployer.config.ApplicationConfiguration;
import org.wso2.carbon.application.deployer.config.Artifact;
import org.wso2.carbon.application.deployer.handler.AppDeploymentHandler;
import org.wso2.carbon.micro.integrator.core.deployment.synapse.deployer.SynapseAppDeployer;
import org.wso2.carbon.utils.FileManipulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;

/**
 * Carbon Application deployer to deploy synapse artifacts
 */
public class CAppDeploymentManager {

    private static final Log log = LogFactory.getLog(CAppDeploymentManager.class);

    private AxisConfiguration axisConfiguration;
    private List<AppDeploymentHandler> appDeploymentHandlers;

    public CAppDeploymentManager(AxisConfiguration axisConfiguration) {
        this.axisConfiguration = axisConfiguration;
        this.appDeploymentHandlers = new ArrayList<AppDeploymentHandler>();
    }

    public void deploy() throws CarbonException {

        String cAppSrcDir = axisConfiguration.getRepository().getPath() + AppDeployerConstants.CARBON_APPS;
        File cAppDir = new File(cAppSrcDir);

        if (cAppDir.isDirectory()) {
            File[] fileList = cAppDir.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    if (!isCAppArchiveFile(file.getName())) {
                        log.warn("Only .car files are processed. Hence " + file.getName() + " will be ignored");
                        continue;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Carbon Application detected : " + file.getName());
                    }

                    String cAppName = file.getName();
                    String targetCAppPath = cAppDir + File.separator + cAppName;

                    // Extract to temporary location
                    String tempExtractedDirPath = AppDeployerUtils.extractCarbonApp(targetCAppPath);

                    // Build the app configuration by providing the artifacts.xml path
                    ApplicationConfiguration appConfig = new ApplicationConfiguration(tempExtractedDirPath +
                                    ApplicationConfiguration.ARTIFACTS_XML);

                    // If we don't have features (artifacts) for this server, ignore
                    if (appConfig.getApplicationArtifact().getDependencies().size() == 0) {
                        log.warn("No artifacts found to be deployed in this server. " +
                                "Ignoring Carbon Application : " + cAppName);
                        return;
                    }

                    CarbonApplication currentApp = new CarbonApplication();
                    currentApp.setAppFilePath(targetCAppPath);
                    currentApp.setExtractedPath(tempExtractedDirPath);
                    currentApp.setAppConfig(appConfig);

                    // Set App Name
                    String appName = appConfig.getAppName();
                    if (appName == null) {
                        log.warn("No application name found in Carbon Application : " + cAppName + ". Using " +
                                "the file name as the application name");
                        appName = cAppName.substring(0, cAppName.lastIndexOf('.'));
                    }
                    currentApp.setAppName(appName);

                    // Set App Version
                    String appVersion = appConfig.getAppVersion();
                    if (appVersion != null && !("").equals(appVersion)) {
                        currentApp.setAppVersion(appVersion);
                    }

                    // deploy sub artifacts of this cApp
                    this.searchArtifacts(currentApp.getExtractedPath(), currentApp);

                    if (isArtifactReadyToDeploy(currentApp.getAppConfig().getApplicationArtifact())) {
                        // Now ready to deploy
                        for (AppDeploymentHandler appDeploymentHandler : appDeploymentHandlers) {
                            try {
                                appDeploymentHandler.deployArtifacts(currentApp, axisConfiguration);
                            } catch (DeploymentException e) {
                                log.error("Error occurred while deploying the Carbon application : " +
                                        currentApp.getAppName());
                            }
                        }
                    } else {
                        log.error("Some dependencies were not satisfied in cApp:" +
                                currentApp.getAppNameWithVersion() +
                                "Check whether all dependent artifacts are included in cApp file: " +
                                targetCAppPath);
                        FileManipulator.deleteDir(currentApp.getExtractedPath());
                        return;
                    }

                    // Deployment Completed
                    currentApp.setDeploymentCompleted(true);
                    //this.addCarbonApp(tenantId, currentApp);
                    log.info("Successfully Deployed Carbon Application : " + currentApp.getAppNameWithVersion() +
                            AppDeployerUtils.getTenantIdLogString(AppDeployerUtils.getTenantId()));

                }
            }
        }
    }


    /**
     * Checks whether a given file is a jar or an aar file.
     *
     * @param filename file to check
     * @return Returns boolean.
     */
    public static boolean isCAppArchiveFile(String filename) {
        return (filename.endsWith(".car"));
    }

    /**
     * Function to register application deployers
     *
     * @param handler - app deployer which implements the AppDeploymentHandler interface
     */
    public synchronized void registerDeploymentHandler(AppDeploymentHandler handler) {
        appDeploymentHandlers.add(handler);
    }

    /**
     * Deploys all artifacts under a root artifact..
     *
     * @param rootDirPath - root dir of the extracted artifact
     * @param parentApp - capp instance
     * @throws org.wso2.carbon.CarbonException - on error
     */
    private void searchArtifacts(String rootDirPath, CarbonApplication parentApp) throws CarbonException {
        File extractedDir = new File(rootDirPath);
        File[] allFiles = extractedDir.listFiles();
        if (allFiles == null) {
            return;
        }

        // list to keep all artifacts
        List<Artifact> allArtifacts = new ArrayList<Artifact>();

        // search for all directories under the extracted path
        for (File artifactDirectory : allFiles) {
            if (!artifactDirectory.isDirectory()) {
                continue;
            }

            String directoryPath = AppDeployerUtils.formatPath(artifactDirectory.getAbsolutePath());
            String artifactXmlPath =  directoryPath + File.separator + Artifact.ARTIFACT_XML;

            File f = new File(artifactXmlPath);
            // if the artifact.xml not found, ignore this dir
            if (!f.exists()) {
                continue;
            }

            Artifact artifact = null;
            InputStream xmlInputStream = null;
            try {
                xmlInputStream = new FileInputStream(f);
                artifact = this.buildAppArtifact(parentApp, xmlInputStream);
            } catch (FileNotFoundException e) {
                handleException("artifacts.xml File cannot be loaded from " + artifactXmlPath, e);
            } finally {
                if (xmlInputStream != null) {
                    try {
                        xmlInputStream.close();
                    } catch (IOException e) {
                        log.error("Error while closing input stream.", e);
                    }
                }
            }

            if (artifact == null) {
                return;
            }
            artifact.setExtractedPath(directoryPath);
            allArtifacts.add(artifact);
        }
        Artifact appArtifact = parentApp.getAppConfig().getApplicationArtifact();
        buildDependencyTree(appArtifact, allArtifacts);
    }

    /**
     * Builds the artifact from the given input steam and adds it as a dependency in the provided
     * parent carbon application.
     *
     * @param parentApp - parent application
     * @param artifactXmlStream - xml input stream of the artifact.xml
     * @return - Artifact instance if successfull. otherwise null..
     * @throws CarbonException - error while building
     */
    public Artifact buildAppArtifact(CarbonApplication parentApp, InputStream artifactXmlStream)
            throws CarbonException {
        Artifact artifact = null;
        try {
            OMElement artElement = new StAXOMBuilder(artifactXmlStream).getDocumentElement();

            if (Artifact.ARTIFACT.equals(artElement.getLocalName())) {
                artifact = AppDeployerUtils.populateArtifact(artElement);
            } else {
                log.error("artifact.xml is invalid. Parent Application : "
                        + parentApp.getAppNameWithVersion());
                return null;
            }
        } catch (XMLStreamException e) {
            handleException("Error while parsing the artifact.xml file ", e);
        }

        if (artifact == null || artifact.getName() == null) {
            log.error("Invalid artifact found in Carbon Application : " + parentApp.getAppNameWithVersion());
            return null;
        }
        return artifact;
    }

    /**
     * Checks whether the given cApp artifact is complete with all it's dependencies. Recursively
     * checks all it's dependent artifacts as well..
     *
     * @param rootArtifact - artifact to check
     * @return true if ready, else false
     */
    private boolean isArtifactReadyToDeploy(Artifact rootArtifact) {
        if (rootArtifact == null) {
            return false;
        }
        boolean isReady = true;
        for (Artifact.Dependency dep : rootArtifact.getDependencies()) {
            isReady = isArtifactReadyToDeploy(dep.getArtifact());
            if (!isReady) {
                return false;
            }
        }
        if (rootArtifact.unresolvedDepCount > 0) {
            isReady = false;
        }
        return isReady;
    }

    /**
     * If the given artifact is a dependent artifact for the rootArtifact, include it as
     * the actual dependency. The existing one is a dummy one. So remove it. Do this recursively
     * for the dependent artifacts as well..
     *
     * @param rootArtifact - root to start search
     * @param allArtifacts - all artifacts found under current cApp
     */
    public void buildDependencyTree(Artifact rootArtifact, List<Artifact> allArtifacts) {
        for (Artifact.Dependency dep : rootArtifact.getDependencies()) {
            for (Artifact temp : allArtifacts) {
                if (dep.getName().equals(temp.getName())) {
                    String depVersion = dep.getVersion();
                    String attVersion = temp.getVersion();
                    if ((depVersion == null && attVersion == null) ||
                            (depVersion != null && depVersion.equals(attVersion))) {
                        dep.setArtifact(temp);
                        rootArtifact.unresolvedDepCount--;
                        break;
                    }
                }
            }

            // if we've found the dependency, check for it's dependencies as well..
            if (dep.getArtifact() != null) {
                buildDependencyTree(dep.getArtifact(), allArtifacts);
            }
        }
    }

    private void handleException(String msg, Exception e) throws CarbonException {
        log.error(msg, e);
        throw new CarbonException(msg, e);
    }


}
