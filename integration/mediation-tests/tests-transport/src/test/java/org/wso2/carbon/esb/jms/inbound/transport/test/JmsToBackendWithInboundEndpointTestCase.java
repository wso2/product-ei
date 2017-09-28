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
 * This testcase tests consuming message from a queue and sending to a backend works
 */
public class JmsToBackendWithInboundEndpointTestCase extends ESBIntegrationTest {

    private static final String QUEUE_NAME = "jmsQueueToHttpWithInboundEndpointTestCase";
    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        activeMQServer.startJMSBroker();
        super.init();

        verifySequenceExistence("jmsQueueToHttpWithInboundEPSendInSequence");

        //Add inbound endpoint configuration
        OMElement inboundEpConfig = esbUtils.loadResource(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jms" + File.separator +
                        "inbound" + File.separator + "transport" + File.separator + "jmsQueueToHttpInboundEndpoint.xml");
        addInboundEndpoint(inboundEpConfig);
    }

    /**
     * Test whether consuming message from a queue and sending to a backend works (i.e. JMS -> HTTP)
     *
     * @throws Exception if any error occurred while running tests
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test JMS to HTTP communication with inbound endpoint")
    public void testJmsQueueToHttpWithInboundEndpoint() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        //send a message to the queue
        sendMessage();

        //check for the log
        boolean assertValue = Utils.assertIfSystemLogContains(logViewerClient,
                                                              "** testJmsQueueToHttpWithInboundEndpoint RESPONSE **");

        Assert.assertTrue(assertValue, "HTTP backend response did not receive with the inbound endpoint.");
    }



    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();
        activeMQServer.stopJMSBroker();
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
            Thread.sleep(2000);
        } finally {
            sender.disconnect();
        }
    }
}
