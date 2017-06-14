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

package org.wso2.ei.tools.mule2ballerina.elementmapper;

/**
 * {@code Attribute} enumeration holds the list of attributes that belong to mule elements
 */
public enum Attribute {

    NAME("name", "name"),
    HOST("host", "host"),
    PORT("port", "port"),
    BASE_PATH("basePath", "basePath"),
    CONFIG_REF("config-ref", "configRef"),
    PATH("path", "path"),
    ALLOWED_MOTHODS("allowedMethods", "allowedMethods"),
    VALUE("value", "value"),
    METHOD("method", "method"),
    PROTOCOL("protocol", "protocol"),
    LEVEL("level", "level"),
    MESSAGE("message", "message"),
    MIME_TYPE("mimeType", "mimeType"),
    PROPERTY_NAME("propertyName", "propertyName"),
    VARIABLE_NAME("variableName", "variableName");

    private String attribute;
    private String property;

    Attribute(final String attribute, final String property) {
        this.property = property;
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public String toString() {
        return this.getProperty();
    }
}
