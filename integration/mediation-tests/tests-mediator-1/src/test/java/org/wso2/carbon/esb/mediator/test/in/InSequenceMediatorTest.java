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
 * This test case covers following test scenarios.
 * 1. Have an "In" sequence with set of child mediators and an 'outMediator' path. Send a message and observe if the In path is correctly invoked.
 * 2. Observe if 'In' path is skipped when the response is received at ESB.
 */
public class InSequenceMediatorTest extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/in/synapse.xml");

    }

    @Test(groups = {"wso2.esb"})
    public void inSequenceTest() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"), "'GetQuoteResponse' String not found !");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "'GetQuoteResponse' String not found !");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
