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
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.inbound.endpoint.InboundAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4702
 */
public class ESBJAVA4702JMSHeaderTest extends ESBIntegrationTest {
    private InboundAdminClient inboundAdminClient;
    private LogViewerClient logViewerClient;

    /**
     * This method initializes an activeMQ server and adds relevant synapse configs to the ESB instance
     *
     * @throws Exception if error occurs in super.init call
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("artifacts/ESB/jms/transport/ESBJAVA4702synapseconfig.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
        inboundAdminClient = new InboundAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        addInboundEndpoint(addEndpoint());
    }

    /**
     * This tests whether the JMS headers are set when reading the headers using an inbound endpoint
     *
     * @throws RemoteException if error occurs in sending the request or reading the logs
     * @throws XMLStreamException if error occurs in reading the payload
     * @throws LogViewerLogViewerException if logviewer is not initialized properly
     * @throws InterruptedException if thread.sleep call is interrupted
     */
    @Test(groups = {"wso2.esb" }, description = "Test JMS Headers : ESBJAVA-4702")
    public void JMSInboundEndpointHeaderTest()
            throws RemoteException, XMLStreamException, LogViewerLogViewerException, InterruptedException {
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
        axis2Client.sendRobust(getProxyServiceURLHttp("JMSProxy"), null, null, AXIOMUtil.stringToOM("<body/>"));
        Thread.sleep(5000);
        boolean isHeaderSet = false;
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent event : logs) {
            String message = event.getMessage();
            if (message.contains("Producer_Log = MDM")) {
                isHeaderSet = true;
            }
        }
        Assert.assertTrue(isHeaderSet);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    /**
     * Provides the endpoint definition as an OMElement
     *
     * @return an OMElement of the endpoint definition
     * @throws XMLStreamException if error occurs when reading the xml
     */
    private OMElement addEndpoint() throws XMLStreamException {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                "                 name=\"JMSIE\"\n" +
                "                 sequence=\"LogSequence\"\n" +
                "                 onError=\"inFault\"\n" +
                "                 protocol=\"jms\"\n" +
                "                 suspend=\"false\">\n" +
                "    <parameters>\n" +
                "        <parameter name=\"interval\">1000</parameter>\n" +
                "        <parameter name=\"transport.jms.Destination\">testqueue</parameter>\n" +
                "        <parameter name=\"transport.jms.CacheLevel\">0</parameter>\n" +
                "        <parameter name=\"transport.jms" +
                ".ConnectionFactoryJNDIName\">QueueConnectionFactory</parameter>\n" +
                "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n"
                +
                "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
                "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
                "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
                "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n" +
                "    </parameters>\n" +
                "</inboundEndpoint>");

        return synapseConfig;
    }
}
