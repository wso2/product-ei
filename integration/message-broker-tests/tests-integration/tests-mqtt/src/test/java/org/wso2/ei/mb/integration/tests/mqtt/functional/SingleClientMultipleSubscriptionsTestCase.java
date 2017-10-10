/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.ei.mb.integration.tests.mqtt.functional;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.mb.integration.common.clients.*;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.xml.xpath.XPathExpressionException;

/**
 * Test case to verify subscribing to multiple topics with the same client.
 */
public class SingleClientMultipleSubscriptionsTestCase extends MBIntegrationBaseTest {

    /**
     * Initialize super class.
     *
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Subscribe to two topics with the same client and verify whether messages are received from both topics.
     *
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Subscribe to two topics with the same client id")
    public void performSingleClientMultipleSubscriptionsTest() throws MqttException, XPathExpressionException {
        String topic1 = "singleClientMultipleSubscriptions1";
        String topic2 = "singleClientMultipleSubscriptions2";
        int noOfMessages = 1;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        //create the subscriber
        mqttClientEngine.createSubscriberConnection(topic1, QualityOfService.LEAST_ONCE, 1, false,
                ClientMode.BLOCKING, automationContext);

        AndesMQTTClient subscriber = mqttClientEngine.getSubscriberList().get(0);

        subscriber.subscribe(topic2);

        mqttClientEngine.createPublisherConnection(topic1, QualityOfService.LEAST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1,
                noOfMessages, ClientMode.BLOCKING, automationContext);

        mqttClientEngine.createPublisherConnection(topic2, QualityOfService.LEAST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1,
                noOfMessages, ClientMode.BLOCKING, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(mqttClientEngine.getReceivedMessageCount(), noOfMessages * 2,
                "Did not receive expected message count ");


    }

    /**
     * Check if a subscriber who subscribed to two topics with clean session false, receive messages to an inactive
     * subscription when one subscription is inactive.
     *
     * 1. Create a topic with a subscriber with clean session false
     * 2. Disconnect the subscriber (The subscription will be added to inactive sessions)
     * 3. Add some messages to the created topic. (The messages will be persisted)
     * 4. Using the same client ID create another topic. (With different topic name)
     * 5. Publish some messages to the inactive topic.
     * 6. Client should not receive any messages
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Subscribe to two topics with clean session false and make"
            + "one subscription inactive")
    public void performSameClientDifferentTopicsTest() throws XPathExpressionException, MqttException {
        String topic1 = "SameClientDifferentTopics1";
        String topic2 = "SameClientDifferentTopic2";
        int noOfMessages = 1;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        MQTTClientConnectionConfiguration mqttConfigs = mqttClientEngine.getConfigurations(automationContext);

        mqttConfigs.setCleanSession(false);
        //create the subscriber
        mqttClientEngine.createSubscriberConnection(topic1, QualityOfService.EXACTLY_ONCE, 1, false,
                ClientMode.BLOCKING, mqttConfigs);

        AndesMQTTClient subscriber = mqttClientEngine.getSubscriberList().get(0);

        subscriber.disconnect();

        // Publish messages to the topic with inactive subscriber
        mqttClientEngine.createPublisherConnection(topic1, QualityOfService.EXACTLY_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1,
                noOfMessages, ClientMode.BLOCKING, automationContext);

        // Re-connect and subscribe to a different topic
        subscriber.connect();

        subscriber.subscribe(topic2);

        // Publish messages to the topic with inactive subscriber
        mqttClientEngine.createPublisherConnection(topic1, QualityOfService.EXACTLY_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1,
                noOfMessages, ClientMode.BLOCKING, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(mqttClientEngine.getReceivedMessageCount(), 0, "Received messages in a different topic"
                + "when no messages are expected");

    }
}
