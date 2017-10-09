/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.mb.test.amqp;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import org.wso2.ei.mb.test.client.TopicReceiver;
import org.wso2.ei.mb.test.client.TopicSender;
import org.wso2.ei.mb.test.utils.ClientConstants;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Test cases for the basic functionality the Topics.
 */
public class TopicBasicsTestCase extends BrokerTest {

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static final Logger log = Logger.getLogger(TopicBasicsTestCase.class);

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    /**
     * In this test method we check sending messages to a Topic and receiving them.
     * 1. Register two subscribers to a Topic.
     * 2. Send a message.
     * 3. Receive them by two subscribers.
     * 4. Send 500 messages.
     * 5. Receive them by two subscribers.
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     */
    @Test(groups = "wso2.mb.topic")
    public void performTopicSendReceive() throws JMSException, NamingException, IOException, InterruptedException {

        TopicSender sender = null;
        TopicReceiver subscriber = null;
        TopicReceiver subscriber2 = null;
        try {
            subscriber = new TopicReceiver("TopicBasicsTestCaseSendReceive", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader, null);

            subscriber2 = new TopicReceiver("TopicBasicsTestCaseSendReceive", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader, null);

            //send a message
            sender = new TopicSender("TopicBasicsTestCaseSendReceive", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            sender.sendMessages(1, "test message");

            Assert.assertNotNull(subscriber.receiveMessage(ClientConstants.CLIENT_WAIT_DURATION),
                    "Topic not received by the subscriber");
            Assert.assertNotNull(subscriber2.receiveMessage(ClientConstants.CLIENT_WAIT_DURATION),
                    "Topic not received by the subscriber2");

            //send 500 messages
            int messageCount = 500;
            sender.sendMessages(messageCount, "test message");
            Assert.assertEquals(subscriber.getMessageCount(), messageCount,
                    "Did not receive all the topics by subscriber1");
            Assert.assertEquals(subscriber2.getMessageCount(), messageCount,
                    "Did not receive all the topics by subscriber2");
        } finally {
            if (sender != null) {
                sender.closeSender();
            }

            if (subscriber != null) {
                subscriber.closeReceiver();
            }
        }
    }

    /**
     * Test for durable subscription.
     * 1. Create two durable subscribers.
     * 2. Send 10 messages and check for their reception.
     * 3. Close one subscriber.
     * 4. Send 10 messages.
     * 5. Start the closed subscriber again.
     * 6. Check the reception of messages.
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.mb.topic")
    public void testDurableSubscription() throws JMSException, NamingException, IOException, InterruptedException {
        int messageCount = 10;
        TopicSender sender = null;
        TopicReceiver subscriber = null;
        TopicReceiver subscriber2 = null;
        try {
            subscriber = new TopicReceiver("TopicBasicsTestCaseDurable", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader, "sub1");

            subscriber2 = new TopicReceiver("TopicBasicsTestCaseDurable", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader, "sub2");

            //send 10 messages
            sender = new TopicSender("TopicBasicsTestCaseDurable", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            sender.sendMessages(messageCount, "test message");

            Assert.assertEquals(subscriber.getMessageCount(), messageCount,
                    "All the published topics not received by subscriber");
            Assert.assertEquals(subscriber2.getMessageCount(), messageCount,
                    "All the published topics not received by subscriber2");

            //disconnect subscriber2
            subscriber2.closeReceiver();

            //send another 10 messages
            sender.sendMessages(messageCount, "test message");
            subscriber2 = new TopicReceiver("TopicBasicsTestCaseDurable", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader, "sub2");
            Assert.assertEquals(subscriber.getMessageCount(), messageCount, "All the published topics not received");
            Assert.assertEquals(subscriber2.getMessageCount(), messageCount,
                    "All the published topics not received by subscriber2 after the reconnection");
        } finally {
            if (sender != null) {
                sender.closeSender();

            }

            if (subscriber != null) {
                subscriber.closeReceiver();
            }

            if (subscriber2 != null) {
                subscriber2.closeReceiver();
            }
        }
    }

    /**
     * Stop server instance.
     */
    @AfterClass
    public void clean() throws IOException {
        super.cleanup();
    }

}
