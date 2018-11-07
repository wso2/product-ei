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
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSMessageType;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * This class includes test cases to test different types of messages (e.g. byte, map, object,
 * stream) which can be sent to a topic.
 */
public class DifferentMessageTypesTopicTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContext;
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

        automationContext = getAutomationContextWithKey("mb002");

        topicAdminClient = new TopicAdminClient(automationContext.getContextUrls().getBackEndUrl(),
                    super.login(automationContext));

    }

    /**
     * Publish byte messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @param messageCount  Number of message to send and receive
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber byte messages", enabled = true)
    @Parameters({"messageCount"})
    public void testByteMessageSingleSubSinglePubTopic(long messageCount)
            throws IOException, JMSException, AndesClientConfigurationException,
                   XPathExpressionException,
                   NamingException, AndesClientException {

        this.runMessageTypeTestCase(JMSMessageType.BYTE, "byteTopic1", messageCount);
    }

    /**
     * Publish map messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @param messageCount  Number of message to send and receive
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber map messages",
            enabled = true)
    @Parameters({"messageCount"})
    public void testMapMessageSingleSubSinglePubTopic(long messageCount)
            throws IOException, JMSException, AndesClientConfigurationException,
                   XPathExpressionException,
                   NamingException, AndesClientException {
        this.runMessageTypeTestCase(JMSMessageType.MAP, "mapTopic1", messageCount);
    }

    /**
     * Publish object messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @param messageCount  Number of message to send and receive
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber object messages",
            enabled = true)
    @Parameters({"messageCount"})
    public void testObjectMessageSingleSubSinglePubTopic(long messageCount)
            throws IOException, JMSException, AndesClientConfigurationException,
                   XPathExpressionException,
                   NamingException, AndesClientException {
        this.runMessageTypeTestCase(JMSMessageType.OBJECT, "objectTopic1", messageCount);
    }

    /**
     * Publish stream messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @param messageCount  Number of message to send and receive
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber stream messages",
            enabled = true)
    @Parameters({"messageCount"})
    public void testStreamMessageSingleSubSinglePubTopic(long messageCount)
            throws IOException, JMSException, AndesClientConfigurationException,
                   XPathExpressionException,
                   NamingException, AndesClientException {
        this.runMessageTypeTestCase(JMSMessageType.STREAM, "streamTopic1", messageCount);
    }

    /**
     * Publish stream messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    @AfterClass(alwaysRun = true)
    public void destroy()
            throws AndesEventAdminServiceEventAdminException, RemoteException {

        topicAdminClient.removeTopic("byteTopic1");
        topicAdminClient.removeTopic("mapTopic1");
        topicAdminClient.removeTopic("objectTopic1");
        topicAdminClient.removeTopic("streamTopic1");
    }

    /**
     * Runs a topic send and receive test case
     *
     * @param messageType     The message type to be used when publishing
     * @param destinationName The destination name for sender and receiver
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    private void runMessageTypeTestCase(JMSMessageType messageType, String destinationName, long messageCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException,
                   JMSException,
                   IOException, AndesClientException {

        // Number of expected messages
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(automationContext.getInstance().getHosts().get("default"),
                                                                                                     Integer.parseInt(automationContext.getInstance().getPorts().get("amqp")),
                                                                                                     ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContext.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContext.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig.setJMSMessageType(messageType);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");
    }
}
