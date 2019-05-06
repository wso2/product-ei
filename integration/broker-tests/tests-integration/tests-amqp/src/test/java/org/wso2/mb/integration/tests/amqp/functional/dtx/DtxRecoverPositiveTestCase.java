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

import java.util.Arrays;
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
 * Test dtx.recover scenarios.
 * Please note that these tests shouldn't run in parallel
 */
public class DtxRecoverPositiveTestCase extends MBIntegrationBaseTest {

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException if internal error
     */
    @BeforeClass
    public void prepare() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);
        super.restartServer();
    }

    /**
     * Tests if recovering transactions return empty set when no prepared transactions are there. Steps are,
     *    1. A distributed sessions is started
     *    2. Before preparing recover and see we get an empty list
     *    3. Go to prepare stage and see if we get one item in the list
     *    4. Rollback and see if we get an empty list
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void performDtxRecoverWithPublishTestCase()
            throws NamingException, JMSException, XAException, XPathExpressionException {
        String queueName = "DtxRecoverPositiveTestCasePerformDtxRecoverWithPublishTestCase";

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

        Xid[] recoveredTransactions = xaResource.recover(XAResource.TMNOFLAGS);
        Assert.assertEquals(recoveredTransactions.length, 0, "Recovered Transaction list length should be 0 since "
                + "we don't have not started any transaction");

        Xid xid = JMSClientHelper.getNewXid();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage("Test 1"));
        xaResource.end(xid, XAResource.TMSUCCESS);

        recoveredTransactions = xaResource.recover(XAResource.TMNOFLAGS);
        Assert.assertEquals(recoveredTransactions.length, 0, "Recovered Transaction list length should be 0 since "
                + "the transaction is not prepared yet");

        int ret = xaResource.prepare(xid);
        Assert.assertEquals(ret, XAResource.XA_OK, "Dtx.prepare was not successful.");

        recoveredTransactions = xaResource.recover(XAResource.TMNOFLAGS);
        Assert.assertEquals(recoveredTransactions.length, 1, "Recovered Transaction list length should be 1 since "
                + "the transaction is in prepared yet");

        byte[] originalBranchQualifier = xid.getBranchQualifier();
        byte[] originalGlobalTransactionId = xid.getGlobalTransactionId();
        byte[] receivedBranchQualifier = recoveredTransactions[0].getBranchQualifier();
        byte[] receivedGlobalTransactionId = recoveredTransactions[0].getGlobalTransactionId();

        boolean matching = Arrays.equals(originalBranchQualifier, receivedBranchQualifier) &&
                Arrays.equals(originalGlobalTransactionId, receivedGlobalTransactionId);

        Assert.assertTrue(matching, "Received xid does not match the original xid" );

        xaResource.rollback(xid);

        recoveredTransactions = xaResource.recover(XAResource.TMNOFLAGS);
        Assert.assertEquals(recoveredTransactions.length, 0, "Recovered Transaction list length should be 0 since "
                + "the transaction is not in prepared state");

        session.close();
        xaConnection.close();
    }
}
