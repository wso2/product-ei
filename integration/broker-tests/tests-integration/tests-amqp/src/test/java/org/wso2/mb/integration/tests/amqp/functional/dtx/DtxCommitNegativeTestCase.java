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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.utils.JMSClientHelper;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

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
 * Test dtx.commit related error scenarios
 */
public class DtxCommitNegativeTestCase extends MBIntegrationBaseTest {

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
     * Tests if committing a DTX branch without starting it throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while committing dtx session.*")
    public void commitDtxBranchWithoutStarting()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxCommitTestCaseCommitDtxBranchWithoutStarting";

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

        // We are not starting the dtx branch
        //  xaResource.start(xid, XAResource.TMNOFLAGS);
        //  producer.send(session.createTextMessage("Test 1"));
        //  xaResource.end(xid, XAResource.TMSUCCESS);

        //  xaResource.prepare(xid);

        xaResource.commit(xid, false);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if committing a DTX branch without starting it throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while committing dtx session.*")
    public void commitDtxBranchWithoutEnding()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxCommitTestCaseCommitDtxBranchWithoutEnding";

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
        producer.send(session.createTextMessage("Test 1"));
        // xaResource.end(xid, XAResource.TMSUCCESS);

        // xaResource.prepare(xid);

        xaResource.commit(xid, false);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if committing a prepared branch with onephase throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while committing dtx session.*")
    public void commitDtxBranchWithOnephaseAfterPrepare()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxCommitTestCaseCmmitDtxBranchWithOnephaseAfterPrepare";

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
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.prepare(xid);

        // one phase should be false
        xaResource.commit(xid, true);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if committing a branch without preparing throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while committing dtx session.*")
    public void commitDtxBranchWithoutPrepare()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxCommitTestCaseCommitDtxBranchWithoutEnding";

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
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        // Should prepare before commit
        // xaResource.prepare(xid);

        xaResource.commit(xid, false);

        session.close();
        xaConnection.close();
    }
}
