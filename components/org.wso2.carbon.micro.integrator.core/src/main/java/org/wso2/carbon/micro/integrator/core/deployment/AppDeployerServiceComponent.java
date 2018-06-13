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
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.application.deployer.handler.DefaultAppDeployer;
import org.wso2.carbon.application.deployer.synapse.FileRegistryResourceDeployer;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.dataservices.core.DBDeployer;
import org.wso2.carbon.event.publisher.core.EventPublisherDeployer;
import org.wso2.carbon.event.publisher.core.EventPublisherService;
import org.wso2.carbon.event.stream.core.EventStreamDeployer;
import org.wso2.carbon.event.stream.core.*;
import org.wso2.carbon.mediation.initializer.services.SynapseEnvironmentService;
import org.wso2.carbon.micro.integrator.core.deployment.application.deployer.CAppDeploymentManager;
import org.wso2.carbon.micro.integrator.core.deployment.artifact.deployer.ArtifactDeploymentManager;
import org.wso2.carbon.micro.integrator.core.deployment.internal.DeploymentServiceImpl;
import org.wso2.carbon.micro.integrator.core.deployment.synapse.deployer.SynapseAppDeployer;
import org.wso2.carbon.ntask.core.service.TaskService;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * @scr.component name="application.deployer.dscomponent" immediate="true"
 * @scr.reference name="org.wso2.carbon.configCtx"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContext" unbind="unsetConfigurationContext"
 * @scr.reference name="synapse.env.service"
 * interface="org.wso2.carbon.mediation.initializer.services.SynapseEnvironmentService"
 * cardinality="1..n" policy="dynamic" bind="setSynapseEnvironmentService"
 * unbind="unsetSynapseEnvironmentService"
 * @scr.reference name="ntask.service"
 * interface="org.wso2.carbon.ntask.core.service.TaskService"
 * cardinality="1..1" policy="dynamic" bind="setTaskService"
 * unbind="unsetTaskService"
 * @scr.reference name="eventstream.service"
 * interface="org.wso2.carbon.event.stream.core.EventStreamService"
 * cardinality="1..1" policy="dynamic" bind="setEventStreamService"
 * unbind="unsetEventStreamService"
 * @scr.reference name="eventpublisher.service"
 * interface="org.wso2.carbon.event.publisher.core.EventPublisherService"
 * cardinality="1..1" policy="dynamic" bind="setEventPublisherService"
 * unbind="unsetEventPublisherService"
 */
public class AppDeployerServiceComponent {

    private static final Log log = LogFactory.getLog(AppDeployerServiceComponent.class);

    private ConfigurationContext configCtx;
    private SynapseEnvironmentService synapseEnvironmentService;
    private TaskService taskService;

