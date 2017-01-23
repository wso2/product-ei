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

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Iterator;

public class PropertyWithinSequenceTestCase extends ESBIntegrationTest {

    private AggregatedRequestClient aggregatedRequestClient;
    private int no_of_requests = 0;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/propertyWithinIterateConfig/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getProxyServiceURLHttp("aggregateMediatorTestProxy"));
        aggregatedRequestClient.setSymbol("IBM");
        no_of_requests = 15;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);

    }


    @Test(groups = {"wso2.esb"}, description = "Defining a property within Iterator")
    public void testPropertyWithinIteratorMediator() throws IOException, XMLStreamException {
        int responseCount = 0;
        for (int i = 0; i < 10; i++) {
            String Response = aggregatedRequestClient.getResponse();
            Assert.assertNotNull(Response);
            OMElement Response2 = AXIOMUtil.stringToOM(Response);
            OMElement soapBody = Response2.getFirstElement();
            Iterator iterator = soapBody.getChildrenWithLocalName("getQuoteResponse");

            while (iterator.hasNext()) {

                OMElement omeReturn = (OMElement) iterator.next();
                responseCount++;
                Iterator<OMElement> itr = omeReturn.getFirstElement().getChildrenWithLocalName("symbol");
                boolean isSymbolFound = false;
                while (itr.hasNext()) {
                    OMElement symbol = itr.next();
                    //to get the number attached by iterator mediator
                    String[] values = symbol.getText().split(" ");
                    Assert.assertEquals(values.length, 2, "Response does not contained the property value attached by Iterator mediator");
                    double property = Double.parseDouble(values[1]);
                    //value must be less tha to 16
                    Assert.assertTrue(16 > property, "Value attached by Iterator mediator to response must be less than to 16");

                    isSymbolFound = true;
                }
                Assert.assertTrue(isSymbolFound, "Symbol Element not found in the response message");
            }
            Assert.assertEquals(responseCount, responseCount, "Response does not contains all getQuoteResponses as in the request");
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        aggregatedRequestClient = null;
        super.cleanup();
    }
}
