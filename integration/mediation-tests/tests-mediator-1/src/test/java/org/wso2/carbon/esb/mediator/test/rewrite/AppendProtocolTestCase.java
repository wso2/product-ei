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
package org.wso2.carbon.esb.mediator.test.rewrite;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class AppendProtocolTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/rewrite/protocol_append_synapse.xml");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Append text to protocol",
          dataProvider = "addressingUrl")
    public void appendProtocol(String addUrl) throws AxisFault {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("urlRewriteProxy"),
                addUrl,
                "IBM");
        assertTrue(response.toString().contains("IBM"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Conditional URL Rewriting")
    public void invalidUrl() throws AxisFault {
        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    getProxyServiceURLHttp("urlRewriteProxy"),
                    "http://localhost:9010/services/SimpleStockQuoteService",
                    "IBM");
            fail("This Query Must fail");
        } catch (AxisFault fault) {

        }

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }

    @DataProvider(name = "addressingUrl")
    public Object[][] addressingUrl() {
        return new Object[][]{
//                {"htt://localhost:9000/services/SimpleStockQuoteService"},
                {"http://localhost:9000/services/SimpleStockQuoteService"},
//                {"htt://localhost:9010/services/SimpleStockQuoteService"},
                {"https://localhost:9002/services/SimpleStockQuoteService"},

        };

    }

}
