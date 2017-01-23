/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import javax.xml.namespace.QName;

/**
 * https://wso2.org/jira/browse/ESBJAVA-1716
 * UnsupportedOperationException thrown when editing JMS message stores in when broker is WSO2 MB.
 */

public class ESBJAVA1716UpdatingJMSMessageStoreBeingUsedTestCase extends ESBIntegrationTest {
    private OMElement synapseConfig;
    private MessageStoreAdminClient messageStoreAdminClient;
    private LogViewerClient logViewer;
    private String messageStoreName = "JMSTestMessageStoreUpdatingTest";
    private String proxyServiceName = "JMSStoreAndProcessorTestProxy";

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        synapseConfig = esbUtils.loadResource("/artifacts/ESB/jms/transport/ESBJAVA-1716_messageStore.xml");
        synapseConfig = JMSEndpointManager.setConfigurations(synapseConfig);
        messageStoreAdminClient = new MessageStoreAdminClient(contextUrls.getBackEndUrl(),getSessionCookie());
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            //to create a subscription for WSO2 MB. then JMSEndPoint queue is created in MB
            consumer.connect("JMSEndPoint");
            consumer.popMessage();

        } finally {
            consumer.disconnect();
        }
    }

    @Test(groups = {"wso2.esb"}, description = "Adding MessageStore configuration and sending messages to store using a proxy")
    public void addMessageStoreConfigurationTest() throws Exception {
        updateESBConfiguration(synapseConfig);
        Thread.sleep(3000);

        AxisServiceClient client = new AxisServiceClient();
        for (int i = 0; i < 5; i++) {
            client.sendRobust(Utils.getStockQuoteRequest("JMS"), getProxyServiceURLHttp(proxyServiceName), "getQuote");
        }

        Thread.sleep(5000);
    }

    @Test(groups = {"wso2.esb"}, description = "Updating MessageStore once it is used by message processor"
            , dependsOnMethods = "addMessageStoreConfigurationTest")
    public void updateMessageStoreBeingUsedTest() throws Exception {
        int beforeLogSize = logViewer.getAllSystemLogs().length;
        messageStoreAdminClient.updateMessageStore(synapseConfig.getFirstChildWithName(
                new QName(synapseConfiguration.getNamespace().getNamespaceURI(), "messageStore")));
        Thread.sleep(5000);
        esbUtils.isMessageStoreDeployed(contextUrls.getBackEndUrl(), getSessionCookie(), messageStoreName);
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;

        for (int i = 0; i < (afterLogSize - beforeLogSize); i++) {
            Assert.assertFalse(logs[i].getMessage().contains("synapse.message.processor.quartz.JMSTestMessageProcessor-forward job threw an")
                    , "Exception observed in backend when editing message store which is used by message processor > " + logs[i].getMessage());
        }

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
