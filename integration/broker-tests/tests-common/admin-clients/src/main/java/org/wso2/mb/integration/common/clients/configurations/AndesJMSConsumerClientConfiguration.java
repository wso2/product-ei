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
package org.wso2.mb.integration.common.clients.configurations;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Andes client consumer configuration. The class contains properties
 * related to JMS message consuming.
 */
public class AndesJMSConsumerClientConfiguration extends AndesJMSClientConfiguration {
    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Log log = LogFactory.getLog(AndesJMSConsumerClientConfiguration.class);

    /**
     * Message count at which the consumer un-subscribes.
     */
    private long unSubscribeAfterEachMessageCount = Long.MAX_VALUE;

    /**
     * Message count at which the session is rolled back.
     */
    private long rollbackAfterEachMessageCount = Long.MAX_VALUE;

    /**
     * Message count at which the session is recovered.
     */
    private long recoverAfterEachMessageCount = Long.MAX_VALUE;

    /**
     * Message count at which the session is committed.
     */
    private long commitAfterEachMessageCount = Long.MAX_VALUE;

    /**
     * Message count at which a message is acknowledge.
     */
    private long acknowledgeAfterEachMessageCount = Long.MAX_VALUE;

    /**
     * The file path to write received messages.
     */
    private String filePathToWriteReceivedMessages = null;

    /**
     * Maximum messages to receiver.
     */
    private long maximumMessagesToReceived = Long.MAX_VALUE;

    /**
     * Subscription ID for durable topics.
     */
    private String subscriptionID = null;

    /**
     * Whether the subscriber is durable.
     */
    private boolean durable = false;

    /**
     * The acknowledge mode for messages.
     */
    private JMSAcknowledgeMode acknowledgeMode = JMSAcknowledgeMode.AUTO_ACKNOWLEDGE;

    /**
     * Whether the consumer is asynchronously reading messages. Asynchronous message reading implies
     * that it uses {@link javax.jms.MessageListener} to listen to receiving messages. Synchronous
     * message reading will use a while loop inside a thread.
     */
    private boolean async = false;

    /**
     * JMS selectors string for filtering.
     */
    private String selectors = null;

    /**
     * Message contents of received messages, as strings.
     */
    private List<String> receivedMessages = new ArrayList<>();

    /**
     * Creates a consumer configuration with default values.
     */
    public AndesJMSConsumerClientConfiguration() {
        super();
    }

    /**
     * Creates a consumer with a given exchange type and destination with default connection string.
     *
     * @param exchangeType    The exchange type.
     * @param destinationName The destination name.
     */
    public AndesJMSConsumerClientConfiguration(
            ExchangeType exchangeType, String destinationName) {
        super(exchangeType, destinationName);
    }

    /**
     * Creates a consumer with a given host name, port for connection string and exchange type and
     * destination name.
     *
     * @param hostName        The host name for connection string.
     * @param port            The port for the connection string.
     * @param exchangeType    The exchange type.
     * @param destinationName The destination name.
     */
    public AndesJMSConsumerClientConfiguration(String hostName, int port,
                                               ExchangeType exchangeType,
                                               String destinationName) {
        super(hostName, port, exchangeType, destinationName);
    }

    /**
     * Creates a consumer with a given port for connection string and exchange type and
     * destination name.
     *
     * @param port            The port for the connection string.
     * @param exchangeType    The exchange type.
     * @param destinationName The destination name.
     */
    public AndesJMSConsumerClientConfiguration( int port,
                                               ExchangeType exchangeType,
                                               String destinationName) {
        super(port, exchangeType, destinationName);
    }


    /**
     * Creates a consumer with a given username, password, for connection
     * string and exchange type and destination name.
     *
     * @param userName        The user name for the connection string.
     * @param password        The password for the connection string.
     * @param exchangeType    The exchange type.
     * @param destinationName The destination name.
     */
    public AndesJMSConsumerClientConfiguration(String userName, String password,
                                               ExchangeType exchangeType,
                                               String destinationName) {
        super(userName, password, exchangeType, destinationName);
    }

    /**
     * Creates a consumer with a given port, username, password, for connection
     * string and exchange type and destination name.
     *
     * @param userName        The user name for the connection string.
     * @param password        The password for the connection string.
     * @param exchangeType    The exchange type.
     * @param destinationName The destination name.
     */
    public AndesJMSConsumerClientConfiguration(int port, String userName, String password,
                                               ExchangeType exchangeType,
                                               String destinationName) {
        super(port, userName, password, exchangeType, destinationName);
    }


