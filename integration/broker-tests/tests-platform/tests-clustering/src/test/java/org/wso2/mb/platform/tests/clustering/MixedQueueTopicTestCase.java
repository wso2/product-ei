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

package org.wso2.mb.platform.tests.clustering;

import com.google.common.net.HostAndPort;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.AndesJMSConsumer;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Perform tests with all possible AMQP types and large number of messages.
 */
public class MixedQueueTopicTestCase extends MBPlatformBaseTest {

    final long printPerMessageCount = 1000;

    String queueName1 = "mixedQueue1";
    String queueName2 = "mixedQueue2";

    String topicName1 = "mixedTopic1";
    String topicName2 = "mixedTopic2";

    HostAndPort broker1;
    HostAndPort broker2;

    Map<String, Set<AndesClient>> publishers = new HashMap<>();
    Map<String, Set<AndesClient>> subscribers = new HashMap<>();

    Map<String, Long> sendCounts = new HashMap<>();


    /**
     * Prepare environment for tests.
     *
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws LoginAuthenticationExceptionException, IOException, XPathExpressionException,
            URISyntaxException, SAXException, XMLStreamException, AutomationUtilException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        super.initAndesAdminClients();
        broker1 = getRandomAMQPBrokerAddress();
        broker2 = getRandomAMQPBrokerAddress();
    }

    /**
     * Send a large number of messages and receive via
     * 1. Queues
     * 2. Non-Durable Topics
     * 3. Durable Topics
     *
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws NamingException
     * @throws AndesClientException
     * @throws JMSException
     */
    @Test(groups = "wso2.mb", description = "Test combination of queues/topics/durable topics with a large number"
            + "of messages")
    public void performMixedQueueTopicTest() throws XPathExpressionException, AndesClientConfigurationException,
            IOException, NamingException, AndesClientException, JMSException {

        // Create all the subscribers

        createQueue1Subscribers();
        createQueue2Subscribers();
        createTopic1Subscribers();
        createTopic2Subscribers();
        createTopic1DurableSubscribers();
        createTopic2DurableSubscribers();

        // Start all the subscribers

        for (Map.Entry<String, Set<AndesClient>> entry : subscribers.entrySet()) {
            for (AndesClient client : entry.getValue()) {
                client.startClient();
            }
        }

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        // Create all the publishers

        createQueue1Publishers();
        createQueue2Publishers();
        createTopic1Publishers();
        createTopic2Publishers();

        // Start publishing

        for (Map.Entry<String, Set<AndesClient>> entry : publishers.entrySet()) {
            for (AndesClient client : entry.getValue()) {
                client.startClient();
            }
        }

        // Verify message counts

        verifyMessageCounts();


    }

