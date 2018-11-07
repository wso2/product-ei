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
 * Performs test cases where there are multiple senders and receivers for queues.
 */
public class MultipleQueueSendReceiveTestCase extends MBIntegrationBaseTest {

    /**
     * Message count to send
     */
    private static final long SEND_COUNT = 2000L;

    /**
     * Expected message count to receive
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * Initializing test case
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Use two queues q1, q2. 2 subscribers for q1 and one subscriber for q2.
     * 2. Use two publishers for q1 and one for q2.
     * 3. Check if messages were received correctly.
     *
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws CloneNotSupportedException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "queue"},enabled = false)
    public void performMultipleQueueSendReceiveTestCase()
            throws AndesClientConfigurationException, AndesClientException, JMSException,
                   IOException, NamingException, CloneNotSupportedException,
                   XPathExpressionException {

        // Creating a consumer client configurations
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "multipleQueue1");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig1.setPrintsPerMessageCount(EXPECTED_COUNT/10L);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setDestinationName("multipleQueue2");
        consumerConfig2.setAsync(false);

        // Creating a publisher client configurations
        AndesJMSPublisherClientConfiguration publisherConfig1 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "multipleQueue1");
        publisherConfig1.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig1.setPrintsPerMessageCount(SEND_COUNT/10L);

        AndesJMSPublisherClientConfiguration publisherConfig2 = publisherConfig1.clone();
        publisherConfig2.setDestinationName("multipleQueue2");

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
        long sentMessageCount = publisherClient1.getSentMessageCount() + publisherClient2.getSentMessageCount();
        long receivedMessageCount = consumerClient1.getReceivedMessageCount() + consumerClient2.getReceivedMessageCount();

        Assert.assertEquals(sentMessageCount, 3 * SEND_COUNT, "Expected message count was not sent.");
        Assert.assertEquals(receivedMessageCount, 3 * EXPECTED_COUNT, "Expected message count was not received.");
    }
}
