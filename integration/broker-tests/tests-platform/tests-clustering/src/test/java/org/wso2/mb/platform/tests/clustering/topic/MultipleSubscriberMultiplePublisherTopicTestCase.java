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
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
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
 * This class includes tests with multiple subscribers for a topic
 */
public class MultipleSubscriberMultiplePublisherTopicTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContextForMB2;
    private AutomationContext automationContextForMB3;
    private TopicAdminClient topicAdminClient;

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

        topicAdminClient = new TopicAdminClient(automationContextForMB2.getContextUrls().getBackEndUrl(),
            super.login(automationContextForMB2));

    }

    /**
     * Publish message to a single topic in a single node by one publisher and subscribe to
     * that topic with two subscribers
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single node single publisher two subscribers test " +
                                            "case", enabled = true)
    public void testMultipleSubscribers()
            throws AndesClientConfigurationException, XPathExpressionException, NamingException,
                   JMSException,
                   IOException, AndesEventAdminServiceEventAdminException,
                   AndesClientException {
        long sendCount = 2000L;
        long expectedCount = 2000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration initialConsumerConfig = new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                            Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                            ExchangeType.TOPIC, "mulSubTopic1");
        initialConsumerConfig.setMaximumMessagesToReceived(expectedCount);
        initialConsumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        AndesJMSConsumerClientConfiguration secondaryConsumerConfig = new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                              Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                              ExchangeType.TOPIC, "mulSubTopic1");
        secondaryConsumerConfig.setMaximumMessagesToReceived(expectedCount);
        secondaryConsumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, "mulSubTopic1");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(initialConsumerConfig, true);
        initialConsumerClient.startClient();

        AndesClient secondaryConsumerClient = new AndesClient(secondaryConsumerConfig, true);
        secondaryConsumerClient.startClient();

        // Check if topic is created
        TopicNode topic = topicAdminClient.getTopicByName("mulSubTopic1");
        assertTrue(topic.getTopicName().equalsIgnoreCase("mulSubTopic1"), "Topic created in MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(initialConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(secondaryConsumerClient);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(initialConsumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed for client 1");
        Assert.assertEquals(secondaryConsumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed  for client 2");
    }

    /**
     * Publish message to a single topic in a single node by one publisher and subscribe to
     * that topic with many subscribers
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws XPathExpressionException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single node single publisher multiple subscribers " +
                                            "test case", enabled = true)
    public void testBulkSubscribers()
            throws AndesClientConfigurationException, NamingException, JMSException,
                   XPathExpressionException,
            AndesEventAdminServiceEventAdminException, IOException,
                   AndesClientException {
        long sendCount = 2000L;
        long expectedCount = 100000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                     Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                     ExchangeType.TOPIC, "mulSubTopic2");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, "mulSubTopic2");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, 50, true);
        consumerClient.startClient();

        // Check if topic is created
        TopicNode topic = topicAdminClient.getTopicByName("mulSubTopic2");
        assertTrue(topic.getTopicName()
                           .equalsIgnoreCase("mulSubTopic2"), "Topic created in MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed");
    }

    /**
     * Publish message to a single topic in a single node by multiple publishers and subscribe to
     * that topic with one subscribers
     *
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single node multiple publishers single subscriber " +
                                            "test case", enabled = true)
    public void testBulkPublishers()
            throws XPathExpressionException, AndesClientConfigurationException, NamingException,
                   JMSException,
            AndesEventAdminServiceEventAdminException, IOException,
                   AndesClientException {
        long sendCount = 100000L;
        long expectedCount = 100000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                     Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                     ExchangeType.TOPIC, "mulSubTopic3");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, "mulSubTopic3");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        // Check if topic is created
        TopicNode topic = topicAdminClient.getTopicByName("mulSubTopic2");
        assertTrue(topic.getTopicName().equalsIgnoreCase("mulSubTopic2"), "Topic created in MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, 50, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");
    }

    /**
     * Publish message to a single topic in a single node by multiple publishers and subscribe to
     * that topic with multiple subscribers
     *
     * @throws XPathExpressionException
     * @throws JMSException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single node multiple publishers multiple " +
                                            "subscribers test case", enabled = true)
    public void testBulkPublishersBulkSubscribers() throws XPathExpressionException, JMSException,
            AndesEventAdminServiceEventAdminException,
                                                           IOException,
                                                           AndesClientConfigurationException,
                                                           NamingException, AndesClientException {
        long sendCount = 2000L;
        long expectedCount = 100000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                     Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                     ExchangeType.TOPIC, "mulSubTopic4");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, "mulSubTopic4");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, 50, true);
        consumerClient.startClient();

        // Check if topic is created
        TopicNode topic = topicAdminClient.getTopicByName("mulSubTopic4");
        assertTrue(topic.getTopicName().equalsIgnoreCase("mulSubTopic4"), "Topic created in MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, 50, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");
    }

    /**
     * Publish message to a single topic in a single node by multiple publishers and subscribe to
     * that topic with multiple subscribers from another node
     *
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "multiple node multiple publishers multiple " +
                                            "subscribers test case", enabled = true)
    public void testBulkPublishersBulkSubscribersDifferentNodes()
            throws XPathExpressionException, AndesClientConfigurationException, NamingException,
                   JMSException,
            AndesEventAdminServiceEventAdminException, IOException,
                   AndesClientException {
        long sendCount = 2000L;
        long expectedCount = 100000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(automationContextForMB2.getInstance().getHosts().get("default"),
                                                                                                     Integer.parseInt(automationContextForMB2.getInstance().getPorts().get("amqp")),
                                                                                                     ExchangeType.TOPIC, "mulSubTopic5");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContextForMB3.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContextForMB3.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, "mulSubTopic5");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, 50, true);
        consumerClient.startClient();

        // Check if topic is created
        TopicNode topic = topicAdminClient.getTopicByName("mulSubTopic5");
        assertTrue(topic.getTopicName().equalsIgnoreCase("mulSubTopic5"), "Topic created in MB node 1 not exist");

        AndesClient publisherClient = new AndesClient(publisherConfig, 50, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");
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
        topicAdminClient.removeTopic("mulSubTopic1");
        topicAdminClient.removeTopic("mulSubTopic2");
        topicAdminClient.removeTopic("mulSubTopic3");
        topicAdminClient.removeTopic("mulSubTopic4");
        topicAdminClient.removeTopic("mulSubTopic5");
    }
}
