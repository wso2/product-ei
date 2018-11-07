/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */

package org.wso2.mb.platform.tests.clustering.mqtt;

import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesMQTTClient;
import org.wso2.mb.integration.common.clients.ClientMode;
import org.wso2.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.mb.integration.common.clients.MQTTConstants;
import org.wso2.mb.integration.common.clients.QualityOfService;
import org.wso2.mb.platform.tests.clustering.mqtt.DataProvider.QualityOfServiceDataProvider;

/**
 * Verify MQTT clean session option by sending messages with clean session =
 * false
 * and disconnecting the subscriber.
 */
public class ClusteredCleanSessionTestCase extends MQTTPlatformBaseTest {

    /**
     * Holds information about cluster marked with 'mb002' in automation.xml.
     */
    private AutomationContext automationContextForMB2;

    /**
     * Holds information about cluster marked with 'mb003' in automation.xml.
     */
    private AutomationContext automationContextForMB3;

    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        automationContextForMB2 = getAutomationContextWithKey("mb002");
        automationContextForMB3 = getAutomationContextWithKey("mb003");

    }

    /**
     * 1. Subscribe to a topic with given Quality of Service setting clean
     * session to false.
     * 2. Close the subscriber without unsubscribing.
     * 3. Publish 3 messages to the same topic one from each QOS level.
     * 3. Resubscribe with the same settings.
     * 4. Verify that two messages have been received which were published when
     * client was disconnected which belongs
     * to qos 1 and 2.
     *
     * @param qualityOfService
     *            The Quality of Service of the subscriber
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.mb", "mqtt" }, dataProvider = "QualityOfServiceDataProvider", enabled = false,
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void performCleanSessionSingleNodeTestCase(QualityOfService qualityOfService) throws MqttException,
                                                                                        XPathExpressionException,
                                                                                        InterruptedException {
        int noOfMessagesPerQos = 1;
        int noOfPublishersPerQos = 1;
        int expectedCount = noOfMessagesPerQos * 1; // Only qos 1 and 2 messages
                                                    // are expected

        // QOS 0 subscribers shouldn't receive messages after re-connect.
        if (qualityOfService == QualityOfService.MOST_ONCE) {
            expectedCount = 0;
        }

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        String topic = "CleanSessionSingleNodeTestTopic";

        MQTTClientConnectionConfiguration configuration = buildConfiguration(automationContextForMB2);
        configuration.setCleanSession(false);

        // create the subscribers
        mqttClientEngine.createSubscriberConnection(configuration, topic, qualityOfService, false, ClientMode.BLOCKING);

        // mqttClientEngine.shutdown();

        // Directly get the 0'th value from the list since we only subscribed
        // one subscriber
        AndesMQTTClient subscriber = mqttClientEngine.getSubscriberList().get(0);

        // Disconnect the subscriber
        subscriber.disconnect();

        TimeUnit.MINUTES.sleep(5);

        // Publish qos 0 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configuration);

        // Publish qos 1 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.LEAST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configuration);

        // Publish qos 2 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.EXACTLY_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configuration);

        // Re connect the subscriber and subscribe to the same topic
        // subscriber.connect();
        // subscriber.subscribe();

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(subscriber.getReceivedMessageCount(), expectedCount,
                            "Incorrect number of messages were " +
                                    "received after reconnecting the subscriber");
    }

    /**
     * 1. Subscribe to a topic with given Quality of Service setting clean
     * session to false.
     * 2. Close the subscriber without unsubscribing.
     * 3. Publish 3 messages to the same topic one from each QOS level.
     * 3. Resubscribe with the same settings.
     * 4. Verify that two messages have been received which were published when
     * client was disconnected which belongs
     * to qos 1 and 2.
     *
     * @param qualityOfService
     *            The Quality of Service of the subscriber
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.mb", "mqtt" }, dataProvider = "QualityOfServiceDataProvider", enabled = false,
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void performCleanSessionTwoNodeTestCase(QualityOfService qualityOfService) throws MqttException,
                                                                                     XPathExpressionException,
                                                                                     Exception {

        int noOfMessagesPerQos = 1;
        int noOfPublishersPerQos = 1;
        int expectedCount = noOfMessagesPerQos * 2; // Only qos 1 and 2 messages
                                                    // are expected

        // QOS 0 subscribers shouldn't receive messages after re-connect.
        if (qualityOfService == QualityOfService.MOST_ONCE) {
            expectedCount = 0;
        }

        MQTTClientEngine mqttClientEngineForNode2 = new MQTTClientEngine();
        String topic = "CleanSessssionTwoNodeTestTopic";
        MQTTClientConnectionConfiguration configurationForNode2 = buildConfiguration(automationContextForMB2);
        configurationForNode2.setCleanSession(false);

        // create the subscribers
        mqttClientEngineForNode2.createSubscriberConnection(configurationForNode2, topic, qualityOfService, true,
                                                            ClientMode.BLOCKING);

        mqttClientEngineForNode2.shutdown();

        // Directly get the 0'th value from the list since we only subscribed
        // one subscriber
        AndesMQTTClient subscriber = mqttClientEngineForNode2.getSubscriberList().get(0);

        // Disconnect the subscriber
        subscriber.disconnect();

        TimeUnit.SECONDS.sleep(5);

        MQTTClientConnectionConfiguration configurationForNode3 = buildConfiguration(automationContextForMB3);

        MQTTClientEngine mqttClientEngineForNode3 = new MQTTClientEngine();
        // Publish qos 0 message
        mqttClientEngineForNode3.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                           MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                           noOfMessagesPerQos, ClientMode.BLOCKING,
                                                           configurationForNode3);

        // Publish qos 1 message
        mqttClientEngineForNode3.createPublisherConnection(topic, QualityOfService.LEAST_ONCE,
                                                           MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                           noOfMessagesPerQos, ClientMode.BLOCKING,
                                                           configurationForNode3);

        // Publish qos 2 message
        mqttClientEngineForNode3.createPublisherConnection(topic, QualityOfService.EXACTLY_ONCE,
                                                           MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                           noOfMessagesPerQos, ClientMode.BLOCKING,
                                                           configurationForNode3);

        subscriber.connect();
        subscriber.subscribe();

        mqttClientEngineForNode2.waitUntilAllMessageReceivedAndShutdownClients();
        mqttClientEngineForNode3.shutdown();
        Assert.assertEquals(subscriber.getReceivedMessageCount(), expectedCount,
                            "Incorrect number of messages were " +
                                    "received after reconnecting the subscriber");
    }

    /**
     * 1. Subscribe to a topic with given Quality of Service setting clean
     * session to false.
     * 2. Close the subscriber
     * 3. Un-subscribe from the relevant topic
     * 3. Publish 3 messages to the same topic one from each QOS level.
     * 3. Resubscribe with the same settings.
     * 4. Verify that the messages which were sent had not being consumed
     *
     * @param qualityOfService
     *            The Quality of Service of the subscriber
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.mb", "mqtt" }, dataProvider = "QualityOfServiceDataProvider", enabled = false,
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void performCleanSessionWithUnSubscriptionSingleNodeTestCase(QualityOfService qualityOfService)
                                                                                                          throws MqttException,
                                                                                                          XPathExpressionException,
                                                                                                          InterruptedException
    {
        int noOfMessagesPerQos = 1;
        int noOfPublishersPerQos = 1;
        // Only qos 1 and 2 messages are expected, always we should not expect
        // to receive
        // messages after un-subscribing
        int expectedCount = 0;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        String topic = "CleanSessionWithSubscribeSingleNodeTestTopic";

        MQTTClientConnectionConfiguration configuration = buildConfiguration(automationContextForMB2);
        configuration.setCleanSession(false);

        // create the subscribers
        mqttClientEngine.createSubscriberConnection(configuration, topic, qualityOfService, false, ClientMode.BLOCKING);

        // Directly get the 0'th value from the list since we only subscribed
        // one subscriber
        AndesMQTTClient subscriber = mqttClientEngine.getSubscriberList().get(0);

        // Will unsubscribe
        subscriber.unsubscribe();
        // Disconnect the subscriber
        subscriber.disconnect();

        // Will shut-down any connected client
        mqttClientEngine.shutdown();

        TimeUnit.SECONDS.sleep(5);

        // Publish qos 0 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configuration);

        // Publish qos 1 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.LEAST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configuration);

        // Publish qos 2 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.EXACTLY_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configuration);

        // Re connect the subscriber and subscribe to the same topic
        subscriber.connect();
        subscriber.subscribe();

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(subscriber.getReceivedMessageCount(), expectedCount,
                            "Incorrect number of messages were " +
                                    "received after connecting to the subscriber");
    }

    /**
     * 1. Subscribe to a topic with given Quality of Service setting clean
     * session to false.
     * 2. Close the subscriber
     * 3. Un-subscribe from the relevant topic
     * 3. Publish 3 messages to the same topic one from each QOS level.
     * 3. Resubscribe with the same settings.
     * 4. Verify that the messages which were sent had not being consumed
     *
     * @param qualityOfService
     *            The Quality of Service of the subscriber
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.mb", "mqtt" }, dataProvider = "QualityOfServiceDataProvider", enabled = false,
            dataProviderClass = QualityOfServiceDataProvider.class)
    public void performCleanSessionWithUnSubscriptionTwoNodeTestCase(QualityOfService qualityOfService)
                                                                                                       throws MqttException,
                                                                                                       XPathExpressionException,
                                                                                                       Exception {
        int noOfMessagesPerQos = 1;
        int noOfPublishersPerQos = 1;
        // Only qos 1 and 2 messages are expected, always we should not expect
        // to receive
        // messages after un-subscribing
        int expectedCount = 0;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        String topic = "CleanSessionWithSubscribeTwoNodeTestTopic";

        MQTTClientConnectionConfiguration configuration = buildConfiguration(automationContextForMB2);
        configuration.setCleanSession(false);

        // create the subscribers
        mqttClientEngine.createSubscriberConnection(configuration, topic, qualityOfService, false, ClientMode.BLOCKING);

        // Directly get the 0'th value from the list since we only subscribed
        // one subscriber
        AndesMQTTClient subscriber = mqttClientEngine.getSubscriberList().get(0);

        // Will unsubscribe
        subscriber.unsubscribe();
        // Disconnect the subscriber
        subscriber.disconnect();

        TimeUnit.SECONDS.sleep(5);

        // Will shut-down any connected client
        mqttClientEngine.shutdown();

        MQTTClientConnectionConfiguration configurationForNode3 = buildConfiguration(automationContextForMB3);

        // Publish qos 0 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.MOST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configurationForNode3);

        // Publish qos 1 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.LEAST_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configurationForNode3);

        // Publish qos 2 message
        mqttClientEngine.createPublisherConnection(topic, QualityOfService.EXACTLY_ONCE,
                                                   MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishersPerQos,
                                                   noOfMessagesPerQos, ClientMode.BLOCKING, configurationForNode3);

        // Re connect the subscriber and subscribe to the same topic
        subscriber.connect();
        subscriber.subscribe();

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        Assert.assertEquals(subscriber.getReceivedMessageCount(), expectedCount,
                            "Incorrect number of messages were " +
                                    "received after connecting to the subscriber");
    }
}
