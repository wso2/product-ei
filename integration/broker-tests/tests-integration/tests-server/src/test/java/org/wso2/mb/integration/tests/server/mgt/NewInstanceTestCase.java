/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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

package org.wso2.mb.integration.tests.server.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.carbonserver.MultipleServersManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * The following test cases checks the primary functionality of the carbon server.
 */
public class NewInstanceTestCase {
    private static final Log log = LogFactory.getLog(NewInstanceTestCase.class);
    private MultipleServersManager manager = new MultipleServersManager();
    private Map<String, String> startupParameterMap1 = new HashMap<>();
    private long TIMEOUT = 180000;

    /**
     * Starts up the product with offset.
     *
     * @throws XPathExpressionException
     * @throws AutomationFrameworkException
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(groups = {"mb.server.startup"})
    public void testStartServers() throws XPathExpressionException, AutomationFrameworkException {
        AutomationContext context = new AutomationContext();
        startupParameterMap1.put("-DportOffset", "2");
        CarbonTestServerManager server1 = new CarbonTestServerManager(context, System.getProperty("carbon.zip"),
                startupParameterMap1);
        manager.startServers(server1);
    }

    /**
     * Starts up the product and checks whether the ports are open and can be connected.
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"mb.server.startup"})
    public void waitForPortTestCase() {
        boolean isPortOpen = false;
        long startTime = System.currentTimeMillis();
        String hostName = "localhost";

        while (!isPortOpen && (System.currentTimeMillis() - startTime) < TIMEOUT) {
            Socket socket = null;
            try {
                InetAddress address = InetAddress.getByName(hostName);
                socket = new Socket(address, 9445);
                isPortOpen = socket.isConnected();
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            } finally {
                try {
                    if ((socket != null) && (socket.isConnected())) {
                        socket.close();
                    }
                } catch (IOException e) {
                    log.error("Cannot close the socket which is used to check the server status ", e);
                }
            }
        }
        Assert.assertTrue(isPortOpen);
    }

    /**
     * Starts up the product and checks whether it can be logged in by admin successfully.
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"mb.server.startup"})
    public void waitForLoginTestCase() {
        long startTime = System.currentTimeMillis();
        boolean loginFailed = true;
        while (((System.currentTimeMillis() - startTime) < TIMEOUT) && loginFailed) {
            log.info("Waiting to login user...");
            try {
                LoginLogoutClient loginClient = new LoginLogoutClient("https://localhost:9445/services/", "admin",
                        "admin");
                loginClient.login();
                loginFailed = false;
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.info("Login failed after server startup", e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    // Nothing to do
                }
            }
        }

        Assert.assertFalse(loginFailed);

    }

    /**
     * Stops all servers.
     *
     * @throws AutomationFrameworkException
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterClass
    public void clean() throws AutomationFrameworkException {
        manager.stopAllServers();
    }
}