    /**
     * Creates a consumer with a given user name, password, host name, port for connection
     * string and exchange type and destination name.
     *
     * @param userName        The user name for the connection string.
     * @param password        The password for the connection string.
     * @param hostName        The host name for the connection string.
     * @param port            The port for the connection string.
     * @param exchangeType    The exchange type.
     * @param destinationName The destination name.
     */
    public AndesJMSConsumerClientConfiguration(String userName, String password,
                                               String hostName, int port,
                                               ExchangeType exchangeType,
                                               String destinationName) {
        super(userName, password, hostName, port, exchangeType, destinationName);
    }

    /**
     * Creates a configuration for a consumer using an xml file.
     *
     * @param xmlConfigFilePath The file path for the xml configuration file path.
     * @throws AndesClientConfigurationException
     */
    public AndesJMSConsumerClientConfiguration(String xmlConfigFilePath)
            throws AndesClientConfigurationException {
        super(xmlConfigFilePath);
        try {
            XMLConfiguration config = new XMLConfiguration(xmlConfigFilePath);

            this.unSubscribeAfterEachMessageCount = config.getLong("base.consumer.unSubscribeAfterEachMessageCount", Long.MAX_VALUE);
            this.rollbackAfterEachMessageCount = config.getLong("base.consumer.rollbackAfterEachMessageCount", Long.MAX_VALUE);
            this.recoverAfterEachMessageCount = config.getLong("base.consumer.recoverAfterEachMessageCount", Long.MAX_VALUE);
            this.commitAfterEachMessageCount = config.getLong("base.consumer.commitAfterEachMessageCount", Long.MAX_VALUE);
            this.acknowledgeAfterEachMessageCount = config.getLong("base.consumer.acknowledgeAfterEachMessageCount", Long.MAX_VALUE);
            this.filePathToWriteReceivedMessages = config.getString("base.consumer.filePathToWriteReceivedMessages", null);
            this.maximumMessagesToReceived = config.getLong("base.consumer.maximumMessagesToReceived", Long.MAX_VALUE);
            this.subscriptionID = config.getString("base.consumer.subscriptionID", null);
            this.durable = config.getBoolean("base.consumer.durable", false);
            this.async = config.getBoolean("base.consumer.async", true);
            this.selectors = config.getString("base.consumer.selectors", null);
            this.acknowledgeMode = JMSAcknowledgeMode.valueOf(config.getString("base.consumer.acknowledgeMode", "AUTO_ACKNOWLEDGE"));
        } catch (ConfigurationException e) {
            throw new AndesClientConfigurationException("Error in reading xml configuration file. Make sure the file exists.", e);
        } catch (IllegalArgumentException e) {
            throw new AndesClientConfigurationException("Invalid acknowledge mode used. Use a value either of the values : SESSION_TRANSACTED, AUTO_ACKNOWLEDGE, CLIENT_ACKNOWLEDGE, DUPS_OK_ACKNOWLEDGE", e);
        }
    }

    /**
     * The copy constructor for the JMS base class. This will copy all attributes defined in another
     * configuration only for the base client.
     *
     * @param config The configuration to be copied.
     */
    public AndesJMSConsumerClientConfiguration(
            AndesJMSClientConfiguration config) {
        super(config);
    }

    /**
     * Creates a JMS message consumer with a given AMQP transport connection string for SSL and a
     * given exchange type and destination.
     *
     * @param userName           The user name for the connection string.
     * @param password           The password for the connection string.
     * @param hostName           The host name for the connection string.
     * @param port               The port for the connection string.
     * @param exchangeType       The exchange type.
     * @param destinationName    The destination name.
     * @param sslAlias           The ssl alias to use in ssl connection.
     * @param trustStorePath     The file path for trust store to use in ssl connection.
     * @param trustStorePassword The trust store password to use in ssl connection.
     * @param keyStorePath       The file path for key store to use in ssl connection.
     * @param keyStorePassword   The key store password to use in ssl connection.
     */
    public AndesJMSConsumerClientConfiguration(String userName, String password, String hostName,
                                               int port,
                                               ExchangeType exchangeType, String destinationName,
                                               String sslAlias, String trustStorePath,
                                               String trustStorePassword, String keyStorePath,
                                               String keyStorePassword) {
        super(userName, password, hostName, port, exchangeType, destinationName, sslAlias,
              trustStorePath, trustStorePassword, keyStorePath, keyStorePassword);

    }

    /**
     * Gets the message count to un-subscribe the consumer.
     *
     * @return The message count to un-subscribe the consumer.
     */
    public long getUnSubscribeAfterEachMessageCount() {
        return unSubscribeAfterEachMessageCount;
    }

