/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

/**
 * A response with a 204 custom status code should not be have a Content-Length header.
 * https://wso2.org/jira/browse/ESBJAVA-4760
 */
public class ESBJAVA4760ContentLengthHeaderTest extends ESBIntegrationTest {
    private final int EXPECTED_HTTP_SC = 204;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA4760/ESBJAVA4760.xml");
    }

    @Test(groups = "wso2.esb", description = "Test 204 status code  with disable chunking", enabled = true)
    public void testContentLengthWithDisableChunking() throws IOException {
        HttpResponse response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("stockquoteapi") + "/view/IBM", null);
        Assert.assertEquals(response.getResponseCode(), EXPECTED_HTTP_SC, "Expected response code didn't match");
    }

    @AfterClass
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
