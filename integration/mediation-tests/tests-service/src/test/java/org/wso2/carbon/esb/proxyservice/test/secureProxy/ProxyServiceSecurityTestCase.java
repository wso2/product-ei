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
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ServiceTransportUtil;
import org.wso2.esb.integration.common.utils.clients.SecureServiceClient;

import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ProxyServiceSecurityTestCase extends ESBIntegrationTest {

    private final String serviceName = "SecureStockQuoteProxy";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/secureProxy/stockquote_pass_through_proxy.xml");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        super.cleanup();
    }
/*
    @Test(groups = {"wso2.esb"}, description = "Provides Authentication. Clients have Username Tokens")
    public void securityPolicy1() throws Exception {
        final int policyId = 1;

        this.secureService(policyId);
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps(serviceName), policyId, "Secured");
            verifyResponse(response);

        }
        log.info("UsernameToken verified");
    }


    @Test(groups = {"wso2.esb"})
    public void securityPolicy2() throws Exception {
        final int policyId = 2;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Non-repudiation verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy3() throws Exception {
        final int policyId = 3;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Integrity verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy4() throws Exception {
        final int policyId = 4;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Confidentiality verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy5() throws Exception {
        final int policyId = 5;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Sign and encrypt - X509 Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy6() throws Exception {
        final int policyId = 6;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Sign and Encrypt - Anonymous clients verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy7() throws Exception {
        final int policyId = 7;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Encrypt only - Username Token Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy8() throws Exception {
        final int policyId = 8;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("Sign and Encrypt - Username Token Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy9() throws Exception {
        final int policyId = 9;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " X509 Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy10() throws Exception {
        final int policyId = 10;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Encrypt only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " X509 Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
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
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - Sign and Encrypt , X509 Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy12() throws Exception {
        final int policyId = 12;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign Only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " Anonymous clients verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy13() throws Exception {
        final int policyId = 13;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Anonymous clients verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy14() throws Exception {
        final int policyId = 14;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Encrypt Only - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Username Token Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void securityPolicy15() throws Exception {
        final int policyId = 15;
        this.secureService(policyId);
        if (!ServiceTransportUtil.isHttpTransportEnable(contextUrls.getBackEndUrl(), sessionCookie, serviceName)) {
            ServiceTransportUtil.addTransportHttp(contextUrls.getBackEndUrl(), sessionCookie, serviceName);
        }
        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttp(serviceName), policyId, "Secured");
            verifyResponse(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Username Token Authentication verified");
    }

    @Test(groups = {"wso2.esb"})
    public void inValidLoginsecurityPolicy15() throws Exception {
        //logAdmin.updateLoggerData("org.apache.synapse", LoggingAdminClient.logLevel.DEBUG.name(), true, false);
        final int policyId = 15;
        this.secureService(policyId);
        SimpleHttpClient httpClient = new SimpleHttpClient();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/xml");
        headers.put("SOAPAction", "urn:getQuote");
        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:getQuote>\n" +
                "         <!--Optional:-->\n" +
                "         <ser:request>\n" +
                "            <!--Optional:-->\n" +
                "            <xsd:symbol>?</xsd:symbol>\n" +
                "         </ser:request>\n" +
                "      </ser:getQuote>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HttpResponse response = httpClient.doPost(getProxyServiceURLHttp(serviceName), headers,payload,"text/xml");
        InputStream responseINStream =response.getEntity().getContent();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(responseINStream));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        String expected = "<html><body><h1>Failed to process the request</h1><p>Error processing POST request for : /services/SecureStockQuoteProxy</p><p>Missing wsse:Security header in request</p></body></html><p>null</p></body></html>";
        String contentType ="Content-Type: text/html";
        Assert.assertEquals(expected,sb.toString());
    }


    //@Test(dependsOnMethods = {"uploadArtifactTest"})
    //    public void securityPolicy16() {
    //        this.secureService(16);
    //        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
    //        serviceEndPoint = DataServiceUtility.getServiceEndpointHttp(sessionCookie, dssBackEndUrl, serviceName);
    //        OMElement response;
    //        for (int i = 0; i < 5; i++) {
    //            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(), serviceEndPoint, "showAllOffices", getPayload(), 16);
    //            Assert.assertTrue("Expected Result not Found", (response.toString().indexOf("<Office>") > 1));
    //            Assert.assertTrue("Expected Result not Found", (response.toString().indexOf("</Office>") > 1));
    //        }
    //    log.info("Kerberos Authentication - Sign - Sign based on a Kerberos Token verified");
    //    }


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
