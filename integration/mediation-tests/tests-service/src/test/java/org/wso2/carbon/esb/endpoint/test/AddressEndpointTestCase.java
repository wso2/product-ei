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

package org.wso2.carbon.esb.endpoint.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class AddressEndpointTestCase extends ESBIntegrationTest {

    private final String ENDPOINT_NAME = "addressEpTest";
    private final String ENDPOINT_NAME1 = "addressEpTest1";
    private EndPointAdminClient endPointAdminClient;

    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" +
                                          File.separator + "endpoint" + File.separator + "addressEndpointConfig" + File.separator + "synapse.xml");

        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());

        resourceAdminServiceClient = new ResourceAdminServiceClient
                (context.getContextUrls().getBackEndUrl(), getSessionCookie());
        uploadResourcesToConfigRegistry();

    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        resourceAdminServiceClient.deleteResource("/_system/config/test_ep_config");
        resourceAdminServiceClient = null;
        endPointAdminClient = null;
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Test endpoint addition, deletion & stats")
    public void testAddressEndpoint() throws Exception {
        cleanupEndpoints();
        endpointAdditionScenario();
        endpointStatisticsScenario();
        endpointDeletionScenario();
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a Address endpoint")
    public void testSendingToAddressEndpoint()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint")
                , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a Address endpoint in Config Reg")
    public void testSendingToAddressEndpoint_ConfigReg()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Config_Reg"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a Invalid Address endpoint")
    public void testSendingToInvalidAddressEndpoint()
            throws IOException, EndpointAdminEndpointAdminException,
                   LoginAuthenticationExceptionException,
                   XMLStreamException {
        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("invalidAddressEndPoint"),
                                                                         getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof AxisFault);
        }
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a Address endpoint has a Invalid Property")
    public void testSendingToInvalidPropertyAddressEndpoint()
            throws XMLStreamException, FileNotFoundException, AxisFault, XPathExpressionException {
        ProxyServiceAdminClient proxyAdmin = new ProxyServiceAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        ESBTestCaseUtils testUtil = new ESBTestCaseUtils();
        try {
            proxyAdmin.addProxyService(testUtil.loadResource("/artifacts/ESB/endpoint/addressEndpointConfig/invalidPropertyAddressEndPoint.xml"));
            Assert.fail("Proxy Deployment must failed due to Unsupported scope. but proxy deployment success");
        } catch (Exception e) {
            Assert.assertEquals(e.getCause().getMessage(),
                                "Only 'axis2' or 'transport' or 'axis2-client' values are allowed for attribute scope for a property, Unsupported scope andun");
        }

    }

    @Test(groups = {"wso2.esb"}, description = "Adding Duplicate Address endpoint", expectedExceptions = AxisFault.class)
    public void testAddingDuplicateAddressEndpoint() throws Exception {
        addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                         "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME1 + "\">\n" +
                                         "    <address uri=\""+ getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE) +"\" />\n" +
                                         "</endpoint>"));
        addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                         "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME1 + "\">\n" +
                                         "    <address uri=\""+getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE)+"\" />\n" +
                                         "</endpoint>"));
    }

    private void cleanupEndpoints()
            throws RemoteException, EndpointAdminEndpointAdminException {
        String[] endpointNames = endPointAdminClient.getEndpointNames();
        List endpointList;
        if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
            endpointList = Arrays.asList(endpointNames);
            if (endpointList.contains(ENDPOINT_NAME)) {
                endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
            }
        }
    }

    private void endpointAdditionScenario()
            throws Exception {
        int beforeCount = endPointAdminClient.getEndpointCount();

        addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                         "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME + "\">\n" +
                                         "    <address uri=\""+getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE)+"\" />\n" +
                                         "</endpoint>"));
        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(1, afterCount - beforeCount);

        String[] endpoints = endPointAdminClient.getEndpointNames();
        if (endpoints != null && endpoints.length > 0 && endpoints[0] != null) {
            List endpointList = Arrays.asList(endpoints);
            assertTrue(endpointList.contains(ENDPOINT_NAME));
        } else {
            fail("Endpoint has not been added to the system properly");
        }
    }

    private void endpointStatisticsScenario()
            throws RemoteException, EndpointAdminEndpointAdminException {
        endPointAdminClient.enableEndpointStatistics(ENDPOINT_NAME);
        String endpoint = endPointAdminClient.getEndpointConfiguration(ENDPOINT_NAME);
        assertTrue(endpoint.contains("statistics=\"enable"));
    }

    private void endpointDeletionScenario()
            throws RemoteException, EndpointAdminEndpointAdminException {
        int beforeCount = endPointAdminClient.getEndpointCount();
        endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(1, beforeCount - afterCount);
    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        resourceAdminServiceClient.addCollection("/_system/config/", "test_ep_config", "",
                                                 "Contains test Default EP files");
        resourceAdminServiceClient.addResource(
                "/_system/config/test_ep_config/addressEP_Test.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/endpoint/addressEndpointConfig/addressEP_Test.xml").getPath())));
        Thread.sleep(1000);

    }

}

