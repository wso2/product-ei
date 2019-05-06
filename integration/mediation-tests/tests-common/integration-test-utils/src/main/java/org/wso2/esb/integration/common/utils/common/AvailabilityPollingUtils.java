/**
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.esb.integration.common.utils.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

public class AvailabilityPollingUtils {

    private static final Log log = LogFactory.getLog(AvailabilityPollingUtils.class);
    private static ESBTestCaseUtils esbUtils = new ESBTestCaseUtils();

    /**
     * Wait until a proxy is being un-deployed.
     *
     * @return Whether the proxy is un-deployed or not
     */
    public static Callable<Boolean> isProxyNotAvailable(final String proxyName, final String backEndUrl,
            final String sessionCookie) {
        return new Callable<Boolean>() {
            @Override public Boolean call() {
                log.info("Waiting until the proxy, " + proxyName + " is un-deployed.");
                try {
                    return !esbUtils.isProxyServiceExist(backEndUrl, sessionCookie, proxyName);
                } catch (RemoteException e) {
                    return false;
                }
            }
        };
    }
}