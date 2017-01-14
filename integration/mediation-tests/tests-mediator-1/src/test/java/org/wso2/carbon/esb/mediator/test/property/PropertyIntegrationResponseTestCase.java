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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 *  This class tests the functionality of the RESPONSE property
 *
 */

public class PropertyIntegrationResponseTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/RESPONSE.xml");

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "RESPONSETEnabledTrue scenario")
    public void testRESPONSETEnabledTrue() throws IOException {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp
                                                                             ("MyProxy")
                , null, "This is the first request");

        assertTrue(response.toString().contains("This is the first request"), "Response Mismatch");

    }

}
