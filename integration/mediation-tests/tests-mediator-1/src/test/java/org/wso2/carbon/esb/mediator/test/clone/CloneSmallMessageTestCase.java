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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FixedSizeSymbolGenerator;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/* Tests sending different number of small messages through iterate mediator */

public class CloneSmallMessageTestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server;
    private CloneClient client;

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
        client = new CloneClient();
        axis2Server = new SampleAxis2Server("test_axis2_server_9001.xml");
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_simple.xml");
        axis2Server.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server.start();

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests small message in small number ~20")
    public void testSmallNumbers() throws Exception {

        String symbol = FixedSizeSymbolGenerator.generateMessageKB(5);
        String response;
        for (int i = 0; i < 20; i++) {
            response =
                    client.getResponse(getMainSequenceURL(), symbol);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.contains("WSO2"));
        }
        symbol = null;
        response = null;
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests small message in small number ~100")
    public void testLargeNumbers() throws Exception {
        String symbol = FixedSizeSymbolGenerator.generateMessageKB(5);
        String response;
        for (int i = 0; i < 100; i++) {
            response =
                    client.getResponse(getMainSequenceURL(),
                                       symbol);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.contains("WSO2"));
        }
        symbol = null;
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        axis2Server.stop();
        client.destroy();
        client = null;
        axis2Server = null;
        super.cleanup();
    }

}
