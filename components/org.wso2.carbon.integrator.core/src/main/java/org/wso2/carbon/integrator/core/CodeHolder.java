/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.integrator.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.integrator.core.internal.DispatcherComponent;

import java.util.HashMap;
import java.util.Map;

public class CodeHolder {

    private static final Log log = LogFactory.getLog(CodeHolder.class);
    private static Map<String, String> authCodes;
    private static CodeHolder codeHolder;

    /**
     * Private constructor to make the class singleton and start the cleanup process.
     */
    private CodeHolder() {
        if (DispatcherComponent.getHazelcastInstance() != null) {
            log.debug("Creating Hazelcast map to store Integrator Authorization Codes");
            authCodes = DispatcherComponent.getHazelcastInstance().getMap("INTEGRATOR_AUTHCODE_HOLDER");
        } else {
            log.debug("Creating simple HashMap to store Integrator Authorization Codes since clustering is not enabled");
            authCodes = new HashMap<String, String>(2);
        }
    }

    /**
     * Method to return the instance.
     *
     * @return singleton instance of the class
     */
    public static synchronized CodeHolder getInstance() {
        if (codeHolder == null) {
            codeHolder = new CodeHolder();
            addCodes();
        }
        return codeHolder;
    }

    public static void addCodes() {
        authCodes.put(CarbonConstants.START_TIME,System.getProperty(CarbonConstants.START_TIME));
    }

    public static String getCodes(String key) {
        return authCodes.get(key);
    }


}
