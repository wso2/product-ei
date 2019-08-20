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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.jmsclient.JMSQueueMessageProducer;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.xml.stream.XMLStreamException;

/**
 * This class tests polling of messages of different JMS message types, by JMS inbound endpoints.
 * Currently includes tests for BytesMessages and MapMessages.
 */
public class JMSInboundMessageSupportTestCase extends ESBIntegrationTest {
    private JMSQueueMessageProducer jmsQueueMessageProducer;
    private LogViewerClient logViewerClient;
    private LogEvent[] logEvents;
    private String endpointName;
    private String queueName;
    private int numberOfMessages = 3;
    private int numberOfMessagesReceived = 0;
    private long startTime;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        jmsQueueMessageProducer = new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance()
                .getBrokerConfiguration());
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(
            groups = { "wso2.esb" },
            description = "JMS Inbound Endpoint Polling BytesMessages from a Queue"
    )
    public void testBytesMessageType() throws Exception {
        endpointName = "BytesMessageEndpoint";
        queueName = "BytesMessageQueue";
        logViewerClient.clearLogs();
        try {
            jmsQueueMessageProducer.connect(queueName);
            for (int i = 0; i < numberOfMessages; i++) {
                jmsQueueMessageProducer.sendBytesMessage(("<?xml version='1.0' encoding='UTF-8'?>"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">"
                        + "   <soapenv:Header/>"
                        + "   <soapenv:Body>"
                        + "      <ser:placeOrder>"
                        + "         <ser:order>"
                        + "            <xsd:price>100</xsd:price>"
                        + "            <xsd:quantity>2000</xsd:quantity>"
                        + "            <xsd:symbol>JMSBytes</xsd:symbol>"
                        + "         </ser:order>"
                        + "      </ser:placeOrder>"
                        + "   </soapenv:Body>"
                        + "</soapenv:Envelope>").getBytes());
                log.info("BytesMessage " + i + " published to the JMS Queue");
            }
        } finally {
            jmsQueueMessageProducer.disconnect();
        }

        addInboundEndpoint(getEndpointConfig(endpointName, queueName));
        startTime = System.currentTimeMillis();
        while ((numberOfMessagesReceived < numberOfMessages) && (System.currentTimeMillis() - startTime) < 10000) {
            numberOfMessagesReceived = 0;
            logEvents = logViewerClient.getAllRemoteSystemLogs();
            for (LogEvent logEvent : logEvents) {
                if (logEvent.getMessage().contains("<xsd:symbol>JMSBytes</xsd:symbol>")) {
                    numberOfMessagesReceived++;
                }
            }
        }

        deleteInboundEndpointFromName(endpointName);
        Assert.assertEquals(numberOfMessagesReceived, numberOfMessages, "JMS Inbound Endpoint couldn't consume"
                + " BytesMessages from Queue");
    }

    @Test(
            groups = { "wso2.esb" },
            description = "JMS Inbound Endpoint Polling MapMessages from a Queue"
    )
    public void testMapMessageType() throws Exception {
        endpointName = "MapMessageEndpoint";
        queueName = "MapMessageQueue";
        logViewerClient.clearLogs();
        try {
            jmsQueueMessageProducer.connect(queueName);
            for (int i = 0; i < 3; i++) {
                jmsQueueMessageProducer.sendMapMessage();
                log.info("MapMessage " + i + " published to the JMS Queue");
            }
        } finally {
            jmsQueueMessageProducer.disconnect();
        }

        addInboundEndpoint(getEndpointConfig(endpointName, queueName));
        startTime = System.currentTimeMillis();
        while ((numberOfMessagesReceived < numberOfMessages) && (System.currentTimeMillis() - startTime) < 10000) {
            numberOfMessagesReceived = 0;
            logEvents = logViewerClient.getAllRemoteSystemLogs();
            for (LogEvent logEvent : logEvents) {
                if (logEvent.getMessage().contains("JMSMap xmlns=\"http://axis.apache.org/axis2/java/transports/jms/"
                        + "map-payload\"")) {
                    numberOfMessagesReceived++;
                }
            }
        }

        deleteInboundEndpointFromName(endpointName);
        Assert.assertEquals(numberOfMessagesReceived, numberOfMessages, "JMS Inbound Endpoint couldn't consume"
                + " MapMessages from Queue");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    /**
     * Method to retrieve the xml configuration of the endpoint with the specified name, for the specified queue.
     *
     * @param name name of the inbound endpoint
     * @param queueName name of the queue
     * @return the xml configuration of the JMS inbound endpoint
     * @throws XMLStreamException if there is an error in processing
     */
    private OMElement getEndpointConfig(String name, String queueName) throws XMLStreamException {
        return AXIOMUtil.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n"
                        + "                 name=\"" + name + "\"\n"
                        + "                 sequence=\"requestHandlerSeq\"\n"
                        + "                 onError=\"inFault\"\n"
                        + "                 protocol=\"jms\"\n"
                        + "                 suspend=\"false\">\n"
                        + "    <parameters>\n"
                        + "        <parameter name=\"interval\">1000</parameter>\n"
                        + "        <parameter name=\"transport.jms.Destination\">" + queueName + "</parameter>\n"
                        + "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n"
                        + "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">"
                        + "QueueConnectionFactory</parameter>\n"
                        + "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi."
                        + "ActiveMQInitialContextFactory</parameter>\n"
                        + "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n"
                        + "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE"
                        + "</parameter>\n"
                        + "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n"
                        + "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n"
                        + "    </parameters>\n"
                        + "</inboundEndpoint>");
    }
}