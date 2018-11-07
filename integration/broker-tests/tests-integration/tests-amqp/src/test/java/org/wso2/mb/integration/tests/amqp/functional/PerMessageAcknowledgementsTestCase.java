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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.AndesJMSConsumer;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import java.io.File;
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
 * This class contains test cases related to per message acknowledgments.
 */
public class PerMessageAcknowledgementsTestCase extends MBIntegrationBaseTest {
    private Log log = LogFactory.getLog(PerMessageAcknowledgementsTestCase.class);

    /**
     * The default andes acknowledgement wait timeout.
     */
    private String defaultAndesAckWaitTimeOut = null;

    /**
     * Initializing test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, IOException, AutomationUtilException, ConfigurationException {
        super.init(TestUserMode.SUPER_TENANT_USER);

        // Updating the redelivery attempts to 1 to speed up the test case.
        super.serverManager = new ServerConfigurationManager(automationContext);
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() + File.separator + "repository" +
                                            File.separator + "conf" + File.separator + "broker.xml";
        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);

        // Changing "maximumRedeliveryAttempts" value to "1" in broker.xml
        configurationEditor.updateProperty(AndesConfiguration.TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS, "1");
        // Restarting server
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);

        // Get current "AndesAckWaitTimeOut" system property.
        defaultAndesAckWaitTimeOut = System.getProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);

        // Setting system property "AndesAckWaitTimeOut" for andes
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "3000");
    }

    /**
     * This test publishes 10 messages and the subscriber rejects the first message and then wait for the redelivered
     * message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void firstMessageInvalidOnlyPerAckQueueMessageListenerTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        long sendCount = 10;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "firstMessageInvalidOnlyPerAckQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "firstMessageInvalidOnlyPerAckQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            private boolean receivedFirstMessage = false;

            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    if (!receivedFirstMessage && "#0".equals(textMessage.getText())) {
                        receivedFirstMessage = true;
                    } else {
                        message.acknowledge();
                    }
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. #" +
                                                                                    Integer.toString(i) + " expected.");
        }

        Assert.assertEquals(receivedMessages.get(10), "#0", "Invalid messages received. #0 expected.");

        Assert.assertEquals(receivedMessages.size(), sendCount + 1, "Message receiving failed.");
    }

    /**
     * This test publishes 10 messages and the subscriber rejects all message and then wait for the redelivered
     * message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void allUnacknowledgeMessageListenerPerAckTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        long sendCount = 10;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "allUnacknowledgePerAckQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "allUnacknowledgePerAckQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    if (receivedMessages.contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount * 2; i++) {
            if (i < sendCount) {
                Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. " +
                                                                                        "#" + Integer.toString(i) + "" +
                                                                                        " expected.");
            } else {
                Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i - 10), "Invalid messages " +
                                                                                             "received. #" + Integer
                                                                                                     .toString(i - 10) +
                                                                                             " expected.");
            }
        }

        Assert.assertEquals(receivedMessages.size(), sendCount * 2, "Message receiving failed.");
    }

    /**
     * This test publishes 10 messages and the subscriber rejects a message after each 3 received messages and then wait
     * for the redelivered message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void oneByOneUnacknowledgeMessageListenerPerAckTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        long sendCount = 10;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "oneByOneUnacknowledgePerAckQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "oneByOneUnacknowledgePerAckQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    if (Integer.parseInt(textMessage.getText().split("#")[1]) % 3 != 0 || receivedMessages.contains
                            (textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. #" +
                                                                                    Integer.toString(i) + " expected.");
        }

        Assert.assertEquals(receivedMessages.get(10), "#0", "Invalid messages received. #0 expected.");
        Assert.assertEquals(receivedMessages.get(11), "#3", "Invalid messages received. #3 expected.");
        Assert.assertEquals(receivedMessages.get(12), "#6", "Invalid messages received. #6 expected.");
        Assert.assertEquals(receivedMessages.get(13), "#9", "Invalid messages received. #9 expected.");

        Assert.assertEquals(receivedMessages.size(), sendCount + 4, "Message receiving failed.");
    }

    /**
     * This test publishes 10 messages and the subscriber rejects first 4 messages and then wait for the redelivered
     * message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void firstFewUnacknowledgeMessageListenerPerAckTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        long sendCount = 10;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "firstFewUnacknowledgePerAckQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "firstFewUnacknowledgePerAckQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    if (Integer.parseInt(textMessage.getText().split("#")[1]) >= 4 || receivedMessages.contains
                            (textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. #" +
                                                                                    Integer.toString(i) + " expected.");
        }

        Assert.assertEquals(receivedMessages.get(10), "#0", "Invalid messages received. #0 expected.");
        Assert.assertEquals(receivedMessages.get(11), "#1", "Invalid messages received. #1 expected.");
        Assert.assertEquals(receivedMessages.get(12), "#2", "Invalid messages received. #2 expected.");
        Assert.assertEquals(receivedMessages.get(13), "#3", "Invalid messages received. #3 expected.");

        Assert.assertEquals(receivedMessages.size(), sendCount + 4, "Message receiving failed.");
    }

    /**
     * This test publishes 10 messages and the subscriber rejects the 8th message and then wait for the redelivered
     * message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void unacknowledgeMiddleMessageMessageListenerPerAckTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        long sendCount = 10;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "unacknowledgeMiddleMessagePerAckQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "unacknowledgeMiddleMessagePerAckQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    if (!textMessage.getText().equals("#7") || receivedMessages.contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. #" +
                                                                                    Integer.toString(i) + " expected.");
        }

        Assert.assertEquals(receivedMessages.get(10), "#7", "Invalid messages received. #7 expected.");

        Assert.assertEquals(receivedMessages.size(), sendCount + 1, "Message receiving failed.");
    }

    /**
     * This test publishes 1000 messages and the subscriber reject each 100th message and then wait for the redelivered
     * message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void oneByOneUnacknowledgeMessageListenerForMultipleMessagesPerAckTestCase() throws
            AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 1000;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "oneByOneUnacknowledgeQueuePerAckMultiple");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "oneByOneUnacknowledgeQueuePerAckMultiple");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    if (Integer.parseInt(textMessage.getText().split("#")[1]) % 100 != 0 || receivedMessages.contains
                            (textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME * 2);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. #" +
                                                                                    Integer.toString(i) + " expected.");
        }

        Assert.assertEquals(receivedMessages.get(1000), "#0", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1001), "#100", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1002), "#200", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1003), "#300", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1004), "#400", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1005), "#500", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1006), "#600", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1007), "#700", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1008), "#800", "Invalid messages received.");
        Assert.assertEquals(receivedMessages.get(1009), "#900", "Invalid messages received.");

        Assert.assertEquals(receivedMessages.size(), sendCount + 10, "Message receiving failed.");
    }

    /**
     * This test publishes 1000 messages and the subscriber accepts all message and then wait for the redelivered
     * message.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void allAcknowledgeMessageListenerForMultipleMessagesTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, IOException, JMSException, AndesClientException, NamingException {
        long sendCount = 1000;
        final List<String> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "allAcknowledgeMultiplePerAckQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "allAcknowledgeMultiplePerAckQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        MessageConsumer receiver = andesJMSConsumer.getReceiver();
        receiver.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    message.acknowledge();
                    receivedMessages.add(textMessage.getText());
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        MessageProducer sender = publisherClient.getPublishers().get(0).getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = publisherClient.getPublishers().get(0).getSession().createTextMessage("#" +
                                                                                                Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME * 2);
        log.info("Received Messages : " + receivedMessages);

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i), "#" + Integer.toString(i), "Invalid messages received. #" +
                                                                                    Integer.toString(i) + " expected.");
        }

        Assert.assertEquals(receivedMessages.size(), sendCount, "Message receiving failed.");
    }

    /**
     * This method will restore all the configurations back. Following configurations will be restored. 1.
     * AndesAckWaitTimeOut system property. 2. Delete all destination created in the test case. 3. Restore default
     * broker.xml and restart server.
     *
     * @throws IOException
     * @throws AutomationUtilException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws LogoutAuthenticationExceptionException
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException, AndesAdminServiceBrokerManagerAdminException,
            LogoutAuthenticationExceptionException {
        if (StringUtils.isBlank(defaultAndesAckWaitTimeOut)) {
            System.clearProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, defaultAndesAckWaitTimeOut);
        }

        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient = new AndesAdminClient(super.backendURL, sessionCookie);

        andesAdminClient.deleteQueue("firstMessageInvalidOnlyPerAckQueue");
        andesAdminClient.deleteQueue("allUnacknowledgePerAckQueue");
        andesAdminClient.deleteQueue("oneByOneUnacknowledgePerAckQueue");
        andesAdminClient.deleteQueue("firstFewUnacknowledgePerAckQueue");
        andesAdminClient.deleteQueue("unacknowledgeMiddleMessagePerAckQueue");
        andesAdminClient.deleteQueue("oneByOneUnacknowledgeQueuePerAckMultiple");
        andesAdminClient.deleteQueue("allAcknowledgeMultiplePerAckQueue");
        loginLogoutClientForAdmin.logout();

        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);
    }
}