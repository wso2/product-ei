package org.wso2.carbon.esb.mediator.test.enrich;/*
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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class EnrichIntegrationReplaceBodyWithInlineTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        String filePath = "/artifacts/ESB/synapseconfig/core/synapse.xml";
        loadESBConfigurationFromClasspath(filePath);
    }

    @Test(groups = "wso2.esb", description = "Replace body with inline")
    public void testEnrichMediator() throws Exception {
        OMElement response1, response2;

        response1 = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, createFakeRequest("WSO2"));
        String response1SymbolValue = response1.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol")).getText();

        assertNotNull(response1, "Response message null");
        assertEquals(response1SymbolValue, "ABC", "Content not changed");

        response2 = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        String response2SymbolValue = response1.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol")).getText();
        assertNotNull(response2, "Response message null");

        assertEquals(response2SymbolValue, "ABC", "Content not changed");
        assertEquals(response2SymbolValue, response1SymbolValue, "Content not changed");


    }

    private OMElement createFakeRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuoteRequest", omNs);
        OMElement value1 = fac.createOMElement("symbols", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);

        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);

        return method;
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
