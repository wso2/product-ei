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
 * {@code Element} enumeration keeps mule elements against its representative internal class
 */
public enum Element {

    HTTP_LISTENER_CONFIG("http:listener-config", "org.wso2.ei.tools.mule2ballerina.model" + ".HttpListenerConfig"),
    HTTP_LISTENER("http:listener", "org.wso2.ei.tools.mule2ballerina.model.HttpListener"),
    FLOW("flow", "org.wso2.ei.tools.mule2ballerina.model.Flow"),
    SET_PAYLOAD("set-payload", "org.wso2.ei.tools.mule2ballerina.model.Payload"),
    HTTP_REQUEST("http:request", "org.wso2.ei.tools.mule2ballerina.model.HttpRequest"),
    HTTP_REQUEST_CONFIG("http:request-config", "org.wso2.ei.tools.mule2ballerina.model.HttpRequestConfig"),
    LOGGER("logger", "org.wso2.ei.tools.mule2ballerina.model.Logger"),
    PROPERTY_SETTER("set-property", "org.wso2.ei.tools.mule2ballerina.model.PropertySetter"),
    PROPERTY_REMOVER("remove-property", "org.wso2.ei.tools.mule2ballerina.model.PropertyRemover"),
    VARIABLE_SETTER("set-variable", "org.wso2.ei.tools.mule2ballerina.model.VariableSetter"),
    VARIABLE_REMOVER("remove-variable", "org.wso2.ei.tools.mule2ballerina.model.VariableRemover"),
    FLOW_REF("flow-ref", "org.wso2.ei.tools.mule2ballerina.model.FlowReference"),
    SUB_FLOW("sub-flow", "org.wso2.ei.tools.mule2ballerina.model.SubFlow"),
    DB_SELECT("db:select", "org.wso2.ei.tools.mule2ballerina.model.Database"),
    DB_CONFIG("db:mysql-config", "org.wso2.ei.tools.mule2ballerina.model.DatabaseConfig"),
    ASYNC("async", "org.wso2.ei.tools.mule2ballerina.model.AsynchronousTask");

    private String value;
    private String internalClassName;

    Element(final String value, final String intermediateMClassname) {
        this.value = value;
        this.internalClassName = intermediateMClassname;
    }

    public String getValue() {
        return value;
    }

    public String getInternalClassName() {
        return internalClassName;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}
