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

package org.wso2.carbon.esb.mediator.test.property;


import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case to check if there is any issue when a property read from call template
 * is set as a transport header value, but the caller has not passed the value.
 */
public class NullValueTransportHeaderTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
    }


    @Test(groups = "wso2.esb", description = "Property mediator test with null value set as transport header")
    public void testRespondMediator() throws AxisFault, MalformedURLException, AutomationFrameworkException {
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "application/json");
        HttpResponse response = HttpRequestUtil.doPost(new URL(getApiInvocationURL("nullValueTransportHeaderTestFrontEnd")),
                "{\"test\" : \"nullHeaderVal\"}", requestHeader);
        Assert.assertTrue(response.getData().contains("{\"company\" : \"wso2\"}"), "Expected response was not"
                + "received. Got " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
