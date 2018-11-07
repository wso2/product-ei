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

package org.wso2.mb.platform.tests.clustering.topic;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.event.stub.core.TopicNode;
import org.wso2.carbon.andes.event.stub.service.AndesEventAdminServiceEventAdminException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient;
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

import static org.testng.Assert.assertTrue;

/**
 * Test class to test topics in clusters
 */
public class TopicClusterTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContextForMB2;
    private AutomationContext automationContextForMB3;
    private TopicAdminClient topicAdminClientForMB2;
    private TopicAdminClient topicAdminClientForMB3;
    private DataAccessUtil dataAccessUtil = new DataAccessUtil();

    /**
     * Prepare environment for tests.
     *
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws XPathExpressionException, URISyntaxException, SAXException, XMLStreamException,
            LoginAuthenticationExceptionException, IOException, AutomationUtilException {

        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

        automationContextForMB2 = getAutomationContextWithKey("mb002");
        automationContextForMB3 = getAutomationContextWithKey("mb003");

        topicAdminClientForMB2 = new TopicAdminClient(automationContextForMB2.getContextUrls().getBackEndUrl(),
          super.login(automationContextForMB2));

        topicAdminClientForMB3 = new TopicAdminClient(automationContextForMB3.getContextUrls().getBackEndUrl(),
          super.login(automationContextForMB3));
    }

    /**
     * Send and receive messages in a single node for a topic
     *
     * @param messageCount  Number of message to send and receive
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XPathExpressionException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single topic Single node send-receive test case")
    @Parameters({"messageCount"})
    public void testSingleTopicSingleNodeSendReceive(long messageCount)
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesEventAdminServiceEventAdminException, XPathExpressionException, AndesClientException,
                   InterruptedException, DataAccessUtilException {
        long sendCount = messageCount;
        long expectedCount = messageCount;
        long printDivider = 10L;
        String destinationName = "clusterSingleTopic1";

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                        ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                        ExchangeType.TOPIC, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        TopicNode topic = topicAdminClientForMB2.getTopicByName(destinationName);
        assertTrue(topic.getTopicName().equalsIgnoreCase(destinationName), "Topic created in" +
                                                                             " MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate message left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(destinationName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(destinationName), 0, "Slots left in database");


    }

    /**
     * Checking for topic deletion and adding cluster wide.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    @Test(groups = "wso2.mb", description = "Single topic replication")
    public void testSingleTopicReplication()
            throws AndesEventAdminServiceEventAdminException, RemoteException {

        String topic = "singleTopic2";

        topicAdminClientForMB2.addTopic(topic);
        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);
        TopicNode topicNode = topicAdminClientForMB3.getTopicByName(topic);

        assertTrue(topicNode != null && topicNode.getTopicName().equalsIgnoreCase(topic),
                   "Topic created in MB node 1 not replicated in MB node 2");

        topicAdminClientForMB3.removeTopic(topic);
        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);
        topicNode = topicAdminClientForMB3.getTopicByName(topic);

        assertTrue(topicNode == null,
                   "Topic deleted in MB node 2 not deleted in MB node 1");

    }

    /**
     * Send messages from one node and received messages from another node.
     *
     * @param messageCount  Number of message to send and receive
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XPathExpressionException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single topic Multi node send-receive test case")
    @Parameters({"messageCount"})
    public void testSingleTopicMultiNodeSendReceive(long messageCount)
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesEventAdminServiceEventAdminException, XPathExpressionException, AndesClientException,
                   InterruptedException, DataAccessUtilException {
        long sendCount = messageCount;
        long expectedCount = messageCount;
        long printDivider = 10L;
        String destinationName = "clusterSingleTopic3";

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                     Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                     ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);


        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(automationContextForMB3.getInstance().getHosts().get("default"),
                    Integer.parseInt(automationContextForMB3.getInstance().getPorts().get("amqp")),
                    ExchangeType.TOPIC, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        TopicNode topic = topicAdminClientForMB2.getTopicByName(destinationName);
        assertTrue(topic.getTopicName().equalsIgnoreCase(destinationName), "Topic created in MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        // Wait until consumers are closed
        Thread.sleep(AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate message left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(destinationName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(destinationName), 0, "Slots left in database");
    }

    /**
     * Cleanup after running tests.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    @AfterClass(alwaysRun = true)
    public void destroy()
            throws AndesEventAdminServiceEventAdminException, RemoteException {

        topicAdminClientForMB2.removeTopic("clusterSingleTopic1");
        topicAdminClientForMB2.removeTopic("clusterSingleTopic2");
        topicAdminClientForMB2.removeTopic("clusterSingleTopic3");
    }
}
