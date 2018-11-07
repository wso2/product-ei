/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.client.JMSAMQException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.AndesJMSPublisher;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Publisher based transaction functionality is tested with this test cases
 * Basic publisher transaction functionality of enqueue a message, commit and rollback is
 * tested. In addition max batch size limit for a transaction is tested
 */
public class TransactionalPublishingTestCase extends MBIntegrationBaseTest {

    /**
     * Initializes the test case
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * test transactional commit behavior
     *
     * - Create transactional publisher and a non transactional subscriber
     * - Enqueue message
     * - Test whether subscriber receives a message (Must not receive a message)
     * - Rollback
     * - Enqueue a different message
     * - Commit
     * - Test whether the subscriber received the message (Must receive)
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws InterruptedException
     */
    @Test(groups = {"wso2.mb", "queue", "transaction" }, description = "Send message and check whether message is received, " +
            "and commit and check whether message is received")
    public void enqueueAndCheckCommitAndCheckTestCase() throws IOException, JMSException,
                                                               AndesClientException,
                                                               NamingException,
                                                               AndesClientConfigurationException,
                                                               InterruptedException,
                                                               XPathExpressionException {

        int expectedCount = 1;
        int expectedBeforeCommit = 0;
        String queueName = "Transactional-enqueueAndCheckCommitAndCheckTestCase";
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setTransactionalSession(true);
        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClient publisherClient1 = new AndesClient(publisherConfig, true);
        AndesJMSPublisher publisher = publisherClient1.getPublishers().get(0);

        try {
            Message message = publisher.getSession().createTextMessage("transactional message");
            publisher.getSender().send(message);

            TimeUnit.MILLISECONDS.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedBeforeCommit,
                    "Message received! send messages are not committed hence no message should be received");

            publisher.getSession().commit();

            AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCount,
                    "Expected message count not received after commit");
        } finally {
            publisherClient1.stopClient();
        }

    }

    /**
     * Test publisher transaction rollback functionality
     *
     * - Create a transactional publisher and a non transactional subscriber
     * - Enqueue a message
     * - Test whether subscriber receives a message (Must not receive a message)
     * - Rollback
     * - Enqueue a different message
     * - Commit
     * - Test whether the subscriber received the message (Must receive)
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws InterruptedException
     */
    @Test(groups = {"wso2.mb", "queue", "transaction" }, description = "Test for rollback functionality")
    public void enqueueAndRollbackEnqueueAndCommitTestCase()
            throws AndesClientConfigurationException,
                   IOException, JMSException, AndesClientException, NamingException,
                   InterruptedException, XPathExpressionException {

        int expectedCount = 1;
        int expectedBeforeCommit = 0;
        String transactionMessage = "transactional message";
        String queueName = "Transactional-enqueueAndRollbackEnqueueAndCommitTestCase";

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig
                .setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES); // writing received messages.
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setTransactionalSession(true);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher publisher = publisherClient.getPublishers().get(0);

        try {
            Message message = publisher.getSession().createTextMessage("rollback message");
            publisher.getSender().send(message);

            TimeUnit.MILLISECONDS.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedBeforeCommit,
                    "Message received! send messages are not committed hence no message should be received");

            publisher.getSession().rollback();

            message = publisher.getSession().createTextMessage(transactionMessage);
            publisher.getSender().send(message);
            publisher.getSession().commit();

            AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

            // Reading received message content
            String outputContent;

            BufferedReader inputFileReader =
                    new BufferedReader(new FileReader(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES));
            outputContent = inputFileReader.readLine();
            inputFileReader.close();
            // Evaluating
            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCount,
                    "Expected message count not received after commit");
            Assert.assertEquals(outputContent, transactionMessage, "Message content has been modified.");
        } finally {
            publisherClient.stopClient();
        }
    }

    /**
     * Multiple publishing is tested. Multiple clients publishing to different queues
     *
     * - Create two transactional publishers and a two non transactional subscribers for two queues
     * - Enqueue both message
     * - Test whether subscribers receives a message (Must not receive a message)
     * - Commit first publisher
     * - Test whether the subscribers received the message (Subscriber1 must receive. Subscriber2 mustn't)
     * - Commit second publisher
     * - Test whether the subscribers2 received the message (Subscriber2 must receive)
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws InterruptedException
     */
    @Test(groups = {"wso2.mb", "queue", "transaction" }, description = "Test transactions with multiple publishers")
    public void multiplePublisherEnqueueAndCheckCommitAndCheckTestCase()
            throws AndesClientConfigurationException,
                   IOException, JMSException, AndesClientException, NamingException,
                   InterruptedException, XPathExpressionException {

        int expectedCount = 1;
        int expectedBeforeCommit = 0;
        String queueName1 = "Transactional-multiplePublisherEnqueueAndCheckCommitAndCheck-1";
        String queueName2 = "Transactional-multiplePublisherEnqueueAndCheckCommitAndCheck-2";

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName1);
        consumerConfig1.setMaximumMessagesToReceived(expectedCount);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName2);
        consumerConfig2.setMaximumMessagesToReceived(expectedCount);
        consumerConfig2.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig1 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName1);
        publisherConfig1.setTransactionalSession(true);

        AndesJMSPublisherClientConfiguration publisherConfig2 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName2);
        publisherConfig2.setTransactionalSession(true);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient1.startClient();
        consumerClient2.startClient();

        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        AndesClient publisherClient2 = new AndesClient(publisherConfig2, true);
        AndesJMSPublisher publisher1 = publisherClient1.getPublishers().get(0);
        AndesJMSPublisher publisher2 = publisherClient2.getPublishers().get(0);

        try {
            Message message1 = publisher1.getSession().createTextMessage("message1");
            Message message2 = publisher2.getSession().createTextMessage("message2");

            publisher1.getSender().send(message1);
            publisher2.getSender().send(message2);

            TimeUnit.MILLISECONDS.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedBeforeCommit,
                    "Message received for " + queueName1 + " ! send messages are not committed hence " +
                            "no message should be received");

            Assert.assertEquals(consumerClient2.getReceivedMessageCount(), expectedBeforeCommit,
                    "Message received for " + queueName2 + " ! send messages are not committed hence " +
                            "no message should be received");

            publisher1.getSession().commit();

            AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

            // Test for consumer 1
            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCount,
                    "Expected message count not received after commit");

            // Test for consumer 2
            Assert.assertEquals(consumerClient2.getReceivedMessageCount(), expectedBeforeCommit,
                    "Message received for " + queueName2 + " ! send messages are not committed hence " +
                            "no message should be received");

            // commit for client two
            publisher2.getSession().commit();

            AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

            // Test for consumer 2
            Assert.assertEquals(consumerClient2.getReceivedMessageCount(), expectedCount,
                    "Expected message count not received after commit");
        } finally {
            publisherClient1.stopClient();
            publisherClient2.stopClient();
        }
    }

    /**
     * Test rollback functionality with multiple publisher scenario
     *
     * - Create a two transactional publishers and a non transactional subscriber
     * - Enqueue a message to each publisher
     * - Test whether subscriber receives messages (Must not receive a message)
     * - Rollback
     * - Enqueue another message for each publisher
     * - Commit for first publisher
     * - Test whether the subscriber received the message sent by publisher 1 (Must receive)
     * - Commit for second publisher
     * - Test whether the subscriber received the message sent by publisher 2 (Must receive)
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws InterruptedException
     */
    @Test(groups = {"wso2.mb", "queue", "transaction" },
            description = "Test rollback functionality with multiple publishers")
    public void multiplePublisherEnqueueAndRollbackEnqueueAndCommitTestCase()
            throws AndesClientConfigurationException,
                   IOException, JMSException, AndesClientException, NamingException,
                   InterruptedException, XPathExpressionException {

        int expectedCountAfterPub1Commit = 1;
        int expectedCountAfterPub2Commit = 2;
        int expectedBeforeCommit = 0;
        int waitForMessages = 2;
        String queueName = "Transactional-multiplePublisherEnqueueAndRollbackEnqueueAndCommit";
        String transactionMessage1 = "transactional message 1";
        String transactionMessage2 = "transactional message 2";


        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(waitForMessages);
        consumerConfig.
                setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES); // writing received messages.

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig1 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig1.setTransactionalSession(true);

        AndesJMSPublisherClientConfiguration publisherConfig2 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig2.setTransactionalSession(true);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        AndesClient publisherClient2 = new AndesClient(publisherConfig2, true);
        AndesJMSPublisher publisher1 = publisherClient1.getPublishers().get(0);
        AndesJMSPublisher publisher2 = publisherClient2.getPublishers().get(0);

        BufferedReader inputFileReader =
                new BufferedReader(new FileReader(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES));
        try {
            Message message1 = publisher1.getSession().createTextMessage("rollback message 1");
            Message message2 = publisher2.getSession().createTextMessage("rollback message 2");

            publisher1.getSender().send(message1);
            publisher2.getSender().send(message2);

            TimeUnit.MILLISECONDS.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedBeforeCommit,
                    "Message received for " + queueName + " ! send messages are not committed hence " +
                            "no message should be received");

            publisher1.getSession().rollback();

            message1 = publisher1.getSession().createTextMessage(transactionMessage1);
            publisher1.getSender().send(message1);
            publisher1.getSession().commit();

            TimeUnit.MILLISECONDS.sleep(AndesClientConstants.DEFAULT_RUN_TIME);
            AndesClientUtils.flushPrintWriters();
            // Reading received message content
            String outputContent;
            outputContent = inputFileReader.readLine();

            // Test whether message received
            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCountAfterPub1Commit,
                    "Expected message count not received after commit");

            Assert.assertEquals(outputContent, transactionMessage1, "Message content has been modified.");

            // Commit for client two after rollback
            publisher2.getSession().rollback();
            message2 = publisher2.getSession().createTextMessage(transactionMessage2);
            publisher2.getSender().send(message2);
            publisher2.getSession().commit();

            AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
            AndesClientUtils.flushPrintWriters();

            outputContent = inputFileReader.readLine();

            // Test for consumer 1
            Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCountAfterPub2Commit,
                    "Expected message count not received after commit");

            Assert.assertEquals(outputContent, transactionMessage2, "Message content has been modified.");
        } finally {
            inputFileReader.close();
            publisherClient1.stopClient();
            publisherClient2.stopClient();
        }

    }

    /**
     * Test for a big batch size with content. This should throw an { @link JMSAMQException }
     * We are limiting the total content stored in memory for a transaction to avoid unwanted OOM issues
     *
     * Create a producer and a subscriber for the same queue
     * enqueue 20 1MB messages
     * commit (This should throw and { @link JMSAMQException })
     *
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     */
    @Test(groups = {"wso2.mb", "queue", "transaction" },
            description = "Test the commit batch size limit check functionality",
            expectedExceptions = JMSException.class)
    public void exceedCommitBatchSizeTest() throws IOException, JMSException, AndesClientException,
                                                   NamingException,
                                                   AndesClientConfigurationException,
                                                   XPathExpressionException {

        String queueName = "Transactional-exceedCommitBatchSizeTest";
        int messageSize = 1024 * 1024;
        int messageCount = 20;
        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setTransactionalSession(true);

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(messageCount);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClient publisherClient1 = new AndesClient(publisherConfig, true);
        AndesJMSPublisher publisher1 = publisherClient1.getPublishers().get(0);

        // Reading message content
        char[] inputContent = new char[messageSize];
        BufferedReader inputFileReader =
                new BufferedReader(new FileReader(AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB));
        try {
            inputFileReader.read(inputContent);
            inputFileReader.close();

            Message message = publisher1.getSession().createTextMessage(new String(inputContent));

            for (int i = 0; i < messageCount; i++) {
                publisher1.getSender().send(message);
            }

            publisher1.getSession().commit();
        } finally {
            publisherClient1.stopClient();
            consumerClient1.stopClient();
        }
    }

    /**
     * Create transactional publisher and commit for a topic with no subscribers. MB should drop the messages but the
     * commit request should be successful.
     *
     * Test for a fix done for https://wso2.org/jira/browse/MB-1629
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue", "transaction" },description = "Test topic publisher without subscribers")
    public void topicPublishingWithoutSubsribers() throws XPathExpressionException, IOException, AndesClientException,
            NamingException, JMSException {

        String topicName = "Transactional-pubwithoutSub";
        int messageCount = 20;
        String messageStr = "Transactional-pubwithoutSub Message";
        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        publisherConfig.setTransactionalSession(true);

        AndesClient publisherClient1 = new AndesClient(publisherConfig, true);
        AndesJMSPublisher publisher1 = publisherClient1.getPublishers().get(0);

        try {

            Message message = publisher1.getSession().createTextMessage(messageStr);

            for (int i = 0; i < messageCount; i++) {
                publisher1.getSender().send(message);
            }

            publisher1.getSession().commit();

        } finally {
            publisherClient1.stopClient();
        }
    }
}
