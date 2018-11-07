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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.operations.utils.JMSClientHelper;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertNull;

/**
 * This tests if a tenant user can create queues, send and receive messages.
 */
public class InterTenantQueueTestCase extends MBIntegrationBaseTest {
    /**
     * Initializes test case
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Consumer from testtenant1 listens to messages from "testtenant1.com/tenant1queue" destination.
     * 2. Publish messages to "testtenant1.com/www" by a tenant user from testtenant2.
     * 3. No messages should be received by the consumer.
     *
     * @throws javax.jms.JMSException
     * @throws javax.naming.NamingException
     */
    @Test(groups = "wso2.mb", description = "Inter tenant queue publish test case")
    public void performSingleQueueSendReceiveTestCase()
            throws NamingException, JMSException, XPathExpressionException {
        String queueName = "testtenant1.com/tenant1queue";
        InitialContext subscriberInitialContext = JMSClientHelper
                .getInitialContextForQueue("tenant1user1!testtenant1.com", "tenant1user1",
                                           "localhost", getAMQPPort().toString(), queueName);
        InitialContext publisherInitialContext = JMSClientHelper
                .getInitialContextForQueue("tenant2user1!testtenant2.com", "tenant2user1",
                                           "localhost", getAMQPPort().toString(), queueName);

        // Initialize subscriber
        ConnectionFactory subscriberConnectionFactory = (ConnectionFactory) subscriberInitialContext.lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection subscriberConnection = subscriberConnectionFactory.createConnection();
        subscriberConnection.start();

        Session subscriberSession = subscriberConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination subscriberDestination = (Destination) subscriberInitialContext.lookup(queueName);
        MessageConsumer consumer = subscriberSession.createConsumer(subscriberDestination);

        // Initialize publisher
        ConnectionFactory publisherConnectionFactory = (ConnectionFactory) publisherInitialContext.lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection publisherConnection = publisherConnectionFactory.createConnection();
        publisherConnection.start();

        Session publisherSession = publisherConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination publisherDestination = (Destination) publisherInitialContext.lookup(queueName);
        MessageProducer producer = publisherSession.createProducer(publisherDestination);

        producer.send(publisherSession.createTextMessage("Test"));

        // Assuming latency is less than 5 seconds
        Message message = consumer.receive(5000);

        assertNull(message, "Publisher was able to publish from a different domain");

        // Close all connections
        subscriberConnection.close();
        // publisher session will be closed by the server since it didn't had permissions
    }
}
