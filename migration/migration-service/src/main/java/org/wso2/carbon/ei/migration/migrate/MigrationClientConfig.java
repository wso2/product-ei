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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Migration client config class.
 */
public class MigrationClientConfig {
    private static MigrationClientConfig instance = new MigrationClientConfig();
    private static Properties migrationConfigurations;

    private String carbonHome = System.getProperty(MigrationConstants.CARBON_HOME);

    private MigrationClientConfig() {
        this.migrationConfigurations = loadProperties();
    }

    public static MigrationClientConfig getInstance() {
        return instance;
    }

    /**
     * Get migration configurations from migration config file
     *
     * @return migration properties
     */
    public Properties getMigrationConfiguration() {
        return migrationConfigurations;
    }

    /**
     * get properties from migration config file
     *
     * @return migration configurations
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        String migrationConfPath = Paths.get(carbonHome, MigrationConstants.MIGRATION_DIR,
                                             MigrationConstants.MIGRATION_CONF).toString();

        File dataSourceFile = new File(migrationConfPath);
        if (!dataSourceFile.exists()) {
            throw new MigrationClientException(MigrationConstants.MIGRATION_CONF + " file not found");
        }
        try (InputStream in = new FileInputStream(dataSourceFile)) {
            properties.load(in);
            if (new File(properties.getProperty(MigrationConstants.KEYSTORE_LOCATION)).exists()) {
                return properties;
            } else {
                throw new MigrationClientException("keystore file does not exist");
            }
        } catch (IOException e) {
            throw new MigrationClientException("Error loading properties from a file at : " + migrationConfPath);
        }
    }
}
