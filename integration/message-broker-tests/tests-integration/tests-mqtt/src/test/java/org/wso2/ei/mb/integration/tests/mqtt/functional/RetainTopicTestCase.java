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

package org.wso2.ei.mb.integration.tests.mqtt.functional;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.mb.integration.common.clients.ClientMode;

import org.wso2.ei.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.ei.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.ei.mb.integration.common.clients.MQTTConstants;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;


import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 *
 * This test case will verify functionality of MQTT retain feature. Retain feature will keep last
 * retain enabled message from publisher, for future subscribers.
 *
 */
public class RetainTopicTestCase extends MBIntegrationBaseTest {



    /**
     * Initialize super class.
     *
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Send a single mqtt message with retain enabled and receive.
     *
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Single mqtt retain message send receive test case")
    public void performSendReceiveRetainTopicTestCase()
            throws MqttException, XPathExpressionException {
        String topic = "SendReceiveRetainTopicTestCase";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;
        boolean retained = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        MQTTClientConnectionConfiguration configuration = mqttClientEngine.getConfigurations(automationContext);
        configuration.setRetain(retained);

        //create the subscriber to receive retain topic message
        mqttClientEngine.createSubscriberConnection(topic, QualityOfService.MOST_ONCE, noOfSubscribers,
                                                    saveMessages, ClientMode.BLOCKING, automationContext);

        // create publisher to publish retain topic message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, configuration);

        // wait until all messages received by subscriber and shut down clients.
        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        // Verify received messages equals to send message count.
        Assert.assertEquals(receivedMessages.size(), noOfMessages,
                            "The received message count is incorrect.");

        // Verify message payload has received correctly
        Assert.assertEquals(receivedMessages.get(0).getPayload(),
                            MQTTConstants.TEMPLATE_PAYLOAD, "The received message is incorrect");

    }



    /**
     * Send and receive single mqtt message with retain enabled. Subscriber will subscribe
     * after message been published.
     *
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Single mqtt retain message send receive test case")
    public void performSendReceiveRetainTopicForLateSubscriberTestCase()
            throws MqttException, XPathExpressionException {
        String topic = "SendReceiveRetainTopicForLateSubscriberTestCase";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;
        boolean retained = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        MQTTClientConnectionConfiguration configuration = mqttClientEngine.getConfigurations(automationContext);
        configuration.setRetain(retained);

        //First, create publisher and publish retain topic message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, configuration);


        //Finally,create the subscriber to receive retain topic message
        mqttClientEngine.createSubscriberConnection(topic, QualityOfService.MOST_ONCE, noOfSubscribers,
                                                    saveMessages, ClientMode.BLOCKING, automationContext);

        // wait until all messages received by subscriber and shut down clients.
        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        // Verify received messages equals to send message count.
        Assert.assertEquals(receivedMessages.size(), noOfMessages,
                            "The received message count is incorrect.");

        // Verify message payload has received correctly
        Assert.assertEquals(receivedMessages.get(0).getPayload(),
                            MQTTConstants.TEMPLATE_PAYLOAD, "The received message is incorrect");

    }


    /**
     * <p>This test case will check if retain topic message will get deleted if it received retained
     * enabled message with empty payload. This is the default way of removing a retained topic from
     * broker as per MQTT specification v 3.1.
     *
     * Test scenario
     * 1. Publish one retained topic message for 'topic2'.
     * 2. Add one subscriber and verify if retained topic message getting received.
     * 3. Publish retain topic message with empty payload for 'topic2'. This will delete retained
     *    entry for 'topic2' from broker.
     * 4. Add new subscriber for 'topic2' and verify it's not receiving any retained messages from
     *    broker.
     *
     * @see <a href="MQTT specification v 3.1">
     *     http://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/MQTT_V3.1_Protocol_Specific.pdf
     *     </a>
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Remove MQTT retain test case")
    public void performRemoveRetainTopicTestCase() throws MqttException, XPathExpressionException {
        String topic = "RemoveRetainTopicTestCase";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;
        boolean retained = true;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        MQTTClientConnectionConfiguration configuration = mqttClientEngine.getConfigurations(automationContext);
        // Set retain flag into configurations.
        configuration.setRetain(retained);

        //Create publisher and publish retain topic message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                                                   noOfMessages, ClientMode.BLOCKING, configuration);


        //Create the subscriber to receive retain topic message
        mqttClientEngine.createSubscriberConnection(topic, QualityOfService.MOST_ONCE, noOfSubscribers,
                                                    saveMessages, ClientMode.BLOCKING, automationContext);


        // wait until all messages received by subscriber.
        mqttClientEngine.waitUntilExpectedNumberOfMessagesReceived(1,20000L);

        // shut down all clients.
        mqttClientEngine.shutdown();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        // Verify received messages equals to send message count.
        Assert.assertEquals(receivedMessages.size(), noOfMessages,
                            "The received message count is incorrect.");

        // Verify message payload has received correctly
        Assert.assertEquals(receivedMessages.get(0).getPayload(),
                            MQTTConstants.TEMPLATE_PAYLOAD, "Received message payload is incorrect");



        // new mqtt client engine has initialized to check if new subscriber gets retained message
        // once it's been deleted by sending empty payload. Since need to reset message received
        // counts, had to initialize a new mqtt client engine.
        MQTTClientEngine mqttClientEngine1 = new MQTTClientEngine();

        // Create new publisher and publish retain topic message with empty payload. This will delete
        // retain entry for 'topic2'.
        mqttClientEngine1.createPublisherConnection(topic, QualityOfService.MOST_ONCE, "".getBytes(),
                                                   noOfPublishers, noOfMessages, ClientMode.BLOCKING,
                                                   configuration);

        //Create the new subscriber to receive retain topic message.
        mqttClientEngine1.createSubscriberConnection(topic, QualityOfService.MOST_ONCE, noOfSubscribers,
                                                    saveMessages, ClientMode.BLOCKING, automationContext);

        // wait until messages received by subscriber.
        mqttClientEngine1.waitUntilExpectedNumberOfMessagesReceived(0,20000L);

        // shut down all clients.
        mqttClientEngine1.shutdown();

        receivedMessages = mqttClientEngine1.getReceivedMessages();

        // Since there's no retain messages for 'topic2' on broker, subscriber won't receive
        // any messages upon subscribe.
        Assert.assertEquals(receivedMessages.size(), 0, "Received message count is incorrect.");
    }

}
