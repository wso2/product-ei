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
import org.apache.commons.lang3.tuple.ImmutablePair;
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
import org.wso2.mb.integration.common.clients.AndesJMSPublisher;
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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Following test cases are related to redelivery delay feature for rejected messages.
 */
public class RedeliveryDelayTestCase extends MBIntegrationBaseTest {
    private Log log = LogFactory.getLog(RedeliveryDelayTestCase.class);

    /**
     * The default andes acknowledgement wait timeout.
     */
    private String defaultAndesAckWaitTimeOut = null;
    private String defaultAndesRedeliveryDelay = null;

    /**
     * Initializing test case.
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
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "0");

        // Get current "AndesRedeliveryDelay" system property.
        defaultAndesRedeliveryDelay = System.getProperty(AndesClientConstants.ANDES_REDELIVERY_DELAY_PROPERTY);

        System.setProperty(AndesClientConstants.ANDES_REDELIVERY_DELAY_PROPERTY, "10000");
    }

    /**
     * This test publishes 10 messages and the subscriber rejects the first message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void firstMessageInvalidOnlyQueueMessageListenerTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();
        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstMessageInvalidOnlyQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstMessageInvalidOnlyQueue");
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
                    receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
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
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 10, "#0");

        Assert.assertEquals(receivedMessages.size(), sendCount + 1, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects the first message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     * Here message receive method is used instead of the message listener to receive messages.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void firstMessageInvalidOnlyQueueMessageReceiverTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();
        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstMessageInvalidOnlyReceiverQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstMessageInvalidOnlyReceiverQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        final MessageConsumer receiver = andesJMSConsumer.getReceiver();
        Thread messageReceivingThread = new Thread() {
            private boolean receivedFirstMessage = false;
            public void run() {
                while (receiver != null) {
                    try {
                        TextMessage textMessage = (TextMessage) receiver.receive();
                        if (!receivedFirstMessage && "#0".equals(textMessage.getText())) {
                            receivedFirstMessage = true;
                        } else {
                            textMessage.acknowledge();
                        }
                        receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                        andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred when receiving messages.", e);
                    }
                }
            }
        };
        messageReceivingThread.start();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + getMessageList(receivedMessages));
        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 10, "#0");

        Assert.assertEquals(receivedMessages.size(), sendCount + 1, "Message receiving failed.");

    }


    /**
     * This test publishes 10 messages and the subscriber rejects all message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void allUnacknowledgeMessageListenerTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        int sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "multipleUnacknowledgeQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "multipleUnacknowledgeQueue");
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
                    if (getMessageList(receivedMessages).contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
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
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount * 2; i++) {
            if (i < sendCount) {
                Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                        "Invalid messages received. #" + Integer.toString(i) + " expected.");
            } else {
                validateMessageContentAndDelay(receivedMessages, i - sendCount, i,
                        "#" + Integer.toString(i - sendCount));
            }
        }

        Assert.assertEquals(receivedMessages.size(), sendCount * 2, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects all message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     * Here message receive method is used instead of the message listener to receive messages.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void allUnacknowledgeMessageReceiverTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        int sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "multipleUnacknowledgeReceiverQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "multipleUnacknowledgeReceiverQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        final MessageConsumer receiver = andesJMSConsumer.getReceiver();
        Thread messageReceivingThread = new Thread() {
            public void run() {
                while (receiver != null) {
                    try {
                        TextMessage textMessage = (TextMessage) receiver.receive();
                        if (getMessageList(receivedMessages).contains(textMessage.getText())) {
                            textMessage.acknowledge();
                        }
                        receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                        andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred when receiving messages.", e);
                    }
                }
            }
        };
        messageReceivingThread.start();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount * 2; i++) {
            if (i < sendCount) {
                Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                        "Invalid messages received. #" + Integer.toString(i) + " expected.");
            } else {
                validateMessageContentAndDelay(receivedMessages, i - sendCount, i,
                        "#" + Integer.toString(i - sendCount));
            }
        }

        Assert.assertEquals(receivedMessages.size(), sendCount * 2, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects a message after each 3 received messages and then wait
     * for the redelivered message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void oneByOneUnacknowledgeMessageListenerTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeQueue");
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
                    if (Integer.parseInt(textMessage.getText().split("#")[1]) % 3 != 0 ||
                        getMessageList(receivedMessages).contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
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
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 10, "#0");
        validateMessageContentAndDelay(receivedMessages, 1, 11, "#3");
        validateMessageContentAndDelay(receivedMessages, 2, 12, "#6");
        validateMessageContentAndDelay(receivedMessages, 3, 13, "#9");

        Assert.assertEquals(receivedMessages.size(), sendCount + 4, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects a message after each 3 received messages and then wait
     * for the redelivered message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     * Here message receive method is used instead of the message listener to receive messages.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void oneByOneUnacknowledgeMessageReceiverTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeReceiverQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeReceiverQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        final MessageConsumer receiver = andesJMSConsumer.getReceiver();
        Thread messageReceivingThread = new Thread() {
            public void run() {
                while (receiver != null) {
                    try {
                        TextMessage textMessage = (TextMessage) receiver.receive();
                        if (Integer.parseInt(textMessage.getText().split("#")[1]) % 3 != 0 ||
                            getMessageList(receivedMessages).contains(textMessage.getText())) {
                            textMessage.acknowledge();
                        }
                        receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                        andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred when receiving messages.", e);
                    }
                }
            }
        };
        messageReceivingThread.start();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 10, "#0");
        validateMessageContentAndDelay(receivedMessages, 1, 11, "#3");
        validateMessageContentAndDelay(receivedMessages, 2, 12, "#6");
        validateMessageContentAndDelay(receivedMessages, 3, 13, "#9");

        Assert.assertEquals(receivedMessages.size(), sendCount + 4, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects first 4 messages and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void firstFewUnacknowledgeMessageListenerTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstFewUnacknowledgeQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstFewUnacknowledgeQueue");
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
                    if (Integer.parseInt(textMessage.getText().split("#")[1]) >= 4 ||
                        getMessageList(receivedMessages).contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
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
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 10, "#0");
        validateMessageContentAndDelay(receivedMessages, 1, 11, "#1");
        validateMessageContentAndDelay(receivedMessages, 2, 12, "#2");
        validateMessageContentAndDelay(receivedMessages, 3, 13, "#3");

        Assert.assertEquals(receivedMessages.size(), sendCount + 4, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects first 4 messages and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     * Here message receive method is used instead of the message listener to receive messages.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void firstFewUnacknowledgeMessageReceiverTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstFewUnacknowledgeReceiverQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "firstFewUnacknowledgeReceiverQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        final MessageConsumer receiver = andesJMSConsumer.getReceiver();
        Thread messageReceivingThread = new Thread() {
            public void run() {
                while (receiver != null) {
                    try {
                        TextMessage textMessage = (TextMessage) receiver.receive();
                        if (Integer.parseInt(textMessage.getText().split("#")[1]) >= 4 ||
                            getMessageList(receivedMessages).contains(textMessage.getText())) {
                            textMessage.acknowledge();
                        }
                        receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                        andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred when receiving messages.", e);
                    }
                }
            }
        };
        messageReceivingThread.start();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 10, "#0");
        validateMessageContentAndDelay(receivedMessages, 1, 11, "#1");
        validateMessageContentAndDelay(receivedMessages, 2, 12, "#2");
        validateMessageContentAndDelay(receivedMessages, 3, 13, "#3");

        Assert.assertEquals(receivedMessages.size(), sendCount + 4, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects the 8th message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void unacknowledgeMiddleMessageMessageListenerTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "unacknowledgeMiddleMessageQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "unacknowledgeMiddleMessageQueue");
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
                    if (!textMessage.getText().equals("#7") ||
                        getMessageList(receivedMessages).contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
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
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 6, 10, "#7");

        Assert.assertEquals(receivedMessages.size(), sendCount + 1, "Message receiving failed.");
    }


    /**
     * This test publishes 10 messages and the subscriber rejects the 8th message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     * Here message receive method is used instead of the message listener to receive messages.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void unacknowledgeMiddleMessageMessageReceiverTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 10;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "unacknowledgeMiddleMessageReceiverQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "unacknowledgeMiddleMessageReceiverQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        final MessageConsumer receiver = andesJMSConsumer.getReceiver();
        Thread messageReceivingThread = new Thread() {
            public void run() {
                while (receiver != null) {
                    try {
                        TextMessage textMessage = (TextMessage) receiver.receive();
                        if (!textMessage.getText().equals("#7") ||
                            getMessageList(receivedMessages).contains(textMessage.getText())) {
                            textMessage.acknowledge();
                        }
                        receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                        andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred when receiving messages.", e);
                    }
                }
            }
        };
        messageReceivingThread.start();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 6, 10, "#7");

        Assert.assertEquals(receivedMessages.size(), sendCount + 1, "Message receiving failed.");
    }


    /**
     * This test publishes 1000 messages and the subscriber reject each 100th message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void oneByOneUnacknowledgeMessageListenerForMultipleMessagesTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 1000;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeMessageListenerForMultiple");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeMessageListenerForMultiple");
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
                    if (Integer.parseInt(textMessage.getText().split("#")[1]) % 100 != 0 ||
                        getMessageList(receivedMessages).contains(textMessage.getText())) {
                        message.acknowledge();
                    }
                    receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                    andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                } catch (JMSException e) {
                    throw new RuntimeException("Exception occurred when receiving messages.", e);
                }
            }
        });

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME * 2);
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 1000, "#0");
        validateMessageContentAndDelay(receivedMessages, 99, 1001, "#100");
        validateMessageContentAndDelay(receivedMessages, 199, 1002, "#200");
        validateMessageContentAndDelay(receivedMessages, 299, 1003, "#300");
        validateMessageContentAndDelay(receivedMessages, 399, 1004, "#400");
        validateMessageContentAndDelay(receivedMessages, 499, 1005, "#500");
        validateMessageContentAndDelay(receivedMessages, 599, 1006, "#600");
        validateMessageContentAndDelay(receivedMessages, 699, 1007, "#700");
        validateMessageContentAndDelay(receivedMessages, 799, 1008, "#800");
        validateMessageContentAndDelay(receivedMessages, 899, 1009, "#900");

        Assert.assertEquals(receivedMessages.size(), sendCount + 10, "Message receiving failed.");
    }


    /**
     * This test publishes 1000 messages and the subscriber reject each 100th message and then wait for the redelivered
     * message.
     * <p/>
     * The redelivered message is tested against the same message content with the original message and the timestamps
     * are also checked against the original message timestamp to make sure that the message was delayed.
     * Here message receive method is used instead of the message listener to receive messages.
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void oneByOneUnacknowledgeMessageReceiverForMultipleMessagesTestCase()
            throws AndesClientConfigurationException, XPathExpressionException, IOException, JMSException,
            AndesClientException, NamingException {
        long sendCount = 1000;
        final List<ImmutablePair<String, Calendar>> receivedMessages = new ArrayList<>();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeMessageReceiverForMultipleQueue");
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.PER_MESSAGE_ACKNOWLEDGE);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE,
                        "oneByOneUnacknowledgeMessageReceiverForMultipleQueue");
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        final AndesJMSConsumer andesJMSConsumer = consumerClient.getConsumers().get(0);
        final MessageConsumer receiver = andesJMSConsumer.getReceiver();
        Thread messageReceivingThread = new Thread() {
            public void run() {
                while (receiver != null) {
                    try {
                        TextMessage textMessage = (TextMessage) receiver.receive();
                        if (Integer.parseInt(textMessage.getText().split("#")[1]) % 100 != 0 ||
                            getMessageList(receivedMessages).contains(textMessage.getText())) {
                            textMessage.acknowledge();
                        }
                        receivedMessages.add(ImmutablePair.of(textMessage.getText(), Calendar.getInstance()));
                        andesJMSConsumer.getReceivedMessageCount().incrementAndGet();
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred when receiving messages.", e);
                    }
                }
            }
        };
        messageReceivingThread.start();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        AndesJMSPublisher andesJMSPublisher = publisherClient.getPublishers().get(0);
        MessageProducer sender = andesJMSPublisher.getSender();
        for (int i = 0; i < sendCount; i++) {
            TextMessage textMessage = andesJMSPublisher.getSession().createTextMessage("#" + Integer.toString(i));
            sender.send(textMessage);
        }

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME * 2);
        log.info("Received Messages : " + getMessageList(receivedMessages));

        for (int i = 0; i < sendCount; i++) {
            Assert.assertEquals(receivedMessages.get(i).getLeft(), "#" + Integer.toString(i),
                    "Invalid messages received. #" + Integer.toString(i) + " expected.");
        }

        validateMessageContentAndDelay(receivedMessages, 0, 1000, "#0");
        validateMessageContentAndDelay(receivedMessages, 99, 1001, "#100");
        validateMessageContentAndDelay(receivedMessages, 199, 1002, "#200");
        validateMessageContentAndDelay(receivedMessages, 299, 1003, "#300");
        validateMessageContentAndDelay(receivedMessages, 399, 1004, "#400");
        validateMessageContentAndDelay(receivedMessages, 499, 1005, "#500");
        validateMessageContentAndDelay(receivedMessages, 599, 1006, "#600");
        validateMessageContentAndDelay(receivedMessages, 699, 1007, "#700");
        validateMessageContentAndDelay(receivedMessages, 799, 1008, "#800");
        validateMessageContentAndDelay(receivedMessages, 899, 1009, "#900");

        Assert.assertEquals(receivedMessages.size(), sendCount + 10, "Message receiving failed.");
    }

    /**
     * This method will restore all the configurations back.
     * Following configurations will be restored.
     * 1. AndesAckWaitTimeOut system property.
     * 2. AndesRedeliveryDelay system property.
     * 3. Deleted all destination created in this test class.
     * 4. Restore default broker.xml and restart server.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass()
    public void tearDown()
            throws IOException, AutomationUtilException, LogoutAuthenticationExceptionException,
            AndesAdminServiceBrokerManagerAdminException {
        if (StringUtils.isBlank(defaultAndesAckWaitTimeOut)) {
            System.clearProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, defaultAndesAckWaitTimeOut);
        }

        if (StringUtils.isBlank(defaultAndesRedeliveryDelay)) {
            System.clearProperty(AndesClientConstants.ANDES_REDELIVERY_DELAY_PROPERTY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_REDELIVERY_DELAY_PROPERTY, defaultAndesRedeliveryDelay);
        }

        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient = new AndesAdminClient(super.backendURL, sessionCookie);

        andesAdminClient.deleteQueue("firstMessageInvalidOnlyQueue");
        andesAdminClient.deleteQueue("firstMessageInvalidOnlyReceiverQueue");
        andesAdminClient.deleteQueue("multipleUnacknowledgeQueue");
        andesAdminClient.deleteQueue("multipleUnacknowledgeReceiverQueue");
        andesAdminClient.deleteQueue("oneByOneUnacknowledgeQueue");
        andesAdminClient.deleteQueue("oneByOneUnacknowledgeReceiverQueue");
        andesAdminClient.deleteQueue("firstFewUnacknowledgeQueue");
        andesAdminClient.deleteQueue("firstFewUnacknowledgeReceiverQueue");
        andesAdminClient.deleteQueue("unacknowledgeMiddleMessageQueue");
        andesAdminClient.deleteQueue("unacknowledgeMiddleMessageReceiverQueue");
        andesAdminClient.deleteQueue("oneByOneUnacknowledgeMessageListenerForMultiple");
        andesAdminClient.deleteQueue("oneByOneUnacknowledgeMessageReceiverForMultipleQueue");
        loginLogoutClientForAdmin.logout();

        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);
    }

    /**
     * Validates message content of redelivered messages against original message. Validate that the redelivery delay
     * has occurred.
     *
     * @param receivedMessages        The received message list.
     * @param originalMessageIndex    The index of the origin message in the received message list.
     * @param redeliveredMessageIndex The index of the redelivered message in the received message list.
     * @param expectedMessageContent  The expected message content.
     */
    private void validateMessageContentAndDelay(
            List<ImmutablePair<String, Calendar>> receivedMessages,
            int originalMessageIndex,
            int redeliveredMessageIndex,
            String expectedMessageContent) {
        // Validate message content
        String messageContent = receivedMessages.get(redeliveredMessageIndex).getLeft();
        Assert.assertEquals(messageContent, expectedMessageContent, "Invalid messages received.");

        // Validate delay
        Calendar originalMessageCalendar = receivedMessages.get(originalMessageIndex).getRight();
        log.info("Original message timestamp for " + messageContent + " : " +
                 originalMessageCalendar.getTimeInMillis());
        originalMessageCalendar.add(Calendar.SECOND, 10);
        log.info("Minimum redelivered timestamp for " + messageContent + " : " +
                 originalMessageCalendar.getTimeInMillis());
        Calendar redeliveredMessageCalendar = receivedMessages.get(redeliveredMessageIndex).getRight();
        log.info("Timestamp of redelivered for " + messageContent + " message : " +
                 redeliveredMessageCalendar.getTimeInMillis());
        Assert.assertTrue(originalMessageCalendar.compareTo(redeliveredMessageCalendar) <= 0,
                "Message received before the redelivery delay");
    }

    /**
     * Gets the received messages to a list.
     *
     * @param receivedMessages The list of received messages as a pair of message content and time received.
     * @return A list of message content.
     */
    private List<String> getMessageList(List<ImmutablePair<String, Calendar>> receivedMessages) {
        List<String> messages = new ArrayList<>();
        for (ImmutablePair<String, Calendar> receivedMessage : receivedMessages) {
            messages.add(receivedMessage.getLeft());
        }
        return messages;
    }
}
