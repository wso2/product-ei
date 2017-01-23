/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test SOAP action attribute of iterate mediator
 */

public class IterateSOAPActionTest extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_SOAP_Action.xml");
    }

    @Test(groups = "wso2.esb", description = "Tests SOAP action property ")
    public void testSOAPAction() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response =
                axisServiceClient.sendReceive(createSimpleQuoteRequestBody("WSO2"),
                                              getMainSequenceURL(), "");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    private OMElement createSimpleQuoteRequestBody(String symbol) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("request", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);
        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);
        return method;
    }

}
