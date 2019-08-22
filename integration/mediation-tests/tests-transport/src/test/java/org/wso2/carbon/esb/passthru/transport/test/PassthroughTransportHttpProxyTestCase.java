/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class PassthroughTransportHttpProxyTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                                                               + "passthru" + File.separator + "transport" + File.separator + "httpproxy" + File.separator + "axis2.xml"));
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/httpproxy/httpProxy.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Passthrough Transport Http.proxy test case")
    public void passthroughTransportHttpProxy() throws Exception {
        logViewer.clearLogs();
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("HttpProxyTest"), "", "IBM");
        } catch (AxisFault expected) {
            //read timeout expected
        }
        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        boolean proxyhostEntryFound = false;
        for (int i = 0; i < afterLogSize; i++) {
            if (logs[i].getMessage().contains("111.wso2.com:7777")) {
                proxyhostEntryFound = true;
                break;
            }
        }
        assertTrue(proxyhostEntryFound);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
