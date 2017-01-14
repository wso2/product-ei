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
package org.wso2.carbon.esb.resource.test.endpoint;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class EndPointMediaTypeTestCase extends ESBIntegrationTest {
    private Log log = LogFactory.getLog(EndPointMediaTypeTestCase.class);

    private EndPointAdminClient endpointAdminClient;
    private ResourceAdminServiceClient resourceAdmin;
    private boolean isDynamicEndpointExist = false;
    private boolean isDefinedEndpointExist = false;
    private final String KEY = "conf:/endpoint/testAutomationEndpoint";
    private final String DEFINED_ENDPOINT_NAME = "definedEndpoint";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        endpointAdminClient = new EndPointAdminClient(contextUrls.getBackEndUrl(), sessionCookie);
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
    }

    @Test(groups = {"wso2.esb"}, description = "Test dynamic Endpoint media type - application/vnd.wso2.esb.endpoint")
    public void dynamicEndpointMediaTypeTest() throws Exception {
        final String name = "automationEndpoint";

        OMElement endpoint = AXIOMUtil.stringToOM("<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + name + "\">" +
                                                  "<address uri=\"http://localhost:9000/services/SimpleStockQuoteService\">" +
                                                  "<suspendOnFailure>" +
                                                  "<progressionFactor>1.0</progressionFactor>" +
                                                  "</suspendOnFailure>" +
                                                  "<markForSuspension>" +
                                                  "<retriesBeforeSuspension>0</retriesBeforeSuspension>" +
                                                  "<retryDelay>0</retryDelay></markForSuspension>" +
                                                  "</address>" +
                                                  "</endpoint>");
        isDynamicEndpointExist = endpointAdminClient.addDynamicEndPoint(KEY, endpoint);
        Assert.assertTrue(isDynamicEndpointExist, "Endpoint addition failed");
        boolean isEndpointExist = false;
        for (String endpointName : endpointAdminClient.getDynamicEndpoints()) {
            if (KEY.equalsIgnoreCase(endpointName)) {
                isEndpointExist = true;
                break;
            }
        }
        Assert.assertTrue(isEndpointExist, "Endpoint not found in Dynamic Endpoint list");
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/endpoint/testAutomationEndpoint");
        Assert.assertEquals(metadata.getMediaType(), "application/vnd.wso2.esb.endpoint", "Media Type mismatched for Dynamic Endpoint");


    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test defined Endpoint media type - text/xml", enabled = false)
    public void definedEndpointMediaTypeTest() throws Exception {

        OMElement endpoint = AXIOMUtil.stringToOM("<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" " +
                                                  "name=\"" + DEFINED_ENDPOINT_NAME + "\">" +
                                                  "<address uri=\"http://localhost:9000/services/SimpleStockQuoteService\" />" +
                                                  "</endpoint>");
        isDefinedEndpointExist = endpointAdminClient.addEndPoint(endpoint);
        Assert.assertTrue(isDefinedEndpointExist, "Endpoint addition failed");
        boolean isEndpointExist = false;
        for (String endpointName : endpointAdminClient.getEndpointNames()) {
            if (DEFINED_ENDPOINT_NAME.equalsIgnoreCase(endpointName)) {
                isEndpointExist = true;
                break;
            }
        }
        Assert.assertTrue(isEndpointExist, "Endpoint not found in Defined Endpoint list");
        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/endpoints/" + DEFINED_ENDPOINT_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for Defined Endpoint");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if(isDefinedEndpointExist){
           endpointAdminClient.deleteEndpoint(DEFINED_ENDPOINT_NAME) ;
        }
        if(isDynamicEndpointExist){
           endpointAdminClient.deleteDynamicEndpoint(KEY);
        }
        super.cleanup();
    }
}
