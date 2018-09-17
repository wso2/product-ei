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
package org.wso2.carbon.esb.jms.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import java.io.File;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;

/**
 * Tests JMS transactions with inbound endpoints.
 */
public class JMSInboundTransactionTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient = null;
    String message;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {

        super.init();

        verifySequenceExistence("jmsInboundEpTransactionRollbackTestInSequence");
        verifySequenceExistence("jmsInboundEpTransactionRollbackTestFaultSequence");
        verifySequenceExistence("jmsInboundEndpointTransactionRequestHandlerSequence");

        message = String.valueOf(loadResource("message.xml"));
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    /**
     * Tests whether committing a transaction removes the message from the queue.
     * Disabling until find a proper fix to https://github.com/wso2/product-ei/issues/1389
     *
     * @throws Exception for any unexpected exception
     */
    @Test(groups = { "wso2.esb" },
          description = "Successfully committing the message", enabled = false)
    public void testTransactionCommit() throws Exception {

        String queueName = "testTransactionCommitQueue";
        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance()
                                                                                                   .getBrokerConfiguration());

        logViewerClient.clearLogs();

        try {
            sender.connect(queueName);
            sender.pushMessage(message);
        } finally {
            sender.disconnect();
        }

        addInboundEndpoint(loadResource("jmsInboundTransactionCommitInboundEp.xml"));

        // Wait till the message is picked from inbound endpoint
        boolean committed = Utils.checkForLog(logViewerClient, "Committed", 8);

        Assert.assertTrue(committed, "Did not find the \"Committed\" log");

        Assert.assertTrue(isQueueEmpty(queueName), "Queue (" + queueName + ") should be empty after commit");
    }

    /**
     * Tests whether rolling back a transaction does not removes the message from the queue.
     *  Disabling until find a proper fix to https://github.com/wso2/product-ei/issues/1389
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" },
          description = "Rolling back the failed message to the queue", enabled = false)
    public void testTransactionRollBack() throws Exception {

        String queueName = "testTransactionRollBackQueue";
        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance()
                                                                                                   .getBrokerConfiguration());
        logViewerClient.clearLogs();

        try {
            sender.connect(queueName);
            sender.pushMessage(message);
        } finally {
            sender.disconnect();
        }

        addInboundEndpoint(loadResource("jmsInboundTransactionRollbackInboundEp.xml"));
        boolean rollbacked = Utils.checkForLog(logViewerClient, "Rollbacked", 8);

        Assert.assertTrue(rollbacked, "Did not find the \"Rollbacked\" log");

        Assert.assertFalse(isQueueEmpty(queueName), "Queue (" + queueName + ") should not be empty after rollback");
    }

    private boolean isQueueEmpty(String queueName) throws Exception {

        String poppedMessage;
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(
                JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect(queueName);
            poppedMessage = consumer.popMessage();
        } finally {
            consumer.disconnect();
        }

        return poppedMessage == null;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

    }

    private OMElement loadResource(String resourceName) throws FileNotFoundException, XMLStreamException {
        return esbUtils.loadResource(
                "artifacts" + File.separator + "ESB" + File.separator + "jms" + File.separator + "inbound"
                        + File.separator + "transport" + File.separator + resourceName);
    }

}
