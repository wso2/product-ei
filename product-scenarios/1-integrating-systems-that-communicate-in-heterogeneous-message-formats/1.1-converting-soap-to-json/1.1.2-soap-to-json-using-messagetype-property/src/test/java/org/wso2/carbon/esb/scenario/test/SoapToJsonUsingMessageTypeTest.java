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
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This test class is to test SOAP to JSON Transformation using MessageType Property. Once a SOAP request is sent to
 * proxy service, the SOAP message will be transformed to JSON using MessageType Property and send the message to
 * backend service. The backend service will send back a JSON response and it will be transformed to SOAP format.
 */

public class SoapToJsonUsingMessageTypeTest extends ScenarioTestBase {

    private String cappNameWithVersion = "approach_1_1_2_synapse_configCompositeApplication_1.0.0";
    private String cappName = "approach_1_1_2_synapse_configCompositeApplication";
    private String proxyServiceName = "1_1_2_soap_to_json_using_message_type";
    private String proxyServiceUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = getProxyServiceURLHttp(proxyServiceName);
        deployCarbonApplication(cappNameWithVersion);
    }

    @Test(description = "1.1.2.1-Valid Soap To Json transformation Using MessageType property", enabled = true,
          dataProvider = "1.1.2.1")
    public void convertValidSoapToJsonUsingMessageType(String request, String expectedResponse, String header) throws Exception {
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.2.1: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "SOAP to JSON transformation failed");

        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @Test(description = "1.1.2.2-Malformed Soap to Json Using MessageType property", enabled = true,
          dataProvider = "1.1.2.2")
    public void convertMalformedSoapToJsonUsingMessageType(String request, String expectedResponse, String header)
            throws Exception {
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.2.2: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500, "SOAP to JSON transformation failed");

        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @Test(description = "1.1.2.3-Large Soap to Json Using MessageType property", enabled = true,
          dataProvider = "1.1.2.3")
    public void convertLargeSoapToJsonUsingMessageType(String request, String expectedResponse, String header)
            throws Exception {
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.2.3: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "SOAP to JSON transformation failed");

        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
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
