/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.integrator.core.handler;

public class BasicAuthConstants {

    //Response Status Codes
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_FORBIDDEN = 403;

    //Response Header Strings
    public static final String HTTP_STATUS_CODE = "HTTP_SC";
    public static final String RESPONSE = "RESPONSE";
    public static final String TRUE = "true";
    public static final String NO_ENTITY_BODY = "NO_ENTITY_BODY";
    public static final String WWW_AUTHENTICATE = "WWW_Authenticate";
    public static final String WWW_AUTH_METHOD = "Basic realm=\"WSO2 EI\"";
}
