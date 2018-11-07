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
 * This class includes test cases with multiple topics.
 */
public class MultipleTopicTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContext;
    private TopicAdminClient topicAdminClient;

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

        automationContext = getAutomationContextWithKey("mb002");

        topicAdminClient = new TopicAdminClient(automationContext.getContextUrls().getBackEndUrl(),
                super.login(automationContext));

    }

    /**
     * Publish messages to a topic in a single node and receive from the same node
     *
     * @param messageCount  Number of message to send and receive
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Same node publisher subscriber test case")
    @Parameters({"messageCount"})
    public void testMultipleTopicSingleNode(long messageCount)
            throws JMSException, AndesClientConfigurationException, XPathExpressionException,
                   NamingException,
                   IOException, AndesClientException {

        // Number of expected messages
        long expectedCount = messageCount;
        // Number of messages send
        long sendCount = messageCount;

        // Creating receiver clients
        AndesClient receivingClient1 = getAndesReceiverClient("topic1", expectedCount);
        AndesClient receivingClient2 = getAndesReceiverClient("topic2", expectedCount);
        AndesClient receivingClient3 = getAndesReceiverClient("topic3", expectedCount);
        AndesClient receivingClient4 = getAndesReceiverClient("topic4", expectedCount);
        AndesClient receivingClient5 = getAndesReceiverClient("topic5", expectedCount);
        AndesClient receivingClient6 = getAndesReceiverClient("topic6", expectedCount);
        AndesClient receivingClient7 = getAndesReceiverClient("topic7", expectedCount);
        AndesClient receivingClient8 = getAndesReceiverClient("topic8", expectedCount);
        AndesClient receivingClient9 = getAndesReceiverClient("topic9", expectedCount);
        AndesClient receivingClient10 = getAndesReceiverClient("topic10", expectedCount);

        // Starting up receiver clients
        receivingClient1.startClient();
        receivingClient2.startClient();
        receivingClient3.startClient();
        receivingClient4.startClient();
        receivingClient5.startClient();
        receivingClient6.startClient();
        receivingClient7.startClient();
        receivingClient8.startClient();
        receivingClient9.startClient();
        receivingClient10.startClient();

        // Creating publisher clients
        AndesClient sendingClient1 = getAndesSenderClient("topic1", sendCount);
        AndesClient sendingClient2 = getAndesSenderClient("topic2", sendCount);
        AndesClient sendingClient3 = getAndesSenderClient("topic3", sendCount);
        AndesClient sendingClient4 = getAndesSenderClient("topic4", sendCount);
        AndesClient sendingClient5 = getAndesSenderClient("topic5", sendCount);
        AndesClient sendingClient6 = getAndesSenderClient("topic6", sendCount);
        AndesClient sendingClient7 = getAndesSenderClient("topic7", sendCount);
        AndesClient sendingClient8 = getAndesSenderClient("topic8", sendCount);
        AndesClient sendingClient9 = getAndesSenderClient("topic9", sendCount);
        AndesClient sendingClient10 = getAndesSenderClient("topic10", sendCount);

        // Starting up publisher clients
        sendingClient1.startClient();
        sendingClient2.startClient();
        sendingClient3.startClient();
        sendingClient4.startClient();
        sendingClient5.startClient();
        sendingClient6.startClient();
        sendingClient7.startClient();
        sendingClient8.startClient();
        sendingClient9.startClient();
        sendingClient10.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(receivingClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(receivingClient2);
        AndesClientUtils.shutdownClient(receivingClient3);
        AndesClientUtils.shutdownClient(receivingClient4);
        AndesClientUtils.shutdownClient(receivingClient5);
        AndesClientUtils.shutdownClient(receivingClient6);
        AndesClientUtils.shutdownClient(receivingClient7);
        AndesClientUtils.shutdownClient(receivingClient8);
        AndesClientUtils.shutdownClient(receivingClient9);
        AndesClientUtils.shutdownClient(receivingClient10);

        // Evaluating
        Assert.assertEquals(sendingClient1
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 1");
        Assert.assertEquals(sendingClient2
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 2");
        Assert.assertEquals(sendingClient3
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 3");
        Assert.assertEquals(sendingClient4
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 4");
        Assert.assertEquals(sendingClient5
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 5");
        Assert.assertEquals(sendingClient6
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 6");
        Assert.assertEquals(sendingClient7
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 7");
        Assert.assertEquals(sendingClient8
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 8");
        Assert.assertEquals(sendingClient9
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 9");
        Assert.assertEquals(sendingClient10
                                    .getSentMessageCount(), sendCount, "Messaging sending failed in sender 10");

        Assert.assertEquals(receivingClient1
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 1");
        Assert.assertEquals(receivingClient2
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 2");
        Assert.assertEquals(receivingClient3
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 3");
        Assert.assertEquals(receivingClient4
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 4");
        Assert.assertEquals(receivingClient5
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 5");
        Assert.assertEquals(receivingClient6
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 6");
        Assert.assertEquals(receivingClient7
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 7");
        Assert.assertEquals(receivingClient8
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 8");
        Assert.assertEquals(receivingClient9
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 9");
        Assert.assertEquals(receivingClient10
                                    .getReceivedMessageCount(), expectedCount, "Did not receive all the messages in receiving client 10");

    }

    /**
     * Gets an AndesClient to subscriber for a given topic
     *
     * @param topicName     Name of the topic which the subscriber subscribes
     * @param expectedCount Expected message count to be received
     * @return AndesClient object to receive messages
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AndesClientException
     */
    private AndesClient getAndesReceiverClient(String topicName, long expectedCount)
            throws NamingException, JMSException, AndesClientConfigurationException,
                   XPathExpressionException, IOException, AndesClientException {

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(automationContext.getInstance().getHosts()
                                                                .get("default"),
                                                        Integer.parseInt(automationContext
                                                                                 .getInstance()
                                                                                 .getPorts()
                                                                                 .get("amqp")),
                                                        ExchangeType.TOPIC, topicName);
        // Amount of message to receive
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        return new AndesClient(consumerConfig, true);
    }

    /**
     * Gets an AndesClient to send messages to a given topic
     *
     * @param topicName Name of the topic
     * @param sendCount Message count to be sent
     * @return An andes client
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    private AndesClient getAndesSenderClient(String topicName, long sendCount)
            throws XPathExpressionException, AndesClientConfigurationException, NamingException,
                   JMSException, IOException, AndesClientException {
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(automationContext.getInstance().getHosts()
                                                                 .get("default"),
                                                         Integer.parseInt(automationContext
                                                                                  .getInstance()
                                                                                  .getPorts()
                                                                                  .get("amqp")),
                                                         ExchangeType.TOPIC, topicName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        return new AndesClient(publisherConfig, true);
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

        topicAdminClient.removeTopic("topic1");
        topicAdminClient.removeTopic("topic2");
        topicAdminClient.removeTopic("topic3");
        topicAdminClient.removeTopic("topic4");
        topicAdminClient.removeTopic("topic5");
        topicAdminClient.removeTopic("topic6");
        topicAdminClient.removeTopic("topic7");
        topicAdminClient.removeTopic("topic8");
        topicAdminClient.removeTopic("topic9");
        topicAdminClient.removeTopic("topic10");

    }

}
