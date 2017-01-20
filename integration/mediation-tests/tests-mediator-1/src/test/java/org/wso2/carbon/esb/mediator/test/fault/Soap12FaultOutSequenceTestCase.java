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
import static org.testng.Assert.fail;

public class Soap12FaultOutSequenceTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fault/soap12_fault_out_sequence_synapse.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "Creating SOAP1.2 fault messages from outMediator sequence")
    public void testSOAP12FaultFromOutSequence() throws AxisFault {
        try {
            axis2Client.sendSimpleStockQuoteSoap12(
                    getMainSequenceURL(),
                    null,
                    "WSO2");
            fail("This query must throw an Axis Fault.");
        } catch (AxisFault expected) {
            log.info("Fault Message : " + expected.getMessage());
            assertEquals(expected.getReason(), "Custom ERROR Message - Soap12FaultOutSequenceTestCase", "Custom ERROR Message mismatched");
            assertEquals(expected.getFaultCode().getLocalPart(), "VersionMismatch", "Fault code value mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "soap12Env", "Fault code prefix mismatched");
            assertEquals(expected.getFaultRoleElement().getRoleValue(), "automation", "Role mismatched");
            assertEquals(expected.getFaultNodeElement().getNodeValue(), "automation-node", "Fault node mismatched");
            assertEquals(expected.getFaultDetailElement().getText(), "fault details by automation", "Fault detail mismatched");

        }

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }
}
