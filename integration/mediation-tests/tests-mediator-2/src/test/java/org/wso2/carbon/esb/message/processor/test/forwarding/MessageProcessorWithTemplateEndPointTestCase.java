/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
*  KIND, either express or implied. See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.wso2.carbon.esb.message.processor.test.forwarding;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import static java.io.File.separator;

/**
 * This test case test Forwarding Message Processor with Template End-Point
 */
public class MessageProcessorWithTemplateEndPointTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath(separator + "artifacts" + separator + "ESB" + separator + "synapseconfig"
                + separator + "processor" + separator + "forwarding" + separator + "ProcessorWithTemplateEndPoint.xml");

    }

    /**
     * Create a message processor which processes messages that are in a In memory message store
     * Test artifact: /artifacts/ESB/synapseconfig/processor/forwarding/ProcessorWithTemplateEndPoint.xml
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "Test a message processor with target endpoint defined as a template "
            + "endpoint ")
    public void testForwardingProcessorwithTemplateEndPoint() throws Exception {

        //Setting up Wire Monitor Server
        WireMonitorServer wireServer = new WireMonitorServer(9501);
        wireServer.start();

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
            Assert.fail("Unexpected reply received !!!");
        } catch (Exception e) {
            // Axis Fault Expected
        }

        String serverResponse = wireServer.getCapturedMessage();

        Assert.assertTrue(serverResponse.contains("WSO2"), "'WSO2 Company' String not found at backend port "
                + "listener!");
        Assert.assertTrue(serverResponse.contains("request"), "'getQuoteResponse' String not found at backend port "
                + "listener !");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
