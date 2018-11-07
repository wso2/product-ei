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
import org.wso2.andes.kernel.AndesException;
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
 * This class includes all order guaranteeing tests
 */
public class OrderGuaranteeTestCase extends MBPlatformBaseTest {

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
     * Publish message to a single node and receive from the same node and check for any out of
     * order delivery and message duplication.
     *
     * @param messageCount  Number of message to send and receive
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Same node ordered delivery test case")
    @Parameters({"messageCount"})
    public void testSameNodeOrderedDelivery(long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
                   IOException, AndesClientException, AndesException, DataAccessUtilException, InterruptedException {
        // Number of messages expected
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;
        String queueName = "singleQueueOrder1";

        HostAndPort brokerAddress = getRandomAMQPBrokerAddress();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(brokerAddress.getHostText(),
                                    brokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);
        consumerConfig.setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES);

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

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluating
        Assert.assertTrue(consumerClient.checkIfMessagesAreInOrder(), "Messages did not receive in order.");
        Assert.assertEquals(consumerClient.checkIfMessagesAreDuplicated().size(), 0, "Messages are not duplicated.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(queueName), 0, "Slots left in database");

    }

    /**
     * Publish message to a single node and receive from another node and check for any out of order
     * delivery and message duplication.
     *
     * @param messageCount  Number of message to send and receive
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Different node ordered delivery test case")
    @Parameters({"messageCount"})
    public void testDifferentNodeOrderedDelivery(long messageCount)
            throws AndesClientConfigurationException, XPathExpressionException, JMSException, NamingException,
                   IOException, AndesClientException, DataAccessUtilException, InterruptedException {
        // Number of messages expected
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;
        String queueName = "singleQueueOrder2";

        HostAndPort consumerBrokerAddress = getRandomAMQPBrokerAddress();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(consumerBrokerAddress.getHostText(),
                            consumerBrokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);
        consumerConfig.setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES);

        HostAndPort publisherBrokerAddress = getRandomAMQPBrokerAddress();

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(publisherBrokerAddress.getHostText(),
                         publisherBrokerAddress.getPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        // Creating clients
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

        // Evaluating
        Assert.assertTrue(consumerClient.checkIfMessagesAreInOrder(), "Messages did not receive in order.");
        Assert.assertEquals(consumerClient.checkIfMessagesAreDuplicated().size(), 0, "Messages are not duplicated.");

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

        if (tempAndesAdminClient.getQueueByName("singleQueue1") != null) {
            tempAndesAdminClient.deleteQueue("singleQueue1");
        }

        if (tempAndesAdminClient.getQueueByName("singleQueue2") != null) {
            tempAndesAdminClient.deleteQueue("singleQueue2");
        }
    }
}