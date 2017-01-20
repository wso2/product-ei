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
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests getting the JSON Request payload from the registry
 * Please refer the ESB JIRA https://wso2.org/jira/browse/ESBJAVA-3459
 */
public class RegistryBasedJSONRequestTestCase extends ESBIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceStub;
    private Client client = Client.create();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/registrybasedjsonproxy.xml");
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        super.cleanup();
        resourceAdminServiceStub.deleteResource("/_system/config/repository/esb/registry/jsonrequest.txt");
    }

    /**
     * //TODO ESBJAVA-3459  - Test case to amend accordingly
     */
    @Test(groups = "wso2.esb", description = "Testing json requests with Registry Based scenario")
    public void testJSONWithRegistryBasedJSONPayloadsScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"Mango\",\"singer\":\"MLTR\"}";

        // Adding json request file to registry
        // - Note: Here we are setting the media type as text/plain explicitly.(https://wso2.org/jira/browse/ESBJAVA-3459)
        resourceAdminServiceStub.addResource(
                "/_system/config/repository/esb/registry/jsonrequest.txt", "text/plain", "Adding json request",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                        "/jaxrs/jsonrequest.txt")));

        Thread.sleep(1000);

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("RegistryBasedJSONProxy"));

        // sending post request
        ClientResponse postResponse = webResource.type("application/json")
                .post(ClientResponse.class, null);

        assertEquals(postResponse.getType().toString(), "application/json", "Content-Type Should be application/json");
        assertEquals(postResponse.getStatus(), 201, "Response status should be 201");

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), JSON_PAYLOAD, "Response mismatch for HTTP Get call");
    }
}
