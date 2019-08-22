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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.message.processor.test.failover;

import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import static org.testng.Assert.assertTrue;

/**
 * This test case tests whether failover forwarding of messages happen
 * with the failover processor.
 */
public class FailoverForwardingProcessorTestCase extends ESBIntegrationTest {
    private static LogViewerClient logViewer;
    private AxisServiceClient client = new AxisServiceClient();
    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        activeMQServer.startJMSBroker();
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/failover/failoverTest.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @Test(groups = "wso2.esb",
          description = "Testing guaranteed delivery scenario")
    public void testFailover() throws Exception {
        logViewer.clearLogs();
        client.sendRobust(Utils.getStockQuoteRequest("RMQ"), getProxyServiceURLHttp("failoverTestProxy"), "getQuote");
        assertTrue(isMatchFound("RMQ Company"), "Message not dispatched from the store and sent to the backend!");

        activeMQServer.stopJMSBroker();
        logViewer.clearLogs();
        client.sendRobust(Utils.getStockQuoteRequest("RMQ"), getProxyServiceURLHttp("failoverTestProxy"), "getQuote");

        activeMQServer.startJMSBroker();
        assertTrue(isMatchFound("RMQ Company"), "Message not dispatched from the store and sent to the backend!");
    }

    private boolean isMatchFound(String matchStr) throws Exception {
        boolean logFound = false;
        for (int i = 0; i < 60; i++) {
            LogEvent[] logEvents = logViewer.getAllRemoteSystemLogs();
            if (logEvents != null) {
                for (LogEvent logEvent : logEvents) {
                    if (logEvent == null) {
                        continue;
                    }
                    if (logEvent.getMessage().contains(matchStr)) {
                        logFound = true;
                        break;
                    }
                }
            }
            if (logFound) {
                break;
            }
            Thread.sleep(500);
        }
        return logFound;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            activeMQServer.stopJMSBroker();
        } finally {
            cleanup();
        }
    }
}