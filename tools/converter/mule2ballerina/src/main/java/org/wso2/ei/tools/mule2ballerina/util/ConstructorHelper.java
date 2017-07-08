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

package org.wso2.ei.tools.mule2ballerina.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  {@code ConstructorHelper} This class maintains the values that needs to be passed to constructors while building
 *  intermediate object
 *  stack.
 */
public enum ConstructorHelper {

    DB_SELECT("db:select", "select"),
    DB_CONFIG("db:mysql-config", "mysql");

    private String elementName;
    private String value;
    private static final Map<String, String> ENUM_MAP;

    public String getElementName() {
        return elementName;
    }

    public String getValue() {
        return value;
    }

    ConstructorHelper(String elementName, String value) {
        this.value = value;
        this.elementName = elementName;
    }

    // Build an immutable map of String name to enum pairs.
    static {
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        for (ConstructorHelper instance : ConstructorHelper.values()) {
            map.put(instance.getElementName(), instance.getValue());
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static String get(String elementName) {
        return ENUM_MAP.get(elementName);
    }
}
