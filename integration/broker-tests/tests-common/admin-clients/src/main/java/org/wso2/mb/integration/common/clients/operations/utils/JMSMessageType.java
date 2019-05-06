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

package org.wso2.mb.integration.common.clients.operations.utils;

/**
 * Enumeration for JMS message types.
 */
public enum JMSMessageType {
    /**
     * JMS message types.
     */
    TEXT("text"), BYTE("byte"), MAP("map"), OBJECT("object"), STREAM("stream");
    private String type;

    /**
     * Initializes JMS message type.
     *
     * @param type Message type.
     */
    JMSMessageType(String type) {
        this.type = type;
    }

    /**
     * Gets JMS message type.
     *
     * @return The message type.
     */
    public String getType() {
        return type;
    }
}