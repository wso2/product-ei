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
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for multiple topics running parallel for consumers and publishers.
 */
public class MultiThreadedMultipleTopicTestCase extends MBIntegrationBaseTest {
    private static final long SEND_COUNT = 30000L;
    private static final long ADDITIONAL = 30L;
    private static final long EXPECTED_COUNT = SEND_COUNT + ADDITIONAL;
    private static final int NUMBER_OF_SUBSCRIBERS = 45;
    private static final int NUMBER_OF_PUBLISHERS = 15;
    private static final String[] DESTINATIONS = {"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12", "T13", "T14", "T15"};
    private List<AndesClient> consumers = new ArrayList<AndesClient>();
    private List<AndesClient> publishers = new ArrayList<AndesClient>();

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
     * 1. Create 45 topic subscribers (there will be three for each topic) thus there will be 15
     * topics.
     * 2. Send 30000 messages , 2000 for each topic.
     * 3. Verify that all messages are received and no more messages are received.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performMultiThreadedMultipleTopicTestCase()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   AndesClientException {
        for (String DESTINATION : DESTINATIONS) {
            // Creating a consumer client configuration
            AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.TOPIC, DESTINATION);
            consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
            consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);

            // Creating consumer
            consumers.add(new AndesClient(consumerConfig, NUMBER_OF_SUBSCRIBERS / DESTINATIONS.length, true));
        }

        for (String DESTINATION : DESTINATIONS) {
            // Creating a publisher client configuration
            AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(ExchangeType.TOPIC, DESTINATION);
            publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
            publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

            // Creating publisher
            publishers.add(new AndesClient(publisherConfig, NUMBER_OF_PUBLISHERS / DESTINATIONS.length, true));
        }

        // Starting up clients
        for (AndesClient consumer : consumers) {
            consumer.startClient();
        }

        for (AndesClient publisher : publishers) {
            publisher.startClient();
        }

        for (AndesClient consumer : consumers) {
            AndesClientUtils.waitForMessagesAndShutdown(consumer, AndesClientConstants.DEFAULT_RUN_TIME * 2L);
        }

        // Evaluating
        for (AndesClient publisher : publishers) {
            Assert.assertEquals(publisher.getSentMessageCount(), SEND_COUNT * (NUMBER_OF_PUBLISHERS / DESTINATIONS.length), "Message sending failed");
        }

        long totalMessagesReceived = 0L;
        for (AndesClient consumer : consumers) {
            Assert.assertEquals(consumer.getReceivedMessageCount(), EXPECTED_COUNT * (NUMBER_OF_SUBSCRIBERS / DESTINATIONS.length), "Message receiving failed.");
            totalMessagesReceived = totalMessagesReceived + consumer.getReceivedMessageCount();
        }

        Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT - ADDITIONAL, "Message receiving failed.");

    }
}
