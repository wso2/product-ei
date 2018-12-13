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
import org.wso2.carbon.esb.scenario.test.common.StringUtil;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This test class is to test SOAP to JSON Transformation using Data Mapper Mediator. Once a SOAP request is sent to
 * proxy service, the SOAP message will be transformed to JSON using Data Mapper and send the message to
 * backend service. The backend service will send back a JSON response and it will be transformed to SOAP format.
 */

public class SoapToJsonUsingDataMapperTest extends ScenarioTestBase {

    private String cappNameWithVersion = "approach_1_1_3_synapse_configCompositeApplication_1.0.0";
    private String cappName = "approach_1_1_3_synapse_configCompositeApplication";
    private String apiName = "1_1_3_API_soap_to_json_using_data_mapper";
    private String apiInvocationUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        apiInvocationUrl = getApiInvocationURLHttp(apiName);
        deployCarbonApplication(cappNameWithVersion);
    }

    @Test(description = "1.1.3.1-Valid Soap To Json transformation Using Data Mapper", enabled = true,
          dataProvider = "1.1.3.1")
    public void convertValidSoapToJsonUsingDataMapper(String request, String expectedResponse, String header)
            throws Exception {
        log.info("apiInvocationUrl is set as : " + apiInvocationUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(apiInvocationUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.3.1: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "SOAP to JSON transformation failed");
        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @Test(description = "1.1.3.2-Malformed Soap To Json transformation Using Data Mapper", enabled = true,
          dataProvider = "1.1.3.2")
    public void convertMalformedValidSoapToJsonUsingDataMapper(String request, String expectedResponse, String header)
            throws Exception {
        log.info("apiInvocationUrl is set as : " + apiInvocationUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(apiInvocationUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.3.2: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500, "SOAP to JSON transformation failed");
        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @Test(description = "1.1.3.3-Large Soap To Json transformation Using Data Mapper", enabled = true,
          dataProvider = "1.1.3.3")
    public void convertLargeSoapToJsonUsingDataMapper(String request, String expectedResponse, String header)
            throws Exception {
        log.info("apiInvocationUrl is set as : " + apiInvocationUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(apiInvocationUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.3.3: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "SOAP to JSON transformation failed");
        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }

    @DataProvider(name = "1.1.3.1")
    public Iterator<Object[]> soapToJson_1_1_3_1() throws Exception {
        String testCase = "1.1.3.1";
        return getRequestResponseHeaderList(testCase).iterator();
    }

    @DataProvider(name = "1.1.3.2")
    public Iterator<Object[]> soapToJson_1_1_3_2() throws Exception {
        String testCase = "1.1.3.2";
        return getRequestResponseHeaderList(testCase).iterator();
    }

    @DataProvider(name = "1.1.3.3")
    public Iterator<Object[]> soapToJson_1_1_3_3() throws Exception {
        String testCase = "1.1.3.3";
        return getRequestResponseHeaderList(testCase).iterator();
    }

}
