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

/**
 * This class tests JSON payloads with API's
 */
public class JSONWithAPITestCase extends ESBIntegrationTest {

    private Client client = Client.create();
    private String JSON_PAYLOAD = "{\"album\":\"New Moon\",\"singer\":\"Eagles\"}";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/jsonwithapi.xml");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Testing json requests with API - POST request scenario" )
    public void testJSONWithAPIHTTPPostScenario() throws Exception {

        WebResource webResource = client
                .resource(getApiInvocationURL("addMusic") + "/music");

        // Calling the POST request
        ClientResponse getResponse = webResource.type("application/json")
                .post(ClientResponse.class, JSON_PAYLOAD);

        // NOTE : ESB appends charset=UTF-8
        assertEquals(getResponse.getType().toString(), "application/json;charset=UTF-8",
                "Content-Type Should be application/json");
        assertEquals(getResponse.getStatus(), 201, "Response status should be 201");

    }

    @Test(groups = "wso2.esb", description = "Testing json requests with API - GET request scenario"
            , dependsOnMethods = "testJSONWithAPIHTTPPostScenario")
    public void testJSONWithAPIHTTPGetScenario() throws Exception {

        WebResource webResource = client
                .resource(getApiInvocationURL("getMusic") + "/music");

        // sending the GET request
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertEquals(getResponse.getStatus(), 200, "Response status should be 200");

        // NOTE : ESB appends charset=UTF-8
        assertEquals(getResponse.getType().toString(), "application/json;charset=UTF-8",
                "Content-Type Should be application/json");
        assertEquals(getResponse.getEntity(String.class), JSON_PAYLOAD, "Response mismatch for HTTP Get call");

    }
}
