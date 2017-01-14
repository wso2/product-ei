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
import org.wso2.esb.integration.common.utils.clients.LoadbalanceFailoverClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/**
 * Sample 52: Using Load Balancing Endpoints to Handle Peak Loads
 */
public class Sample52TestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private SampleAxis2Server axis2Server3;
    private LoadbalanceFailoverClient lbClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(52);

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
        axis2Server3 = new SampleAxis2Server("test_axis2_server_9003.xml");

        axis2Server1.deployService(SampleAxis2Server.LB_SERVICE_1);
        axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);
        axis2Server3.deployService(SampleAxis2Server.LB_SERVICE_3);

        axis2Server1.start();
        axis2Server2.start();
        axis2Server3.start();

        lbClient = new LoadbalanceFailoverClient();

    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          description = "Using Load Balancing Endpoints to Handle Peak Loads")
    public void testLoadBalanceEndpoint() throws Exception {
        String[] resMessages = { "Response from server: Server_1", "Response from server: Server_2",
                                 "Response from server: Server_3", "Response from server: Server_1",
                                 "Response from server: Server_2", "Response from server: Server_3",
                                 "Response from server: Server_2", "Response from server: Server_3",
                                 "Response from server: Server_2",
                                 "Response from server: Server_3" };
        for (int i = 0; i < 10; i++) {
            String response =
                lbClient.sendLoadBalanceRequest(getMainSequenceURL(), null);
            Assert.assertNotNull(response, "Response is null");
            Assert.assertTrue(response.contains(resMessages[i]),
                              "Response message should be : " + resMessages[i]);

            if (i == 5) {
                axis2Server1.stop();
            }
        }
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
        axis2Server1 = null;
        axis2Server2 = null;
        axis2Server3 = null;
        lbClient = null;
    }
}
