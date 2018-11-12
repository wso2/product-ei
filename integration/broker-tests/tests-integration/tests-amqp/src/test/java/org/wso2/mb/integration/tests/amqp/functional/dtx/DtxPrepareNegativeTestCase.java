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
import org.wso2.carbon.automation.engine.context.beans.User;
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
 * Test dtx.prepare related error scenarios
 */
public class DtxPrepareNegativeTestCase extends MBIntegrationBaseTest {

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
     * Tests if preparing a DTX branch without starting it throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while preparing dtx session.*")
    public void prepareDtxBranchWithoutStarting()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxPrepareTestCasePrepareDtxBranchWithoutStarting";

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
        // MessageProducer producer = session.createProducer(xaTestQueue);

         Xid xid = JMSClientHelper.getNewXid();

        // We are not starting the dtx branch
        // xaResource.start(xid, XAResource.TMNOFLAGS);
        // producer.send(session.createTextMessage("Test 1"));
        // xaResource.end(xid, XAResource.TMSUCCESS);

        xaResource.prepare(xid);

        xaResource.rollback(xid);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if preparing a DTX branch after setting fail flag in dtx.end throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class)
    public void prepareDtxBranchAfterEndFails()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxPrepareTestCasePrepareDtxBranchAfterEndFails";

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
        xaResource.start(xid, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMFAIL);

        xaResource.prepare(xid);

        xaResource.rollback(xid);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if preparing a DTX branch without starting it throws an exception
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class,
          expectedExceptionsMessageRegExp = ".*Error while preparing dtx session.*")
    public void prepareDtxBranchWithoutEnding()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxPrepareTestCasePrepareDtxBranchWithoutEnding";

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
        xaResource.start(xid, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage("Test 1"));
        // xaResource.end(xid, XAResource.TMFAIL);

        xaResource.prepare(xid);

        xaResource.rollback(xid);

        session.close();
        xaConnection.close();
    }

    /**
     * Tests if preparing a DTX branch with publishing permission issues throws an error
     */
    @Test(groups = { "wso2.mb", "dtx" }, expectedExceptions = XAException.class)
    public void prepareDtxBranchWithNoRoutesIssue()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxPrepareTestCasePrepareDtxBranchWithNoRoutesIssues";

        User adminUser = getSuperTenantAdminUser();
        InitialContext adminInitialContext
                = JMSClientHelper.createInitialContextBuilder(adminUser.getUserNameWithoutDomain(),
                                                              adminUser.getPassword(),
                                                              getBrokerHost(),
                                                              getAMQPPort()).withQueue(queueName).build();

        // Publish to queue and rollback
        XAConnectionFactory connectionFactory
                = (XAConnectionFactory) adminInitialContext.lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        XAConnection xaConnection = connectionFactory.createXAConnection();
        xaConnection.start();
        XASession xaSession = xaConnection.createXASession();

        XAResource xaResource = xaSession.getXAResource();
        Session session = xaSession.getSession();

        Destination testQueue = (Destination) adminInitialContext.lookup(queueName);
        MessageProducer producer = session.createProducer(testQueue);

        Xid xid = JMSClientHelper.getNewXid();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        // Test should fail at prepare stage due to no route issue
        int prepareResponseCode = xaResource.prepare(xid);

        Assert.assertNotEquals(prepareResponseCode, XAResource.XA_OK, "Prepare should fail due to no route issue");

        xaResource.commit(xid, false);

        session.close();
        xaConnection.close();
    }
}
