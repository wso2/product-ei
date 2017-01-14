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
package org.wso2.carbon.esb.samples.test.transport;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
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
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * Sample 268: Proxy Services with the Local Transport
 */
public class Sample268TestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        AutomationContext context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfigurationWithoutRestart(
            new File(getESBResourceLocation() + File.separator +
                     "sample_268" + File.separator + "axis2.xml")
        );

        serverConfigurationManager
            .applyConfiguration(new File(getESBResourceLocation() + File.separator +
                                         "sample_268" + File.separator + "carbon.xml"));

        super.init();
        loadSampleESBConfiguration(268);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" }, description = "Proxy Services with the Local Transport")
    public void testLocalTransport() throws Exception {

        OMElement response = axis2Client
            .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("LocalTransportProxy"), null,
                                         "IBM");

        Assert.assertNotNull(response, "Response is null");

        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
        boolean isInLocalTransportProxyInvoked = false;
        boolean isInSecondProxyInvoked = false;
        boolean isOutStockQuoteProxyInvoked = false;
        boolean isOutSecondProxyInvoked = false;
        boolean isOutLocalTransportProxyInvoked = false;

        for (LogEvent event : getLogsInfo) {
            if (!isInLocalTransportProxyInvoked &&
                event.getMessage().contains("In sequence of LocalTransportProxy invoked!")) {
                isInLocalTransportProxyInvoked = true;

            }
            if (!isInSecondProxyInvoked &&
                event.getMessage().contains("In sequence of Second proxy invoked!")) {
                isInSecondProxyInvoked = true;

            }
            if (!isOutStockQuoteProxyInvoked &&
                event.getMessage().contains("Out sequence of StockQuote proxy invoked!")) {
                isOutStockQuoteProxyInvoked = true;

            }
            if (!isOutSecondProxyInvoked &&
                event.getMessage().contains("Out sequence of Second proxy invoked!")) {
                isOutSecondProxyInvoked = true;

            }
            if (!isOutLocalTransportProxyInvoked &&
                event.getMessage().contains("Out sequence of LocalTransportProxy invoked!")) {
                isOutLocalTransportProxyInvoked = true;

            }
            if (isInLocalTransportProxyInvoked && isInSecondProxyInvoked &&
                isOutStockQuoteProxyInvoked && isOutSecondProxyInvoked &&
                isOutLocalTransportProxyInvoked) {
                break;
            }
        }

        Assert.assertTrue(isInLocalTransportProxyInvoked,
                          "In sequence of LocalTransportProxy not invoked!");
        Assert.assertTrue(isInSecondProxyInvoked, "In sequence of Second proxy not invoked!");
        Assert.assertTrue(isOutStockQuoteProxyInvoked,
                          "Out sequence of StockQuote proxy not invoked!");
        Assert.assertTrue(isOutSecondProxyInvoked, "Out sequence of Second proxy not invoked!");
        Assert.assertTrue(isOutLocalTransportProxyInvoked,
                          "Out sequence of LocalTransportProxy not invoked!");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration(true);
        }

    }
}
