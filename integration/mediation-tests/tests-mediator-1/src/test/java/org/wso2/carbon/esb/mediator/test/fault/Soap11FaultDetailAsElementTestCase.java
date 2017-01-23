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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class Soap11FaultDetailAsElementTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fault/soap11_fault_detail_as_element_synapse.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "Creating SOAP1.1 fault details as Element")
    public void testSOAP11FaultDetailAsElement() throws AxisFault {

        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    getMainSequenceURL(),
                    null,
                    "WSO2");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Fault Message : " + expected.getMessage());
            assertEquals(expected.getReason(), "Soap11FaultDetailAsElementTestCase", "Fault Reason Mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "soap11Env", "Fault code prefix mismatched");
            SOAPFaultDetail detailElm = expected.getFaultDetailElement();
            OMElement statusOME = detailElm.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "StatusCode", "axis2ns1"));
            assertNotNull(statusOME, "Fault detail element StatusCode null");
            assertEquals(statusOME.getText(), "1000", "Fault detail StatusCode mismatched");

            OMElement messageOME = detailElm.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "message", "axis2ns1"));
            assertNotNull(messageOME, "Fault detail element message null");
            assertEquals(messageOME.getText(), "fault details by automation", "Fault detail message mismatched");

        }

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }
}
