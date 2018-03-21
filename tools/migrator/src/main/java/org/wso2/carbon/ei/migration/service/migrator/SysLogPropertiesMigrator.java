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
package org.wso2.carbon.ei.migration.service.migrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.service.RegistryDataManager;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;

/**
 * Password transformation class for System Log.
 */
public class SysLogPropertiesMigrator extends Migrator {

    private static final Log log = LogFactory.getLog(SysLogPropertiesMigrator.class);

    @Override
    public void migrate() {
        migrateSysLogPropertiesPassword();
    }

    private void migrateSysLogPropertiesPassword() {
        log.info(Constant.MIGRATION_LOG + "Migration starting on SYSLOG_PROPERTIES file");
        boolean isIgnoreForInactiveTenants = Boolean.parseBoolean(System.getProperty(Constant.IGNORE_INACTIVE_TENANTS));
        try {
            RegistryDataManager.getInstance().migrateSysLogPropertyPassword(isIgnoreForInactiveTenants);
        } catch (UserStoreException e) {
            log.error("Error while retrieving all tenants. ", e);
        } catch (RegistryException e) {
            log.error("Error while accessing registry and loading SYSLOG_PROPERTIES file.", e);
        } catch (CryptoException e) {
            log.error("Error while encrypting/decrypting SYSLOG_PROPERTIES password. ", e);
        }
    }
}
