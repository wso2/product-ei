/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.platform.tests.clustering.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
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
import org.wso2.mb.platform.tests.clustering.mqtt.DataProvider.QualityOfServiceDataProvider;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


/**
 * Test class to test mqtt Retain feature in cluster environment
 */
public class RetainClusterTestCase extends MQTTPlatformBaseTest {



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
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws java.net.URISyntaxException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.stream.XMLStreamException
     * @throws org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException
     * @throws java.io.IOException
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
     * Send and receive retain messages in a single node for a topic
     *
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb",
          description = "Single topic Single node send-receive retain test case",
          dataProvider = "QualityOfServiceDataProvider",
          dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicSingleNodeSendReceiveRetainMessages(QualityOfService qualityOfService)
            throws MqttException, XPathExpressionException{
        String topic = "testSingleTopicSingleNodeSendReceiveRetain";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode2 = buildConfiguration(automationContextForMB2);

        // set retain flag in published message
        clientConnectionConfigurationForNode2.setRetain(true);

        // create the publisher and send retain message
        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        // create the subscriber
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                                                    ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                            "The received message is incorrect");

    }



    /**
     * Send and receive retain messages in multiple node for a topic
     *
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb",
          description = "Single topic two node send-receive test case",
          dataProvider = "QualityOfServiceDataProvider",
          dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicMultipleNodeSendReceiveRetainMessages(QualityOfService qualityOfService)
            throws MqttException, XPathExpressionException{
        String topic = "testSingleTopicMultipleNodeSendReceiveRetain_" + qualityOfService;
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode2 =
                                                                    buildConfiguration(automationContextForMB2);
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode3 =
                                                                    buildConfiguration(automationContextForMB3);

        // set retain flag in published message
        clientConnectionConfigurationForNode3.setRetain(true);

        // create publisher in node3
        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, clientConnectionConfigurationForNode3);
        // create the subscriber in node2
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                                                    ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                            "The received message is incorrect");

    }


    /**
     * Remove retain message in multiple node for a topic
     *
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb",
          description = "Single topic two node remove retain message test case",
          dataProvider = "QualityOfServiceDataProvider",
          dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicMultipleNodeRemoveRetainMessage(QualityOfService qualityOfService)
            throws MqttException, XPathExpressionException{
        String topic = "testSingleTopicMultipleNodeRemoveRetain";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        int expectedNumberOfMessages = 0;
        boolean saveMessages = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode2 =
                buildConfiguration(automationContextForMB2);
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode3 =
                buildConfiguration(automationContextForMB3);

        // set retain flag in published message
        clientConnectionConfigurationForNode3.setRetain(true);

        // send retain message to node3 with non-empty payload
        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        // send retain message to node3 with empty payload. This should remove retain entry from broker.
        mqttClientEngine.createPublisherConnection(topic, qualityOfService,
                                                   MQTTConstants.EMPTY_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, clientConnectionConfigurationForNode3);

        // create the subscriber in node2
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, saveMessages,
                                                    ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), expectedNumberOfMessages, "The received message count is incorrect.");

    }






}
