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


public class AggregatedEnclosingElementTestCase extends ESBIntegrationTest {

    private AggregatedRequestClient aggregatedRequestClient;
    private int no_of_requests = 0;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/aggregatedEnclosingElement/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getProxyServiceURLHttp("aggregateMediatorTestProxy"));
        aggregatedRequestClient.setSymbol("WSO2");
    }


    @Test(groups = {"wso2.esb"}, description = "enclose the element to response aggregator")
    public void testEncloseElementToResponseAggregatorCheck() throws IOException, XMLStreamException {
        no_of_requests = 1;
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
        OMElement response = aggregatedRequestClient.getResponsenew();
        Assert.assertNotNull(response);
        String responseStr = response.toString();
        Assert.assertTrue(responseStr.contains("WSO2"));
        Assert.assertTrue(responseStr.contains("rootelement"));
        Assert.assertTrue(responseStr.contains("www.wso2esb.com"));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        aggregatedRequestClient = null;
        super.cleanup();
    }

}
