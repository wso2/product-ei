/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.ei.migration.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.ei.migration.MigrationClient;
import org.wso2.carbon.ei.migration.MigrationClientImpl;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.tomcat.api.CarbonTomcatService;
import org.apache.axis2.context.ConfigurationContext;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.carbon.ei.migration.internal" immediate="true"
 * @scr.reference name="realm.service"
 * interface="org.wso2.carbon.user.core.service.RealmService" cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="serverconfiguration.service"
 * interface="org.wso2.carbon.base.api.ServerConfigurationService" cardinality="1..1"
 * policy="dynamic" bind="setServerConfigurationService" unbind="unsetServerConfigurationService"
 * @scr.reference name="carbontomcat.service"
 * interface="org.wso2.carbon.tomcat.api.CarbonTomcatService" cardinality="1..1"
 * policy="dynamic" bind="setCarbonTomcatService" unbind="unsetCarbonTomcatService"
 */
public class MigrationServiceComponent {

    private static final Log log = LogFactory.getLog(MigrationServiceComponent.class);
    private ConfigurationContext configCtx;

    /**
     * Method to activate bundle.
     *
     * @param context OSGi component context.
     */
    protected void activate(ComponentContext context) {
        try {
            // if -Dmigrate option is used.
            String migrate = System.getProperty("migrate");
            if (Boolean.parseBoolean(migrate)) {
                log.info("Executing Migration client : " + MigrationClient.class.getName());
                MigrationClient migrationClientImpl = new MigrationClientImpl();
                migrationClientImpl.execute();
            }
            if (log.isDebugEnabled()) {
                log.debug("WSO2 EI migration bundle is activated");
            }
        } catch (Throwable e) {
            log.error("Error while initiating Config component", e);
        }

    }

    /**
     * Method to deactivate bundle.
     *
     * @param context OSGi component context.
     */
    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("WSO2 EI migration bundle is deactivated");
        }
    }

    /**
     * Method to set realm service.
     *
     * @param realmService service to get tenant data.
     */
    protected void setRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting RealmService to WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setRealmService(realmService);
    }

    /**
     * Method to unset realm service.
     *
     * @param realmService service to get tenant data.
     */
    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting RealmService from WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setRealmService(null);
    }

    /**
     * Method to set registry service.
     *
     * @param registryService service to get tenant data.
     */
    protected void setRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting RegistryService to WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setRegistryService(registryService);
    }

    /**
     * Method to unset registry service.
     *
     * @param registryService service to get tenant data.
     */
    protected void unsetRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting RegistryService from WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setRegistryService(null);
    }

    /**
     * Method to set server configuration service.
     *
     * @param serverConfigurationService ServerConfigurationService.
     */
    protected void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting ServerConfigurationService to WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setServerConfigurationService(serverConfigurationService);
    }

    /**
     * Method to unset server configuration service.
     *
     * @param serverConfigurationService ServerConfigurationService.
     */
    protected void unsetServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting ServerConfigurationService from WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setServerConfigurationService(null);
    }
    /**
     * Method to set carbon tomcat service.
     *
     * @param carbonTomcatService CarbonTomcatService.
     */
    protected void setCarbonTomcatService(CarbonTomcatService carbonTomcatService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting CarbonTomcatService to WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setCarbonTomcatService(carbonTomcatService);
    }

    /**
     * Method to unset carbon tomcat service.
     *
     * @param carbonTomcatService CarbonTomcatService.
     */
    protected void unsetCarbonTomcatService(CarbonTomcatService carbonTomcatService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting CarbonTomcatService from WSO2 EI Config component");
        }
        MigrationServiceDataHolder.setCarbonTomcatService(null);
    }
}
