/**
 *  Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.passthru.transport.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.esb.nhttp.transport.test.MaximumOpenConnectionsClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.util.*;

import static java.io.File.separator;
import static org.testng.Assert.assertTrue;

public class PttMaximumOpenConnections extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManagerProp;
    private ServerConfigurationManager serverConfigurationManagerAxis2;
    private final int CONCURRENT_CLIENTS = 20;
    private MaximumOpenConnectionsClient[] maxOpenConnectionClients;
    private Thread[] clients;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        serverConfigurationManagerProp = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        String pttFile = /*ProductConstant.getResourceLocations(ProductConstant.ESB_SERVER_NAME)*/FrameworkPathUtil.getSystemResourceLocation()  + "artifacts" + separator +
                "ESB" +separator + "synapseconfig" + separator + "MaxOpenConnections" + separator
                         + "passthru-http.properties";
        File propFile = new File(pttFile);
        serverConfigurationManagerProp.applyConfigurationWithoutRestart(propFile);

        serverConfigurationManagerAxis2 = new ServerConfigurationManager(context);
        String pttAxis2xml = /*ProductConstant.getResourceLocations(ProductConstant.ESB_SERVER_NAME)*/FrameworkPathUtil.getSystemResourceLocation()  + "artifacts" + separator +
                "ESB" +separator + "synapseconfig" + separator + "MaxOpenConnections" + separator + "ptt" +
                             separator + "axis2.xml";
        File axis2File = new File(pttAxis2xml);
        serverConfigurationManagerAxis2.applyConfiguration(axis2File);

        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/MaxOpenConnections/max_open_connections.xml");
        maxOpenConnectionClients = new MaximumOpenConnectionsClient[CONCURRENT_CLIENTS];
        clients = new Thread[CONCURRENT_CLIENTS];
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "PTT Test Maximum Open Connections")
    public void testMaximumConnections() throws InterruptedException {
        initClients();         //initialising Axis2Clients
        startClients();
        int aliveCount = 0;
        Calendar startTime = Calendar.getInstance();
        while (aliveCount < CONCURRENT_CLIENTS) {
            if ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) > 120000) {
                break;
            }
            if (clients[aliveCount].isAlive()) {
                aliveCount = 0;
                continue;
            }
            aliveCount++;
        }

//	System.out.println("COUNT: " + MaximumOpenConnectionsClient.getDeniedRequests());
        assertTrue(MaximumOpenConnectionsClient.getDeniedRequests() >= 1, "(Pass Thru) No Connections Rejected by max_open_connection limit - max_open_connections limit will not be exact.");
    }

    private void initClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            maxOpenConnectionClients[i] = new MaximumOpenConnectionsClient(getProxyServiceURLHttp("MaxOpenConnectionsTest"));
        }
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i] = new Thread(maxOpenConnectionClients[i]);
        }
    }

    private void startClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i].start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                //ignored
            }
        }
    }

    /**
     * Replace passthru-http.properties file and remove test client threads.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void atEnd() throws Exception {
        maxOpenConnectionClients = null;
        clients = null;
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManagerProp.restoreToLastConfiguration(false);
            serverConfigurationManagerProp = null;
            serverConfigurationManagerAxis2.restoreToLastConfiguration();
            serverConfigurationManagerAxis2 = null;
        }
    }
}
