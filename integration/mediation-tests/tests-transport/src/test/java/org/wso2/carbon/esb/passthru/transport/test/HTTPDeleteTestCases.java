/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Testcase to test HTTP DELETE support
 */
public class HTTPDeleteTestCases extends ESBIntegrationTest{

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/http_transport/HTTPDeleteSupportTestSynapseConfig.xml");
    }


    @Test(groups = "wso2.esb", description = "Test Invoking proxy with HTTP DELETE request")
    public void testInvokeBasicProxy () throws Exception {

        String requestMessage = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body>"
                + "<Request><Message>This is request message to test HTTP DELETE</Message></Request>"
                + "</soapenv:Body></soapenv:Envelope>";

        String expectedResponse = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">"
                + "<soapenv:Body><Response><HTTPMethod>DELETE</HTTPMethod>"
                + "<RequestMessage>This is request message to test HTTP DELETE</RequestMessage></Response>"
                + "</soapenv:Body></soapenv:Envelope>";

        SimpleHttpClient httpClient = new SimpleHttpClient();
        String url = getProxyServiceURLHttp("testDeleteProxy");
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/soap+xml; charset=UTF-8; action=\"urn:mediate\"");

        HttpResponse response = httpClient.doDeleteWithPayload(url, header, requestMessage, "application/soap+xml");

        Assert.assertNotNull(response, "Response Null");
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Unexpected response status code");
        Assert.assertEquals(SimpleHttpClient.responseEntityBodyToString(response), expectedResponse);

    }


    @Test(groups = "wso2.esb", description = "Test Invoking api with HTTP DELETE request")
    public void testInvokeBasicAPI () throws Exception {

        String requestMessage = "{\"Request\" : {\"Message\":\"This is request message to test HTTP DELETE\"}}";

        String expectedResponse = "{\"HTTPMethod\":\"DELETE\",\"Response\": {\"RequestMessage\":\"This is request message to test HTTP DELETE\"}}";

        SimpleHttpClient httpClient = new SimpleHttpClient();
        String url = getApiInvocationURL("testbackend");

        HttpResponse response = httpClient.doDeleteWithPayload(url, null, requestMessage, "application/json");

        Assert.assertNotNull(response, "Response Null");
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Unexpected response status code");
        Assert.assertEquals(SimpleHttpClient.responseEntityBodyToString(response), expectedResponse);

    }

    /**
     * This testcase tests making DELETE request to external service from ESB.
     * TEST Client -> testdeletecall API -> testbackend API
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test Invoking api containing call mediator to test HTTP DELETE with Call Mediator")
    public void testHttpDeleteWithCallMediator () throws Exception {

        String requestMessage = "{\"Request\" : {\"Message\":\"Hello\"}}";

        String expectedResponse = "{\"HTTPMethod\":\"DELETE\",\"Response\": {\"RequestMessage\":\"This is request message to test HTTP DELETE\"}}";

        SimpleHttpClient httpClient = new SimpleHttpClient();
        String url = getApiInvocationURL("testdeletecall");

        HttpResponse response = httpClient.doPost(url, null, requestMessage, "application/json");

        Assert.assertNotNull(response, "Response Null");
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Unexpected response status code");
        Assert.assertEquals(SimpleHttpClient.responseEntityBodyToString(response), expectedResponse);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }





}
