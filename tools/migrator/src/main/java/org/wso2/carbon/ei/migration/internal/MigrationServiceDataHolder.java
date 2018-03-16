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

import org.apache.axis2.context.ConfigurationContext;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.service.mgt.internal.DataHolder;
import org.wso2.carbon.tomcat.api.CarbonTomcatService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * Migration Service Data Holder
 */
public class MigrationServiceDataHolder {
    //Registry Service which is used to get registry data.
    private static RegistryService registryService;

    //Realm Service which is used to get tenant data.
    private static RealmService realmService;

    private static ServerConfigurationService serverConfigurationService;

    private static CarbonTomcatService carbonTomcatService;

    /**
     * Method to get RegistryService.
     *
     * @return registryService.
     */
    public static RegistryService getRegistryService() {
        return registryService;
    }

    /**
     * Method to set registry RegistryService.
     *
     * @param service RegistryService.
     */
    public static void setRegistryService(RegistryService service) {
        registryService = service;
    }

    /**
     * This method used to get RealmService.
     *
     * @return RealmService.
     */
    public static RealmService getRealmService() {
        return realmService;
    }

    /**
     * Method to set server configuration service.
     *
     * @param service RealmService.
     */
    public static void setRealmService(RealmService service) {
        realmService = service;
    }

    /**
     * This method used to get ServerConfigurationService.
     *
     * @return ServerConfigurationService.
     */
    public static ServerConfigurationService getServerConfigurationService() {
        return serverConfigurationService;
    }

    /**
     * Method to set server configuration service.
     *
     * @param serverConfigurationService RealmService.
     */
    public static void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        MigrationServiceDataHolder.serverConfigurationService = serverConfigurationService;
    }

    /**
     * This method used to get CarbonTomcatService.
     *
     * @return CarbonTomcatService.
     */
    public static CarbonTomcatService getCarbonTomcatService() {
        return carbonTomcatService;
    }

    /**
     * Method to set carbon tomcat service.
     *
     * @param carbonTomcatService CarbonTomcatService.
     */
    public static void setCarbonTomcatService(CarbonTomcatService carbonTomcatService) {
        MigrationServiceDataHolder.carbonTomcatService = carbonTomcatService;
    }
}
