/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.jms.inbound.transport.test;

import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

/**
 * This class tests server behaviour with broker shutdown, when a JMS Inbound EP is configured.
 */
public class JMSInboundBrokerShutdownTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        activeMQServer.startJMSBroker();
        super.init();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
    }

    //Disabling due to https://github.com/wso2/product-ei/issues/1199
    @Test(
            groups = {"wso2.esb"},
            description = "Behaviour of a server, with a JMS Inbound Endpoint configured, when the JMS broker is down",
            enabled = false
    )
    public void testJMSInboundEndpointBehaviourWithBrokerShutdown() throws Exception {
        addInboundEndpoint(AXIOMUtil
                .stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n"
                        + "                 name=\"JMSInboundBrokerShutdown\"\n"
                        + "                 sequence=\"requestHandlerSeq\"\n"
                        + "                 onError=\"inFault\"\n"
                        + "                 protocol=\"jms\"\n"
                        + "                 suspend=\"false\">\n"
                        + "    <parameters>\n"
                        + "        <parameter name=\"interval\">1000</parameter>\n"
                        + "        <parameter name=\"transport.jms.Destination\">brokershutdownqueue</parameter>\n"
                        + "        <parameter name=\"transport.jms.CacheLevel\">0</parameter>\n"
                        + "        <parameter name=\"transport.jms"
                        + ".ConnectionFactoryJNDIName\">QueueConnectionFactory</parameter>\n"
                        + "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi"
                        + ".ActiveMQInitialContextFactory</parameter>\n"
                        + "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n"
                        + "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE"
                        + "</parameter>\n"
                        + "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n"
                        + "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n"
                        + "    </parameters>\n"
                        + "</inboundEndpoint>"));
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        String expectedErrorMessage = "Could not connect to broker URL: tcp://localhost:61616. Reason: java.net."
                + "ConnectException: Connection refused (Connection refused)";

        //Test behaviour when broker shuts down while the server is up
        boolean assertAffected = false;
        activeMQServer.stopJMSBroker();
        assertAffected = Utils.assertIfSystemLogContains(logViewerClient, expectedErrorMessage);
        Assert.assertTrue(assertAffected, "ESB server not affected by broker shutdown.");

        //Test behaviour when the server restarts after the broker shuts down
        boolean expectedErrorOccurred = false;
        boolean assertSuccessfulStart = false;
        String logMessage;
        serverConfigurationManager.restartGracefully();
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        long startTime = System.currentTimeMillis();
        LogEvent[] remoteSystemLogs;
        while (!expectedErrorOccurred && (System.currentTimeMillis() - startTime) < 50000) {
            remoteSystemLogs  = logViewerClient.getAllRemoteSystemLogs();
            if (remoteSystemLogs != null) {
                for (LogEvent logEvent : remoteSystemLogs) {
                    /*
                    Logs (returned in reversed order) are checked to see if the first error is logged only after the
                    log for server start up.
                     */
                    logMessage = logEvent.getMessage();
                    if (logMessage.contains(expectedErrorMessage)) {
                        expectedErrorOccurred = true;
                    }
                    /*
                    Since the logs are in reversed order, the start up is considered successful only if the error is
                    already logged.
                     */
                    if (logMessage.contains("WSO2 Carbon started")) {
                        assertSuccessfulStart = expectedErrorOccurred;
                        break;
                    }
                }
            }
        }
        Assert.assertTrue(assertSuccessfulStart, "ESB server start up affected by broker shutdown.");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        activeMQServer.stopJMSBroker();
    }
}
