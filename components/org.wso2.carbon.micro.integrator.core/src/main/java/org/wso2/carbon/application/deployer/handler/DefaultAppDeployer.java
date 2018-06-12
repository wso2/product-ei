/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.application.deployer.handler;

import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.repository.util.DeploymentFileData;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.wso2.carbon.application.deployer.AppDeployerConstants;
import org.wso2.carbon.application.deployer.AppDeployerUtils;
import org.wso2.carbon.application.deployer.CarbonApplication;
import org.wso2.carbon.application.deployer.config.Artifact;
import org.wso2.carbon.application.deployer.config.CappFile;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.micro.integrator.core.internal.ServiceComponent;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This is one of the default handlers which is registered into the ApplicationManager. This
 * class deploys AAR services, JAXWS services, Data services and libs.
 */
public class DefaultAppDeployer implements AppDeploymentHandler {

    private static final Log log = LogFactory.getLog(DefaultAppDeployer.class);

    public static final String AAR_TYPE = "service/axis2";
    public static final String DS_TYPE = "service/dataservice";
    public static final String BUNDLE_TYPE = "bundle";
    public static final String MEDIATOR_TYPE = "lib/synapse/mediator";

    public static final String DS_DIR = "dataservices";

    private Map<String, Boolean> acceptanceList = null;

    /**
     * Deploy the artifacts which can be deployed through this deployer (Axis2 services,
     * JAXWS services ..).
     *
     * @param carbonApp  - find artifacts from this CarbonApplication instance
     * @param axisConfig - AxisConfiguration of the current tenant
     */
    public void deployArtifacts(CarbonApplication carbonApp, AxisConfiguration axisConfig) throws
                                                                                           DeploymentException{
        List<Artifact.Dependency> dependencies = carbonApp.getAppConfig().getApplicationArtifact()
                .getDependencies();
        deployRecursively(dependencies, axisConfig);
    }

    /**
     * Undeploys AAR, Data services, libs etc.
     *
     * @param carbonApp  - all information about the existing artifacts are in this instance
     * @param axisConfig - AxisConfiguration of the current tenant
     */
    public void undeployArtifacts(CarbonApplication carbonApp, AxisConfiguration axisConfig) {
        List<Artifact.Dependency> dependencies = carbonApp.getAppConfig().getApplicationArtifact()
                .getDependencies();
        undeployRecursively(dependencies, axisConfig);
    }

    /**
     * Installs an OSGi bundle into the OSGi environment through the bundle context..
     *
     * @param bundlePath - absolute path to the bundle to be installed..
     */
    private void installBundle(String bundlePath) {
        String bundlePathFormatted = getFormattedBundlePath(bundlePath);
	log.info("OSGi bundle in "+bundlePathFormatted+" location is about to be installed to Carbon Server.");

        try {
            Bundle bundle = CarbonCoreDataHolder.getInstance()
                    .getBundleContext().installBundle(bundlePathFormatted);
            if (bundle != null) {
		log.info("OSGi bundle "+bundle.getSymbolicName()+" installed to Carbon Server.");
                bundle.start();
		log.info("OSGi bundle "+bundle.getSymbolicName()+" successfully started on Carbon Server.");
            }
        } catch (BundleException e) {
            log.error("Error while installing bundle : " + bundlePathFormatted, e);
        }
    }
    
    private void uninstallBundle(String bundlePath){
    	String bundlePathFormatted = getFormattedBundlePath(bundlePath);
	log.info("OSGi bundle in "+bundlePathFormatted+" location is about to be uninstalled from Carbon Server.");

        try {
            Bundle bundle = CarbonCoreDataHolder.getInstance()
                    .getBundleContext().getBundle(bundlePathFormatted);
             if (bundle != null) {
		log.info("Uninstalling the OSGi bundle "+ bundle.getSymbolicName()+" from Carbon server.");
                bundle.uninstall();
		log.info("Sucessfully uninstalled the OSGi bundle "+ bundle.getSymbolicName()+" from Carbon server.");
            }
        } catch (BundleException e) {
            log.error("Error while uninstalling bundle : " + bundlePathFormatted, e);
        }
    }

