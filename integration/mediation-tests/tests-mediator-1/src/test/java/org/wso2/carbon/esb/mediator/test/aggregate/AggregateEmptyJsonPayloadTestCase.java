/*
 * Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.aggregate;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import static org.wso2.esb.integration.common.utils.Utils.assertIfSystemLogContains;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Test for aggregating no content json responses at aggregate mediator
 */
public class AggregateEmptyJsonPayloadTestCase extends ESBIntegrationTest {
    
    private static final String PROXY_NAME = "aggregateEmptyJsonPayloadTestProxy";
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        isProxyDeployed(PROXY_NAME);
    }

    /**
     * At the proxy service, requests will be sent iteratively for the no content response backend, and recieved
     * responses will be aggregated
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test CorrelateOn in Aggregate mediator ")
    public void testAggregateEmptyJsonPayload() throws Exception {

        String inputPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<soapenv:Header/>\n" +
                "<soapenv:Body>\n" +
                "<m0:getQuote xmlns:m0=\"http://services.samples\">\n" +
                " <m0:request>IBM\n" +
                " </m0:request>\n" +
                "   <m0:request>WSO2\n" +
                " </m0:request>\n" +
                "</m0:getQuote>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String expectedOutput = "<OverallResponse " +
                "xmlns=\"http://ws.apache.org/ns/synapse\"><jsonObject xmlns=\"\"/><jsonObject " +
                "xmlns=\"\"/></OverallResponse>";

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "text/xml");
        requestHeader.put("SOAPAction", "urn:mediate");
        requestHeader.put("Accept", "application/json");
        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(PROXY_NAME)), inputPayload, requestHeader);

        Assert.assertTrue(assertIfSystemLogContains(logViewerClient, expectedOutput),
                          "No content 204 responses are not properly aggregated at the aggregate mediator.");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
