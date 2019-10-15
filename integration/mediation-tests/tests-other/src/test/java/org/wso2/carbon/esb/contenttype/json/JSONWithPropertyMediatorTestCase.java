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
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the behaviour of the property mediator for JSON payloads
 */
public class JSONWithPropertyMediatorTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;
    private Client client = Client.create();
    private boolean isJsonPathProperty = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/jsonwithpropertymediator.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests JSON requests behaviour with property mediator")
    public void testJSONWithPropertyMediatorTestScenario() throws Exception {

        String FirstJSON_PAYLOAD = "{\"album\":\"Hotel California\",\"singer\":\"Eagles\"}";
        String SecondJSON_PAYLOAD = "{\"album\":\"TradeWorld\",\"singer\":\"Hotel California\"}";

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("PropertyMediatorWithJsonPath"));

        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        // sending post request
        ClientResponse postResponse = webResource.type("application/json")
                .post(ClientResponse.class, FirstJSON_PAYLOAD);

        Thread.sleep(3000);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        String msg = "Property1 = TradeWorld";

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(msg)) {
                isJsonPathProperty = true;
                break;
            }
        }

        //This is to verify that the payload factory in the 'in sequence' behaved correctly.
        assertTrue(isJsonPathProperty, "Response does not contain the expected Json path property value");

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