	private String getFormattedBundlePath(String bundlePath) {
		String bundlePathFormatted = AppDeployerUtils.formatPath(bundlePath);

        // prepare the URL
        if (bundlePathFormatted.startsWith("/")) {
            // on linux
            bundlePathFormatted = "file://" + bundlePathFormatted;
        } else {
            // on windows
            bundlePathFormatted = "file:///" + bundlePathFormatted;
        }
		return bundlePathFormatted;
	}

    /**
     * Each artifact can have it's dependencies which are also artifacts. This method searches
     * the entire tree of artifacts to deploy default types..
     *
     * @param deps       - list of dependencies to be searched..
     * @param axisConfig - Axis config of the current tenant
     */
    private void deployRecursively(List<Artifact.Dependency> deps, AxisConfiguration axisConfig)
            throws DeploymentException{
        for (Artifact.Dependency dependency : deps) {
            Artifact artifact = dependency.getArtifact();
            if (artifact == null) {
                continue;
            }

            /*if (!isAccepted(artifact.getType())) {
                log.warn("Can't deploy artifact : " + artifact.getName() + " of type : " +
                        artifact.getType() + ". Required features are not installed in the system");
                continue;
            }*/

            List<CappFile> files = artifact.getFiles();
            if (files.size() != 1) {
                log.error(artifact.getType() + " type must have a single file to " +
                        "be deployed. But " + files.size() + " files found.");
                continue;
            }
            artifact.setDeploymentStatus(AppDeployerConstants.DEPLOYMENT_STATUS_PENDING);
            String fileName = artifact.getFiles().get(0).getName();
            String artifactPath = artifact.getExtractedPath() + File.separator + fileName;

            // get the relevant deployer
            Deployer deployer = getDeployer(axisConfig, artifact.getType());
            if (deployer != null) {
                try {
                    // Call the deploy method of the deployer
                    deployer.deploy(new DeploymentFileData(new File(artifactPath), deployer));
                    artifact.setDeploymentStatus(AppDeployerConstants.DEPLOYMENT_STATUS_DEPLOYED);
                } catch (DeploymentException e) {
                    artifact.setDeploymentStatus(AppDeployerConstants.DEPLOYMENT_STATUS_FAILED);
                    throw e;
                }
            } else if (MEDIATOR_TYPE.equals(artifact.getType())) { // skip bundle installation for mediators
                continue;
            } else if ((artifact.getType().startsWith("lib/") || BUNDLE_TYPE.
                    equals(artifact.getType()))
                       && AppDeployerUtils.getTenantId() ==
                          MultitenantConstants.SUPER_TENANT_ID) {
                // First copy the file into dropoins
                /**
                 * if the current artifact is a lib or bundle, we have to manually install it into the
                 * OSGi environment for the usage of the lib before the first restart..
                 * Important : This OSGi library installation is only allowed for the super tenant
                 */
                /**
                 * Removing code that copies jar artifact to dropins. We call installBundle from the extracted
                 * location instead.
                 */
                installBundle(artifactPath);
                artifact.setRuntimeObjectName(fileName);
            }
            // deploy the dependencies of the current artifact
            deployRecursively(artifact.getDependencies(), axisConfig);
        }
    }

    /**
     * Check whether a particular artifact type can be accepted for deployment. If the type doesn't
     * exist in the acceptance list, we assume that it doesn't require any special features to be
     * installed in the system. Therefore, that type is accepted.
     * If the type exists in the acceptance list, the acceptance value is returned.
     *
     * @param serviceType - service type to be checked
     * @return true if all features are there or entry is null. else false
     */
    private boolean isAccepted(String serviceType) {
        if (acceptanceList == null) {
            acceptanceList = AppDeployerUtils.buildAcceptanceList(ServiceComponent
                    .getRequiredFeatures());
        }
        Boolean acceptance = acceptanceList.get(serviceType);
        return (acceptance == null || acceptance);
    }


