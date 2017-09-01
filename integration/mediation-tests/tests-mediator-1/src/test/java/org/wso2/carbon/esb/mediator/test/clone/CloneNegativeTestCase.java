/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.clone;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

public class CloneNegativeTestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_unmaching_aggregate.xml");
        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server1.start();
        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.start();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests clone ID that does not match with the aggregate ID")
    public void testAggregateID() throws Exception {

        OMElement response = null;
        try {
            response =
                    axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

            Assert.fail("This Request must throws AxisFault"); // This will not executed if the exception thrown as expected
        } catch (AxisFault e) {
            Assert.assertEquals(e.getMessage(), ESBTestConstant.READ_TIME_OUT);
        }

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests against a invalid SOAP adress")
    public void testSOAPAction() throws Exception {
        //TODO QA test has failed

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests against a invalid SOAP adress")
    public void testAddress() throws Exception {
        //TODO not yet tested

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        axis2Server1.stop();
        axis2Server2.stop();
        axis2Server1 = null;
        axis2Server2 = null;
        super.cleanup();
    }

}
