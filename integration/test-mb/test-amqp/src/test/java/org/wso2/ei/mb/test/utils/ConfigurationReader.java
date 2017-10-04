/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.mb.test.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This file reads the automation xml configuration file and provides the set of parameters inside
 */
public class ConfigurationReader {

    // Create a map of Config key and Config value
    private Map<String, String> clientConfigPropertyMap = new HashMap<String, String>();

    /**
     * Initializes a config reader object while reading from config file
     *
     * @throws IOException
     */
    public ConfigurationReader(boolean invalid) throws IOException {

        InputStream propertyFileInputStream = getClass().getClassLoader()
                .getResourceAsStream(ConfigurationConstants.CLIENT_CONFIG_PROPERTY_FILE);
        Properties configProperties = new Properties();
        configProperties.load(propertyFileInputStream);

        clientConfigPropertyMap.put(ConfigurationConstants.CARBON_CLIENT_ID_PROPERTY,
                configProperties.getProperty(ConfigurationConstants.CARBON_CLIENT_ID_PROPERTY));
        clientConfigPropertyMap.put(ConfigurationConstants.CARBON_VIRTUAL_HOSTNAME_PROPERTY,
                configProperties.getProperty(ConfigurationConstants.CARBON_VIRTUAL_HOSTNAME_PROPERTY));
        clientConfigPropertyMap.put(ConfigurationConstants.CARBON_DEFAULT_HOSTNAME_PROPERTY,
                configProperties.getProperty(ConfigurationConstants.CARBON_DEFAULT_HOSTNAME_PROPERTY));
        clientConfigPropertyMap.put(ConfigurationConstants.CARBON_DEFAULT_PORT_PROPERTY,
                configProperties.getProperty(ConfigurationConstants.CARBON_DEFAULT_PORT_PROPERTY));
        if (invalid) {
            clientConfigPropertyMap.put(ConfigurationConstants.INVALID_USERNAME_PROPERTY,
                    configProperties.getProperty(ConfigurationConstants.INVALID_USERNAME_PROPERTY));
            clientConfigPropertyMap.put(ConfigurationConstants.INVALID_PASSWORD_PROPERTY,
                    configProperties.getProperty(ConfigurationConstants.INVALID_PASSWORD_PROPERTY));
        } else {
            clientConfigPropertyMap.put(ConfigurationConstants.DEFAULT_USERNAME_PROPERTY,
                    configProperties.getProperty(ConfigurationConstants.DEFAULT_USERNAME_PROPERTY));
            clientConfigPropertyMap.put(ConfigurationConstants.DEFAULT_PASSWORD_PROPERTY,
                    configProperties.getProperty(ConfigurationConstants.DEFAULT_PASSWORD_PROPERTY));
        }

        propertyFileInputStream.close();

    }

    /**
     * This method returns the map of client configs
     *
     * @return A Map of clientConfig Properties
     * @throws IOException
     */
    public Map<String, String> getClientConfigProperties() throws IOException {

        return clientConfigPropertyMap;
    }

}
