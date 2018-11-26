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
 * This class includes unit tests to verify that messages with JMS expiration are properly removed when delivering to
 * non-durable topics.
 */
public class TopicMessageExpiryTestCase extends MBIntegrationBaseTest {

    /**
     * Total message count sent
     */
    private static final long SEND_COUNT = 1000L;

    /**
     * Message count sent without expiration
     */
    private static final long SEND_COUNT_WITHOUT_EXPIRATION = 600L;

    /**
     * Message count sent with expiration
     */
    private static final long SEND_COUNT_WITH_EXPIRATION = SEND_COUNT - SEND_COUNT_WITHOUT_EXPIRATION;

    /**
     * Expected message count
     */
    private static final long EXPECTED_COUNT = SEND_COUNT_WITHOUT_EXPIRATION;

    /**
     * Expiration time for messages with expiration time
     */
    private static final long EXPIRATION_TIME = 5L;

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
     * 1. Subscribe to a topic.
     * 2. Send messages with and without expiry as configured
     * 3. Verify that only messages without expiry have been received and that both types of messages have been sent.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single topic send-receive test case with jms expiration")
    public void performSingleExpiryTopicSendReceiveTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "topicWithExpiry");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        consumerConfig.setAsync(false);

        AndesJMSPublisherClientConfiguration publisherConfigWithoutExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "topicWithExpiry");
        publisherConfigWithoutExpiration.setPrintsPerMessageCount(SEND_COUNT_WITHOUT_EXPIRATION / 10L);
        publisherConfigWithoutExpiration.setNumberOfMessagesToSend(SEND_COUNT_WITHOUT_EXPIRATION);

        AndesJMSPublisherClientConfiguration publisherConfigWithExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "topicWithExpiry");
        publisherConfigWithExpiration.setPrintsPerMessageCount(SEND_COUNT_WITH_EXPIRATION / 10L);
        publisherConfigWithExpiration.setNumberOfMessagesToSend(SEND_COUNT_WITH_EXPIRATION);
        publisherConfigWithExpiration.setJMSMessageExpiryTime(EXPIRATION_TIME); // Setting expiry time

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClientWithoutExpiration = new AndesClient(publisherConfigWithoutExpiration, true);
        publisherClientWithoutExpiration.startClient();

        AndesClient publisherClientWithExpiration = new AndesClient(publisherConfigWithExpiration, true);
        publisherClientWithExpiration.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClientWithoutExpiration.getSentMessageCount(), SEND_COUNT_WITHOUT_EXPIRATION, "Message send failed for publisherClientWithoutExpiration");
        Assert.assertEquals(publisherClientWithExpiration.getSentMessageCount(), SEND_COUNT_WITH_EXPIRATION, "Message send failed for publisherClientWithExpiration");

        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed for consumerClient");
    }

}
