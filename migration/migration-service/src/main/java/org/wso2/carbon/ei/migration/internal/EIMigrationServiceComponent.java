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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.ei.migration.migrate.SecureVaultPasswordMigrationClient;
import org.wso2.carbon.ei.migration.migrate.RegistryTaskDeletionClient;
import org.wso2.carbon.registry.core.service.RegistryService;

@Component(
        name = "org.wso2.carbon.ei.migration.client",
        immediate = true)
public class EIMigrationServiceComponent {

    private static final Log log = LogFactory.getLog(EIMigrationServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {
        log.info("WSO2 EI migration bundle is activated");
        if (System.getProperty("migrate.from.product.version").startsWith("esb")) {
            migrateSecureVaultPasswords();
            deleteScheduledMPRegistryTasks();
        } else if (System.getProperty("migrate.from.product.version").equalsIgnoreCase("ei650")) {
            deleteScheduledMPRegistryTasks();
        } else {
            log.error("Provided product version is invalid");
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        log.info("WSO2 EI migration bundle is deactivated");
    }

    @Reference(
            name = "registry.service",
            service = org.wso2.carbon.registry.core.service.RegistryService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistryService")
    protected void setRegistryService(RegistryService registryService) {
        EIMigrationServiceDataHolder.setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        EIMigrationServiceDataHolder.setRegistryService(null);
    }

    /**
     * Migrate secure vault passwords
     */
    private void migrateSecureVaultPasswords() {
        log.info("Initiating WSO2 EI secureVault password migration");
        try {
            SecureVaultPasswordMigrationClient migrateSecureVaultPasswords = new SecureVaultPasswordMigrationClient();
            migrateSecureVaultPasswords.migratePasswords();
            log.info("Successfully completed password migration");
        } catch (Throwable e) {
            log.error("Error occurred during secure vault password migration", e);
        }
    }

    /**
     * Delete scheduled message processor tasks
     */
    private void deleteScheduledMPRegistryTasks() {
        log.info("Initiating WSO2 EI scheduled message processor task deletion");
        try {
            RegistryTaskDeletionClient registryTaskDeletionClient = new RegistryTaskDeletionClient();
            registryTaskDeletionClient.deleteRegistryTasks();
            log.info("Successfully completed task deletion");
        } catch (Throwable e) {
            log.error("Error occurred during registry task deletion", e);
        }
    }
}
