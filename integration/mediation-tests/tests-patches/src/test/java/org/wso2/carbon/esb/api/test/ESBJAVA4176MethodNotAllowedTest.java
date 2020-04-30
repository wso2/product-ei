/*
 *Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *WSO2 Inc. licenses this file to you under the Apache License,
 *Version 2.0 (the "License"); you may not use this file except
 *in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an
 *"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */

package org.wso2.carbon.esb.api.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

public class ESBJAVA4176MethodNotAllowedTest extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        verifyAPIExistence("methodNotAllowedAPI.xml");
    }

    @Test(groups = { "wso2.esb" }, description = "Sending http GET request for a POST resource")
    public void testMethodNotAllowed() {
        try {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("method/post"),
                null);
        Assert.fail("Method should be not found. But got a response with response code " + response.getResponseCode());
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("405"), "The method should be not found. But instead " +
                    "got an error message " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
