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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Checking if SOAP12 fault message is valid
 */
public class Soap12FaultMessageTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fault/soap12_fault_response_validate_synapse.xml");

    }

    /**
     * To check the validity of fault response
     * Test artifacts : mediatorconfig/fault/soap12_fault_response_validate_synapse.xml
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", enabled = false)
    public void testSoap12FaultMessage() throws Exception {


        try {
            axis2Client.sendSimpleStockQuoteSoap12(getMainSequenceURL(),
                                                    "http://localhost:9020/services/NonExistingService",
                                                    "MSFT");
            Assert.fail("Expected Axis Fault not occurred ");
        } catch (AxisFault expected) {

            log.info("Fault Message : " + expected.getMessage());
            assertTrue(expected.getReason().contains("Connection refused"), "ERROR Message mismatched." +
                                                                            " Not Contain Connection refused or." +
                                                                            " actual message:" + expected.getMessage());
            assertEquals(expected.getFaultCode().getLocalPart(), "Receiver", "Fault code value mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "tns", "Fault code prefix mismatched");

        }


    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
