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
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;
import org.wso2.ei.mb.test.utils.ServerManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.naming.NamingException;


/**
 * This class includes test cases to test auto acknowledgements modes for queues
 */
public class AcknowledgementsTestCase {

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
    private static Logger log = Logger.getLogger(QueueTestCase.class);

    /**
     * Initiate new server manager instance.
     */
    private ServerManager serverManager = new ServerManager();

    /**
     * initiate configuration reader instance
     */
    private ConfigurationReader configurationReader = new ConfigurationReader();

    /**
     * create config map
     */
    private Map<String, String> clientConfigPropertiesMap;

    /**
     * Initialise test environment.
     */
    @BeforeClass
    public void init() {

        String archiveFilePath = System.getProperty("carbon.zip");

        // Create file instance for given path.
        File distributionArchive = new File(archiveFilePath);

        // Verify if given archive path is a file and not a directory before proceed.
        if (distributionArchive.exists() && !distributionArchive.isDirectory()) {
            try {
                String tempCarbonHome = serverManager.setupServerHome(archiveFilePath);

                // load client configs to map
                clientConfigPropertiesMap = configurationReader.readClientConfigProperties();

                // Start Enterprise Integrator broker instance
                serverManager.startServer(tempCarbonHome);
            } catch (IOException e) {
                log.error("IO exception occured when trying to initialize server environment", e);
            }
        }
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
    @Test
    public void autoAcknowledgementsTestCase()
            throws JMSException, NamingException, IOException, InterruptedException {

        // Creating a JMS consumer
        QueueReceiver queueReceiver = new QueueReceiver("autoAckTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        // Creating a JMS publisher
        QueueSender queueSender = new QueueSender("autoAckTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(SEND_COUNT, "text message");

        TimeUnit.SECONDS.sleep(50L);

        int receivedMessageCount = queueReceiver.receivedMessageCount();

        // close queue sender.
        queueSender.closeSender();

        // close queue receiver .
        queueReceiver.closeReceiver();

        // Evaluating results
        Assert.assertEquals(receivedMessageCount, EXPECTED_COUNT, "Total number of sent and received messages" +
                " are not equal");
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
     * @throws InterruptedException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     */
    @Test
    public void autoAcknowledgementsDropReceiverTestCase() throws InterruptedException, JMSException, NamingException,
            IOException {

        // Creating a initial JMS consumer with autoack mode
        QueueReceiver queueReceiver = new QueueReceiver("autoAckDropReceiverTestQueue",
                JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, clientConfigPropertiesMap);
        queueReceiver.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        // Creating a JMS publisher
        QueueSender queueSender = new QueueSender("autoAckDropReceiverTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(SEND_COUNT, "text message");
        int receivedMessageCountSub1 = 0;

        TimeUnit.SECONDS.sleep(50L);

        // Wait until 500 messages are received by first consumer client.
        while (true) {
            receivedMessageCountSub1 = queueReceiver.receivedMessageCount();
            if (receivedMessageCountSub1 >= (SEND_COUNT / 2)) {
                queueReceiver.closeReceiver();
                break;
            }
        }

        // Creating a secondary JMS consumer client configuration
        QueueReceiver queueReceiver2 = new QueueReceiver("autoAckDropReceiverTestQueue",
                JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, clientConfigPropertiesMap);
        queueReceiver2.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        // Get total received messages count
        int totalMessagesReceived = receivedMessageCountSub1 + queueReceiver2.receivedMessageCount();

        queueReceiver2.closeReceiver();

        queueSender.closeSender();

        // Evaluating
        Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT, "Total number of received messages should be" +
                " equal to total number of sent messages");
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
    @Test
    public void performClientAcknowledgementsTestCase()
            throws JMSException, NamingException, IOException, InterruptedException {

        // Creating a initial JMS consumer client configuration
        QueueReceiver queueReceiver = new QueueReceiver("clientAckTestQueue", JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        // Creating a JMS publisher client configuration
        QueueSender queueSender = new QueueSender("clientAckTestQueue", JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(SEND_COUNT, "text message");

        TimeUnit.SECONDS.sleep(50L);

        // Creating a second JMS consumer with client ack mode
        QueueReceiver queueReceiver2 = new QueueReceiver("clientAckTestQueue", JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver2.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        long totalMessagesReceived = queueReceiver.receivedMessageCount() + queueReceiver2
                .receivedMessageCount();

        queueReceiver.closeReceiver();

        queueReceiver2.closeReceiver();

        queueSender.closeSender();

        Assert.assertEquals(totalMessagesReceived, EXPECTED_COUNT, "Expected message count not received.");
    }

    /**
     * In this method we just test a sender and receiver with acknowledgements
     * 1. Create consumer client with duplicate acknowledge mode
     * 2. Publisher sends {@link #SEND_COUNT} messages.
     * 3. Consumer will receive {@link #EXPECTED_COUNT} or more messages.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void duplicatesOkAcknowledgementsTest()
            throws JMSException, NamingException, IOException, InterruptedException {

        // Creating a initial JMS consumer client configuration
        QueueReceiver queueReceiver = new QueueReceiver("dupOkAckTestQueue", JMSAcknowledgeMode.DUPS_OK_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver.registerSubscriber();

        TimeUnit.SECONDS.sleep(10L);

        // Creating a JMS publisher client configuration
        QueueSender queueSender = new QueueSender("dupOkAckTestQueue", JMSAcknowledgeMode.DUPS_OK_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(SEND_COUNT / 10, "text message");

        TimeUnit.SECONDS.sleep(10L);

        long totalMessagesReceived = queueReceiver.receivedMessageCount();

        queueReceiver.closeReceiver();

        queueSender.closeSender();

        // Evaluating
        Assert.assertTrue(totalMessagesReceived >= EXPECTED_COUNT / 10, "The number of received messages " +
                "(" + totalMessagesReceived + ") should be equal or more than the amount sent");
    }

    /**
     * Clean up after test case.
     */
    @AfterClass
    public void cleanup() throws IOException {

        // Stop server instance and clean up.
        serverManager.stopServer();
    }
}


