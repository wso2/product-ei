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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 *
 * Test for an API with a back end service responding with HTTP 202 status code and a json payload.
 */
public class ESBJAVA4999ResponsePayloadWithHTTPAcceptedTestCase extends ESBIntegrationTest {

    /**
     * Deploy the test API and the back end service
     *
     * @throws Exception throws when initialisation process failed
     */
    @BeforeClass(alwaysRun = true)
    public void deployAPI() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA-4999.xml");
    }

    /**
     * Send a request to the API and check the back end response transmitted through the API
     * Response should be a HTTP 202 response with a json payload
     *
     * @throws Exception throws on test failure
     */
    @Test(groups = {"wso2.esb"}, description = "test for the respond for 202 status code and payload")
    public void testPayloadWithHTTP202Response() throws Exception {
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "application/json");

        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getApiInvocationURL("payload-with-202/frontend/")), "{}", requestHeader);

        Assert.assertEquals(response.getResponseCode(), 202);
        Assert.assertFalse(response.getData().isEmpty());
    }

    @AfterClass(alwaysRun = true)
    public void cleanupSynapseConfig() throws Exception {
        super.cleanup();
    }
}
