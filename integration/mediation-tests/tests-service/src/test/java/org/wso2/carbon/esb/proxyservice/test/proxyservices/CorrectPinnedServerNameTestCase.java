/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.proxyservice.test.proxyservices;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.http.HttpResponse;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Test case to test if the proxy is getting deployed when the correct value is given for the pinned server.
 */
public class CorrectPinnedServerNameTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = { "wso2.esb" },
          description = "Testing if the proxy is getting deployed when the correct value is given for the pinned server")
    public void testDeployedProxy() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/xml");

        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <testBody>\n"
                + "      <foo/>\n"
                + "      </testBody>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>\n";

        String testResponse = "<testResponse xmlns=\"http://ws.apache.org/ns/synapse\"><foo/></testResponse>";
        SimpleHttpClient simpleHttpClient = new SimpleHttpClient();

        String proxyServiceURL = getProxyServiceURLHttp("CorrectPinnedServerTestProxy");
        HttpResponse response = simpleHttpClient.doPost(proxyServiceURL, headers, payload, "application/xml");
        String responsePayload = simpleHttpClient.getResponsePayload(response);
        assertEquals(responsePayload, testResponse, "Proxy not deployed successfully!");
    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        super.cleanup();
    }
}