/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.passthru.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static java.io.File.separator;
import static org.testng.Assert.assertTrue;

/**
 * Test case for HttpAccesslogs generation
 */
public class HttpAccessLogTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private String httpLogDir;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        String nhttpFile = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + separator + "ESB" + separator +
                "passthru" + separator + "transport" + separator + "httpaccesslogs" + separator + "nhttp.properties";

        File srcFile = new File(nhttpFile);
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        httpLogDir = carbonHome + File.separator + "repository" + File.separator + "logs" + File.separator + "httpLogs";
        File log4jProperties = new File(carbonHome + File.separator + "repository" + File.separator + "conf" +
                File.separator + "log4j.properties");

        String propertyName = "nhttp.log.directory";
        createNewDir(httpLogDir);
        applyProperty(srcFile, propertyName, httpLogDir);
        applyProperty(log4jProperties, "log4j.logger.org.apache.synapse.transport.http.access", "DEBUG");
        serverConfigurationManager.restartGracefully();
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/httpaccesslogs/httpaccesslog_test_synapse.xml");
        Thread.sleep(30000);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb")
    public void testHttpAccessLogGeneration() throws Exception {
        BufferedReader bf = null;
        boolean found = false;
        String line;
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("HttpAccessLogsTestProxy"),
                    getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            File[] files = new File(httpLogDir).listFiles();
            Assert.assertTrue(files.length > 0,
                    "nhttp access logs were not written to the configured directory " + httpLogDir);
            String fileName = null;
            for (int i = 0; i < files.length; i++) {
                fileName = files[i].getName();
            }
            Thread.sleep(30000);
            bf = new BufferedReader(new FileReader(httpLogDir + File.separator + fileName));
            while ((line = bf.readLine()) != null) {
                int indexfound = line.indexOf("HttpAccessLogsTestProxy");
                if (indexfound > -1) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Access logs not generated for the proxy service.");
        } finally {
            if (bf != null) {
                bf.close();
            }
        }
    }

    /*
    create a new dir with the given path, deleting if already exists
     */
    private void createNewDir(String path) throws IOException {
        File dir = new File(path);
        if (dir.exists()) {
            dir.delete();
        } else {
            dir.mkdir();
        }
    }

    /**
     * Apply the given property and restart the server to
     *
     * @param srcFile
     * @param key
     * @param value
     * @throws Exception
     */
    private void applyProperty(File srcFile, String key, String value) throws Exception {
        File destinationFile = new File(srcFile.getName());
        Properties properties = new Properties();
        properties.load(new FileInputStream(srcFile));
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(destinationFile), null);
        serverConfigurationManager.applyConfigurationWithoutRestart(destinationFile);
    }

    /**
     * At the wind-up replace nhttp.properties file with previous
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void atEnd() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
            httpLogDir = null;
        }
    }
}
