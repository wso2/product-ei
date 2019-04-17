/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.message.conversion;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This Test Case Validates SOAP11 to JSON Conversion and JSON to SOAP11 Conversion.
 * This is written to check whether the conversion happens properly and no exception is thrown
 * when there is no content aware mediator in the flow.
 * Refer - https://github.com/wso2/product-ei/issues/414
 */
public class SOAP11ToJSONConversion extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        verifyProxyServiceExistence("soapToJson");
        verifyProxyServiceExistence("jsonBE");
        verifyProxyServiceExistence("jsonToSoap11");
        verifyProxyServiceExistence("soapRespond");
    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @Test(groups = "wso2.esb", description = "SOAP11 to JSON Conversion")
    public void testSOAP11ToJson() throws Exception {

        URL endpoint = new URL(getProxyServiceURLHttp("soapToJson"));

        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "text/xml");

        HttpResponse response = HttpRequestUtil.doPost(endpoint, "<test>123</test>", header);

        String expectedPayload = "<jsonObject><Hello>World</Hello></jsonObject>";
        Assert.assertEquals(expectedPayload, response.getData(), "Expected payload not received.");
    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @Test(groups = "wso2.esb", description = "JSon to SOAP11 Conversion")
    public void testJsonToSOAP11() throws Exception {

        URL endpoint = new URL(getProxyServiceURLHttp("jsonToSoap11"));

        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");
        String inputPayload = "{\"Hello\":\"World\"}";

        HttpResponse response = HttpRequestUtil.doPost(endpoint, inputPayload, header);
        Assert.assertEquals(inputPayload, response.getData(), "Expected payload not received.");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
