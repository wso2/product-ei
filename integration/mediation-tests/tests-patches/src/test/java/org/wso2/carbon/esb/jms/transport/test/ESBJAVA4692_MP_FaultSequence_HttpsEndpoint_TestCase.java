/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.jms.transport.test;

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

import static org.testng.Assert.assertTrue;

public class ESBJAVA4692_MP_FaultSequence_HttpsEndpoint_TestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.
                loadResource("/artifacts/ESB/jms/transport/ESBJAVA4692_MP_FaultSequence.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
    }

    @Test(groups = {"wso2.esb"}, description = "MP Fault Sequence test case for https")
    public void testCalloutJMSHeaders() throws Exception {

        AxisServiceClient client = new AxisServiceClient();
        String payload = "<payload/>";
        AXIOMUtil.stringToOM(payload);
        client.sendRobust(AXIOMUtil.stringToOM(payload),
                          getProxyServiceURLHttps("MSProxy"), "urn:mediate");

        Thread.sleep(10000); //wait until message processor picks the message

        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        boolean logFound = false;
        for (LogEvent item : logs) {
            if (item.getPriority().equals("INFO")) {
                String message = item.getMessage();
                if (message.contains("FaultSeq = *********** FaultSeq *****************")) {
                    logFound = true;
                    break;
                }
            }
        }

        assertTrue(logFound, "Fault Sequence Not Executed for Soap Fault");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
