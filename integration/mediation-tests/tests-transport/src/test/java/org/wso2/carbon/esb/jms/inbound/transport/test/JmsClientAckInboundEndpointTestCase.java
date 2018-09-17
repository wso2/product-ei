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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.carbon.esb.jms.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import java.io.File;

/**
 * This testcase tests consuming message from a queue using client ack mode works
 */
public class JmsClientAckInboundEndpointTestCase extends ESBIntegrationTest {

    private static final String QUEUE_NAME = "jmsQueueClientAckInboundEndpointTestCase";

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();

        verifySequenceExistence("jmsClientAckInboundEPSendInSequence");

        //Add inbound endpoint configuration
        OMElement inboundEpConfig = esbUtils.loadResource(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jms" + File.separator +
                        "inbound" + File.separator + "transport" + File.separator + "jmsClientAckInboundEndpoint.xml");
        addInboundEndpoint(inboundEpConfig);
    }

    /**
     * Test whether consuming message from a queue using client ack mode works
     *
     * @throws Exception if any error occurred while running tests
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test JMS client ack mode with inbound endpoint")
    public void testJmsQueueToHttpWithInboundEndpoint() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        //send a message to the queue
        sendMessage();

        //check for the log
        boolean assertValue = Utils.checkForLog(logViewerClient,
                                                "** jmsClientAckInboundEPSendInSequence was called **",
                                                5);

        Assert.assertTrue(assertValue, "Message was not received to the inbound EP in client ack mode.");
        Assert.assertTrue(Utils.isQueueEmpty(QUEUE_NAME),"Queue should be empty if ack was properly sent");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();
    }

    /**
     * Send a message to testInboundQueue queue
     *
     * @throws Exception
     */
    private void sendMessage() throws Exception {
        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(
                JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        String message = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
                "  <soapenv:Header/>" +
                "  <soapenv:Body>" +
                "    <ser:getQuote> " +
                "      <ser:request>" +
                "        <xsd:symbol>IBM</xsd:symbol>" +
                "      </ser:request>" +
                "    </ser:getQuote>" +
                "  </soapenv:Body>" +
                "</soapenv:Envelope>";
        try {
            sender.connect(QUEUE_NAME);
            sender.pushMessage(message);
        } finally {
            sender.disconnect();
        }
    }
}
