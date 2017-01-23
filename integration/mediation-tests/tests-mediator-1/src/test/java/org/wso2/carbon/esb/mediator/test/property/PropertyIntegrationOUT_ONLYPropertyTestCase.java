/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

/**
 * This class tests the functionality of OUT_ONLY property
 */
public class PropertyIntegrationOUT_ONLYPropertyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Tests when Out_Only property is disabled")
    public void testOutOnlyPropertyEnabledFalse() throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/OUT_ONLY_DISABLED.xml");


        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("MyProxy"), null, "WSO2");

        assertTrue(response.toString().contains("GetQuoteResponse"));
        assertTrue(response.toString().contains("WSO2 Company"));
    }

    @Test(groups = "wso2.esb", description = "Tests when Out_Only property is enabled",
          dependsOnMethods = "testOutOnlyPropertyEnabledFalse", expectedExceptions = AxisFault.class)
    public void testOutOnlyPropertyEnabledTrue() throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/OUT_ONLY_ENABLED.xml");

        axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("MyProxy"), null, "WSO2");
    }
}


