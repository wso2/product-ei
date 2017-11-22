/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.ei.mb.integration.common.clients.operations.mqtt.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.ei.mb.integration.common.clients.MQTTConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback handler to handle message arrival, delivery complete and connection lost.
 * Keeps track of sent/received message counts.
 */
public class CallbackHandler implements MqttCallback {

    private final Log log = LogFactory.getLog(CallbackHandler.class);

    private boolean saveMessages = false;

    private final List<MqttMessage> receivedMessages = new ArrayList<MqttMessage>();
    private int receivedMessageCount;
    private int sentMessageCount;

    /**
     * Saves the topic name for which the last message received if saveMessages is set to true.
     */
    String lastTopicReceived;

    /**
     * Default constructor. Use this if you do not care about receiving message are saved or not.
     */
    public CallbackHandler() {

    }

    /**
     * Set saveMessages while initializing. Use this if you want to specifically want to save/not save receiving
     * messages.
     * <p/>
     * Do not set saveMessages to true if you are expecting a large number of messages.
     *
     * @param saveReceivingMessages Save receiving message
     */
    public CallbackHandler(boolean saveReceivingMessages) {
        this.saveMessages = saveReceivingMessages;
    }

    /**
     * Extract the received message count for the client.
     *
     * @return Received message count
     */
    public int getReceivedMessageCount() {
        return receivedMessageCount;
    }

    /**
     * Handle losing connection with the server.
     * Here we just print it to the test console.
     *
     * @param throwable Throwable connection lost
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Connection Lost - Client Disconnected", throwable);
    }

    /**
     * Handle a receiving message from a server.
     * The receiving message count will be updated. If save message flag is up, the received message will be saved.
     *
     * @param topic       Topic message received from
     * @param mqttMessage The mqtt message received
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if (null != mqttMessage) {
            if (saveMessages) {
                receivedMessages.add(mqttMessage);
                lastTopicReceived = topic;
                log.info("Message arrived on " + topic + " : " + mqttMessage.toString());
            }

            incrementReceivedMessageCount();
            int receivedMessageCount = getReceivedMessageCount();

            if (receivedMessageCount % MQTTConstants.MESSAGE_PRINT_LIMIT == 0) {
                log.info(receivedMessageCount + " messages received.");
            }
        } else {
            log.warn("Empty message received by the callback handler on topic " + topic);
        }


    }

    /**
     * Handle delivery complete ack.
     *
     * @param iMqttDeliveryToken Delivery complete token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        incrementSentMessageCount();

        int sentMessageCount = getSentMessageCount();

        if (sentMessageCount % MQTTConstants.MESSAGE_PRINT_LIMIT == 0) {
            log.info(sentMessageCount + " messages received.");
        }
    }

    /**
     * Increment the receiving message count.
     * Use this instead of just incrementing so if threads are involved this method can be used to handle concurrency
     * issues.
     */
    private void incrementReceivedMessageCount() {
        receivedMessageCount++;
    }

    /**
     * Increment the sent message count.
     * Use this instead of just incrementing so if threads are involved this method can be used to handle concurrency
     * issues.
     */
    private void incrementSentMessageCount() {
        sentMessageCount++;
    }

    /**
     * Retrieve the received messages. This will return a non empty value only if saveMessages flag is set.
     *
     * @return Received messages
     */
    public List<MqttMessage> getReceivedMessages() {
        return receivedMessages;
    }

    /**
     * Get the sent message count.
     *
     * @return Sent message count
     */
    public int getSentMessageCount() {
        return sentMessageCount;
    }


    public String getLastTopicReceived() {
        return lastTopicReceived;
    }
}
