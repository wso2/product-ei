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
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ProxyServiceWithWSAddressingTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/proxyservice/proxy_with_addressing.xml");

    }

    @Test(groups = "wso2.esb", description = "test proxy service behavior when addressing is sent")
    public void testProxyWithAddressing() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("proxyWithAddressing")
                , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        assertNotNull(response, "Response Message is null");
        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "addressing", "Mediation Ignored. proxy service invocation failed when WS-addressing is sent");

    }


    @Test(groups = "wso2.esb", description = "test proxy service behavior when both addressing and endpoint are available")
    public void testInlineEndpointPriorityWithWSAddressing() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("proxyWithAddressingPriority")
                , getBackEndServiceUrl("NonExistingService"), "WSO2");
        assertNotNull(response, "Response Message is null");
        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "AddressingPriority", "Addressing has more priority than inline endpoint when message is routing");


    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
