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
package org.wso2.carbon.esb.mediator.test.rule;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


public class MultipleRuleSetPropertyTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config_multiple_rule/synapse.xml");

    }

    @Test(groups = "wso2.esb",
            description = "scenario with multiple rules- Invoke IBM rule")
    public void testInvokeIBMRule() throws AxisFault {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "http://localhost:9000/services/SimpleStockQuoteService", "IBM");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb",
            description = "scenario with multiple rules- Invoke SUN rule ")
    public void testInvokeSUNRule() throws Exception {
        try {
            // If the endpoint suspended from the previous request then need to wait until the endpoint is active
            Thread.sleep(35000);
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "http://localhost:9000/services/SimpleStockQuoteService", "SUN");
            assertTrue(response.toString().contains("WRONG_RULE"), "Fault: value 'responseText' mismatched");
        } catch (AxisFault axisFault) {
            throw axisFault;
        }
    }


    @Test(groups = "wso2.esb",
            description = "scenario with multiple rules- Invoke MFST rule ")
    public void testInvokeMSFTRule() throws Exception {
        try {
            // If the endpoint suspended from the previous request then need to wait until the endpoint is active
            Thread.sleep(35000);
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "http://localhost:9000/services/SimpleStockQuoteService", "MFST");
            assertTrue(response.toString().contains("WRONG_RULE"), "Fault: value 'responseText' mismatched");
        } catch (AxisFault axisFault) {
            throw axisFault;
        }

    }


    @Test(groups = "wso2.esb",
            description = "scenario with multiple rules- Invoke an invalid rule ")
    public void testInvokeInvalidRule() throws Exception {
        // If the endpoint suspended from the previous request then need to wait until the endpoint is active
        Thread.sleep(35000);
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "http://localhost:9000/services/SimpleStockQuoteService", "Invalid");
            fail();
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL,
                    "Fault: value mismatched, should be 'The input stream for an incoming message is null.'");
        }

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
