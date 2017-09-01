package org.wso2.carbon.esb.mediator.test.call;

/*
* Copyright 2015 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

public class CallMediatorFaultHandlingTests extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/call/faultHandlingTests.xml");
    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - Proxy Service Inline InSequence Inline Fault Sequence")
    public void testProxyInlineInSequenceInlineFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/services/InlineInSeqInlineFaultSeqProxy", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - Proxy Service Target InSequence Inline Fault Sequence")
    public void testProxyTargetInSequenceInlineFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/services/TargetInSeqInlineFaultSeqProxy", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - Proxy Service Target InSequence Target Fault Sequence")
    public void testProxyTargetInSequenceTargetFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/services/TargetInSeqTargetFaultSeqProxy", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - Proxy Service Inline InSequence Target Fault Sequence")
    public void testProxyInlineInSequenceTargetFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/services/InlineInSeqTargetFaultSeqProxy", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }


    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - API Inline InSequence Inline Fault Sequence")
    public void testAPIInlineInSequenceInlineFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/callFaultTest/InlineInSeqInlineFaultSeq", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - API Inline InSequence Target Fault Sequence")
    public void testAPIInlineInSequenceTargetFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/callFaultTest/InlineInSeqTargetFaultSeq", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - API Target InSequence Target Fault Sequence")
    public void testAPITargetInSequenceTargetFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/callFaultTest/TargetInSeqTargetFaultSeq", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @Test(groups = "wso2.esb", description = "Check whether Fault sequence is properly working for Call mediator: " +
                                             "Scenario - API Target InSequence Inline Fault Sequence")
    public void testAPITargetInSequenceInlineFaultSequence() throws Exception {

        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(
                        "http://localhost:8480/callFaultTest/TargetInSeqInlineFaultSeq", null, "IBM");

        assertTrue(response.toString().contains("Failure"), "Fault: Fault Sequence is not invoked");

    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }


}
