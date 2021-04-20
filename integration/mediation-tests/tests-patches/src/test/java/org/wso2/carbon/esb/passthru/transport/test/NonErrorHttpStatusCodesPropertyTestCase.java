/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

/**
 * This class tests non.error.http.status.codes property (only applicable to blocking calls).
 */
public class NonErrorHttpStatusCodesPropertyTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;
    private String apiUrl;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        verifyAPIExistence("testNonErrorHttpStatusCodesPropertyApi");
        apiUrl = getApiInvocationURL("testNonErrorHttpStatusCodesPropertyApi");
    }

    @Test(groups = "wso2.esb", description = "Check non.error.http.status.codes property for HTTP status code 403")
    public void testNonErrorHttpStatusCodesProperty() throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();
        logViewerClient.clearLogs();
        HttpResponse response = httpClient.doGet(apiUrl + "/test", null);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 403);
        Assert.assertFalse(Utils.checkForLog(logViewerClient, "org.apache.axis2.AxisFault: " +
                "Transport error: 403 Error: Forbidden", 10), "Did not receive expected response");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
