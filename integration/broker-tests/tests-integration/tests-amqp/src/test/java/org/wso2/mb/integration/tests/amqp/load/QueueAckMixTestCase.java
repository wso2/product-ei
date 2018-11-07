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
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * Load test for standalone MB with consumers having auto and client ack modes when receiving messages
 */
public class QueueAckMixTestCase extends MBIntegrationBaseTest {
    private static final long SEND_COUNT = 100000L;
    private static final long EXPECTED_COUNT = SEND_COUNT;
    private static final int NUMBER_OF_SUBSCRIBERS = 7;
    private static final int NUMBER_OF_PUBLISHERS = 7;
    private static final long NUMBER_OF_RETURNED_MESSAGES = SEND_COUNT / 10L;
    private static final int NUMBER_OF_CLIENT_ACK_SUBSCRIBERS = 1;
    private static final int NUMBER_OF_AUTO_ACK_SUBSCRIBERS = NUMBER_OF_SUBSCRIBERS - NUMBER_OF_CLIENT_ACK_SUBSCRIBERS;

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
     * Send million messages and receive them via AUTO_ACKNOWLEDGE subscribers and CLIENT_ACKNOWLEDGE subscribers and
     * check if AUTO_ACKNOWLEDGE subscribers receive all the messages.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Send million messages and Receive them via AUTO_ACKNOWLEDGE subscribers " +
                                            "and CLIENT_ACKNOWLEDGE", enabled = true)
    public void performMillionMessageTenPercentReturnTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "MillionTenPercentAckMixReturnQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE); // Consumer uses auto acknowledge mode
        consumerConfig.setAcknowledgeAfterEachMessageCount(200);    // Acknowledging messages only after message count
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);

        AndesJMSConsumerClientConfiguration consumerReturnedConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "MillionTenPercentAckMixReturnQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);   // Consumer uses client acknowledge mode
        consumerConfig.setAcknowledgeAfterEachMessageCount(100000);     // Acknowledge messages only after message count
        consumerReturnedConfig.setMaximumMessagesToReceived(NUMBER_OF_RETURNED_MESSAGES);
        consumerReturnedConfig.setPrintsPerMessageCount(NUMBER_OF_RETURNED_MESSAGES / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "MillionTenPercentAckMixReturnQueue");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, NUMBER_OF_AUTO_ACK_SUBSCRIBERS, true);
        consumerClient.startClient();

        AndesClient consumerReturnedClient = new AndesClient(consumerReturnedConfig, NUMBER_OF_CLIENT_ACK_SUBSCRIBERS, true);
        consumerReturnedClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, NUMBER_OF_PUBLISHERS, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerReturnedClient);

        // Evaluation
        long totalReceivedMessageCount = consumerClient.getReceivedMessageCount() + consumerReturnedClient.getReceivedMessageCount();

        log.info("Total Non Returning Subscribers Received Messages [" + consumerClient.getReceivedMessageCount() + "]");
        log.info("Total Returning Subscribers Received Messages [" + consumerReturnedClient.getReceivedMessageCount() + "]");
        log.info("Total Received Messages [" + totalReceivedMessageCount + "]");

        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT * NUMBER_OF_PUBLISHERS, "Message sending failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), SEND_COUNT, "Did not receive expected message count.");
    }
}
