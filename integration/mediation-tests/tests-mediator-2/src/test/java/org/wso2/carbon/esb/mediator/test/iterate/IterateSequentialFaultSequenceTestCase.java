/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test case for https://wso2.org/jira/browse/ESBJAVA-3675
 * Test whether a property set inside iterate mediator(sequential=true) can be retrieved inside fault sequence.
 *
 * Class mediator jar : ClassMediatorDemo-1.0.0.jar
 * Class mediator name : com.wso2.example.CsvValidatorMediator
 * Class mediator throws a org.apache.synapse.SynapseException so that the fault sequence will be triggered.
 */

public class IterateSequentialFaultSequenceTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        verifyProxyServiceExistence("iterateMediatorSequentialFaultSequenceTestProxy");
    }

    @Test(groups = "wso2.esb", description = "Tests property retrieval in fault sequence")
    public void testPropertyRetrievalInFaultSequence() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(
                    "iterateMediatorSequentialFaultSequenceTestProxy"), null, "WSO2");
            Assert.fail("Request must throw a AxisFault");
        } catch (AxisFault axisFault) {
            Assert.assertEquals(axisFault.getFaultCode().getLocalPart(), "AB005", "Fault code mismatched");
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
