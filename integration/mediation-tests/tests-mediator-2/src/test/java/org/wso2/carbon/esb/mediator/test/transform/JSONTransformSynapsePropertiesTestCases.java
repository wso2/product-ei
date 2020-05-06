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
 * Testcases to test the synapse properties with JSON Transform Mediator
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
        verifyProxyServiceExistence("transformMediatorNamespaceProperties");
        verifyProxyServiceExistence("transformMediatorValidNCNameProperty");
        verifyProxyServiceExistence("transformMediatorAutoPrimitiveCustomRegex");
        verifyProxyServiceExistence("transformMediatorXMLNilReadWrite");
        parser = new JsonParser();
    }

    @Test(groups = "wso2.esb", description = "Do XML to JSON transformation with overridden autoprimitive" +
            " synapse property")
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
                "XML to JSON transformation with a simple synapse property overridden did not occur properly");
    }

    @Test(groups = "wso2.esb", description = "Do XML to JSON transformation with overridden namespace synapse property")
    public void testSimpleNamespaceProperty() throws Exception {
        String payload = "<ns:stock xmlns:ns='http://services.samples'>\n" +
                "    <ns:name>WSO2</ns:name>\n" +
                "</ns:stock>";
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String expectedOutput = "{\n" +
                "    \"ns^stock\": {\n" +
                "        \"@xmlns^ns\": \"http://services.samples\",\n" +
                "        \"ns^name\": \"WSO2\"\n" +
                "    }\n" +
                "}";
        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getProxyServiceURLHttp("transformMediatorNamespaceProperties")), payload, httpHeaders);
        assertEqualJsonObjects(response.getData(), expectedOutput,
                "XML to JSON transformation with namespace synapse properties overridden did not occur properly");
    }

    @Test(groups = "wso2.esb", description = "Do XML to JSON transformation with overridden validNCName synapse property")
    public void testValidNCNameProperty() throws Exception {
        String payload = "<stock_JsonReader_32_quote>\n" +
                "    <name>WSO2</name>\n" +
                "</stock_JsonReader_32_quote>";
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String expectedOutput = "{\n" +
                "    \"stock quote\": {\n" +
                "        \"name\": \"WSO2\"\n" +
                "    }\n" +
                "}";
        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getProxyServiceURLHttp("transformMediatorValidNCNameProperty")), payload, httpHeaders);
        assertEqualJsonObjects(response.getData(), expectedOutput,
                "XML to JSON transformation with validNCName synapse properties overridden did not occur properly");
    }

    @Test(groups = "wso2.esb", description = "Do XML to JSON transformation with overridden autoprimitive " +
            "custom regext synapse property")
    public void testAutoprimitiveCustomRegexProperty() throws Exception {
        String payload = "<coordinates>\n" +
                "    <location>\n" +
                "        <name>Bermuda Triangle</name>\n" +
                "        <n>25e1</n>\n" +
                "        <w>7.1e1</w>\n" +
                "    </location>\n" +
                "    <location>\n" +
                "        <name>Eiffel Tower</name>\n" +
                "        <n>4.8e3</n>\n" +
                "        <e>1.8e2</e>\n" +
                "    </location>\n" +
                "</coordinates>";
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String expectedOutput = "{\n" +
                "    \"coordinates\": {\n" +
                "        \"location\": [\n" +
                "            {\n" +
                "                \"name\": \"Bermuda Triangle\",\n" +
                "                \"n\": \"25e1\",\n" +
                "                \"w\": \"7.1e1\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"name\": \"Eiffel Tower\",\n" +
                "                \"n\": \"4.8e3\",\n" +
                "                \"e\": \"1.8e2\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getProxyServiceURLHttp("transformMediatorAutoPrimitiveCustomRegex")), payload, httpHeaders);
        assertEqualJsonObjects(response.getData(), expectedOutput,
                "XML to JSON transformation with autoprimitve custom regext synapse properties overridden " +
                        "did not occur properly");
    }

    @Test(groups = "wso2.esb", description = "Do XML to JSON transformation with overridden XMLNilReadWrite " +
            "synapse property")
    public void testXMLNilReadWriteProperty() throws Exception {
        String payload = "<TransactionQuery xmlns=\"\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "                        <TransactionID>1</TransactionID>\n" +
                "                        <ResponseCode>001</ResponseCode>\n" +
                "                        <ResponseDescription i:nil=\"true\"/>\n" +
                "                    </TransactionQuery>";
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String expectedOutput = "{\n" +
                "    \"TransactionQuery\": {\n" +
                "        \"TransactionID\": 1,\n" +
                "        \"ResponseCode\": \"001\",\n" +
                "        \"ResponseDescription\": null\n" +
                "    }\n" +
                "}";
        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getProxyServiceURLHttp("transformMediatorXMLNilReadWrite")), payload, httpHeaders);
        assertEqualJsonObjects(response.getData(), expectedOutput,
                "XML to JSON transformation with XMLNilReadWrite synapse properties " +
                        "overridden did not occur properly");
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
