/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.java.org.wso2.ei.mb.test.client;

import org.wso2.ei.mb.test.client.QueueMessageListener;
import org.wso2.ei.mb.test.utils.ConfigurationConstants;
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Topic subscriber client
 */
public class TopicReceiver {

    private static final String initialConnectionFactory = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String connectionFactoryNamePrefix = "connectionfactory.";
    private static final String queueNamePrefix = "topic.";
    private static final String connectionFactoryName = "andesConnectionfactory";
    private QueueMessageListener messageListener;
    private JMSAcknowledgeMode acknowledgeMode;

    private TopicConnection topicConnection;
    private TopicSession topicSession;
    private TopicSubscriber subscriber;
    private int maximumMessageCount = Integer.MAX_VALUE;

    /**
     * Initialize the message subscriber client. Will be a durable subscriber if id provided
     * @param topicName Name of the topic to receive messages from
     * @param acknowledgeMode Acknowledgement mode
     * @param configurationReader Server configurations
     * @param durableSubId Durable subscriber ID, if durable subscription
     * @throws IOException
     * @throws NamingException
     * @throws JMSException
     */
    public TopicReceiver(String topicName, JMSAcknowledgeMode acknowledgeMode,
            ConfigurationReader configurationReader, String durableSubId) throws IOException, NamingException,
            JMSException {

        this.acknowledgeMode = acknowledgeMode;

        // map of config key and config value
        Map<String, String> clientConfigPropertiesMap = configurationReader.getClientConfigProperties();
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, initialConnectionFactory);
        properties.put(connectionFactoryNamePrefix + connectionFactoryName,
                getTCPConnectionURL(clientConfigPropertiesMap));
        properties.put(queueNamePrefix + topicName, topicName);
        InitialContext ctx = new InitialContext(properties);

        TopicConnectionFactory connectionFactory = (TopicConnectionFactory) ctx.lookup(connectionFactoryName);
        topicConnection = connectionFactory.createTopicConnection();
        topicConnection.start();
        topicSession = topicConnection.createTopicSession(false, acknowledgeMode.getType());
        Topic topic = (Topic) ctx.lookup(topicName);
        if (durableSubId != null) {
            subscriber = topicSession.createDurableSubscriber(topic, durableSubId);
        } else {
            subscriber = topicSession.createSubscriber(topic);
        }
    }

    /**
     * Register a message listener for the topic
     *
     * @return MessageConsumer
     * @throws JMSException
     */
    public MessageConsumer registerMessageListener() throws JMSException {
        messageListener = new QueueMessageListener(5, acknowledgeMode, maximumMessageCount);
        subscriber.setMessageListener(messageListener);
        return subscriber;
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
        subscriber.close();
        topicSession.close();
        topicConnection.stop();
        topicConnection.close();
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
     * Receive a message from a topic in given timeout
     * @param timeout Maximum time to wait for the message
     * @return Received message
     * @throws JMSException
     */
    public Message receiveMessage(long timeout) throws JMSException {
        return subscriber.receive(timeout);
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
