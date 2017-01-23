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
package org.wso2.carbon.esb.mediator.test.throttle;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class InvalidThrottlingPolicyTest extends ESBIntegrationTest {

    private static final int THROTTLE_MAX_MSG_COUNT = 4;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/throttle/invalidThrottlingPolicyTest.xml");
    }

    @Test(groups = "wso2.esb",
          description = "Invalid throttling policy test")
    public void testInvalidPolicy() throws Exception {

        OMElement response;

        try{
            for (int i = 0; i <= THROTTLE_MAX_MSG_COUNT; i++) {
                response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
                assertTrue(response.toString().contains("WSO2"),"Fault: Required response not found.");
            }

        }catch (Exception e){
            assertFalse(e.getMessage().contains("**Access Denied**"),"Fault value mismatched, should be 'Access Denied'");
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
