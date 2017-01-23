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
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;


public class CheckAggregateContentTestCase extends ESBIntegrationTest {


    private AggregatedRequestClient aggregatedRequestClient;
    private final int no_of_requests = 5;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/CheckAggregateChildContentConfig/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getProxyServiceURLHttp("aggregateMediatorTestProxy"));
        aggregatedRequestClient.setSymbol("IBM");
        aggregatedRequestClient.setNoOfIterations(no_of_requests);


    }

    @Test(groups = {"wso2.esb"}, description = "Check the content of the aggregation specified by an xpath")
    public void test() throws IOException, XMLStreamException {


        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response);
        OMElement Response2 = AXIOMUtil.stringToOM(Response);


        OMElement responseParts = Response2.getFirstElement().getFirstElement();
        String singleResponse = responseParts.toString();

        Assert.assertTrue(singleResponse.contains("return"), "return child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("change"), "change child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("earnings"), "earnings child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("high"), "high child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("last"), "last child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("lastTradeTimestamp"), "lastTradeTimestamp child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("low"), "low child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("marketCap"), "marketCap child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("open"), "open child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("name"), "name child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("open"), "open child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("peRatio"), "PerRatio child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("percentageChange"), "PercentageChange child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("symbol"), "symbol child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("volume"), "volume child message does not exist in the aggregated response");
        Assert.assertTrue(singleResponse.contains("getQuoteResponse"), "getQuoteResponse child message does not exist in the aggregated response");


        OMNode content = responseParts.getNextOMSibling();

        for (int count = 0; count < no_of_requests - 1; count++) {

            Assert.assertNotNull(content);
            singleResponse = content.toString();
            Assert.assertTrue(singleResponse.contains("return"), "return child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("change"), "change child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("earnings"), "earinings child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("high"), "high child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("last"), "last child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("lastTradeTimestamp"), "lastTradeTimestamp child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("low"), "low child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("marketCap"), "marketCap child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("open"), "open child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("name"), "name child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("open"), "open child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("peRatio"), "PerRatio child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("percentageChange"), "PercentageChange child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("symbol"), "symbol child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("volume"), "volume child message does not exist in the aggregated response");
            Assert.assertTrue(singleResponse.contains("getQuoteResponse"), "getQuoteResponse child message does not exist in the aggregated response");
            content = content.getNextOMSibling();

        }


    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        aggregatedRequestClient = null;
    }


}
