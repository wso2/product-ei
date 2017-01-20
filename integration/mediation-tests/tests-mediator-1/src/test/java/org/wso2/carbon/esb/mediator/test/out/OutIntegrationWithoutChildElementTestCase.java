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
package org.wso2.carbon.esb.mediator.test.out;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.testng.Assert;
import org.wso2.esb.integration.common.utils.ESBTestConstant;


public class OutIntegrationWithoutChildElementTestCase extends ESBIntegrationTest {

    @BeforeClass
    public void beforeClass() throws Exception {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/out/out_without_children.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "Out Mediator: Negative Case 1: Out Mediator without children")
    public void testOutWithoutChildren() throws Exception {

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                                                    getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                                    "IBM");
            Assert.fail("This must throw an exception.");
        } catch (AxisFault e) {
            Assert.assertTrue( e.getReason().contains(ESBTestConstant.READ_TIME_OUT),"Evaluate Read Timed out Exception");
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        cleanup();
    }

}
