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
 * Test cases for basic queue related scenarios.
 */
public class QueueTestCase {

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
     * Test basic queue test scenario
     * 1. Add queue subscriber to the broker.
     * 2. Add queue publisher to the broker.
     * 3. Publish 5 messages to the queue.
     * 4. Verify if queue message received by subscriber.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void performSingleQueueSendReceiveTestCase() throws JMSException, NamingException, InterruptedException,
            IOException {

        int sendMessageCount = 5;

        // Start JMS queue subscriber.
        QueueReceiver queueReceiver = new QueueReceiver("testQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver.registerSubscriber();

        TimeUnit.SECONDS.sleep(1L);

        // Start JMS queue publisher.
        QueueSender queueSender = new QueueSender("testQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(sendMessageCount, "text message");

        TimeUnit.SECONDS.sleep(1L);

        int receivedMessageCount = queueReceiver.receivedMessageCount();

        // close queue sender.
        queueSender.closeSender();

        // close queue receiver.
        queueReceiver.closeReceiver();

        Assert.assertEquals(receivedMessageCount, sendMessageCount, "assertion failed. Expected message count : "
                + sendMessageCount + ".Received message count : " + receivedMessageCount);
    }

    /**
     * 1. Create 2 consumers for simple queue
     * 2. Publish 3000 message to queue
     * 3. Total messages received by both consumers should be 3000 messages.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void performManyConsumersTestCase() throws JMSException, NamingException, InterruptedException, IOException {
        int sendCount = 3000;
        int expectedCount = 3000;

        //create consumer 1
        QueueReceiver queueReceiver1 = new QueueReceiver("manyConsumersTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver1.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        //create consumer 2
        QueueReceiver queueReceiver2 = new QueueReceiver("manyConsumersTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver2.registerSubscriber();

        TimeUnit.SECONDS.sleep(50L);

        //create publisher
        QueueSender queueSender = new QueueSender("manyConsumersTestQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(sendCount, "text message");

        TimeUnit.SECONDS.sleep(50L);

        int receivedMessageCount = queueReceiver1.receivedMessageCount() + queueReceiver2.receivedMessageCount();

        // close queue sender.
        queueSender.closeSender();

        // close queue receiver 1.
        queueReceiver1.closeReceiver();

        // close queue receiver 2.
        queueReceiver2.closeReceiver();

        Assert.assertEquals(receivedMessageCount, expectedCount, "assertion failed in manyConsumers test case " +
                "Expected message count : "
                + expectedCount + ".Received message count : " + receivedMessageCount);
    }

    /**
     * 1. Subscribe to a queue named "CASEInsensitiveQueue".
     * 2. Publish 500 messages to 'caseINSENSITIVEQueue'.
     * 3. Consumer should receive 500 messages.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void performDifferentCasesQueueSendReceiveTestCase() throws JMSException, NamingException,
            InterruptedException, IOException {

        int sendCount = 500;
        int expectedCount = 500;

        //create consumer to CASEInsensitiveQueue
        QueueReceiver queueReceiver = new QueueReceiver("CASEInsensitiveQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueReceiver.registerSubscriber();

        TimeUnit.SECONDS.sleep(25L);

        // Create publisher to caseINSENSITIVEQueue
        QueueSender queueSender = new QueueSender("caseINSENSITIVEQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                clientConfigPropertiesMap);
        queueSender.sendMessages(sendCount, "text message");

        TimeUnit.SECONDS.sleep(25L);

        int receivedMessageCount = queueReceiver.receivedMessageCount();

        // close queue sender.
        queueSender.closeSender();

        // close queue receiver .
        queueReceiver.closeReceiver();

        // Evaluating
        Assert.assertEquals(receivedMessageCount, expectedCount, "Message receiving failed.");

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
