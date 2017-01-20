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
package org.wso2.carbon.esb.proxyservice.test.secureProxy;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.proxyservice.test.secureProxy.util.SecureEndpointSetter;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ServiceTransportUtil;
import org.wso2.esb.integration.common.utils.clients.SecureServiceClient;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

public class SecurityTransformationProxyForPolicy8BackEndTestCase extends ESBIntegrationTest {
    private final String serviceName = "StockQuoteSecurityTransformProxyToPolicy8";
    private final String policyPath = TestConfigurationProvider.getSecurityPolicyLocation() + File.separator + "custom" + File.separator;
    private final String symbol = "Secured";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        resourceAdmin.addCollection("/_system/config/", "securityTransform", "collection", "policy files");
        resourceAdmin.addResource("/_system/config/securityTransform/scenario8-policy.xml", "application/xml", "dss"
                , new DataHandler(new URL("file:///" + policyPath + "scenario8-policy.xml")));

        updateESBConfiguration(SecureEndpointSetter.setEndpoint(
                "/artifacts/ESB/proxyconfig/proxy/secureProxy/security_transformation_proxy_for_policy8_backend.xml"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        try {
            resourceAdmin.deleteResource("/_system/config/securityTransform");
        } finally {

            super.cleanup();
        }

    }
/*
    @Test(groups = {"wso2.dss"}, description = "Provides Authentication. Clients have Username Tokens. Backend is secured using policy 8")
    public void securityPolicy1() throws Exception {
        final int policyId = 1;
        secureService(policyId);
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps(serviceName), policyId, symbol);
            verifyResponse(response);

        }
        log.info("UsernameToken verified");
    }


    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 2 and secure back end using policy 8")
    public void securityPolicy2() throws Exception {
        final int policyId = 2;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Non-repudiation verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 3 and secure back end using policy 8")
    public void securityPolicy3() throws Exception {
        final int policyId = 3;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Integrity verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 4 and secure back end using policy 8")
    public void securityPolicy4() throws Exception {
        final int policyId = 4;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Confidentiality verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 5 and secure back end using policy 8")
    public void securityPolicy5() throws Exception {
        final int policyId = 5;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Sign and encrypt - X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 6 and secure back end using policy 8")
    public void securityPolicy6() throws Exception {
        final int policyId = 6;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Sign and Encrypt - Anonymous clients verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 7 and secure back end using policy 8")
    public void securityPolicy7() throws Exception {
        final int policyId = 7;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Encrypt only - Username Token Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 8 and secure back end using policy 8")
    public void securityPolicy8() throws Exception {
        final int policyId = 8;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("Sign and Encrypt - Username Token Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 9 and secure back end using policy 8")
    public void securityPolicy9() throws Exception {
        final int policyId = 9;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 10 and secure back end using policy 8")
    public void securityPolicy10() throws Exception {
        final int policyId = 10;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Encrypt only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 11 and secure back end using policy 8")
    public void securityPolicy11() throws Exception {
        final int policyId = 11;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        Thread.sleep(5000);
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - Sign and Encrypt , X509 Authentication verified");
    }


    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 12 and secure back end using policy 8")
    public void securityPolicy12() throws Exception {
        final int policyId = 12;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign Only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " Anonymous clients verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 13 and secure back end using policy 8")
    public void securityPolicy13() throws Exception {
        final int policyId = 13;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Anonymous clients verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 14 and secure back end using policy 8")
    public void securityPolicy14() throws Exception {
        final int policyId = 14;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Encrypt Only - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Username Token Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 15 and secure back end using policy 8")
    public void securityPolicy15() throws Exception {
        final int policyId = 15;
        secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, symbol);
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Username Token Authentication verified");
    }

    private void secureService(int policyId)
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
                   InterruptedException {

        applySecurity(serviceName, policyId, getUserRole());

    }

    private void verifyResponse(OMElement response) {
        String symbol = response.getFirstElement().getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol", "ax21")).getText();
        Assert.assertEquals(symbol, "Secured", "Symbol name mismatched");
    }

    private String changeServiceDef(final int policy) throws Exception {
        String proxyStr = "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"StockQuoteSecurityTransformProxyToPolicy8_" + policy + "\" transports=\"https http\" startOnLoad=\"true\" trace=\"disable\">\n" +
                          "        <target>\n" +
                          "            <inSequence>\n" +
                          "                <send>\n" +
                          "                    <endpoint name=\"secure\">\n" +
                          "                        <address uri=\"http://localhost:9007/services/SecureStockQuoteServiceScenario8\">\n" +
                          "                            <enableAddressing/>\n" +
                          "                            <enableSec policy=\"conf:/securityTransform/scenario8-policy.xml\"/>\n" +
                          "                        </address>\n" +
                          "                    </endpoint>\n" +
                          "                </send>\n" +
                          "            </inSequence>\n" +
                          "            <outSequence>\n" +
                          "                <header xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"\n" +
                          "                        name=\"wsse:Security\" action=\"remove\"/>\n" +
                          "                <send/>\n" +
                          "            </outSequence>\n" +
                          "        </target>\n" +
                          "    </proxy>";

        String serviceName = "StockQuoteSecurityTransformProxyToPolicy8_" + policy;
        OMElement proxy = AXIOMUtil.stringToOM(proxyStr);
        addProxyService(proxy);
        Thread.sleep(5000);
        isProxyDeployed(serviceName);
        applySecurity(serviceName, policy, getUserRole());
        return serviceName;
    }*/
}
