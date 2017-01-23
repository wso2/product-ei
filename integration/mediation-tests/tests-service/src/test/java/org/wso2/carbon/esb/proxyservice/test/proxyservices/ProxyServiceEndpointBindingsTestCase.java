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
package org.wso2.carbon.esb.proxyservice.test.proxyservices;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ProxyServiceEndpointBindingsTestCase extends ESBIntegrationTest {
    private String proxyServiceName = "StockQuoteProxyEndpointBinding";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/proxyservice/stock_quote_proxy.xml");

    }

    @Test(groups = "wso2.esb", description = "Proxy service endpoint binding HttpSoap11Endpoint test")
    public void testHttpSoap11EndpointBinding() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceHttp11BindingURL(proxyServiceName)
                , null, "HttpSoap11Endpoint");
        assertNotNull(response, "Response Message is null");
        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "HttpSoap11Endpoint", "Mediation Ignored. proxy service invocation failed when WS-addressing is sent");

    }

    @Test(groups = "wso2.esb", description = "Proxy service endpoint binding HttpsSoap11Endpoint test")
    public void testHttpsSoap11EndpointBinding() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceHttps11BindingURL(proxyServiceName)
                , null, "HttpsSoap11Endpoint");
        assertNotNull(response, "Response Message is null");
        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "HttpsSoap11Endpoint", "Mediation Ignored. proxy service invocation failed when WS-addressing is sent");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }


    private String getProxyServiceHttp11BindingURL(String proxyServiceName) throws XPathExpressionException {
        return "http://" + context.getInstance().getHosts().get("default") + ":" +
                context.getInstance().getPorts().get("nhttp") + "/services/" + proxyServiceName + "." +
               proxyServiceName + "HttpSoap11Endpoint";
    }

    private String getProxyServiceHttps11BindingURL(String proxyServiceName) throws XPathExpressionException {
        return "https://" + context.getInstance().getHosts().get("default") + ":" +
                context.getInstance().getPorts().get("nhttps")+ "/services/" + proxyServiceName + "." +
               proxyServiceName + "HttpsSoap11Endpoint";
    }
}
