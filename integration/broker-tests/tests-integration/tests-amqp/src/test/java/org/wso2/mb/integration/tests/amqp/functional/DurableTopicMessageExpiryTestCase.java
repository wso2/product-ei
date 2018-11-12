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
 * Unit tests to ensure jms expiration works as expected with durable topics.
 */
public class DurableTopicMessageExpiryTestCase extends MBIntegrationBaseTest {

    /**
     * Total message amount published
     */
    private static final long SEND_COUNT = 1000L;

    /**
     * Message amount published without an expiration
     */
    private static final long SEND_COUNT_WITHOUT_EXPIRATION = 600L;

    /**
     * Message content published with an expiration
     */
    private static final long SEND_COUNT_WITH_EXPIRATION = SEND_COUNT - SEND_COUNT_WITHOUT_EXPIRATION;

    /**
     * Amount of message expected by a subscriber
     */
    private static final long EXPECTED_COUNT_BY_ONE_SUBSCRIBER = SEND_COUNT_WITHOUT_EXPIRATION / 2L;

    /**
     * Expiration time for message with expiration time
     */
    private static final long EXPIRATION_TIME = 5L;

    /**
     * Initializing test case
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * 1. Start durable topic subscriber
     * 2. Send 1000 messages with 400 messages having expiration
     * 3. Stop subscriber after receiving 300 messages
     * 4. Start subscriber
     * 5. Verify that the subscriber has received remaining 300 messages.
     * 6. Pass test case if and only if 600 messages in total have been received.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single durable topic send-receive test case with jms expiration")
    public void performExpiryDurableTopicTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Creating a subscriber client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicWithExpiration");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT_BY_ONE_SUBSCRIBER);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT_BY_ONE_SUBSCRIBER / 10L);
        consumerConfig.setDurable(true, "expirationSub");
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfigWithoutExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicWithExpiration");
        publisherConfigWithoutExpiration.setNumberOfMessagesToSend(SEND_COUNT_WITHOUT_EXPIRATION);
        publisherConfigWithoutExpiration.setPrintsPerMessageCount(SEND_COUNT_WITHOUT_EXPIRATION / 10L);

        AndesJMSPublisherClientConfiguration publisherConfigWithExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicWithExpiration");
        publisherConfigWithExpiration.setNumberOfMessagesToSend(SEND_COUNT_WITH_EXPIRATION);
        publisherConfigWithExpiration.setPrintsPerMessageCount(SEND_COUNT_WITH_EXPIRATION / 10L);
        publisherConfigWithExpiration.setJMSMessageExpiryTime(EXPIRATION_TIME); // Setting expiry time

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClient publisherClientWithoutExpiration = new AndesClient(publisherConfigWithoutExpiration, true);
        publisherClientWithoutExpiration.startClient();

        AndesClient publisherClientWithExpiration = new AndesClient(publisherConfigWithExpiration, true);
        publisherClientWithExpiration.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(initialConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Creating second subscriber client configuration
        AndesJMSConsumerClientConfiguration secondaryConsumerConfig = consumerConfig.clone();
        secondaryConsumerConfig.setUnSubscribeAfterEachMessageCount(EXPECTED_COUNT_BY_ONE_SUBSCRIBER);
        secondaryConsumerConfig.setMaximumMessagesToReceived(Long.MAX_VALUE);

        // Creating second subscriber
        AndesClient secondaryConsumerClient = new AndesClient(secondaryConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(secondaryConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Creating third subscriber
        AndesClient tertiaryConsumerClient = new AndesClient(consumerConfig, true);
        tertiaryConsumerClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(tertiaryConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClientWithoutExpiration.getSentMessageCount(), SEND_COUNT_WITHOUT_EXPIRATION, "Message send failed for publisher client without expiration");
        Assert.assertEquals(publisherClientWithExpiration.getSentMessageCount(), SEND_COUNT_WITH_EXPIRATION, "Message send failed for publisher client with expiration");

        Assert.assertEquals(initialConsumerClient.getReceivedMessageCount(), EXPECTED_COUNT_BY_ONE_SUBSCRIBER, "Message receiving failed for initial consumer client");
        Assert.assertEquals(secondaryConsumerClient.getReceivedMessageCount(), EXPECTED_COUNT_BY_ONE_SUBSCRIBER, "Message receiving failed for secondary consumer client");
        Assert.assertEquals(tertiaryConsumerClient.getReceivedMessageCount(), 0L, "Message receiving failed for tertiaryConsumerClient");

    }
}
