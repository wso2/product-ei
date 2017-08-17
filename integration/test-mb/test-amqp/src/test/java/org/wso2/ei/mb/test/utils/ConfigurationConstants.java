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

/**
 * This class contains the constants that are being used by the configuration files.
 */
public class ConfigurationConstants {

    /**
     * JMS Clients config property file.
     */
    public static final String CLIENT_CONFIG_PROPERTY_FILE = "clientconfig.properties";

    /**
     * JMS client ID used to create the tcp connection
     */
    public static final String CARBON_CLIENT_ID_PROPERTY = "carbonClientId";

    /**
     * JMS virtual hostname used to create the tcp connection
     */
    public static final String CARBON_VIRTUAL_HOSTNAME_PROPERTY = "carbonVirtualHostName";

    /**
     * JMS default hostname used to create the tcp connection
     */
    public static final String CARBON_DEFAULT_HOSTNAME_PROPERTY = "carbonDefaultHostName";

    /**
     * Default port for client connection.
     */
    public static final String CARBON_DEFAULT_PORT_PROPERTY = "carbonDefaultPort";

    /**
     * Default Username for carbon server.
     */
    public static final String DEFAULT_USERNAME_PROPERTY = "userName";

    /**
     * Default password for carbon server.
     */
    public static final String DEFAULT_PASSWORD_PROPERTY = "password";

}
