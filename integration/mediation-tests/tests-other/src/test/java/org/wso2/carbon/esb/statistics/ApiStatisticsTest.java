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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.statistics;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.servers.ThriftServer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class ApiStatisticsTest extends ESBIntegrationTest {
    ThriftServer thriftServer;
    private ServerConfigurationManager serverConfigurationManager;
    public static final int MEDIATOR_ID_INDEX = 4;
    String url = "http://127.0.0.1:8480/stockquote/view/IBM";
    String postUrl = "http://127.0.0.1:8480/stockquote/order/";
    String payload = "<placeOrder xmlns=\"http://services.samples\">\n" +
            "  <order>\n" +
            "     <price>50</price>\n" +
            "     <quantity>10</quantity>\n" +
            "     <symbol>IBM</symbol>\n" +
            "  </order>\n" +
            "</placeOrder>";
    String contentType = "application/xml";

    @BeforeClass(alwaysRun = true)
    protected void initialize() throws Exception {
        //Starting the thrift port to listen to statistics events
        thriftServer = new ThriftServer("Wso2EventTestCase", 7612, true);
        thriftServer.start(7612);
        log.info("Thrift Server is Started on port 8462");

        //Changing synapse configuration to enable statistics and tracing
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(
                new File(getESBResourceLocation() + File.separator + "StatisticTestResources" + File.separator +
                        "synapse.properties"));
        super.init();

        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();
        //load esb configuration to the server
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/statistics/synapseconfigapi.xml");
        thriftServer.waitToReceiveEvents(20000, 3); //waiting for esb to send artifact config data to the thriftserver

        //Checking whether all the artifact configuration events are received
        Assert.assertEquals("Three configuration events are required", 3, thriftServer.getMsgCount());
    }

    @Test(groups = {"wso2.esb"}, description = "API statistics message count check.")
    public void statisticsCollectionCountTest() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();

        for (int i = 0; i < 100; i++) {
            SimpleHttpClient httpClient = new SimpleHttpClient();
            httpClient.doGet(url, null);
        }

        thriftServer.waitToReceiveEvents(20000, 100); //wait to esb for asynchronously send statistics
        // events to the backend
        Assert.assertEquals("Hundred statistics events are required, but different number is found", 100,
                thriftServer.getMsgCount());
    }

    @Test(groups = {"wso2.esb"}, description = "API statistics message count check for post requests")
    public void statisticsCollectionCountTestForPostRequests() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();
        for (int i = 0; i < 100; i++) {
            SimpleHttpClient httpClient = new SimpleHttpClient();
            httpClient.doPost(postUrl, null, payload, contentType);
        }
        thriftServer.waitToReceiveEvents(20000, 100); //wait to esb for asynchronously send statistics
        // events to the backend
        Assert.assertEquals("Hundred statistics events are required, but different number is found", 100,
                thriftServer.getMsgCount());
    }

    @Test(groups = {"wso2.esb"}, description = "API statistics statistics event data check")
    public void statisticsEventDataTest() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();
        SimpleHttpClient httpClient = new SimpleHttpClient();
        httpClient.doGet(url, null);
        thriftServer.waitToReceiveEvents(20000, 1);//wait to esb for asynchronously send statistics events
        Assert.assertEquals("Statistics event is received", 1, thriftServer.getMsgCount());
        Map<String, Object> aggregatedEvent =
                ESBTestCaseUtils.decompress((String) thriftServer.getPreservedEventList().get(0).getPayloadData()[1]);
        ArrayList eventList = (ArrayList) aggregatedEvent.get("events");
        HashSet<String> allMediatorEventIds = new HashSet<>();
        for (Object list : eventList) {
            allMediatorEventIds.add((String) ((ArrayList) list).get(MEDIATOR_ID_INDEX));
        }

        /*
        Mediator list in the StockQuoteAPI
        StockQuoteAPI@0:StockQuoteAPI
        StockQuoteAPI@1:Resource
        StockQuoteAPI@2:API_INSEQ
        StockQuoteAPI@3:PayloadFactoryMediator
        StockQuoteAPI@4:HeaderMediator:Action
        StockQuoteAPI@5:SendMediator
        StockQuoteAPI@6:AnonymousEndpoint
        StockQuoteAPI@7:API_OUTSEQ
        StockQuoteAPI@8:SendMediator
        */

        ArrayList<String> mediatorList = new ArrayList<>();
        mediatorList.add("StockQuoteAPI@0:StockQuoteAPI");
        mediatorList.add("StockQuoteAPI@1:Resource");
        mediatorList.add("StockQuoteAPI@2:API_INSEQ");
        mediatorList.add("StockQuoteAPI@3:PayloadFactoryMediator");
        mediatorList.add("StockQuoteAPI@4:HeaderMediator:Action");
        mediatorList.add("StockQuoteAPI@5:SendMediator");
        mediatorList.add("StockQuoteAPI@6:AnonymousEndpoint");
        mediatorList.add("StockQuoteAPI@7:API_OUTSEQ");
        mediatorList.add("StockQuoteAPI@8:SendMediator");

        //Checking whether all the mediators are present in the event
        Assert.assertEquals("Nine configuration events are required", 9, eventList.size());

        for (String mediatorId : mediatorList) {
            Assert.assertTrue("Mediator not found", allMediatorEventIds.contains(mediatorId));
        }

    }


    @Test(groups = {"wso2.esb"}, description = "API statistics statistics event data check for post requests")
    public void statisticsEventDataTestForPostRequest() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();

        SimpleHttpClient httpClient = new SimpleHttpClient();
        httpClient.doPost(postUrl, null, payload, contentType);
        thriftServer.waitToReceiveEvents(20000, 1);//wait to esb for asynchronously send statistics events
        // to the backend
        Assert.assertEquals("Statistics event is received", 1, thriftServer.getMsgCount());
        Map<String, Object> aggregatedEvent =
                ESBTestCaseUtils.decompress((String) thriftServer.getPreservedEventList().get(0).getPayloadData()[1]);
        ArrayList eventList = (ArrayList) aggregatedEvent.get("events");
        HashSet<String> allMediatorEventIds = new HashSet<>();
        for (Object list : eventList) {
            allMediatorEventIds.add((String) ((ArrayList) list).get(MEDIATOR_ID_INDEX));
        }

        /*
        Mediator list in the StockQuoteAPI
        StockQuoteAPI@0:StockQuoteAPI
        StockQuoteAPI@9:Resource
        StockQuoteAPI@10:API_INSEQ
        PropertyMediator:FORCE_SC_ACCEPTED
        StockQuoteAPI@12:PropertyMediator:OUT_ONLY
        StockQuoteAPI@13:SendMediator
        StockQuoteAPI@14:AnonymousEndpoint
        */

        ArrayList<String> mediatorList = new ArrayList<>();
        mediatorList.add("StockQuoteAPI@0:StockQuoteAPI");
        mediatorList.add("StockQuoteAPI@9:Resource");
        mediatorList.add("StockQuoteAPI@10:API_INSEQ");
        mediatorList.add("StockQuoteAPI@11:PropertyMediator:FORCE_SC_ACCEPTED");
        mediatorList.add("StockQuoteAPI@12:PropertyMediator:OUT_ONLY");
        mediatorList.add("StockQuoteAPI@13:SendMediator");
        mediatorList.add("StockQuoteAPI@14:AnonymousEndpoint");

        //Checking whether all the mediators are present in the event
        Assert.assertEquals("Four configuration events are required", 7, eventList.size());

        for (String mediatorId : mediatorList) {
            Assert.assertTrue("Mediator not found", allMediatorEventIds.contains(mediatorId));
        }

    }

    @AfterClass(alwaysRun = true)
    public void cleanupArtifactsIfExist() throws Exception {
        thriftServer.stop();
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
