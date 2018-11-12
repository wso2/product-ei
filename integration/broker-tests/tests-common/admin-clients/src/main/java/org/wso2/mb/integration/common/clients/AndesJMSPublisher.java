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

import org.apache.log4j.Logger;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.JMSHeaderProperty;
import org.wso2.mb.integration.common.clients.configurations.JMSHeaderPropertyType;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.JMSMessageType;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * The JMS message publisher used for creating a publisher and for publishing JMS messages.
 */
public class AndesJMSPublisher extends AndesJMSBase implements Runnable {
    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(AndesJMSPublisher.class);

    /**
     * The configuration for the publisher
     */
    private AndesJMSPublisherClientConfiguration publisherConfig;

    /**
     * The amount of messages sent by the publisher
     */
    private long sentMessageCount;

    /**
     * The timestamp at which the first message was published
     */
    private long firstMessagePublishTimestamp;

    /**
     * The timestamp at which the last message was published
     */
    private long lastMessagePublishTimestamp;

    /**
     * The connection which is used to create the JMS session
     */
    private Connection connection;

    /**
     * The session which is used to create the JMS message producer
     */
    private Session session;

    /**
     * The message producer which produces/sends messages
     */
    private MessageProducer sender;

    /**
     * Message content which is needed to be published. The value will depend on the configuration.
     */
    private String messageContent = null;

