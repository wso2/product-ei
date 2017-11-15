/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.rest.test.header;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;

public class HTTPResponseCodeTestCase extends ESBIntegrationTest {

    private Log log = LogFactory.getLog(HTTPResponseCodeTestCase.class);
    private HttpServer server = null;
    private static int responseCode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        String relativePath = "/artifacts/ESB/synapseconfig/esbjava2283/api.xml";
        ESBTestCaseUtils util = new ESBTestCaseUtils();
        relativePath = relativePath.replaceAll("[\\\\/]", File.separator);
        OMElement apiConfig = util.loadResource(relativePath);
        addApi(apiConfig);
    }

    @Test(groups = {"wso2.esb" }, description = "Test whether ESB pass-through responses with different response codes.",
            dataProvider = "getResponseCodes")
    public void testReturnResponseCode(
            int responseCode) throws Exception {
        HTTPResponseCodeTestCase.responseCode = responseCode;
        //Starting backend server
        int port = 8089;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/gettest", new ResponseHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        //Invoke API deployed in ESB
        switch (responseCode) {
        case 404:
            String contentType = "text/html";
            String url = getApiInvocationURL("/serviceTest/notfound");
            sendRequest(url, contentType);
        default:
            contentType = "text/xml";
            url = getApiInvocationURL("/serviceTest/test");
            sendRequest(url, contentType);
        }

        server.stop(0);

    }

    // Construct response that should be returned from the backend server
    private class ResponseHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Content-Type", "text/xml");
            String response = "This is Response status code test case";
            httpExchange.sendResponseHeaders(responseCode, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Function to send an HTTP get request and assert response.
    private void sendRequest(String url, String contentType) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            log.error("Error Occured while sending http get request. " + e);
        }

        assertEquals(response.getStatusLine().getStatusCode(), responseCode, "response code doesn't match");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @DataProvider(name = "responseCodeProvider")
    public Object[][] getResponseCodes() {
        return new Object[][] { { 200 }, { 400 }, { 403 }, { 404 }, { 500 }, { 501 }, { 503 }, };
    }

}