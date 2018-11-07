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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.event.stub.core.TopicRolePermission;
import org.wso2.carbon.andes.event.stub.service.AndesEventAdminServiceEventAdminException;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * Testing for multi tenant - Durable subscriber specific test case
 * <p/>
 * Test case 1
 * 1. Start a Durable subscriber from a normal tenant (Not super tenant)
 * (Topic - topictenant1.com/durableTenantTopic)
 * 2. Send 200 messages to the the topic
 * 3. Durable subscriber should receive all 200 messages
 * <p/>
 * Test case 2
 * 1. Start 2 Durable subscribers from different tenant for the same topic
 * 2. Start 2 publishers from different tenant for the same topic
 * 3. Durable subscribers should receive the message from their tenant only.
 */
public class MultiTenantDurableTopicTestCase extends MBIntegrationBaseTest {

    public static final String PUBLISHER_ROLE = "topicPublisher";

    private UserManagementClient userManagementClient;

    /**
     * Initializing test
     *
     * @throws XPathExpressionException
     * @throws RemoteException
     * @throws UserAdminUserAdminException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, RemoteException, UserAdminUserAdminException {
        super.init(TestUserMode.SUPER_TENANT_USER);

        // Logging into user management as admin and adding a new role to give permission for publishing/subscribe
        userManagementClient = new UserManagementClient(backendURL, "admin@topictenant1.com",
                "admin");

        String[] publishers = {"topictenantuser1"};
        userManagementClient.addRole(PUBLISHER_ROLE, publishers, new String[]{});
    }

    /**
     * Test case 1
     * 1. Start a Durable subscriber from a normal tenant (Not super tenant)
     * (Topic - topictenant1.com/durableTenantTopic)
     * 2. Send 200 messages to the the topic
     * 3. Durable subscriber should receive all 200 messages
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Single Tenant Test case")
    public void performSingleTenantMultipleUserDurableTopicTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, XPathExpressionException, URISyntaxException, SAXException,
            LoginAuthenticationExceptionException, XMLStreamException, LogoutAuthenticationExceptionException,
            AndesAdminServiceBrokerManagerAdminException, AndesEventAdminServiceEventAdminException, AutomationUtilException {

        String destinationName = "topictenant1.com/durableTenantTopic";

        int sendMessageCount = 200;
        int expectedMessageCount = 200;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration adminConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "admin!topictenant1.com", "admin",
                        ExchangeType.TOPIC, destinationName);
        adminConsumerConfig.setUnSubscribeAfterEachMessageCount(expectedMessageCount);
        adminConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        adminConsumerConfig.setDurable(true, "topictenant1.com/multitenant1");
        adminConsumerConfig.setAsync(false);

        // Creating clients
        AndesClient adminConsumerClient = new AndesClient(adminConsumerConfig, true);
        adminConsumerClient.startClient();


        // Add permission to the created role to be able to publish/subscribe
        TopicRolePermission topicRolePermission = new TopicRolePermission();
        topicRolePermission.setRoleName(PUBLISHER_ROLE);
        topicRolePermission.setAllowedToSubscribe(true);
        topicRolePermission.setAllowedToPublish(true);

        // Get the automation context for the tenant
        AutomationContext tenantContext = new AutomationContext("MB", "mb001", "topictenant1", "topictenantuser1");


        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(tenantContext);
        String sessionCookie = loginLogoutClient.login();

        TopicAdminClient topicAdminClient =
                new TopicAdminClient(super.backendURL, sessionCookie);

        // Update permissions for the destination queue to be able to publish/subscribe from topictenantuser1
        topicAdminClient.updatePermissionForTopic(destinationName, topicRolePermission);
        loginLogoutClient.logout();

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration tenantPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant1.com",
                     "topictenantuser1", ExchangeType.TOPIC, destinationName);
        tenantPublisherConfig.setNumberOfMessagesToSend(sendMessageCount);
        tenantPublisherConfig.setPrintsPerMessageCount(sendMessageCount / 10L);

        AndesClient tenantPublisherClient = new AndesClient(tenantPublisherConfig, true);
        tenantPublisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(adminConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(tenantPublisherClient
                                    .getSentMessageCount(), sendMessageCount, "Sending failed for " +
                                                              "topictenantuser1!topictenant1.com.");
        Assert.assertEquals(adminConsumerClient
                                    .getReceivedMessageCount(), expectedMessageCount, "Message " +
                                                  "receiving failed for admin!topictenant1.com.");
    }

    /**
     * Test case 2
     * 1. Start 2 Durable subscribers from different tenant for the same topic
     * 2. Start 2 publishers from different tenant for the same topic
     * 3. Durable subscribers should receive the message from their tenant only.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Multiple Tenant Single Users Test")
    public void performMultipleTenantDurableTopicTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        int sendMessageCount1 = 80;
        int sendMessageCount2 = 120;
        int expectedMessageCount = 200;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration tenant1ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant1.com",
                "topictenantuser1", ExchangeType.TOPIC, "topictenant1.com/multitenantTopicDurable");
        tenant1ConsumerConfig.setUnSubscribeAfterEachMessageCount(expectedMessageCount);
        tenant1ConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        tenant1ConsumerConfig.setDurable(true, "topictenant1.com/multi");
        tenant1ConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration tenant2ConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant2.com",
                "topictenantuser1", ExchangeType.TOPIC, "topictenant2.com/multitenantTopicDurable");
        tenant2ConsumerConfig.setUnSubscribeAfterEachMessageCount(expectedMessageCount);
        tenant2ConsumerConfig.setPrintsPerMessageCount(expectedMessageCount / 10L);
        tenant2ConsumerConfig.setDurable(true, "topictenant2.com/multi2");
        tenant2ConsumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration tenant1PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant1.com",
                 "topictenantuser1", ExchangeType.TOPIC, "topictenant1.com/multitenantTopicDurable");
        tenant1PublisherConfig.setNumberOfMessagesToSend(sendMessageCount1);
        tenant1PublisherConfig.setPrintsPerMessageCount(sendMessageCount1 / 10L);

        AndesJMSPublisherClientConfiguration tenant2PublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "topictenantuser1!topictenant2.com",
                "topictenantuser1", ExchangeType.TOPIC, "topictenant2.com/multitenantTopicDurable");
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
                                    .getSentMessageCount(), sendMessageCount2, "Sending failed " +
                                                                               "for tenant 2.");
        Assert.assertEquals(tenant1ConsumerClient
                                    .getReceivedMessageCount(), sendMessageCount1, "Tenant 1 " +
                                   "Durable subscriber received incorrect number of message count.");
        Assert.assertEquals(tenant2ConsumerClient
                                    .getReceivedMessageCount(), sendMessageCount2, "Tenant 2 " +
                                   "Durable subscriber received incorrect number of message count.");
    }

    /**
     * Cleans up the test case effects. Deletes created roles.
     *
     * @throws java.rmi.RemoteException
     * @throws UserAdminUserAdminException
     */
    @AfterClass(alwaysRun = true)
    public void cleanUp() throws RemoteException, UserAdminUserAdminException {
        // Deleting roles of the users used in the test case
        userManagementClient.deleteRole(PUBLISHER_ROLE);
    }
}
