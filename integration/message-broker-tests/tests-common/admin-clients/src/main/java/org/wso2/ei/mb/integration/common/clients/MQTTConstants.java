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
 * Constants for MQTT tests.
 */
public class MQTTConstants {

    public static final String BROKER_PROTOCOL = "tcp";
    public static final String BROKER_HOST = "localhost";
    public static final String BROKER_PORT = "1883";
    public static final String BROKER_PASSWORD = "admin";
    public static final String BROKER_USER_NAME = "admin";

    public static final byte[] TEMPLATE_PAYLOAD = "hello".getBytes();
    public static final byte[] EMPTY_PAYLOAD = "".getBytes();

    /**
     * Maximum length of a MQTT client Id defined by MQTT 3.1 specifications
     */
    public static final int CLIENT_ID_LENGTH = 23;

    // Print message send/receive details on each 1000 messages
    public static final int MESSAGE_PRINT_LIMIT = 1000;

    /***
     * Timeout for an MQTT client to connect to the broker.
     */
    public static final long CLIENT_CONNECT_TIMEOUT = 1000;
}
