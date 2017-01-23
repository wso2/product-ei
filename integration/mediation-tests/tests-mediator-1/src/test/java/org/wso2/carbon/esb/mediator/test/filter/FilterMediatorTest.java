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
package org.wso2.carbon.esb.mediator.test.filter;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;


public class FilterMediatorTest extends ESBIntegrationTest {

    private String toUrl;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        toUrl =getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE);

    }

    /**
     * Test for filter mediator  - filter using source and regex
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"})
    public void filterMediatorWithSourceAndRegexTest() throws Exception {

        loadSampleESBConfiguration(1);

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuote"), null, "WSO2");


        Assert.assertTrue(response.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response.toString().contains("WSO2 Company"));


    }

    /**
     * Test for filter mediator  - filter using source and regex with namespace prefix
     * /filters/filter/syanpse1.xml is used
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void filterMediatorWithSourceAndRegexNSTest() throws Exception {
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "filters" + File.separator + "filter" + File.separator + "synapse1.xml");
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), toUrl, "IBM");
        Assert.assertTrue(response.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response.toString().contains("IBM Company"));
    }

    /**
     * With setting "Specify As" set to "Xpath"
     * /filters/filter/syanpse2.xml is used
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void filterMediatorWithXpathTest() throws Exception {
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "filters" + File.separator + "filter" + File.separator + "synapse2.xml");
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        Assert.assertTrue(response.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response.toString().contains("IBM Company"));
    }

    /**
     * Using OR operation with Xpaths
     * /filters/filter/syanpse3.xml is used
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void filterMediatorXpathWithORTest() throws Exception {

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "filters" + File.separator + "filter" + File.separator + "synapse3.xml");
        OMElement response11 = axis2Client.sendSimpleStockQuoteSoap11(getMainSequenceURL(), null, "IBM");
        OMElement response12 = axis2Client.sendSimpleStockQuoteSoap12(getMainSequenceURL(), null, "IBM");

        Assert.assertTrue(response11.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response11.toString().contains("IBM Company"));

        Assert.assertTrue(response12.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response12.toString().contains("IBM Company"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        toUrl = null;
        super.cleanup();

    }


}
