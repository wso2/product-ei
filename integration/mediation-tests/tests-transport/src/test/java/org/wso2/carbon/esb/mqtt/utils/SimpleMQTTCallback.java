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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mqtt.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * The MQTT client callback handler which handles message arrivals, delivery completions and connection lost.
 */
public class SimpleMQTTCallback implements MqttCallback {

    private static final Log log = LogFactory.getLog(SimpleMQTTCallback.class);
    /**
     * Inform when connection with server is lost.
     *
     * @param throwable Connection lost cause
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Mqtt client lost connection with the server", throwable);
    }
    /**
     * Inform when a message is received through a subscribed topic.
     *
     * @param topic       The topic message received from
     * @param mqttMessage The message received
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("Message arrived on topic : \"" + topic + "\" Message : \"" + mqttMessage.toString() + "\"");
    }
    /**
     * Inform when message delivery is complete for a published message.
     *
     * @param iMqttDeliveryToken The message complete token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        for (String topic : iMqttDeliveryToken.getTopics()) {
            log.info("Message delivered successfully to topic : \"" + topic + "\".");
        }
    }
}
