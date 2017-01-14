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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

public class MultipleOutMediatorsTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/out/multiple_out_mediators_config.xml");
    }

    /*https://wso2.org/jira/browse/STRATOS-2257*/
    @Test(groups = {"wso2.esb"}, description = "Out Mediator: Negative Case 2: Multiple out Mediators")
    public void testMultipleOutMediators() throws Exception {


        axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                                                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                                                "IBM");

        // TODO Assert the Test property of INFO log for "Inside First Out Mediator" & "Inside Second Out Mediator"


    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        cleanup();
    }


}
