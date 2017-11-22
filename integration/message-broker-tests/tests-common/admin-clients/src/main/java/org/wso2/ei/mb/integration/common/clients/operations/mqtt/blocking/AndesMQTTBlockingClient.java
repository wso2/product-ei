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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.clients.AndesMQTTClient;
import org.wso2.ei.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.ei.mb.integration.common.clients.operations.mqtt.callback.CallbackHandler;

/**
 * Blocking(synchronous) publish/subscribe MQTT client.
 * <p/>
 * Each blocking publisher/subscriber should extend from this.
 */
public abstract class AndesMQTTBlockingClient extends AndesMQTTClient {

    private final static Log log = LogFactory.getLog(AndesMQTTBlockingClient.class);

    protected MqttClient mqttClient;

    /**
     * Create a mqtt client initializing mqtt options.
     *
     * @param configuration   MQTT configurations
     * @param clientID        The unique client Id
     * @param topic           Topic to subscribe/publish to
     * @param qos             The quality of service
     * @param callbackHandler Callback Handler to handle receiving messages/message sending ack
     * @throws MqttException
     */
    public AndesMQTTBlockingClient(MQTTClientConnectionConfiguration configuration, String clientID, String topic,
                                   QualityOfService qos, CallbackHandler callbackHandler) throws MqttException {
        super(configuration, clientID, topic, qos, callbackHandler);

        // Construct MQTT client
        mqttClient = new MqttClient(this.brokerUrl, clientID, dataStore);

        // Connect to the MQTT server
        connect();

        mqttClient.setCallback(callbackHandler);
    }

    /**
     * Publish message to broker using mqtt synchronously.
     *
     * @param payload      Data to send
     * @param noOfMessages Number of message to send
     * @throws MqttException
     */
    protected void publish(byte[] payload, int noOfMessages) throws MqttException {
        log.info("Publishing to topic : " + topic + " on qos : " + qos);

        if (null != payload) {

            // Create and configure message
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos.getValue());
            message.setRetained(retain);

            for (int i = 0; i < noOfMessages; i++) {
                // Send message to server, control is either returned or blocked until it has been delivered to the
                // server depending on the MqttClient type (Blocking/Async)meeting the specified quality of service.
                mqttClient.publish(topic, message);
            }
        }
    }

    /**
     * Subscribe to the requested topic in a synchronous manner.
     *
     * @throws MqttException
     */
    public void subscribe() throws MqttException {
        log.info("Subscribing to topic \"" + topic + "\" qos " + qos);
        mqttClient.subscribe(topic, qos.getValue());

        //Will need to wait to receive all messages - subscriber closes on disconnect
    }

    /**
     * Synchronously subscribe to a given topic.
     *
     * @param topicName The topic to subscribe to
     * @throws MqttException
     */
    @Override
    public void subscribe(String topicName) throws MqttException {
        log.info("Subscribing to topic \"" + topicName + "\" on qos" + qos);
        mqttClient.subscribe(topicName, qos.getValue());
    }

    /**
     * Un-subscribe from the topic.
     *
     * @throws MqttException
     */
    public void unsubscribe() throws MqttException {
        mqttClient.unsubscribe(topic);
        log.info("Subscriber for topic : " + topic + " un-subscribed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(String topic) throws MqttException {
        mqttClient.unsubscribe(topic);
        log.info("Subscriber for topic : " + topic + " un-subscribed");
    }

    /**
     * Shutdown the mqtt client. Call this whenever the system exits, test cases are finished or disconnect hook is
     * called.
     *
     * @throws MqttException
     */
    public void disconnect() throws MqttException {
        if (isConnected()) {
            mqttClient.disconnect();
            log.info("Client " + mqttClientID + " Disconnected");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws MqttException {
        log.info("Connecting to " + brokerUrl + " with client ID " + mqttClientID);
        mqttClient.connect(connectionOptions);

        log.info("Client " + mqttClientID + " Connected");
    }

    /**
     * Use this to validate if connection to server is still active.
     *
     * @return Is MQTT client connected to the server
     */
    public boolean isConnected() {
        return mqttClient.isConnected();
    }

}
