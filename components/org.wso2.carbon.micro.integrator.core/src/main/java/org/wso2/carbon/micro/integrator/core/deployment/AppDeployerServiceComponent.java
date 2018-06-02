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
import org.apache.synapse.core.SynapseEnvironment;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.mediation.initializer.services.SynapseEnvironmentService;
import org.wso2.carbon.micro.integrator.core.deployment.application.deployer.CAppDeployer;
import org.wso2.carbon.utils.ConfigurationContextService;

import javax.xml.crypto.Data;

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

public class AppDeployerServiceComponent implements ServiceListener {

    private static final Log log = LogFactory.getLog(AppDeployerServiceComponent.class);

    private ConfigurationContext configCtx;
    private SynapseEnvironmentService synapseEnvironmentService;

    protected void activate(ComponentContext ctxt) {
        log.info("*********** ACTIVATE **********");

        // Update DataHolder with SynapseEnvironmentService
        DataHolder.getInstance().setSynapseEnvironmentService(this.synapseEnvironmentService);
        DataHolder.getInstance().setConfigContext(this.configCtx);

        // Initialize synapse deployers
        DataHolder.getInstance().initializeDefaultSynapseDeployers();

        // Deploy carbon applications
        try {
            new CAppDeployer(configCtx.getAxisConfiguration()).deploy();
        } catch (CarbonException e) {
            log.error(e.getMessage(), e);
        }

    }

    protected void deactivate(ComponentContext ctxt) {
        log.info("*********** DEACTIVATE **********");
    }

    protected void setConfigurationContext(ConfigurationContextService configCtx) {
        log.info("*********** setConfigurationContext **********");

        this.configCtx = configCtx.getServerConfigContext();
    }

    protected void unsetConfigurationContext(ConfigurationContextService configCtx) {
        log.info("*********** unsetConfigurationContext **********");

        this.configCtx = null;
    }

    /**
     * Here we receive an event about the creation of a SynapseEnvironment. If this is
     * SuperTenant we have to wait until all the other constraints are met and actual
     * initialization is done in the activate method. Otherwise we have to do the activation here.
     *
     * @param synapseEnvironmentService SynapseEnvironmentService which contains information
     *                                  about the new Synapse Instance
     */
    protected void setSynapseEnvironmentService(
            SynapseEnvironmentService synapseEnvironmentService) {
        log.info("*********** setSynapseEnvironmentService **********");

        this.synapseEnvironmentService = synapseEnvironmentService;
    }

    /**
     * Here we receive an event about Destroying a SynapseEnvironment. This can be the super tenant
     * destruction or a tenant destruction.
     *
     * @param synapseEnvironmentService synapseEnvironment
     */
    protected void unsetSynapseEnvironmentService(
            SynapseEnvironmentService synapseEnvironmentService) {
        log.info("*********** setSynapseEnvironmentService **********");

        this.synapseEnvironmentService = null;
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        //Nothing to do yet
        log.info("*********** serviceChanged ********** ");
    }
}
