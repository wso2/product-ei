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

package org.wso2.carbon.esb.endpoint.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.LoadbalanceFailoverClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

//  https://issues.apache.org/jira/browse/SYNAPSE-622
public class EndPointFailoverInLoadBalancingTestCase extends ESBIntegrationTest {

    private LoadbalanceFailoverClient lbClient;
    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.start();
        axis2Server2.start();

        axis2Server1.deployService(SampleAxis2Server.LB_SERVICE_1);
        axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);

        lbClient = new LoadbalanceFailoverClient();
        uploadSynapseConfig();
    }

    private void uploadSynapseConfig() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/patch_automation/load_balance_failover_synapse.xml");
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoints With while one endpoint is out of order")
    public void testLoadbalancerWithFailedEndpoints() throws Exception {
        String response;
        lbClient.sendLoadBalanceFailoverRequest(getMainSequenceURL());
        lbClient.sendLoadBalanceFailoverRequest(getMainSequenceURL());
        response = lbClient.sendLoadBalanceFailoverRequest(getMainSequenceURL());
        Assert.assertNotNull(response, "Asserting for null response");
        Assert.assertTrue(!response.contains("COULDN'T SEND THE MESSAGE TO THE SERVER"), "Asserting whether the request is lost");
        System.out.println(response);
        Assert.assertTrue(response.contains("Response from server: Server_1"), "Asserting for correct response");

    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        axis2Server1.stop();
        axis2Server2.stop();
        axis2Server1 = null;
        axis2Server2 = null;
        lbClient = null;
        super.cleanup();

    }
}
