/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.application.deployer.config;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.application.deployer.AppDeployerConstants;
import org.wso2.carbon.application.deployer.AppDeployerUtils;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the runtime representation of the entire app configuration.
 */
public class ApplicationConfiguration {

    private static final Log log = LogFactory.getLog(ApplicationConfiguration.class);

    public static final String ARTIFACTS_XML = "artifacts.xml";
    public static final String FEATURE_POSTFIX = ".feature.group";

    // TODO - define a correct ns
    public static final String APPLICATION_NS = "http://products.wso2.org/carbon";

    private String appName;
    private String appVersion;
    private Artifact applicationArtifact;

    /**
     * Constructor builds the cApp configuration by reading the artifacts.xml file from the
     * provided path.
     *
     * @param appXmlPath - absolute path to artifacts.xml file
     * @throws CarbonException - error while reading artifacts.xml
     */
    public ApplicationConfiguration(String appXmlPath) throws CarbonException {
        File f = new File(appXmlPath);
        if (!f.exists()) {
            throw new CarbonException("artifacts.xml file not found at : " + appXmlPath);
        }
        InputStream xmlInputStream = null;
        try {
            xmlInputStream = new FileInputStream(f);
            buildConfiguration(new StAXOMBuilder(xmlInputStream).getDocumentElement());
        } catch (FileNotFoundException e) {
            handleException("artifacts.xml File cannot be loaded from " + appXmlPath, e);
        } catch (XMLStreamException e) {
            handleException("Error while parsing the artifacts.xml file ", e);
        } finally {
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                } catch (IOException e) {
                    log.error("Error while closing input stream.", e);
                }
            }
        }
    }

    /**
     * Constructor builds the cApp configuration by reading the artifacts.xml file from the
     * provided xml input stream.
     *
     * @param xmlInputStream - input stream of the artifacts.xml
     * @throws CarbonException - error while reading artifacts.xml
     */
    public ApplicationConfiguration(InputStream xmlInputStream) throws CarbonException {
        try {
            buildConfiguration(new StAXOMBuilder(xmlInputStream).getDocumentElement());
        } catch (XMLStreamException e) {
            handleException("Error while parsing the artifacts.xml file content stream", e);
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public Artifact getApplicationArtifact() {
        return applicationArtifact;
    }

    public String getAppNameWithVersion() {
        if (getAppName() != null) {
            if (getAppVersion() != null) {
                return getAppName() + "_" + getAppVersion();
            }else{
                return getAppName();
            }
        }else{
            return null;
        }
    }

    /**
     * Builds the cApp configuration from the given OMElement which represents the artifacts.xml
     *
     * @param documentElement - root OMElement
     * @throws CarbonException - error while building
     */
    private void buildConfiguration(OMElement documentElement) throws CarbonException {
        if (documentElement == null) {
            throw new CarbonException("Document element for artifacts.xml is null. Can't build " +
                    "the cApp configuration");
        }

        Iterator artifactItr = documentElement.getChildrenWithLocalName(Artifact.ARTIFACT);
        Artifact appArtifact = null;
        while (artifactItr.hasNext()) {
            Artifact temp = AppDeployerUtils.populateArtifact((OMElement) artifactItr.next());
            if (AppDeployerConstants.CARBON_APP_TYPE.equals(temp.getType())) {
                appArtifact = temp;
                break;
            }
        }
        if (appArtifact == null) {
            throw new CarbonException("artifacts.xml is invalid. No Artifact " +
                    "found with the type - " + AppDeployerConstants.CARBON_APP_TYPE);
        }
        this.appName = appArtifact.getName();
        this.appVersion = appArtifact.getVersion();

        String[] serverRoles = AppDeployerUtils.readServerRoles();
        List<Artifact.Dependency> depsToRemove = new ArrayList<Artifact.Dependency>();

        /**
         * serverRoles contains regular expressions. So for each dependency's role, we have to
         * check whether there's a matching role from the list of serverRoles.
         */
        String role;
        for (Artifact.Dependency dep : appArtifact.getDependencies()) {
            boolean matched = false;
            role = dep.getServerRole();
            // try to find a matching serverRole for this dep
            for (String currentRole : serverRoles) {
                if (role.matches(currentRole)) {
                    matched = true;
                    break;
                }
            }
            
            if (!matched) {
                depsToRemove.add(dep);
            }
        }

        // removing unwanted dependencies for the current server
        for (Artifact.Dependency item : depsToRemove) {
            appArtifact.removeDependency(item);
        }
        this.applicationArtifact = appArtifact;
    }

    private void handleException(String msg, Exception e) throws CarbonException {
        log.error(msg, e);
        throw new CarbonException(msg, e);
    }

}
