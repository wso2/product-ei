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
 * Test class to verify breaking the consumers from time to time to make sure that at the end all
 * messages are delivered.
 */
public class QueueSubscriptionsBreakAndReceiveTestCase extends MBIntegrationBaseTest {

    /**
     * Number of messages to send
     */
    private static final long SEND_COUNT = 1000L;

    /**
     * Number of subscribers
     */
    private static final int NUMBER_OF_SUBSCRIPTION_BREAKS = 5;

    /**
     * Number of messages expected by one subscriber/consumer
     */
    private static final long EXPECTED_COUNT_BY_EACH_SUBSCRIBER = SEND_COUNT / NUMBER_OF_SUBSCRIPTION_BREAKS;

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
     * 1. Subscribe to a single queue which will take 1/5 messages of sent and stop
     * 2. Send messages to the queue
     * 3. Close and resubscribe 5 times to the queue. Each subscriber should get 200 messages each.
     * 4. Verify message count is equal to the sent total
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueueSubscriptionsBreakAndReceiveTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "breakSubscriberQueue");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT_BY_EACH_SUBSCRIBER);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT_BY_EACH_SUBSCRIBER / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "breakSubscriberQueue");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating clients
        AndesClient firstConsumerClient = new AndesClient(consumerConfig, true);
        firstConsumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(firstConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(firstConsumerClient.getReceivedMessageCount(), EXPECTED_COUNT_BY_EACH_SUBSCRIBER, "Message receiving failed for first consumer.");

        long totalMessageCountReceived = firstConsumerClient.getReceivedMessageCount();

        // Using a loop to create consumers and receive expected messages
        for (int count = 1; count < NUMBER_OF_SUBSCRIPTION_BREAKS; count++) {
            AndesClient newConsumerClient = new AndesClient(consumerConfig, true);
            newConsumerClient.startClient();

            AndesClientUtils.waitForMessagesAndShutdown(newConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME * 2L);

            totalMessageCountReceived = totalMessageCountReceived + newConsumerClient.getReceivedMessageCount();
            AndesClientUtils.sleepForInterval(1000L);
        }

        // Evaluating received total message count
        Assert.assertEquals(totalMessageCountReceived, SEND_COUNT, "Expected message count was not received.");
    }
}
