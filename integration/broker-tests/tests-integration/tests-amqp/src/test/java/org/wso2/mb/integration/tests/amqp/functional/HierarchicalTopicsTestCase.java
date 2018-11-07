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
 * Tests topic subscriptions with Topic and Children(#) and Immediate Children(*).
 */
public class HierarchicalTopicsTestCase extends MBIntegrationBaseTest {

    /**
     * Message count to send
     */
    private static final long EXPECTED_COUNT = 1000L;

    /**
     * Message count expected
     */
    private static final long SEND_COUNT = EXPECTED_COUNT;

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * Un-matching hierarchical topic without wildcards should not receive message.
     * 1. Create a first subscription under "games.cricket".
     * 2. Publish messages to "games".
     * 3. No messages should be received for the first subscription.
     * 4. Close first subscriber.
     * 5. Create a second subscription under "games.cricket".
     * 6. Publish messages to "games.cricket".
     * 7. Messages should receive for second subscriber.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performHierarchicalTopicsTopicOnlyTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        /**
         * topic only option. Here we subscribe to games.cricket and verify that only messages
         * specifically published to games.cricket is received
         */
        //we should not get any message here
        AndesClient receivingClient1 = getConsumerClientForTopic("games.cricket");
        receivingClient1.startClient();

        AndesClient sendingClient1 = getPublishingClientForTopic("games");
        sendingClient1.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(receivingClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        //now we send messages specific to games.cricket topic. We should receive messages here
        AndesClientUtils.sleepForInterval(1000);

        AndesClient receivingClient2 = getConsumerClientForTopic("games.cricket");
        receivingClient2.startClient();

        AndesClient sendingClient2 = getPublishingClientForTopic("games.cricket");
        sendingClient2.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(receivingClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating publishers
        Assert.assertEquals(sendingClient1
                                    .getSentMessageCount(), SEND_COUNT, "Publisher client1 failed to publish messages");
        Assert.assertEquals(sendingClient2
                                    .getSentMessageCount(), SEND_COUNT, "Publisher client2 failed to publish messages");

        // Evaluating consumers
        Assert.assertEquals(receivingClient1
                                    .getReceivedMessageCount(), 0, "Messages received when subscriber should not receive messages.");
        Assert.assertEquals(receivingClient2
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Did not receive messages for games.cricket.");
    }

    /**
     * Immediate children option. Here you subscribe to the first level of sub-topics but not to the topic itself.
     * 1. Create a first subscription under "games.*".
     * 2. Publish messages to "games".
     * 3. No messages should be received for the first subscription.
     * 4. Close first subscription.
     * 5. Create a second subscription under "games.*".
     * 6. Publish messages to "games.football".
     * 7. Messages should receive for second subscriber.
     * 8. Close second subscription.
     * 9. Create a third subscription under "games.*".
     * 10. Publish messages to "games.cricket.sl".
     * 11. No messages should be received for the third subscription.
     * 12. Create a forth subscription under "*.cricket.sl".
     * 13. Publish messages to "games.cricket.sl".
     * 14. Messages should be received by forth subscriber.
     * 15. No messages should be received for the first subscription.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performHierarchicalTopicsImmediateChildrenTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating clients
        AndesClient consumerClient1 = getConsumerClientForTopic("games.*");
        consumerClient1.startClient();

        AndesClient publisherClient1 = getPublishingClientForTopic("games");
        publisherClient1.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        AndesClientUtils.sleepForInterval(1000);

        // Creating clients
        AndesClient consumerClient2 = getConsumerClientForTopic("games.*");
        consumerClient2.startClient();

        AndesClient publisherClient2 = getPublishingClientForTopic("games.football");
        publisherClient2.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        AndesClientUtils.sleepForInterval(1000);

        // Creating clients
        AndesClient consumerClient3 = getConsumerClientForTopic("games.*");
        consumerClient3.startClient();

        AndesClient publisherClient3 = getPublishingClientForTopic("games.cricket.sl");
        publisherClient3.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient3, AndesClientConstants.DEFAULT_RUN_TIME);

        AndesClientUtils.sleepForInterval(1000);

        AndesClient consumerClient4 = getConsumerClientForTopic("*.cricket.sl");
        consumerClient4.startClient();

        AndesClient publisherClient4 = getPublishingClientForTopic("games.cricket.sl");
        publisherClient4.startClient();


        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient4, AndesClientConstants.DEFAULT_RUN_TIME);


        // Evaluating publishers
        Assert.assertEquals(publisherClient1.getSentMessageCount(), SEND_COUNT,
                "Publisher publisherClient1 failed to publish messages.");
        Assert.assertEquals(publisherClient2.getSentMessageCount(), SEND_COUNT,
                "Publisher publisherClient2 failed to publish messages.");
        Assert.assertEquals(publisherClient3.getSentMessageCount(), SEND_COUNT,
                "Publisher publisherClient3 failed to publish messages.");
        Assert.assertEquals(publisherClient4.getSentMessageCount(), SEND_COUNT,
                "Publisher publisherClient4 failed to publish messages.");


        // Evaluating consumers
        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), 0,
                "Messages received when subscriber consumerClient1 should not receive messages.");

        Assert.assertEquals(consumerClient2.getReceivedMessageCount(), EXPECTED_COUNT,
                "Did not receive messages for consumerClient2.");

        Assert.assertEquals(consumerClient3.getReceivedMessageCount(), 0,
                "Messages received when subscriber consumerClient3 should not receive messages.");

        Assert.assertEquals(consumerClient4.getReceivedMessageCount(), EXPECTED_COUNT,
                "Did not receive message count for consumerClient4.");



    }

    /**
     * Topic and children option. Here messages published to topic itself and any level
     * in the hierarchy should be received
     * 1. Create a first subscription under "games.#".
     * 2. Publish messages to "games".
     * 3. Messages should receive for first subscriber.
     * 4. Close first subscription.
     * 5. Create a second subscription under "games.#".
     * 6. Publish messages to "games.football.sl".
     * 7. Messages should receive for second subscriber.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performHierarchicalTopicsChildrenTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        //we should  get any message here
        AndesClient consumerClient6 = getConsumerClientForTopic("games.#");
        consumerClient6.startClient();

        AndesClient publisherClient6 = getPublishingClientForTopic("games");
        publisherClient6.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient6, AndesClientConstants.DEFAULT_RUN_TIME);

        //now we send messages to level 2 child. We should receive messages here
        AndesClientUtils.sleepForInterval(1000);

        AndesClient consumerClient7 = getConsumerClientForTopic("games.#");
        consumerClient7.startClient();

        AndesClient publisherClient7 = getPublishingClientForTopic("games.football.sl");
        publisherClient7.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient7, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating publishers
        Assert.assertEquals(publisherClient6
                                    .getSentMessageCount(), SEND_COUNT, "Publisher publisherClient6 failed to publish messages.");
        Assert.assertEquals(publisherClient7
                                    .getSentMessageCount(), SEND_COUNT, "Publisher publisherClient7 failed to publish messages.");

        // Evaluating consumers
        Assert.assertEquals(consumerClient6
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Did not receive messages for consumerClient6.");
        Assert.assertEquals(consumerClient7
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Did not receive messages for consumerClient7.");
    }

    /**
     * Creates an andes consumer client for a given topic name
     *
     * @param topicName Topic name
     * @return The andes client.
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    private AndesClient getConsumerClientForTopic(String topicName)
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        consumerConfig.setAsync(false);

        return new AndesClient(consumerConfig, true);
    }

    /**
     * Create an andes publisher client for a given topic name
     *
     * @param topicName Topic name
     * @return The andes client.
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    private AndesClient getPublishingClientForTopic(String topicName)
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        // Creating a JMS publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        return new AndesClient(publisherConfig, true);
    }
}
