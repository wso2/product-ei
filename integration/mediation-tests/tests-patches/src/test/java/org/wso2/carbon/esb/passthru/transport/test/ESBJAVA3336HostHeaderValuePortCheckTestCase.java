/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
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
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertFalse;

public class ESBJAVA3336HostHeaderValuePortCheckTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(context);
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        File log4jProperties = new File(carbonHome + File.separator + "conf" + File.separator + "log4j2.properties");
        String loggers = getProperty(log4jProperties, "loggers");
        if (loggers == null) {
            Assert.assertTrue(false, "Loggers property became null");
        }
        applyProperty(log4jProperties, "loggers", "" + loggers + ", synapse-transport-http-wire");
        applyProperty(log4jProperties, "logger.synapse-transport-http-wire.name",
                "org.apache.synapse.transport.http.wire");
        serverConfigurationManager.reInitializeConfigData();
        applyProperty(log4jProperties, "logger.synapse-transport-http-wire.level", "DEBUG");
        serverConfigurationManager.restartGracefully();
        init();
        verifyProxyServiceExistence("ESBJAVA3336httpsBackendProxyService");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "Test wrong port(80) attached with the HOST_HEADERS for https backend")
    public void testHOST_HEADERPropertyTest() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ESBJAVA3336httpsBackendProxyService"), null, "WSO2");
        } catch (Exception e) {

        }

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        boolean errorLogFound = false;
        for (LogEvent log : logs) {
            if (log.getMessage().contains("Host: google.com:80")) {
                errorLogFound = true;
                break;
            }
        }
        assertFalse(errorLogFound, "Port 80 should not append to the Host header");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }

    /**
     * Apply the given property and restart the server to
     *
     * @param srcFile
     * @param key
     * @param value
     * @throws IOException
     */
    private void applyProperty(File srcFile, String key, String value) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        String outFileName = srcFile.getName();
        try {
            File destinationFile = new File(outFileName);
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destinationFile);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();
            properties.setProperty(key, value);
            properties.store(fos, null);
            fos.flush();
            serverConfigurationManager.applyConfigurationWithoutRestart(destinationFile);
        } catch (Exception e) {
            Assert.assertTrue(false, "Exception occured with the message: " + e.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Get the given property
     *
     * @param srcFile
     * @param key
     * @throws IOException
     */
    private String getProperty(File srcFile, String key) throws IOException {
        FileInputStream fis = null;
        String value = null;
        try {
            fis = new FileInputStream(srcFile);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();
            value = properties.getProperty(key);
        } catch (Exception e) {
            Assert.assertTrue(false, "Exception occured with the message: " + e.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return value;
    }
}