    /**
     * Sets message count to un-subscribe the consumer.
     *
     * @param unSubscribeAfterEachMessageCount The message count to un-subscribe the consumer.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setUnSubscribeAfterEachMessageCount(long unSubscribeAfterEachMessageCount)
            throws AndesClientConfigurationException {
        if (0 < unSubscribeAfterEachMessageCount) {
            this.unSubscribeAfterEachMessageCount = unSubscribeAfterEachMessageCount;
        } else {
            throw new AndesClientConfigurationException("Value cannot be less than 0");
        }
    }

    /**
     * Gets the message count at which the session should be rolled-back.
     *
     * @return The message count at which the session should be rolled-back.
     */
    public long getRollbackAfterEachMessageCount() {
        return rollbackAfterEachMessageCount;
    }

    /**
     * Gets the message count at which the session should be recovered.
     *
     * @return The message count at which the session should be recovered.
     */
    public long getRecoverAfterEachMessageCount() {
        return recoverAfterEachMessageCount;
    }

    /**
     * Sets message count at which the session should be rolled-back.
     *
     * @param rollbackAfterEachMessageCount The message count at which the session should be
     *                                      rolled-back.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setRollbackAfterEachMessageCount(long rollbackAfterEachMessageCount)
            throws AndesClientConfigurationException {
        if (0 < rollbackAfterEachMessageCount) {
            this.rollbackAfterEachMessageCount = rollbackAfterEachMessageCount;
        } else {
            throw new AndesClientConfigurationException("Value cannot be less than 0");
        }
    }

    /**
     * Sets message count at which the session should be recovered.
     *
     * @param recoverAfterEachMessageCount The message count at which the session should be recovered.
     * @throws AndesClientConfigurationException on in-applicable value passed
     */
    public void setRecoverAfterEachMessageCount(long recoverAfterEachMessageCount)
            throws AndesClientConfigurationException {
        if(0 < recoverAfterEachMessageCount) {
            this.recoverAfterEachMessageCount = recoverAfterEachMessageCount;
        } else {
            throw new AndesClientConfigurationException("Value cannot be less than 0");
        }
    }

    /**
     * Gets the message count at which the session should be committed.
     *
     * @return The message count at which the session should be committed.
     */
    public long getCommitAfterEachMessageCount() {
        return commitAfterEachMessageCount;
    }

    /**
     * Sets the message count at which the session should be committed.
     *
     * @param commitAfterEachMessageCount The message count at which the session should be
     *                                    committed.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setCommitAfterEachMessageCount(long commitAfterEachMessageCount)
            throws AndesClientConfigurationException {
        if (0 < commitAfterEachMessageCount) {
            this.commitAfterEachMessageCount = commitAfterEachMessageCount;
        } else {
            throw new AndesClientConfigurationException("Value cannot be less than 0");
        }
    }

    /**
     * Gets the message count at which a message should be acknowledged after.
     *
     * @return The the message count at which a message should be acknowledged after.
     */
    public long getAcknowledgeAfterEachMessageCount() {
        return acknowledgeAfterEachMessageCount;
    }

    /**
     * Sets the message count at which a message should be acknowledged after.
     *
     * @param acknowledgeAfterEachMessageCount The the message count at which a message should be
     *                                         acknowledged after.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setAcknowledgeAfterEachMessageCount(long acknowledgeAfterEachMessageCount)
            throws AndesClientConfigurationException {
        if (0 < acknowledgeAfterEachMessageCount) {
            this.acknowledgeAfterEachMessageCount = acknowledgeAfterEachMessageCount;
        } else {
            throw new AndesClientConfigurationException("Value cannot be less than 0");
        }
    }

    /**
     * Gets the file path where the received messages should be written to,
     *
     * @return The file path where the received messages should be written to,
     */
    public String getFilePathToWriteReceivedMessages() {
        return filePathToWriteReceivedMessages;
    }

    /**
     * Sets the file path where the received messages should be written to,
     *
     * @param filePathToWriteReceivedMessages The file path where the received messages should be
     *                                        written to,
     */
    public void setFilePathToWriteReceivedMessages(String filePathToWriteReceivedMessages) {
        this.filePathToWriteReceivedMessages = filePathToWriteReceivedMessages;
    }

    /**
     * Gets the maximum number of messages to received.
     *
     * @return The maximum number of messages to received.
     */
    public long getMaximumMessagesToReceived() {
        return this.maximumMessagesToReceived;
    }

