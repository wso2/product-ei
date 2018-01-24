/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.businessprocess.integration.tests.bpmn;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * BPMN REST task JSON mapping test case
 */
public class BPMNRestTaskJsonMappingTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BPMNUserSubstitutionTestCase.class);

    private WorkflowServiceClient workflowServiceClient;
    private static final String PROCESS_NAME = "RestInvokeProcess";
    private HttpServer backendServer;

    @BeforeTest(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loginLogoutClient.login();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        uploadBPMNForTest(PROCESS_NAME);
    }

    /***
     * Test the JSon mapping with null JSON values.
     * @throws Exception
     */
    @Test(groups = { "wso2.bps.bpmn.rest" }, description = "Test REST task JSON mapping",
          priority = 1, singleThreaded = true)
    public void testRestTaskJsonMapping() throws Exception {
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, PROCESS_NAME, 0);

        //Start backend server
        startHTTPServer();
        //start a process instance
        JSONObject payload = new JSONObject();
        payload.put("tenantId", -1234);
        payload.put("processDefinitionKey", PROCESS_NAME);
        HttpResponse response = BPMNTestUtils.postRequest(backEndUrl + "runtime/process-instances", payload);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201,
                "Starting a new process instance failed.");

        JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
        String instanceId = json.getString("id");

        //get variables
        response = BPMNTestUtils
                .getRequestResponse(backEndUrl + "runtime/process-instances/" + instanceId + "/variables");
        String responseString = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(responseString.contains(":null"), "Null json value mapping failed");
    }

    private void startHTTPServer() throws InterruptedException, IOException {
        backendServer = HttpServer.create(new InetSocketAddress(5389), 0);
        backendServer.createContext("/testBE", new JsonResponseBEHandler());
        backendServer.setExecutor(null); // creates a default executor
        backendServer.start();//start server
        log.info("Back-End mock HttpServer started!");
    }

    /**
     * HttpHandler implementation to handle request
     */
    private static class JsonResponseBEHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");

            //response payload
            String response = "{\"user\":null, \"id\":123}";

            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream responseStream = httpExchange.getResponseBody();
            try {
                responseStream.write(response.getBytes(Charset.defaultCharset()));
            } finally {
                responseStream.close();
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        backendServer.stop(0);//shutdown server
        workflowServiceClient.undeploy(PROCESS_NAME);
}

}