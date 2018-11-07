/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.load;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for multiple queues and topics running parallel for consumers and publishers.
 */
public class MultiThreadedMultipleQueueTopicTestCase extends MBIntegrationBaseTest {
    private static final long SEND_COUNT = 30000L;
    private static final long ADDITIONAL = 30L;
    private static final long EXPECTED_COUNT = SEND_COUNT + ADDITIONAL;
    private static final int QUEUE_NUMBER_OF_SUBSCRIBERS = 45;
    private static final int QUEUE_NUMBER_OF_PUBLISHERS = 45;
    private static final int TOPIC_NUMBER_OF_SUBSCRIBERS = 45;
    private static final int TOPIC_NUMBER_OF_PUBLISHERS = 15;
    private static final String[] QUEUE_DESTINATIONS =
            {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15"};
    private static final String[] TOPIC_DESTINATIONS =
            {"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12", "T13", "T14", "T15"};
    private List<AndesClient> consumers = new ArrayList<AndesClient>();
    private List<AndesClient> publishers = new ArrayList<AndesClient>();

    /**
     * Initialize the test as super tenant user.
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Define 15 queues and topic.
     * 2. Send 30000 messages to 15 queues (2000 to each) by 45 threads.
     * 2. Send 30000 messages to 15 topic (2000 to each) by 15 threads.
     * 3. Receive messages from 15 queues by 45 threads.
     * 3. Receive messages from 15 topics by 45 threads.
     * 4. Verify that all messages are received and no more messages are received.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb"})
    public void performMultiThreadedMultipleQueueTopicTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException {

        // Creating consumers
        for (String queueDestination : QUEUE_DESTINATIONS) {
            // Creating a queue consumer client configuration
            AndesJMSConsumerClientConfiguration consumerConfig =
                    new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, queueDestination);
            consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
            consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);

            // Creating client
            consumers
                    .add(new AndesClient(consumerConfig, QUEUE_NUMBER_OF_SUBSCRIBERS / QUEUE_DESTINATIONS.length, true));
        }

        for (String topicDestination : TOPIC_DESTINATIONS) {
            // Creating a topic consumer client configuration
            AndesJMSConsumerClientConfiguration consumerConfig =
                    new AndesJMSConsumerClientConfiguration(ExchangeType.TOPIC, topicDestination);
            consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
            consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);

            // Creating client
            consumers
                    .add(new AndesClient(consumerConfig, TOPIC_NUMBER_OF_SUBSCRIBERS / TOPIC_DESTINATIONS.length, true));
        }

        // Creating publishers
        for (String queueDestinations : QUEUE_DESTINATIONS) {
            // Creating a queue publisher client configuration
            AndesJMSPublisherClientConfiguration publisherConfig =
                    new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, queueDestinations);
            publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
            publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

            // Creating client
            publishers
                    .add(new AndesClient(publisherConfig, QUEUE_NUMBER_OF_PUBLISHERS / QUEUE_DESTINATIONS.length, true));
        }

        for (String topicDestination : TOPIC_DESTINATIONS) {
            // Creating a topic publisher client configuration
            AndesJMSPublisherClientConfiguration publisherConfig =
                    new AndesJMSPublisherClientConfiguration(ExchangeType.TOPIC, topicDestination);
            publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
            publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

            // Creating client
            publishers
                    .add(new AndesClient(publisherConfig, TOPIC_NUMBER_OF_PUBLISHERS / TOPIC_DESTINATIONS.length, true));
        }

        // Starting up clients
        for (AndesClient consumer : consumers) {
            consumer.startClient();
        }

        for (AndesClient publisher : publishers) {
            publisher.startClient();
        }

        for (AndesClient consumer : consumers) {
            AndesClientUtils
                    .waitForMessagesAndShutdown(consumer, AndesClientConstants.DEFAULT_RUN_TIME * 3L);
        }

        // Evaluating
        for (AndesClient publisher : publishers) {
            if (ExchangeType.QUEUE == publisher.getPublishers().get(0).getConfig().getExchangeType()) {
                Assert.assertEquals(publisher
                                            .getSentMessageCount(), SEND_COUNT * (QUEUE_NUMBER_OF_PUBLISHERS / QUEUE_DESTINATIONS.length), "Message sending failed for queues for " + publisher
                        .getPublishers().get(0).getConfig().getDestinationName());
            } else if (ExchangeType.TOPIC == publisher.getPublishers().get(0).getConfig().getExchangeType()) {
                Assert.assertEquals(publisher
                                            .getSentMessageCount(), SEND_COUNT * (TOPIC_NUMBER_OF_PUBLISHERS / TOPIC_DESTINATIONS.length), "Message sending failed for topics " + publisher
                        .getPublishers().get(0).getConfig().getDestinationName());
            }
        }

        long totalQueueMessagesReceived = 0L;
        long totalTopicMessagesReceived = 0L;
        for (AndesClient consumer : consumers) {
            if (ExchangeType.QUEUE == consumer.getConsumers().get(0).getConfig().getExchangeType()) {
                Assert.assertEquals(consumer.getReceivedMessageCount(), (EXPECTED_COUNT - ADDITIONAL) * (TOPIC_NUMBER_OF_SUBSCRIBERS / QUEUE_DESTINATIONS.length), "Message receiving failed " + consumer
                        .getConsumers().get(0).getConfig().getDestinationName());
                totalQueueMessagesReceived =
                        totalQueueMessagesReceived + consumer.getReceivedMessageCount();
            } else if (ExchangeType.TOPIC == consumer.getConsumers().get(0).getConfig().getExchangeType()) {
                Assert.assertEquals(consumer.getReceivedMessageCount(), EXPECTED_COUNT - ADDITIONAL, "Message receiving failed " + consumer
                        .getConsumers().get(0).getConfig().getDestinationName());
                totalTopicMessagesReceived =
                        totalTopicMessagesReceived + consumer.getReceivedMessageCount();
            }
        }

        Assert.assertEquals(totalQueueMessagesReceived, SEND_COUNT * (QUEUE_NUMBER_OF_SUBSCRIBERS / QUEUE_DESTINATIONS.length), "Message receiving failed.");
        Assert.assertEquals(totalQueueMessagesReceived, (EXPECTED_COUNT - ADDITIONAL) * TOPIC_NUMBER_OF_SUBSCRIBERS / TOPIC_DESTINATIONS.length, "Message receiving failed.");
    }
}
