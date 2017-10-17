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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.rabbitmq.store;

import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import static org.testng.Assert.assertTrue;

/**
 * This test case tests whether messages can be stored and forwarded in a RabbitMQ message store.
 */
public class RabbitMQStoreTestCase extends ESBIntegrationTest {
    private static LogViewerClient logViewer;
    private AxisServiceClient client = new AxisServiceClient();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/store/rabbitmqStoreTest.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @Test(groups = "wso2.esb",
          description = "Testing store and forward mechanism using RabbitMQ message store")
    public void testRMQStoreAndForward() throws Exception {
        logViewer.clearLogs();
        client.sendRobust(Utils.getStockQuoteRequest("RMQ"), getProxyServiceURLHttp("rmqstoreTestProxy"), "getQuote");
        assertTrue(Utils.checkForLog(logViewer, "RMQ Company", 1000), "Message not dispatched from the store and sent to the backend!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
        //Empty try block just to make sure the deployed artifacts are removed regardless of test passing or failing
        } finally {
            cleanup();
        }
    }
}