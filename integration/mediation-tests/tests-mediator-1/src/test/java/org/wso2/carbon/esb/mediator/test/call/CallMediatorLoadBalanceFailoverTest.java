/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.mediator.test.call;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.IOException;

public class CallMediatorLoadBalanceFailoverTest extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server1.deployService(SampleAxis2Server.LB_SERVICE_1);
        axis2Server1.start();

        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);
        axis2Server2.start();

        loadESBConfigurationFromClasspath("artifacts/ESB/mediatorconfig/call/loadbalancecalltestproxy.xml");

        //Test weather all the axis2 servers are up and running
        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
        response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:9001/services/SimpleStockQuoteService",
                null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
        response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:9002/services/SimpleStockQuoteService",
                null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        log.info("Tests Are Completed");
        if (axis2Server1.isStarted()) {
            axis2Server1.stop();
        }
        if (axis2Server2.isStarted()) {
            axis2Server2.stop();
        }
        axis2Server1 = null;
        axis2Server2 = null;
        super.cleanup();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test sending request to Load balanced Endpoint")
    public void testCallForLoadBalanceFailover() throws IOException, InterruptedException {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("CallLoadBalance"),
                null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("CallLoadBalance"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        Thread.sleep(1000); //waiting for server2 to shut down

        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("CallLoadBalance"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("CallLoadBalance"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

}
