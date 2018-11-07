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

package org.wso2.mb.platform.tests.clustering.durable.topic;

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
 * This class performs tests related to message delivery of durable topics
 */
public class DurableTopicMessageDeliveringTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContext;
    private TopicAdminClient topicAdminClient;
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

        automationContext = getAutomationContextWithKey("mb002");
        topicAdminClient = new TopicAdminClient(automationContext.getContextUrls().getBackEndUrl(),
                super.login(automationContext));

    }

    /**
     * Subscribe to a durable topic and publish messages to that topic
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    @Parameters({"messageCount"})
    public void pubSubDurableTopicTestCase(long messageCount)
            throws AndesClientConfigurationException, NamingException, JMSException, XPathExpressionException,
                   IOException, AndesClientException, DataAccessUtilException {

        long sendCount = messageCount;
        long expectedCount = messageCount;
        String destinationName = "durableTopicMessageDelivering";

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(automationContext.getInstance().getHosts()
                                                                .get("default"),
                                                        Integer.parseInt(automationContext
                                                                                 .getInstance()
                                                                                 .getPorts()
                                                                                 .get("amqp")),
                                                        ExchangeType.TOPIC, destinationName);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        consumerConfig.setDurable(true, "durableTopicSub5");    // durable topic
        consumerConfig
                .setUnSubscribeAfterEachMessageCount(expectedCount);   // Un-Subscribes messages at this message count

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(automationContext.getInstance().getHosts()
                                                                 .get("default"),
                                                         Integer.parseInt(automationContext
                                                                                  .getInstance()
                                                                                  .getPorts()
                                                                                  .get("amqp")),
                                                         ExchangeType.TOPIC, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating andes client
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
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
        topicAdminClient.removeTopic("durableTopicMessageDelivering");

    }
}
