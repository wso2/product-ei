/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
 * This class includes test cases to test auto acknowledgements modes for queues
 */
public class AutoAcknowledgementsTestCase extends MBIntegrationBaseTest {

    /**
     * The amount of messages to be sent.
     */
    private static final long SEND_COUNT = 1500L;

    /**
     * The amount of messages to be expected.
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * Prepare environment for tests
     *
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * In this method we just test a sender and receiver with acknowledgements.
     * 1. Start a queue receiver in auto acknowledge mode.
     * 2. Publisher sends {@link #SEND_COUNT} amount of messages.
     * 3. Receiver receives {@link #EXPECTED_COUNT}
     * 4. Check whether all messages received.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"}, description = "Single queue send-receive test case with auto Ack")
    public void autoAcknowledgementsTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "autoAckTestQueue");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        consumerConfig.setAsync(false);

        // Creating a JMS publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(consumerConfig);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating clients
        AndesClient receivingClient = new AndesClient(consumerConfig, true);
        receivingClient.startClient();

        AndesClient sendingClient = new AndesClient(publisherConfig, true);
        sendingClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(receivingClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating results
        Assert.assertEquals(sendingClient.getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(receivingClient.getReceivedMessageCount(), EXPECTED_COUNT, "Total number of sent and received messages are not equal");
    }

    /**
     * In this method we drop receiving client and connect it again and tries to get messages from MB.
     * 1. Start a queue receiver in auto acknowledge mode.
     * 2. Publishers sends {@link #SEND_COUNT} number of messages.
     * 3. First receiver will read up to first 1000 messages.
     * 4. Close up the receiver.
     * 5. Start a second queue receiver in auto acknowledge mode.
     * 6. Second receiver will read up 500 messages.
     * 7. Check whether total received messages were equal to {@link #EXPECTED_COUNT}.
     *
     * @throws AndesClientConfigurationException
     * @throws CloneNotSupportedException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"}, description = "Single queue send-receive test case with dropping the receiving client")
    public void autoAcknowledgementsDropReceiverTestCase()
            throws AndesClientConfigurationException, CloneNotSupportedException, JMSException,
                   NamingException,
                   IOException, AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration initialConsumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "autoAckTestQueueDropReceiver");
        initialConsumerConfig.setMaximumMessagesToReceived(1000L);
        initialConsumerConfig.setPrintsPerMessageCount(1000L / 10L);
        initialConsumerConfig.setAsync(false);

        // Creating a JMS publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "autoAckTestQueueDropReceiver");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating clients
        AndesClient initialReceivingClient = new AndesClient(initialConsumerConfig, true);
        initialReceivingClient.startClient();

        AndesClient sendingClient = new AndesClient(publisherConfig, true);
        sendingClient.startClient();

        // Wait until messages are received by first consumer client.
        AndesClientUtils.waitForMessagesAndShutdown(initialReceivingClient, AndesClientConstants.DEFAULT_RUN_TIME);
        long totalMessagesReceived = initialReceivingClient.getReceivedMessageCount();

        log.info("Messages received by first client : " + totalMessagesReceived);

        // Creating a secondary JMS publisher client configuration
        AndesJMSConsumerClientConfiguration consumerConfigForClientAfterDrop = new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "autoAckTestQueueDropReceiver");
        consumerConfigForClientAfterDrop.setMaximumMessagesToReceived(EXPECTED_COUNT - 1000L);
        consumerConfigForClientAfterDrop.setAsync(false);

        // Creating clients
        AndesClient secondaryReceivingClient = new AndesClient(consumerConfigForClientAfterDrop, true);
        secondaryReceivingClient.startClient();

        // Wait until messages are received by second consumer client.
        AndesClientUtils.waitForMessagesAndShutdown(secondaryReceivingClient, AndesClientConstants.DEFAULT_RUN_TIME);

        totalMessagesReceived = totalMessagesReceived + secondaryReceivingClient.getReceivedMessageCount();

        // Evaluating
        Assert.assertEquals(sendingClient.getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT, "Total number of received messages should be equal to total number of sent messages");
    }
}
