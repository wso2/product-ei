/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.passthru.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * Testcase for check whether Synapse handlers getting executed in blocking calls.
 */
public class ESBJAVA4883SynapseHandlerBlockingCallTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private  LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator + "passthru" +
                File.separator + "transport" + File.separator + "ESBJAVA4883" + File.separator + "synapse-handlers.xml"));
        super.init();
        verifyProxyServiceExistence("SynapseHandlerTestProxy");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
    }

    @Test(groups = "wso2.esb", description = "Invoking Synapse handlers in blocking calls test")
    public void testSynapseHandlerBlockingCall() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("SynapseHandlerTestProxy"), null, "WSO2");
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        Assert.assertTrue(stringExistsInLog("Request Out Flow", logs), "Synapse Handler not executed in the request out path");
        Assert.assertTrue(stringExistsInLog("Response In Flow", logs), "Synapse Handler not executed in the response in path");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        super.cleanup();
    }

    protected boolean stringExistsInLog(String string, LogEvent[] logs) throws Exception {
        boolean logFound = false;
        for (LogEvent item : logs) {
            if (item.getPriority().equals("INFO")) {
                String message = item.getMessage();
                if (message.contains(string)) {
                    logFound = true;
                    break;
                }
            }
        }

        return logFound;
    }
}
