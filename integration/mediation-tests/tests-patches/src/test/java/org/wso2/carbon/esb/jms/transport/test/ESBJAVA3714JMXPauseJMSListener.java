package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.Utils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
public class ESBJAVA3714JMXPauseJMSListener extends ESBIntegrationTest {

    private String PROXY_NAME = "JMStoHTTPStockQuoteProxy";

    private String msgBefore = "BEFORE_PAUSE_TEST";
    private String msgAfter =  "AFTER_PAUSE_TEST";

    private JMXClient jmxClient;
    private MBeanServerConnection mbsc;


    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/ESBJAVA3714_JMX_Pause_JMS_Listener.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

        jmxClient = new JMXClient(null, null, "localhost", "admin", "admin");
        mbsc = jmxClient.connect();
    }

    @Test(groups = "wso2.esb", description = "JMS Consumer Test before pause")
    public void testJMSListner() throws Exception {
        String msg = "";
        // Put message in queue.
        sendMessage(msgBefore);
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        assertTrue(Utils.checkForLogsWithPriority(logViewerClient, "INFO", msgBefore, 10));
    }

    @Test(groups = "wso2.esb", description = "JMS Consumer Test after pause")
    public void testJMSPause() throws Exception {

        // pause JMS Listener from JMXClient
        Set<ObjectInstance> objSet = mbsc.queryMBeans(new ObjectName("org.apache.axis2:Type=Transport,ConnectorName=jms-listener-*"), null);
        Iterator i = objSet.iterator();
        while (i.hasNext()) {
            ObjectInstance obj = (ObjectInstance) i.next();
            mbsc.invoke(obj.getObjectName(), "pause" , null, null);
        }

        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        assertTrue(Utils.checkForLogsWithPriority(logViewerClient, "INFO", "Listener paused", 10));

        // Put message in queue.
        sendMessage(msgAfter);
        assertFalse(Utils.checkForLogsWithPriority(logViewerClient, "INFO", msgAfter, 10));
    }

    //This was disabled since it failed to start JMS listener intermittently
    @Test(groups = "wso2.esb", description = "JMS Consumer Test after resume", enabled = false)
    public void testJMSResume() throws Exception {

        // redeploy proxy service
        addProxyService(AXIOMUtil.stringToOM(getJMSProxy()));

        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        // JMS should still be paused.
        assertFalse(Utils.checkForLogsWithPriority(logViewerClient, "INFO", msgAfter, 16));

        // resume JMS Listener from JMXClient
        Set<ObjectInstance> objSet = mbsc.queryMBeans(new ObjectName("org.apache.axis2:Type=Transport,ConnectorName=jms-listener-*"), null);
        Iterator i = objSet.iterator();
        while (i.hasNext()) {
            ObjectInstance obj = (ObjectInstance) i.next();
            mbsc.invoke(obj.getObjectName(), "resume" , null, null);
        }

        assertTrue(Utils.checkForLogsWithPriority(logViewerClient, "INFO", "Listener resumed", 10));
        assertTrue(Utils.checkForLogsWithPriority(logViewerClient, "INFO", msgAfter, 10));
    }

    private void sendMessage(String msg) throws Exception {
        // creation of jms queue message producer instance
        JMSQueueMessageProducer sender = new JMSQueueMessageProducer
                (JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());

        String queueName = PROXY_NAME;   // defining a queue name for ActiveMQ

        String message = "<Customer><name>" + msg + "</name></Customer>";  // message

        try {
            sender.connect(queueName);    //establishment of connection
            sender.pushMessage(message);  // push message to queue
        } finally {
            sender.disconnect();
        }
    }

    protected void addProxyService(OMElement proxyConfig) throws Exception {
        String proxyName = proxyConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isProxyServiceExist(contextUrls.getBackEndUrl(), getSessionCookie(), proxyName)) {
            esbUtils.deleteProxyService(contextUrls.getBackEndUrl(), getSessionCookie(), proxyName);
        }
        esbUtils.addProxyService(contextUrls.getBackEndUrl(), getSessionCookie(), setEndpoints(proxyConfig));
    }

    protected void deleteProxyService(String proxyName) throws Exception {
        if (esbUtils.isProxyServiceExist(contextUrls.getBackEndUrl(), getSessionCookie(), proxyName)) {
            esbUtils.deleteProxyService(contextUrls.getBackEndUrl(), getSessionCookie(), proxyName);
        }
    }

    private String getJMSProxy() {
        return "    <proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + PROXY_NAME + "\" transports=\"jms\" startOnLoad=\"true\" trace=\"disable\">\n" +
                "        <description/>\n" +
                "        <target>\n" +
                "            <inSequence>\n" +
                "                <property name=\"OUT_ONLY\" value=\"true\" scope=\"default\" type=\"STRING\"/>\n" +
                "                <log level=\"full\"/>\n" +
                "                <drop/>\n" +
                "            </inSequence>\n" +
                "            <outSequence>\n" +
                "                <send/>\n" +
                "            </outSequence>\n" +
                "            <faultSequence/>\n" +
                "        </target>\n" +
                "    </proxy>";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        jmxClient.disconnect();
    }

}