/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
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
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.clients.inbound.endpoint.InboundAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

/**
 * Test tenant users with inbound endpoints.
 */
public class JMSInboundHttpTenantTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewerClient;
    InboundAdminClient inboundAdminClient1, inboundAdminClient2;
    private final String TENANT1_QUEUE = "tenant1Queue";
    private final String TENANT2_QUEUE = "tenant2Queue";
    private final String TENANT1_SYMBOL = "tenant1Symbol";
    private final String TENANT2_SYMBOL = "tenant2Symbol";
    private final String TENANT1 = "wso2";
    private final String TENANT2 = "abc";
    private final String TENANT1_INBOUND_EP = "JMSTenant1InboundEp";
    private final String TENANT2_INBOUND_EP = "JMSTenant2InboundEp";
    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        activeMQServer.startJMSBroker();
        OMElement synapse;
        super.init(TENANT1, "user1");
        inboundAdminClient1 = new InboundAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        synapse = esbUtils.loadResource("/artifacts/ESB/jms/inbound/transport/jms_http_tenant_transport.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), sessionCookie);
        super.init(TENANT2, "user1");
        inboundAdminClient2 = new InboundAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        synapse = esbUtils.loadResource("/artifacts/ESB/jms/inbound/transport/jms_http_tenant_transport.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

    }

    @Test(groups = {"wso2.esb"}, description = "Tenants Sending Messages to the Same Backend")
    public void testTenantTestCase() throws Exception {

        JMSQueueMessageProducer sender =
                new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            sender.connect(TENANT1_QUEUE);
            sender.pushMessage(createMessage(TENANT1_SYMBOL));
        } finally {
            sender.disconnect();
        }
        try {
            sender.connect(TENANT2_QUEUE);
            sender.pushMessage(createMessage(TENANT2_SYMBOL));
        } finally {
            sender.disconnect();
        }
        logViewerClient.clearLogs();
        inboundAdminClient1.addInboundEndpoint(createJMSInboundEndpoint(TENANT1_INBOUND_EP, TENANT1_QUEUE).toString());
        inboundAdminClient2.addInboundEndpoint(createJMSInboundEndpoint(TENANT2_INBOUND_EP, TENANT2_QUEUE).toString());
        Assert.assertTrue(Utils.checkForLog(logViewerClient, TENANT1_SYMBOL, 10),
                          "Message is not received by tenant: " + TENANT1);
        Assert.assertTrue(Utils.checkForLog(logViewerClient, TENANT2_SYMBOL, 10),
                          "Message is not received by tenant: " + TENANT2);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try{
            super.cleanup();
            inboundAdminClient1.removeInboundEndpoint(TENANT1_INBOUND_EP);
            inboundAdminClient2.removeInboundEndpoint(TENANT2_INBOUND_EP);
        } finally {
            activeMQServer.stopJMSBroker();
        }
    }

    /**
     * Creates the synapse configuration for an inbound endpoint given the inbound endpoint name and the queue name
     * to be connected to.
     *
     * @param inboundEPName the name of the inbound endpoint ot be created
     * @param queueName     the name of the queue to be connected to
     * @return the synapse configuration of the inbound endpoint
     * @throws Exception if an error occurs during the conversion of the string representation of the inbound
     *                   endpoint to an OMElement
     */
    private OMElement createJMSInboundEndpoint(String inboundEPName, String queueName) throws Exception {
        OMElement synapseConfig;
        synapseConfig = AXIOMUtil.stringToOM(createInboundEndpointString(inboundEPName, queueName));
        return synapseConfig;
    }

    /**
     * Creates a soap message with a symbol specified.
     *
     * @param symbol the symbol to be added
     * @return the soap message
     */
    private String createMessage(String symbol) {
        return "<?xml version='1.0' encoding='UTF-8'?>"
               + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
               + "                   xmlns:ns= \"http://services.samples\"\n"
               + "                   xmlns:xsd=\"http://services.samples/xsd\">\n"
               + "      <soapenv:Header/>\n"
               + "       <soapenv:Body>\n"
               + "           <ns:getQuote >\n"
               + "               <ns:request>\n"
               + "                   <ns:symbol>" + symbol + "</ns:symbol>\n"
               + "               </ns:request>\n"
               + "           </ns:getQuote>\n"
               + "       </soapenv:Body>\n"
               + "</soapenv:Envelope>";
    }

    /**
     * Creates a JMS inbound endpoint provied the name of the inbound endpoint and the name of the queue to be
     * listening to.
     *
     * @param inboundEPName the inbound endpoint name
     * @param queueName     the name of the queue to be connected to
     * @return the string representation of inbound endpoint configuration
     */
    private String createInboundEndpointString(String inboundEPName, String queueName) {
        return "<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n"
               + "                 name=\"" + inboundEPName + "\"\n"
               + "                 sequence=\"jmsInboundTenantRequestHandlerSeq\"\n"
               + "                 onError=\"inFault\"\n"
               + "                 protocol=\"jms\"\n"
               + "                 suspend=\"false\">\n"
               + "    <parameters>\n"
               + "        <parameter name=\"interval\">1000</parameter>\n"
               + "        <parameter name=\"transport.jms.Destination\">" + queueName + "</parameter>\n"
               + "        <parameter name=\"transport.jms.CacheLevel\">0</parameter>\n"
               + "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">QueueConnectionFactory</parameter>\n"
               + "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n"
               + "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n"
               + "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n"
               + "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n"
               + "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n"
               + "    </parameters>\n"
               + "</inboundEndpoint>";
    }
}
