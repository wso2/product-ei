/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

/**
 * Post requests with empty body hangs inside ESB when chunking is disable.
 */
public class ESBJAVA4402MessageWithoutPayloadTestCase extends ESBIntegrationTest {
    private static final Log log = LogFactory.getLog(ESBJAVA4402MessageWithoutPayloadTestCase.class);
    private static final String PROXY_SERVICE_NAME = "TestProxy";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/MessageWithoutPayload/synapse.xml");
    }

    /**
     * Send a POST request to the endpoint "without payload" via a proxy service.
     * If chunking is disabled and if there is a content aware mediator in mediation path
     * Message will not send out to the endpoint. With the patch Message should send out
     * so there should be a response at the client side.
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.ALL })
    @Test(groups = "wso2.esb")
    public void testMessageWithoutContentType() throws Exception {
        // Get target URL
        String strURL = getProxyServiceURLHttp(PROXY_SERVICE_NAME);
        // Get SOAP action
        String strSoapAction = "urn:getQuote";
        // Prepare HTTP post
        PostMethod post = new PostMethod(strURL);
        // consult documentation for your web service
        post.setRequestHeader("SOAPAction", strSoapAction);
        // Get HTTP client
        HttpClient httpclient = new HttpClient();

        // Execute request
        try {
            //without the patch POST request without body it not going out from the ESB.
            //if we receive any response from backend test is successful.
            int result = httpclient.executeMethod(post);
            // Display status code
            log.info("Response status code: " + result);
            // http head method should return a 500 error
            assertTrue(result == 500);
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
