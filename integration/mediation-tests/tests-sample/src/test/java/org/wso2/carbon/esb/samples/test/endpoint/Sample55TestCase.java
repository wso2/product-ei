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
package org.wso2.carbon.esb.samples.test.endpoint;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.LoadBalanceSessionFullClient;
import org.wso2.esb.integration.common.utils.clients.ResponseData;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sample 55: Session Affinity Load Balancing between Failover Endpoints
 */
public class Sample55TestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private SampleAxis2Server axis2Server3;
    private SampleAxis2Server axis2Server4;
    private LoadBalanceSessionFullClient lbClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(55);

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
        axis2Server3 = new SampleAxis2Server("test_axis2_server_9003.xml");
        axis2Server4 = new SampleAxis2Server("test_axis2_server_9004.xml");

        axis2Server1.deployService(SampleAxis2Server.LB_SERVICE_1);
        axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);
        axis2Server3.deployService(SampleAxis2Server.LB_SERVICE_3);
        axis2Server4.deployService(SampleAxis2Server.LB_SERVICE_4);

        axis2Server1.start();
        axis2Server2.start();
        axis2Server3.start();
        axis2Server4.start();

        lbClient = new LoadBalanceSessionFullClient();

    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb"  },
          description = "Session Affinity Load Balancing between Failover Endpoints" , enabled = false)
    public void testLoadBalanceEndpoint() throws Exception {

        List<ResponseData> messages =
            lbClient.sendLoadBalanceRequest(getMainSequenceURL(), null, null, 10);
        Assert.assertEquals(messages.size(), 10, "Message count should be 10");

        // Verify Response: given session id should be directed to the same server
        String[] responseServers = verifyRespondedServers(messages);

        // Verify Response: all requested should be directed to Server 1 and Server 3
        Set<String> serverSet = new HashSet<String>(Arrays.asList(responseServers));
        Assert.assertEquals(serverSet.size(), 2, "Responded server count should be 2");
        Assert.assertTrue(serverSet.contains("Response from server: Server_1") &&
                          serverSet.contains("Response from server: Server_3"),
                          "Server_1 and Server_3 should be responded"
        );

        // Stop axis2Server1
        axis2Server1.stop();

        messages =
            lbClient.sendLoadBalanceRequest(getMainSequenceURL(), null, null, 10);
        Assert.assertEquals(messages.size(), 10, "Message count should be 10");

        // Verify Response: given session id should be directed to the same server
        responseServers = verifyRespondedServers(messages);

        // Verify Response: all requested should be directed to Server 2 and Server 3
        serverSet = new HashSet<String>(Arrays.asList(responseServers));
        Assert.assertEquals(serverSet.size(), 2, "Responded server count should be 2");
        Assert.assertTrue(serverSet.contains("Response from server: Server_2") &&
                          serverSet.contains("Response from server: Server_3"),
                          "Server_2 and Server_3 should be responded"
        );

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();

        if (axis2Server1.isStarted()) {
            axis2Server1.stop();
        }
        if (axis2Server2.isStarted()) {
            axis2Server2.stop();
        }
        if (axis2Server3.isStarted()) {
            axis2Server3.stop();
        }
        if (axis2Server4.isStarted()) {
            axis2Server4.stop();
        }
        axis2Server1 = null;
        axis2Server2 = null;
        axis2Server3 = null;
        axis2Server4 = null;
        lbClient = null;
    }

    private String[] verifyRespondedServers(List<ResponseData> messages) {
        String[] responseServers = new String[3];
        boolean isResponseCorrect = true;

        for (ResponseData message : messages) {
            if (message.isSuccess()) {
                if (responseServers[Integer.valueOf(message.getSessionId())] == null) {
                    responseServers[Integer.valueOf(message.getSessionId())] =
                        message.getResponseServer();
                } else {
                    if (!responseServers[Integer.valueOf(message.getSessionId())]
                        .equals(message.getResponseServer())) {
                        isResponseCorrect = false;
                        break;
                    }
                }
            } else {
                isResponseCorrect = false;
                break;
            }
        }

        Assert.assertTrue(isResponseCorrect, "Response invalid");

        return responseServers;
    }
}
