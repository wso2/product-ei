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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        File log4jProperties = new File(carbonHome + File.separator + "repository" + File.separator + "conf" +
                                        File.separator + "log4j.properties");
        applyProperty(log4jProperties, "log4j.logger.org.apache.synapse.transport.http.wire", "DEBUG");
        serverConfigurationManager.restartGracefully();
        init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/HOST_HEADER.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "Test wrong port(80) attached with the HOST_HEADERS for https backend")
    public void testHOST_HEADERPropertyTest() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("httpsBackendProxyService"), null, "WSO2");
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
}
