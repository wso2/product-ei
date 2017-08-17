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
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import java.util.Map;
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

    private QueueMessageListener messageListener;
    private MessageConsumer consumer;
    private QueueSession queueSession;
    private QueueConnection queueConnection;
    private JMSAcknowledgeMode acknowledgeMode;
    private int maximumMessageCount = Integer.MAX_VALUE;

    /**
     * This constructor creates a queue receiver object which is used as the subscriber
     * @param queueName name of the queue to be subscribed
     * @param acknowledgeMode acknowledge mode
     * @param configurationReader configuration reader object to read the JMS client config
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    public QueueReceiver(String queueName, JMSAcknowledgeMode acknowledgeMode,
                         ConfigurationReader configurationReader)
            throws NamingException, JMSException, IOException {

        this.acknowledgeMode = acknowledgeMode;
        // map of config key and config value
        Map<String, String> clientConfigPropertiesMap = configurationReader.getClientConfigProperties();
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, initialConnectionFactory);
        properties.put(connectionFactoryNamePrefix + connectionFactoryName,
                getTCPConnectionURL(clientConfigPropertiesMap));
        properties.put("queue." + queueName, queueName);
        InitialContext ctx = new InitialContext(properties);

        // Lookup connection factory
        QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup(connectionFactoryName);
        queueConnection = connFactory.createQueueConnection();
        queueConnection.start();
        queueSession = queueConnection.createQueueSession(false, acknowledgeMode.getType());

        Queue queue = (Queue) ctx.lookup(queueName);
        consumer = queueSession.createConsumer(queue);
    }

    /**
     * Create JMS session/connection for subscriber based on defined parameters.
     *
     * @return MessageConsumer
     * @throws JMSException
     */
    public MessageConsumer registerSubscriber() throws JMSException {

        messageListener = new QueueMessageListener(5, acknowledgeMode, maximumMessageCount);
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
     * Close JMS connection/session and clean up resources.
     *
     * @throws JMSException
     */
    public void stopReceiver() throws JMSException {
        consumer.close();
        queueSession.close();
        queueConnection.close();
    }

    /**
     * get Maximum Messages Count
     */
    public int getMaximumMessageCount() {
        return maximumMessageCount;
    }

    /**
     * set Maximum Messages Count
     */
    public void setMaximumMessageCount(int maximumMessageCount) {
        this.maximumMessageCount = maximumMessageCount;
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
