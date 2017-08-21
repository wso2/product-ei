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
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;
import test.java.org.wso2.ei.mb.test.amqp.BrokerTest;
import test.java.org.wso2.ei.mb.test.client.TopicReceiver;
import test.java.org.wso2.ei.mb.test.client.TopicSender;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Test cases for the basic functionality the Topics
 */
public class TopicBasicsTestCase extends BrokerTest {

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(TopicBasicsTestCase.class);

    @BeforeClass
    public void init() {
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
    public void performTopicSendReceive() throws JMSException, NamingException, IOException,
            InterruptedException {

        TopicSender sender = null;
        TopicReceiver subscriber = null;
        TopicReceiver subscriber2 = null;
        try {
            subscriber = new TopicReceiver("testTopic", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader,
                    null);

            subscriber2 = new TopicReceiver("testTopic", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader,
                    null);

            //send a message
            sender = new TopicSender("testTopic", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader);
            sender.sendMessages(1, "test message");

            Assert.assertNotNull(subscriber.receiveMessage(1000L), "Topic not received by the subscriber");
            Assert.assertNotNull(subscriber2.receiveMessage(1000L), "Topic not received by the 2nd subscriber");

            //send 500 messages
            subscriber.registerMessageListener();
            subscriber2.registerMessageListener();
            sender.sendMessages(500, "test message");
            TimeUnit.SECONDS.sleep(5L);
            Assert.assertEquals(subscriber.receivedMessageCount(), 500, "Did not receive all the topics");
            Assert.assertEquals(subscriber2.receivedMessageCount(), 500, "Did not receive all the topics");
        } finally {
            if (sender != null) {
                sender.closeSender();
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
        TopicSender sender = null;
        TopicReceiver subscriber = null;
        TopicReceiver subscriber2 = null;
        try {
            subscriber = new TopicReceiver("testTopic2", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader,
                    "sub1");

            subscriber2 = new TopicReceiver("testTopic2", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader,
                    "sub2");

            //register listeners
            subscriber.registerMessageListener();
            subscriber2.registerMessageListener();

            //send 10 messages
            sender = new TopicSender("testTopic2", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader);
            sender.sendMessages(10, "test message");

            TimeUnit.SECONDS.sleep(2L);
            Assert.assertEquals(subscriber.receivedMessageCount(), 10, "All the published topics not received");
            Assert.assertEquals(subscriber2.receivedMessageCount(), 10, "All the published topics not received");

            //disconnect subscriber2
            subscriber2.closeReceiver();

            //send another 10 messages
            sender.sendMessages(10, "test message");
            subscriber2 = new TopicReceiver("testTopic2", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE, configurationReader,
                    "sub2");
            subscriber2.registerMessageListener();
            TimeUnit.SECONDS.sleep(2L);
            Assert.assertEquals(subscriber.receivedMessageCount(), 20, "All the published topics not received");
            Assert.assertEquals(subscriber2.receivedMessageCount(), 10, "All the published topics not received");
        } finally {
            if (sender != null) {
                sender.closeSender();
                subscriber.closeReceiver();
                subscriber2.closeReceiver();
            }
        }
    }


    /**
     * Stop server instance
     */
    @AfterClass
    public void clean() throws IOException {
        super.cleanup();
    }

}
