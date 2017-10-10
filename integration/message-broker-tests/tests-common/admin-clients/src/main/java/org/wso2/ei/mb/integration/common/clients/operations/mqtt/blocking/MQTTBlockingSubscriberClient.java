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

package org.wso2.ei.mb.integration.common.clients.operations.mqtt.blocking;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.ei.mb.integration.common.clients.operations.mqtt.callback.CallbackHandler;

import java.util.List;

/**
 * Synchronous MQTT subscriber client.
 */
public class MQTTBlockingSubscriberClient extends AndesMQTTBlockingClient {

    private static final Log log = LogFactory.getLog(MQTTBlockingSubscriberClient.class);

    // Listening to the server for message or not
    private boolean subscribed = false;

    /**
     * Initialize subscribing to mqtt.
     *
     * @param configuration MQTT configurations
     * @param clientID      Unique mqtt client Id
     * @param topic         Topic to subscribe to
     * @param qos           Quality of Service
     * @param saveMessages  Save receiving messages
     * @throws MqttException
     */
    public MQTTBlockingSubscriberClient(MQTTClientConnectionConfiguration configuration, String clientID, String topic,
                                        QualityOfService qos, boolean saveMessages) throws MqttException {
        super(configuration, clientID, topic, qos, new CallbackHandler(saveMessages));
    }

    /**
     * Get all the received messages through this client.
     * Use this if want to validate message content.
     *
     * @return Received messages.
     */
    public List<MqttMessage> getReceivedMessages() {
        return getCallbackHandler().getReceivedMessages();
    }


    /**
     * Invokes client and subscribes to the given topic.
     */
    @Override
    public void run() {
        try {
            subscribe();
            subscribed = true;
        } catch (MqttException e) {
            log.error("Error subscribing to topic " + getTopic() + " from client " + getMqttClientID(), e);
        }
    }


    /**
     * Gracefully disconnect the client after un-subscribing to subscribed topic.
     * Called through ClientEngine disconnect.
     *
     * @throws MqttException
     */
    @Override
    public void disconnect() throws MqttException {
        CallbackHandler callbackHandler = getCallbackHandler();
        if (isConnected()) {
            if (null != callbackHandler) {
                log.info("No of messages received for client " + getMqttClientID() + " : " + callbackHandler
                        .getReceivedMessageCount());
            }

            // Disconnecting forcefully without un-subscribing to prevent a publisher from blocking an un-subscribe
            // process.
            mqttClient.disconnect();
            subscribed = false;

            log.info("Client " + getMqttClientID() + " Disconnected");
        }
    }

    /**
     * Check if the subscriber is subscribed to a topic
     *
     * @return Is Subscribed
     */
    public boolean isSubscribed() {
        return subscribed;
    }
}
