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
package org.wso2.carbon.esb.mediator.test.aggregate;

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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Iterator;

public class NestedAggregatesTestCase extends ESBIntegrationTest {

    private AggregatedRequestClient aggregatedRequestClient;
    private final int no_of_requests = 4;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config13/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getMainSequenceURL());
        aggregatedRequestClient.setSymbol("IBM");
        aggregatedRequestClient.setNoOfIterations(no_of_requests);


    }

    @Test(groups = {"wso2.esb"}, description = "Sending request for aggregation")
    public void test() throws IOException, XMLStreamException {
        int responseCount = 0;

        OMElement response = AXIOMUtil.stringToOM(aggregatedRequestClient.getResponse());
        OMElement soapBody = response.getFirstElement();

        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));
        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("IBM Company"));
        }


        Assert.assertEquals(no_of_requests, responseCount, "Response Aggregation not as expected");
    }

    @Test(groups = {"wso2.esb"}, description = "sending nested aggregate request")
    public void testNestedAggregate() throws IOException, XMLStreamException {

        String response = aggregatedRequestClient.getResponse(createNestedQuoteRequestBody("WSO2", no_of_requests));
        OMElement response2 = AXIOMUtil.stringToOM(response);
        OMElement soapBody = response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        int responseCount = 0;
        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2 Company"));
        }


        Assert.assertEquals(responseCount, no_of_requests * no_of_requests, "Response count Mismatched");
    }

    /*   https://wso2.org/jira/browse/ESBJAVA-2152   */
    @Test(groups = {"wso2.esb"}, description = "sending nested aggregate request > request iterator count 2500")
    public void testNestedAggregateWithLargeMessage() throws IOException, XMLStreamException {
        int messageItr = 15;
        String response = aggregatedRequestClient.getResponse(createNestedQuoteRequestBody("WSO2", messageItr));
        OMElement response2 = AXIOMUtil.stringToOM(response);
        OMElement soapBody = response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        int responseCount = 0;
        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2 Company"));
        }


        Assert.assertEquals(responseCount, messageItr * messageItr, "Response count Mismatched");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        aggregatedRequestClient = null;
        super.cleanup();
    }

    private OMElement createNestedQuoteRequestBody(String symbol, int noOfItr) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method1 = fac.createOMElement("getQuotes", omNs);
        for (int i = 0; i < noOfItr; i++) {
            OMElement method2 = fac.createOMElement("getQuote", omNs);

            for (int j = 0; j < noOfItr; j++) {
                OMElement value1 = fac.createOMElement("request", omNs);
                OMElement value2 = fac.createOMElement("symbol", omNs);
                value2.addChild(fac.createOMText(value1, symbol));
                value1.addChild(value2);
                method2.addChild(value1);

            }
            method1.addChild(method2);
        }
        return method1;
    }


}
