/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.platform.tests.clustering;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.andes.stub.admin.types.Queue;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
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
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * Test class to test queues in clusters
 */
public class QueueClusterTestCase extends MBPlatformBaseTest {

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
     * Send and receive messages in a single node for a queue
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws IOException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     */
    @Test(groups = "wso2.mb", description = "Single queue Single node send-receive test case")
    @Parameters({"messageCount"})
    public void testSingleQueueSingleNodeSendReceive(long messageCount)
            throws XPathExpressionException, AndesAdminServiceBrokerManagerAdminException, IOException,
                   AndesClientConfigurationException, NamingException, JMSException, AndesClientException,
                   DataAccessUtilException, InterruptedException {

        long sendCount = messageCount;
        long expectedCount = messageCount;
        long printRate = 10L;
        String queueName = "clusterSingleQueue1";

        String randomInstanceKey = getRandomMBInstance();

        AutomationContext tempContext = getAutomationContextWithKey(randomInstanceKey);

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                                         Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")),
                                         ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printRate);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        randomInstanceKey = getRandomMBInstance();

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_RUN_TIME);
        Queue queue = getAndesAdminClientWithKey(randomInstanceKey).getQueueByName(queueName);
        assertTrue(queue.getQueueName().equalsIgnoreCase(queueName), "Queue created in MB node 1 not exist");

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                                        Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")),
                                        ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printRate);


        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");
    }

    /**
     * Creating the same queue in 2 different nodes.
     *
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    @Test(groups = "wso2.mb", description = "Single queue replication")
    public void testSingleQueueReplication()
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException, DataAccessUtilException,
                   InterruptedException {

        String queueName = "clusterSingleQueue2";
        String randomInstanceKey = getRandomMBInstance();
        AndesAdminClient tempAndesAdminClient = getAndesAdminClientWithKey(randomInstanceKey);

        if (tempAndesAdminClient.getQueueByName(queueName) != null) {
            tempAndesAdminClient.deleteQueue(queueName);
        }

        tempAndesAdminClient.createQueue(queueName);

        randomInstanceKey = getRandomMBInstance();
        tempAndesAdminClient = getAndesAdminClientWithKey(randomInstanceKey);
        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_RUN_TIME);
        Queue queue = tempAndesAdminClient.getQueueByName(queueName);

        assertTrue(queue != null && queue.getQueueName().equalsIgnoreCase(queueName),
                   "Queue created in MB node instance not replicated in other MB node instance");

        tempAndesAdminClient.deleteQueue(queueName);

        // Wait for queue delete notification to reach other node in the cluster
        TimeUnit.SECONDS.sleep(1);

        randomInstanceKey = getRandomMBInstance();
        tempAndesAdminClient = getAndesAdminClientWithKey(randomInstanceKey);
        queue = tempAndesAdminClient.getQueueByName(queueName);

        assertTrue(queue == null, "Queue deleted in MB node instance not replicated in other MB node instance");

    }

    /**
     * Send messages from one node and received messages from another node.
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws IOException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     */
    @Test(groups = "wso2.mb", description = "Single queue Multi node send-receive test case")
    @Parameters({"messageCount"})
    public void testSingleQueueMultiNodeSendReceive(long messageCount)
            throws XPathExpressionException, AndesAdminServiceBrokerManagerAdminException, IOException,
                   AndesClientConfigurationException, NamingException, JMSException, AndesClientException,
                   DataAccessUtilException, InterruptedException {
        long sendCount = messageCount;
        long expectedCount = messageCount;
        long printRate = 10L;
        String queueName = "clusterSingleQueue3";

        String randomInstanceKey = getRandomMBInstance();

        AutomationContext tempContext = getAutomationContextWithKey(randomInstanceKey);

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                                         Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")),
                                         ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printRate);


        randomInstanceKey = getRandomMBInstance();
        tempContext = getAutomationContextWithKey(randomInstanceKey);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                                        Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")),
                                        ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printRate);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_RUN_TIME);

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate message left in database
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

        if (tempAndesAdminClient.getQueueByName("clusterSingleQueue1") != null) {
            tempAndesAdminClient.deleteQueue("clusterSingleQueue1");
        }

        if (tempAndesAdminClient.getQueueByName("clusterSingleQueue2") != null) {
            tempAndesAdminClient.deleteQueue("clusterSingleQueue2");
        }

        if (tempAndesAdminClient.getQueueByName("clusterSingleQueue3") != null) {
            tempAndesAdminClient.deleteQueue("clusterSingleQueue3");
        }
    }

}
