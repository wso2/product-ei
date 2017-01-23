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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.proxyservice.test.secureProxy.util.SecureEndpointSetter;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PassThroughProxyEngagingSecurityPolicyTestCase extends ESBIntegrationTest {
    private final String policyPath = TestConfigurationProvider.getSecurityPolicyLocation() + File.separator + "custom" + File.separator;
    private final String proxyName = "StockQuoteProxyEngagingScenario";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        resourceAdmin.addCollection("/_system/config/", "securityEngaging", "collection", "Security Policy");
        for (int i = 1; i < 10; i++) {
            resourceAdmin.addResource("/_system/config/securityEngaging/scenario" + i + "-policy.xml", "application/xml", "dss"
                    , new DataHandler(new URL("file:///" + policyPath + "scenario" + i + "-policy.xml")));
        }


        updateESBConfiguration(SecureEndpointSetter.setEndpoint(
                "artifacts/ESB/proxyconfig/proxy/secureProxy/passthrough_proxy_engaging_security_with_secueBackEnd.xml"));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        try {

            resourceAdmin.deleteResource("/_system/config/securityEngaging");
        } finally {

            super.cleanup();
        }

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 1", enabled = false)
    public void secureRequestScenario1() throws Exception {
        applySecurity("UTSecureStockQuoteProxyForSecurityEngaging", 1,  getUserRole());

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "1"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 2")
    public void secureRequestScenario2() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "2"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 3")
    public void secureRequestScenario3() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "3"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 4")
    public void secureRequestScenario4() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "4"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 5")
    public void secureRequestScenario5() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "5"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 6")
    public void secureRequestScenario6() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "6"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 7")
    public void secureRequestScenario7() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "7"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "Request to a pass through proxy engaging security and refer secure backend service by policy 8")
    public void secureRequestScenario8() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName + "8"),
                                                                     null, "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

}
