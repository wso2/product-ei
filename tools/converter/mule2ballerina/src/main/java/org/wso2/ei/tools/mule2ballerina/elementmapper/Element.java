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
 * {@code Element} enumeration keeps mule elements against internal mclasses
 */
public enum Element {

    MULE_HTTP_LISTENER_CONFIG("http:listener-config", "org.wso2.ei.tools.mule2ballerina.model" + ".HttpListenerConfig"),
    MULE_HTTP_LISTENER("http:listener", "org.wso2.ei.tools.mule2ballerina.model.HttpListener"),
    MULE_FLOW("flow", "org.wso2.ei.tools.mule2ballerina.model.Flow"),
    MULE_SET_PAYLOAD("set-payload", "org.wso2.ei.tools.mule2ballerina.model.Payload"),
    MULE_HTTP_REQUEST("http:request", "org.wso2.ei.tools.mule2ballerina.model.HttpRequest"),
    MULE_HTTP_REQUEST_CONFIG("http:request-config", "org.wso2.ei.tools.mule2ballerina.model.HttpRequestConfig"),
    MULE_LOGGER("logger", "org.wso2.ei.tools.mule2ballerina.model.Logger"),
    MULE_PROPERTY_SETTER("set-property", "org.wso2.ei.tools.mule2ballerina.model.PropertySetter"),
    MULE_PROPERTY_REMOVER("remove-property", "org.wso2.ei.tools.mule2ballerina.model.PropertyRemover"),
    MULE_VARIABLE_SETTER("set-variable", "org.wso2.ei.tools.mule2ballerina.model.VariableSetter"),
    MULE_VARIABLE_REMOVER("remove-variable", "org.wso2.ei.tools.mule2ballerina.model.VariableRemover"),
    MULE_FLOW_REF("flow-ref", "org.wso2.ei.tools.mule2ballerina.model.FlowReference"),
    MULE_SUB_FLOW("sub-flow", "org.wso2.ei.tools.mule2ballerina.model.SubFlow");

    private String value;
    private String mClassName;

    Element(final String value, final String intermediateMClassname) {
        this.value = value;
        this.mClassName = intermediateMClassname;
    }

    public String getValue() {
        return value;
    }

    public String getmClassName() {
        return mClassName;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}
