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
package org.wso2.carbon.esb.passthru.transport.test;


import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;




public class RetrieveBackendWsdlTestCase extends ESBIntegrationTest {


    private HttpClientUtil httpClientUtil;
    private String backendWSDLUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        httpClientUtil = new HttpClientUtil();
        backendWSDLUrl=getProxyServiceURLHttp("StockQuoteProxy1")  +"?wsdl";
    }

    /**
     * Test to check the correct backend WSDL can be retrieved through an ESB Proxy
     * Testing done using the Axis2 Sample server service - "SimpleStockQuoteService" shipped with WSO2 ESB
     * Test Artifacts : ESB sample 151
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb")
    public void testRetrieveBackendServiceWsdl() throws Exception {

        loadSampleESBConfiguration(151);

        OMElement result = httpClientUtil.get(backendWSDLUrl);

        // Return the backend service WSDLL instead of  proxy WSDL

        Assert.assertTrue(result.toString().contains("SimpleStockQuoteService"), "Failed to receive service WSDL, Named SimpleStockQuoteService");


    }


    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
        httpClientUtil = null;
        backendWSDLUrl = null;
    }
}
