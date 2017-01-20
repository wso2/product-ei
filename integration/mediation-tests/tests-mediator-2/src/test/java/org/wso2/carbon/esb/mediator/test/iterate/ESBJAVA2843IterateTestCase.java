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

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.util.Iterator;

/**
 * https://wso2.org/jira/browse/ESBJAVA-2843
 * Iterator mediator does not remove request payload in SoapEnvelop if preservePayload is false
 */
public class ESBJAVA2843IterateTestCase extends ESBIntegrationTest {

    @BeforeClass()
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/ESBJAVA-2843-iterateIfPreservePayloadFalse.xml");
    }

    @Test(groups = "wso2.esb", description = "Iterating when payload containing different element")
    public void testIteratorWithMultipleElement() throws Exception {
        IterateClient client = new IterateClient();
        String response = client.send(getProxyServiceURLHttp("testMultipleElement"), createRequestPayload(), "urn:getQuote");
        Assert.assertNotNull(response, "Response message null");
        OMElement envelope = client.toOMElement(response);
        OMElement soapBody = envelope.getFirstElement();
        Iterator iterator =
                soapBody.getChildrenWithName(new QName("http://services.samples",
                                                       "getQuoteResponse"));
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2 Company"));
        }
        Assert.assertEquals(i, 3, "Child Element count mismatched");

    }

    @AfterClass()
    public void close() throws Exception {
        super.cleanup();
    }


    private OMElement createRequestPayload() {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement top = fac.createOMElement("getQuotes", omNs);

        for (int i = 0; i < 3; i++) {
            OMElement method = fac.createOMElement("getQuote", omNs);
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, "WSO2"));
            value1.addChild(value2);
            method.addChild(value1);
            top.addChild(method);
        }

        for (int i = 0; i < 3; i++) {
            OMElement method = fac.createOMElement("dummy", omNs);
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, "WSO2"));
            value1.addChild(value2);
            method.addChild(value1);
            top.addChild(method);
        }

        return top;
    }

}
