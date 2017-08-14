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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enum to keep proxyservice types
 */
public enum ProxyServiceType {

    HTTP("http"), HTTPS("https"), JMS("jms");

    ProxyServiceType(String type) {
        this.type = type;
    }

    private String type;
    private static final Map<String, ProxyServiceType> ENUM_MAP;

    public String getType() {
        return type;
    }

    // Build an immutable map of String name to enum pairs.
    static {
        Map<String, ProxyServiceType> map = new ConcurrentHashMap<String, ProxyServiceType>();
        for (ProxyServiceType instance : ProxyServiceType.values()) {
            map.put(instance.getType(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static ProxyServiceType get(String type) {
        return ENUM_MAP.get(type);
    }
}
