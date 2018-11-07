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

/**
 * Load test for standalone MB with consumers having auto and client ack modes when receiving messages
 */
public class QueueAutoAckSubscriberCloseTestCase extends MBIntegrationBaseTest {

    private static final long SEND_COUNT = 100000L;
    private static final long EXPECTED_COUNT = SEND_COUNT;
    private static final int NUMBER_OF_SUBSCRIBERS = 7;
    private static final int NUMBER_OF_PUBLISHERS = 7;
    private static final long NUMBER_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS = 1000L;
    private static final int NUMBER_OF_SUBSCRIBERS_TO_CLOSE = 1;
    private static final long NUMBER_OF_MESSAGES_TO_EXPECT = EXPECTED_COUNT - NUMBER_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS;
    private static final int NUMBER_OF_NON_CLOSING_SUBSCRIBERS = NUMBER_OF_SUBSCRIBERS - NUMBER_OF_SUBSCRIBERS_TO_CLOSE;

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
     * Create 50 subscriptions for a queue and publish one million messages. Then close 10% of the subscribers while
     * messages are retrieving and check if all the messages are received by other subscribers.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "50 subscriptions for a queue and 50 publishers. Then close " +
                                            "10% of the subscribers ", enabled = true)
    public void performMillionMessageTenPercentSubscriberCloseTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException {

        // Creating a consumer client configurations
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "millionTenPercentAutoAckSubscriberCloseQueue");
        consumerConfig.setMaximumMessagesToReceived(NUMBER_OF_MESSAGES_TO_EXPECT);
        consumerConfig.setPrintsPerMessageCount(NUMBER_OF_MESSAGES_TO_EXPECT / 10L);

        AndesJMSConsumerClientConfiguration consumerClosingConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "millionTenPercentAutoAckSubscriberCloseQueue");
        consumerClosingConfig.setMaximumMessagesToReceived(NUMBER_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS);
        consumerClosingConfig.setPrintsPerMessageCount(NUMBER_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "millionTenPercentAutoAckSubscriberCloseQueue");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, NUMBER_OF_NON_CLOSING_SUBSCRIBERS, true);
        consumerClient.startClient();

        AndesClient consumerClosingClient = new AndesClient(consumerClosingConfig, NUMBER_OF_SUBSCRIBERS_TO_CLOSE, true);
        consumerClosingClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, NUMBER_OF_PUBLISHERS, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClosingClient);

        // Evaluating
        long totalReceivedMessageCount = consumerClient.getReceivedMessageCount() + consumerClosingClient.getReceivedMessageCount();

        log.info("Total Non Closing Subscribers Received Messages [" + consumerClient.getReceivedMessageCount() + "]");
        log.info("Total Closing Subscribers Received Messages [" + consumerClosingClient.getReceivedMessageCount() + "]");
        log.info("Total Received Messages [" + totalReceivedMessageCount + "]");

        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT * NUMBER_OF_PUBLISHERS, "Message sending failed");
        Assert.assertTrue(totalReceivedMessageCount >= SEND_COUNT, "Received only " + totalReceivedMessageCount + " messages.");
    }
}