    /**
     * Creates a new JMS publisher with a given configuration.
     *
     * @param config          The configuration
     * @param createPublisher Creates connection, session and sender.
     * @throws NamingException
     * @throws JMSException
     */
    public AndesJMSPublisher(AndesJMSPublisherClientConfiguration config, boolean createPublisher)
            throws NamingException, JMSException {
        super(config);

        // Sets the configuration
        this.publisherConfig = config;

        if (null != config.getMessagesContentToSet()) {
            this.messageContent = config.getMessagesContentToSet();
        }

        // Creates a JMS connection, sessions and sender
        if (createPublisher) {
            ConnectionFactory connFactory = (ConnectionFactory) super.getInitialContext()
                    .lookup(AndesClientConstants.CF_NAME);
            connection = connFactory.createConnection();
            connection.start();
            if(config.isTransactionalSession()) {
                this.session = connection.createSession(true, 0);
            } else {
                this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            }

            Destination destination = (Destination) super.getInitialContext()
                    .lookup(this.publisherConfig.getDestinationName());
            this.sender = this.session.createProducer(destination);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startClient() throws AndesClientException, IOException {
        if (null != connection && null != session && null != sender) {
            //reading message content from file
            if (null != this.publisherConfig.getReadMessagesFromFilePath()) {
                this.getMessageContentFromFile();
            }

            Thread subscriberThread = new Thread(this);
            subscriberThread.start();
        } else {
            throw new AndesClientException("The connection, session and message sender is not assigned.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopClient() throws JMSException {
        if (null != connection && null != session && null != sender) {
            long threadID = Thread.currentThread().getId();
            log.info("Closing publisher | ThreadID : " + threadID);
            this.sender.close();
            this.session.close();
            this.connection.close();
            this.sender = null;
            this.session = null;
            this.connection = null;
            log.info("Publisher closed | ThreadID : " + threadID);
        }
    }

    /**
     * Reads message content from a file which is used as the message content to when publishing
     * messages.
     *
     * @throws IOException
     */
    public void getMessageContentFromFile() throws IOException {
        if (null != this.publisherConfig.getReadMessagesFromFilePath()) {
            BufferedReader br = new BufferedReader(new FileReader(this.publisherConfig
                                                                          .getReadMessagesFromFilePath()));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append('\n');
                    line = br.readLine();
                }

                // Remove the last appended next line since there is no next line.
                sb.replace(sb.length() - 1, sb.length() + 1, "");
                messageContent = sb.toString();
            } finally {
                br.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            Message message = null;
            long threadID = Thread.currentThread().getId();
            while (this.sentMessageCount < this.publisherConfig.getNumberOfMessagesToSend()) {
                // Creating a JMS message
                if (JMSMessageType.TEXT == this.publisherConfig.getJMSMessageType()) {
                    if ((null != this.publisherConfig.getReadMessagesFromFilePath()) || (null != this.messageContent)) {
                        message = this.session.createTextMessage(this.messageContent);
                    } else {
                        message = this.session.createTextMessage(MessageFormat
                                .format(AndesClientConstants.PUBLISH_MESSAGE_FORMAT, this.sentMessageCount, threadID));
                    }
                } else if (JMSMessageType.BYTE == this.publisherConfig.getJMSMessageType()) {
                    message = this.session.createBytesMessage();
                } else if (JMSMessageType.MAP == this.publisherConfig.getJMSMessageType()) {
                    MapMessage mapMessage = this.session.createMapMessage();
                    if (null != this.publisherConfig.getReadMessagesFromFilePath()) {
                        String[] entries = this.messageContent.split(System.getProperty("line.separator"));
                        for (int i = 0; i < entries.length; i++) {
                            mapMessage.setString("key" + i, entries[i]);
                        }
                    }
                    message = mapMessage;
                } else if (JMSMessageType.OBJECT == this.publisherConfig.getJMSMessageType()) {
                    message = this.session.createObjectMessage();
                } else if (JMSMessageType.STREAM == this.publisherConfig.getJMSMessageType()) {
                    message = this.session.createStreamMessage();
                }

                //set JMS message type
                String jmsType = publisherConfig.getJMSType();
                if(message!= null && null != jmsType && !jmsType.isEmpty()) {
                    message.setJMSType(jmsType);
                }

                //set JMS header properties
                setMessageProperties(message);

                if (null != message) {
                    this.sender.send(message, DeliveryMode.PERSISTENT, 0, this.publisherConfig
                            .getJMSMessageExpiryTime());
                    // need to commit if transactional
                    if(getConfig().isTransactionalSession()) {
                        session.commit();
                    }
                    if (message instanceof TextMessage && null != this.publisherConfig.getFilePathToWritePublishedMessages()){
                        AndesClientUtils.writePublishedMessagesToFile(((TextMessage) message)
                              .getText(), this.publisherConfig.getFilePathToWritePublishedMessages());
                    }

                    this.sentMessageCount++;

                    // TPS calculation
                    long currentTimeStamp = System.currentTimeMillis();
                    if (0 == this.firstMessagePublishTimestamp) {
                        this.firstMessagePublishTimestamp = currentTimeStamp;
                    }

                    this.lastMessagePublishTimestamp = currentTimeStamp;
                    if (0 == this.sentMessageCount % this.publisherConfig.getPrintsPerMessageCount()) {
                        // Logging the sent message details.
                        if (null != this.publisherConfig.getReadMessagesFromFilePath()) {
                            log.info("[SEND]" + " (FROM FILE) ThreadID:" +
                                     threadID + " Destination(" + this.publisherConfig
                                    .getExchangeType().getType() + "):" +
                                     this.publisherConfig
                                             .getDestinationName() + " SentMessageCount:" +
                                     this.sentMessageCount + " CountToSend:" +
                                     this.publisherConfig.getNumberOfMessagesToSend());
                        } else {
                            log.info("[SEND]" + " (INBUILT MESSAGE) ThreadID:" +
                                     threadID + " Destination(" + this.publisherConfig
                                    .getExchangeType().getType() + "):" +
                                     this.publisherConfig
                                             .getDestinationName() + " SentMessageCount:" +
                                     this.sentMessageCount + " CountToSend:" +
                                     this.publisherConfig.getNumberOfMessagesToSend());
                        }
                    }
                    // Writing statistics
                    if (null != this.publisherConfig.getFilePathToWriteStatistics()) {
                        String statisticsString =
                                ",,,," + Long.toString(currentTimeStamp) + "," + Double
                                        .toString(this.getPublisherTPS());
                        AndesClientUtils
                                .writeStatisticsToFile(statisticsString, this.publisherConfig
                                        .getFilePathToWriteStatistics());
                    }

                    // Delaying the publishing of messages
                    if (0 < this.publisherConfig.getRunningDelay()) {
                        try {
                            Thread.sleep(this.publisherConfig.getRunningDelay());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            this.stopClient();
        } catch (JMSException e) {
            throw new RuntimeException("Error while publishing messages", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while writing statistics", e);
        }
    }

    /**
     * Set JMS Headers to the message according to publisher configuration
     *
     * @param message message to set properties
     */
    private void setMessageProperties(Message message) throws JMSException {

        List<JMSHeaderProperty> headerPropertyList = publisherConfig.getJMSHeaderProperties();

        for (JMSHeaderProperty jmsHeaderProperty : headerPropertyList) {
            JMSHeaderPropertyType type = jmsHeaderProperty.getType();
            String propertyKey = jmsHeaderProperty.getKey();
            Object propertyValue = jmsHeaderProperty.getValue();
            switch (type) {
                case OBJECT:
                    message.setObjectProperty(propertyKey, propertyValue);
                    break;
                case BYTE:
                    message.setByteProperty(propertyKey, (Byte) propertyValue);
                    break;
                case BOOLEAN:
                    message.setBooleanProperty(propertyKey, (Boolean) propertyValue);
                    break;
                case DOUBLE:
                    message.setDoubleProperty(propertyKey, (Double) propertyValue);
                    break;
                case FLOAT:
                    message.setFloatProperty(propertyKey, (Float) propertyValue);
                    break;
                case SHORT:
                    message.setShortProperty(propertyKey, (Short) propertyValue);
                    break;
                case STRING:
                    message.setStringProperty(propertyKey, (String) propertyValue);
                    break;
                case INTEGER:
                    message.setIntProperty(propertyKey, (Integer) propertyValue);
                    break;
                case LONG:
                    message.setLongProperty(propertyKey, (Long) propertyValue);
                    break;
            }
        }
    }

    /**
     * Gets the published message count.
     *
     * @return The published message count.
     */
    public long getSentMessageCount() {
        return this.sentMessageCount;
    }

    /**
     * Gets the transactions per seconds for publisher.
     *
     * @return The transactions per second.
     */
    public double getPublisherTPS() {
        if (0 == this.lastMessagePublishTimestamp - this.firstMessagePublishTimestamp) {
            return ((double) this.sentMessageCount) / (1D / 1000);
        } else {
            return ((double) this.sentMessageCount) / (((double) (this.lastMessagePublishTimestamp - this.firstMessagePublishTimestamp)) / 1000);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AndesJMSPublisherClientConfiguration getConfig() {
        return this.publisherConfig;
    }

    /**
     * Gets the JMS message sending connection ({@link javax.jms.Connection}).
     *
     * @return A {@link javax.jms.Connection}
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Sets the JMS message sending connection ({@link javax.jms.Connection}).
     *
     * @param connection A {@link javax.jms.Connection}.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the JMS message sending session ({@link javax.jms.Session}).
     *
     * @return A {@link javax.jms.Session}.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * Sets the JMS message sending session ({@link javax.jms.Session}).
     *
     * @param session A {@link javax.jms.Session}.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Gets the JMS message producer ({@link javax.jms.MessageProducer}).
     *
     * @return A {@link javax.jms.MessageProducer}.
     */
    public MessageProducer getSender() {
        return this.sender;
    }

    /**
     * Sets the JMS message producer ({@link javax.jms.MessageProducer}). Suppressing
     * "UnusedDeclaration" as the client acts as a service.
     *
     * @param sender A {@link javax.jms.MessageProducer}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setSender(MessageProducer sender) {
        this.sender = sender;
    }
}
