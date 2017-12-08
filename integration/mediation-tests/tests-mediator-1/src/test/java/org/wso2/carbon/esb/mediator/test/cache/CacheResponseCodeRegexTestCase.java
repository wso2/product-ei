/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.cache;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Cache Mediator - verify 'responseCode' parameter with caching
 * <p>
 * This testcase is to verify that caching works only with the defined response code
 */
public class CacheResponseCodeRegexTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.esb",
          description = "Verify caching works for the defined response code regex")
    public void testCachingResponseCodeRegex() throws Exception {

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("CODE", "202");
        requestHeader.put("symbol", "RegexPattern");

        // Backend respond with 202 status code
        HttpResponse response1 = HttpRequestUtil.doGet((getApiInvocationURL("ResponseCodeRegex")), requestHeader);
        assertNotNull(response1, "Response is null");
        String[] firstmsg1 = response1.getData().split("<ax21:change>");
        String[] secondmsg1 = firstmsg1[1].split("</ax21:change>");
        String change1 = secondmsg1[0];
        assertNotNull(change1, "change value is null");
        assertEquals(response1.getResponseCode(), 202, "Unexpected response code");

        HttpResponse response2 = HttpRequestUtil.doGet((getApiInvocationURL("ResponseCodeRegex")), requestHeader);
        assertNotNull(response2, "Response is null");
        String[] firstmsg2 = response2.getData().split("<ax21:change>");
        String[] secondmsg2 = firstmsg2[1].split("</ax21:change>");
        String change2 = secondmsg2[0];
        assertNotNull(change2, "change value is null");
        assertEquals(response2.getResponseCode(), 202, "Unexpected response code");

        assertEquals(change1, change2, "Response caching did not work for the defined response code regex ");

        Map<String, String> requestHeader2 = new HashMap<>();
        requestHeader2.put("CODE", "401");
        requestHeader2.put("symbol", "RegexPattern");

        // Backend respond with 401 status code
        HttpResponse response3 = HttpRequestUtil.doGet((getApiInvocationURL("ResponseCodeRegex")), requestHeader2);
        assertNotNull(response3, "Response is null");
        String[] firstmsg3 = response3.getData().split("<ax21:change>");
        String[] secondmsg3 = firstmsg3[1].split("</ax21:change>");
        String change3 = secondmsg3[0];
        assertNotNull(change3, "change value is null");
        assertEquals(response3.getResponseCode(), 401, "Unexpected response code");

        HttpResponse response4 = HttpRequestUtil.doGet((getApiInvocationURL("ResponseCodeRegex")), requestHeader2);
        assertNotNull(response4, "Response is null");
        String[] firstmsg4 = response4.getData().split("<ax21:change>");
        String[] secondmsg4 = firstmsg4[1].split("</ax21:change>");
        String change4 = secondmsg4[0];
        assertNotNull(change4, "change value is null");
        assertEquals(response4.getResponseCode(), 401, "Unexpected response code");

        assertEquals(change3, change4, "Response caching did not work for the defined response code regex ");

        Map<String, String> requestHeader3 = new HashMap<>();
        requestHeader3.put("CODE", "500");
        requestHeader3.put("symbol", "RegexPattern");

        // Backend respond with 500 status code
        HttpResponse response5 = HttpRequestUtil.doGet((getApiInvocationURL("ResponseCodeRegex")), requestHeader3);
        assertNotNull(response5, "Response is null");
        String[] firstmsg5 = response5.getData().split("<ax21:change>");
        String[] secondmsg5 = firstmsg5[1].split("</ax21:change>");
        String change5 = secondmsg5[0];
        assertNotNull(change5, "change value is null");
        assertEquals(response5.getResponseCode(), 500, "Unexpected response code");

        HttpResponse response6 = HttpRequestUtil.doGet((getApiInvocationURL("ResponseCodeRegex")), requestHeader3);
        assertNotNull(response6, "Response is null");
        String[] firstmsg6 = response6.getData().split("<ax21:change>");
        String[] secondmsg6 = firstmsg6[1].split("</ax21:change>");
        String change6 = secondmsg6[0];
        assertNotNull(change6, "change value is null");
        assertEquals(response6.getResponseCode(), 500, "Unexpected response code");

        assertNotEquals(change5, change6, "Response caching works for the undefined response code");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
