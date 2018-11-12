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

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;

/**
 * This class includes unit tests to verify that messages with JMS expiration are properly removed when delivering to queues.
 */
public class QueueMessageExpireTestCase extends MBIntegrationBaseTest {

    /**
     * Logger used for logging information related to the test class
     */
    private static Logger log = Logger.getLogger(QueueMessageExpireTestCase.class);

    /**
     * Initializing test case
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Subscribe to a queue.
     * 2. Send messages with and without expiry as configured
     * 3. Verify that only messages without expiry have been received and that both types of messages have been sent.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single queue send-receive test case with 50% expired messages")
    public void performSingleQueueExpirySendReceiveTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Message send count
        long sendCount = 1000L;
        // Message count sent without expiration
        long sendCountWithoutExpiration = sendCount / 2L;
        // Message count sent with expiration
        long sendCountWithExpiration = sendCount - sendCountWithoutExpiration;
        // Expiration time for messages with expiration
        long expirationTime = 1L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiration");
        consumerConfig.setMaximumMessagesToReceived(sendCountWithoutExpiration);
        consumerConfig.setPrintsPerMessageCount(sendCountWithoutExpiration / 10L);
        consumerConfig.setAsync(false);

        AndesJMSPublisherClientConfiguration publisherConfigWithoutExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiration");
        publisherConfigWithoutExpiration.setNumberOfMessagesToSend(sendCountWithoutExpiration);
        publisherConfigWithoutExpiration.setPrintsPerMessageCount(sendCountWithoutExpiration / 10L);

        AndesJMSPublisherClientConfiguration publisherConfigWithExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiration");
        publisherConfigWithExpiration.setNumberOfMessagesToSend(sendCountWithExpiration);
        publisherConfigWithExpiration.setPrintsPerMessageCount(sendCountWithExpiration / 10L);
        publisherConfigWithExpiration.setJMSMessageExpiryTime(expirationTime); // setting expiration time

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClientWithoutExpiration = new AndesClient(publisherConfigWithoutExpiration, true);
        publisherClientWithoutExpiration.startClient();

        AndesClient publisherClientWithExpiration = new AndesClient(publisherConfigWithExpiration, true);
        publisherClientWithExpiration.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClientWithoutExpiration.getSentMessageCount(), sendCountWithoutExpiration, "Message send failed for publisher without expiration.");
        Assert.assertEquals(publisherClientWithExpiration.getSentMessageCount(), sendCountWithExpiration, "Message send failed for publisher with expiration.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), sendCountWithoutExpiration, "Message receiving failed.");
    }

    /**
     * 1. Start two subscribers
     * 2. Send messages with and without expiration as configured
     * 3. Verify that the total number of messages received by both subscribers is equal to message count sent with no expiration.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "send messages to a queue which has two consumers with jms expiration")
    public void performManyQueueExpirySendReceiveTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, XPathExpressionException, InterruptedException {

        // Message send count
        long sendCount = 1000L;
        // Message count sent without expiration
        long sendCountWithoutExpiration = sendCount / 2L;
        // Message count sent with expiration
        long sendCountWithExpiration = sendCount - sendCountWithoutExpiration;
        // Message count expected by one subscriber. Considering 2 consumers.
        long expectedCountByOneSubscriber = sendCountWithoutExpiration / 2L;
        // Expiration time for messages with expiration.
        long expirationTime = 1L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration initialConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiryAndManyConsumers");
        initialConsumerConfig.setMaximumMessagesToReceived(expectedCountByOneSubscriber);
        initialConsumerConfig.setPrintsPerMessageCount(expectedCountByOneSubscriber / 10L);
        initialConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration secondaryConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiryAndManyConsumers");
        secondaryConsumerConfig.setMaximumMessagesToReceived(expectedCountByOneSubscriber);
        secondaryConsumerConfig.setPrintsPerMessageCount(expectedCountByOneSubscriber / 10L);
        secondaryConsumerConfig.setAsync(false);

        // Creating a consumer client configuration
        AndesJMSPublisherClientConfiguration publisherConfigWithoutExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiryAndManyConsumers");
        publisherConfigWithoutExpiration.setNumberOfMessagesToSend(sendCountWithoutExpiration);
        publisherConfigWithoutExpiration.setPrintsPerMessageCount(sendCountWithoutExpiration / 10L);

        AndesJMSPublisherClientConfiguration publisherConfigWithExpiration =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "queueWithExpiryAndManyConsumers");
        publisherConfigWithExpiration.setPrintsPerMessageCount(sendCountWithExpiration / 10L);
        publisherConfigWithExpiration.setNumberOfMessagesToSend(sendCountWithExpiration);
        publisherConfigWithExpiration.setJMSMessageExpiryTime(expirationTime);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(initialConsumerConfig, true);
        initialConsumerClient.startClient();

        AndesClient secondaryConsumerClient = new AndesClient(secondaryConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClient publisherClientWithoutExpiration = new AndesClient(publisherConfigWithoutExpiration, true);
        publisherClientWithoutExpiration.startClient();

        AndesClient publisherClientWithExpiration = new AndesClient(publisherConfigWithExpiration, true);
        publisherClientWithExpiration.startClient();

        long consumer1MsgCount = initialConsumerClient.getReceivedMessageCount();
        long consumer2MsgCount = secondaryConsumerClient.getReceivedMessageCount();
        long timeout = 600000L;

        long startTime = System.currentTimeMillis();

        // Wait until total number of messages received by consumers equal or exceed sent message count
        // without expiration.
        while(consumer1MsgCount + consumer2MsgCount < sendCountWithoutExpiration) {
            // wait 10 seconds to consume messages.
            TimeUnit.SECONDS.sleep(10L);

            consumer1MsgCount = initialConsumerClient.getReceivedMessageCount();
            consumer2MsgCount = secondaryConsumerClient.getReceivedMessageCount();
            long elapsedTime = System.currentTimeMillis()-startTime;

            if(elapsedTime > timeout) {
                // At this point timeout has been reached and test case will not wait for any new messages.
                // test case will be failed if it reaches timeout.
                log.error("Expected number of messages didn't receive after " + timeout + " milliseconds. " +
                          "Therefore, no longer waiting for new messages.");
                break;
            }
        }

        AndesClientUtils.shutdownClient(initialConsumerClient);
        AndesClientUtils.shutdownClient(secondaryConsumerClient);

        // Evaluating
        Assert.assertEquals(publisherClientWithoutExpiration.getSentMessageCount(), sendCountWithoutExpiration, "Message send failed for publisher without expiration.");
        Assert.assertEquals(publisherClientWithExpiration.getSentMessageCount(), sendCountWithExpiration, "Message send failed for publisher with expiration");

        Assert.assertEquals((consumer1MsgCount + consumer2MsgCount) , sendCountWithoutExpiration,
                "Message receiving failed. Expected " + sendCountWithoutExpiration + " but received "
                        + (consumer1MsgCount + consumer2MsgCount));
    }
}
