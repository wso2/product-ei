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
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

public class AvailabilityPollingUtils {

    private static final Log log = LogFactory.getLog(AvailabilityPollingUtils.class);
    private static ESBTestCaseUtils esbUtils = new ESBTestCaseUtils();

    /**
     * Check whether the proxy service is being deployed.
     *
     * @param backEndUrl    Backend URL of the EI server
     * @param sessionCookie Session cookie for the EI server
     * @param proxyName     Proxy name to check the availability
     * @return Whether the proxy is deployed or not
     */
    public static Callable<Boolean> isProxyAvailable(final String backEndUrl, final String sessionCookie,
                                                     final String proxyName) {

        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                log.info("Checking whether the proxy '" + proxyName + "' is deployed.");
                try {
                    return esbUtils
                            .isProxyServiceExist(backEndUrl, sessionCookie, proxyName);
                } catch (RemoteException e) {
                    return false;
                }
            }
        };
    }

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

    /**
     * Check whether server is being started
     *
     * @param host Host name to check the availability
     * @param port Port number to check the availability
     * @return Whether the server is started or not
     */
    public static Callable<Boolean> isHostAvailable(final String host, final int port) {

        return new Callable<Boolean>() {
            @Override
            public Boolean call() {

                log.info("Checking whether the vfs server is started.");
                try (Socket ignored = new Socket(host, port)) {
                    return true;
                } catch (IOException ex) {
                    return false;
                }
            }
        };
    }
    /**
     * Check whether port is closed
     *
     * @param host Host name to check the availability
     * @param port Port number to check the availability
     * @return Whether the server is started or not
     */
    public static Callable<Boolean> isPortClosed(final String host, final int port) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                boolean portCondition = false;
                log.info("Checking whether the " + host + " on " + port + " is not started.");
                try (Socket ignored = new Socket(host, port)) {
                    portCondition = false;
                } catch (IOException ex) {
                    portCondition = true;
                }
                return portCondition;
            }
        };
    }

    /**
     * Wait until the file is moved to original folder
     *
     * @param outputFolder Output folder of the moved location
     * @return Whether the file is moved or not
     */
    public static Callable<Boolean> isFileNotMoved(final File outputFolder) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {

                log.info("Checking whether the file is moved to the original folder");
                File[] files = outputFolder.listFiles();
                return files != null && files.length > 0;
            }
        };
    }

    /**
     * Check whether server is not started
     *
     * @param synapseConfigAdminClient check synapseAdminClient
     * @param config                   check synapseConfigs
     * @return Whether the synapseConfigAdminClient is updated or not
     */
    public static Callable<Boolean> isUpdated
    (final SynapseConfigAdminClient synapseConfigAdminClient, final String config) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                boolean clientStatus = false;
                try {
                    if (synapseConfigAdminClient.updateConfiguration(config)) {
                        clientStatus = true;
                    }
                } catch (Exception e) {
                    log.error(e);
                }
                return clientStatus;
            }
        };
    }
}