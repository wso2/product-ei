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
package org.wso2.carbon.esb.mediator.test.in;


import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


/**
 * Test scenarios cover;
 * 1. Put a log mediator inside the IN mediator and send a request to the main sequence and check whether the meassage gets logged
 * 2. Execute sample 15
 */
public class InSequenceIntegrationTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

    }

    /**
     * This test is for testing logging functionality of the In Mediator
     * TODO:   Halted due to Log mediator problem
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", enabled = false)
    public void inSequenceLogTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/in/synapse.xml");
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

        // TODO: Use log viewer to read logs that gets recorded - halted due to Log mediator problem
        /**
         * This message contains symbol "WSO2". So after retrieving logs , they should be asserted with "WSO2"
         *  If the logs contain "WSO2" symbol test should be passed otherwise should be failed
         *
         */
    }

    /**
     * To test sample 15
     * Assertion will only happen if no exception is thrown
     *
     * @throws Exception
     */
    /*https://wso2.org/jira/browse/ESBJAVA-1695*/
    @Test(groups = "wso2.esb")
    public void inSequenceSample15Test() throws Exception {
        loadSampleESBConfiguration(15);
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

        Assert.assertTrue(response.toString().contains("MSFT Company"), "'MSFT Company' String not found !");
        Assert.assertTrue(response.toString().contains("getQuoteResponse"), "'getQuoteResponse' String not found !");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
