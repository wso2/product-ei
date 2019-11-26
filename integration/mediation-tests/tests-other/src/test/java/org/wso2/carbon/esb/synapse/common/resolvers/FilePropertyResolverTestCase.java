/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.synapse.common.resolvers;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This test case is to validate correctly resolving the file property variables
 */
public class FilePropertyResolverTestCase extends ESBIntegrationTest {

    private static final String targetApiName = "filePropertyTestAPI";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

    }

    @Test(groups = "wso2.esb" , description = "This method is used to verify the resolved variable from file property")
    public void testFilePropertyResolveVariable() throws IOException {

        String contentType = "text/xml";
        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "<soapenv:Body>\n" +
                "\t<m0:getQuote xmlns:m0=\"http://services.samples\">\n" +
                "        <m0:request>\n" +
                "            <m0:symbol>WSO2</m0:symbol>\n" +
                "        </m0:request>\n" +
                "     </m0:getQuote>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String url = getApiInvocationURL(targetApiName);

        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", contentType);
        headers.put("SOAPAction", "urn:mediate");

        SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.doPost(url, headers, payload, contentType);
        String responsePayload = httpClient.getResponsePayload(response);
        boolean ResponseContainsWSO2Info = responsePayload.contains("getQuoteResponse") && responsePayload.contains("WSO2");
        Assert.assertTrue(ResponseContainsWSO2Info);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}