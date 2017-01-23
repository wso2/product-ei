/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.mediator.test.publishevent;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA4689ErrorHandlingOnPublishEventMediator extends ESBIntegrationTest {
    private static final String PROXY_NAME = "publishevent";

    @BeforeClass(alwaysRun = true)
    public void deployArtifact() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/publishevent/synapseConfig.xml");
    }

    @Test(groups = "wso2.esb", description = "Test whether fault sequence is hit when on error in publish event" +
                                             " mediator")
    public void invokeMediator() throws Exception {

        String endpoint = getProxyServiceURLHttp(PROXY_NAME);
        try {
            OMElement omElement = axis2Client.sendSimpleQuoteRequest(endpoint, null, "WSO2");
            Assert.fail("Publish event mediator does not invoked the fault sequence. This configuration " +
                        "must throw a error");
        } catch (AxisFault fault) {
            Assert.assertEquals(fault.getMessage(), "Fault Sequence Invoked", "Fault sequence is not invoked");
        }

    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