    /* Each artifact can have it's dependencies which are also artifacts. This method searches
    * the entire tree of artifacts to undeploy default types..
    *
    * @param deps       - list of deps to be searched..
    * @param axisConfig - AxisConfiguration of the current tenant
    */
    private void undeployRecursively(List<Artifact.Dependency> deps,
                                     AxisConfiguration axisConfig) {
        for (Artifact.Dependency dependency : deps) {
            Artifact artifact = dependency.getArtifact();
            if (artifact == null) {
                continue;
            }

            List<CappFile> files = artifact.getFiles();
            if (files.size() != 1) {
                log.error(artifact.getType() + " type must have a single file. But " +
                          files.size() + " files found.");
                continue;
            }

            String fileName = artifact.getFiles().get(0).getName();
            String artifactPath = artifact.getExtractedPath() + File.separator + fileName;

            // get the relevant deployer
            Deployer deployer = getDeployer(axisConfig, artifact.getType());
            if (deployer != null && AppDeployerConstants.DEPLOYMENT_STATUS_DEPLOYED.
                                equals(artifact.getDeploymentStatus())) {
                try {
                    // Call the deploy method of the deployer
                    deployer.undeploy(artifactPath);
                    artifact.setDeploymentStatus(AppDeployerConstants.DEPLOYMENT_STATUS_PENDING);
                    File artifactFile = new File(artifactPath);
                    if (artifactFile.exists() && !artifactFile.delete()) {
                        log.warn("Couldn't delete artifact file : " + artifactPath);
                    }
                } catch (DeploymentException e) {
                    artifact.setDeploymentStatus(AppDeployerConstants.DEPLOYMENT_STATUS_FAILED);
                    log.error("Error while undeploying artifact : " + artifactPath, e);
                }
            } else if (MEDIATOR_TYPE.equals(artifact.getType())) {
                continue;
            } else if (artifact.getType() != null && (artifact.getType().startsWith("lib/") ||
                                                      BUNDLE_TYPE.equals(artifact.getType()))
                       && AppDeployerUtils.getTenantId() ==
                          MultitenantConstants.SUPER_TENANT_ID) {
                /**
                 * Removing code that removes jar artifact from dropins. We call uninstallBundle from the extracted
                 * location instead.
                 */
                uninstallBundle(artifactPath);
            } else {
                continue;
            }
            // undeploy the dependencies of the current artifact
            undeployRecursively(artifact.getDependencies(), axisConfig);
        }
    }
    /**
     * Finds the correct deployer for the given artifact type
     *
     * @param axisConfig   - AxisConfiguration instance
     * @param artifactType - type of the artifact
     * @return Deployer instance
     */
    private Deployer getDeployer(AxisConfiguration axisConfig, String artifactType) {
        // access the deployment engine through axis config
        DeploymentEngine deploymentEngine = (DeploymentEngine) axisConfig.getConfigurator();
        Deployer deployer = null;

        // for each service type, select the correct deployer
        if (AAR_TYPE.equals(artifactType)) {
            deployer = deploymentEngine.getDeployer("axis2services", "aar");

            /* when ghost deployer is off, deployer is null since the ServiceDeployer is not
            registered as a normal deployer. Therefore the axis2service deployer is obtained as
            follows.

            Fix me properly.
            */
            if (deployer == null) {
                deployer = deploymentEngine.getServiceDeployer();
            }
        } else if (DS_TYPE.equals(artifactType)) {
            // TODO : Move data services out of carbon core
            deployer = deploymentEngine.getDeployer(DefaultAppDeployer.DS_DIR, "dbs");
        } else if (AppDeployerConstants.CARBON_APP_TYPE.equals(artifactType)) {
            deployer = deploymentEngine.getDeployer(AppDeployerConstants.CARBON_APPS, "car");
        }
        return deployer;
    }

}
