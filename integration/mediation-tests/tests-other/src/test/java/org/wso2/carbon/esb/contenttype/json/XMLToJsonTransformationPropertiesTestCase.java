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

package org.wso2.carbon.esb.contenttype.json;

import org.testng.Assert;
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

/**
 * Test case for XML to JSON transformation parameters
 */
public class XMLToJsonTransformationPropertiesTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator + "json" +
                File.separator + "jsonTransformationConfig" + File.separator + "deployment.toml"));
        super.init();
        verifyProxyServiceExistence("xmlToJsonTestProxy");
    }

    /**
     * Test the below properties
     * synapse.commons.json.preserve.namespace=true
     * synapse.commons.json.output.enableNSDeclarations=true
     * Preserve namespaces in XML payload
     * Add XML namespace declarations in the JSON
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test XML to JSON preserve namespace")
    public void testXmlToJsonPreserveNamespace() throws Exception {

        String xmlPayload = "<ns:stock xmlns:ns='http://services.samples'>\n" +
                "               <ns:name>WSO2</ns:name>\n" +
                "            </ns:stock>";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "text/xml");
        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getProxyServiceURLHttp("xmlToJsonTestProxy")), xmlPayload, requestHeader);

        Assert.assertEquals(response.getData(), "{\"ns_stock\":{\"@xmlns_ns\":\"http://services.samples\",\"ns_name\":\"WSO2\"}}",
                "Invalid XML to JSON conversion. " + response.getData());
    }

    /**
     * Test the property synapse.commons.json.buildValidNCNames=true
     * Replace "_JsonReader_32_" in XML payload with spaces in Json
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test XML to JSON build Valid NC Names")
    public void testXmlToJsonBuildValidNCNames() throws Exception {

        String xmlPayload = "<stock_JsonReader_32_quote>\n" +
                "               <name>WSO2</name>\n" +
                "            </stock_JsonReader_32_quote>";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "text/xml");
        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getProxyServiceURLHttp("xmlToJsonTestProxy")), xmlPayload, requestHeader);

        Assert.assertEquals(response.getData(), "{\"stock quote\":{\"name\":\"WSO2\"}}",
                "Invalid XML to JSON conversion. " + response.getData());
    }

    /**
     * Test the property synapse.commons.json.output.autoPrimitive=false
     * Add double quotes around the primitive values
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test XML to JSON auto primitive")
    public void testXmlToJsonAutoPrimitive() throws Exception {

        String xmlPayload = "<stock>\n" +
                "               <name>WSO2</name>\n" +
                "               <price>10</price>\n" +
                "            </stock>";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "text/xml");
        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getProxyServiceURLHttp("xmlToJsonTestProxy")), xmlPayload, requestHeader);

        Assert.assertEquals(response.getData(), "{\"stock\":{\"name\":\"WSO2\",\"price\":\"10\"}}",
                "Invalid XML to JSON conversion. " + response.getData());
    }

    /**
     * Test the property synapse.commons.json.output.jsonoutAutoArray=false
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test XML to JSON Array conversion")
    public void testXmlToJsonArray() throws Exception {

        String xmlPayload = "<stocks>" +
                "               <stock>\n" +
                "                   <name>WSO2</name>\n" +
                "                   <price>10</price>\n" +
                "               </stock>\n"             +
                "               <stock>\n"              +
                "                   <name>IBM</name>\n" +
                "                   <price>15</price>\n" +
                "               </stock>" +
                "            </stocks>";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "text/xml");
        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getProxyServiceURLHttp("xmlToJsonTestProxy")), xmlPayload, requestHeader);

        Assert.assertEquals(response.getData(),
                "{\"stocks\":{\"stock\":{\"name\":\"WSO2\",\"price\":\"10\"},\"stock\":{\"name\":\"IBM\",\"price\":\"15\"}}}",
                "Invalid XML to JSON array conversion. " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }
}
