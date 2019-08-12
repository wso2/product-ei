/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.common.clients;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSDeliveryStatus;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The JMS message consumer used for creating a consumer, reading messages synchronously and also
 * asynchronously.
 */
public class AndesJMSConsumer extends AndesJMSBase
        implements Runnable, MessageListener {
    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Log log = LogFactory.getLog(AndesJMSConsumer.class);

    /**
     * The configuration for the consumer
     */
    private final AndesJMSConsumerClientConfiguration consumerConfig;

    /**
     * Timestamp for the first message consumed
     */
    private long firstMessageConsumedTimestamp;

    /**
     * Timestamp of the last message consumes
     */
    private long lastMessageConsumedTimestamp;

    /**
     * The amount of messages received by the the consumer
     */
    private AtomicLong receivedMessageCount;

    /**
     * The addition of the time differences between the timestamp at which it got published and the
     * timestamp at which it got consumed for each message consumed.
     */
    private long totalLatency;

    /**
     * The JMS connection used to create the JMS sessions
     */
    private Connection connection;

    /**
     * The JMS session used to create the JMS receiver
     */
    private Session session;

    /**
     * The receiver used the consume the received messages
     */
    private MessageConsumer receiver;

    /**
     * Creates a new JMS consumer with a given configuration.
     *
     * @param config         The configuration.
     * @param createConsumer Creates the connection, session and receiver.
     * @throws NamingException
     * @throws JMSException
     */
    public AndesJMSConsumer(AndesJMSConsumerClientConfiguration config, boolean createConsumer)
            throws NamingException, JMSException {
        super(config);
        receivedMessageCount = new AtomicLong(0);

        // Sets the configuration
        this.consumerConfig = config;

        if (createConsumer) {
            if (ExchangeType.QUEUE == this.consumerConfig.getExchangeType()) {
                this.createQueueConnection();

            } else if (ExchangeType.TOPIC == this.consumerConfig.getExchangeType()) {
                this.createTopicConnection();
            }
        }
    }

    /**
     * Creates a topic connection, session and receiver.
     *
     * @throws NamingException
     * @throws JMSException
     */
    private void createTopicConnection() throws NamingException, JMSException {
        // Creates a topic connection, sessions and receiver
        TopicConnectionFactory connFactory = (TopicConnectionFactory) super.getInitialContext()
                .lookup(AndesClientConstants.CF_NAME);
        TopicConnection topicConnection = connFactory.createTopicConnection();
        topicConnection.setClientID(this.consumerConfig.getSubscriptionID());
        topicConnection.start();
        TopicSession topicSession;
        // Sets acknowledgement mode
        if (TopicSession.SESSION_TRANSACTED == this.consumerConfig.getAcknowledgeMode().getType()) {
            topicSession = topicConnection
                    .createTopicSession(true, this.consumerConfig.getAcknowledgeMode().getType());
        } else {
            topicSession = topicConnection
                    .createTopicSession(false, this.consumerConfig.getAcknowledgeMode().getType());
        }

        Topic topic =
                (Topic) super.getInitialContext().lookup(this.consumerConfig.getDestinationName());

        connection = topicConnection;
        session = topicSession;
        // If topic is durable
        if (this.consumerConfig.isDurable()) {
            // If selectors exists
            if (null != this.consumerConfig.getSelectors()) {
                receiver = topicSession.createDurableSubscriber(topic, this.consumerConfig
                        .getSubscriptionID(), this.consumerConfig.getSelectors(), false);
            } else {
                receiver = topicSession
                        .createDurableSubscriber(topic, this.consumerConfig.getSubscriptionID());
            }
        } else {
            // If selectors exists
            if (null != this.consumerConfig.getSelectors()) {
                receiver = topicSession
                        .createSubscriber(topic, this.consumerConfig.getSelectors(), false);
            } else {
                receiver = topicSession.createSubscriber(topic);
            }
        }
    }

    /**
     * Creates a queue connection, session and receiver.
     *
     * @throws NamingException
     * @throws JMSException
     */
    private void createQueueConnection() throws NamingException, JMSException {
        // Creates a queue connection, sessions and receiver
        QueueConnectionFactory connFactory = (QueueConnectionFactory) super.getInitialContext()
                .lookup(AndesClientConstants.CF_NAME);
        QueueConnection queueConnection = connFactory.createQueueConnection();
        queueConnection.start();
        QueueSession queueSession;

        // Sets acknowledgement mode
        if (QueueSession.SESSION_TRANSACTED == this.consumerConfig.getAcknowledgeMode().getType()) {
            queueSession = queueConnection
                    .createQueueSession(true, this.consumerConfig.getAcknowledgeMode().getType());
        } else {
            queueSession = queueConnection
                    .createQueueSession(false, this.consumerConfig.getAcknowledgeMode().getType());
        }

        Queue queue =
                (Queue) super.getInitialContext().lookup(this.consumerConfig.getDestinationName());
        connection = queueConnection;
        session = queueSession;

        // If selectors exists
        if (null != this.consumerConfig.getSelectors()) {
            receiver = queueSession.createReceiver(queue, this.consumerConfig.getSelectors());
        } else {
            receiver = queueSession.createReceiver(queue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startClient() throws AndesClientException, JMSException {
        if (null != connection && null != session && null != receiver) {
            log.info("Starting Consumer");
            if (this.consumerConfig.isAsync()) {
                // Use an asynchronous message listener
                receiver.setMessageListener(this);
            } else {
                // Uses a thread to listen to messages
                Thread consumerThread = new Thread(this);
                consumerThread.start();
            }
        } else {
            throw new AndesClientException("The connection, session and message receiver is not assigned.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopClient(){
        /**
         * Using a separate thread as stopping the consumer on "onMessage" thread is not allowed.
         */
        Thread stopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != connection && null != session && null != receiver) {
                    try {
                        log.info("Closing Consumer");
                        if (ExchangeType.TOPIC == consumerConfig.getExchangeType()) {
                            if (null != receiver) {
                                TopicSubscriber topicSubscriber = (TopicSubscriber) receiver;
                                topicSubscriber.close();
                            }

                            if (null != session) {
                                TopicSession topicSession = (TopicSession) session;
                                topicSession.close();
                            }

                            if (null != connection) {
                                TopicConnection topicConnection = (TopicConnection) connection;
                                topicConnection.close();
                            }
                        } else if (ExchangeType.QUEUE == consumerConfig.getExchangeType()) {
                            if (null != receiver) {
                                QueueReceiver queueReceiver = (QueueReceiver) receiver;
                                queueReceiver.close();
                            }

                            if (null != session) {
                                QueueSession queueSession = (QueueSession) session;
                                queueSession.close();
                            }

                            if (null != connection) {
                                QueueConnection queueConnection = (QueueConnection) connection;
                                queueConnection.stop();
                                queueConnection.close();
                            }
                        }

                        receiver = null;
                        session = null;
                        connection = null;

                        log.info("Consumer Closed");

                    } catch (JMSException e) {
                        log.error("Error in stopping client.", e);
                        throw new RuntimeException("Error in stopping client.", e);
                    }
                }
            }
        });

        stopThread.start();

        try {
            stopThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error waiting for subscriber to stop", e);
        }
    }

    public void stopClientSync(){
        if (null != connection && null != session && null != receiver) {
            try {
                log.info("Closing Consumer");
                if (ExchangeType.TOPIC == consumerConfig.getExchangeType()) {
                    if (null != receiver) {
                        TopicSubscriber topicSubscriber = (TopicSubscriber) receiver;
                        topicSubscriber.close();
                    }

                    if (null != session) {
                        TopicSession topicSession = (TopicSession) session;
                        topicSession.close();
                    }

                    if (null != connection) {
                        TopicConnection topicConnection = (TopicConnection) connection;
                        topicConnection.close();
                    }
                } else if (ExchangeType.QUEUE == consumerConfig.getExchangeType()) {
                    if (null != receiver) {
                        QueueReceiver queueReceiver = (QueueReceiver) receiver;
                        queueReceiver.close();
                    }

                    if (null != session) {
                        QueueSession queueSession = (QueueSession) session;
                        queueSession.close();
                    }

                    if (null != connection) {
                        QueueConnection queueConnection = (QueueConnection) connection;
                        queueConnection.stop();
                        queueConnection.close();
                    }
                }

                receiver = null;
                session = null;
                connection = null;

                log.info("Consumer Closed");

            } catch (JMSException e) {
                log.error("Error in stopping client.", e);
                throw new RuntimeException("Error in stopping client.", e);
            }
        }
    }

    /**
     * Un-Subscribes and closes a consumers.
     *
     * @param stopClient true if the client needs to stopped after un-subscribing, false otherwise.
     * @throws JMSException
     */
    public void unSubscribe(final boolean stopClient) throws JMSException {
        /**
         * Using a separate thread as un-subscribing the consumer on "onMessage" thread is not allowed.
         */
        Thread unsubscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != connection && null != session && null != receiver) {
                    try {
                        log.info("Un-subscribing Subscriber");
                        session.unsubscribe(consumerConfig.getSubscriptionID());
                        log.info("Subscriber Un-Subscribed");
                        if (stopClient) {
                            stopClient();
                        }

                    } catch (JMSException e) {
                        log.error("Error in removing subscription(un-subscribing).", e);
                        throw new RuntimeException("JMSException : Error in removing subscription(un-subscribing).", e);
                    }
                } else {
                    AndesClientException andesClientException =
                            new AndesClientException("The connection, session and message receiver is not assigned.");
                    log.error("The connection, session and message receiver is not assigned.", andesClientException);
                    throw new RuntimeException("The connection, session and message receiver is not assigned.", andesClientException);
                }
            }
        });

        unsubscribeThread.start();

        try {
            unsubscribeThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error waiting for consumer to unsubscribe", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            boolean interrupted = false;
            while (true) {
                Message message = this.receiver.receive();

                // We assume message receiving was interrupted if we receive null
                if (null == message) {
                    interrupted = true;
                    break;
                } else if ( processReceivedMessage(message)) {
                    break;
                }
            }
            if (!interrupted) {
                stopClientSync();
            }
        } catch (JMSException e) {
            log.error("Error while receiving messages ", e);
            throw new RuntimeException("JMSException : Error while listening to messages", e);
        } catch (IOException e) {
            log.error("Error while writing message to file", e);
            throw new RuntimeException("IOException : Error while writing message to file\"", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        try {
            boolean success = this.processReceivedMessage(message);
            if(success) {
                stopClient();
            }
        } catch (JMSException e) {
            log.error("Error while listening to messages", e);
            throw new RuntimeException("Error while listening to messages", e);
        } catch (IOException e) {
            log.error("Error while writing message to file", e);
            throw new RuntimeException("Error while listening to messages", e);
        }
    }

    /**
     * Processes the received messages. The processing includes the following actions. 1.
     * Calculation of transactions per second. 2. Calculation of  average latency for messages. 3.
     * Message detail logging 4. Writes messages to a file. 5. Writes statistics to a file. 6.
     * Closing and un-subscribing of client.
     *
     * @param message The {@link javax.jms.Message} to publish.
     * @return true if client is stopped or un-subscribed, false otherwise.
     * @throws JMSException
     * @throws IOException
     */
    private boolean processReceivedMessage(Message message)
            throws JMSException, IOException {
        if (null != message) {
            long threadID = Thread.currentThread().getId();
            // Calculating total latency
            long currentTimeStamp = System.currentTimeMillis();
            this.totalLatency = this.totalLatency + (currentTimeStamp - message.getJMSTimestamp());
            // Setting timestamps for TPS calculation
            if (0 == this.firstMessageConsumedTimestamp) {
                this.firstMessageConsumedTimestamp = currentTimeStamp;
            }
            this.lastMessageConsumedTimestamp = currentTimeStamp;

            // Incrementing message received count
            this.receivedMessageCount.incrementAndGet();
            JMSDeliveryStatus deliveryStatus;
            // Gets whether the message is original or redelivered
            if (message.getJMSRedelivered()) {
                deliveryStatus = JMSDeliveryStatus.REDELIVERED;
            } else {
                deliveryStatus = JMSDeliveryStatus.ORIGINAL;
            }
            // Logging the received message
            if (0 == this.receivedMessageCount.get() % this.consumerConfig
                    .getPrintsPerMessageCount()) {
                log.info("[RECEIVE] ThreadID:" + threadID + " Destination(" + this.consumerConfig
                        .getExchangeType().getType() + "):" +
                         this.consumerConfig.getDestinationName() + " ReceivedMessageCount:" +
                         this.receivedMessageCount + " MessageToReceive:" +
                         this.consumerConfig
                                 .getMaximumMessagesToReceived() + " Original/Redelivered:" + deliveryStatus
                                 .getStatus());

            }
            // Writes the statistics
            if (null != this.consumerConfig.getFilePathToWriteStatistics()) {
                String statisticsString = Long.toString(currentTimeStamp) + "," + Double
                        .toString(this.getConsumerTPS()) + "," + Double
                                                  .toString(this.getAverageLatency());
                AndesClientUtils.writeStatisticsToFile(statisticsString, this.consumerConfig
                        .getFilePathToWriteStatistics());
            }
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                // Writes the received messages
                if (null != this.consumerConfig.getFilePathToWriteReceivedMessages()) {
                    AndesClientUtils
                            .writeReceivedMessagesToFile(textMessage.getText(), this.consumerConfig
                                    .getFilePathToWriteReceivedMessages());
                } else {
                    this.consumerConfig.addReceivedMessage(textMessage.getText());
                }
            }

            // Acknowledges messages
            if (0 == this.receivedMessageCount.get() % this.consumerConfig
                    .getAcknowledgeAfterEachMessageCount()) {
                if (Session.CLIENT_ACKNOWLEDGE == session.getAcknowledgeMode()) {
                    message.acknowledge();
                    log.info("Acknowledging message : " + message.getJMSMessageID());
                }
            }

            if (0 == this.receivedMessageCount.get() % consumerConfig
                    .getCommitAfterEachMessageCount()) {
                // Committing session
                session.commit();
                log.info("Committed session");
            } else if (0 == this.receivedMessageCount.get() % consumerConfig
                    .getRollbackAfterEachMessageCount()) {
                // Roll-backing session
                session.rollback();
                log.info("Roll-backed session");
            } else if (0 == this.receivedMessageCount.get() % consumerConfig
                    .getRecoverAfterEachMessageCount()) {
                // recover the session
                log.info("Recovering session");
                session.recover();

            }

            if (this.receivedMessageCount.get() >= consumerConfig
                    .getUnSubscribeAfterEachMessageCount()) {
                // Un-Subscribing consumer
                unSubscribe(true);
                // Waiting till consumer is un-subscribed so that no messages will be read.
                AndesClientUtils.sleepForInterval(1000L);
                return true;
            } else if (this.receivedMessageCount.get() >= consumerConfig
                    .getMaximumMessagesToReceived()) {
                return true;
            }

            // Delaying reading of messages
            if (0 < consumerConfig.getRunningDelay()) {
                try {
                    Thread.sleep(consumerConfig.getRunningDelay());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }

    /**
     * Gets the received message count for the consumer.
     *
     * @return The received message count.
     */
    public AtomicLong getReceivedMessageCount() {
        return this.receivedMessageCount;
    }

    /**
     * Gets the consumer transactions per seconds.
     *
     * @return The consumer transactions per seconds.
     */
    public double getConsumerTPS() {
        if (0 == this.lastMessageConsumedTimestamp - this.firstMessageConsumedTimestamp) {
            return this.receivedMessageCount.doubleValue() / (1D / 1000);
        } else {
            return this.receivedMessageCount
                           .doubleValue() / (((double) (this.lastMessageConsumedTimestamp - this.firstMessageConsumedTimestamp)) / 1000D);
        }
    }

    /**
     * Gets the average latency for the consumer in receiving messages.
     *
     * @return The average latency.
     */
    public double getAverageLatency() {
        if (0 == this.receivedMessageCount.doubleValue()) {
            log.warn("No messages were received to calculate average latency.");
            return 0D;
        } else {
            return (((double) this.totalLatency) / 1000D) / this.receivedMessageCount.doubleValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AndesJMSConsumerClientConfiguration getConfig() {
        return this.consumerConfig;
    }

    /**
     * Gets the JMS message consuming connection ({@link javax.jms.Connection}).
     *
     * @return A {@link javax.jms.Connection}
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Sets the JMS message consuming connection ({@link javax.jms.Connection}).
     *
     * @param connection A {@link javax.jms.Connection}.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the JMS message consuming session ({@link javax.jms.Session}).
     *
     * @return A {@link javax.jms.Session}.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * Sets the JMS message consuming session ({@link javax.jms.Session}).
     *
     * @param session A {@link javax.jms.Session}.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Gets the JMS message consumer ({@link javax.jms.MessageConsumer}).
     *
     * @return A {@link javax.jms.MessageConsumer}.
     */
    public MessageConsumer getReceiver() {
        return this.receiver;
    }

    /**
     * Sets the JMS message consumer ({@link javax.jms.MessageConsumer}).
     *
     * @param receiver A {@link javax.jms.MessageConsumer}.
     */
    public void setReceiver(MessageConsumer receiver) {
        this.receiver = receiver;
    }
}
