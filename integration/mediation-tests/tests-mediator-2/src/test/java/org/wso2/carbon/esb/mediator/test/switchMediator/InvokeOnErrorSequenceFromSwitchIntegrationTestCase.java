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

package org.wso2.carbon.esb.mediator.test.switchMediator;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertEquals;

public class InvokeOnErrorSequenceFromSwitchIntegrationTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/switchMediator/onerror_sequence_within_switch_config.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "Switch Mediator : Check whether Onerror sequence is executed if an error happens inside a switch")
    public void testSample2() throws AxisFault, XPathExpressionException {
        //Send mediator will be defined inside onError sequence so that a response will come only is onError sequence is executed.
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                                                    getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                                    "IBM");
            Assert.fail("Expected Axis Fault");
        } catch (AxisFault expected) {
            log.info("Fault Message : " + expected.getMessage());
            assertEquals(expected.getReason(), "InvokeOnErrorSequenceFromSwitchIntegrationTestCase", "ERROR Message mismatched");
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        super.cleanup();
    }

}
