/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.platform.tests.clustering.mqtt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.ClientMode;
import org.wso2.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.mb.integration.common.clients.MQTTConstants;
import org.wso2.mb.integration.common.clients.QualityOfService;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.platform.tests.clustering.mqtt.DataProvider.QualityOfServiceDataProvider;
import org.xml.sax.SAXException;

/**
 * Test class to test topics in clusters
 */
public class MQTTClusterTestCase extends MQTTPlatformBaseTest {

    /**
     * Holds information about cluster marked with 'mb002' in automation.xml.
     */
    private AutomationContext automationContextForMB2;
    /**
     * Holds information about cluster marked with 'mb003' in automation.xml.
     */
    private AutomationContext automationContextForMB3;
    
    /**
     * Prepare environment for tests.
     *
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws XPathExpressionException, URISyntaxException, SAXException, XMLStreamException,
            LoginAuthenticationExceptionException, IOException, AutomationUtilException {

        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

        automationContextForMB2 = getAutomationContextWithKey("mb002");
        automationContextForMB3 = getAutomationContextWithKey("mb003");
        
    }

    /**
     * Send message to a node and receive from a another node.
     * 
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb",
            description = "Single topic two node send-receive test case",
            dataProvider = "QualityOfServiceDataProvider",
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicTwoNodeSendReceive(QualityOfService qualityOfService)
                                                    throws MqttException, XPathExpressionException{
        String topic = "testSingleTopicTwoNodeSendReceive";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode2 =
                                                                    buildConfiguration(automationContextForMB2);
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode3 =
                                                                    buildConfiguration(automationContextForMB3);
                
        // create the subscribers
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                noOfMessages, ClientMode.BLOCKING, clientConnectionConfigurationForNode3);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                "The received message is incorrect");

    }
    
    /**
     * Send and receive messages in a single node for a topic
     *
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb",
            description = "Single topic single node send-receive test case",
            dataProvider = "QualityOfServiceDataProvider",
            dataProviderClass = QualityOfServiceDataProvider.class)   
    public void testSingleTopicSingleNodeSendReceive(QualityOfService qualityOfService)
                                                  throws MqttException, XPathExpressionException{
        String topic = "testSingleTopicSingleNodeSendReceive";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfiguration = buildConfiguration(automationContextForMB2);
        
        // create the subscribers
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                ClientMode.BLOCKING, clientConnectionConfiguration);

        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                noOfMessages, ClientMode.BLOCKING, clientConnectionConfiguration);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                "The received message is incorrect");

    }
    
    /**
     * Send 100 mqtt message on all QOS levels and receive them from a single node.
     *
     * @throws MqttException
     * @throws XPathExpressionException 
     */
    @Test(groups = { "wso2.mb", "mqtt" }, description = "Multiple mqtt messages sent/received from single node",
            dataProvider = "QualityOfServiceDataProvider",
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicSingleNodeMultipleMessagesTestCase(QualityOfService qualityOfService)
                                                                                                    throws MqttException,
                                                                                                    XPathExpressionException {
        String topic = "testSingleTopicSingleNodeMultipleMessagesTestCase";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 100;
        boolean saveMessages = true;
        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfiguration = buildConfiguration(automationContextForMB2);
        //create the subscribers
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                ClientMode.BLOCKING, clientConnectionConfiguration);

        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                noOfMessages, ClientMode.BLOCKING, clientConnectionConfiguration);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(mqttClientEngine.getReceivedMessageCount(), noOfMessages,
                "The received message count is incorrect.");

    }

    
    /**
     * Send 100 mqtt message on all QOS levels and receive them from different nodes.
     *
     * @throws MqttException
     * @throws XPathExpressionException 
     */
    @Test(groups = { "wso2.mb", "mqtt" }, description = "Multiple mqtt messages sent/received from two nodes",
            dataProvider = "QualityOfServiceDataProvider",
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicTwoNodeMultipleMessagesTestCase(QualityOfService qualityOfService)
                                                                                                    throws MqttException,
                                                                                                    XPathExpressionException {
        String topic = "testSingleTopicTwoNodeMultipleMessagesTestCase";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 100;
        boolean saveMessages = true;
        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode2 =
                buildConfiguration(automationContextForMB2);
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode3 =
                buildConfiguration(automationContextForMB3);

        // create the subscribers
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                                                    ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        // create the publishers and start publish
        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, clientConnectionConfigurationForNode3);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                            "The received message is incorrect");

    }
}
