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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.proxyservice.test.secureProxy.util.SecureEndpointSetter;
import org.wso2.carbon.esb.proxyservice.test.secureProxy.util.SecureStockQuoteClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PassThroughProxyWithPreserverSecurityHeaderTestCase extends ESBIntegrationTest {
    private final String policyPath = TestConfigurationProvider.getSecurityPolicyLocation() + File.separator + "custom" + File.separator;
    private final String proxyName = "StockQuoteProxyPreserveHeaderScenario";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        updateESBConfiguration(SecureEndpointSetter.setEndpoint(
                "artifacts/ESB/proxyconfig/proxy/secureProxy/passthrough_proxy_with_secueBackEnd.xml"));
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario1() throws Exception {
        applySecurity("UTSecureStockQuoteProxy", 1, getUserRole());
        addProxy();
        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest(userInfo.getUserName()
                , userInfo.getPassword(), getProxyServiceURLHttps(proxyName + "1")
                , policyPath + "scenario1-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario2() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest(null, null
                , getProxyServiceURLHttp(proxyName + "2")
                , policyPath + "scenario2-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario3() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest(null, null
                , getProxyServiceURLHttp(proxyName + "3")
                , policyPath + "scenario3-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario4() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest(null, null
                , getProxyServiceURLHttp(proxyName + "4")
                , policyPath + "scenario4-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario5() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest(null, null
                , getProxyServiceURLHttp(proxyName + "5")
                , policyPath + "scenario5-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario6() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest(null, null
                , getProxyServiceURLHttp(proxyName + "6")
                , policyPath + "scenario6-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario7() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest("bob"
                , "password", getProxyServiceURLHttp(proxyName + "7")
                , policyPath + "scenario7-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    @Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario8() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest("bob"
                , "password", getProxyServiceURLHttp(proxyName + "8")
                , policyPath + "scenario8-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }

    /*@Test(groups = "wso2.esb", description = "secure Request to a pass through proxy refer secure backend service")
    public void secureRequestScenario10() throws Exception {

        OMElement response = new SecureStockQuoteClient().sendSecuredSimpleStockQuoteRequest("bob"
                , "password", getProxyServiceURLHttp(proxyName + "10")
                , policyPath + "scenario9-policy.xml", "Secured");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "Secured", "Fault: value 'symbol' mismatched");

    }*/

    private void addProxy() throws Exception {
        String proxy = " <proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"StockQuoteProxyPreserveHeaderScenario1\">\n" +
                       "        <target>\n" +
                       "            <inSequence>\n" +
                       "                <property name=\"preserveProcessedHeaders\" value=\"true\"/>\n" +
                       "                <send>\n" +
                       "                    <endpoint>\n" +
                       "                        <address\n" +
                       "                                uri=\"https://localhost:8243/services/UTSecureStockQuoteProxy\"/>\n" +
                       "                    </endpoint>\n" +
                       "                </send>\n" +
                       "            </inSequence>\n" +
                       "            <outSequence>\n" +
                       "                <send/>\n" +
                       "            </outSequence>\n" +
                       "        </target>\n" +
                       "    </proxy>";
        proxy = proxy.replace("https://localhost:8243/services/UTSecureStockQuoteProxy"
                , getProxyServiceURLHttps("UTSecureStockQuoteProxy"));
        addProxyService(AXIOMUtil.stringToOM(proxy));
    }


}
