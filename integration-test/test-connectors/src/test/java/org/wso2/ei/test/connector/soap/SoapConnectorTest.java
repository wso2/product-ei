/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.ei.test.connector.soap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.test.utils.TestUtils;

import java.io.IOException;

/**
 * Integration tests for soap connector.
 */
public class SoapConnectorTest {
    private static final Logger log = LoggerFactory.getLogger(SoapConnectorTest.class);

    private boolean serverStarted;

    @BeforeClass(description = "Starts the server with the SoapConnectorTest.bal file")
    public void startServer() {
        serverStarted = TestUtils.startServer(
                "integration-test/test-connectors/src/test/java/org/wso2/ei/test/connector/soap/SoapConnectorTest.bal");
    }

    @Test(description = "Tests the sendReceive method of the soap connector")
    public void testSendReceive() {
        String symbol = "ABC";
        String responseBody = sendToBackend(symbol, 200, "sendReceive");

        String responseSymbol = responseBody.split("<ax21:symbol>")[1].split("</ax21:symbol>")[0].trim();
        Assert.assertEquals(responseSymbol, symbol);
    }

    @Test(description = "Tests the sendReboust method")
    public void testSendRobust() {
        String symbol = "DEF";
        String responseBody = sendToBackend(symbol, 200, "sendRobust");

        Assert.assertEquals(responseBody, "Success");
    }

    @Test(description = "Tests the error returned of the sendRobust method of the soap connector")
    public void testSendRobustTimeout() {
        String symbol = "ABC";
        String responseBody = sendToBackend(symbol, 504, "sendRobustTimeout");

        Assert.assertEquals(responseBody, "Gateway Timeout");
    }

    @Test(description = "Tests the fireAndForget method of the soap connector")
    public void testFireAndForget() {
        String symbol = "GHI";
        sendToBackend(symbol, 200, "fireAndForget");
    }

    /**
     * This is a convenient method for sending to the backend and asserts the status.
     *
     * @param symbol         the symbol to send to backend.
     * @param expectedStatus the expected status from the call to the backend.
     * @param invoke         the path to invoke when calling the backend.
     * @return the response as a string.
     */
    private String sendToBackend(String symbol, int expectedStatus, String invoke) {
        if (!serverStarted) {
            Assert.fail("Error running the test, server is not started");
        }

        String requestPayload = "<ser:getSimpleQuote xmlns:ser=\"http://services.samples\">\n"
                + "<ser:symbol>" + symbol + "</ser:symbol>\n"
                + "</ser:getSimpleQuote>";

        String url = "http://localhost:9090/SimpleStockQuoteService/" + invoke;

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);

        try {
            RequestEntity requestEntity = new StringRequestEntity(requestPayload, "application/xml", "UTF-8");

            post.setRequestEntity(requestEntity);

            int status = client.executeMethod(post);
            Assert.assertEquals(status, expectedStatus, "Incorrect status of the response");
            return post.getResponseBodyAsString();

        } catch (IOException e) {
            log.error("Error while invoking the server ", e);
        }
        return null;
    }

    @AfterClass(description = "Stops the server and logs if it fails")
    public void stopServer() {
        if (!TestUtils.stopServer()) {
            log.error("Error stopping the server");
        }
    }
}