    /**
     * Sets the maximum number of messages to received.
     *
     * @param maximumMessagesToReceived The maximum number of messages to received.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setMaximumMessagesToReceived(long maximumMessagesToReceived)
            throws AndesClientConfigurationException {
        if (0 < maximumMessagesToReceived) {
            this.maximumMessagesToReceived = maximumMessagesToReceived;
        } else {
            throw new AndesClientConfigurationException("The maximum number of messages to receive " +
                                                        "cannot be less than 1");
        }
    }

    /**
     * Gets the subscription ID.
     *
     * @return The subscription ID.
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }

    /**
     * Sets the subscription ID
     *
     * @param subscriptionID The subscription ID
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setSubscriptionID(String subscriptionID) throws AndesClientConfigurationException {
        if (this.durable) {
            if (StringUtils.isNotEmpty(subscriptionID)) {
                this.subscriptionID = subscriptionID;
            } else {
                throw new AndesClientConfigurationException("Subscription ID cannot be null or empty " +
                                                            "for an durable topic");
            }
        } else {
            this.subscriptionID = subscriptionID;
            log.warn("Setting subscription ID for non-durable topics. Subscription ID is not " +
                     "necessary for non-durable topics or queues");
        }
    }

    /**
     * Checks whether the subscriber/consumer is durable.
     *
     * @return true if subscriber/consumer is durable, false otherwise.
     */
    public boolean isDurable() {
        return durable;
    }

    /**
     * Sets values for a durable subscription
     *
     * @param durable        True if subscription is durable, false otherwise.
     * @param subscriptionID The subscription ID.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setDurable(boolean durable, String subscriptionID) throws
                                                                   AndesClientConfigurationException {
        if (durable) {
            if (StringUtils.isNotEmpty(subscriptionID)) {
                this.subscriptionID = subscriptionID;
            } else {
                throw new AndesClientConfigurationException("Subscription ID cannot be null or empty " +
                                                            "for an durable topic");
            }
        }

        this.durable = durable;
    }

    /**
     * Gets acknowledge mode for messages.
     *
     * @return The acknowledge mode for messages.
     */
    public JMSAcknowledgeMode getAcknowledgeMode() {
        return acknowledgeMode;
    }

    /**
     * Sets acknowledge mode for messages.
     *
     * @param acknowledgeMode The acknowledge mode for messages.
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     */
    public void setAcknowledgeMode(JMSAcknowledgeMode acknowledgeMode)
            throws AndesClientConfigurationException {
        this.acknowledgeMode = acknowledgeMode;
    }

    /**
     * Checks whether consumer is asynchronously reading messages.
     *
     * @return true if messages are read asynchronously, false otherwise. Asynchronously message
     * reading implies that it uses {@link javax.jms.MessageListener} to listen to receiving
     * messages.
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * Sets the consumer to read message asynchronously. Asynchronously message
     * reading implies that it uses {@link javax.jms.MessageListener} to listen to receiving
     * messages.
     * Suppressing "UnusedDeclaration" as this is a configuration
     *
     * @param async true if messages should be read asynchronously, false otherwise.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setAsync(boolean async) {
        this.async = async;
    }

    /**
     * Gets the selectors query used by the consumer for filtering.
     *
     * @return The selectors query used by the consumer for filtering.
     */
    public String getSelectors() {
        return selectors;
    }

    /**
     * Sets the selectors query used by the consumer for filtering.
     *
     * @param selectors The selectors query used by the consumer for filtering.
     */
    public void setSelectors(String selectors) {
        this.selectors = selectors;
    }

    /**
     * This method returns received messages for this consumer. This is not valid when write to files before
     * compare message contents.
     *
     * @return Received message contents.
     */
    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    /**
     * Adding the message content to the received messages list, after receiving the message, for this consumer. This
     * is not valid when write to files before compare message contents.
     */
    public void addReceivedMessage(String receivedMessages) {
        this.receivedMessages.add(receivedMessages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() +
               "UnSubscribeAfterEachMessageCount=" + this.unSubscribeAfterEachMessageCount + "\n" +
               "RollbackAfterEachMessageCount=" + this.rollbackAfterEachMessageCount + "\n" +
                "RecoverAfterEachMessageCount=" + this.recoverAfterEachMessageCount + "\n" +
               "CommitAfterEachMessageCount=" + this.commitAfterEachMessageCount + "\n" +
               "AcknowledgeAfterEachMessageCount=" + this.acknowledgeAfterEachMessageCount + "\n" +
               "FilePathToWriteReceivedMessages=" + this.filePathToWriteReceivedMessages + "\n" +
               "ReceivedMessages=" + this.receivedMessages + "\n" +
               "MaximumMessagesToReceived=" + this.maximumMessagesToReceived + "\n" +
               "SubscriptionID=" + this.subscriptionID + "\n" +
               "Durable=" + this.durable + "\n" +
               "AcknowledgeMode=" + this.acknowledgeMode + "\n" +
               "Async=" + this.async + "\n" +
               "Selectors=" + this.selectors + "\n";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AndesJMSConsumerClientConfiguration clone() throws CloneNotSupportedException {
        return (AndesJMSConsumerClientConfiguration) super.clone();
    }
}
