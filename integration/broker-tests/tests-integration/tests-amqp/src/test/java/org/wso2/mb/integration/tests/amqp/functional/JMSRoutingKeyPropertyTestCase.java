/*
 * Copyright (c) 2016 WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.wso2.mb.integration.tests.amqp.functional;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.AndesJMSConsumer;
import org.wso2.mb.integration.common.clients.AndesJMSPublisher;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;

/**
 * Test cases to check that routing key value can be set as a JMS property.
 */
public class JMSRoutingKeyPropertyTestCase extends MBIntegrationBaseTest {
    /**
     * The default andes acknowledgement wait timeout.
     */
    private String defaultAndesSetRoutingKeyValue = null;

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);

        // Get current "AndesSetRoutingKey" system property.
        defaultAndesSetRoutingKeyValue = System.getProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY);
    }

    /**
     * Publishes few messages to a queue with setting "AndesSetRoutingKey" system property set to non-null value and
     * check the correct routing key comes as a JMS property for each message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void queueRoutingKeyPropertyTestCase() throws AndesClientConfigurationException, XPathExpressionException,
            IOException, JMSException, AndesClientException, NamingException {
        System.setProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY, "1");
        long sendCount = 10;
        final List<Message> messages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "RoutingKeyPropertyQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "RoutingKeyPropertyQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                messages.add(message);
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        for (Message message : messages) {
            Assert.assertEquals(message.getStringProperty(AndesClientConstants
                    .JMS_ANDES_ROUTING_KEY_MESSAGE_PROPERTY), "RoutingKeyPropertyQueue", "Invalid value received for " +
                                                                                         "routing key property.");
        }
    }

    /**
     * Publishes few messages to a topic with setting "AndesSetRoutingKey" system property set to non-null value and
     * check the correct routing key comes as a JMS property for each message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void topicRoutingKeyPropertyTestCase() throws AndesClientConfigurationException, XPathExpressionException,
            IOException, JMSException, AndesClientException, NamingException {
        System.setProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY, "1");
        long sendCount = 10;
        final List<Message> messages = new ArrayList<>();
        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.TOPIC, "RoutingKeyPropertyTopic");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.TOPIC, "RoutingKeyPropertyTopic");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                messages.add(message);
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        for (Message message : messages) {
            Assert.assertEquals(message.getStringProperty(AndesClientConstants
                    .JMS_ANDES_ROUTING_KEY_MESSAGE_PROPERTY), "RoutingKeyPropertyTopic", "Invalid value received for " +
                                                                                         "routing key property.");

        }
    }

    /**
     * Publishes few messages to a queue with setting "AndesSetRoutingKey" system property set to null value and check
     * null comes as a JMS property "JMS_ANDES_ROUTING_KEY" for each message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void queueRoutingKeyPropertyNullTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        System.clearProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY);
        long sendCount = 10;
        final List<Message> messages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "RoutingKeyPropertyQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "RoutingKeyPropertyQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                messages.add(message);
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        for (Message message : messages) {
            Assert.assertEquals(message.getStringProperty(AndesClientConstants
                    .JMS_ANDES_ROUTING_KEY_MESSAGE_PROPERTY), null, "Invalid value received for routing key property.");
        }
    }

    /**
     * Publishes few messages to a topic with setting "AndesSetRoutingKey" system property set to null value and check
     * null comes as a JMS property "JMS_ANDES_ROUTING_KEY" for each message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void topicRoutingKeyPropertyNullTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        System.clearProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY);
        long sendCount = 10;
        final List<Message> messages = new ArrayList<>();
        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.TOPIC, "RoutingKeyPropertyTopic");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.TOPIC, "RoutingKeyPropertyTopic");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                messages.add(message);
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        for (Message message : messages) {
            Assert.assertEquals(message.getStringProperty(AndesClientConstants
                    .JMS_ANDES_ROUTING_KEY_MESSAGE_PROPERTY), null, "Invalid value received for routing key property.");

        }
    }

    /**
     * Set default properties after test case.
     */
    @AfterClass()
    public void tearDown() {
        // Setting system property "AndesAckWaitTimeOut" to default value.
        if (StringUtils.isBlank(defaultAndesSetRoutingKeyValue)) {
            System.clearProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_SET_ROUTING_KEY, defaultAndesSetRoutingKeyValue);
        }
    }
}
