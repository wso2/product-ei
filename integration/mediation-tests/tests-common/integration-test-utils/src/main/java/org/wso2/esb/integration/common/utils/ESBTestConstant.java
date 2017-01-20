/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.esb.integration.common.utils;

public class ESBTestConstant {
    public static final String SIMPLE_STOCK_QUOTE_SERVICE = "SimpleStockQuoteService";
    public static final String SECURE_STOCK_QUOTE_SERVICE = "SecureStockQuoteService";
    public static final String SIMPLE_AXIS2_SERVICE = "Axis2Service";
    public static final String STUDENT_REST_SERVICE = "StudentService";
    public static final String ECHO_SERVICE = "Echo";
    public static final java.lang.String CARBON_HOME = "carbon.home";
    public static final String ESB_PRODUCT_GROUP = "ESB";
    public static final String UNEXPECTED_SENDING_OUT = "org.apache.synapse.SynapseException: " +
            "Unexpected error during sending message out";
    public static final String READ_TIME_OUT = "Read timed out";
    public static final String ERROR_CONNECTING_TO_BACKEND = "Error connecting to the back end";
    public static final String INCOMING_MESSAGE_IS_NULL = "The input stream for an incoming message is null.";
    public static final String ERROR_ADDING_SEQUENCE = "Error adding sequence";     //when invalid sequence is uploaded
    public static final String UNABLE_TO_SAVE_SEQUENCE = "Unable to save the Sequence";     //when updating sequence with invalid content
}
