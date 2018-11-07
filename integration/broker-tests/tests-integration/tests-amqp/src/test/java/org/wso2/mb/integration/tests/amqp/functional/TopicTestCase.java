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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.functional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
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
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * Test class for topics
 */
public class TopicTestCase extends MBIntegrationBaseTest {

    /**
     * Initializes test case
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Creates consumer that consumes messages from "singleTopic" topic.
     * 2. Publisher sends messages to topic "singleTopic".
     * 3. Consumer receives all sent messages.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single topic send-receive test case")
    public void performSingleTopicSendReceiveTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        long sendCount = 1000L;
        long expectedCount = 1000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "singleTopic");
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "singleTopic");
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient,
                                                            AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message send failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message " +
                                                                             "receiving failed.");
    }

    /**
     * 1. Create 3 consumers.
     *      - "commontopic" topic for admin user.
     *      - "commontopic" topic for tenant1user1
     *      - "commontopic" topic for tenant2user1
     * 2. Create 3 publishers.
     *      - "commontopic" topic for admin user.
     *      - "commontopic" topic for tenant1user1
     *      - "commontopic" topic for tenant2user1
     * 3. Each consumer will receive the sent count.
     *
     * @throws AndesClientConfigurationException
     * @throws CloneNotSupportedException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "")
    public void performMultipleTenantTopicSendReceiveTestCase()
            throws AndesClientConfigurationException, CloneNotSupportedException, JMSException,
                   NamingException,
                   IOException, AndesClientException, XPathExpressionException {
        long sendCount = 100L;
        long expectedCount = 100L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration adminConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "admin", "admin", ExchangeType.TOPIC,
                                                                                    "commontopic");
        adminConsumerConfig.setMaximumMessagesToReceived(expectedCount);
        adminConsumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        adminConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration tenant1ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "tenant1user1!testtenant1.com",
                                "tenant1user1", ExchangeType.TOPIC, "testtenant1.com/commontopic");
        tenant1ConsumerConfig.setMaximumMessagesToReceived(expectedCount);
        tenant1ConsumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        tenant1ConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration tenant2ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "tenant2user1!testtenant2.com",
                                "tenant2user1", ExchangeType.TOPIC, "testtenant2.com/commontopic");
        tenant2ConsumerConfig.setMaximumMessagesToReceived(expectedCount);
        tenant2ConsumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        tenant2ConsumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration adminPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "admin", "admin",
                                                         ExchangeType.TOPIC, "commontopic");
        adminPublisherConfig.setNumberOfMessagesToSend(sendCount);
        adminPublisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        AndesJMSPublisherClientConfiguration tenant1PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "tenant1user1!testtenant1.com",
                                 "tenant1user1", ExchangeType.TOPIC, "testtenant1.com/commontopic");
        tenant1PublisherConfig.setNumberOfMessagesToSend(sendCount);
        tenant1PublisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        AndesJMSPublisherClientConfiguration tenant2PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "tenant2user1!testtenant2.com",
                                 "tenant2user1", ExchangeType.TOPIC, "testtenant2.com/commontopic");
        tenant2PublisherConfig.setNumberOfMessagesToSend(sendCount);
        tenant2PublisherConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating clients
        AndesClient adminConsumerClient = new AndesClient(adminConsumerConfig, true);
        AndesClient tenant1ConsumerClient = new AndesClient(tenant1ConsumerConfig, true);
        AndesClient tenant2ConsumerClient = new AndesClient(tenant2ConsumerConfig, true);

        AndesClient adminPublisherClient = new AndesClient(adminPublisherConfig, true);
        AndesClient tenant1PublisherClient = new AndesClient(tenant1PublisherConfig, true);
        AndesClient tenant2PublisherClient = new AndesClient(tenant2PublisherConfig, true);

        adminConsumerClient.startClient();
        tenant1ConsumerClient.startClient();
        tenant2ConsumerClient.startClient();

        adminPublisherClient.startClient();
        tenant1PublisherClient.startClient();
        tenant2PublisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(adminConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.waitForMessagesAndShutdown(tenant1ConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.waitForMessagesAndShutdown(tenant2ConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(adminPublisherClient.getSentMessageCount(), sendCount, "Sending " +
                                                                               "failed for admin.");
        Assert.assertEquals(tenant1PublisherClient.getSentMessageCount(), sendCount, "Sending " +
                                                                         "  failed for tenant 1.");
        Assert.assertEquals(tenant2PublisherClient.getSentMessageCount(), sendCount, "Sending " +
                                                                         "  failed for tenant 2.");

        Assert.assertEquals(adminConsumerClient.getReceivedMessageCount(), expectedCount,
                                                            "Message receiving failed for admin.");
        Assert.assertEquals(tenant1ConsumerClient.getReceivedMessageCount(), expectedCount,
                                                        "Message receiving failed for tenant 1.");
        Assert.assertEquals(tenant2ConsumerClient.getReceivedMessageCount(), expectedCount,
                                                        "Message receiving failed for tenant 2.");
    }
}
