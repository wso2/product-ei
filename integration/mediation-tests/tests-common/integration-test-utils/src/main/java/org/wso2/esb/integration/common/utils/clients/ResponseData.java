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

package org.wso2.esb.integration.common.utils.clients;

/**
 * This class holds the response data
 */
public class ResponseData {
    private boolean success;
    private String sessionId;
    private String responseServer;

    public ResponseData(boolean success, String sessionId, String responseServer) {
        this.success = success;
        this.sessionId = sessionId;
        this.responseServer = responseServer;
    }

    /**
     * Returns whether the response is success
     *
     * @return true if response is success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the session ID
     *
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Get Responded Server
     *
     * @return responseServer
     */
    public String getResponseServer() {
        return responseServer;
    }
}
