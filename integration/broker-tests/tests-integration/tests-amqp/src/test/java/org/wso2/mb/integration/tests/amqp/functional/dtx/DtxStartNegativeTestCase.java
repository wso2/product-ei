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

import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import javax.jms.JMSException;
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
 * Test dtx.start related error scenarios
 */
public class DtxStartNegativeTestCase extends MBIntegrationBaseTest {

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException if the test initialization fails
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Tests if joining a new XID will throw an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while starting dtx session.*")
    public void joinANonExistingDtxBranch()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartTestCaseJoinANonExistingDtxBranch";

        InitialContext initialContext = JMSClientHelper
                .createInitialContextBuilder("admin", "admin", "localhost", getAMQPPort())
                .withQueue(queueName)
                .build();

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

        xaResource.start(xid, XAResource.TMJOIN);

        // Below this line should not execute
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.prepare(xid);

        xaResource.rollback(xid);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if resuming a new XID will throw an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while starting dtx session.*")
    public void resumeANonExistingDtxBranch()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartTestCaseResumeANonExistingDtxBranch";

        InitialContext initialContext = JMSClientHelper
                .createInitialContextBuilder("admin", "admin", "localhost", getAMQPPort())
                .withQueue(queueName)
                .build();

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

        xaResource.start(xid, XAResource.TMRESUME);

        // Below this line should not execute
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.prepare(xid);

        xaResource.rollback(xid);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if resuming a new XID will throw an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while starting dtx session.*")
    public void startAnAlreadyStartedDtxBranch()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxStartTestCaseStartAnAlreadyStartedDtxBranch";

        InitialContext initialContext = JMSClientHelper
                .createInitialContextBuilder("admin", "admin", "localhost", getAMQPPort())
                .withQueue(queueName)
                .build();

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

        //
        XAConnection xaConnectionDuplicate = connectionFactory.createXAConnection();
        xaConnection.start();
        XASession xaSessionDuplicate = xaConnectionDuplicate.createXASession();

        XAResource xaResourceDuplicate = xaSessionDuplicate.getXAResource();
        Session sessionDuplicate = xaSessionDuplicate.getSession();

        MessageProducer producerDuplicate = sessionDuplicate.createProducer(xaTestQueue);

        xaResourceDuplicate.start(xid, XAResource.TMNOFLAGS);

        // Below this line should not execute
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.prepare(xid);

        xaResource.rollback(xid);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if transaction expiration throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void startDtxBranchWithShortDtxTimeout()
            throws NamingException, JMSException, XAException, XPathExpressionException, InterruptedException {
        String queueName = "DtxStartTestCaseStartDtxBranchWithShortDtxTimeout";

        InitialContext initialContext = JMSClientHelper
                .createInitialContextBuilder("admin", "admin", "localhost", getAMQPPort())
                .withQueue(queueName)
                .build();

        XAConnectionFactory connectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        XAConnection xaConnection = connectionFactory.createXAConnection();
        XASession xaSession = xaConnection.createXASession();

        XAResource xaResource = xaSession.getXAResource();
        Session session = xaSession.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        session.createQueue(queueName);
        MessageProducer producer = session.createProducer(xaTestQueue);

        Xid xid = JMSClientHelper.getNewXid();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        // Set timeout to 2 seconds
        xaResource.setTransactionTimeout(2);

        producer.send(session.createTextMessage("Test 1"));

        TimeUnit.SECONDS.sleep(3);
        int errorCode = 0;
        try {
            xaResource.end(xid, XAResource.TMSUCCESS);
        } catch (XAException e) {
            errorCode = e.errorCode;
        }

        Assert.assertEquals(errorCode, XAException.XA_RBTIMEOUT, "xaResource.end should fail with XA_RBTIMEOUT");

        errorCode = 0;
        try {
            xaResource.rollback(xid);
        } catch (XAException e) {
            errorCode = e.errorCode;
        }

        Assert.assertEquals(errorCode, XAException.XA_RBTIMEOUT, "xaResource.rollback should get with XA_RBTIMEOUT");

        session.close();
        xaConnection.close();
    }
}
