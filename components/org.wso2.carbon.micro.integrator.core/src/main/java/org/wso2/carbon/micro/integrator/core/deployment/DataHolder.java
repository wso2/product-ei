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

package org.wso2.carbon.micro.integrator.core.deployment;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.Deployer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mediation.initializer.services.SynapseEnvironmentService;
import org.wso2.carbon.micro.integrator.core.deployment.synapse.deployer.SynapseAppDeployerConstants;
import org.wso2.carbon.proxyadmin.ProxyServiceDeployer;

import java.util.HashMap;

/**
 * This holds data shared across internals of micro integrator
 */
public class DataHolder {

    private static final Log log = LogFactory.getLog(DataHolder.class);

    private static DataHolder instance;
    private SynapseEnvironmentService synapseEnvironmentService;
    private HashMap<String, Deployer> synapseDeployers = new HashMap<String, Deployer>();
    private ConfigurationContext configContext;

    private DataHolder() {
    }

    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public SynapseEnvironmentService getSynapseEnvironmentService() {
        return synapseEnvironmentService;
    }

    public void setSynapseEnvironmentService(SynapseEnvironmentService synapseEnvironmentService) {
        this.synapseEnvironmentService = synapseEnvironmentService;
    }

    public ConfigurationContext getConfigContext() {
        return configContext;
    }

    public void setConfigContext(ConfigurationContext configContext) {
        this.configContext = configContext;
    }

    /**
     * Function to add synapse deployer
     *
     * @param type artifact type that deployed by the deployer
     * @param deployer deployer implementation
     */
    public void addSynapseDeployer(String type, Deployer deployer) {
        if (deployer == null) {
            log.error("Failed to add Deployer : deployer is null");
            return;
        }
        if (configContext != null) {
            // Initialize the Deployer
            deployer.init(configContext);
        } else {
            log.warn("ConfigurationContext has not been set. Deployer: " +
                    deployer.getClass() + "is not initialized");
        }
        synapseDeployers.put(type, deployer);

    }

    /**
     * Function to initialize deployers with default deployers. Need to invoke this before adding custom implementations
     */
    public void initializeDefaultSynapseDeployers() {
        addSynapseDeployer(SynapseAppDeployerConstants.PROXY_SERVICE_TYPE, new ProxyServiceDeployer());
    }

    /**
     * Function to retrieve related deployer for the given artifact type
     * @param type artifact type
     * @return related deployer, returns null if no deployer registered for given artifact type
     */
    public Deployer getDeployer(String type) {
        return synapseDeployers.get(type);
    }
}
