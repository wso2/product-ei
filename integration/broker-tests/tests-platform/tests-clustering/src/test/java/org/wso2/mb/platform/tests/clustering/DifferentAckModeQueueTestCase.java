/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.platform.tests.clustering;

import com.google.common.net.HostAndPort;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.platform.common.utils.DataAccessUtil;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.wso2.mb.platform.common.utils.exceptions.DataAccessUtilException;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * This class includes test cases to test different ack modes for queues
 */
public class DifferentAckModeQueueTestCase extends MBPlatformBaseTest {

    private DataAccessUtil dataAccessUtil = new DataAccessUtil();

    /**
     * Prepare environment for tests.
     *
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws LoginAuthenticationExceptionException, IOException, XPathExpressionException,
            URISyntaxException, SAXException, XMLStreamException, AutomationUtilException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        super.initAndesAdminClients();
    }

    /**
     * Publish messages to a single node and receive from the same node with SESSION_TRANSACTED
     * ack mode
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "SESSION_TRANSACTED ack mode test case for queue")
    @Parameters({"messageCount"})
    public void testSessionTransactedAckModeForQueueTestCase(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
                   IOException, AndesClientException, DataAccessUtilException, InterruptedException {
        // Expected message count
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;
        String queueName = "sessionTransactedAckQueue";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                            brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.SESSION_TRANSACTED);
        consumerConfig.setCommitAfterEachMessageCount(1L);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(brokerAddress.getHostText(),
                         brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");
    }

    /**
     * Publish messages to a single node and receive from the same node with AUTO_ACKNOWLEDGE
     * ack mode
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "AUTO_ACKNOWLEDGE ack mode test case for queue")
    @Parameters({"messageCount"})
    public void testAutoAcknowledgeModeForQueue(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
                   IOException, AndesClientException, DataAccessUtilException, InterruptedException {
        // Expected message count
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;
        String queueName = "autoAcknowledgeQueue";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                                brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(brokerAddress.getHostText(),
                                 brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");
    }

    /**
     * Publish messages to a single node and receive from the same node with CLIENT_ACKNOWLEDGE
     * ack mode
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "CLIENT_ACKNOWLEDGE ack mode test case for queue")
    @Parameters({"messageCount"})
    public void testClientAcknowledgeModeForQueue(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
                   IOException, AndesClientException, DataAccessUtilException, InterruptedException {
        // Expected message count
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;
        String queueName = "clientAcknowledgeQueue";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                            brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig.setAcknowledgeAfterEachMessageCount(1L);
        consumerConfig.setRunningDelay(10L);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(brokerAddress.getHostText(),
                            brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");
    }

    /**
     * Publish messages to a single node and receive from the same node with DUPS_OK_ACKNOWLEDGE
     * ack mode
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "DUPS_OK_ACKNOWLEDGE ack mode test case for queue")
    @Parameters({"messageCount"})
    public void testDupOkAcknowledgeModeForQueue(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, JMSException, NamingException,
                   IOException, AndesClientException, DataAccessUtilException, InterruptedException {
        // Expected message count
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;
        String queueName = "dupsOkAcknowledgeQueue";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                            brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.DUPS_OK_ACKNOWLEDGE);
        consumerConfig.setRunningDelay(10L);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(brokerAddress.getHostText(),
                             brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");
    }

    /**
     * Cleanup after running tests.
     *
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    @AfterClass(alwaysRun = true)
    public void destroy() throws AndesAdminServiceBrokerManagerAdminException, RemoteException {

        String randomInstanceKey = getRandomMBInstance();

        AndesAdminClient tempAndesAdminClient = getAndesAdminClientWithKey(randomInstanceKey);

        if (tempAndesAdminClient.getQueueByName("sessionTransactedAckQueue") != null) {
            tempAndesAdminClient.deleteQueue("sessionTransactedAckQueue");
        }

        if (tempAndesAdminClient.getQueueByName("autoAcknowledgeQueue") != null) {
            tempAndesAdminClient.deleteQueue("autoAcknowledgeQueue");
        }

        if (tempAndesAdminClient.getQueueByName("clientAcknowledgeQueue") != null) {
            tempAndesAdminClient.deleteQueue("clientAcknowledgeQueue");
        }

        if (tempAndesAdminClient.getQueueByName("dupsOkAcknowledgeQueue") != null) {
            tempAndesAdminClient.deleteQueue("dupsOkAcknowledgeQueue");
        }
    }

}
