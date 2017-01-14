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
package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.logging.LoggingAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SequenceWhichHasJsFunctionsForBothInOutTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        enableDebugLogging();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config06/synapse.xml");
    }

    @Test(groups = "wso2.esb", description = "Invoke a sequence which has inlined js functions for both 'in' and 'outMediator' flows")
    public void testInvokeSequence() throws Exception {

        OMElement response = axis2Client.sendCustomQuoteRequest(getMainSequenceURL(), null, "WSO2");
        assertNotNull(response,"Fault response message null");

        assertNotNull(response.getQName().getLocalPart(),"Fault response null localpart");
        assertEquals(response.getQName().getLocalPart(), "CheckPriceResponse", "Fault localpart mismatched");

        assertNotNull(response.getFirstElement().getQName().getLocalPart(), " Fault response null localpart");
        assertEquals(response.getFirstElement().getQName().getLocalPart(), "Code", "Fault localpart mismatched");
        assertEquals(response.getFirstElement().getText(),"WSO2","Fault value mismatched");

        assertNotNull(response.getFirstChildWithName(new QName("http://services.samples/xsd","Price")), "Fault response null localpart");


    }

    @AfterClass(alwaysRun = true )
    public void destroy() throws Exception {
        super.cleanup();
    }


    private void enableDebugLogging() throws Exception {
        LoggingAdminClient logAdminClient = new LoggingAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logAdminClient.updateLoggerData("org.apache.synapse", "DEBUG", true, false);
    }
 }
