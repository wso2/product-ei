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
package org.wso2.carbon.esb.samples.test.mediation.script;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class Sample352TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        loadSampleESBConfiguration(352);
    }

    @Test(groups = "wso2.esb", description = "Tests level log")
    public void accessSynapseMsgAPI() throws Exception {

        OMElement response;
        //addressing is sent for get mc.getReplyTo()
        response= axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), getBackEndServiceUrl("backEndService"), "wso2");
        assertNotNull(response, "Response message null");
        assertEquals(response.getQName().getLocalPart().toString(), "getQuoteResponse");
        assertEquals(response.getFirstElement().getLocalName(),"return");
        assertTrue(response.toString().contains("<ns:last>99.9</ns:last>"), "Last price mismatched.Not set by js script");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

}
