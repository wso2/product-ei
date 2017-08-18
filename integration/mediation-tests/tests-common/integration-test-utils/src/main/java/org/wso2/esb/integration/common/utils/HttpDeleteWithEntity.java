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

package org.wso2.esb.integration.common.utils;

import org.apache.http.client.methods.HttpPost;

import java.net.URI;

/**
 * Since org.apache.http.client.methods.HttpDelete does not support entity, we have to extend HttpPost capabilities
 * to act as Http DELETE
 */
public class HttpDeleteWithEntity extends HttpPost{

    public static final String METHOD_NAME = "DELETE";

    public HttpDeleteWithEntity() {
        super();
    }

    public HttpDeleteWithEntity(URI uri) {
        super.setURI(uri);
    }

    public HttpDeleteWithEntity(String uri) {
        super.setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }

}
