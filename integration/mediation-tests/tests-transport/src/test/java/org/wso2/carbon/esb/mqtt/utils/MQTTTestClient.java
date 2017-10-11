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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Class representing MQTT client
 */
public class MQTTTestClient {

    private static final Log log = LogFactory.getLog(MQTTTestClient.class);

    // Java temporary directory location
    private static final String JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");
    // The MQTT broker URL
    private String brokerURL = "tcp://localhost:1883";
    //Paho MQTT client
    MqttClient mqttClient;


    /**
     * Generate a MQTT client with given parameters
     *
     * @param brokerURL url of MQTT provider
     * @param userName username to connect to MQTT provider
     * @param password password to connect to MQTT provider
     * @param clientId unique id for the publisher/subscriber client
     * @throws MqttException in case of issue of connect/publish/consume
     */
    public MQTTTestClient(String brokerURL, String userName, char[] password, String clientId) throws MqttException {
        this.brokerURL = brokerURL;
        //Store messages until server fetches them
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(JAVA_TMP_DIR + "/" + clientId);
        mqttClient = new MqttClient(brokerURL, clientId, dataStore);
        SimpleMQTTCallback callback = new SimpleMQTTCallback();
        mqttClient.setCallback(callback);
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(userName);
        connectOptions.setPassword(password);
        connectOptions.setCleanSession(true);
        mqttClient.connect(connectOptions);

        log.info("MQTTTestClient successfully connected to broker at " + this.brokerURL);
    }

    public void publishMessage(String topic, byte[] payload, int qosLevel, boolean retained) throws MqttException {
        mqttClient.publish(topic,payload, qosLevel, retained);
    }

    public void subscribe(String topic, int qosLevel) throws MqttException {
        mqttClient.subscribe(topic, qosLevel);
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
    }
}
