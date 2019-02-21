/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * This Simple JMS client acts as JMS consumer and producer for ActiveMQ
 */
public class ActiveMQJMSClient implements ExceptionListener {

    private static final Log log = LogFactory.getLog(ActiveMQJMSClient.class);
    private String brokerUrl;

    public ActiveMQJMSClient(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }


    /**
     * Function to retrieve message from specified message queue
     *
     * @param queueName Name of the queue
     * @param timeout Timeout value (in milliseconds)
     * @return Retrieved message from the queue
     * @throws JMSException if error occurred
     */
    public Message consumeMessageFromQueue(String queueName, long timeout) throws JMSException {

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();
            connection.setExceptionListener(this);

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queueName);

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

            // Wait for a message
            return consumer.receive(timeout);

        } finally {
            if (consumer != null) {
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Function to produce message to ActiveMQ Queue
     *
     * @param queueName name of the target queue
     * @param messageStr message to place
     * @throws JMSException
     */
    public void produceMessageToQueue(String queueName, String messageStr) throws JMSException {

        Connection connection = null;
        Session session = null;

        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();
            connection.setExceptionListener(this);

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queueName);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            TextMessage message = session.createTextMessage(messageStr);

            // Tell the producer to send the message
            producer.send(message);

        } finally {
            // Clean up
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }


    @Override
    public void onException(JMSException e) {
        log.error("Error occurred while communicating with ActiveMQ message broker", e);
    }
}
