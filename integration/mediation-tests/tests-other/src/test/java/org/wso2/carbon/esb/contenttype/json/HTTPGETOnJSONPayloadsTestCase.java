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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.services.jaxrs.musicsample.MusicConfig;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test class can be used to verify how HTTP GET Request works on JSON payloads
 */
public class HTTPGETOnJSONPayloadsTestCase extends ESBIntegrationTest {

    private TomcatServerManager tomcatServerManager;
    private Client client = Client.create();

    @BeforeTest(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/jsonHTTPGetProxy.xml");
        tomcatServerManager = new TomcatServerManager(MusicConfig.class.getName(), "jaxrs", 8080);
        tomcatServerManager.startServer();
    }

    @AfterTest(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        tomcatServerManager.stop();
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests GET method with application/json content type")
    public void testHTTPGetRequestJSONScenario() throws Exception {

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("GetProxy"));

        // Calling the GET request to verify by default Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getStatus(), 200, "Response status should be 200");
        assertEquals(getResponse.getEntity(String.class), "{\"album\":\"Gold\",\"singer\":\"Elton John\"}",
                "Response mismatch for HTTP Get call");
    }
}