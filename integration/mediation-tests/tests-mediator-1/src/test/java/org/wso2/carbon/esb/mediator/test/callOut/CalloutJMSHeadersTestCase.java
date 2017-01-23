/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.esb.mediator.test.callOut;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import static org.testng.Assert.assertTrue;

public class CalloutJMSHeadersTestCase extends ESBIntegrationTest {

    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        activeMQServer.startJMSBrokerAndConfigureESB();
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/mediatorconfig/callout/callout_jms_headers.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

    }

    @Test(groups = {"wso2.esb"}, description = "Callout JMS headers test case")
    public void testCalloutJMSHeaders() throws Exception {

        AxisServiceClient client = new AxisServiceClient();
        String payload = "<payload/>";
        AXIOMUtil.stringToOM(payload);
         client.sendRobust(AXIOMUtil.stringToOM(payload),
                           getProxyServiceURLHttp("JMCalloutClientProxy"), "urn:mediate");

        Thread.sleep(60000); //wait until all message received to jms proxy

        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),
                                                              getSessionCookie());
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        boolean logFound = false;
        for (LogEvent item : logs) {
            if (item.getPriority().equals("INFO")) {
                String message = item.getMessage();
                if (message.contains("RequestHeaderVal")) {
                    logFound = true;
                    break;
                }
            }
        }

        assertTrue(logFound);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        activeMQServer.stopJMSBrokerRevertESBConfiguration();
    }
}
