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
package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Property Mediator DisableAddressingForOutMessages Property Test
 */

public class PropertyIntegrationDisableAddressingForOutMessagesTestCase extends ESBIntegrationTest {

    private AxisOperationClient axisOperationClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/disableAddressingForOutMessages.xml");
        axisOperationClient = new AxisOperationClient();

    }

    @Test(groups = "wso2.esb", description = "Test- DisableAddressingForOutMessages")
    public void testDisableAddressingForOutMessages() throws Exception {

        OMElement response = axisOperationClient.send
                (getProxyServiceURLHttp("Axis2ProxyService"),
                 getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                 createStandardRequest("wso2"),"urn:getQuote");
        assertNotNull(response, "Response is null");
        assertEquals(response.getFirstElement().getFirstElement().getQName().getPrefix(),
                     "wsa",
                     "Property not set");
        assertEquals
                (response.getFirstElement().getFirstElement().getQName().getNamespaceURI(),
                 "http://www.w3.org/2005/08/addressing",
                 "Property not set");
        assertTrue(response.getFirstElement().toString().contains("wsa:MessageID"),
                   "Property not set");
    }


    private OMElement createStandardRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("request", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);

        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);

        return method;
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
        axisOperationClient.destroy();
        axisOperationClient = null;
    }
}
