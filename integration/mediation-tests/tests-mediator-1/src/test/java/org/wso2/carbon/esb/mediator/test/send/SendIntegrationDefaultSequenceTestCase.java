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
package org.wso2.carbon.esb.mediator.test.send;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SendIntegrationDefaultSequenceTestCase extends ESBIntegrationTest {
    private ResourceAdminServiceClient resourceAdminServiceStub;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        resourceAdminServiceStub = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), getSessionCookie());
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/send/synapse_default.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "End point in local registry and default receiving sequence with build message before receive")
    public void testSequenceDefaultBuildMessageTest() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("testSequenceDefaultBuildMessage"), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "WSO2", "Symbol is not match");
    }

    @Test(groups = {"wso2.esb"}, description = "End point in local registry and default receiving sequence with not build message before receive")
    public void testSequenceDefaultBuildMessageNo() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("testSequenceDefaultBuildMessageNo"), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "WSO2", "Symbol is not match");
    }

    private void uploadResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceStub.deleteResource("/_system/config/endpoints");
        resourceAdminServiceStub.addCollection("/_system/config/", "endpoints", "",
                                               "Contains test endpoint files");
        resourceAdminServiceStub.addResource(
                "/_system/config/endpoints/registry_endpoint.xml", "application/xml", "xml files",
                setEndpoints(new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                                     "/mediatorconfig/send/endpoints/registry_endpoint.xml"))));
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        resourceAdminServiceStub.deleteResource("/_system/config/endpoints");
        super.cleanup();
    }
}
