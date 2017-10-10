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
 * Check for client reconnect scenarios.
 */
public class ClientReconnectTestCase extends MBIntegrationBaseTest {

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
     * Check client reconnect scenario.
     *
     * 1. Subscribe to a topic.
     * 2. Publish 10 messages.
     * 3. Verify 10 messages are received.
     * 4. Disconnect subscriber.
     * 5. Reconnect subscriber and re-subscribe to the same topic.
     * 6. Publish 10 messages.
     * 7. Verify 10 more messages are received.
     *
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test
    public void performClientReconnectTest() throws MqttException, XPathExpressionException {
        String topicName = "clientReconnectTest";
        int noOfMessages = 10;
        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        //create the subscribers
        mqttClientEngine.createSubscriberConnection(topicName, QualityOfService.MOST_ONCE, 1, false,
                ClientMode.BLOCKING, automationContext);

        AndesMQTTClient subscriber =  mqttClientEngine.getSubscriberList().get(0);

        mqttClientEngine.createPublisherConnection(topicName, QualityOfService.MOST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1, noOfMessages, ClientMode.ASYNC, automationContext);

        mqttClientEngine.waitUntilAllMessageReceived();

        Assert.assertEquals(subscriber.getReceivedMessageCount(), noOfMessages, "Did not receive excepted message " +
                "count before disconnecting");

        // Reconnect subscriber
        subscriber.disconnect();
        subscriber.connect();
        subscriber.subscribe();

        mqttClientEngine.createPublisherConnection(topicName, QualityOfService.MOST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1, noOfMessages, ClientMode.ASYNC, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(subscriber.getReceivedMessageCount(), noOfMessages * 2, "Did not recieve expected message" +
                " count after reconnecting");




    }
}
