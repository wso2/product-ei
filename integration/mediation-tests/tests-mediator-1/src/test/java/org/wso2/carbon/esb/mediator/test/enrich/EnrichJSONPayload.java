/*
 *Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.enrich;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


public class EnrichJSONPayload extends ESBIntegrationTest {

    private Client client = Client.create();
    private String JSON_Payload
            = "{ \"resourceId\": \"f4d3c43d-0c10-42c5-854a-5335469cf3d0\", \"assetCode\": \"000002\" }";
    private String Expected_Response
            = "{\"resourceId\":\"f4d3c43d-0c10-42c5-854a-5335469cf3d0\",\"assetCode\":[{}]}";

    //Creates the API
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/jaxrs/EnrichJSONPayload_API.xml");
    }

    @Test(groups = "wso2.esb", description = "Testing json requests with enrich mediator in API")
    public void testJSONWithEnrichMediator() throws Exception {
        WebResource webResource = client.resource(getApiInvocationURL("addMusic") + "/music");
        ClientResponse getResponse = webResource.type("application/json").post(ClientResponse.class, JSON_Payload);
        String response = getResponse.getEntity(String.class);
        Assert.assertEquals(response, Expected_Response);
    }

}
