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
 * This class tests transformation of message content inside the payload factory using JSON path scenario
 */
public class TransformPayloadMessageContentFromJSONPathTestCase extends ESBIntegrationTest {

    private Client client = Client.create();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/transformmessagecontentjsonpath.xml");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Transform Message content inside payload factory using JSON path")
    public void testTransformMessageFromJSONPathScenario() throws Exception {

        String FirstJSON_PAYLOAD = "{\"album\":\"Tulip\",\"singer\":\"LevisM\"}";
        String SecondJSON_PAYLOAD = "{\"album\":\"Fire in the sky\",\"singer\":\"Tulip\"}";

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("TransformInJsonPath"));

        // sending post request
        ClientResponse postResponse = webResource.type("application/json")
                .post(ClientResponse.class, FirstJSON_PAYLOAD);

        assertEquals(postResponse.getType().toString(), "application/json;charset=UTF-8",
                "Content-Type Should be application/json");
        assertEquals(postResponse.getStatus(), 201, "Response status should be 201");

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), SecondJSON_PAYLOAD, "Response mismatch for HTTP Get call");
    }
}
