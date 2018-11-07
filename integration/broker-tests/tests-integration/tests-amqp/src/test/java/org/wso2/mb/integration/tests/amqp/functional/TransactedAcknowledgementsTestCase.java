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
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * This class includes test cases to test transacted acknowledgements modes for queues
 */
public class TransactedAcknowledgementsTestCase extends MBIntegrationBaseTest {

    /**
     * Message count to send
     */
    private static final long SEND_COUNT = 10L;

    /**
     * Message count expected
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
     * 1. Start a queue receiver with transacted sessions
     * 2. Send 10 messages
     * 3. After 10 messages are received rollback session
     * 4. After 50 messages received commit the session and close subscriber
     * 5. Analyse and see if each message is duplicated five times
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single queue send-receive test case with transactions")
    public void transactedAcknowledgements()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.QUEUE, "transactedAckTestQueue");
        consumerConfig.setMaximumMessagesToReceived(100L);
        consumerConfig.setRunningDelay(100L);   // Setting a delay publishing each message
        consumerConfig.setRollbackAfterEachMessageCount(10);   // Roll back session after given message count
        consumerConfig.setCommitAfterEachMessageCount(30);     // Committing sessions after given message count
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.SESSION_TRANSACTED);  // Use session_transacted acknowledgement mode
        consumerConfig.setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES); // Write received messages to file
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.QUEUE, "transactedAckTestQueue");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Messaging sending failed");

        //If received messages less than expected number wait until received again
        //Get rollback status , check message id of next message of roll backed message equal to first message
        long duplicateCount = consumerClient.getTotalNumberOfDuplicates();
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), (EXPECTED_COUNT + duplicateCount), "Total number of received message should be equal sum of expected and duplicate message count ");
        Assert.assertTrue(consumerClient.transactedOperation(10L), "After rollback next message need to equal first message of batch");
    }
}
