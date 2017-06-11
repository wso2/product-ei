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
import org.wso2.ei.mb.test.utils.ServerManager;

import java.io.File;
import java.io.IOException;
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

                // Start Enterprise Integrator broker instance
                serverManager.startServer(tempCarbonHome);
            } catch (IOException e) {
                log.error("IO exception", e);
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
     */
    @Test
    public void performSingleQueueSendReceiveTestCase() throws JMSException, NamingException, InterruptedException {

        int sendMessageCount = 5;

        // Start JMS queue subscriber.
        QueueReceiver queueReceiver = new QueueReceiver();
        queueReceiver.registerSubscriber();

        Thread.sleep(1000L);

        // Start JMS queue publisher.
        QueueSender queueSender = new QueueSender();
        queueSender.sendMessages(sendMessageCount);

        Thread.sleep(1000L);

        int a = queueReceiver.receivedMessageCount();

        // close queue sender.
        queueSender.closeSender();

        // close queue receiver.
        queueReceiver.closeReceiver();

        Assert.assertEquals(a, sendMessageCount, "assertion failed : " + a);
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
