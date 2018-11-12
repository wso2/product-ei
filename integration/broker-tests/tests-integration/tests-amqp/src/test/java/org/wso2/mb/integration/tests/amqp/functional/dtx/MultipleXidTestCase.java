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
 * Test working with multiple Xid for distributed transaction
 */
public class MultipleXidTestCase extends MBIntegrationBaseTest {

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
     * Publish two messages with the same publisher inside two separate transactions
     * <p>
     * publish messages within two separate transactions
     * commit the second transaction
     * receive the second message published
     * commit the first transaction
     * receive the first message published
     *
     * @throws XPathExpressionException Error on reading AMQP port
     * @throws NamingException          Throws when Initial context lookup failed
     * @throws JMSException             Exception related to JMS
     * @throws XAException              Throws when error occurred in XA transaction
     */
    @Test(groups = {"wso2.mb", "dtx"})
    public void PublishWithMultipleXidTestCase() throws XPathExpressionException, NamingException,
                                                        JMSException, XAException {

        String queueName = "PublishWithMultipleXidTestCase";
        String xid1Message = "PublishWithMultipleXidTestCase-Msg-1";
        String xid2Message = "PublishWithMultipleXidTestCase-Msg-2";

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

        Xid xid1 = new TestXidImpl(100, new byte[]{0x01}, new byte[]{0x05});
        Xid xid2 = new TestXidImpl(100, new byte[]{0x01}, new byte[]{0x06});

        xaResource.start(xid1, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage(xid1Message));
        xaResource.end(xid1, XAResource.TMSUCCESS);

        xaResource.start(xid2, XAResource.TMNOFLAGS);
        producer.send(session.createTextMessage(xid2Message));
        xaResource.end(xid2, XAResource.TMSUCCESS);

        // Xid 2
        int status = xaResource.prepare(xid2);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");

        xaResource.commit(xid2, false);
        JMSTextMessage message = (JMSTextMessage) consumer.receive(30000);

        Assert.assertEquals(message.getText(), xid2Message, "Invalid Message received");

        // Xid 1
        status = xaResource.prepare(xid1);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");

        xaResource.commit(xid1, false);
        JMSTextMessage message2 = (JMSTextMessage) consumer.receive(30000);

        Assert.assertEquals(message2.getText(), xid1Message, "Invalid Message received");

        session.close();
        xaConnection.close();
    }

    /**
     * Consume messages on two separate transactions
     * <p>
     * publish two messages
     * consume first message in a transaction
     * consume second message in another transaction
     * commit first transaction
     * commit second transaction
     *
     * @throws XPathExpressionException Error on reading AMQP port
     * @throws NamingException          Throws when Initial context lookup failed
     * @throws JMSException             Exception related to JMS
     * @throws XAException              Throws when error occurred in XA transaction
     */
    @Test(groups = {"wso2.mb", "dtx"})
    public void consumeRollbackWithMultipleXidTestCase() throws XPathExpressionException, NamingException,
                                                                JMSException, XAException {
        String queueName = "consumeRollbackWithMultipleXidTestCase";
        String xid1Message = "consumeRollbackWithMultipleXidTestCase-Msg-1";
        String xid2Message = "consumeRollbackWithMultipleXidTestCase-Msg-2";

        InitialContext initialContext =
                JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                                                            getAMQPPort()).withQueue(queueName).build();

        XAConnectionFactory connectionFactory =
                (XAConnectionFactory) initialContext.lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        XAConnection xaConnection = connectionFactory.createXAConnection();
        xaConnection.start();
        XASession xaSession = xaConnection.createXASession();

        XAResource xaResource = xaSession.getXAResource();
        Session session = xaSession.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        session.createQueue(queueName);

        MessageConsumer consumer = session.createConsumer(xaTestQueue);
        MessageProducer producer = session.createProducer(xaTestQueue);

        Xid xid1 = new TestXidImpl(100, new byte[]{0x01}, new byte[]{0x07});
        Xid xid2 = new TestXidImpl(100, new byte[]{0x01}, new byte[]{0x08});

