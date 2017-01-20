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
package org.wso2.carbon.esb.proxyservice.test.customProxy;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class InvokingNonExistingProxyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

    }

    @Test(groups = "wso2.esb", description = "Invoking Non existing proxy service")
    public void testNonExistingProxyInvocation() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/customProxy/non_existing_proxy.xml");
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("NonExistingProxyService"), null, "WSO2");
        } catch (AxisFault fault) {
            assertEquals(fault.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL, "Error Message Mismatched");
        }

    }

    @Test(groups = "wso2.esb", description = "Invoking Non existing proxy service when main sequence has endpoint")
    public void testMainSequenceForNonExistingProxy() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/customProxy/non_existing_proxy_route_to_main.xml");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("NonExistingProxyService"), null, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
