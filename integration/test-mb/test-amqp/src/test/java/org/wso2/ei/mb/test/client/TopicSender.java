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

import org.testng.log4testng.Logger;
import org.wso2.ei.mb.test.utils.ConfigurationConstants;
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Message sender to the topics
 */
public class TopicSender {
    private static Logger log = Logger.getLogger(TopicSender.class);

    private static final String initialConnectionFactory = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String connectionFactoryNamePrefix = "connectionfactory.";
    private static final String queueNamePrefix = "topic.";
    private static final String connectionFactoryName = "andesConnectionfactory";

    private TopicConnection topicConnection;
    private TopicSession topicSession;
    private TopicPublisher publisher;

    /**
     * Initialize the topic publisher
     * @param topicName Topic name to publish
     * @param acknowledgeMode JMSAcknowledgement mode
     * @param configurationReader Server configurations
     * @throws IOException
     * @throws NamingException
     * @throws JMSException
     */
    public TopicSender(String topicName, JMSAcknowledgeMode acknowledgeMode,
         ConfigurationReader configurationReader) throws IOException, NamingException, JMSException {

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
        publisher = topicSession.createPublisher(topic);

    }

    /**
     * Send a text message to the topic
     * @param messageCount Number of messages to send
     * @param textPayload   Message content
     * @throws JMSException
     */
    public void sendMessages(int messageCount, String textPayload) throws JMSException {
        TextMessage message = topicSession.createTextMessage(textPayload);

        for (int i = 0; i < messageCount; i++) {
            publisher.publish(message);
        }
    }

    /**
     * Close the publisher connections
     * @throws JMSException
     */
    public void closeSender() throws JMSException {
        publisher.close();
        topicSession.close();
        topicConnection.close();
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
