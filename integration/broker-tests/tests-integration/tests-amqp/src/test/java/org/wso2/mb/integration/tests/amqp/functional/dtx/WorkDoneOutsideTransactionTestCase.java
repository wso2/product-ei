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
import org.wso2.andes.client.message.JMSTextMessage;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.utils.JMSClientHelper;
import org.wso2.mb.integration.common.utils.TestXidImpl;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.Destination;
import javax.jms.JMSException;
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
 * Tests for publishing and subscribing for messages outside of a distributed transaction
 */
public class WorkDoneOutsideTransactionTestCase extends MBIntegrationBaseTest {

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
     * Publish and then consume outside a distributed transaction
     *
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws XAException
     */
    @Test(groups = { "wso2.mb", "dtx" })
    public void publishConsumeOutsideTransactionTestCase() throws XPathExpressionException, NamingException,
                                                                  JMSException, XAException {

        String queueName = "publishConsumeOutsideTransactionTestCase";
        String outsideTransactionMessage = "outside transaction";

        InitialContext initialContext =
                JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                                                            getAMQPPort()).withQueue(queueName).build();


        XAConnectionFactory connectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        XAConnection xaConnection = connectionFactory.createXAConnection();
        xaConnection.start();
        XASession xaSession = xaConnection.createXASession();

        XAResource xaResource = xaSession.getXAResource();
        Session session = xaSession.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        session.createQueue(queueName);

        MessageConsumer consumer = session.createConsumer(xaTestQueue);
        MessageProducer producer = session.createProducer(xaTestQueue);

        Xid xid = new TestXidImpl(100, new byte[] { 0x01 }, new byte[] { 0x05 });

        producer.send(session.createTextMessage(outsideTransactionMessage));

        xaResource.start(xid, XAResource.TMNOFLAGS);
        consumer.receive(30000);
        xaResource.end(xid, XAResource.TMSUCCESS);

        int status  = xaResource.prepare(xid);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");

        xaResource.rollback(xid);
        JMSTextMessage message = (JMSTextMessage) consumer.receive(30000);

        Assert.assertNotNull(message, "Message did not receive from server");
        Assert.assertEquals(message.getText(), outsideTransactionMessage, "Invalid Message received");

        session.close();
        xaConnection.close();

    }
}
