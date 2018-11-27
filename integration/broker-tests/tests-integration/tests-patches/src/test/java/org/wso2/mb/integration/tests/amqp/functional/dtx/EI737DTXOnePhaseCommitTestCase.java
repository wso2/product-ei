/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.functional.dtx;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.utils.JMSClientHelper;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XASession;
import javax.naming.InitialContext;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.xml.xpath.XPathExpressionException;

/**
 * Tests one-phase commit scenarios in distributed transactions.
 */
public class EI737DTXOnePhaseCommitTestCase extends MBIntegrationBaseTest {
    /**
     * Initializing test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Tests if one-phase commit in distributed transactions, is working correctly for message consumption. Steps
     * are,
     * 1. Publish a message to a queue
     * 2. Using a distributed transacted session receive the message and commit in one-phase
     * 3. Subscribe again using a normal session and verify that a message is not received
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void onePhaseCommitMessageConsumptionTest() throws Exception {
        String queueName = "DtxOnePhaseCommitMessageConsumptionTest";

        InitialContext initialContext = JMSClientHelper
                .createInitialContextBuilder("admin", "admin", "localhost", getAMQPPort()).withQueue(queueName).build();
        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);

        // Publish message to queue
        ConnectionFactory queueConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection queueConnection = queueConnectionFactory.createConnection();
        queueConnection.start();
        Session queueSession = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queueSession.createQueue(queueName);
        MessageProducer messageProducer = queueSession.createProducer(xaTestQueue);
        messageProducer.send(queueSession.createTextMessage("Test message consumption"));
        messageProducer.close();

        // Publish to queue and commit in one-phase
        XAConnectionFactory connectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);
        XAConnection xaConnection = connectionFactory.createXAConnection();
        xaConnection.start();
        XASession xaSession = xaConnection.createXASession();
        XAResource xaResource = xaSession.getXAResource();
        Session session = xaSession.getSession();
        MessageConsumer xaConsumer = session.createConsumer(xaTestQueue);

        Xid xid = JMSClientHelper.getNewXid();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        Message receivedMessage = xaConsumer.receive(10000);
        xaResource.end(xid, XAResource.TMSUCCESS);

        Assert.assertNotNull(receivedMessage, "No message received");

        xaResource.commit(xid, true);

        session.close();
        xaConnection.close();

        // subscribe and see if the message is received
        MessageConsumer messageConsumer = queueSession.createConsumer(xaTestQueue);

        // wait 5 seconds
        Message receivedMessageAfterOnephaseCommit = messageConsumer.receive(10000);
        Assert.assertNull(receivedMessageAfterOnephaseCommit,
                "Message received. One phase commit might have failed");

        queueConnection.close();
    }

    /**
     * Tests if one-phase commit in distributed transactions, is working correctly for message publishing.
     * Steps are,
     * 1. Using a distributed transaction publish a message to a  queue and commit in one-phase
     * 2. Subscribe to the published queue and see if the message is received.
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void performClientQueuePublishTestCase() throws Exception {
        String queueName = "DtxOnePhaseCommitMessagePublishingTest";

        InitialContext initialContext = JMSClientHelper
                .createInitialContextBuilder("admin", "admin", "localhost", getAMQPPort()).withQueue(queueName).build();

        // Publish to queue and commit in one-phase
        XAConnectionFactory connectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);
        XAConnection xaConnection = connectionFactory.createXAConnection();
        xaConnection.start();
        XASession xaSession = xaConnection.createXASession();

        XAResource xaResource = xaSession.getXAResource();
        Session session = xaSession.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        session.createQueue(queueName);
        MessageProducer producer = session.createProducer(xaTestQueue);

        Xid xid = JMSClientHelper.getNewXid();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage("Test Message publishing"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.commit(xid, true);

        session.close();
        xaConnection.close();

        // subscribe and see if the message is received
        ConnectionFactory queueConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection queueConnection = queueConnectionFactory.createConnection();
        queueConnection.start();
        Session queueSession = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer messageConsumer = queueSession.createConsumer(xaTestQueue);

        // wait 5 seconds
        Message receive = messageConsumer.receive(10000);
        Assert.assertNotNull(receive, "Message was not received. One-phase commit might have failed");

        queueConnection.close();
    }

}