        producer.send(session.createTextMessage(xid1Message));
        producer.send(session.createTextMessage(xid2Message));

        xaResource.start(xid1, XAResource.TMNOFLAGS);
        consumer.receive(30000);
        xaResource.end(xid1, XAResource.TMSUCCESS);

        xaResource.start(xid2, XAResource.TMNOFLAGS);
        JMSTextMessage message2 = (JMSTextMessage) consumer.receive(30000);
        xaResource.end(xid2, XAResource.TMSUCCESS);

        // Xid 1
        int status = xaResource.prepare(xid1);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");

        xaResource.rollback(xid1);

        // Xid 2
        status = xaResource.prepare(xid2);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");
        Assert.assertEquals(message2.getText(), xid2Message, "Invalid Message received");

        xaResource.commit(xid2, false);

        JMSTextMessage message3 = (JMSTextMessage) consumer.receive(30000);
        Assert.assertNotNull(message3, "Didn't receive the message from server");
        Assert.assertEquals(message3.getText(), xid1Message, "Invalid Message received");

        session.close();
        xaConnection.close();
    }

    /**
     * Publish with two distinct connections with separate transactions and then consume the messages
     * Messages should preserve the order in which messages were committed
     *
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws XAException
     */
    @Test(groups = {"wso2.mb", "dtx"})
    private void publishConsumeWithDistinctConnections() throws XPathExpressionException, NamingException,
                                                                JMSException, XAException {

        String queueName = "publishConsumeWithDistinctConnections";
        String xid1Message = "xid 1";
        String xid2Message = "xid 2";

        InitialContext initialContext =
                JMSClientHelper.createInitialContextBuilder("admin", "admin", "localhost",
                                                            getAMQPPort()).withQueue(queueName).build();

        XAConnectionFactory connectionFactory = (XAConnectionFactory) initialContext
                .lookup(JMSClientHelper.QUEUE_XA_CONNECTION_FACTORY);

        XAConnection xaConnection1 = connectionFactory.createXAConnection();
        XAConnection xaConnection2 = connectionFactory.createXAConnection();
        xaConnection1.start();
        xaConnection2.start();
        XASession xaSession1 = xaConnection1.createXASession();
        XASession xaSession2 = xaConnection2.createXASession();

        XAResource xaResource1 = xaSession1.getXAResource();
        XAResource xaResource2 = xaSession2.getXAResource();
        Session session1 = xaSession1.getSession();
        Session session2 = xaSession2.getSession();

        Destination xaTestQueue = (Destination) initialContext.lookup(queueName);
        session1.createQueue(queueName);

        MessageConsumer consumer = session1.createConsumer(xaTestQueue);

        MessageProducer producer = session1.createProducer(xaTestQueue);
        MessageProducer producer2 = session2.createProducer(xaTestQueue);

        Xid xid1 = new TestXidImpl(100, new byte[]{0x01}, new byte[]{0x09});
        Xid xid2 = new TestXidImpl(100, new byte[]{0x01}, new byte[]{0x10});

        xaResource1.start(xid1, XAResource.TMNOFLAGS);
        producer.send(session1.createTextMessage(xid1Message));
        xaResource1.end(xid1, XAResource.TMSUCCESS);

        xaResource2.start(xid2, XAResource.TMNOFLAGS);
        producer2.send(session2.createTextMessage(xid2Message));
        xaResource2.end(xid2, XAResource.TMSUCCESS);

        // Xid 2
        int status = xaResource2.prepare(xid2);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");

        xaResource2.commit(xid2, false);
        JMSTextMessage message = (JMSTextMessage) consumer.receive(30000);

        Assert.assertEquals(message.getText(), xid2Message, "Invalid Message received");

        // Xid 1
        status = xaResource1.prepare(xid1);
        Assert.assertEquals(status, XAResource.XA_OK, "Prepare state failed for distributed transaction");

        xaResource1.commit(xid1, false);
        message = (JMSTextMessage) consumer.receive(30000);

        Assert.assertEquals(message.getText(), xid1Message, "Invalid Message received");

        session1.close();
        session2.close();
        xaConnection1.close();
        xaConnection2.close();

    }

}
