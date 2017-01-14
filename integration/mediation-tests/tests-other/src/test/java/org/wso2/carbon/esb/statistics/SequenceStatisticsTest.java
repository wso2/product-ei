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
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.servers.ThriftServer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class SequenceStatisticsTest extends ESBIntegrationTest {
    public static final int MEDIATOR_ID_INDEX = 4;
    ThriftServer thriftServer;
    private ServerConfigurationManager serverConfigurationManager;

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
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/statistics/sequencereference.xml");
        thriftServer.waitToReceiveEvents(20000, 6); //waiting for esb to send artifact config data to the thriftserver

        //Checking whether all the artifact configuration events are received
        Assert.assertEquals("Six configuration events are required", 6, thriftServer.getMsgCount());
    }

    @Test(groups = {"wso2.esb"}, description = "Proxy statistics message count check.")
    public void statisticsCollectionCountTest() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();
        for (int i = 0; i < 100; i++) {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ReferencingProxy"), null, "WSO2");
        }
        thriftServer.waitToReceiveEvents(20000, 100); //wait to esb for asynchronously send statistics events to the
        // backend
        Assert.assertEquals("Hundred statistics events are required, but different number is found", 100,
                thriftServer.getMsgCount());
    }

    @Test(groups = {"wso2.esb"}, description = "Sequence statistics message count check.")
    public void statisticsCollectionCountTestForNestedSequence() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();
        for (int i = 0; i < 100; i++) {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ReferencingProxyStatisticDisable"), null, "WSO2");
        }
        thriftServer.waitToReceiveEvents(20000, 100); //wait to esb for asynchronously send statistics events to the
        // backend
        Assert.assertEquals("Hundred statistics events are required, but different number is found", 100,
                thriftServer.getMsgCount());
    }

    @Test(groups = {"wso2.esb"}, description = "Nested Sequence statistics statistics event data check")
    public void statisticsEventDataTestForNestedSequence() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();

        axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ReferencingProxyStatisticDisable"), null, "WSO2");
        thriftServer.waitToReceiveEvents(20000, 1);//wait to esb for asynchronously send statistics events
        Assert.assertEquals("Statistics event is received", 1, thriftServer.getMsgCount());
        Map<String, Object> aggregatedEvent =
                ESBTestCaseUtils.decompress((String) thriftServer.getPreservedEventList().get(0).getPayloadData()[1]);
        ArrayList eventList = (ArrayList) aggregatedEvent.get("events");
        HashSet<String> allMediatorEventIds = new HashSet<>();
        for (Object list : eventList) {
            allMediatorEventIds.add((String) ((ArrayList) list).get(MEDIATOR_ID_INDEX));
        }

		/* Mediator list in the StockQuoteProxy
        BackendSequence@0:BackendSequence
		BackendSequence@1:CallMediator
		StockQuoteService@0:StockQuoteService
		BackendSequence@3:HeaderMediator:Action
		BackendSequence@4:PayloadFactoryMediator
		BackendSequence@5:CallMediator
		StockQuoteService@0:StockQuoteService
		BackendSequence@7:RespondMediator
		 */
        ArrayList<String> mediatorList = new ArrayList<>();
        mediatorList.add("BackendSequence@0:BackendSequence");
        mediatorList.add("BackendSequence@1:CallMediator");
        mediatorList.add("StockQuoteService@0:StockQuoteService");
        mediatorList.add("BackendSequence@3:HeaderMediator:Action");
        mediatorList.add("BackendSequence@4:PayloadFactoryMediator");
        mediatorList.add("BackendSequence@5:CallMediator");
        mediatorList.add("StockQuoteService@0:StockQuoteService");
        mediatorList.add("BackendSequence@7:RespondMediator");

        //Checking whether all the mediators are present in the event
        Assert.assertEquals("Eight configuration events are required", 8, eventList.size());

        for (String mediatorId : mediatorList) {
            Assert.assertTrue("Mediator not found", allMediatorEventIds.contains(mediatorId));
        }
    }

    @Test(groups = {"wso2.esb"}, description = "Sequence statistics statistics event data check")
    public void statisticsEventDataTest() throws Exception {
        thriftServer.resetMsgCount();
        thriftServer.resetPreservedEventList();

        axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ReferencingProxy"), null, "WSO2");
        thriftServer.waitToReceiveEvents(20000, 1);//wait to esb for asynchronously send statistics events
        Assert.assertEquals("Statistics event is received", 1, thriftServer.getMsgCount());
        Map<String, Object> aggregatedEvent =
                ESBTestCaseUtils.decompress((String) thriftServer.getPreservedEventList().get(0).getPayloadData()[1]);
        ArrayList eventList = (ArrayList) aggregatedEvent.get("events");
        HashSet<String> allMediatorEventIds = new HashSet<>();
        for (Object list : eventList) {
            allMediatorEventIds.add((String) ((ArrayList) list).get(MEDIATOR_ID_INDEX));
        }

		/* Mediator list in the StockQuoteProxy
        ReferencingProxy@0:ReferencingProxy
		BackendSequence@0:BackendSequence
		BackendSequence@1:CallMediator
		StockQuoteService@0:StockQuoteService
		BackendSequence@3:HeaderMediator:Action
		BackendSequence@4:PayloadFactoryMediator
		BackendSequence@5:CallMediator
		StockQuoteService@0:StockQuoteService
		BackendSequence@7:RespondMediator
		 */

        ArrayList<String> mediatorList = new ArrayList<>();
        mediatorList.add("ReferencingProxy@0:ReferencingProxy");
        mediatorList.add("BackendSequence@0:BackendSequence");
        mediatorList.add("BackendSequence@1:CallMediator");
        mediatorList.add("StockQuoteService@0:StockQuoteService");
        mediatorList.add("BackendSequence@3:HeaderMediator:Action");
        mediatorList.add("BackendSequence@4:PayloadFactoryMediator");
        mediatorList.add("BackendSequence@5:CallMediator");
        mediatorList.add("StockQuoteService@0:StockQuoteService");
        mediatorList.add("BackendSequence@7:RespondMediator");

        //Checking whether all the mediators are present in the event
        Assert.assertEquals("Nine configuration events are required", 9, eventList.size());

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
