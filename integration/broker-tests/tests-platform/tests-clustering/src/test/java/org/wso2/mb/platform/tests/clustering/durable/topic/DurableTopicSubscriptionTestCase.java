/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.mb.platform.tests.clustering.durable.topic;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
import org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient;
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

/**
 * Durable test cases with subscribers and publishers
 */
public class DurableTopicSubscriptionTestCase extends MBPlatformBaseTest {
    private String hostNode1;
    private String hostNode2;
    private int portInNode1;
    private int portInNode2;
    private TopicAdminClient topicAdminClient;

    /**
     * Prepare environment for tests.
     *
     * @throws XPathExpressionException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XMLStreamException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, LoginAuthenticationExceptionException, IOException,
            XMLStreamException, URISyntaxException, SAXException, AutomationUtilException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

        AutomationContext automationContext1 = getAutomationContextWithKey("mb002");
        AutomationContext automationContext2 = getAutomationContextWithKey("mb003");
        hostNode1 = automationContext1.getInstance().getHosts().get("default");
        hostNode2 = automationContext2.getInstance().getHosts().get("default");
        portInNode1 = Integer.parseInt(automationContext1.getInstance().getPorts().get("amqp"));
        portInNode2 = Integer.parseInt(automationContext2.getInstance().getPorts().get("amqp"));
        topicAdminClient = new TopicAdminClient(automationContext1.getContextUrls().getBackEndUrl(),
                super.login(automationContext1));

        super.initAndesAdminClients();
    }

    /**
     * Create with sub id= x topic=y to node 'mb002'. Publish from 'mb003'.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Reconnect to topic with same sub ID after " +
                                            "disconnecting", enabled = true)
    @Parameters({"messageCount"})
    public void subscribeDisconnectAndSubscribeAgainTest(long messageCount)
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
            AndesClientException {

        long sendCount = messageCount;
        long expectedCount = messageCount;

        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "durableTopicPublishing1");
        consumerConfig.setDurable(true, "durableTopicPublisherID1");

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "durableTopicPublishing1");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");
    }

    /**
     * Cleanup after running tests.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        //deleting the topics created
        topicAdminClient.removeTopic("durableTopicPublishing1");
    }
}
