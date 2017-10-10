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

package org.wso2.ei.mb.integration.common.clients;

/**
 * The configurations that needs to be passed to MQTT client.
 */
public class MQTTClientConnectionConfiguration {

    /**
     * The protocol used by the broker.
     * tcp, udp .etc
     */
    private String brokerProtocol = null;

    /**
     * Broker host address.
     * eg :- 127.0.0.1
     */
    private String brokerHost = null;

    /**
     * Broker working port.
     * eg :- 1883
     */
    private String brokerPort = null;

    /**
     * The password to connect to the broker.
     */
    private String brokerPassword = null;

    /**
     * The username to connect to the broker.
     */
    private String brokerUserName = null;

    /**
     * MQTT retain parameter
     *
     * When retain enabled with published topic message, it should retained for future subscribers
     * for the same topic.
     */
    private boolean retain = false;

    /**
     * MQTT clean session parameter.
     * <p/>
     * When a client is connected to a broker, and if it has been previously connected and that session information
     * is available in the broker, clean session = true will discard previous session information.
     */
    private boolean cleanSession = false;

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public String getBrokerProtocol() {
        return brokerProtocol;
    }

    public void setBrokerProtocol(String brokerProtocol) {
        this.brokerProtocol = brokerProtocol;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public String getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(String brokerPort) {
        this.brokerPort = brokerPort;
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    public String getBrokerUserName() {
        return brokerUserName;
    }

    public void setBrokerUserName(String brokerUserName) {
        this.brokerUserName = brokerUserName;
    }

    /**
     * Generate the broker URL using the given configurations.
     *
     * @return The broker URL
     */
    public String getBrokerURL() {
        return brokerProtocol + "://" + brokerHost + ":" + brokerPort;
    }
}
