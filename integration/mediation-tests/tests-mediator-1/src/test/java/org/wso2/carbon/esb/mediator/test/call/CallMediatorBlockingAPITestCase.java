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
 *
 */
package org.wso2.carbon.esb.mediator.test.call;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests using Call mediator with blocking true, when a dynamic (resolving endpoint with a payload returned) endpoint
 * is called, the response payload is received.
 */
public class CallMediatorBlockingAPITestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        verifyAPIExistence("CallBlockingPayloadAPI");
        verifyAPIExistence("replyAPI");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test invoking dynamic endpoint with blocking call")
    public void callMediatorBlockingAPITest() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getApiInvocationURL("testBlockingApi"), null, "WSO2");
        assertNotNull(response, "Empty response received");
        boolean responseContainsWSO2 = response.getFirstElement().toString().contains("WSO2");
        assertTrue(responseContainsWSO2, "Response does not contain expected output.Received: " + response);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
