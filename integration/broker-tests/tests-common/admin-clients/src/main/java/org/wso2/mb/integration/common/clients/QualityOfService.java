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

package org.wso2.mb.integration.common.clients;

/**
 * The quality of service levels in MQTT.
 */
public enum QualityOfService {

    /**
     * The message is delivered at most once, or it may not be delivered at all. Its delivery across the network is
     * not acknowledged. The message is not stored. The message could be lost if the client is disconnected,
     * or if the server fails. QoS0 is the fastest mode of transfer. It is sometimes called "fire and forget".
     */
    MOST_ONCE(0),

    /**
     * The message is always delivered at least once. It might be delivered multiple times if there is a failure
     * before an acknowledgment is received by the sender. The message must be stored locally at the sender,
     * until the sender receives confirmation that the message has been published by the receiver. The message is
     * stored in case the message must be sent again.
     */
    LEAST_ONCE(1),

    /**
     * The message is always delivered exactly once. The message must be stored locally at the sender,
     * until the sender receives confirmation that the message has been published by the receiver. The message is
     * stored in case the message must be sent again. QoS2 is the safest, but slowest mode of transfer. A more
     * sophisticated handshaking and acknowledgement sequence is used than for QoS1 to ensure no duplication of
     * messages occurs.
     */
    EXACTLY_ONCE(2);

    private final int qos;

    /**
     * Initialize with the given Quality of Service.
     *
     * @param qos The quality of service level
     */
    private QualityOfService(int qos) {
        this.qos = qos;
    }

    /**
     * Get the corresponding value for the given quality of service.
     * Retrieve this value whenever quality of service level needs to feed into external libraries.
     *
     * @return The integer representation of this quality of service
     */
    public int getValue() {
        return qos;
    }
}