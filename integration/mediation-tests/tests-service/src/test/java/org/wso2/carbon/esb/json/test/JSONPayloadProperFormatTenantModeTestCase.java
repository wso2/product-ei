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

package org.wso2.carbon.esb.json.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test case to test whether a nested json payload is handled correctly.
 */
public class JSONPayloadProperFormatTenantModeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init(TestUserMode.TENANT_ADMIN);
    }

    @Test(groups = "wso2.esb",
          description = "Check whether JSON message formatting works properly in tenant mode")
    public void testJSONFormattingInTenantMode() throws MalformedURLException, AutomationFrameworkException {
        String JSON_PAYLOAD = "{\"emails\": [{\"value\": \"test@wso2.com\"}]}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        HttpResponse response = HttpRequestUtil
                .doPost(new URL("http://localhost:8480/json/payload"), JSON_PAYLOAD, headers);

        //checking whether JSON payload of wrong format is received
        assertFalse(response.getData().equals("{\"emails\":{\"value\":\"test@wso2.com\"}}"),
                "Incorrect format received!");

        //checking whether JSON payload of correct format is present
        assertTrue(response.getData().equals("{\"emails\": [{\"value\": \"test@wso2.com\"}]}"),
                "Expected format not received!");
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }

}