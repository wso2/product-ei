/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.esb.integration.common.utils.clients.jmsclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This class replicates org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer
 * introducing a Message Producer for a JMS queue, with additional methods including publishing different types of
 * JMS messages.
 */
public class JMSQueueMessageProducer {
    private static final Log logger = LogFactory.getLog(JMSQueueMessageProducer.class);
    /**
     * Constant to identify if the broker used is WSO2 MB.
     */
    private static final String MB_BROKER_URL_PREFIX = "amqp://";

    private Connection connection = null;
    private Session session = null;
    private MessageProducer producer = null;
    private QueueConnectionFactory connectionFactory = null;

    public JMSQueueMessageProducer(JMSBrokerConfiguration jmsBrokerConfiguration) throws NamingException {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, jmsBrokerConfiguration.getInitialNamingFactory());
        if (jmsBrokerConfiguration.getProviderURL().startsWith(MB_BROKER_URL_PREFIX)) {
            //setting property for Qpid running on WSO2 MB
            properties.put("connectionfactory.QueueConnectionFactory", jmsBrokerConfiguration.getProviderURL());
        } else {
            //setting property for ActiveMQ
            properties.setProperty(Context.PROVIDER_URL, jmsBrokerConfiguration.getProviderURL());
        }
        Context context = new InitialContext(properties);
        connectionFactory = (QueueConnectionFactory) context.lookup("QueueConnectionFactory");
    }

    /**
     * Method to establish the connection with the given Queue, with messages not being persisted.
     * This must be called before calling pushMessage() to send messages.
     *
     * @param queueName name of the Queue
     * @throws JMSException if an error occurs creating the connection, session or producer
     */
    public void connect(String queueName) throws JMSException {
        connect(queueName, false);
    }

    /**
     * Method to establish the connection with the given Queue, with message persistance as specified.
     * This must be called before calling pushMessage() to send messages.
     *
     * @param persistMessage whether or not messages need to be persisted
     * @param queueName name of the queue
     * @throws JMSException if connection to the queue fails
     */
    public void connect(String queueName, boolean persistMessage) throws JMSException {
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        producer = session.createProducer(destination);
        if (persistMessage) {
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        } else {
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
    }

    /**
     * Method to disconnect the connection with the given Queue.
     * This must be called after sending the messages to release the connection.
     */
    public void disconnect() {
        try {
            if (producer != null) {
                producer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            logger.warn("JMSException thrown while disconnecting ", e);
        }
    }

    /**
     * Method to send the message to the destination Queue.
     *
     * @param message message to be sent
     */
    public void sendMessage(Message message) throws JMSException {
        checkIfConnected();
        producer.send(message);
    }

    /**
     * Method to send a TextMessage.
     *
     * @param payload content of the TextMessage to be sent
     * @throws JMSException if an error occurs sending the BytesMessage
     */
    public void sendTextMessage(String payload) throws JMSException {
        checkIfConnected();
        TextMessage textMessage = session.createTextMessage(payload);
        producer.send(textMessage);
    }

    /**
     * Method to send a BytesMessage.
     *
     * @param payload content of the BytesMessage to be sent
     * @throws JMSException if an error occurs sending the BytesMessage
     */
    public void sendBytesMessage(byte[] payload) throws JMSException {
        checkIfConnected();
        BytesMessage bytesMessage = session.createBytesMessage();
        bytesMessage.writeBytes(payload);
        producer.send(bytesMessage);
    }

    /**
     * Method to send a StreamMessage.
     *
     * @param payload content of the StreamMessage to be sent
     * @throws JMSException if an error occurs sending the BytesMessage
     */
    public void sendStreamMessage(byte[] payload) throws JMSException {
        checkIfConnected();
        StreamMessage streamMessage = session.createStreamMessage();
        streamMessage.writeBytes(payload);
        producer.send(streamMessage);
    }

    /**
     * Method to send a MapMessage.
     *
     * @throws JMSException if an error occurs sending the MapMessage
     */
    public void sendMapMessage() throws JMSException {
        checkIfConnected();
        MapMessage mapMessage = session.createMapMessage();
        producer.send(mapMessage);
    }

    /**
     * Method to send an ObjectMessage.
     *
     * @param payload content of the ObjectMessage to be sent
     * @throws JMSException if an error occurs sending the ObjectMessage
     */
    public void sendObjectMessage(Serializable payload) throws JMSException {
        checkIfConnected();
        ObjectMessage objectMessage = session.createObjectMessage(payload);
        producer.send(objectMessage);
    }

    /**
     * Private method to check if session or producer are null, implying a connection was not established.
     *
     * @throws IllegalStateException if a connection has not been established
     */
    private void checkIfConnected() throws IllegalStateException {
        if (session == null || producer == null) {
            throw new IllegalStateException("No connection to a queue. Connection needs to be established to send "
                    + "messages");
        }
    }
}

