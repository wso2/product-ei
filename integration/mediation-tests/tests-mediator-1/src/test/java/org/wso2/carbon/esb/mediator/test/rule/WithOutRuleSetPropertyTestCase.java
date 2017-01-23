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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class WithOutRuleSetPropertyTestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();


    }

    @Test(groups = "wso2.esb",
          description = "scenario without rules")
    public void testSequenceWithOutRuleSet() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config_without_rule/synapse.xml");
        try {

            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
            Assert.fail("This Configuration can not be saved successfully due to empty rule set");
        } catch (AxisFault expected) {
            assertEquals(expected.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL,
                         "Fault: value mismatched, should be 'The input stream for an incoming message is null.'");
        }

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
