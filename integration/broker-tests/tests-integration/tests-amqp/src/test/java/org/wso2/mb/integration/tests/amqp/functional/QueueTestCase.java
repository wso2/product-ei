/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.functional;

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

/**
 * Test cases for queue related scenarios.
 */
public class QueueTestCase extends MBIntegrationBaseTest {

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Create queue names "singleQueue".
     * 2. Publish 1000 messages to queue.
     * 3. Consumer should receive 1000 messages.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single queue send-receive test case")
    public void performSingleQueueSendReceiveTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        long sendCount = 1000L;
        long expectedCount = 1000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueue");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

    }

    /**
     * 1. Create a topic named "subTopicPubQueue"
     * 2. Create a subscriber for that topic.
     * 3. Publish messages to a queue that has the same name as "subTopicPubQueue".
     * 4. Subscriber should not receive any messages.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "subscribe to a topic and send message to a queue which has the same name" +
                                            " as queue")
    public void performSubTopicPubQueueTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        long sendCount = 1000L;
        long expectedCount = 1000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "subTopicPubQueue");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "subTopicPubQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), 0, "Messages should have not received");
    }

    /**
     * 1. Create 2 consumers for queue name "queueManyConsumers"
     * 2. Publish 3000 message to queue name "queueManyConsumers"
     * 3. Total messages received by both consumers should be 3000 messages.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "send large number of messages to a queue which has two consumers")
    public void performManyConsumersTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        long sendCount = 3000L;
        long expectedCount = 3000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueManyConsumers");
        consumerConfig1.setMaximumMessagesToReceived(expectedCount);
        consumerConfig1.setPrintsPerMessageCount(expectedCount / 10L);
        consumerConfig1.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueManyConsumers");
        consumerConfig2.setMaximumMessagesToReceived(expectedCount);
        consumerConfig2.setPrintsPerMessageCount(100L);
        consumerConfig2.setAsync(false);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueManyConsumers");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(100L);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        // Waiting for all messages
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);

        // Evaluating
        long msgCountFromClient1 = consumerClient1.getReceivedMessageCount();
        long msgCountFromClient2 = consumerClient2.getReceivedMessageCount();

        Assert.assertEquals(msgCountFromClient1 + msgCountFromClient2, expectedCount,
                            "Did not received expected message count");
    }

    /**
     * 1. Subscribe to a queue named "CASEInsensitiveQueue".
     * 2. Publish 1000 messages to 'caseINSENSITIVEQueue'.
     * 3. Consumer should receive 1000 messages.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single queue send-receive test case for queue names in different cases")
    public void performDifferentCasesQueueSendReceiveTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, XPathExpressionException {

        long sendCount = 1000L;
        long expectedCount = 1000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "CASEInsensitiveQueue");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "caseINSENSITIVEQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

    }
}
