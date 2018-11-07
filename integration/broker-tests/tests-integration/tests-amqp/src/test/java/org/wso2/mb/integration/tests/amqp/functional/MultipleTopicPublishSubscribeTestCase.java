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
 * Test case for multiple publishers and subscribers for topics
 */
public class MultipleTopicPublishSubscribeTestCase extends MBIntegrationBaseTest {

    private static final long SEND_COUNT_1000 = 1000L;
    private static final long SEND_COUNT_2000 = 2000L;
    private static final long ADDITIONAL = 10L;

    // Expect little more to check if no more messages are received
    private static final long EXPECTED_COUNT_4010 = 4000L + ADDITIONAL;
    private static final long EXPECTED_COUNT_1010 = 1000L + ADDITIONAL;

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
     * 1. Use two topics t1, t2. 2 subscribers for t1 and one subscriber for t2.
     * 2. Use two publishers for t1 and one for t2.
     * 3. Check if messages were received correctly.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performMultipleTopicPublishSubscribeTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configurations
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "multipleTopic2");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT_4010);
        consumerConfig1.setPrintsPerMessageCount(EXPECTED_COUNT_4010 / 10);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "multipleTopic1");
        consumerConfig2.setMaximumMessagesToReceived(EXPECTED_COUNT_1010);
        consumerConfig2.setPrintsPerMessageCount(EXPECTED_COUNT_1010 / 10);
        consumerConfig2.setAsync(false);

        // Creating a publisher client configurations
        AndesJMSPublisherClientConfiguration publisherConfig1 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "multipleTopic2");
        publisherConfig1.setPrintsPerMessageCount(100L);
        publisherConfig1.setNumberOfMessagesToSend(SEND_COUNT_2000);

        AndesJMSPublisherClientConfiguration publisherConfig2 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "multipleTopic1");
        publisherConfig2.setPrintsPerMessageCount(100L);
        publisherConfig2.setNumberOfMessagesToSend(SEND_COUNT_1000);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, 2, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient publisherClient1 = new AndesClient(publisherConfig1, 2, true);
        publisherClient1.startClient();

        AndesClient publisherClient2 = new AndesClient(publisherConfig2, true);
        publisherClient2.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);

        // Evaluating
        Assert.assertEquals(publisherClient1.getSentMessageCount(), SEND_COUNT_2000 * 2L, "Publisher publisherClient1 failed to publish messages");
        Assert.assertEquals(publisherClient2.getSentMessageCount(), SEND_COUNT_1000, "Publisher publisherClient2 failed to publish messages");
        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), (EXPECTED_COUNT_4010 - ADDITIONAL) * 2L,
                            "Did not receive expected message count for consumerClient1.");
        Assert.assertEquals(consumerClient2.getReceivedMessageCount(), EXPECTED_COUNT_1010 - ADDITIONAL,
                            "Did not receive expected message count for consumerClient2.");
    }
}
