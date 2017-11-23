/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.esb.mediator.test.callOut;

import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import static org.testng.Assert.assertEquals;

/**
 * Test case to check whether callout mediator is handling an empty reponse from the endpoint
 */
public class CalloutMediatorHandlingEmptySuccessResponseTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb")
    public void testForwardingWithInMemoryStore() throws Exception {
        String requestPayload = "{\"hello\":\"world\"}";
        SimpleHttpClient httpClient = new SimpleHttpClient();
        String url = getApiInvocationURL("callouttest");
        HttpResponse httpResponse = httpClient.doPost(url, null, requestPayload, "application/json");
        String responsePayload = httpClient.getResponsePayload(httpResponse);
        assertEquals(responsePayload, requestPayload,
                "Response not matching with the request! Response received is :" + responsePayload);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}