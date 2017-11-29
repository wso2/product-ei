package org.wso2.carbon.esb.passthru.transport.test;
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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ETagSupportTestCase extends ESBIntegrationTest {

    //TODO: Enable tests after fixing fixing SimpleHttpClient issue TA-945

    private String epr;
    private SimpleHttpClient httpClient;
    private Map<String, String> headers;
    private Header eTagHeader;

    private String payload;
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        httpClient = new SimpleHttpClient();
        epr = getProxyServiceURLHttp("ETagTestProxy");

        // Setting initial http headers
        headers = new HashMap<String, String>();
        headers.put("action", "urn:echoString");
        headers.put("Content-Type", "application/soap+xml");
        headers.put("charset", "UTF-8");

        payload = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                  "<soapenv:Body>" +
                  "<p:echoInt xmlns:p=\"http://echo.services.core.carbon.wso2.org\">" +
                  "<in>1</in>" +
                  "</p:echoInt>" +
                  "</soapenv:Body>" +
                  "</soapenv:Envelope>";

    }

    @Test(groups = "wso2.esb", description = "ETag enable test for a pass-through proxy", enabled = true)
    public void testEnableETag() throws Exception {

        HttpResponse response = httpClient.doPost(epr, headers, payload, "text/xml");

        eTagHeader = response.getFirstHeader("ETag");
        Assert.assertNotNull(eTagHeader, "ETag header is not present in the response");

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(statusCode, 200, "Incorrect status code. Expected: 200 Found: " + statusCode);
        Assert.assertNotNull(httpClient.getResponsePayload(response), "Payload should not empty");
    }

    @Test(groups = "wso2.esb", description = "If-None-Match header test for cache hit", enabled = false)
    public void testIfNoneMatchHeaderCacheHit() throws Exception {
        headers.put("If-None-Match", "\"" + eTagHeader.getValue() + "\"");

        HttpResponse response = httpClient.doPost(epr, headers, payload, "text/xml");

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(statusCode, 304, "Incorrect status code. Expected: 304 Found: " + statusCode);
        Assert.assertNotNull(httpClient.getResponsePayload(response), "Payload should not empty");
    }

    @Test(groups = "wso2.esb", description = "If-None-Match header test for cache miss", enabled = false)
    public void testIfNoneNoneMatchHeaderCacheMiss() throws Exception {

        payload = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                  "<soapenv:Body>" +
                  "<p:echoInt xmlns:p=\"http://echo.services.core.carbon.wso2.org\">" +
                  "<in>2</in>" +
                  "</p:echoInt>" +
                  "</soapenv:Body>" +
                  "</soapenv:Envelope>";

        HttpResponse response = httpClient.doPost(epr, headers, payload, "text/xml");

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(statusCode, 200, "Incorrect status code. Expected: 200 Found: " + statusCode);
        Assert.assertNotNull(httpClient.getResponsePayload(response), "Payload should not empty");

        Assert.assertTrue(!response.getFirstHeader("ETag").getValue().equals(headers.get("If-None-Match")),
                          "New ETag should be different from the old ETag value");

    }

    @Test(groups = "wso2.esb", description = "If-None-Match header with more than one value test case", enabled = false)
    public void testIfNoneMatchHeaderMoreThanOneValue() throws Exception {

        payload = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                  "<soapenv:Body>" +
                  "<p:echoInt xmlns:p=\"http://echo.services.core.carbon.wso2.org\">" +
                  "<in>1</in>" +
                  "</p:echoInt>" +
                  "</soapenv:Body>" +
                  "</soapenv:Envelope>";

        headers.remove("If-None-Match");
        headers.put("If-None-Match",
                    "\"-27-97-102-122-100-29-106112-103-39117-127-648049-356\",\"" +
                    "-16-97-102-122-100-29-106112-103-39117-127-648049-104\",\"" +
                    "263-97-102-122-100-29-106112-103-39117-127-648049-773\"");

        HttpResponse response = httpClient.doPost(epr, headers, payload, "text/xml");

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(statusCode, 304, "Incorrect status code. Expected: 304 Found: " + statusCode);
        Assert.assertNotNull(httpClient.getResponsePayload(response), "Payload should not empty");
    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        super.cleanup();
    }
}