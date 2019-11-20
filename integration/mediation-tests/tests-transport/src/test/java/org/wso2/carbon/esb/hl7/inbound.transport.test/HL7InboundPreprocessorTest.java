package org.wso2.carbon.esb.hl7.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

public class HL7InboundPreprocessorTest extends ESBIntegrationTest {
    private LogViewerClient logViewerClient = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(priority=3, groups = { "wso2.esb" }, description = "Test HL7 PreProcessor")
    public void testHL7InboundAutoAck() throws Exception {
        logViewerClient.clearLogs();
        addInboundEndpoint(addEndpoint0());
        HL7InboundTestSender sender = new HL7InboundTestSender();
        sender.send("localhost", 20003);
        Thread.sleep(500);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean found = false;
        for (int i = 0; i < logs.length; i++) {
            if (logs[i].getMessage().contains("Encoding ER7")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "Can we see the log added by custom HL7MessagePreprocessor?");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private OMElement addEndpoint0() throws Exception {
        return AXIOMUtil
                .stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                        "                 name=\"hl7_inbound\"\n" +
                        "                 sequence=\"main\"\n" +
                        "                 onError=\"fault\"\n" +
                        "                 protocol=\"hl7\"\n" +
                        "                 suspend=\"false\">\n" +
                        "   <parameters>\n" +
                        "      <parameter name=\"inbound.hl7.ValidateMessage\">true</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.Port\">20003</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.TimeOut\">3000</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.MessagePreProcessor\">org.wso2.sample.MessageFilter</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.AutoAck\">true</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.BuildInvalidMessages\">true</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.PassThroughInvalidMessages\">true</parameter>\n" +
                        "      <parameter name=\"inbound.hl7.CharSet\">UTF-8</parameter>\n" +
                        "   </parameters>\n" +
                        "</inboundEndpoint>");
    }
}
