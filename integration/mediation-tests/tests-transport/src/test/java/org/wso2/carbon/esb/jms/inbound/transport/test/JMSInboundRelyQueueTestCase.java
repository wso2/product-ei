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
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.jmsclient.JmsClientHelper;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import java.io.File;
import java.rmi.RemoteException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This tests whether specifying a reply queue works correctly with JMS inbound endpoint
 */
public class JMSInboundRelyQueueTestCase extends ESBIntegrationTest {

    /**
     * Embedded broker instance used for testcase
     */
    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        activeMQServer.startJMSBroker();
        super.init();

        verifySequenceExistence("jmsInboundEpReplyQueueTestInSequence");

        //Add inbound endpoint configuration
        OMElement inboundEpConfig = esbUtils.loadResource(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jms" + File.separator +
                        "inbound" + File.separator + "transport" + File.separator + "jmsReplyQueueTestInboundEndpoint.xml");
        addInboundEndpoint(inboundEpConfig);
    }

    /**
     * Tests whether specifying a reply queue works correctly with JMS inbound endpoint
     */
    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE})
    @Test(groups = { "wso2.esb"}, description = "Test JMS inbound endpoint with a reply queue")
    public void testReplyQueueWithJmsInbound() throws RemoteException, NamingException, JMSException {
        String testReplyQueueWithJmsInboundQueue = "testReplyQueueWithJmsInboundQueue";
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        logViewerClient.clearLogs();

        InitialContext activeMqInitialContext = JmsClientHelper.getActiveMqInitialContext();
        ConnectionFactory connectionFactory = (ConnectionFactory) activeMqInitialContext.lookup(JmsClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createQueue(testReplyQueueWithJmsInboundQueue));

        TextMessage testMessage = session.createTextMessage("<?xml version='1.0' encoding='UTF-8'?>" +
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
                                                                    "</soapenv:Envelope>");
        TemporaryQueue temporaryQueue = session.createTemporaryQueue();
        testMessage.setJMSReplyTo(temporaryQueue);

        producer.send(testMessage);

        producer.close();

        // create inbound ep

        MessageConsumer consumer = session.createConsumer(temporaryQueue);
        Message message = consumer.receive(10000);
        connection.close();

        Assert.assertNotNull(message, "Message was not received to reply queue");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();
        activeMQServer.stopJMSBroker();
    }
}
