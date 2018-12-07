/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SoapToJsonUsingMessageTypeTest extends ScenarioTestBase {

    private String cappNameWithVersion = "scenario_1_1-synapse-configCompositeApplication_1.0.0";
    private String proxyServiceName = "1_1_2_soap_to_json_using_message_type";
    private String proxyServiceUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = getProxyServiceURLHttp(proxyServiceName);
        deployCarbonApplication(cappNameWithVersion);
    }

    @Test(description = "1.1.2.1-Valid Soap To Json transformation", enabled = true, dataProvider = "1.1.2.1")
    public void convertValidSoapToJson(String request, String expectedResponse, String header) throws Exception {
        log.info("Executing test case 1.1.2.1");
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request, HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.2.1: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "SOAP to JSON transformation failed");

        Assert.assertEquals(expectedResponse, responsePayload);
    }

    @Test(description = "1.1.2.2", enabled = true, dataProvider = "1.1.2.2")
    public void convertMalformedSoapToJson(String request, String expectedResponse, String header) throws Exception {
        log.info("Executing test case 1.1.2.2");
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request, HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.2.2: " + responsePayload);

        Assert.assertEquals(expectedResponse, responsePayload);
    }

    @Test(description = "1.1.2.3", enabled = true, dataProvider = "1.1.2.3")
    public void convertLargeSoapToJson(String request, String expectedResponse, String header) throws Exception {
        log.info("Executing test case 1.1.2.3");
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request, HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.2.3: " + responsePayload);

        Assert.assertEquals(expectedResponse.replaceAll("\\s+",""), responsePayload.replaceAll("\\s+",""));
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }

    @DataProvider(name = "1.1.2.1")
    public Iterator<Object[]> soapToJson_1_1_2_1() throws Exception {
        String testCase = "1.1.2.1";
        return getRequestResponseHeaderList(testCase).iterator();
    }

    @DataProvider(name = "1.1.2.2")
    public Iterator<Object[]> soapToJson_1_1_2_2() throws Exception {
        String testCase = "1.1.2.2";
        return getRequestResponseHeaderList(testCase).iterator();
    }

    @DataProvider(name = "1.1.2.3")
    public Iterator<Object[]> soapToJson_1_1_2_3() throws Exception {
        String testCase = "1.1.2.3";
        return getRequestResponseHeaderList(testCase).iterator();
    }
}
