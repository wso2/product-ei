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
 * Test class for multiple queues running parallel for consumers and publishers.
 */
public class MultiThreadedMultipleQueueTestCase extends MBIntegrationBaseTest {
    private static final long SEND_COUNT = 30000L;
    private static final long ADDITIONAL = 30L;
    private static final long EXPECTED_COUNT = SEND_COUNT + ADDITIONAL;
    private static final int NUMBER_OF_SUBSCRIBERS = 45;
    private static final int NUMBER_OF_PUBLISHERS = 45;
    private static final String[] DESTINATIONS = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15"};
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
     * 1. Define 15 queues.
     * 2. Send 30000 messages to 15 queues (2000 to each) by 45 threads.
     * 3. Receive messages from 15 queues by 45 threads.
     * 4. Verify that all messages are received and no more messages are received.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performMultiThreadedMultipleQueueTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException {

        for (String DESTINATION : DESTINATIONS) {
            // Creating a consumer client configuration
            AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, DESTINATION);
            consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
            consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);

            // Creating consumer clients
            AndesClient newConsumer = new AndesClient(consumerConfig, NUMBER_OF_SUBSCRIBERS / DESTINATIONS.length, true);
            newConsumer.setStartDelay(100L);
            consumers.add(newConsumer);
        }

        for (String DESTINATION : DESTINATIONS) {
            // Creating a publisher client configuration
            AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, DESTINATION);
            publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
            publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

            // Creating publisher clients
            AndesClient newPublisher = new AndesClient(publisherConfig, NUMBER_OF_PUBLISHERS / DESTINATIONS.length, true);
            newPublisher.setStartDelay(100L);
            publishers.add(newPublisher);
        }

        // Starting clients
        for (AndesClient consumer : consumers) {
            consumer.startClient();
        }

        for (AndesClient publisher : publishers) {
            publisher.startClient();
        }

        for (AndesClient consumer : consumers) {
            AndesClientUtils.waitForMessagesAndShutdown(consumer, AndesClientConstants.DEFAULT_RUN_TIME * 2L);
        }

        for (AndesClient publisher : publishers) {
            Assert.assertEquals(publisher.getSentMessageCount(), SEND_COUNT * (NUMBER_OF_PUBLISHERS / DESTINATIONS.length), "Message sending failed");
        }

        // Evaluating
        long totalMessagesReceived = 0L;
        for (AndesClient consumer : consumers) {
            Assert.assertEquals(consumer.getReceivedMessageCount(), EXPECTED_COUNT * (NUMBER_OF_SUBSCRIBERS / DESTINATIONS.length), "Message receiving failed.");
            totalMessagesReceived = totalMessagesReceived + consumer.getReceivedMessageCount();
        }

        Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT - ADDITIONAL, "Message receiving failed.");
    }
}
