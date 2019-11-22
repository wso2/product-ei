/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.transform;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Testcases to test the basic functionality of JSON Transform Mediator
 */
public class JSONTransformSynapsePropertiesTestCases extends ESBIntegrationTest {
    private JsonParser parser;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext(
                "ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator + "json" +
                File.separator + "jsonTransformationConfig" + File.separator + "synapse.properties"));
        super.init();
        verifyProxyServiceExistence("transformMediatorSimpleAutoPrimitive");
        parser = new JsonParser();
    }

    @Test(groups = "wso2.esb", description = "Do XML to JSON transformation with overridden synapse property")
    public void testSimpleAutoPrimitiveProperty() throws Exception {
        String payload = "<jsonObject>\n" +
                "    <fruit>12345</fruit>\n" +
                "    <price>7.5</price>\n" +
                "</jsonObject>";
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String expectedOutput = "{\n" +
                "    \"fruit\": \"12345\",\n" +
                "    \"price\": \"7.5\"\n" +
                "}";
        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getProxyServiceURLHttp("transformMediatorSimpleAutoPrimitive")), payload, httpHeaders);
        assertEqualJsonObjects(response.getData(), expectedOutput,
                "XML to JSON transformation with a simple synapse property overridden");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }

    private void assertEqualJsonObjects(String json1, String json2, String errorMessage) {
        JsonElement element1 = parser.parse(json1);
        JsonElement element2 = parser.parse(json2);
        assertEquals(element1, element2, errorMessage);
    }
}
