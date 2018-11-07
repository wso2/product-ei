/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.integration.common.utils.backend;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;
import java.io.IOException;

/**
 * This class allows a test case to edit the main server configuration (currently broker.xml) and apply it to the
 * server before execution.
 */
public class ConfigurationEditor {

    /**
     * File name prefix used for the updated configuration file.
     */
    public static final String UPDATED_CONFIG_FILE_PREFIX = "updated_";

    /**
     * Configuration property holder
     */
    public XMLConfiguration configuration;

    /**
     * File path of the original configuration file.
     */
    public String originalConfigFilePath;

    /**
     * File path of the updated configuration file.
     */
    public String updatedConfigFilePath;

    public ConfigurationEditor(String originalConfigFilePath) throws ConfigurationException {
        this.originalConfigFilePath = originalConfigFilePath;

        configuration = new XMLConfiguration(this.originalConfigFilePath);

        // Support XPath queries.
        configuration.setExpressionEngine(new XPathExpressionEngine());

        configuration.setDelimiterParsingDisabled(true); // If we don't do this,
        // we can't add a new configuration to the compositeConfiguration by code.
    }

    /**
     * Update a property in loaded original configuration
     * @param property AndesConfiguration property.
     * @param value New value to be set
     * @return the set value
     */
    public String updateProperty(AndesConfiguration property, String value) {
        configuration.setProperty(property.get().getKeyInFile(),value);
        return value;
    }

    /**
     * Apply modified configuration and restart server
     *
     * @param serverConfigurationManager Server configuration manager object from automation engine.
     * @return true if the update was successful.
     * @throws IOException
     * @throws AutomationUtilException
     * @throws ConfigurationException
     */
    public boolean applyUpdatedConfigurationAndRestartServer(ServerConfigurationManager serverConfigurationManager)
            throws IOException, AutomationUtilException, ConfigurationException {

        //Rename original configuration file to original_broker.xml
        String originalConfigFileDirectory = originalConfigFilePath.substring(0,originalConfigFilePath.lastIndexOf(File.separator));
        String originalConfigFileName = originalConfigFilePath.substring(originalConfigFilePath.lastIndexOf(File.separator));

        updatedConfigFilePath = originalConfigFileDirectory + UPDATED_CONFIG_FILE_PREFIX + originalConfigFileName;
        configuration.save(updatedConfigFilePath);

        serverConfigurationManager.applyConfiguration(new File(updatedConfigFilePath), new File(originalConfigFilePath), true, true);

        return true;
    }

}
