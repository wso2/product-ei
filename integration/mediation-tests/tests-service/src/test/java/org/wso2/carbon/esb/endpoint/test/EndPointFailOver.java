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

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

public class EndPointFailOver extends ESBIntegrationTest {
    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private OMElement response;

    @BeforeClass(alwaysRun = true)
    public void initiateTest() throws Exception {
        init();

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");              // initializing 2 axis2servers
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/configFailOver/ConfigFailOver.xml");    // load synapse
        axis2Server1.start();
        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);     //deploy the simple stock quote in server 2
        axis2Server2.start();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb")
    public void TestFailOver() throws Exception {

        // send the requests to each servers.

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "", "IBM");
        boolean ResponseContainsIBM = response.getFirstElement().toString().contains("IBM");
        Assert.assertTrue(ResponseContainsIBM);              //checking whether response has IBM. If so server has successfully shifted

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            axis2Server1.stop();
            axis2Server2.stop();//stopping the started services

            axis2Server1 = null;
            axis2Server2 = null;
            response = null;
        } finally {
            super.cleanup();          //clearing resources
        }


    }
}
