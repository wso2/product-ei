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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

public class AggregateWithinTimeoutTestCase extends ESBIntegrationTest {

    private AggregatedRequestClient aggregatedRequestClient;
    private final int no_of_requests = 200;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/aggregateWithinTimoutConfig/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getProxyServiceURLHttp("aggregateMediatorTestProxy"));
        aggregatedRequestClient.setSymbol("IBM");
        aggregatedRequestClient.setNoOfIterations(no_of_requests);

    }

    @Test(groups = {"wso2.esb"}, description = "replacing a property by using an enrich mediator", enabled = false)
    public void test() throws IOException {
        int companyCount = 0, responseTagCount = 0, SoapEnvCount = 0;

        String Response = aggregatedRequestClient.getResponse();
        String[] response = getTagArray(Response);

        for (int i = 0; i < response.length; i++) {
            if (response[i].contains("soapenv")) {
                SoapEnvCount++;
            } else if (response[i].contains("WSO2 Company")) {
                companyCount++;
            } else if (response[i].contains("getQuoteResponse")) {
                responseTagCount++;
            }
        }


        Assert.assertTrue(Response.contains("WSO2 Company"));
        Assert.assertTrue(Response.contains("getQuoteResponse"));
        Assert.assertTrue(2 * no_of_requests > responseTagCount);
        Assert.assertTrue((no_of_requests > companyCount));
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        aggregatedRequestClient = null;
        super.cleanup();
    }

    public String[] getTagArray(String xml) {
        return xml.split("<");
    }

}
