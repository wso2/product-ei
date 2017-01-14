/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.proxyservice.test.secureProxy;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SecureServiceClient;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class SecureProxyTestCase extends ESBIntegrationTest {

    private SecureServiceClient secureAxisServiceClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/secureProxy/secure_proxy_service_scenarios.xml");
        applySecurity();
        secureAxisServiceClient = new SecureServiceClient();

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service with providing endpoint through url")
    public void testSecureProxyEndPointThruUri() throws Exception {


        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy11"), 1, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service with providing endpoint from registry")
    public void testSecureProxyEndPointFromReg() throws Exception {

        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy12"), 1, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service with publishing wsdl inline")
    public void testSecureProxyWSDLInline() throws Exception {

        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy13"), 1, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service with publishing wsdl source uri")
    public void testSecureProxyWSDLSourceUri() throws Exception {

        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy14"), 1, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service with publishing wsdl from registry")
    public void testSecureProxyWSDLFromReg() throws Exception {

        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy15"), 1, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service Enabling only HTTPS")
    public void testSecureProxyEnableOnlyHTTPS() throws Exception {

        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy16"), 1, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Proxy service Enabling only HTTP")
    public void testSecureProxyEnableOnlyHTTP() throws Exception {

        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp("StockQuoteProxy17"), 5, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    private void applySecurity()
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
                   InterruptedException {

        for (int i = 1; i < 7; i++) {
            applySecurity("StockQuoteProxy1" + i, 1, getUserRole());
        }
        applySecurity("StockQuoteProxy17", 5, getUserRole());

    }


    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);


        resourceAdminServiceStub.deleteResource("/_system/config/policy");
        resourceAdminServiceStub.addCollection("/_system/config/", "policy", "",
                                               "Contains test policy files");

        resourceAdminServiceStub.addResource(
                "/_system/config/policy/scenario1-policy.xml", "application/xml", "policy files",
                new DataHandler(new URL("file:///" + TestConfigurationProvider.getResourceLocation() +
                                        "/security/policies/scenario1-policy.xml")));

        resourceAdminServiceStub.addCollection("/_system/config/", "proxy", "",
                                               "Contains test proxy tests files");

        resourceAdminServiceStub.addResource(
                "/_system/config/proxy/registry_endpoint.xml", "application/xml", "xml files",
                setEndpoints(new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                                     "/proxyconfig/proxy/utils/registry_endpoint.xml"))));

        resourceAdminServiceStub.addResource(
                "/_system/config/proxy/sample_proxy_1.wsdl", "application/wsdl+xml", "wsdl+xml files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                        "/proxyconfig/proxy/utils/sample_proxy_1.wsdl")));
    }


    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);

        resourceAdminServiceStub.deleteResource("/_system/config/policy");
        resourceAdminServiceStub.deleteResource("/_system/config/proxy");
    }


}
