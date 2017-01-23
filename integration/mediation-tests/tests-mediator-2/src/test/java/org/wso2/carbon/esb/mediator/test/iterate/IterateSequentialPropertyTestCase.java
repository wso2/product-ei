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
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Tests sequential true property of iterate mediator
 * Currently cannot check whether messages are sending sequentially
 * since logs cannot be examined at test level
 * Asserts only whether response comes
 * <p/>
 * <p/>
 * This test sends a multiple stock quote request which contains stock quotes
 * with symbols like WSO2-1,WSO2-2,.... in order to iterate mediator. We have to check whether
 * simple stock quote requests are send from iterate mediator sequentially as
 * simple stock quote request1: symbol=WSO2-1,
 * simple stock quote request2: symbol=WSO2-2,etc.
 */

public class IterateSequentialPropertyTestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_sequential.xml");
    }

    @Test(groups = "wso2.esb", description = "Tests sequential='true' property")
    public void testSequentialProperty() throws Exception {
        AxisOperationClient operationClient = new AxisOperationClient();
        String response = operationClient.send(getMainSequenceURL(), null, createMultipleQuoteRequestBody("WSO2", 10), "urn:getQuote").toString();
        operationClient.destroy();
        Assert.assertNotNull(response);
        OMElement envelope = AXIOMUtil.stringToOM(response);
        OMElement soapBody = envelope.getFirstElement();
        Iterator iterator =
                soapBody.getChildrenWithName(new QName("http://services.samples",
                                                       "getQuoteResponse"));
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2"));
        }
        Assert.assertEquals(i , 10, "Chile Element mismatched");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }


    private OMElement createMultipleQuoteRequestBody(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, symbol + "-" + i));
            value1.addChild(value2);
            method.addChild(value1);
        }
        return method;
    }


}
