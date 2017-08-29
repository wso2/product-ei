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

import org.wso2.ei.mb.test.utils.ClientConstants;
import org.wso2.ei.mb.test.utils.ClientUtils;
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Topic subscriber client.
 */
public class TopicReceiver {

    private TopicConnection topicConnection;
    private TopicSession topicSession;
    private TopicSubscriber subscriber;
    private int maximumMessageCount = Integer.MAX_VALUE;

    /**
     * Initialize the message subscriber client. Will be a durable subscriber if id provided.
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

        // map of config key and config value
        Map<String, String> clientConfigPropertiesMap = configurationReader.getClientConfigProperties();
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, ClientConstants.FILE_INITIAL_CONNECTION_FACTORY);
        properties.put(ClientConstants.CONNECTION_FACTORY_NAME_PREFIX + ClientConstants.ANDES_CONNECTION_FACTORY_NAME,
                ClientUtils.getTCPConnectionURL(clientConfigPropertiesMap));
        properties.put(ClientConstants.TOPIC_NAME_PREFIX + topicName, topicName);
        InitialContext ctx = new InitialContext(properties);

        TopicConnectionFactory connectionFactory = (TopicConnectionFactory) ctx
                .lookup(ClientConstants.ANDES_CONNECTION_FACTORY_NAME);
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
     * Close JMS connection/session and clean up resources.
     *
     * @throws JMSException
     */
    public void closeReceiver() throws JMSException {
        try {
            subscriber.close();
            topicSession.close();
            topicConnection.stop();
            topicConnection.close();
        } catch (NullPointerException e) {
            //ignore
        }

    }

    /**
     * get Maximum Messages Count.
     */
    public int getMaximumMessageCount() {
        return maximumMessageCount;
    }

    /**
     * set Maximum Messages Count.
     */
    public void setMaximumMessageCount(int maximumMessageCount) {
        this.maximumMessageCount = maximumMessageCount;
    }

    /**
     * Receive a message from a topic in given timeout.
     * @param timeout Maximum time to wait for the message
     * @return Received message
     * @throws JMSException
     */
    public Message receiveMessage(long timeout) throws JMSException {
        return subscriber.receive(timeout);
    }

    /**
     * Get all the received messages count by looping until a timeout occurred.
     * @return Received messages count
     * @throws JMSException
     */
    public int getMessageCount() throws JMSException {
        int count = 0;
        while (true) {
            Message message = subscriber.receive(ClientConstants.CLIENT_WAIT_DURATION);
            if (message != null) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
