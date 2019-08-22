/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;

/**
 * Related JIRA: https://wso2.org/jira/browse/ESBJAVA-4973
 * <p>
 * This test was done using random ip addresses to check whether the defined ip addresses get set for http/https
 * pass-through listeners
 */
public class ESBJAVA4973BindAddressFeatureTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        File file = new File(
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" + File.separator +
                        "passthru" + File.separator + "transport" + File.separator + "ESBJAVA4973" + File.separator +
                        "axis2.xml");
        serverConfigurationManager.applyConfiguration(file);
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Test the bind-addresses for http/https pass-through listeners")
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void bindAddressTest() throws Exception {
        boolean httpListenerAssert = false;
        boolean httpsListenerAssert = false;

        //check for system logs
        LogEvent[] systemLogs = logViewerClient.getAllRemoteSystemLogs();

        for (LogEvent logEvent : systemLogs) {
            String logMessage = logEvent.getMessage();
            if (logMessage.contains("Pass-through HTTP Listener started on 192.168.0.0")) {
                httpListenerAssert = true;
            } else if (logMessage.contains("Pass-through HTTPS Listener started on 192.168.0.1")) {
                httpsListenerAssert = true;
            }
            if (httpListenerAssert && httpsListenerAssert) {
                break;
            }
        }

        Assert.assertTrue(httpListenerAssert, "Incorrect bind-address for PassThroughHttpListener");
        Assert.assertTrue(httpsListenerAssert, "Incorrect bind-address for PassThroughHttpSSLListener");
    }

    @AfterClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void destroy() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }
}
