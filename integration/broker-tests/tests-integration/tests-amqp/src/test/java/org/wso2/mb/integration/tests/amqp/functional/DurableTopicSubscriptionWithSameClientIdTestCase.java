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

package org.wso2.mb.integration.tests.amqp.functional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;


/**
 * This class holds test case to verify if shared durable topic subscriptions. Shared durable topic
 * subscriptions has enabled in broker.xml and tested in following test class.
 */
public class DurableTopicSubscriptionWithSameClientIdTestCase extends MBIntegrationBaseTest {

    /**
     * Expected amount set to more than what is received as the amount of messages received by the
     * subscribers are unknown but the total should be the same amount as sent
     */
    private static final long EXPECTED_COUNT = 500L;
    private static final long SEND_COUNT_12 = 12L;
    private static final long SEND_COUNT_8 = 8L;

    /**
     * Prepare environment for durable topic subscription with same client Id tests
     *
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);

        super.serverManager = new ServerConfigurationManager(automationContext);

        // Replace the broker.xml with the allowSharedTopicSubscriptions configuration enabled under amqp
        // and restarts the server.
        super.serverManager.applyConfiguration(new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                "artifacts" + File.separator + "mb" + File.separator + "config" + File.separator +
                "allowSharedTopicSubscriptionsConfig" + File.separator + "broker.xml"),
                new File(ServerConfigurationManager.getCarbonHome() + File.separator + "repository" + File.separator +
                                                                "conf" + File.separator + "broker.xml"), true, true);

    }

    /**
     * Start 3 durable subscribers. Start publisher which sends 12 messages. Get the total count
     * received by all durable subscribers and compare with sent message count of the publisher.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performDurableTopicWithSameClientIdTestCase() throws Exception {

        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicSameClientID");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setDurable(true, "sameClientIDSub1");
        consumerConfig.setAsync(false);

        // Creating a JMS consumer client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicSameClientID");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT_12);

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig, true);
        consumerClient3.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);
        AndesClientUtils.shutdownClient(consumerClient3);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT_12, "Message sending failed.");
        long totalReceivingMessageCount =
                consumerClient1.getReceivedMessageCount() + consumerClient2
                        .getReceivedMessageCount() + consumerClient3.getReceivedMessageCount();
        Assert.assertEquals(totalReceivingMessageCount, SEND_COUNT_12, "Message receive count not equal to sent message count.");

    }

    /**
     * Start 6 durable subscribers with 3 for 2 topics each. Start publishers which sends 12 messages each. Get the total count
     * received by all durable subscribers for each topic and compare with sent message count of the publisher.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performDurableTopicMultiClientTestCase() throws Exception {

        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicSameClientIDTopic1");
        consumerConfig1.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig1.setDurable(true, "sameClientIDSub2");
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicSameClientIDTopic2");
        consumerConfig2.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig2.setDurable(true, "sameClientIDSub3");
        consumerConfig2.setAsync(false);

        // Creating a JMS consumer client configuration
        AndesJMSPublisherClientConfiguration publisherConfig1 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicSameClientIDTopic1");
        publisherConfig1.setNumberOfMessagesToSend(SEND_COUNT_12);

        AndesJMSPublisherClientConfiguration publisherConfig2 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableTopicSameClientIDTopic2");
        publisherConfig2.setNumberOfMessagesToSend(SEND_COUNT_8);

        // Creating clients
        // Publishers and Consumers for "durableTopicSameClientIDTopic1"
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig1, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig1, true);
        consumerClient3.startClient();

        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        publisherClient1.startClient();

        // Publishers and Consumers for "durableTopicSameClientIDTopic2"
        AndesClient consumerClient4 = new AndesClient(consumerConfig2, true);
        consumerClient4.startClient();

        AndesClient consumerClient5 = new AndesClient(consumerConfig2, true);
        consumerClient5.startClient();

        AndesClient consumerClient6 = new AndesClient(consumerConfig2, true);
        consumerClient6.startClient();

        AndesClient publisherClient2 = new AndesClient(publisherConfig2, true);
        publisherClient2.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClient2);
        AndesClientUtils.shutdownClient(consumerClient3);
        AndesClientUtils.shutdownClient(consumerClient4);
        AndesClientUtils.shutdownClient(consumerClient5);
        AndesClientUtils.shutdownClient(consumerClient6);

        // Evaluating
        Assert.assertEquals(publisherClient1
                .getSentMessageCount(), SEND_COUNT_12, "Message sending failed for 'durableTopicSameClientIDTopic1'");
        Assert.assertEquals(publisherClient2
                .getSentMessageCount(), SEND_COUNT_8, "Message sending failed for 'durableTopicSameClientIDTopic2'");

        long totalReceivingMessageCount =
                consumerClient1.getReceivedMessageCount() + consumerClient2
                        .getReceivedMessageCount() + consumerClient3.getReceivedMessageCount();
        Assert.assertEquals(totalReceivingMessageCount, SEND_COUNT_12, "Message receive count not equal to sent message " +
                                                                    "count for 'durableTopicSameClientIDTopic1'.");

        totalReceivingMessageCount =
                consumerClient4.getReceivedMessageCount() + consumerClient5
                        .getReceivedMessageCount() + consumerClient6.getReceivedMessageCount();
        Assert.assertEquals(totalReceivingMessageCount, SEND_COUNT_8, "Message receive count not equal to sent message " +
                                                                    "count for 'durableTopicSameClientIDTopic2'.");
    }

    /**
     * Restore to the previous configurations when the shared subscription test is complete.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass
    public void tearDown() throws IOException, AutomationUtilException {
        super.serverManager.restoreToLastConfiguration(true);
    }
}
