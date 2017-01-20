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

package org.wso2.carbon.esb.samples.test.mediation;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.ConfigurationContextProvider;
import org.wso2.esb.integration.common.clients.mediation.EventBrokerAdminClient;
import org.wso2.esb.integration.common.clients.mediation.TopicAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

//https://wso2.org/jira/browse/ESBJAVA-3445
//public class Sample460TestCase extends ESBIntegrationTest {

//    private TopicAdminClient topicAdminClient;
//    private EventBrokerAdminClient eventBrokerAdminClient;
//    private WireMonitorServer wireMonitorServer;
//
//    @BeforeClass(alwaysRun = true)
//    public void initialize() throws Exception {
//
//        super.init();
//
//        topicAdminClient = new TopicAdminClient(contextUrls.getBackEndUrl(), getSessionCookie(),
//                ConfigurationContextProvider.getInstance().getConfigurationContext());
//
//        eventBrokerAdminClient = new EventBrokerAdminClient(contextUrls.getBackEndUrl(), getSessionCookie(),
//                ConfigurationContextProvider.getInstance().getConfigurationContext());
//
//        wireMonitorServer = new WireMonitorServer(8991);
//        wireMonitorServer.start();
//
//        loadSampleESBConfiguration(460);
//    }
//
//    @Test(groups = {"wso2.esb"}, description = "testDtaSources ")
//    public void testDtaSources() throws Exception {
//
//        topicAdminClient.addTopic("stockquote");
//        Thread.sleep(5000);
//
//        eventBrokerAdminClient.subscribe("stockquote","http://localhost:8991/");
//        Thread.sleep(5000);
//
//        AxisServiceClient client = new AxisServiceClient();
//        client.sendRobust(Utils.getStockQuoteRequest("WSO2"), getMainSequenceURL(), "placeOrder");
//        Thread.sleep(5000);
//
//        Assert.assertTrue(wireMonitorServer.getCapturedMessage().contains("WSO2"), "Event sink doesnot work");
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void destroy() throws Exception {
//        eventBrokerAdminClient.unsubscribe("stockquote");
//        topicAdminClient.removeTopic("stockquote");
//        super.cleanup();
//    }

//}

