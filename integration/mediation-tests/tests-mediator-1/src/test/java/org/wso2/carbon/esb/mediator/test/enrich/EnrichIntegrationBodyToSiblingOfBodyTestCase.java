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
package org.wso2.carbon.esb.mediator.test.enrich;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;

import javax.xml.namespace.QName;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class EnrichIntegrationBodyToSiblingOfBodyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        String filePath = "/artifacts/ESB/synapseconfig/core/synapse_body_to_sibling_of_body.xml";
        loadESBConfigurationFromClasspath(filePath);
    }

    //get the body of request and add it as a sibling of response.
    @Test(groups = "wso2.esb", description = "Add custom content as a child to the part of message" +
                                             " specified by xpath expression ")
    public void testEnrichMediator() throws Exception {
        String soapResponse = getResponse();

        assertNotNull(soapResponse, "Response message null");
        OMElement response = AXIOMUtil.stringToOM(soapResponse);

        OMElement soapBody = response.getFirstElement();
        OMElement getQuoteElement = soapBody.getFirstChildWithName(
                new QName("http://services.samples", "getQuote"));

        assertNotNull(getQuoteElement, "sibling null");
        OMElement requestElement = getQuoteElement.getFirstElement();
        OMElement symbolTagInsideRequestSibling = requestElement.getFirstElement();

        assertEquals(requestElement.getLocalName(), "request", "Fault, child");
        assertEquals(symbolTagInsideRequestSibling.getLocalName(), "symbol", "Fault, child");
        assertEquals(symbolTagInsideRequestSibling.getText(), "WSO2", "Fault, child");


    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    //method to create a request using an operation client, and then get the response.
    private String getResponse() throws IOException {

        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(getProxyServiceURLHttp("enrichSample"), null,
                                            createQuoteRequestBody("WSO2"), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }

        Assert.assertNotNull(response, "Response null");
        return response.toString();

    }

    //create the body for the request to be sent through operation client.
    private OMElement createQuoteRequestBody(String symbol) {
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
