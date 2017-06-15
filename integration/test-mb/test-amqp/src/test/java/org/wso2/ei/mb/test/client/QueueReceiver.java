/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.mb.test.client;

import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Queue message receiver.
 */
public class QueueReceiver {

    private static final String initialConnectionFactory = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String connectionFactoryNamePrefix = "connectionfactory.";
    private static final String connectionFactoryName = "andesConnectionfactory";

    private String carbonClientId = "carbon";
    private String carbonVirtualHostName = "carbon";
    private String carbonDefaultHostName = "localhost";
    private String carbonDefaultPort = "5672";

    private String userName = "admin";
    private String password = "admin";
    private String queueName = "testQueue";

    private QueueMessageListener messageListener;
    private MessageConsumer consumer;
    private QueueSession queueSession;
    private QueueConnection queueConnection;

    public QueueReceiver() throws NamingException, JMSException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, initialConnectionFactory);
        properties.put(connectionFactoryNamePrefix + connectionFactoryName, getTCPConnectionURL(userName, password));
        properties.put("queue." + queueName, queueName);
        InitialContext ctx = new InitialContext(properties);

        // Lookup connection factory
        QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup(connectionFactoryName);
        queueConnection = connFactory.createQueueConnection();
        queueConnection.start();
        queueSession = queueConnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

        Queue queue = (Queue) ctx.lookup(queueName);
        consumer = queueSession.createConsumer(queue);
    }

    /**
     * Create JMS session/connection for subscriber based on defined parameters.
     *
     * @return MessageConsumer
     * @throws NamingException
     * @throws JMSException
     */
    public MessageConsumer registerSubscriber() throws JMSException {

        messageListener = new QueueMessageListener(5);
        consumer.setMessageListener(messageListener);

        return consumer;
    }

    /**
     * Provide message count received by subscriber.
     *
     * @return message count
     * @throws NamingException
     * @throws JMSException
     */
    public int receivedMessageCount() throws NamingException, JMSException {
        return messageListener.getMessageCount();
    }

    /**
     * Close JMS connection/session and clean up resources.
     *
     * @throws JMSException
     */
    public void closeReceiver() throws JMSException {
        consumer.close();
        queueSession.close();
        queueConnection.stop();
        queueConnection.close();
    }

    /**
     * Provide connection URL based on defined parameters.
     *
     * @param username username for basic authentication
     * @param password password for basic authentication
     * @return connection URL
     */
    private String getTCPConnectionURL(String username, String password) {
        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{port}'
        return new StringBuffer()
                .append("amqp://").append(username).append(":").append(password)
                .append("@").append(carbonClientId)
                .append("/").append(carbonVirtualHostName)
                .append("?brokerlist='tcp://").append(carbonDefaultHostName).append(":")
                .append(carbonDefaultPort).append("'").toString();
    }

}
