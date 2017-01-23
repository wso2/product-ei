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

public class SecurityTransformationProxyForPolicy7BackEndTestCase extends ESBIntegrationTest {
    private final String serviceName = "StockQuoteSecurityTransformProxyToPolicy7";
    private final String policyPath = TestConfigurationProvider.getSecurityPolicyLocation() + File.separator + "custom" + File.separator;
    private final String symbol = "Secured";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        resourceAdmin.addCollection("/_system/config/", "securityTransform", "collection", "policy files");
        resourceAdmin.addResource("/_system/config/securityTransform/scenario7-policy.xml", "application/xml", "dss"
                , new DataHandler(new URL("file:///" + policyPath + "scenario7-policy.xml")));

        updateESBConfiguration(SecureEndpointSetter.setEndpoint(
                "/artifacts/ESB/proxyconfig/proxy/secureProxy/security_transformation_proxy_for_policy7_backend.xml"));

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
    @Test(groups = {"wso2.dss"}, description = "Provides Authentication. Clients have Username Tokens. Backend is secured using policy 7")
    public void securityPolicy1() throws Exception {
        final int policyId = 1;

        this.secureService(policyId);
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps(serviceName), policyId, symbol);
            verifyResponse(response);

        }
        log.info("UsernameToken verified");
    }


    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 2 and secure back end using policy 7")
    public void securityPolicy2() throws Exception {
        final int policyId = 2;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 3 and secure back end using policy 7")
    public void securityPolicy3() throws Exception {
        final int policyId = 3;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 4 and secure back end using policy 7")
    public void securityPolicy4() throws Exception {
        final int policyId = 4;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 5 and secure back end using policy 7")
    public void securityPolicy5() throws Exception {
        final int policyId = 5;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 6 and secure back end using policy 7")
    public void securityPolicy6() throws Exception {
        final int policyId = 6;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 7 and secure back end using policy 7")
    public void securityPolicy7() throws Exception {
        final int policyId = 7;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 8 and secure back end using policy 7")
    public void securityPolicy8() throws Exception {
        final int policyId = 8;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 9 and secure back end using policy 7")
    public void securityPolicy9() throws Exception {
        final int policyId = 9;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 10 and secure back end using policy 7")
    public void securityPolicy10() throws Exception {
        final int policyId = 10;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 11 and secure back end using policy 7")
    public void securityPolicy11() throws Exception {
        final int policyId = 11;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 12 and secure back end using policy 7")
    public void securityPolicy12() throws Exception {
        final int policyId = 12;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 13 and secure back end using policy 7")
    public void securityPolicy13() throws Exception {
        final int policyId = 13;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 14 and secure back end using policy 7")
    public void securityPolicy14() throws Exception {
        final int policyId = 14;
        this.secureService(policyId);
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

    @Test(groups = {"wso2.dss"}, description = "Secure request using policy 15 and secure back end using policy 7")
    public void securityPolicy15() throws Exception {
        final int policyId = 15;
        this.secureService(policyId);
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
*/
}
