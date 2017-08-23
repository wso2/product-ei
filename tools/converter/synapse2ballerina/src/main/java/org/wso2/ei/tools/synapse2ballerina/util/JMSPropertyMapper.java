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

package org.wso2.ei.tools.synapse2ballerina.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Keep Synapse jms property names against ballerina jms property keys.
 */
public enum JMSPropertyMapper {

    CONTEXT_FACTORY("java.naming.factory.initial", "initialContextFactory"),
    PROVIDER_URL("java.naming.provider.url", "providerUrl"),
    CONNECTION_FACTORY("transport.jms.ConnectionFactoryJNDIName", "connectionFactoryName"),
    DESTINATION_TYPE("transport.jms.DestinationType", "connectionFactoryType");

    private String propertyName; //synapse jms property name
    private String ballerinaKey; // ballerina jms key
    private static final Map<String, String> ENUM_MAP; //Keep ballerina keys against matching synapse jms properties

    JMSPropertyMapper(String propertyName, String ballerinaKey) {
        this.propertyName = propertyName;
        this.ballerinaKey = ballerinaKey;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getBallerinaKey() {
        return ballerinaKey;
    }

    public static Map<String, String> getEnumMap() {
        return ENUM_MAP;
    }

    static {
        Map<String, String> map = new HashMap<String, String>();
        for (JMSPropertyMapper propertyMapper : JMSPropertyMapper.values()) {
            map.put(propertyMapper.getPropertyName(), propertyMapper.getBallerinaKey());
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

}
