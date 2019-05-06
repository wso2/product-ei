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
 * Test case to identify whether messages are received in order and the received messages does not
 * contains duplicates.
 */
public class QueueMessageSequentialAndDuplicateTestCase extends MBIntegrationBaseTest {

    private static final long SEND_COUNT = 1000L;
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Send 1000 messages and subscribe them for a single queue (set expected count to more than 1000 so it will wait)
     * 2. Check if messages were received in order
     * 3. Check if there are any duplicates
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueueMessageSequentialAndDuplicateTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueueDuplication");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT + 10L);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        // writing received messages to a file
        consumerConfig.setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueueDuplication");
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message send failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed");
        Assert.assertTrue(consumerClient.checkIfMessagesAreInOrder(), "Messages are not in order");
        Assert.assertEquals(consumerClient.checkIfMessagesAreDuplicated().keySet().size(), 0, "Duplicate message are available.");
    }
}
