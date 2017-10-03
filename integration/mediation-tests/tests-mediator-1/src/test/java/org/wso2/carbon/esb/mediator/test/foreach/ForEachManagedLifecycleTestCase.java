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

package org.wso2.carbon.esb.mediator.test.foreach;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case for ESBJAVA-5227
 * Test the ManagedLifecycle interface implementation in ForEach mediator.
 * Inside the ManagedLifecyle::init() method, init() of child mediators are invoked,
 * therefore message mediation inside DBReport/DBLookup get success.
 */
public class ForEachManagedLifecycleTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        verifyProxyServiceExistence("forEachManagedLifeCycleTestProxy");
    }

    @Test(groups = {"wso2.esb"}, description = "testManagedLifecycle")
    public void testManagedLifecycle() throws Exception {

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "text/xml");
        requestHeader.put("SOAPAction", "urn:mediate");
        String message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <stock>\n" +
                "   <company>IBM</company>\n" +
                "   <company>SUN</company>\n" +
                "   </stock>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getProxyServiceURLHttp("forEachManagedLifeCycleTestProxy")), message, requestHeader);

        Assert.assertTrue(response.getData().contains("Message mediation successful"),
                "Invalid response received. " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}