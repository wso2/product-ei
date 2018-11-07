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
 * Test class for durable topics. Tests include the testing of durability properties by
 * un-subscribing and resubscribing.
 */
public class DurableTopicTestCase extends MBIntegrationBaseTest {

    /**
     * Message count to send.
     */
    private static final long SEND_COUNT = 1500L;

    /**
     * Message count expected.
     */
    private static final long EXPECTED_COUNT = 500L;

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
     * 1. Start a durable topic subscription.
     * 2. Send {@link #SEND_COUNT} messages.
     * 3. After {@link #EXPECTED_COUNT} messages were received close the subscriber.
     * 4. Subscribe again. After {@link #EXPECTED_COUNT} messages were received un-subscribe.
     * 5. Subscribe again. Verify no more messages are coming.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performDurableTopicTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicTest");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig1.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        consumerConfig1.setDurable(true, "durableSubToDurableTopic1");
        consumerConfig1.setAsync(false);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicTest");
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig1, true);
        initialConsumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        //Wait until messages receive
        AndesClientUtils
                .waitForMessagesAndShutdown(initialConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Creating a second consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setUnSubscribeAfterEachMessageCount(EXPECTED_COUNT);

        // Creating clients
        AndesClient secondaryConsumerClient = new AndesClient(consumerConfig2, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(secondaryConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Creating a third JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig2.clone();

        // Creating clients
        AndesClient tertiaryConsumerClient = new AndesClient(consumerConfig3, true);
        tertiaryConsumerClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(tertiaryConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        AndesClientUtils.sleepForInterval(5000L);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(initialConsumerClient
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed for client 1.");
        Assert.assertEquals(secondaryConsumerClient
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed for client 2.");
        Assert.assertEquals(tertiaryConsumerClient
                                    .getReceivedMessageCount(), 0L, "Messages received for client 3.");
    }
}