    protected void activate(ComponentContext ctxt) {

        log.debug("Activating AppDeployerServiceComponent");

        PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext
                .getThreadLocalCarbonContext();
        privilegedCarbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        privilegedCarbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        // Update DataHolder with SynapseEnvironmentService
        DataHolder.getInstance().setSynapseEnvironmentService(this.synapseEnvironmentService);
        DataHolder.getInstance().setConfigContext(this.configCtx);

        // Initialize Tasks Service
        if (taskService != null) {
            taskService.serverInitialized();
        } else {
            log.error("Task Service is not set. Unable to initialize task service");
        }

        // Initialize deployers
        ArtifactDeploymentManager artifactDeploymentManager = new ArtifactDeploymentManager(configCtx.getAxisConfiguration());
        CAppDeploymentManager cAppDeploymentManager = new CAppDeploymentManager(configCtx.getAxisConfiguration());
        initializeDeployers(artifactDeploymentManager, cAppDeploymentManager);

        // Deploy artifacts
        artifactDeploymentManager.deploy();

        // Deploy carbon applications
        try {
            cAppDeploymentManager.deploy();
        } catch (CarbonException e) {
            log.error("Error occurred while deploying carbon application", e);
        }

        // Register deployment service. This will allow to activate StartupFinalizerServiceComponent
        DeploymentService deploymentService = new DeploymentServiceImpl(artifactDeploymentManager, cAppDeploymentManager);
        ctxt.getBundleContext().registerService(DeploymentService.class.getName(),deploymentService, null);

        log.debug("micro-integrator artifact/Capp Deployment completed");
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

    protected void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    protected void unsetTaskService(TaskService taskService) {
    }

    protected void setEventStreamService(EventStreamService taskService) {
    }

    protected void unsetEventStreamService(EventStreamService taskService) {
    }

    protected void setEventPublisherService(EventPublisherService taskService) {
    }

    protected void unsetEventPublisherService(EventPublisherService taskService) {
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

    /**
     * Function to initialize deployer
     *
     * @param artifactDeploymentManager
     * @param cAppDeploymentManager
     */
    private void initializeDeployers(ArtifactDeploymentManager artifactDeploymentManager,
                                     CAppDeploymentManager cAppDeploymentManager) {

        String artifactRepoPath = configCtx.getAxisConfiguration().getRepository().getPath();

        log.debug("Initializing ArtifactDeploymentManager deployment manager");

        // Create data services deployer
        DBDeployer dbDeployer = new DBDeployer();
        dbDeployer.setDirectory(artifactRepoPath + DeploymentConstants.DSS_DIR_NAME);
        dbDeployer.setExtension(DeploymentConstants.DSS_TYPE_EXTENSION);

        EventStreamDeployer eventStreamDeployer = new EventStreamDeployer();
        eventStreamDeployer.init(configCtx);
        EventPublisherDeployer eventPublisherDeployer = new EventPublisherDeployer();
        eventPublisherDeployer.init(configCtx);
        // Register artifact deployers in ArtifactDeploymentManager
        try {
            artifactDeploymentManager.registerDeployer(artifactRepoPath + DeploymentConstants.DSS_DIR_NAME, dbDeployer);
        } catch (DeploymentException e) {
            log.error("Error occurred while registering data services deployer");
        }

        try {
            artifactDeploymentManager.registerDeployer(artifactRepoPath + DeploymentConstants.EVENT_STREAMS_DIR_NAME, eventStreamDeployer);
            artifactDeploymentManager.registerDeployer(artifactRepoPath + DeploymentConstants.EVENT_PUBLISHERS_DIR_NAME, eventPublisherDeployer);
        } catch (DeploymentException e) {
            log.error("Error occurred while registering event streams and publisher deployer");
        }
        // Initialize micro integrator carbon application deployer
        log.debug("Initializing carbon application deployment manager");

        // Initialize synapse deployers
        DataHolder.getInstance().initializeDefaultSynapseDeployers();

        // Register deployers in DeploymentEngine (required for CApp deployment)
        DeploymentEngine deploymentEngine = (DeploymentEngine) configCtx.getAxisConfiguration().getConfigurator();
        deploymentEngine.addDeployer(dbDeployer, DeploymentConstants.DSS_DIR_NAME, DeploymentConstants.DSS_TYPE_DBS);

        deploymentEngine.addDeployer(eventStreamDeployer, DeploymentConstants.EVENT_STREAMS_DIR_NAME, DeploymentConstants.XML_TYPE_EXTENSION);
        deploymentEngine.addDeployer(eventPublisherDeployer, DeploymentConstants.EVENT_PUBLISHERS_DIR_NAME, DeploymentConstants.XML_TYPE_EXTENSION);
        // Register application deployment handlers
        cAppDeploymentManager.registerDeploymentHandler(new FileRegistryResourceDeployer(
                synapseEnvironmentService.getSynapseEnvironment().getSynapseConfiguration().getRegistry()));
        cAppDeploymentManager.registerDeploymentHandler(new SynapseAppDeployer());
        cAppDeploymentManager.registerDeploymentHandler(new DefaultAppDeployer());

    }
}
