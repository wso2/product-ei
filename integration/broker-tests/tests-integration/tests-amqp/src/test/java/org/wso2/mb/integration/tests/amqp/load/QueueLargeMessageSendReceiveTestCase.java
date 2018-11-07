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


/**
 * Send large messages (1MB and 10MB) to message broker and check if they are received
 */
package org.wso2.mb.integration.tests.amqp.load;

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
 * Load test case to check message size support for queues
 */
public class QueueLargeMessageSendReceiveTestCase extends MBIntegrationBaseTest {

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
     * Send 1000 messages of 1MB value and check 1000 messages are received by the consumer.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"}, enabled = true)
    public void performQueueOneMBSizeMessageSendReceiveTestCase()
            throws AndesClientConfigurationException, IOException, NamingException, JMSException,
                   AndesClientException {
        long sendCount = 1000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "Queue1MBSendReceive");
        consumerConfig.setMaximumMessagesToReceived(sendCount);
        consumerConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "Queue1MBSendReceive");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig
                .setReadMessagesFromFilePath(AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB);   // Setting file to be sent by publisher

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), sendCount, "Message receiving failed.");
    }

    /**
     * Send 10 messages of size 10MB and check whether consumer receives the same amount of
     * messages
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"}, enabled = true)
    public void performQueueTenMBSizeMessageSendReceiveTestCase()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException {
        long sendCount = 10L;

        // Creating a file of 10MB
        AndesClientUtils.createMockFile(AndesClientConstants.FILE_PATH_FOR_ONE_KB_SAMPLE_FILE,
                                        AndesClientConstants.FILE_PATH_FOR_CREATING_A_NEW_FILE, 10 * 1024);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "singleLargeQueue10MB");
        consumerConfig.setMaximumMessagesToReceived(sendCount);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "singleLargeQueue10MB");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig
                .setReadMessagesFromFilePath(AndesClientConstants.FILE_PATH_FOR_CREATING_A_NEW_FILE);   // Setting file to be sent by publisher

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), sendCount, "Message receiving failed.");
    }
}
