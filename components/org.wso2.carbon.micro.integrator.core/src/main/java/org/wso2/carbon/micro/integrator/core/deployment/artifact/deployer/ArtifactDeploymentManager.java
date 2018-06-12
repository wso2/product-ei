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

package org.wso2.carbon.micro.integrator.core.deployment.artifact.deployer;

import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.repository.util.DeploymentFileData;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manage and perform artifact deployment
 */
public class ArtifactDeploymentManager {

    private static final Log log = LogFactory.getLog(ArtifactDeploymentManager.class);

    private AxisConfiguration axisConfiguration;
    private HashMap<String, Deployer> dirToDeployerMap;

    public ArtifactDeploymentManager(AxisConfiguration axisConfiguration) {
        this.axisConfiguration = axisConfiguration;
        dirToDeployerMap = new HashMap<String, Deployer>();
    }

    /**
     * Function to execute artifact deployment
     */
    public void deploy() {

        Set<Map.Entry<String, Deployer>> deploymentEntries = dirToDeployerMap.entrySet();
        for (Map.Entry<String, Deployer> deployerEntry : deploymentEntries) {
            if (log.isDebugEnabled()) {
                log.debug("Deploying artifacts from: " + deployerEntry.getKey());
            }

            File confDirFile = new File(deployerEntry.getKey());
            if (confDirFile.isDirectory() && confDirFile.exists()) {
                File[] configFiles = confDirFile.listFiles();
                if (configFiles == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("No configurations found to deploy in: " + deployerEntry.getKey());
                    }
                    continue;
                }

                // Deploy each config file
                for (File configFile : configFiles) {
                    if (configFile.isFile()) {
                        try {
                            deployerEntry.getValue().deploy(new DeploymentFileData(configFile, deployerEntry.getValue()));
                        } catch (DeploymentException e) {
                            log.error("Error occurred while deploying : " + configFile.getName(), e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to register deployer
     *
     * @param directoryPath
     * @param deployer
     */
    public void registerDeployer(String directoryPath, Deployer deployer) throws DeploymentException {
        if (deployer != null) {
            dirToDeployerMap.put(directoryPath, deployer);
        } else {
            throw new DeploymentException("Registering null deployer for target directory: " + directoryPath);
        }
    }
}
