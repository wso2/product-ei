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

package org.wso2.carbon.esb.sequence.fault;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.servers.SimpleHTTPServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case to verify Fault Sequence execution when malformed payload received as response from the BE
 */
public class TestFaultSequenceExecutionForMalformedResponse extends ESBIntegrationTest {

    private static final String targetApiName = "simpleApiToTestResponsePayload";
    private  SimpleHTTPServer simpleHTTPServer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        verifyAPIExistence(targetApiName);

        //Prepare and start mock back end server
        int port = 8089;
        simpleHTTPServer = new SimpleHTTPServer(port);
        simpleHTTPServer.createContext("/testBE", new malformedResponseBEHandler());
        simpleHTTPServer.start();//start server
    }

    @Test(groups = "wso2.esb",
          description = "Testcase to test whether the the fault sequence get executed when malformed payload received from BE")
    public void testFaultSeqExecutionForMalformedPayload() throws IOException {
        //Send request to ESB
        String contentType = "text/xml";
        String payload = "<Request><Message>This is request message</Message></Request>";
        String url = getApiInvocationURL(targetApiName);

        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", contentType);

        SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.doPost(url, headers, payload, contentType);
        String responsePayload = httpClient.getResponsePayload(response);

        Assert.assertNotNull(responsePayload, "Error occurred while retrieving response payload: entity null");
        Assert.assertTrue(responsePayload.contains("Response from fault sequence"),
                "Fault sequence did not execute due to malformed response");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        simpleHTTPServer.stop();//shutdown mock BE server
        super.cleanup();
    }

    /**
     * HttpHandler implementation to handle request
     */
    private static class malformedResponseBEHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Content-Type", "text/xml");

            //response payload
            String response = "<a></b>";

            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream responseStream = httpExchange.getResponseBody();
            try {
                responseStream.write(response.getBytes(Charset.defaultCharset()));
            } finally {
                responseStream.close();
            }
        }
    }
}
