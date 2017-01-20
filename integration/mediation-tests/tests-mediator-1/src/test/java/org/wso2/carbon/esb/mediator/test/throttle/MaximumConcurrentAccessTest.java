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
package org.wso2.carbon.esb.mediator.test.throttle;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.esb.mediator.test.throttle.utils.ConcurrencyThrottleTestClient;
import org.wso2.carbon.esb.mediator.test.throttle.utils.ThrottleTestCounter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MaximumConcurrentAccessTest extends ESBIntegrationTest {

    private final int CONCURRENT_CLIENTS = 100;
    private final int AS_POLICY_ACCESS_GRANTED = 100;
    //    This is not the maximum value its a random value just for testing purposes
    private final int AS_POLICY_ACCESS_DENIED = 0;
    private List list;
    private ConcurrencyThrottleTestClient[] concurrencyThrottleTestClients;
    private Thread[] clients;
    private ThrottleTestCounter clientsDone;
    private int grantedRequests;
    private int deniedRequests;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/throttle/MaximumConcurrentAccess.xml");

        list = Collections.synchronizedList(new ArrayList());
        concurrencyThrottleTestClients = new ConcurrencyThrottleTestClient[CONCURRENT_CLIENTS];
        clients = new Thread[CONCURRENT_CLIENTS];
        clientsDone = new ThrottleTestCounter();
        grantedRequests = 0;
        deniedRequests = 0;
        initClients();         //initialising Axis2Clients
    }

    @Test(groups = "wso2.esb",
          description = "Test MaximumConcurrentAccess = <max.accepted.value> For testing 100 is used",
          timeOut = 1000 * 60 * 2)
    public void testMaximumValue() throws InterruptedException {
        startClients();
        while (clientsDone.getCount() < CONCURRENT_CLIENTS) {
            Thread.sleep(1000);
        }

        for (Object aList : list) {
            if (aList.toString().equals("Access Granted")) {
                grantedRequests++;
            } else if (aList.toString().equals("Access Denied")) {
                deniedRequests++;
            }
        }

        assertEquals(grantedRequests, AS_POLICY_ACCESS_GRANTED, "Fault: Concurrent throttle policy failure");
        assertEquals(deniedRequests, AS_POLICY_ACCESS_DENIED, "Fault: Concurrent throttle policy failure");

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        concurrencyThrottleTestClients = null;
        clients = null;
        clientsDone = null;
        list = null;
        super.cleanup();
    }


    private void initClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            concurrencyThrottleTestClients[i] = new ConcurrencyThrottleTestClient(getMainSequenceURL(), list, clientsDone);
        }
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i] = new Thread(concurrencyThrottleTestClients[i]);
        }
    }


    private void startClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i].start();
        }
        int aliveCount = 0;
        Calendar startTime = Calendar.getInstance();
        while (aliveCount < CONCURRENT_CLIENTS) {
            if ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) > 60000) {
                break;
            }
            if (clients[aliveCount].isAlive()) {
                aliveCount = 0;
                continue;
            }
            aliveCount++;
        }
    }

}
