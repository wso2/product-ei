/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.platform.tests.clustering;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
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
import java.util.List;


/**
 * Test class to test compatibility of compression enabled and disabled packs, in cluster mode. Main aim of this
 * test is, to get a prediction about compatibility of MB 3.1.0 and MB 3.0.0.
 * Before starting cluster test cases, need to start two clusters, one is without enabling compression and the other
 * one is with compression.
 * In this test case, publishing without compression. Then, sending message from the compression enabled server to
 * the subscriber, and checking the other way also.
 */
public class LZ4CompressCompatibilityTestCase extends MBPlatformBaseTest {

    /**
     * Input file size is 256KB
     */
    private static final int SIZE_TO_READ = 250 * 1024;
    /**
     * Message sent count
     */
    private static final int SEND_COUNT = 1;

    /**
     * Message expected count
     */
    private static final int EXPECTED_COUNT = SEND_COUNT;

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
     * Send messages from one node and received messages from another node. One node has enabled compression, and the
     * other node hasn't enabled compression.
     *
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws IOException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     */
    @Test(groups = "wso2.mb", description = "Single queue two node test compress compatibility test case")
    public void testSingleQueueMultiNodeCompressCompatibility()
            throws XPathExpressionException, AndesAdminServiceBrokerManagerAdminException, IOException,
            AndesClientConfigurationException, NamingException, JMSException, AndesClientException,
            DataAccessUtilException, InterruptedException {

        String inputContentAsString = AndesClientUtils.createRandomString(SIZE_TO_READ, 100);

        int printRate = 1;
        String queueName = "lz4CompressCompatibilityQueue";

        // Get 2 instances. One is a compression enabled node, and the other one is a node without enabling compression
        AutomationContext automationContextForMB2 = getAutomationContextWithKey("mb002");
        AutomationContext automationContextForMB3 = getAutomationContextWithKey("mb003");

        // Checking the 1st way

        // Creating a consumer using the 1st node
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                        ExchangeType.QUEUE, queueName);

        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / printRate);

        // Creating the publisher using the 2nd node
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(automationContextForMB3.getInstance().getHosts().get("default"),
                        Integer.parseInt(automationContextForMB3.getInstance().getPorts().get("amqp")),
                        ExchangeType.QUEUE, queueName);

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        // Message content will be constructed and will be published
        publisherConfig.setMessagesContentOfConfiguration(inputContentAsString);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Reading received message content
        List<String> receivedMessage = consumerClient.getReceivedMessages();

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertEquals(receivedMessage.get(0), inputContentAsString, "Message content has been modified");

        // Checking the 2nd way: opposite of the 1st way

        // Creating a consumer using the 2nd node
        consumerConfig =
                new AndesJMSConsumerClientConfiguration(automationContextForMB3.getInstance().getHosts().get("default"),
                        Integer.parseInt(automationContextForMB3.getInstance().getPorts().get("amqp")),
                        ExchangeType.QUEUE, queueName);

        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / printRate);

        // Creating the publisher using the 1st node
        publisherConfig =
                new AndesJMSPublisherClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                        ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        // Message content will be constructed and will be published
        publisherConfig.setMessagesContentOfConfiguration(inputContentAsString);

        consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        // Reading received message content
        receivedMessage = consumerClient.getReceivedMessages();

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertEquals(receivedMessage.get(0), inputContentAsString, "Message content has been modified");
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

        if (tempAndesAdminClient.getQueueByName("lz4CompressCompatibilityQueue") != null) {
            tempAndesAdminClient.deleteQueue("lz4CompressCompatibilityQueue");
        }
    }
}