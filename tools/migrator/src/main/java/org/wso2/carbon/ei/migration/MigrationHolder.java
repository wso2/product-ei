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
package org.wso2.carbon.ei.migration;

import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.service.migrator.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder class to hold version migrator objects.
 */
public class MigrationHolder {

    private static MigrationHolder migrationHolder = new MigrationHolder();
    private List<Migrator> migrationList = new ArrayList<>();

    private MigrationHolder() {
        migrationList.add(new DatasourceMigrator());
        migrationList.add(new EntitlementMediatorMigrator());
        migrationList.add(new EventSinkMigrator());
        migrationList.add(new InputOutputDataMigration());
        migrationList.add(new KeyStorePasswordMigrator());
        migrationList.add(new JMXProfileDataMigrator());
        migrationList.add(new SecurityPolicyPasswordMigrator());
        migrationList.add(new ServerProfileMigrator());
        migrationList.add(new SysLogPropertiesMigrator());
        migrationList.add(new UserStorePasswordMigrator());
    }

    public static MigrationHolder getInstance() {
        return MigrationHolder.migrationHolder;
    }

    public List<Migrator> getMigrationList() {
        return migrationList;
    }
}
