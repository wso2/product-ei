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
package org.wso2.carbon.esb.resource.test.template;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.clients.template.EndpointTemplateAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.rmi.RemoteException;

public class EndpointTemplateMediaTypeTestCase extends ESBIntegrationTest{
    private Log log = LogFactory.getLog(EndpointTemplateMediaTypeTestCase.class);

    private EndpointTemplateAdminServiceClient endpointTemplateAdminServiceClient;
    private ResourceAdminServiceClient resourceAdmin;
    private boolean isDynamicEndpointTemplateExist = false;
    private boolean isDefinedEndpointTemplateExist = false;
    private final String KEY = "conf:/template/registryEndpointTemplate";
    private final String DEFINED_ENDPOINT_TEMPLATE_NAME = "definedEndpointTemplate";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        endpointTemplateAdminServiceClient = new EndpointTemplateAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test dynamic Endpoint Template media type - application/vnd.wso2.template.endpoint")
    public void dynamicEndpointTemplateMediaTypeTest() throws Exception {
        final String name = "registryEndpointTemplate";

        OMElement endpoint = AXIOMUtil.stringToOM("<template xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + name + "\">" +
                                                  "<endpoint name=\"stockQuoteEndpoint\"> " +
                                                  "<address uri=\"https://localhost:9000/services/SimpleStockQuoteService\">" +
                                                  "<suspendOnFailure>" +
                                                  "<progressionFactor>1.0</progressionFactor>" +
                                                  "</suspendOnFailure>" +
                                                  "<markForSuspension>" +
                                                  "<retriesBeforeSuspension>0</retriesBeforeSuspension>" +
                                                  "<retryDelay>0</retryDelay>" +
                                                  "</markForSuspension>" +
                                                  "</address>" +
                                                  "</endpoint>" +
                                                  "</template>");
        endpointTemplateAdminServiceClient.addDynamicEndpointTemplate(KEY, endpoint);
        isDynamicEndpointTemplateExist = true;

        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/template/registryEndpointTemplate");
        Assert.assertEquals(metadata.getMediaType(), "application/vnd.wso2.template.endpoint", "Media Type mismatched for Dynamic Endpoint Template");


    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test defined Endpoint Template media type - text/xml", enabled = false)
    public void definedEndpointTemplateMediaTypeTest() throws Exception {

        OMElement endpoint = AXIOMUtil.stringToOM("<template xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + DEFINED_ENDPOINT_TEMPLATE_NAME + "\">" +
                                                  "<axis2ns3:parameter xmlns:axis2ns3=\"http://ws.apache.org/ns/synapse\" name=\"name\" />" +
                                                  "<axis2ns4:parameter xmlns:axis2ns4=\"http://ws.apache.org/ns/synapse\" name=\"uri\" />" +
                                                  "<endpoint name=\"quoteEndpoint\">" +
                                                  "<address uri=\"https://localhost:9000/services/SimpleStockQuoteService\">" +
                                                  "<suspendOnFailure>" +
                                                  "<progressionFactor>1.0</progressionFactor>" +
                                                  "</suspendOnFailure>" +
                                                  "<markForSuspension>" +
                                                  "<retriesBeforeSuspension>0</retriesBeforeSuspension>" +
                                                  "<retryDelay>0</retryDelay>" +
                                                  "</markForSuspension>" +
                                                  "</address>" +
                                                  "</endpoint>" +
                                                  "</template>");
        endpointTemplateAdminServiceClient.addEndpointTemplate(endpoint);
        isDefinedEndpointTemplateExist = true;

        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/templates/" + DEFINED_ENDPOINT_TEMPLATE_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for Defined Endpoint Template");

    }

    @AfterClass
    public void destroy() throws RemoteException {
        if (isDefinedEndpointTemplateExist) {
            endpointTemplateAdminServiceClient.deleteEndpointTemplate(DEFINED_ENDPOINT_TEMPLATE_NAME);
        }
        if (isDynamicEndpointTemplateExist) {
            endpointTemplateAdminServiceClient.deleteDynamicEndpointTemplate(KEY);
        }
    }
}
