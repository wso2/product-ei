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
 * This class includes tests subscribers/publishers with different rates
 */
public class DifferentRateSubscriberTestCase extends MBPlatformBaseTest {

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
     * Publish message to a single node and receive from the same node at a slow rate.
     *
     * @param messageCount number of message to send and receive
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb", description = "Same node slow subscriber test case")
    @Parameters({"messageCount"})
    public void testSameNodeSlowSubscriber(long messageCount)
            throws IOException, JMSException, AndesClientConfigurationException, NamingException,
                   XPathExpressionException, AndesClientException, DataAccessUtilException, InterruptedException {
        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();
        this.runDifferentRateSubscriberTestCase("singleQueue1", 10L, 0L, messageCount, true, brokerAddress, brokerAddress);
    }

    /**
     * Publish message at a slow rate to a single node and receive from the same node.
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Same node slow publisher test case")
    @Parameters({"messageCount"})
    public void testSameNodeSlowPublisher(long messageCount)
            throws XPathExpressionException, IOException, JMSException, AndesClientConfigurationException,
                   NamingException, AndesClientException, DataAccessUtilException, InterruptedException {
        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();
        this.runDifferentRateSubscriberTestCase("singleQueue2", 0L, 10L, messageCount, true, brokerAddress, brokerAddress);
    }


    /**
     * Publish message to a single node and receive from a different node at a slow rate.
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Different node slow subscriber test case")
    @Parameters({"messageCount"})
    public void testDifferentNodeSlowSubscriber(long messageCount)
            throws XPathExpressionException, IOException, JMSException, AndesClientConfigurationException,
                   NamingException, AndesClientException, DataAccessUtilException, InterruptedException {

        this.runDifferentRateSubscriberTestCase("singleQueue3", 10L, 0L, messageCount, false,
                getRandomAMQPBrokerAddress(), getRandomAMQPBrokerAddress());
    }

    /**
     * Publish message at a slow rate to a single node and receive from a different node.
     *
     * @param messageCount number of message to send and receive
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Different node slow publisher test case")
    @Parameters({"messageCount"})
    public void testDifferentNodeSlowPublisher(long messageCount)
            throws XPathExpressionException, IOException, JMSException, AndesClientConfigurationException,
                   NamingException, AndesClientException, DataAccessUtilException, InterruptedException {
        this.runDifferentRateSubscriberTestCase("singleQueue4", 0L, 10L, messageCount, false,
                getRandomAMQPBrokerAddress(), getRandomAMQPBrokerAddress());
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

        if (tempAndesAdminClient.getQueueByName("singleQueue1") != null) {
            tempAndesAdminClient.deleteQueue("singleQueue1");
        }

        if (tempAndesAdminClient.getQueueByName("singleQueue2") != null) {
            tempAndesAdminClient.deleteQueue("singleQueue2");
        }

        if (tempAndesAdminClient.getQueueByName("singleQueue3") != null) {
            tempAndesAdminClient.deleteQueue("singleQueue3");
        }

        if (tempAndesAdminClient.getQueueByName("singleQueue4") != null) {
            tempAndesAdminClient.deleteQueue("singleQueue4");
        }
    }

    /**
     * Runs a receiver and a consumer that publishes messages with a delay.
     *
     * @param destinationName        The destination name
     * @param consumerDelay          The delay in which the consumer sends messages
     * @param publisherDelay         The delay in which the publisher received messages
     * @param messageCount           Number of message to send and receive
     * @param isSameNode             true if the test has subscribers and publishers on the same node
     * @param consumerBrokerAddress  The amqp connection string for consumer
     * @param publisherBrokerAddress The amqp connection string for publisher   @throws org.wso2.mb.integration
     *                               .common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    private void runDifferentRateSubscriberTestCase(String destinationName, long consumerDelay,
            long publisherDelay,
            long messageCount,
            boolean isSameNode, HostAndPort consumerBrokerAddress,
            HostAndPort publisherBrokerAddress)
            throws AndesClientConfigurationException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException, InterruptedException {
        // Number of messages expected
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(consumerBrokerAddress.getHostText(),
                                consumerBrokerAddress.getPort(), ExchangeType.QUEUE, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);
        consumerConfig.setRunningDelay(consumerDelay);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(publisherBrokerAddress.getHostText(),
                             publisherBrokerAddress.getPort(), ExchangeType.QUEUE, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);
        publisherConfig.setRunningDelay(publisherDelay);

        // Creating client
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        if (!isSameNode) {
            AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);
        }

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(destinationName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(destinationName), 0, "Slots left in database");
    }
}