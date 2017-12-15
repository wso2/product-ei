/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.esb.passthru.transport.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test case for testing relative url path as the location header.
 */

public class LocationHeaderWithRelativeURLPathTestCase extends ESBIntegrationTest {

    private final String LOCATION_HEADER_NAME = "Location";
    private final String EXPECTED_LOCATION_HEADER = "http://127.0.0.1:8480/services";

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Test to check whether location header value persists in the http response")
    public void testForLocationHeaderInResponse() throws Exception {

        String proxyServiceUrl = getProxyServiceURLHttp("LocationHeaderTestProxy");

        String requestPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <testBody>\n"
                + "      <foo/>\n"
                + "      </testBody>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>\n";

        Map<String, String> headers = new HashMap<>();
        headers.put("SOAPAction", "urn:getQuote");

        HttpResponse response = HttpRequestUtil.doPost(new URL(proxyServiceUrl), requestPayload, headers);
        Map<String, String> responseHeaders = response.getHeaders();
        assertNotNull(responseHeaders, "Error in retrieving the response headers!");
        String locationHeaderValue = responseHeaders.get(LOCATION_HEADER_NAME);
        assertNotNull(locationHeaderValue, "Location header not set!");
        assertEquals(locationHeaderValue, EXPECTED_LOCATION_HEADER, "Incorrect location header!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}