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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;
import org.apache.synapse.rest.Handler;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;

import java.util.Map;

public class RESTBasicAuthHandler implements Handler {

    private static final Log log = LogFactory.getLog(RESTBasicAuthHandler.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) {

        org.apache.axis2.context.MessageContext axis2MessageContext
                = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        Object headers = axis2MessageContext.getProperty(
                org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);

        if (headers != null && headers instanceof Map) {
            Map headersMap = (Map) headers;
            if (headersMap.get("Authorization") == null) {
                headersMap.clear();
                axis2MessageContext.setProperty("HTTP_SC", "401");
                headersMap.put("WWW-Authenticate", "Basic realm=\"WSO2 EI\"");
                axis2MessageContext.setProperty("NO_ENTITY_BODY", true);
                messageContext.setProperty("RESPONSE", "true");
                messageContext.setTo(null);
                Axis2Sender.sendBack(messageContext);
                return false;
                
            } else {
                String authHeader = (String) headersMap.get("Authorization");
                String credentials = authHeader.substring(6).trim();
                if (processSecurity(credentials)) {
                    return true;
                } else {
                    headersMap.clear();
                    axis2MessageContext.setProperty("HTTP_SC", "403");
                    axis2MessageContext.setProperty("NO_ENTITY_BODY", true);
                    messageContext.setProperty("RESPONSE", "true");
                    messageContext.setTo(null);
                    Axis2Sender.sendBack(messageContext);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        return true;
    }

    @Override
    public void addProperty(String s, Object o) {

    }

    @Override
    public Map getProperties() {
        return null;
    }

    public boolean processSecurity(String credentials) {
        String decodedCredentials = new String(new Base64().decode(credentials.getBytes()));
        String username = decodedCredentials.split(":")[0];
        String password = decodedCredentials.split(":")[1];
        UserRealm realm = (UserRealm) CarbonContext.getThreadLocalCarbonContext().getUserRealm();
        try {
            UserStoreManager userStoreManager = realm.getUserStoreManager();
            return userStoreManager.authenticate(username, password);
        } catch (UserStoreException e) {
            log.error("Error in authenticating user", e);
            return false;
        }
    }
}
