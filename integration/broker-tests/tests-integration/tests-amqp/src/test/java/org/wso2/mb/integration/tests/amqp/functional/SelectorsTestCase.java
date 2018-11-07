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

package org.wso2.mb.integration.tests.amqp.functional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.AndesJMSPublisher;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.JMSHeaderPropertyType;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * Test case to test functionality of selectors. Selectors can be used to filter messages received
 * for the consumer.
 */
public class SelectorsTestCase extends MBIntegrationBaseTest {

    /**
     * Message count sent
     */
    private static final long SEND_COUNT = 10L;

    /**
     * Expected message count
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * Initializes test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Create a consumer that accepts messages with JMSType message header value having as AAA
     * 2. Publish messages that does not have JMSType value as AAA
     * 3. Verify that no messages are received by receiver.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "send-receive test case with jms selectors without conforming messages")
    public void performQueueSendWithReceiverHavingSelectorsButNoModifiedPublisherSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE, "jmsSelectorSubscriberJMSType");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setSelectors("JMSType='AAA'");
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE, "jmsSelectorSubscriberJMSType");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), 0, "Message receiving failed.");
    }

    /**
     * 1. Create a consumer that accepts messages with JMSType message header value having as AAA
     * 2. Publish messages that does have JMSType value as AAA
     * 3. Verify that all sent messages received by receiver.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performQueueSendWithModifiedPublisherSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE, "jmsSelectorSubscriberAndPublisherJMSType");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setSelectors("JMSType='AAA'");
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE, "jmsSelectorSubscriberAndPublisherJMSType");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setJMSType("AAA");

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
    }

    /**
     * 1. Create a consumer that accepts message which are published 1 second after the current time.
     * 2. Publisher sends messages with a delay.
     * 3. Consumer will receive a certain amount of messages. But will not receive all messages.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performQueueSendWithTimestampBasedSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE, "jmsSelectorSubscriberJMSTimestamp");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setSelectors("JMSTimestamp > " + Long.toString(System.currentTimeMillis() + 1000L));
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE, "jmsSelectorSubscriberJMSTimestamp");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setRunningDelay(300L);  // Setting publishing delay

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertTrue(consumerClient
                                  .getReceivedMessageCount() < EXPECTED_COUNT, "Message receiving failed.");
    }

    /**
     * 1. Create a consumer that filters out message which has the "location" property as "wso2.trace".
     * 2. 2 publishers will send messages with one having location as "wso2.trace" and another having
     * "wso2.palmgrove".
     * 3. Consumer should only receive messages having "wso2.trace".
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performQueueReceiverCustomPropertyBasedSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE, "jmsSelectorSubscriberCustomProperty");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setSelectors("location = 'wso2.trace'");
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration initialPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE, "jmsSelectorSubscriberCustomProperty");
        initialPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT / 2L);
        initialPublisherConfig.setJMSHeaderProperty("location", "wso2.trace", JMSHeaderPropertyType.STRING);


        AndesJMSPublisherClientConfiguration secondaryPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE, "jmsSelectorSubscriberCustomProperty");
        secondaryPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT / 2L);
        secondaryPublisherConfig.setJMSHeaderProperty("location", "wso2.palmgrove", JMSHeaderPropertyType.STRING);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient initialPublisherClient = new AndesClient(initialPublisherConfig, true);
        initialPublisherClient.startClient();

        AndesClient secondaryPublisherClient = new AndesClient(secondaryPublisherConfig, true);
        secondaryPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(initialPublisherClient.getSentMessageCount(), SEND_COUNT / 2L,
                "Message sending failed for first client");
        Assert.assertEquals(secondaryPublisherClient.getSentMessageCount(), SEND_COUNT / 2L,
                "Message sending failed for second client");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), SEND_COUNT / 2L,
                "Message receiving failed.");

    }

    /**
     * 1. Create consumer that filters out messages having "location" as "wso2.trace" and "JMSType" as "MyMessage".
     * 2. Create 2 publisher. One publisher publishing with messages having "location" as "wso2.trace"
     * and "JMSType" as "MyMessage" in message header. Other having "location" as "wso2.palmGrove"
     * and "JMSType" as "otherMessage" in message header.
     * 3. Consumer should only receive messages having header "location" as "wso2.trace" and "JMSType" as "MyMessage".
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performQueueReceiverCustomPropertyAndJMSTypeBasedSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE,
                                                        "jmsSelectorSubscriberCustomPropertyAndJMSType");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setSelectors("location = 'wso2.trace' AND JMSType='myMessage'");
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration initialPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                                                         "jmsSelectorSubscriberCustomPropertyAndJMSType");
        initialPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT / 2L);
        initialPublisherConfig.setJMSType("myMessage");
        initialPublisherConfig.setJMSHeaderProperty("location", "wso2.trace", JMSHeaderPropertyType.STRING);

        AndesJMSPublisherClientConfiguration secondaryPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                                                         "jmsSelectorSubscriberCustomPropertyAndJMSType");
        secondaryPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT / 2L);
        secondaryPublisherConfig.setJMSType("otherMessage");
        secondaryPublisherConfig.setJMSHeaderProperty("location", "wso2.palmGrove", JMSHeaderPropertyType.STRING);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient initialPublisherClient = new AndesClient(initialPublisherConfig, true);
        initialPublisherClient.startClient();

        AndesClient secondaryPublisherClient = new AndesClient(secondaryPublisherConfig, true);
        secondaryPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(initialPublisherClient.getSentMessageCount(), SEND_COUNT / 2L,
                "Message sending failed for first client");
        Assert.assertEquals(secondaryPublisherClient.getSentMessageCount(), SEND_COUNT / 2L,
                "Message sending failed for second client");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), SEND_COUNT / 2L,
                "Message receiving failed.");
    }

    /**
     * 1. Create consumer that filters out messages having "location" as "wso2.palmgrove" or "JMSType" as "MyMessage".
     * 2. Create 2 publisher. One publisher publishing with messages having "location" as "wso2.trace"
     * and "JMSType" as "MyMessage" in message header. Other having "location" as "wso2.palmGrove"
     * and "JMSType" as "otherMessage" in message header.
     * 3. Consumer should receive all sent messages.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performQueueReceiverCustomPropertyOrJMSTypeBasedSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE,
                                                        "jmsSelectorSubscriberCustomPropertyOrJMSType");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setSelectors("location = 'wso2.palmGrove' OR JMSType='myMessage'");
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration initialPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE,
                                                         "jmsSelectorSubscriberCustomPropertyOrJMSType");
        initialPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT / 2L);
        initialPublisherConfig.setJMSType("myMessage");
        initialPublisherConfig.setJMSHeaderProperty("location", "wso2.trace", JMSHeaderPropertyType.STRING);

        AndesJMSPublisherClientConfiguration secondaryPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE,
                                                         "jmsSelectorSubscriberCustomPropertyOrJMSType");
        secondaryPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT / 2L);
        secondaryPublisherConfig.setJMSType("otherMessage");
        secondaryPublisherConfig.setJMSHeaderProperty("location", "wso2.palmGrove", JMSHeaderPropertyType.STRING);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient initialPublisherClient = new AndesClient(initialPublisherConfig, true);
        initialPublisherClient.startClient();

        AndesClient secondaryPublisherClient = new AndesClient(secondaryPublisherConfig, true);
        secondaryPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(initialPublisherClient.getSentMessageCount(), SEND_COUNT / 2L,
                "Message sending failed for first client");
        Assert.assertEquals(secondaryPublisherClient.getSentMessageCount(), SEND_COUNT / 2L,
                "Message sending failed for second client");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), SEND_COUNT,
                "Message receiving failed.");
    }

    /**
     * 1. Create a queue consumer with selector releaseYear < '1980'.
     * 2. Create a queue consumer with selector releaseYear < '1960'
     * 3. Create a queue publisher with jms header property releaseYear = '1960'
     * Only the first consumer should get the messages
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb")
    public void performMultipleQueueReceiversWithSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
            AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configurations
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        ExchangeType.QUEUE,
                        "MultipleQueueReceiversWithSelectors");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig1.setSelectors("releaseYear < 1980");
        consumerConfig1.setAsync(false);


        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        ExchangeType.QUEUE,
                        "MultipleQueueReceiversWithSelectors");
        consumerConfig2.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig2.setSelectors("releaseYear < 1960");
        consumerConfig2.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration initialPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                        ExchangeType.QUEUE,
                        "MultipleQueueReceiversWithSelectors");
        initialPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        initialPublisherConfig.setJMSHeaderProperty("releaseYear", 1970L, JMSHeaderPropertyType
                .LONG);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient initialPublisherClient = new AndesClient(initialPublisherConfig, true);
        initialPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);

        // Evaluating
        Assert.assertEquals(initialPublisherClient.getSentMessageCount(), SEND_COUNT,
                "Message sending failed for client");
        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), EXPECTED_COUNT,
                "Message receiving failed for consumer 1.");
        Assert.assertEquals(consumerClient2
                        .getReceivedMessageCount(), 0,
                "Unexpected message count received.");
    }


    /**
     * 1. Create a topic consumer with selector releaseYear < '1980'.
     * 2. Create a topic consumer with selector releaseYear < '1960'
     * 3. Create a topic publisher with jms header property releaseYear = '1960'
     * Only the first consumer should get the messages
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb")
    public void performMultipleTopicReceiversWithSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
            AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configurations
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        ExchangeType.TOPIC,
                        "MultipleTopicReceiversWithSelectors");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig1.setSelectors("releaseYear < 1980");
        consumerConfig1.setAsync(false);


        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        ExchangeType.TOPIC,
                        "MultipleTopicReceiversWithSelectors");
        consumerConfig2.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig2.setSelectors("releaseYear < 1960");
        consumerConfig2.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration initialPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                        ExchangeType.TOPIC,
                        "MultipleTopicReceiversWithSelectors");
        initialPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        initialPublisherConfig.setJMSHeaderProperty("releaseYear", 1970L, JMSHeaderPropertyType
                .LONG);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient initialPublisherClient = new AndesClient(initialPublisherConfig, true);
        initialPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);

        // Evaluating
        Assert.assertEquals(initialPublisherClient.getSentMessageCount(), SEND_COUNT,
                "Message sending failed for client");
        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), EXPECTED_COUNT,
                "Message sending failed for consumer client 1");
        Assert.assertEquals(consumerClient2
                        .getReceivedMessageCount(), 0,
                "Unexpected message count received");
    }

    /**
     * 1. Create a durable topic consumer with selector releaseYear < '1980'.
     * 2. Create a durable topic consumer with selector releaseYear < '1960'
     * 3. Create a topic publisher with jms header property releaseYear = '1960'
     * Only the first consumer should get the messages
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb")
    public void performMultipleDurableTopicReceiversWithSelectors()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
            AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configurations
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        ExchangeType.TOPIC,
                        "MultipleDurableTopicReceiversWithSelectors");
        consumerConfig1.setDurable(true, "selectorSub1");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig1.setSelectors("releaseYear < 1980");
        consumerConfig1.setAsync(false);


        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        ExchangeType.TOPIC,
                        "MultipleDurableTopicReceiversWithSelectors");
        consumerConfig2.setDurable(true, "selectorSub2");
        consumerConfig2.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig2.setSelectors("releaseYear < 1960");
        consumerConfig2.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration initialPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                        ExchangeType.TOPIC,
                        "MultipleDurableTopicReceiversWithSelectors");
        initialPublisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        initialPublisherConfig.setJMSHeaderProperty("releaseYear", 1970L , JMSHeaderPropertyType
                .LONG);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient initialPublisherClient = new AndesClient(initialPublisherConfig, true);
        initialPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);

        // Evaluating
        Assert.assertEquals(initialPublisherClient.getSentMessageCount(), SEND_COUNT,
                "Message sending failed for client");
        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), EXPECTED_COUNT,
                "Message sending failed for consumer client 1");
        Assert.assertEquals(consumerClient2
                        .getReceivedMessageCount(), 0,
                "Unexpected message count received");
    }
}