    /**
     * Create subscribers for the first queue.
     *
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createQueue1Subscribers() throws AndesClientException, JMSException, IOException, NamingException,
            AndesClientConfigurationException {
        AndesJMSConsumerClientConfiguration queue1ConsumerBroker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.QUEUE, queueName1);
        queue1ConsumerBroker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration queue1ConsumerBroker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.QUEUE, queueName1);
        queue1ConsumerBroker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> queue1Subscribers = new HashSet<>();

        AndesClient queue1ConsumerBroker1 = new AndesClient(queue1ConsumerBroker1Config, true);
        queue1Subscribers.add(queue1ConsumerBroker1);

        AndesClient queue1ConsumerBroker2 = new AndesClient(queue1ConsumerBroker2Config, true);
        queue1Subscribers.add(queue1ConsumerBroker2);

        subscribers.put(queueName1, queue1Subscribers);
    }

    /**
     * Create subscribers for the second queue.
     *
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createQueue2Subscribers() throws AndesClientException, JMSException, IOException, NamingException,
            AndesClientConfigurationException {
        AndesJMSConsumerClientConfiguration queue2ConsumerBroker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.QUEUE, queueName2);
        queue2ConsumerBroker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration queue2ConsumerBroker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.QUEUE, queueName2);
        queue2ConsumerBroker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> queue2Subscribers = new HashSet<>();
        AndesClient queue2ConsumerBroker1 = new AndesClient(queue2ConsumerBroker1Config, true);
        queue2Subscribers.add(queue2ConsumerBroker1);

        AndesClient queue2ConsumerBroker2 = new AndesClient(queue2ConsumerBroker2Config, true);
        queue2Subscribers.add(queue2ConsumerBroker2);

        subscribers.put(queueName2, queue2Subscribers);
    }

    /**
     * Create non-durable subscribers for the first topic.
     *
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createTopic1Subscribers() throws AndesClientException, JMSException, IOException, NamingException,
            AndesClientConfigurationException {
        AndesJMSConsumerClientConfiguration topic1ConsumerBroker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName1);
        topic1ConsumerBroker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration topic1ConsumerBroker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName1);
        topic1ConsumerBroker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> topic1Subscribers = new HashSet<>();

        // Create 3 subscribers to broker1 and 2 subscribers to broker2
        AndesClient topic1Consumer1Broker1 = new AndesClient(topic1ConsumerBroker1Config, 3, true);
        topic1Subscribers.add(topic1Consumer1Broker1);

        AndesClient topic1Consumer1Broker2 = new AndesClient(topic1ConsumerBroker2Config, 2,  true);
        topic1Subscribers.add(topic1Consumer1Broker2);

        subscribers.put(topicName1, topic1Subscribers);
    }

    /**
     * Create non-durable subscribers for the second topic.
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createTopic2Subscribers() throws IOException, JMSException, AndesClientException, NamingException,
            AndesClientConfigurationException {
        AndesJMSConsumerClientConfiguration topic2ConsumerBroker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName2);
        topic2ConsumerBroker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration topic2ConsumerBroker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName2);
        topic2ConsumerBroker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> topic2Subscribers = new HashSet<>();

        // Create 2 subscribers to broker1 and 3 subscribers to broker2
        AndesClient topic2Consumer1Broker1 = new AndesClient(topic2ConsumerBroker1Config, 2, true);
        topic2Subscribers.add(topic2Consumer1Broker1);


        AndesClient topic2Consumer2Broker2 = new AndesClient(topic2ConsumerBroker2Config, 3, true);
        topic2Subscribers.add(topic2Consumer2Broker2);

        subscribers.put(topicName2, topic2Subscribers);

    }

    /**
     * Create durable subscribers for the first topic.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    private void createTopic1DurableSubscribers() throws AndesClientConfigurationException, IOException,
            JMSException, AndesClientException, NamingException {
        AndesJMSConsumerClientConfiguration durable1Consumer1Broker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName1);
        durable1Consumer1Broker1Config.setDurable(true, "ultimateDurable1Sub1Broker1");
        durable1Consumer1Broker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable1Consumer2Broker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName1);
        durable1Consumer2Broker1Config.setDurable(true, "ultimateDurable1Sub2Broker1");
        durable1Consumer2Broker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable1Consumer3Broker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName1);
        durable1Consumer3Broker1Config.setDurable(true, "ultimateDurable1Sub3Broker1");
        durable1Consumer3Broker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable1Consumer1Broker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName1);
        durable1Consumer1Broker2Config.setDurable(true, "ultimateDurable1Sub1Broker2");
        durable1Consumer1Broker2Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable1Consumer2Broker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName1);
        durable1Consumer2Broker2Config.setDurable(true, "ultimateDurable1Sub2Broker2");
        durable1Consumer2Broker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> durableTopic1Subscribers = new HashSet<>();

        AndesClient durable1Consumer1Broker1 = new AndesClient(durable1Consumer1Broker1Config, true);
        durableTopic1Subscribers.add(durable1Consumer1Broker1);

        AndesClient durable1Consumer2Broker1 = new AndesClient(durable1Consumer2Broker1Config, true);
        durableTopic1Subscribers.add(durable1Consumer2Broker1);

        AndesClient durable1Consumer3Broker1 = new AndesClient(durable1Consumer3Broker1Config, true);
        durableTopic1Subscribers.add(durable1Consumer3Broker1);

        AndesClient durable1Consumer1Broker2 = new AndesClient(durable1Consumer1Broker2Config, true);
        durableTopic1Subscribers.add(durable1Consumer1Broker2);

        AndesClient durable1Consumer2Broker2 = new AndesClient(durable1Consumer2Broker2Config, true);
        durableTopic1Subscribers.add(durable1Consumer2Broker2);

        subscribers.put(topicName1, durableTopic1Subscribers);
    }

    /**
     * Create durable subscribers for the second topic.
     *
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     */
    private void createTopic2DurableSubscribers() throws AndesClientConfigurationException, AndesClientException,
            JMSException, IOException, NamingException {
        AndesJMSConsumerClientConfiguration durable2Consumer1Broker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName2);
        durable2Consumer1Broker1Config.setDurable(true, "ultimateDurable2Sub1Broker1");
        durable2Consumer1Broker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable2Consumer2Broker1Config =
                new AndesJMSConsumerClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName2);
        durable2Consumer2Broker1Config.setDurable(true, "ultimateDurable2Sub2Broker1");
        durable2Consumer2Broker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable2Consumer1Broker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName2);
        durable2Consumer1Broker2Config.setDurable(true, "ultimateDurable1Sub3Broker2");
        durable2Consumer1Broker2Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable2Consumer2Broker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName2);
        durable2Consumer2Broker2Config.setDurable(true, "ultimateDurable2Sub1Broker2");
        durable2Consumer2Broker2Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSConsumerClientConfiguration durable2Consumer3Broker2Config =
                new AndesJMSConsumerClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName2);
        durable2Consumer3Broker2Config.setDurable(true, "ultimateDurable2Sub2Broker2");
        durable2Consumer3Broker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> durableTopic2Subscribers = new HashSet<>();

        AndesClient durable2Consumer1Broker1 = new AndesClient(durable2Consumer1Broker1Config, true);
        durableTopic2Subscribers.add(durable2Consumer1Broker1);

        AndesClient durable2Consumer2Broker1 = new AndesClient(durable2Consumer2Broker1Config, true);
        durableTopic2Subscribers.add(durable2Consumer2Broker1);

        AndesClient durable2Consumer1Broker2 = new AndesClient(durable2Consumer1Broker2Config, true);
        durableTopic2Subscribers.add(durable2Consumer1Broker2);

        AndesClient durable2Consumer2Broker2 = new AndesClient(durable2Consumer2Broker2Config, true);
        durableTopic2Subscribers.add(durable2Consumer2Broker2);

        AndesClient durable2Consumer3Broker2 = new AndesClient(durable2Consumer3Broker2Config, true);
        durableTopic2Subscribers.add(durable2Consumer3Broker2);

        subscribers.put(topicName2, durableTopic2Subscribers);


    }

    /**
     * Create publishers for the first queue.
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createQueue1Publishers() throws IOException, JMSException, AndesClientException, NamingException,
            AndesClientConfigurationException {
        long queueSendCountPerPublisher = 50000;

        AndesJMSPublisherClientConfiguration queue1PublisherBroker1Config =
                new AndesJMSPublisherClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.QUEUE, queueName1);
        queue1PublisherBroker1Config.setNumberOfMessagesToSend(queueSendCountPerPublisher);
        queue1PublisherBroker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSPublisherClientConfiguration queue1PublisherBroker2Config =
                new AndesJMSPublisherClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.QUEUE, queueName1);
        queue1PublisherBroker2Config.setNumberOfMessagesToSend(queueSendCountPerPublisher);
        queue1PublisherBroker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> queue1Publishers= new HashSet<>();

        AndesClient queue1PublisherBroker1 = new AndesClient(queue1PublisherBroker1Config, true);
        queue1Publishers.add(queue1PublisherBroker1);

        AndesClient queue1PublisherBroker2 = new AndesClient(queue1PublisherBroker2Config, true);
        queue1Publishers.add(queue1PublisherBroker2);

        publishers.put(queueName1, queue1Publishers);

        sendCounts.put(queueName1, queueSendCountPerPublisher * 2);
    }

    /**
     * Create publishers for the second queue.
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createQueue2Publishers() throws IOException, JMSException, AndesClientException, NamingException,
            AndesClientConfigurationException {
        long queueSendCountPerPublisher = 50000;

        AndesJMSPublisherClientConfiguration queue2PublisherBroker1Config =
                new AndesJMSPublisherClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.QUEUE, queueName2);
        queue2PublisherBroker1Config.setNumberOfMessagesToSend(queueSendCountPerPublisher);
        queue2PublisherBroker1Config.setPrintsPerMessageCount(printPerMessageCount);

        AndesJMSPublisherClientConfiguration queue2PublisherBroker2Config =
                new AndesJMSPublisherClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.QUEUE, queueName2);
        queue2PublisherBroker2Config.setNumberOfMessagesToSend(queueSendCountPerPublisher);
        queue2PublisherBroker2Config.setPrintsPerMessageCount(printPerMessageCount);

        Set<AndesClient> queue2Publishers= new HashSet<>();

        AndesClient queue2PublisherBroker1 = new AndesClient(queue2PublisherBroker1Config, true);
        queue2Publishers.add(queue2PublisherBroker1);

        AndesClient queue2PublisherBroker2 = new AndesClient(queue2PublisherBroker2Config, true);
        queue2Publishers.add(queue2PublisherBroker2);

        publishers.put(queueName2, queue2Publishers);

        sendCounts.put(queueName2, queueSendCountPerPublisher * 2);
    }

    /**
     * Create publishers for the first topic.
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createTopic1Publishers() throws IOException, JMSException, AndesClientException, NamingException,
            AndesClientConfigurationException {
        long topicSendCountPerPublisher = 25000;

        AndesJMSPublisherClientConfiguration topic1PublisherBroker1Config =
                new AndesJMSPublisherClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName1);
        topic1PublisherBroker1Config.setNumberOfMessagesToSend(topicSendCountPerPublisher);
        topic1PublisherBroker1Config.setPrintsPerMessageCount(printPerMessageCount);
        topic1PublisherBroker1Config.setRunningDelay(1L);

        AndesJMSPublisherClientConfiguration topic1PublisherBroker2Config =
                new AndesJMSPublisherClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName1);
        topic1PublisherBroker2Config.setNumberOfMessagesToSend(topicSendCountPerPublisher);
        topic1PublisherBroker2Config.setPrintsPerMessageCount(printPerMessageCount);
        topic1PublisherBroker2Config.setRunningDelay(1L);

        Set<AndesClient> topic1Publishers= new HashSet<>();

        AndesClient topic1PublisherBroker1 = new AndesClient(topic1PublisherBroker1Config, true);
        topic1Publishers.add(topic1PublisherBroker1);

        AndesClient topic1PublisherBroker2 = new AndesClient(topic1PublisherBroker2Config, true);
        topic1Publishers.add(topic1PublisherBroker2);

        publishers.put(topicName1, topic1Publishers);

        sendCounts.put(topicName1, topicSendCountPerPublisher * 2);

    }

    /**
     * Create publishers for the second topic.
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    private void createTopic2Publishers() throws IOException, JMSException, AndesClientException, NamingException,
            AndesClientConfigurationException {
        long topicSendCountPerPublisher = 25000;

        AndesJMSPublisherClientConfiguration topic2PublisherBroker1Config =
                new AndesJMSPublisherClientConfiguration(broker1.getHostText(),
                        broker1.getPort(), ExchangeType.TOPIC, topicName2);
        topic2PublisherBroker1Config.setNumberOfMessagesToSend(topicSendCountPerPublisher);
        topic2PublisherBroker1Config.setPrintsPerMessageCount(printPerMessageCount);
        topic2PublisherBroker1Config.setRunningDelay(1L);

        AndesJMSPublisherClientConfiguration topic2PublisherBroker2Config =
                new AndesJMSPublisherClientConfiguration(broker2.getHostText(),
                        broker2.getPort(), ExchangeType.TOPIC, topicName2);
        topic2PublisherBroker2Config.setNumberOfMessagesToSend(topicSendCountPerPublisher);
        topic2PublisherBroker2Config.setPrintsPerMessageCount(printPerMessageCount);
        topic2PublisherBroker2Config.setRunningDelay(1L);

        Set<AndesClient> topic2Publishers= new HashSet<>();

        AndesClient topic2PublisherBroker1 = new AndesClient(topic2PublisherBroker1Config, true);
        topic2Publishers.add(topic2PublisherBroker1);

        AndesClient topic2PublisherBroker2 = new AndesClient(topic2PublisherBroker2Config, true);
        topic2Publishers.add(topic2PublisherBroker2);

        publishers.put(topicName2, topic2Publishers);

        sendCounts.put(topicName2, topicSendCountPerPublisher * 2);
    }

    /**
     * Run asserts and verify all the published messages are received accordingly.
     *
     * @throws JMSException
     */
    private void verifyMessageCounts() throws JMSException {
        // Verify all messages are received for queue1
        long queue1Count = 0;

        for (AndesClient subscriber : subscribers.get(queueName1)) {
            // we are waiting for 60s here since flow control can delay messages for a long time due to heavy load
            AndesClientUtils.waitForMessagesAndShutdown(subscriber, AndesClientConstants.DEFAULT_RUN_TIME * 6);
            queue1Count = queue1Count + subscriber.getReceivedMessageCount();
        }

        Assert.assertEquals(queue1Count, sendCounts.get(queueName1).longValue(), "Did not receive expected count for "
                + queueName1);

        // Verify all messages are received for queue2
        long queue2Count = 0;

        for (AndesClient subscriber : subscribers.get(queueName2)) {
            AndesClientUtils.waitForMessagesAndShutdown(subscriber, AndesClientConstants.DEFAULT_RUN_TIME);
            queue2Count = queue2Count + subscriber.getReceivedMessageCount();
        }

        Assert.assertEquals(queue2Count, sendCounts.get(queueName2).longValue(), "Did not receive expected count for "
                + queueName2

        );

        // Verify all messages are received for topic1 subscribers
        long topic1SendCount = sendCounts.get(topicName1);

        for (AndesClient subscriber : subscribers.get(topicName1)) {
            AndesClientUtils.waitForMessagesAndShutdown(subscriber, AndesClientConstants.DEFAULT_RUN_TIME);

            for (AndesJMSConsumer consumer : subscriber.getConsumers()) {
                Assert.assertEquals(consumer.getReceivedMessageCount().get(), topic1SendCount, "Did not receive expected "
                        + "count for topic " + topicName1 + " for subscriber "
                        + consumer.getConfig().getSubscriptionID());
            }
        }

        // Verify all messages are received for topic2 subscribers
        long topic2SendCount = sendCounts.get(topicName2);

        for (AndesClient subscriber : subscribers.get(topicName2)) {
            AndesClientUtils.waitForMessagesAndShutdown(subscriber, AndesClientConstants.DEFAULT_RUN_TIME);

            for (AndesJMSConsumer consumer : subscriber.getConsumers()) {
                Assert.assertEquals(consumer.getReceivedMessageCount().get(), topic2SendCount, "Did not receive expected "
                        + "count for topic " + topicName2 + " for subscriber "
                        + consumer.getConfig().getSubscriptionID());
            }
        }



    }
}
