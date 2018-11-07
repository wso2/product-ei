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
 * This class includes subscription disconnecting and reconnecting tests
 */
public class SubscriptionDisconnectingTestCase extends MBPlatformBaseTest {

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
     * Publish messages to a single node and receive from the same node while reconnecting 4 times.
     *
     * @param messageCount  Number of message to send and receive
     * @throws XPathExpressionException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Same node subscription reconnecting test")
    @Parameters({"messageCount"})
    public void testSameNodeSubscriptionReconnecting(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
                   IOException, AndesClientException, DataAccessUtilException {

        long sendCount = messageCount;
        long expectedCount = sendCount / 4;
        long printDivider = 10L;
        String queueName = "singleQueueSubscription1";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                            brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(brokerAddress.getHostText(),
                             brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCount, "Message receiving failed for consumerClient1");

        AndesClient consumerClient2 = new AndesClient(consumerConfig, true);
        consumerClient2.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient2.getReceivedMessageCount(), expectedCount, "Message receiving failed for consumerClient2");

        AndesClient consumerClient3 = new AndesClient(consumerConfig, true);
        consumerClient3.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient3, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient3.getReceivedMessageCount(), expectedCount, "Message receiving failed for consumerClient3");

        AndesClient consumerClient4 = new AndesClient(consumerConfig, true);
        consumerClient4.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient4, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient4.getReceivedMessageCount(), expectedCount, "Message receiving failed for consumerClient4");

        long totalMessagesReceived = consumerClient1.getReceivedMessageCount() + consumerClient2.getReceivedMessageCount() +
                                     consumerClient3.getReceivedMessageCount() + consumerClient4
                                             .getReceivedMessageCount();

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(totalMessagesReceived, expectedCount * 4, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");
    }

    /**
     * Publish messages to a single node and receive from random nodes while reconnecting 4 times.
     *
     * @param messageCount  Number of message to send and receive
     * @throws XPathExpressionException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws CloneNotSupportedException
     */
    @Test(groups = "wso2.mb", description = "Random node subscription reconnecting test")
    @Parameters({"messageCount"})
    public void testDifferentNodeSubscriptionReconnecting(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
                   IOException, CloneNotSupportedException, AndesClientException, DataAccessUtilException {
        long sendCount = messageCount;
        long expectedCount = sendCount / 4;
        long printDivider = 10L;
        String queueName = "singleQueueSubscription2";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                            brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(brokerAddress.getHostText(),
                             brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedCount,
                            "Message " + "receiving failed for consumerClient1");

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig.clone();
        HostAndPort randomAMQPBrokerAddress = getRandomAMQPBrokerAddress();
        consumerConfig2.setHostName(randomAMQPBrokerAddress.getHostText());
        consumerConfig2.setPort(randomAMQPBrokerAddress.getPort());
        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient2.getReceivedMessageCount(), expectedCount, "Message " +
                                                                                      "receiving " +
                                                                                      "failed for" +
                                                                                      " consumerClient2");
        
        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig.clone();
        randomAMQPBrokerAddress = getRandomAMQPBrokerAddress();
        consumerConfig3.setHostName(randomAMQPBrokerAddress.getHostText());
        consumerConfig3.setPort(randomAMQPBrokerAddress.getPort());
        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient3, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient3.getReceivedMessageCount(), expectedCount,
                            "Message " + "receiving failed for consumerClient3");

        AndesJMSConsumerClientConfiguration consumerConfig4 = consumerConfig.clone();
        randomAMQPBrokerAddress = getRandomAMQPBrokerAddress();
        consumerConfig4.setHostName(randomAMQPBrokerAddress.getHostText());
        consumerConfig4.setPort(randomAMQPBrokerAddress.getPort());
        AndesClient consumerClient4 = new AndesClient(consumerConfig4, true);
        consumerClient4.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient4, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient4.getReceivedMessageCount(), expectedCount,
                            "Message receiving failed for consumerClient4");

        long totalMessagesReceived = consumerClient1.getReceivedMessageCount() + consumerClient2.getReceivedMessageCount() +
                                     consumerClient3.getReceivedMessageCount() + consumerClient4
                                             .getReceivedMessageCount();

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(totalMessagesReceived, expectedCount * 4, "Message receiving failed.");

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

        if (tempAndesAdminClient.getQueueByName("singleQueueSubscription1") != null) {
            tempAndesAdminClient.deleteQueue("singleQueueSubscription1");
        }

        if (tempAndesAdminClient.getQueueByName("singleQueueSubscription2") != null) {
            tempAndesAdminClient.deleteQueue("singleQueueSubscription2");
        }
    }
}