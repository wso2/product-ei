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
package org.wso2.carbon.esb.mediator.test.switchMediator;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class SwitchingBasedOnAddressTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/switchMediator/switching_based_on_address_synapse.xml");

    }

    @Test(groups = {"wso2.esb"}, description = "Switch messages based on address")
    public void testSample2() throws RemoteException, XPathExpressionException {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("switchByAddress"),
                                                           getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                                           "IBM");
        assertEquals("getQuote Response root element name mismatched", "getQuoteResponse", response.getLocalName());
        assertTrue("Symbol name mismatched", response.toString().contains("IBM"));

        response = axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp("switchByAddress"),
                                                      getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                                      "MSTF");
        assertEquals("getSimpleQuote Response root element name mismatched", "getSimpleQuoteResponse", response.getLocalName());
        assertTrue("Symbol name mismatched", response.toString().contains("MSTF"));

    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }


}
