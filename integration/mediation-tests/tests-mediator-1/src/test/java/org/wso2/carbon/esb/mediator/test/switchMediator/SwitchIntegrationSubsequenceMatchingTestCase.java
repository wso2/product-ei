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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.fail;

/**
 * https://wso2.org/jira/browse/CARBON-11568
 */
public class SwitchIntegrationSubsequenceMatchingTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/switch_conf/switch_mediator_subsequence_matching.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Using switch mediator matching the part of the input at regex")
    public void testMatchSubSequenceAtRegexSwitchMediator1() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "WSO2", "Symbol is not match");
    }

    @Test(groups = {"wso2.esb"}, description = "Using switch mediator matching the part of the input at regex")
    public void testMatchSubSequenceAtRegexSwitchMediator2() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
            fail("Request must throw a Axis fault");
        } catch (AxisFault expected) {
            Assert.assertEquals(expected.getReason(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL, "Error Message Mismatched");

        }
    }

    @Test(groups = {"wso2.esb"}, description = "Using switch mediator matching the part of the input at regex")
    public void testMatchSubSequenceAtRegexSwitchMediator3() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "MSFT");
            fail("Request must throw a Axis fault");
        } catch (AxisFault expected) {
            Assert.assertEquals(expected.getReason(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL, "Error Message Mismatched");

        }
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }
}
