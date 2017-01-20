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
package org.wso2.carbon.esb.samples.test.template;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CallTemplateMediatorSample751TestCase extends ESBIntegrationTest {

    private String proxyServiceName = "SplitAggregateProxy";
    private int iterations = 4;
    private String symbol = "IBM";


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(751);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        proxyServiceName = null;
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Stereotyping XSLT Transformations with Templates " +
                                               ":Test using sample 751")
    public void testXSLTTransformationWithTemplates() throws IOException, XMLStreamException {
        // OMElement response=axis2Client.sendMultipleQuoteRequest(getProxyServiceURL(),null,"IBM",4);
        String soapResponse = getResponse();
        assertNotNull(soapResponse, "Response message is null");
        OMElement response = AXIOMUtil.stringToOM(soapResponse);
        OMElement soapBody = response.getFirstElement();
        Iterator quoteBody = soapBody.getChildElements();
        int count = 0;
        while (quoteBody.hasNext()) {
            OMElement getQuote = (OMElement) quoteBody.next();
            String test = getQuote.getLocalName();
            assertEquals(test, "getQuoteResponse", "getQuoteResponse tag not in response");
            OMElement omElement = getQuote.getFirstElement();
            String symbolResponse = omElement.getFirstChildWithName
                    (new QName("http://services.samples/xsd", "symbol")).getText();
            assertEquals(symbolResponse, symbol, "Symbol not match");
            count++;
        }
        assertEquals(count, iterations, "Iterations not match");
    }


    private OMElement createMultipleQuoteRequestBody(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, symbol));
            value1.addChild(value2);
            method.addChild(value1);
        }
        return method;
    }

    private String getResponse() throws IOException {

        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = operationClient.send(getProxyServiceURLHttp(proxyServiceName), null,
                                                  createMultipleQuoteRequestBody(symbol, iterations), "urn:getQuote");
        operationClient.destroy();
        return response.toString();

    }
}
