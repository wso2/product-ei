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


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Iterator;


public class SpecifyMinMessageCountAsExpressionTestCase extends ESBIntegrationTest {
    private AggregatedRequestClient aggregatedRequestClient;
    private int no_of_requests = 0;
    private final int minMessageCount = 5;
    private final int maxMessageCount = 10;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config19/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getProxyServiceURLHttp("aggregateMediatorTestProxy"));
        aggregatedRequestClient.setSymbol("IBM");

    }

    @Test(groups = {"wso2.esb"}, description = "less number of messages than minimum count")
    public void testLessThanMinimum() throws IOException, XMLStreamException {
        int responseCount = 0;
        no_of_requests = 3;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response);
        OMElement Response2 = AXIOMUtil.stringToOM(Response);
        OMElement soapBody = Response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("IBM"));
        }
        Assert.assertEquals(responseCount, no_of_requests, "GetQuoteResponse Element count mismatched");

    }

    @Test(groups = {"wso2.esb"}, description = "number of messages is equal to the minimum")
    public void testEqualtoMinimum() throws IOException, XMLStreamException {
        int responseCount = 0;
        no_of_requests = minMessageCount;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response);
        OMElement Response2 = AXIOMUtil.stringToOM(Response);
        OMElement soapBody = Response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("IBM"));
        }
        Assert.assertEquals(responseCount, no_of_requests, "GetQuoteResponse Element count mismatched");

    }

    @Test(groups = {"wso2.esb"}, description = "number of messages is equal to the maximum")
    public void testEqualtoMaximum() throws IOException, XMLStreamException {
        int responseCount = 0;
        no_of_requests = maxMessageCount;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response);
        OMElement Response2 = AXIOMUtil.stringToOM(Response);
        OMElement soapBody = Response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("IBM"));
        }
        Assert.assertTrue(minMessageCount <= responseCount && responseCount <= no_of_requests);

    }

    @Test(groups = {"wso2.esb"}, description = "higher number of messages than minimum count")
    public void testMoreNumberThanMinimum() throws IOException, XMLStreamException {
        int responseCount = 0;
        no_of_requests = 8;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response);
        OMElement Response2 = AXIOMUtil.stringToOM(Response);
        OMElement soapBody = Response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("IBM"));
        }
        Assert.assertTrue(minMessageCount <= responseCount && responseCount <= no_of_requests);

    }

    @Test(groups = {"wso2.esb"}, description = "more number of messages than maximum count")
    public void testMoreNumberThanMaximum() throws IOException, XMLStreamException {
        int responseCount = 0;
        no_of_requests = 15;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response);
        OMElement Response2 = AXIOMUtil.stringToOM(Response);
        OMElement soapBody = Response2.getFirstElement();
        Iterator iterator = soapBody.getChildrenWithName(new QName("http://services.samples",
                                                                   "getQuoteResponse"));

        while (iterator.hasNext()) {
            responseCount++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("IBM"));
        }
        Assert.assertTrue(minMessageCount <= responseCount && responseCount <= maxMessageCount);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        aggregatedRequestClient = null;
        super.cleanup();
    }

}
