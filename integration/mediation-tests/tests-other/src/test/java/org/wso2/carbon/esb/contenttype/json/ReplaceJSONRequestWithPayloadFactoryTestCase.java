/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.contenttype.json;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the scenario of using Payload factory to replace the contents of JSON payload
 */
public class ReplaceJSONRequestWithPayloadFactoryTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;
    private Client client = Client.create();
    private boolean isJsonPayload = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/jsonreplacepayload.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Testing and Replacing of JSON Payload With Payload Factory scenario")
    public void testReplaceJSONPayloadWithPayloadFactoryScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"Ocean\",\"singer\":\"Brown\"}";

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("ReplacePayloadJSONProxy"));

        int beforeLogSize = logViewer.getAllSystemLogs().length;

        // sending post request
        ClientResponse postResponse = webResource.type("application/json")
                .post(ClientResponse.class, JSON_PAYLOAD);

        Thread.sleep(3000);

        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;

        String msg = "Direction: response, JSON-Payload = {\"album\":\"First Change\",\"singer\":\"First DKL\"}";

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(msg)) {
                isJsonPayload = true;
                break;
            }
        }

        //This is to verify that the payload factory in the 'insequence' behaved correctly.
        assertTrue(isJsonPayload, "Response does not contain the expected JSON payload value");

        //This is to verify that the payload factory in the 'outsequence' behaved correctly.
        assertEquals(postResponse.getType().toString(), "application/json;charset=UTF-8",
                "Content-Type Should be application/json");
        assertEquals(postResponse.getStatus(), 201, "Response status should be 201");

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), "{\"album\":\"Second Change\",\"singer\":\"Second DKL\"}",
                "Response mismatch for HTTP Get call");

    }
}