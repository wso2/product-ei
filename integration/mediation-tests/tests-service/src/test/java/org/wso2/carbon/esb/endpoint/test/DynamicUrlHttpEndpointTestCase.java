/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JSONClient;

import static org.testng.Assert.assertEquals;

/**
 * Tests invoking http endpoint with dynamic URL values
 */
public class DynamicUrlHttpEndpointTestCase extends ESBIntegrationTest {
    private JSONClient jsonclient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        jsonclient = new JSONClient();
        verifyProxyServiceExistence("IterateDynamicProxy");
        verifyProxyServiceExistence("OrderOneProxy");
        verifyProxyServiceExistence("OrderTwoProxy");
        verifyProxyServiceExistence("OrderThreeProxy");
        verifyProxyServiceExistence("OrderFourProxy");
        verifyProxyServiceExistence("OrderFiveProxy");
        verifyProxyServiceExistence("OrderSixProxy");

    }

    @Test(groups = { "wso2.esb" }, description = "Http Endpoint test with dynamic url values")
    public void testRelativeLocationHeader() throws Exception {
        String addUrl = getProxyServiceURLHttp("IterateDynamicProxy");
        String request =
                "{\n" + " \"request\": {\n" + "\"terms\": {\n" + "\"term\": [\n" + "\"OrderOneProxy\",\n"
                        + "\"OrderTwoProxy\",\n" + "\"OrderThreeProxy\",\n" + "\"OrderFourProxy\",\n" + "\"OrderFiveProxy\",\n"
                        + "\"OrderSixProxy\"\n" + "]\n" + "}\n" + "}\n" + "}";
        String actualResult = jsonclient.sendUserDefineRequest(addUrl, request).toString();
        int result = 0;
        if (actualResult != null) {
            if (actualResult.contains("One")) {
                ++result;
            }
            if (actualResult.contains("Two")) {
                ++result;
            }
            if (actualResult.contains("Three")) {
                ++result;
            }
            if (actualResult.contains("Four")) {
                ++result;
            }
            if (actualResult.contains("Five")) {
                ++result;
            }
            if (actualResult.contains("Six")) {
                ++result;
            }
        }
        assertEquals(result, 6, "Did not receive response from all six endpoints");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}

