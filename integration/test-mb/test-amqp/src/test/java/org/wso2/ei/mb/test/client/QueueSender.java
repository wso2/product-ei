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

import org.wso2.ei.mb.test.utils.ConfigurationConstants;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Queue message sender.
 */
public class QueueSender {

    private static final String initialConnectionFactory = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String connectionFactoryNamePrefix = "connectionfactory.";
    private static final String queueNamePrefix = "queue.";
    private static final String connectionFactoryName = "andesConnectionfactory";

    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private javax.jms.QueueSender queueSender;

    public QueueSender(String queueName, JMSAcknowledgeMode acknowledgeMode,
                       Map<String, String> clientConfigPropertiesMap)
            throws JMSException, NamingException, IOException {

        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, initialConnectionFactory);
        properties.put(connectionFactoryNamePrefix + connectionFactoryName,
                getTCPConnectionURL(clientConfigPropertiesMap));
        properties.put(queueNamePrefix + queueName, queueName);
        InitialContext ctx = new InitialContext(properties);
        // Lookup connection factory
        QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup(connectionFactoryName);
        queueConnection = connFactory.createQueueConnection();
        queueConnection.start();
        queueSession = queueConnection.createQueueSession(false, acknowledgeMode.getType());
        // Send message
        Queue queue = (Queue) ctx.lookup(queueName);
        // create the message to send
        queueSender = queueSession.createSender(queue);

    }

    /**
     * Send queue messages
     * @param sendMessageCount Number of message to be sent
     * @param textPayload String payload to be sent
     * @throws JMSException
     */
    public void sendMessages(int sendMessageCount, String textPayload) throws JMSException {

        TextMessage textMessage = queueSession.createTextMessage(textPayload);

        for (int i = 0; i < sendMessageCount; i++) {
            queueSender.send(textMessage);
        }
    }

    /**
     * Send single queue message
     * @param textPayload String payload to be sent
     * @throws JMSException
     */
    public void sendMessage(String textPayload) throws JMSException {

        TextMessage textMessage = queueSession.createTextMessage(textPayload);

        queueSender.send(textMessage);

    }

    /**
     * Close JMS connection/session and cleanup resources.
     * @throws JMSException
     */
    public void closeSender() throws JMSException {
        queueSender.close();
        queueSession.close();
        queueConnection.close();
    }

    /**
     * Provide connection URL based on defined parameters.
     *
     * @param clientConfigPropertiesMap client connection config properties map
     * @return connection URL
     */
    private String getTCPConnectionURL(Map<String, String> clientConfigPropertiesMap) {
        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{port}'

        return new StringBuffer()
                .append("amqp://").append(clientConfigPropertiesMap.get(
                        ConfigurationConstants.DEFAULT_USERNAME_PROPERTY)).append(":")
                .append(clientConfigPropertiesMap.get(
                        ConfigurationConstants.DEFAULT_PASSWORD_PROPERTY))
                .append("@").append(clientConfigPropertiesMap.get(
                        ConfigurationConstants.CARBON_CLIENT_ID_PROPERTY))
                .append("/").append(clientConfigPropertiesMap.get(
                        ConfigurationConstants.CARBON_VIRTUAL_HOSTNAME_PROPERTY))
                .append("?brokerlist='tcp://").append(clientConfigPropertiesMap.get(
                        ConfigurationConstants.CARBON_DEFAULT_HOSTNAME_PROPERTY))
                .append(":")
                .append(clientConfigPropertiesMap.get(
                        ConfigurationConstants.CARBON_DEFAULT_PORT_PROPERTY))
                .append("'").toString();
    }
}
