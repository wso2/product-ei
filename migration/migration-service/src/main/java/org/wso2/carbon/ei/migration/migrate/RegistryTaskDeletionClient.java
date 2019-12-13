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
package org.wso2.carbon.ei.migration.migrate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.ei.migration.internal.EIMigrationServiceDataHolder;
import org.wso2.carbon.registry.core.CollectionImpl;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;

/**
 * This class delete tasks related to scheduled message processors from the registry
 */
public class RegistryTaskDeletionClient {
    private static final Log log = LogFactory.getLog(RegistryTaskDeletionClient.class);
    private UserRegistry userRegistry;
    private Resource userRegistryResource;

    /**
     * Default constructor
     */
    public RegistryTaskDeletionClient() {
        String adminUserName = MigrationClientConfig.getInstance()
                                                    .getMigrationConfiguration()
                                                    .getProperty(MigrationConstants.ADMIN_USERNAME);

        if (adminUserName.isEmpty()) {
            throw new MigrationClientException("Invalid admin username");
        }

        try {
            this.userRegistry = EIMigrationServiceDataHolder.getRegistryService().getRegistry(adminUserName);
            this.userRegistryResource = userRegistry.get(MigrationConstants.ESB_TASK_PATH);
        } catch (RegistryException e) {
            throw new MigrationClientException("Error occurred while retrieving registry resources", e);
        }
    }

    /**
     * Delete tasks related to scheduled message processors from the registry
     */
    public void deleteRegistryTasks(){
        try {
            String[] tasks = ((CollectionImpl) userRegistryResource).getChildren();
            for (String task : tasks) {
                if (task.startsWith(MigrationConstants.ESB_TASK_PATH + MigrationConstants.SCHEDULED_MP_TASK_PREFIX)) {
                    log.info("Deleting task : [-1234][ESB_TASK][" +
                             task.substring(MigrationConstants.ESB_TASK_PATH.length()) + "]");
                    userRegistry.delete(task);
                }
            }
        } catch (RegistryException e) {
            throw new MigrationClientException("Error occurred while deleting registry tasks", e);
        }
    }
}
