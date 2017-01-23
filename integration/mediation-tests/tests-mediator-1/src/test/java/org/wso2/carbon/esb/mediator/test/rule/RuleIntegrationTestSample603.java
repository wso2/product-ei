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

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class RuleIntegrationTestSample603 extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config603/synapse.xml");
    }

    @Test(groups = "wso2.esb",
          description = "Scenario to verify advance rule based routing - Switching Routing" +
                        "Decision According to the Rules - Rule Mediator as Switch mediator -Invoke IBM rule")
    public void testInvokeIBMRule() throws AxisFault {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");

    }


    @Test(groups = "wso2.esb",
          description = "Scenario to verify advance rule based routing - Switching Routing" +
                        "Decision According to the Rules - Rule Mediator as Switch mediator- Invoke SUN rule ")
    public void testInvokeSUNRule() throws Exception {
        TimeUnit.SECONDS.sleep(30);
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "SUN");
            fail("Response message not expected. AxisFault expected");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), ESBTestConstant.READ_TIME_OUT,
                         "Fault: value mismatched, should be 'Read timed out'");
        }
    }


    @Test(groups = "wso2.esb",
          description = "Scenario to verify advance rule based routing - Switching Routing" +
                        "Decision According to the Rules - Rule Mediator as Switch mediator- Invoke MFST rule ")
    public void testInvokeMSFTRule() throws Exception {
        TimeUnit.SECONDS.sleep(30);
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "MFST");
            fail("Response message not expected. AxisFault expected");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), ESBTestConstant.READ_TIME_OUT,
                         "Fault: value mismatched, should be 'Read timed out'");
        }

    }


    @Test(groups = "wso2.esb",
          description = "Scenario to verify advance rule based routing - Switching Routing" +
                        "Decision According to the Rules - Rule Mediator as Switch mediator- Invoke an invalid rule ")
    public void testInvokeInvalidRule() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "Invalid");
            fail("Response message not expected. AxisFault expected");
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
