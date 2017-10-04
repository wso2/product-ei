/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.mb.test.amqp;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import org.wso2.ei.mb.test.client.QueueReceiver;
import org.wso2.ei.mb.test.client.QueueSender;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;
import org.wso2.ei.mb.test.utils.QueueSignalHandler;
import sun.misc.Signal;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * This class includes test cases to test auto acknowledgements modes for queues.
 */
public class AcknowledgementsTestCase extends BrokerTest {

    /**
     * The amount of messages to be sent.
     */
    private static final int SEND_COUNT = 1000;

    /**
     * The amount of messages to be expected.
     */
    private static final int EXPECTED_COUNT = SEND_COUNT;

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(AcknowledgementsTestCase.class);

    /**
     * Delay for multiple message test cases
     */
    private static final Long PUBLISHER_DELAY = 50L;

    /**
     * Initialise test environment.
     */
    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    /**
     * In this method we just test a sender and receiver with acknowledgements.
     * 1. Start a queue receiver in auto acknowledge mode.
     * 2. Publisher sends {@link #SEND_COUNT} amount of messages.
     * 3. Receiver receives {@link #EXPECTED_COUNT}
     * 4. Check whether all messages received.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.mb")
    public void autoAcknowledgementsTestCase() throws JMSException, NamingException, IOException, InterruptedException {

        QueueReceiver queueReceiver = null;
        QueueSender queueSender = null;

        try {
            // Creating a JMS consumer
            queueReceiver = new QueueReceiver("autoAckTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            queueReceiver.registerSubscriber();

            // Creating a JMS publisher
            queueSender = new QueueSender("autoAckTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader);
            queueSender.sendMessages(SEND_COUNT, "text message");

            TimeUnit.SECONDS.sleep(PUBLISHER_DELAY);

            int receivedMessageCount = queueReceiver.receivedMessageCount();

            // Evaluating results
            Assert.assertEquals(receivedMessageCount, EXPECTED_COUNT,
                    "Total number of sent and received messages" + " are not equal");

        } finally {
            // close queue sender.
            if (queueSender != null) {
                queueSender.closeSender();
            }

            // close queue receiver.
            if (queueReceiver != null) {
                queueReceiver.closeReceiver();
            }
        }

    }

    /**
     * In this method we drop receiving client and connect it again and tries to get messages from MB.
     * 1. Start a queue receiver in auto acknowledge mode.
     * 2. Publishers sends {@link #SEND_COUNT} number of messages.
     * 3. First receiver will read up to first 500 messages.
     * 4. Close up the receiver.
     * 5. Start a second queue receiver in auto acknowledge mode.
     * 6. Second receiver will read up 500 messages.
     * 7. Check whether total received messages were equal to {@link #EXPECTED_COUNT}.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.mb")
    public void autoAcknowledgementsDropReceiverTestCase()
            throws JMSException, NamingException, IOException, InterruptedException {

        QueueReceiver queueReceiver = null;
        QueueReceiver queueReceiverTwo = null;
        QueueSender queueSender = null;

        try {

            // Creating a initial JMS consumer with autoack mode
            queueReceiver = new QueueReceiver("autoAckDropReceiverTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            queueReceiver.setMaximumMessageCount(500);
            queueReceiver.registerSubscriber();

            // Creating a JMS publisher
            queueSender = new QueueSender("autoAckDropReceiverTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            queueSender.sendMessages(SEND_COUNT, "text message");

            // Capture the maximum amount reached signal
            Signal.handle(new Signal("HUP"), new QueueSignalHandler(queueReceiver));

            // Creating a secondary JMS consumer client configuration
            queueReceiverTwo = new QueueReceiver("autoAckDropReceiverTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            queueReceiverTwo.registerSubscriber();

            TimeUnit.SECONDS.sleep(PUBLISHER_DELAY);

            // Get total received messages count
            int totalMessagesReceived = queueReceiver.receivedMessageCount() + queueReceiverTwo.receivedMessageCount();

            // Evaluating
            Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT,
                    "Total number of received messages should be" + " equal to total number of sent messages");

        } finally {
            // close queue sender.
            if (queueSender != null) {
                queueSender.closeSender();
            }

            // close queue receiver.
            if (queueReceiverTwo != null) {
                queueReceiverTwo.closeReceiver();
            }
        }

    }

    /**
     * In this test it will check functionality of client acknowledgement by acknowledging bunch by
     * bunch.
     * 1. Start queue receiver in client acknowledge mode.
     * 2. Publisher sends {@link #SEND_COUNT} messages.
     * 3. Consumer receives messages and only acknowledge after each 10 messages.
     * 4. Consumer should receive {@link #EXPECTED_COUNT} messages.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.mb")
    public void performClientAcknowledgementsTestCase()
            throws JMSException, NamingException, IOException, InterruptedException {

        QueueReceiver queueReceiver = null;
        QueueReceiver queueReceiverTwo = null;
        QueueSender queueSender = null;

        try {
            // Creating a initial JMS consumer client configuration
            queueReceiver = new QueueReceiver("clientAckTestQueue", JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE,
                    configurationReader);
            queueReceiver.registerSubscriber();

            // Creating a JMS publisher client configuration
            queueSender = new QueueSender("clientAckTestQueue", JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE,
                    configurationReader);
            queueSender.sendMessages(SEND_COUNT, "text message");

            TimeUnit.SECONDS.sleep(PUBLISHER_DELAY);

            // Creating a second JMS consumer with client ack mode
            queueReceiverTwo = new QueueReceiver("clientAckTestQueue", JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE,
                    configurationReader);
            queueReceiverTwo.registerSubscriber();

            TimeUnit.SECONDS.sleep(PUBLISHER_DELAY);

            long totalMessagesReceived = queueReceiver.receivedMessageCount() + queueReceiverTwo.receivedMessageCount();

            Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT, "Expected message count not received.");

        } finally {

            if (queueReceiver != null) {
                queueReceiver.closeReceiver();
            }

            if (queueReceiverTwo != null) {
                queueReceiverTwo.closeReceiver();
            }

            if (queueSender != null) {
                queueSender.closeSender();
            }
        }

    }

    /**
     * In this method we just test a sender and receiver with acknowledgements.
     * 1. Create consumer client with duplicate acknowledge mode
     * 2. Publisher sends {@link #SEND_COUNT} messages.
     * 3. Consumer will receive {@link #EXPECTED_COUNT} or more messages.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.mb")
    public void duplicatesOkAcknowledgementsTest()
            throws JMSException, NamingException, IOException, InterruptedException {

        QueueReceiver queueReceiver = null;
        QueueSender queueSender = null;

        try {
            // Creating a initial JMS consumer client configuration
            queueReceiver = new QueueReceiver("dupOkAckTestQueue", JMSAcknowledgeMode.DUPS_OK_ACKNOWLEDGE,
                    configurationReader);
            queueReceiver.registerSubscriber();

            // Creating a JMS publisher client configuration
            queueSender = new QueueSender("dupOkAckTestQueue", JMSAcknowledgeMode.DUPS_OK_ACKNOWLEDGE,
                    configurationReader);
            queueSender.sendMessages(SEND_COUNT / 10, "text message");

            TimeUnit.SECONDS.sleep(PUBLISHER_DELAY / 10);

            long totalMessagesReceived = queueReceiver.receivedMessageCount();

            // Evaluating
            Assert.assertTrue(totalMessagesReceived >= EXPECTED_COUNT / 10,
                    "The number of received messages " + "(" + totalMessagesReceived
                            + ") should be equal or more than the amount sent");

        } finally {
            // close queue sender.
            if (queueSender != null) {
                queueSender.closeSender();
            }

            // close queue receiver.
            if (queueReceiver != null) {
                queueReceiver.closeReceiver();
            }
        }
    }

    /**
     * Clean up after test case.
     */
    @AfterClass
    public void clean() throws IOException {
        super.cleanup();
    }
}


