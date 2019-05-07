/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.migration.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.ei.migration.migrate.SecureVaultPasswordMigrationClient;
import org.wso2.carbon.registry.core.service.RegistryService;

/**
 * @scr.component name="org.wso2.carbon.ei.migration.client" immediate="true"
 * @scr.reference name="registry.service" interface="org.wso2.carbon.registry.core.service.RegistryService"
 * cardinality="1..1" policy="dynamic"  bind="setRegistryService" unbind="unsetRegistryService"
 */
public class EIMigrationServiceComponent {

    private static final Log log = LogFactory.getLog(EIMigrationServiceComponent.class);

    /**
     * Method to activate bundle.
     *
     * @param context OSGi component context.
     */
    protected void activate(ComponentContext context) {
        if (Boolean.valueOf(System.getProperty("migrate"))) {
            log.info("WSO2 EI migration bundle is activated");
            log.info("Initiating WSO2 EI migration");
            try {
                SecureVaultPasswordMigrationClient migrateSecureVaultPasswords = new SecureVaultPasswordMigrationClient();
                migrateSecureVaultPasswords.migratePasswords();
                log.info("Successfully completed password migration");
            } catch (Throwable e) {
                log.error("Error occurred during migration", e);
            }
        }
    }

    /**
     * Method to deactivate bundle.
     *
     * @param context OSGi component context.
     */
    protected void deactivate(ComponentContext context) {
        log.info("WSO2 EI migration bundle is deactivated");
    }

    protected void setRegistryService(RegistryService registryService) {
        EIMigrationServiceDataHolder.setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        EIMigrationServiceDataHolder.setRegistryService(null);
    }
}
