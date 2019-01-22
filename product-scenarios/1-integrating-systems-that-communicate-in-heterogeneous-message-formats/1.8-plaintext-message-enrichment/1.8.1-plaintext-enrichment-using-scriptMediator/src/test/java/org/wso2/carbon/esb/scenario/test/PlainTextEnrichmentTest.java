/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

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
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.io.IOException;

/**
 * This test is to send a plain test message to a EI proxy service. The proxy service will modify the plaintext
 * message and send to backend service. Backend service will respond with a plaintext message.
 */
public class PlainTextEnrichmentTest extends ScenarioTestBase {

    private final String carFileName = "scenario_1_8-synapse-configCompositeApplication_1.0.0";
    private String proxyServiceUrl;
    private final String textRequestToSend = "WSO2 cooperation is a good company";
    //enrichment done by backend server
    private final String serverAppended = "incoming message is: ";
    //enrichment done by EI
    private final String enrichment = " to work in";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = getProxyServiceURLHttp("1_8_1_1_Proxy_PlainTextReceiver");
    }

    @Test(description = "1.8.1.1")
    public void convertPlainTextMessageToSOAP() throws IOException {
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);

        //create a REST client and send the payload to proxy
        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doPost(proxyServiceUrl , textRequestToSend,
                HttpConstants.MEDIA_TYPE_TEXT_PLAIN);
        String responsePayload = HTTPUtils.getResponsePayload(httpResponse);

        String expectedString = serverAppended + textRequestToSend + enrichment;

        Assert.assertEquals(responsePayload, expectedString);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(httpResponse), 200,
                "plaintext enrichment failed");

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
