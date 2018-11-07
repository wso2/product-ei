/*
 *
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.mb.integration.common.clients.configurations;

/**
 * This class represents configuration of a JMS Header property. JMS sender reads them and set
 * when sending a JMS message to the provider
 */
public class JMSHeaderProperty {

    private String key;

    private Object value;

    private JMSHeaderPropertyType type;

    /**
     * Create a new JMSHeaderProperty.
     *
     * @param key   key of the header to be set to message
     * @param value value of the header to be set to message
     * @param type  type of the header to be set to message
     */
    public JMSHeaderProperty(String key, Object value, JMSHeaderPropertyType type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    /**
     * Get Key of the header property
     *
     * @return key as a string
     */
    public String getKey() {
        return key;
    }

    /**
     * Get value of the header property
     *
     * @return value as a Object
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get property of the header property. Long, String, Integer etc
     *
     * @return Property type
     */
    public JMSHeaderPropertyType getType() {
        return type;
    }
}
