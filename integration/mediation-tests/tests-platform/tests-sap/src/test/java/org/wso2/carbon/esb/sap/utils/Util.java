/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.sap.utils;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Util class to provide basic function required for SAP tests.
 */
public class Util {

    /**
     * Extracts the payload from a HTTP response. For a given HttpResponse object, this method can be called only once.
     *
     * @param response HttpResponse instance to be extracted
     * @return Content payload
     * @throws IOException If an error occurs while reading from the response
     */
    public static String getResponsePayload(HttpResponse response) throws IOException {
        String responseAsString = "";
        if (response.getEntity() != null) {
            InputStream in = response.getEntity().getContent();
            int length;
            byte[] tmp = new byte[2048];
            StringBuilder buffer = new StringBuilder();
            while ((length = in.read(tmp)) != -1) {
                buffer.append(new String(tmp, 0, length));
            }
            responseAsString = buffer.toString();
        }
        return responseAsString;
    }
}
