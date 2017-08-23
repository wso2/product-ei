/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.ei.tools.converter.common.util;

/**
 * Enum to keep property names that is used for passing information between converter and ballerina helper methods
 */
public enum Property {
    OUTBOUND_MSG, //Identify ballerina outbound message variable name
    INBOUND_MSG, //Identify ballerina inbound message variable name
    JMS_MSG, //Identify ballerina jms message variable name
    HEADER_NAME, //Identify http header name
    VARIABLE_NAME, //Identify any variable name
    TYPE, //Identify variable type
    FUNCTION_NAME, //Identify function name
    FORMAT, //Identify payload
    PAYLOAD_VAR_NAME, //Identify payload variable
    CONNECTOR_VAR_NAME, //Identify connector variable
    URL, //Identify url
    SERVICE_NAME, //Identify ballerina service name
    PROTOCOL_PKG_NAME, //Identify ballerina protocol package name
    RESOURCE_NAME, //Identify ballerina resource name
    RESOURCE_ANNOTATION_COUNT, //Identify ballerina resource
    BASEPATH_VALUE, //Identify basepath value
    METHOD_NAME, //Identify method name
    EXPRESSION, //Identify expression
    PACKAGE_NAME, //Identify ballerina package name
    VARIABLE_NAME_NEW, //Identify newly created variable name
    PATH, //Identify resource path
    JMS_EP_VAR_NAME, //JMS endpoint variable name
    JMS_QUEUE_NAME; //JMS queue name
}
