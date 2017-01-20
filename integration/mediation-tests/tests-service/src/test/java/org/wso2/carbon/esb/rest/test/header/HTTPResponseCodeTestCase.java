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

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
	    String relativePath = "/artifacts/ESB/synapseconfig/esbjava2283/api.xml";
	    ESBTestCaseUtils util = new ESBTestCaseUtils();
	    relativePath = relativePath.replaceAll("[\\\\/]", File.separator);
	    OMElement apiConfig = util.loadResource(relativePath);
	    addApi(apiConfig);
    }

	@Test(groups = { "wso2.esb" }, description = "Test different response codes", dataProvider = "getResponseCodes")
	public void testReturnResponseCode(int responseCode) throws Exception {
		int port = 8089;
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/gettest", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		switch (responseCode) {
			case 200:
				String contentType = "text/xml";
				String url = "http://localhost:8480/serviceTest/test";
				sendRequest(url, 200, contentType);
			case 404:
				contentType = "text/html";
				url = "http://localhost:8480/serviceTest/notfound";
				sendRequest(url, 404, contentType);
		}

		server.stop(0);

	}

	private class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "text/xml");
			String response = "This is Response status code test case";
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	private void sendRequest(String url, int responseCode, String contentType) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			log.error("Error Occured while sending http get request. " + e);
		}
		log.info(response.getEntity().getContentType());
		log.info(response.getStatusLine().getStatusCode());

		assertEquals(response.getFirstHeader("Content-Type").getValue(), contentType,
		             "Expected content type doesn't match");
		assertEquals(response.getStatusLine().getStatusCode(), responseCode, "response code doesn't match");
	}

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @DataProvider(name = "responseCodeProvider")
    public Object[][] getResponseCodes() {
        return new Object[][]{
                {200},
                {400},
                {403},
                {404},
                {500},
                {501},
                {503},
        };
    }

}