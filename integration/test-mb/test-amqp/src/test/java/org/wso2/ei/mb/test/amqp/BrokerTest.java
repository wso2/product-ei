/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.
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
package org.wso2.ei.mb.test.amqp;

import org.testng.log4testng.Logger;
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.ServerManager;

import java.io.File;
import java.io.IOException;

/**
 * This is the base test case for Broker related tests.
 */
public class BrokerTest {
    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static final Logger log = Logger.getLogger(BrokerTest.class);

    /**
     * Initiate new server manager instance.
     */
    public ServerManager serverManager = new ServerManager();

    /**
     * initiate configuration reader instance.
     */
    public ConfigurationReader configurationReader;

    /**
     * Initialize the test environment.
     */
    public void init() throws Exception {
        String archiveFilePath = System.getProperty("carbon.zip");

        // Create file instance for given path.
        File distributionArchive = new File(archiveFilePath);

        // Verify if given archive path is a file and not a directory before proceed.
        if (distributionArchive.exists() && !distributionArchive.isDirectory()) {
            try {
                String tempCarbonHome = serverManager.setupServerHome(archiveFilePath);

                // load client configs to map with valid username/password
                configurationReader = new ConfigurationReader(false);

                // Start Enterprise Integrator broker instance
                serverManager.startServer(tempCarbonHome);
            } catch (IOException e) {
                String errorMsg = "IO exception occurred when trying to initialize server environment";
                log.error(errorMsg, e);
                throw new IOException(errorMsg, e);
            }
        } else {
            throw new Exception("Server distribution not found. Colud not start the server");
        }
    }

    /**
     * Clean up after test case.
     */
    public void cleanup() throws IOException {
        serverManager.stopServer();
    }
}
