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
package org.wso2.carbon.esb.samples.test.mediation.throttling;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.samples.test.mediation.throttling.utils.ConcurrencyAndRequestThrottleTestClient;
import org.wso2.carbon.esb.samples.test.mediation.throttling.utils.ThrottleTestCounter;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class Sample370TestCase extends ESBIntegrationTest {

    private final int CONCURRENT_CLIENTS = 10;
    private final int AS_POLICY_ACCESS_GRANTED = 6;
    private final int AS_POLICY_ACCESS_DENIED = 4;
    private final int THROTTLE_MAX_MSG_COUNT = 6;
    //Since Concurrent access is 6 even Max message count is set to 10 it will throttle at 6

    private List list;
    private ConcurrencyAndRequestThrottleTestClient[] concurrencyAndRequestThrottleTestClients;
    private Thread[] clients;
    private ThrottleTestCounter clientsDone;
    private int grantedRequests;
    private int deniedRequests;
    private ThrottleTestCounter requestThrottledClients;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/throttle/ConcurrencyAndRequestBasedThrottlingConcurrency.xml");


        list = Collections.synchronizedList(new ArrayList());
        concurrencyAndRequestThrottleTestClients = new ConcurrencyAndRequestThrottleTestClient[CONCURRENT_CLIENTS];
        clients = new Thread[CONCURRENT_CLIENTS];
        clientsDone = new ThrottleTestCounter();
        requestThrottledClients = new ThrottleTestCounter();
        grantedRequests = 0;
        deniedRequests = 0;
        initClients();         //initialising Axis2Clients
    }

    @Test(groups = "wso2.esb",
            description = "Use of both concurrency throttling and request rate based throttling -Throttle Concurrent Access",
            timeOut = 1000 * 60 * 2)
    public void testConcurrencyAndRequestBasedPolicyThrottlingConcurrency()
            throws InterruptedException {
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
        assertEquals(grantedRequests, requestThrottledClients.getCount(), "Fault: Request throttle policy failure");

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        concurrencyAndRequestThrottleTestClients = null;
        clients = null;
        clientsDone = null;
        requestThrottledClients = null;
        list = null;
        super.cleanup();
    }


    private void initClients() {
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            concurrencyAndRequestThrottleTestClients[i] = new ConcurrencyAndRequestThrottleTestClient(
                    getMainSequenceURL(), list, clientsDone, requestThrottledClients, THROTTLE_MAX_MSG_COUNT);
        }
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            clients[i] = new Thread(concurrencyAndRequestThrottleTestClients[i]);
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
