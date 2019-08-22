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
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
public class HL7InboundTransportTest extends ESBIntegrationTest {
    private LogViewerClient logViewerClient = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/hl7/inbound/transport/hl7_inbound.xml");

        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }


    /**
     * This test case always needs to be the first, since we depend on control ID generated in expected response to equal
     * request control ID of 1.
     * @throws Exception
     */
    @Test(priority=1, groups = { "wso2.esb" }, description = "Test HL7 Inbound Generated ACK")
    public void testHL7InboundGenerateAck() throws Exception {
        addInboundEndpoint(addEndpoint0());
        HL7InboundTestSender sender = new HL7InboundTestSender();
        String response = sender.send("localhost", 20001);
        Assert.assertTrue(response.contains("Jambugasmulla Mawatha"));
        Thread.sleep(5000);
    }

    @Test(priority=2, groups = { "wso2.esb" }, description = "Test HL7 Inbound Automated ACK")
    public void testHL7InboundAutoAck() throws Exception {
        logViewerClient.clearLogs();
        addInboundEndpoint(addEndpoint1());
        HL7InboundTestSender sender = new HL7InboundTestSender();
        String response = sender.send("localhost", 20000);
        Thread.sleep(500);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean found = false;
        for (int i = 0; i < logs.length; i++) {
            if (logs[i].getMessage().contains("<MSG.3>ADT_A01</MSG.3>")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(response.contains("ACK^A01"));
        Assert.assertTrue(found, "Found HL7 message in ESB log");
        Thread.sleep(5000);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private OMElement addEndpoint0() throws Exception {
        return AXIOMUtil
                .stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                        "                     name=\"Sample2\"\n" +
                        "                     sequence=\"genMain\"\n" +
                        "                     onError=\"fault\"\n" +
                        "                     protocol=\"hl7\"\n" +
                        "                     suspend=\"false\">\n" +
                        "        <parameters>\n" +
                        "            <parameter name=\"inbound.hl7.AutoAck\">false</parameter>\n" +
                        "            <parameter name=\"inbound.hl7.Port\">20001</parameter>\n" +
                        "            <parameter name=\"inbound.hl7.TimeOut\">3000</parameter>\n" +
                        "            <parameter name=\"inbound.hl7.CharSet\">ISO-8859-1</parameter>\n" +
                        "            <parameter name=\"inbound.hl7.ValidateMessage\">false</parameter>\n" +
                        "            <parameter name=\"transport.hl7.BuildInvalidMessages\">false</parameter>\n" +
                        "        </parameters>\n" +
                        "    </inboundEndpoint>");
    }

    private OMElement addEndpoint1() throws Exception {
        return AXIOMUtil.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                "                     name=\"Sample1\"\n" +
                "                     sequence=\"main\"\n" +
                "                     onError=\"fault\"\n" +
                "                     protocol=\"hl7\"\n" +
                "                     suspend=\"false\">\n" +
                "        <parameters>\n" +
                "            <parameter name=\"inbound.hl7.AutoAck\">true</parameter>\n" +
                "            <parameter name=\"inbound.hl7.Port\">20000</parameter>\n" +
                "            <parameter name=\"inbound.hl7.TimeOut\">3000</parameter>\n" +
                "            <parameter name=\"inbound.hl7.CharSet\">UTF-8</parameter>\n" +
                "            <parameter name=\"inbound.hl7.ValidateMessage\">false</parameter>\n" +
                "            <parameter name=\"transport.hl7.BuildInvalidMessages\">false</parameter>\n" +
                "        </parameters>\n" +
                "    </inboundEndpoint>");
    }
}
