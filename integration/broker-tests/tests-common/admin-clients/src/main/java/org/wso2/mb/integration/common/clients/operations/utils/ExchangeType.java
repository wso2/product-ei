/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
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
 * Enumeration for JMS exchange types.
 */
public enum ExchangeType {
    /**
     * Exchange types
     */
    QUEUE("queue"), TOPIC("topic");
    private String type;

    /**
     * Initializes enums
     * @param type The exchange type as string.
     */
    ExchangeType(String type) {
        this.type = type;
    }

    /**
     * Gets exchange string.
     * @return The exchange string.
     */
    public String getType() {
        return type;
    }
}
