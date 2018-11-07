/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.mb.integration.common.clients;

/**
 * The MQTT client mode to use.
 */
public enum ClientMode {

    /**
     * All the server calls will be synchronous. The control will not be returned until the call is successful.
     */
    BLOCKING,

    /**
     * All the server calls are asynchronous. The control is immediately returned and the server call will be made
     * asynchronously.
     */
    ASYNC

}
