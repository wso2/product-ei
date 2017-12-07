/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.test.utils.TestUtils;

import java.io.IOException;

/**
 * Integration tests for soap connector.
 */
public class SoapConnectorTest {
    private static final Logger log = LoggerFactory.getLogger(SoapConnectorTest.class);

    private boolean serverStarted;

    @BeforeTest
    public void startServer() {
        serverStarted = TestUtils.startServer("samples/soap-connector/soap-connector-sample.balx");
    }

    @Test
    public void testSoapConnector() {
        if (!serverStarted) {
            Assert.fail("Error running the test, server not started");
        }

        String symbol = "ABC";
        String requestPayload = "<ser:getSimpleQuote xmlns:ser=\"http://services.samples\">\n"
                + "<ser:symbol>" + symbol + "</ser:symbol>\n"
                + "</ser:getSimpleQuote>";

        String url = "http://localhost:9090/soapSample";

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);

        try {
            RequestEntity requestEntity = new StringRequestEntity(requestPayload, "application/xml", "UTF-8");

            post.setRequestEntity(requestEntity);

            int status = client.executeMethod(post);
            Assert.assertEquals(200, status);
            String responseBody = post.getResponseBodyAsString();
            String responseSymbol = responseBody.split("<ax21:symbol>")[1].split("</ax21:symbol>")[0].trim();
            Assert.assertEquals(responseSymbol, symbol);

        } catch (IOException e) {
            log.error("Error while invoking the server ", e);
        }
    }

    @AfterTest
    public void stopServer() {
        if (!TestUtils.stopServer()) {
            log.error("Error Stopping the server");
        }
    }

}
