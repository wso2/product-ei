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

import com.ibm.wsdl.extensions.http.HTTPConstants;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils.getResponsePayload;

/**
 * This test class is to test POX to JSON Transformation using MessageType Property. Once a POX request is sent to
 * proxy service, the POX message will be transformed to JSON using MessageType Property and send the message to
 * backend service. The backend service will send back a JSON response and it will be transformed to POX.
 */
public class PoxToJsonUsingMessageTypeTest extends ScenarioTestBase {

    private String apiName = "1_2_2_API_pox_to_json_using_message_type";
    private String apiInvocationUrl;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        apiInvocationUrl = getApiInvocationURLHttp(apiName);
    }

    @Test(description = "1.2.2.1", dataProvider = "1.2.2.1")
    public void convertValidPoxToJsonUsingMessageType(String request, String expectedResponse, String header) throws Exception {
        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl, request, HttpConstants.MEDIA_TYPE_APPLICATION_XML,
                                           header, expectedResponse, 200,
                                           "Valid Pox To Json transformation Using MessageType property");
    }

    @Test(description = "1.2.2.2")
    public void convertMalformedPoxToJsonUsingMessageType() throws Exception {

        String request = "<m0:getQuote xmlns:m0=\"http://services.samples/xsd\">\n" +
                         "   <m0:request>\n" +
                         "      <m0:symbol>IBM</m0:symbol>\n" +
                         "</m0:getQuote>";

        String responseSubstring = "Unexpected close tag &lt;/m0:getQuote>; expected &lt;/m0:request>";

        String header = "1_2_2_2_1";

        HTTPUtils.invokePoxEndpointAndCheckContains(apiInvocationUrl, request, HttpConstants.MEDIA_TYPE_APPLICATION_XML,
                                                  header, responseSubstring, 500,
                                                  "Malformed Pox To Json transformation Using MessageType property");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }

    @DataProvider(name = "1.2.2.1")
    public Iterator<Object[]> soapToJson_1_1_2_3() throws Exception {
        String testCase = "1.2.2.1";
        return getRequestResponseHeaderList(testCase).iterator();
    }
}
