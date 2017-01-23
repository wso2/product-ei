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
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test class can be used to verify how HTTP PUT Request works on JSON payloads
 */
public class HTTPPUTOnJsonPayloadsTestCase extends ESBIntegrationTest {

    private Client client;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/jsonHTTPPutProxy.xml");
        client = Client.create();
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests Get method for by default album with application/json content type")
    public void testHTTPGetRequestByDefaultAlbumJSONScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"THE ENDLESS RIVER\",\"singer\":\"UB40\"}";

        WebResource webResource = client.resource(getProxyServiceURLHttp("JsonHTTPPutProxy"));

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), JSON_PAYLOAD, "Response mismatch for HTTP Get call");

    }

    @Test(groups = {"wso2.esb"}, description = "Tests PUT method with application/json content type",
            dependsOnMethods = "testHTTPGetRequestByDefaultAlbumJSONScenario")
    public void testHTTPPutRequestUpdatedAlbumJSONScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"THE ENDLESS RIVER\",\"singer\":\"MichaelJ\"}";

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("JsonHTTPPutProxy"));

        // sending put request
        ClientResponse putResponse = webResource.type("application/json")
                .put(ClientResponse.class, JSON_PAYLOAD);

        assertEquals(putResponse.getType().toString(), "application/json", "Content-Type Should be application/json");
        assertEquals(putResponse.getStatus(), 201, "Response status should be 201");

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), JSON_PAYLOAD, "Response mismatch for HTTP Get call");

    }

}
