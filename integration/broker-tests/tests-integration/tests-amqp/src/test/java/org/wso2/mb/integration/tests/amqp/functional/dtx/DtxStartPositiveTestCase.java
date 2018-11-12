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
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.utils.JMSClientHelper;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import java.rmi.RemoteException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XASession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.xml.xpath.XPathExpressionException;

/**
 * Test dtx.start positive scenarios with message publishing and acking
 */
public class DtxStartPositiveTestCase extends MBIntegrationBaseTest {

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException if initialization fails
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Tests if publishing messages works correctly with session suspending and resuming.Steps are,
     * 1. Using a distributed transaction a message is published to a queue and session is suspended
     * 2. Subscribe to the published queue and see if any message is received.
     * 3. Resume the suspended session and publish another message and commit
     * 4. Subscribe to the queue and see if two messages are received
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void suspendResumeQueuePublishTestCase()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartPositiveTestCaseSuspendResumeQueuePublishTestCase";

        InitialContext initialContext = JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                getAMQPPort()).withQueue(queueName).build();

        // Publish to queue and rollback
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
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUSPEND);

        // subscribe and see if the message is received
        ConnectionFactory queueConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection queueConnection = queueConnectionFactory.createConnection();
        queueConnection.start();
        Session queueSession = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer messageConsumer = queueSession.createConsumer(xaTestQueue);

        // wait 5 seconds
        Message receive = messageConsumer.receive(5000);
        Assert.assertNull(receive, "Message received. Message was not rolled back");


        xaResource.start(xid, XAResource.TMRESUME);
        producer.send(session.createTextMessage("Test 2"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.prepare(xid);
        xaResource.commit(xid, false);

        session.close();
        xaConnection.close();

        receive = messageConsumer.receive(5000);
        Assert.assertNotNull(receive, "Message not received");

        receive = messageConsumer.receive(5000);
        Assert.assertNotNull(receive, "Message not received");

        queueConnection.close();
    }

    /**
     * Tests if message acknowledgement works correctly with session suspend and resume. Steps are,
     *    1. Publish a message to a queue
     *    2. Using a distributed transacted session receive the message and suspend the session
     *    3. Publish a message to the queue
     *    4. Resume the session again, ack and commit
     *    5. Subscribe again using a normal session and see if any message is received
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void suspendResumeMessageAckTestCase()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartPositiveTestCaseSuspendResumeMessageAckTestCase";

        InitialContext initialContext = JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                getAMQPPort()).withQueue(queueName).build();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);

        // Publish message to queue
        ConnectionFactory queueConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection queueConnection = queueConnectionFactory.createConnection();
        queueConnection.start();
        Session queueSession = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        queueSession.createQueue(queueName);
        MessageProducer messageProducer = queueSession.createProducer(xaTestQueue);

        messageProducer.send(queueSession.createTextMessage("Test message 1"));

        // Publish to queue and rollback
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
        Message receivedMessage = xaConsumer.receive(5000);
        xaResource.end(xid, XAResource.TMSUSPEND);

        Assert.assertNotNull(receivedMessage, "No message received");

        messageProducer.send(queueSession.createTextMessage("Test message 2"));

        messageProducer.close();

        xaResource.start(xid, XAResource.TMRESUME);
        receivedMessage = xaConsumer.receive(5000);
        xaResource.end(xid, XAResource.TMSUCCESS);

        Assert.assertNotNull(receivedMessage, "No message received");

        int ret = xaResource.prepare(xid);
        Assert.assertEquals(ret, XAResource.XA_OK, "Dtx.prepare was not successful.");

        xaResource.commit(xid, false);

        session.close();
        xaConnection.close();

        // subscribe and see if the message is received
        MessageConsumer messageConsumer = queueSession.createConsumer(xaTestQueue);

        // wait 5 seconds
        Message receivedMessageFromNormalConnection = messageConsumer.receive(5000);
        Assert.assertNull(receivedMessageFromNormalConnection, "Message received. Commit might have failed");

        queueConnection.close();
    }

    /**
     * Tests if publishing messages works correctly with session joining. Steps are,
     * 1. Create two distributed transaction sessions and join one session to other.
     * 2. Publish messages using two sessions.
     * 3. Subscribe to the published queue and see if any message is received.
     * 4. Commit the session
     * 5. Subscribe to the queue and see if two messages are received
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void xaStartJoinQueuePublishTestCase()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartPositiveTestCaseXaStartJoinQueuePublishTestCase";

        InitialContext initialContext = JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                getAMQPPort()).withQueue(queueName).build();

        XAConnectionFactory xaConnectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        // Create XA resource one
        XAConnection xaConnectionOne = xaConnectionFactory.createXAConnection();
        xaConnectionOne.start();
        XASession xaSessionOne = xaConnectionOne.createXASession();

        XAResource xaResourceOne = xaSessionOne.getXAResource();
        Session sessionOne = xaSessionOne.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        sessionOne.createQueue(queueName);
        MessageProducer producerOne = sessionOne.createProducer(xaTestQueue);

        // Create XA resource two
        XAConnection xaConnectionTwo = xaConnectionFactory.createXAConnection();
        xaConnectionTwo.start();
        XASession xaSessionTwo = xaConnectionTwo.createXASession();

        XAResource xaResourceTwo = xaSessionTwo.getXAResource();
        Session sessionTwo = xaSessionTwo.getSession();

        MessageProducer producerTwo = sessionTwo.createProducer(xaTestQueue);

        Xid xid = JMSClientHelper.getNewXid();

        boolean sameRM = xaResourceOne.isSameRM(xaResourceTwo);

        Assert.assertEquals(sameRM, true, "Resource one and resource two are connected to different resource "
                + "managers");

        xaResourceOne.start(xid, XAResource.TMNOFLAGS);
        xaResourceTwo.start(xid, XAResource.TMJOIN);

        producerOne.send(sessionOne.createTextMessage("Test 1"));
        producerTwo.send(sessionTwo.createTextMessage("Test 2"));

        xaResourceOne.end(xid, XAResource.TMSUCCESS);

        // subscribe and see if the message is received
        ConnectionFactory nonXaConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection nonXaQueueConnection = nonXaConnectionFactory.createConnection();
        nonXaQueueConnection.start();
        Session nonXaQueueSession = nonXaQueueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer messageConsumer = nonXaQueueSession.createConsumer(xaTestQueue);

        // wait 5 seconds
        Message receive = messageConsumer.receive(5000);
        Assert.assertNull(receive, "Message received before committing");

        xaResourceOne.prepare(xid);
        xaResourceOne.commit(xid, false);

        xaConnectionOne.close();
        xaConnectionTwo.close();

        //This is only added to find out the reason for the intermittent failure of this test method. Should be removed
        // once the issue is identified.
        try {
            // Logging in
            LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
            String sessionCookie = loginLogoutClientForAdmin.login();
            AndesAdminClient admin = new AndesAdminClient(super.backendURL, sessionCookie);

            //Check message count in queue
            org.wso2.carbon.andes.stub.admin.types.Message[] queueOneMessages
                    = admin.browseQueue(queueName, 0, 10);
            Assert.assertEquals(queueOneMessages.length, 2, "Message not published to queue " + queueName);

            //Logging out
            loginLogoutClientForAdmin.logout();

        } catch (RemoteException | AutomationUtilException | AndesAdminServiceBrokerManagerAdminException
                | LogoutAuthenticationExceptionException e) {
            e.printStackTrace();
        }

        receive = messageConsumer.receive(5000);
        Assert.assertNotNull(receive, "Message not received");

        receive = messageConsumer.receive(5000);
        Assert.assertNotNull(receive, "Message not received");

        nonXaQueueConnection.close();
    }
    /**
     * Tests if publishing messages works correctly with session joining. Steps are,
     * 1. Create two distributed transaction sessions and join one session to other.
     * 2. Publish messages using two sessions.
     * 3. Subscribe to the published queue and see if any message is received.
     * 4. Commit the session
     * 5. Subscribe to the queue and see if two messages are received
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void xaMultiSessionPublishTestCase()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartPositiveTestCaseXaMultiSessionPublishTestCase";

        InitialContext initialContext = JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                                                                                    getAMQPPort()).withQueue(queueName).build();

        XAConnectionFactory xaConnectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        // Create XA resource one
        XAConnection xaConnectionOne = xaConnectionFactory.createXAConnection();
        xaConnectionOne.start();
        XASession xaSessionOne = xaConnectionOne.createXASession();

        XAResource xaResourceOne = xaSessionOne.getXAResource();
        Session sessionOne = xaSessionOne.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        sessionOne.createQueue(queueName);
        MessageProducer producerOne = sessionOne.createProducer(xaTestQueue);

        // Create XA resource two
        XASession xaSessionTwo = xaConnectionOne.createXASession();

        XAResource xaResourceTwo = xaSessionTwo.getXAResource();
        Session sessionTwo = xaSessionTwo.getSession();

        MessageProducer producerTwo = sessionTwo.createProducer(xaTestQueue);

        Xid xid = JMSClientHelper.getNewXid();

        boolean sameRM = xaResourceOne.isSameRM(xaResourceTwo);

        Assert.assertEquals(sameRM, true, "Resource one and resource two are connected to different resource "
                + "managers");

        xaResourceOne.start(xid, XAResource.TMNOFLAGS);
        xaResourceTwo.start(xid, XAResource.TMJOIN);

        producerOne.send(sessionOne.createTextMessage("Test 1"));
        producerTwo.send(sessionTwo.createTextMessage("Test 2"));

        xaResourceOne.end(xid, XAResource.TMSUCCESS);

        // subscribe and see if the message is received
        ConnectionFactory nonXaConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection nonXaQueueConnection = nonXaConnectionFactory.createConnection();
        nonXaQueueConnection.start();
        Session nonXaQueueSession = nonXaQueueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer messageConsumer = nonXaQueueSession.createConsumer(xaTestQueue);

        // wait 5 seconds
        Message receive = messageConsumer.receive(5000);
        Assert.assertNull(receive, "Message received before committing");

        xaResourceOne.prepare(xid);
        xaResourceOne.commit(xid, false);

        xaConnectionOne.close();

        //This is only added to find out the reason for the intermittent failure of this test method. Should be removed
        // once the issue is identified.
        try {
            // Logging in
            LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
            String sessionCookie = loginLogoutClientForAdmin.login();
            AndesAdminClient admin = new AndesAdminClient(super.backendURL, sessionCookie);

            //Check message count in queue
            org.wso2.carbon.andes.stub.admin.types.Message[] queueOneMessages
                    = admin.browseQueue(queueName, 0, 10);
            Assert.assertEquals(queueOneMessages.length, 2, "Message not published to queue " + queueName);

            //Logging out
            loginLogoutClientForAdmin.logout();

        } catch (RemoteException | AutomationUtilException | AndesAdminServiceBrokerManagerAdminException
                | LogoutAuthenticationExceptionException e) {
            e.printStackTrace();
        }

        receive = messageConsumer.receive(5000);
        Assert.assertNotNull(receive, "Message not received");

        receive = messageConsumer.receive(5000);
        Assert.assertNotNull(receive, "Message not received");

        nonXaQueueConnection.close();
    }

    /**
     * Tests if acknowledging a messages works correctly with session joining. Steps are,
     * 1. Publish two messages to two queues using two non-transacted sessions
     * 2. Create two distributed transaction sessions and join one session to other.
     * 3. Receive messages and ack using two sessions.
     * 4. Commit the session
     * 5. Subscribe to the published queue and see if any message is received.
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void xaStartJoinMessageAckTestCase()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueNameOne = "DtxStartPositiveTestCaseXaStartJoinMessageAckTestCaseOne";
        String queueNameTwo = "DtxStartPositiveTestCaseXaStartJoinMessageAckTestCaseTwo";

        InitialContext initialContext = JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                getAMQPPort()).withQueue(queueNameOne).build();

        ConnectionFactory nonXaConnectionFactory = (ConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_CONNECTION_FACTORY);
        Connection nonXaQueueConnection = nonXaConnectionFactory.createConnection();
        nonXaQueueConnection.start();
        Session nonXaQueueSessionOne = nonXaQueueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination xaTestQueueOne = nonXaQueueSessionOne.createQueue(queueNameOne);
        Destination xaTestQueueTwo = nonXaQueueSessionOne.createQueue(queueNameTwo);

        MessageProducer nonXaQueueSessionProducerOne = nonXaQueueSessionOne.createProducer(xaTestQueueOne);
        MessageProducer nonXaQueueSessionProducerTwo = nonXaQueueSessionOne.createProducer(xaTestQueueTwo);

        nonXaQueueSessionProducerOne.send(nonXaQueueSessionOne.createTextMessage("Message 1"));
        nonXaQueueSessionProducerTwo.send(nonXaQueueSessionOne.createTextMessage("Message 2"));

        nonXaQueueSessionProducerOne.close();
        nonXaQueueSessionProducerTwo.close();

        XAConnectionFactory xaConnectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        // Create XA resource one
        XAConnection xaConnectionOne = xaConnectionFactory.createXAConnection();
        xaConnectionOne.start();
        XASession xaSessionOne = xaConnectionOne.createXASession();

        XAResource xaResourceOne = xaSessionOne.getXAResource();
        Session sessionOne = xaSessionOne.getSession();

        MessageConsumer xaConsumerOne = sessionOne.createConsumer(xaTestQueueOne);

        // Create XA resource two
        XAConnection xaConnectionTwo = xaConnectionFactory.createXAConnection();
        xaConnectionTwo.start();
        XASession xaSessionTwo = xaConnectionTwo.createXASession();

        XAResource xaResourceTwo = xaSessionTwo.getXAResource();
        Session sessionTwo = xaSessionTwo.getSession();

        MessageConsumer xaConsumerTwo = sessionTwo.createConsumer(xaTestQueueTwo);

        Xid xid = JMSClientHelper.getNewXid();

        boolean sameRM = xaResourceOne.isSameRM(xaResourceTwo);

        Assert.assertEquals(sameRM, true, "Resource one and resource two are connected to different resource "
                + "managers");

        xaResourceOne.start(xid, XAResource.TMNOFLAGS);
        xaResourceTwo.start(xid, XAResource.TMJOIN);

        Message receivedMessageForQueueOne = xaConsumerOne.receive(5000);
        Assert.assertNotNull(receivedMessageForQueueOne, "A message was not received for queue " + queueNameOne);
        Message receivedMessageForQueueTwo = xaConsumerTwo.receive(5000);
        Assert.assertNotNull(receivedMessageForQueueTwo, "A message was not received for queue " + queueNameTwo);

        xaResourceOne.end(xid, XAResource.TMSUCCESS);

        xaResourceOne.prepare(xid);
        xaResourceOne.commit(xid, false);

        xaConnectionOne.close();
        xaConnectionTwo.close();

        // subscribe and see if the message is received
        MessageConsumer nonXaConsumerOne = nonXaQueueSessionOne.createConsumer(xaTestQueueOne);

        Session nonXaQueueSessionTwo = nonXaQueueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer nonXaConsumerTwo = nonXaQueueSessionTwo.createConsumer(xaTestQueueTwo);

        // wait 3 seconds
        receivedMessageForQueueOne = nonXaConsumerOne.receive(3000);
        Assert.assertNull(receivedMessageForQueueOne, "Message received after committing for queue " + queueNameOne);

        receivedMessageForQueueTwo = nonXaConsumerTwo.receive(3000);
        Assert.assertNull(receivedMessageForQueueTwo, "Message received after committing for queue " + queueNameTwo);

        nonXaQueueConnection.close();
    }

}
