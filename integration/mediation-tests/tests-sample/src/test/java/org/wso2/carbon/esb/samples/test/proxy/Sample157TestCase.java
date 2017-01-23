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
package org.wso2.carbon.esb.samples.test.proxy;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;

public class Sample157TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(157);
    }

    /**
     * Test for conditional router mediator with multiple conditional routes
     * Test Artifact: Sample 157 - Conditional Router for Routing Messages based on HTTP URL, HTTP Headers and Query Parameters
     * Note: since toUrl is not given, it will take "/services/StockQuoteProxy" (from transport address) as To address by default
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithMultiRoutesTest() throws Exception {

        StockQuoteClient client1 = new StockQuoteClient();
        StockQuoteClient client2 = new StockQuoteClient();
        StockQuoteClient client3 = new StockQuoteClient();

        client1.addHttpHeader("foo", "bar");

        client2.addHttpHeader("my_custom_header1", "foo1");

        client3.addHttpHeader("my_custom_header2", "bar");
        client3.addHttpHeader("my_custom_header3", "foo");

        OMElement response1 = client1.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");

        Assert.assertTrue(response1.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response1.toString().contains("WSO2 Company"));

        OMElement response2 = client2.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");

        Assert.assertTrue(response2.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response2.toString().contains("WSO2 Company"));

        OMElement response3 = client3.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy") + "?qparam1=qpv_foo&qparam2=qpv_foo2", null, "WSO2");

        Assert.assertTrue(response3.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response3.toString().contains("WSO2 Company"));

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
