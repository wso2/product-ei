/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * Testing for multi tenant - Topic specific test case
 * <p/>
 * Test case 1
 * 1. Start a 3 subscribers in same tenant(Normal tenant) who listens to the same topic
 * 2. Send 200 messages to the topic
 * 3. All 3 subscribers should receive all 200 messages
 * <p/>
 * Test case 2
 * 1. Start 2 subscribers from different tenant for the same topic
 * 2. Start 2 publishers from different tenant for the same topic
 * 3. Subscribers should receive the message from their tenant only
 */
public class MultiTenantTopicTestCase extends MBIntegrationBaseTest {

    /**
     * Initializes test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Test case 1
     * 1. Admin under topictenant1.com domain creates consumer for "topictenant1.com/tenantTopic".
     * 2. topictenantuser1 user under topictenant1.com domain tries to use
     * "topictenant1.com/tenantTopic".
     * 3.topictenantuser1 user fails to use "topictenant1.com/tenantTopic" destination as no
     * permissions were given.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single Tenant with multiple Users Test",
            expectedExceptions = JMSException.class)
    public void performSingleTenantMultipleUserTopicTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        int sendMessageCount = 200;
        int expectedMessageCount = 200;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration adminConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "admin!topictenant1.com", "admin",
                                                ExchangeType.TOPIC, "topictenant1.com/tenantTopic");
        adminConsumerConfig.setMaximumMessagesToReceived(expectedMessageCount);
        adminConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        adminConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration tenant1ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "tenant1user2!topictenant1.com",
                            "tenant1user2", ExchangeType.TOPIC, "topictenant1.com/tenantTopic");
        tenant1ConsumerConfig.setMaximumMessagesToReceived(expectedMessageCount);
        tenant1ConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        tenant1ConsumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration tenant1PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "admin!topictenant1.com", "admin",
                                                 ExchangeType.TOPIC, "topictenant1.com/tenantTopic");
        tenant1PublisherConfig.setNumberOfMessagesToSend(sendMessageCount);
        tenant1PublisherConfig.setPrintsPerMessageCount(sendMessageCount / 10L);

        // Creating clients
        AndesClient adminConsumerClient = new AndesClient(adminConsumerConfig, true);
        adminConsumerClient.startClient();

        AndesClient tenant1ConsumerClient = new AndesClient(tenant1ConsumerConfig, true);
        tenant1ConsumerClient.startClient();

        AndesClient tenant2PublisherClient = new AndesClient(tenant1PublisherConfig, true);
        tenant2PublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(adminConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(tenant1ConsumerClient);

        // Evaluating
        Assert.assertEquals(tenant2PublisherClient
                                    .getSentMessageCount(), sendMessageCount, "Sending failed for" +
                                                          " tenant1user2!topictenant1.com.");
        Assert.assertEquals(adminConsumerClient
                                    .getReceivedMessageCount(), expectedMessageCount, "Message " +
                                                  "receiving failed for admin!topictenant1.com.");
        Assert.assertEquals(tenant1ConsumerClient
                                    .getReceivedMessageCount(), expectedMessageCount, "Message " +
                                          "receiving failed for tenant1user2!topictenant1.com.");

    }

    /**
     * Test case 2
     * 1. Start 2 subscribers from different tenant for the same topic
     * 2. Start 2 publishers from different tenant for the same topic
     * 3. Subscribers should receive the message from their tenant only
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Multiple Tenant Single Users Test")
    public void performMultipleTenantTopicTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        int sendMessageCount1 = 120;
        int sendMessageCount2 = 80;
        int expectedMessageCount = 200;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration tenant1ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant1.com",
                        "topictenantuser1", ExchangeType.TOPIC, "topictenant1.com/multitenantTopic");
        tenant1ConsumerConfig.setMaximumMessagesToReceived(expectedMessageCount);
        tenant1ConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        tenant1ConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration tenant2ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant2.com",
                        "topictenantuser1", ExchangeType.TOPIC, "topictenant2.com/multitenantTopic");
        tenant2ConsumerConfig.setMaximumMessagesToReceived(expectedMessageCount);
        tenant2ConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        tenant2ConsumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration tenant1PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant1.com",
                    "topictenantuser1", ExchangeType.TOPIC, "topictenant1.com/multitenantTopic");
        tenant1PublisherConfig.setNumberOfMessagesToSend(sendMessageCount1);
        tenant1PublisherConfig.setPrintsPerMessageCount(sendMessageCount1 / 10L);

        AndesJMSPublisherClientConfiguration tenant2PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant2.com",
                     "topictenantuser1", ExchangeType.TOPIC, "topictenant2.com/multitenantTopic");
        tenant2PublisherConfig.setNumberOfMessagesToSend(sendMessageCount2);
        tenant2PublisherConfig.setPrintsPerMessageCount(sendMessageCount2 / 10L);

        // Creating clients
        AndesClient tenant1ConsumerClient = new AndesClient(tenant1ConsumerConfig, true);
        tenant1ConsumerClient.startClient();

        AndesClient tenant2ConsumerClient = new AndesClient(tenant2ConsumerConfig, true);
        tenant2ConsumerClient.startClient();

        AndesClient tenant1PublisherClient = new AndesClient(tenant1PublisherConfig, true);
        tenant1PublisherClient.startClient();

        AndesClient tenant2PublisherClient = new AndesClient(tenant2PublisherConfig, true);
        tenant2PublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(tenant1ConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(tenant2ConsumerClient);

        // Evaluating
        Assert.assertEquals(tenant1PublisherClient
                                    .getSentMessageCount(), sendMessageCount1, "Sending failed " +
                                                                                    "for tenant 1.");
        Assert.assertEquals(tenant2PublisherClient
                                    .getSentMessageCount(), sendMessageCount2, "Sending failed" +
                                                                                    " for tenant 2.");
        Assert.assertEquals(tenant1ConsumerClient
                                    .getReceivedMessageCount(), sendMessageCount1, "Tenant 1 " +
                                               "client received incorrect number of message count.");
        Assert.assertEquals(tenant2ConsumerClient
                                    .getReceivedMessageCount(), sendMessageCount2, "Tenant 2 " +
                                               "client received incorrect number of message count.");
    }
}
