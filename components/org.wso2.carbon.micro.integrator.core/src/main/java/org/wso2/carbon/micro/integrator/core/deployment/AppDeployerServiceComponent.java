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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.mediation.initializer.services.SynapseEnvironmentService;
import org.wso2.carbon.micro.integrator.core.deployment.application.deployer.CAppDeploymentManager;
import org.wso2.carbon.micro.integrator.core.deployment.synapse.deployer.SynapseAppDeployer;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component name="application.deployer.dscomponent" immediate="true"
 * @scr.reference name="org.wso2.carbon.configCtx"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContext" unbind="unsetConfigurationContext"
 * @scr.reference name="synapse.env.service"
 * interface="org.wso2.carbon.mediation.initializer.services.SynapseEnvironmentService"
 * cardinality="1..n" policy="dynamic" bind="setSynapseEnvironmentService"
 * unbind="unsetSynapseEnvironmentService"
 */
public class AppDeployerServiceComponent {

    private static final Log log = LogFactory.getLog(AppDeployerServiceComponent.class);

    private ConfigurationContext configCtx;
    private SynapseEnvironmentService synapseEnvironmentService;

    protected void activate(ComponentContext ctxt) {

        log.debug("Activating AppDeployerServiceComponent");

        // Update DataHolder with SynapseEnvironmentService
        DataHolder.getInstance().setSynapseEnvironmentService(this.synapseEnvironmentService);
        DataHolder.getInstance().setConfigContext(this.configCtx);

        // Initialize synapse deployers
        DataHolder.getInstance().initializeDefaultSynapseDeployers();

        // Initialize micro integrator carbon application deployer
        log.debug("Initializing carbon application deployment manager");
        CAppDeploymentManager cAppDeploymentManager = new CAppDeploymentManager(configCtx.getAxisConfiguration());

        // Register application deployment handlers
        cAppDeploymentManager.registerDeploymentHandler(new SynapseAppDeployer());

        // Deploy carbon applications
        try {
            cAppDeploymentManager.deploy();
        } catch (CarbonException e) {
            log.error("Error occurred while deploying carbon application", e);
        }

    }

    protected void deactivate(ComponentContext ctxt) {
        log.debug("Deactivating AppDeployerServiceComponent");
    }

    /**
     * Receive an event about creation of ConfigurationContext.
     *
     * @param configCtx Instance of ConfigurationContextService which wraps server configuration context
     */
    protected void setConfigurationContext(ConfigurationContextService configCtx) {
        this.configCtx = configCtx.getServerConfigContext();
    }

    /**
     * Receive an event about destroying ConfigurationContext
     *
     * @param configCtx
     */
    protected void unsetConfigurationContext(ConfigurationContextService configCtx) {
        this.configCtx = null;
    }

    /**
     * Receive an event about the creation of a SynapseEnvironment. If this is
     * SuperTenant we have to wait until all the other constraints are met and actual
     * initialization is done in the activate method. Otherwise we have to do the activation here.
     *
     * @param synapseEnvironmentService SynapseEnvironmentService which contains information
     *                                  about the new Synapse Instance
     */
    protected void setSynapseEnvironmentService(SynapseEnvironmentService synapseEnvironmentService) {
        this.synapseEnvironmentService = synapseEnvironmentService;
    }

    /**
     * Receive an event about Destroying a SynapseEnvironment. This can be the super tenant
     * destruction or a tenant destruction.
     *
     * @param synapseEnvironmentService synapseEnvironment
     */
    protected void unsetSynapseEnvironmentService(SynapseEnvironmentService synapseEnvironmentService) {
        this.synapseEnvironmentService = null;
    }

}
