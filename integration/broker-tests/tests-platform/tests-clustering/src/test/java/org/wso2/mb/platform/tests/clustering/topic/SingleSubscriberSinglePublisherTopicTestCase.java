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
 * This class includes tests subscribers/publishers with different rates for topics
 */
public class SingleSubscriberSinglePublisherTopicTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContextForMB2;
    private AutomationContext automationContext2;
    private TopicAdminClient topicAdminClient1;
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
        automationContext2 = getAutomationContextWithKey("mb003");

        topicAdminClient1 =
                new TopicAdminClient(automationContextForMB2.getContextUrls().getBackEndUrl(),
                super.login(automationContextForMB2));

    }

    /**
     * Publish messages to a topic in a single node and receive from the same node.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Same node publisher subscriber test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testSameNodePubSub(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                automationContextForMB2, automationContextForMB2, 0L, 0L, "singleTopic1", messageCount, true);
    }

    /**
     * Publish messages to a topic in a node and receive from the same node at a slow rate.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Same node publisher, slow subscriber test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testSameNodeSlowSubscriber(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                        automationContextForMB2, automationContextForMB2, 10L, 0L, "singleTopic2", messageCount, true);
    }

    /**
     * Publish messages at a slow rate to a topic in one node and and receive from the
     * same node.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Same node slow publisher test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testSameNodeSlowPublisher(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                        automationContextForMB2, automationContextForMB2, 0L, 10L, "singleTopic3", messageCount, true);
    }

    /**
     * Publish messages to a topic in a single node at a slower rate and receive from a different
     * node at a slow rate.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Single node slow publisher slow subscriber test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testSingleNodeSlowPublisherSlowSubscriber(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                        automationContextForMB2, automationContextForMB2, 10L, 10L, "singleTopic8", messageCount, true);
    }

    /**
     * Publish messages to a topic in a single node and receive from a different node
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Different node publisher subscriber test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testDifferentNodePubSub(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                            automationContextForMB2, automationContext2, 0L, 0L, "singleTopic10", messageCount, false);
    }

    /**
     * Publish messages to a topic in a single node and receive from a different node at a slow
     * rate.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Different node slow subscriber test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testDifferentNodeSlowSubscriber(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                            automationContextForMB2, automationContext2, 10L, 0L, "singleTopic5", messageCount, false);
    }

    /**
     * Publish messages to a topic in a single node and receive from a different node at a slow
     * rate.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Different node slow publisher test case",
            enabled = true)
    @Parameters({"messageCount"})
    public void testDifferentNodeSlowPublisher(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                            automationContextForMB2, automationContext2, 0L, 10L, "singleTopic6", messageCount, false);
    }

    /**
     * Publish messages to a topic in a single node at a slower rate and receive from a different
     * node at a slow rate.
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = "wso2.mb", description = "Different node slow publisher slow subscriber test " +
                                            "case", enabled = true)
    @Parameters({"messageCount"})
    public void testDifferentNodeSlowPublisherSlowSubscriber(long messageCount)
            throws AndesEventAdminServiceEventAdminException, AndesClientConfigurationException,
                   XPathExpressionException, NamingException, JMSException, IOException, AndesClientException,
                   DataAccessUtilException {
        this.runSingleSubscriberSinglePublisherTopicTestCase(
                            automationContextForMB2, automationContext2, 10L, 10L, "singleTopic7", messageCount, false);
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

        topicAdminClient1.removeTopic("singleTopic1");
        topicAdminClient1.removeTopic("singleTopic2");
        topicAdminClient1.removeTopic("singleTopic3");
        topicAdminClient1.removeTopic("singleTopic5");
        topicAdminClient1.removeTopic("singleTopic6");
        topicAdminClient1.removeTopic("singleTopic7");
        topicAdminClient1.removeTopic("singleTopic8");
        topicAdminClient1.removeTopic("singleTopic10");
    }

    /**
     * Runs a consumer publisher test for 2 automation contexts and allowing to publish messages with
     * a delay.
     *
     * @param contextForConsumer  Automation context for consumer.
     * @param contextForPublisher Automation context for publisher.
     * @param consumerDelay       Message reading delay for consumer.
     * @param publisherDelay      Message publishing delay for publisher.
     * @param destinationName     Destination for publisher and consumer.
     * @param isSameNode
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws AndesClientException
     */
    private void runSingleSubscriberSinglePublisherTopicTestCase(
            AutomationContext contextForConsumer,
            AutomationContext contextForPublisher, long consumerDelay,
            long publisherDelay, String destinationName, long messageCount, boolean isSameNode)
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   XPathExpressionException, AndesEventAdminServiceEventAdminException, AndesClientException,
                   DataAccessUtilException {
        // Number of messages expected
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;
        long printDivider = 10L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(contextForConsumer.getInstance().getHosts()
                                                                .get("default"),
                                                        Integer.parseInt(contextForConsumer
                                                                                 .getInstance()
                                                                                 .getPorts()
                                                                                 .get("amqp")),
                                                        ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount * 2);
        consumerConfig.setPrintsPerMessageCount(expectedCount / printDivider);
        consumerConfig.setRunningDelay(consumerDelay);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        if (!isSameNode) {
            AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);
        }

        // Check if topic is created
        TopicNode topic = topicAdminClient1.getTopicByName(destinationName);
        assertTrue(topic.getTopicName()
                           .equalsIgnoreCase(destinationName), "Topic created in MB node 1 not exist");

        // Creating a publisher config
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(contextForPublisher.getInstance()
                                                                 .getHosts().get("default"),
                                                         Integer.parseInt(contextForPublisher
                                                                                  .getInstance()
                                                                                  .getPorts()
                                                                                  .get("amqp")),
                                                         ExchangeType.TOPIC, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / printDivider);
        publisherConfig.setRunningDelay(publisherDelay);


        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");

        // Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(destinationName), 0, "Messages left in database");
        // Evaluate slots left in database
        Assert.assertEquals(dataAccessUtil.getAssignedSlotCountForQueue(destinationName), 0, "Slots left in database");
    }
}
