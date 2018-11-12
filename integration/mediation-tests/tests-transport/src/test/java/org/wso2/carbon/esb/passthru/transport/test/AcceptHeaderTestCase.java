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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Test case tests for building the message when Content-Type transport header is different from Accept header.
 * In this case, Content-Type is application/x-www-form-urlencoded and the Accept header is application/json.
 *
 * Related issues: https://github.com/wso2/product-ei/issues/2842
 *                 https://github.com/wso2/product-ei/issues/2835
 */
public class AcceptHeaderTestCase extends ESBIntegrationTest {

    @BeforeClass public void init() throws Exception {
        super.init();
    }

    @Test(groups = { "wso2.esb" }, description = "Test for response when the Accept header is different from request "
            + "Content-Type, and build the message within the inflow.")
    public void testAcceptHeader() throws Exception {

        verifyAPIExistence("AcceptHeaderTestAPI");

        String expectedOutput = "Accept Header Test : success";
        String payload = "accept-header-test-key=Test%20Accept%20header%20'\\''application%2Fjson'\\'"
                + "'%20with%20Content-Type%20application%2Fx-www-form-urlencoded.";

        String contentType = "application/x-www-form-urlencoded";
        String accept = "application/json";

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Content-Type", contentType);
        headers.put("Accept", accept);

        HttpResponse response = httpClient.doPost(getApiInvocationURL("acceptHeaderTest"), headers, payload,
                contentType);

        Assert.assertTrue(httpClient.getResponsePayload(response).contains(expectedOutput), "Error while building the "
                + "message when the Content-Type is different from Accept header type.");
    }

    @AfterClass public void cleanUp() throws Exception {
        super.cleanup();
    }
}
