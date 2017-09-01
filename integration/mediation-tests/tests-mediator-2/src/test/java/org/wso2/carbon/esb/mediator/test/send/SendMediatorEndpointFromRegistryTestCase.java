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

public class SendMediatorEndpointFromRegistryTestCase extends ESBIntegrationTest {
    ResourceAdminServiceClient resourceAdminServiceStub;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        resourceAdminServiceStub = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), getSessionCookie());
        uploadResourcesToRegistry();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/send/synapse_endpoint_registry.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "End point in config registry")
    public void endPointFromConfigRegistryTest() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("testConfRegistry"), null, "IBM");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "IBM", "Symbol is not match");
    }

    @Test(groups = {"wso2.esb"}, description = "End point in local registry")
    public void endPointFromLocalRegistryTest() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("testLocalRegistry"), null, "IBM");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "IBM", "Symbol is not match");
    }

    @Test(groups = {"wso2.esb"}, description = "End point in governance registry")
    public void endPointFromGovernanceRegistryTest() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("testGovRegistry"), null, "IBM");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "IBM", "Symbol is not match");

    }

    private void uploadResourcesToRegistry() throws Exception {
        new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), getSessionCookie());
        resourceAdminServiceStub.deleteResource("/_system/config/endpointConfig");
        resourceAdminServiceStub.addCollection("/_system/config/", "endpointConfig", "",
                                               "Contains test endpoint files");
        resourceAdminServiceStub.addResource(
                "/_system/config/endpointConfig/registry_endpoint.xml", "application/xml", "xml files",
                setEndpoints(new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                                     "/synapseconfig/send_mediator/endpoints/registry_endpoint.xml"))));

        resourceAdminServiceStub.deleteResource("/_system/local/endpointConfig");
        resourceAdminServiceStub.addCollection("/_system/local/", "endpointConfig", "",
                                               "Contains test endpoint files");
        resourceAdminServiceStub.addResource(
                "/_system/local/endpointConfig/registry_endpoint.xml", "application/xml", "xml files",
                setEndpoints(new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                                     "/synapseconfig/send_mediator/endpoints/registry_endpoint.xml"))));


        resourceAdminServiceStub.deleteResource("/_system/governance/endpointConfig");
        resourceAdminServiceStub.addCollection("/_system/governance/", "endpointConfig", "",
                                               "Contains test endpoint files");
        resourceAdminServiceStub.addResource(
                "/_system/governance/endpointConfig/registry_endpoint.xml", "application/xml", "xml files",
                setEndpoints(new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                                     "/synapseconfig/send_mediator/endpoints/registry_endpoint.xml"))));

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        resourceAdminServiceStub.deleteResource("/_system/config/endpointConfig");
        resourceAdminServiceStub.deleteResource("/_system/local/endpointConfig");
        resourceAdminServiceStub.deleteResource("/_system/governance/endpointConfig");
        super.cleanup();
    }
}
