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
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Map;

/**
 * Tests
 */
public class JMSSubscriberTransactionsSessionCommitRollbackTestCase extends MBIntegrationBaseTest {

    /**
     * Message send count.
     */
    private static final long SEND_COUNT = 10L;

    /**
     * Number of rollback iterations.
     */
    private static final int ROLLBACK_ITERATIONS = 5;

    /**
     * Total expected message count after rollback.
     */
    private static final long EXPECTED_COUNT = SEND_COUNT * ROLLBACK_ITERATIONS;

    @BeforeClass
    public void prepare() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Start a queue receiver with transacted sessions.
     * 2. Send 10 messages.
     * 3. After 10 messages are received rollback session.
     * 4. Do same 5 times. After 50 messages received commit the session and close subscriber.
     * 5. Analyse and see if each message is duplicated five times.
     *
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws CloneNotSupportedException
     */
    @Test(groups = {"wso2.mb", "queue", "transactions"})
    public void performJMSSubscriberTransactionsSessionCommitRollbackTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "transactionQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.SESSION_TRANSACTED);
        consumerConfig.setCommitAfterEachMessageCount(EXPECTED_COUNT);
        consumerConfig.setRollbackAfterEachMessageCount(SEND_COUNT);
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT/10L);
        consumerConfig.setAsync(false);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "transactionQueue");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(initialConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        AndesClientUtils.sleepForInterval(1000);

        Map<Long, Integer> duplicateMessages = initialConsumerClient.checkIfMessagesAreDuplicated();

        boolean expectedCountDelivered = false;
        // waiting till the number of deliveries is equal to {@link #ROLLBACK_ITERATIONS}.
        if (duplicateMessages != null) {
            for (Long messageIdentifier : duplicateMessages.keySet()) {
                int numberOfTimesDelivered = duplicateMessages.get(messageIdentifier);
                if (ROLLBACK_ITERATIONS == numberOfTimesDelivered) {
                    expectedCountDelivered = true;
                } else {
                    expectedCountDelivered = false;
                    break;
                }
            }
        }

        AndesClientUtils.sleepForInterval(2000);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(initialConsumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertTrue(expectedCountDelivered, "Expected message count was not delivered.");
    }
}
