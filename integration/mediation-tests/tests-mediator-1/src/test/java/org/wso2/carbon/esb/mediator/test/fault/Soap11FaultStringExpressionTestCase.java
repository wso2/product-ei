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
package org.wso2.carbon.esb.mediator.test.fault;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class Soap11FaultStringExpressionTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fault/soap11_fault_string_expression_synapse.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "Creating SOAP1.1 fault String expression")
    public void testSOAP11FaultStringExpression() throws AxisFault {
        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    getMainSequenceURL(),
                    null,
                    "WSO2");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Fault Message : " + expected.getMessage());
            assertTrue((expected.getReason().contains("Connection refused or") || expected.getReason().contains("Error connecting to the back end")), "ERROR Message mismatched." +
                    " Not Contain Connection refused or." +
                    " actual message:" + expected.getMessage());
            assertEquals(expected.getFaultCode().getLocalPart(), "VersionMismatch", "Fault code value mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "soap11Env", "Fault code prefix mismatched");

        }

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }

}
