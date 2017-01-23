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
package org.wso2.carbon.esb.mediator.test.switchMediator;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertTrue;

public class WithoutDefaultCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/switchMediator/basic_and_without_default_case_synapse.xml");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Without default case scenario")
    public void testSwitchWithoutDefault() throws AxisFault, XPathExpressionException {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("switchSample1"),
                                             getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                             "IBM");
        assertTrue(response.toString().contains("IBM"));

        try {
            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("switchSample1"),
                                                               getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                                               "test");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            assertTrue(expected.getReason().contains(
                    "The input stream for an incoming message is null"));
        }
    }


}
