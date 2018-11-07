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

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.andes.stub.admin.types.Queue;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
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
 * This test case contains test to check if messages goes to correct
 * tenants dead letter channel.
 */
public class TenantDeadLetterChannelTestCase extends MBIntegrationBaseTest {

    /**
     * The default andes acknowledgement wait timeout.
     */
    private String defaultAndesAckWaitTimeOut = null;

    /**
     * Name of tenant's dlc queue
     */
    private String tenantDlcQueueName = "dlctenant1.com/DeadLetterChannel";

    /**
     * Name of super tenant's dlc queue
     */
    private String superTenantDlcQueueName = "DeadLetterChannel";

    /**
     * Initializes the test case.
     *
     * @throws XPathExpressionException
     * @throws java.rmi.RemoteException
     * @throws org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, RemoteException,
                              UserAdminUserAdminException {
        super.init(TestUserMode.SUPER_TENANT_USER);

        // Get current "AndesAckWaitTimeOut" system property.
        defaultAndesAckWaitTimeOut = System.getProperty(AndesClientConstants.
                                                                ANDES_ACK_WAIT_TIMEOUT_PROPERTY);


        // Setting system property "AndesAckWaitTimeOut" for andes
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "0");

    }

    /**
     * Set default properties after test case.
     */
    @AfterClass()
    public void tearDown() {
        // Setting system property "AndesAckWaitTimeOut" to default value.
        if (StringUtils.isBlank(defaultAndesAckWaitTimeOut)) {
            System.clearProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY,
                               defaultAndesAckWaitTimeOut);
        }
    }


    /**
     * This test case will test functionality of tenant dead letter channel in a queue scenario.
     * 1. Publish 1 queue message to tenant.
     * 2. Add consumer for the queue message.
     * 3. Consumer do not acknowledge for the queue message.
     * 4. Message will put into tenant dlc after retry sending queue message 10 times.
     * 5. Number of messages in tenant dlc should be equal to 1.
     * 6. Number of messages in super tenant dlc should be equal to 0.
     *
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws LoginAuthenticationExceptionException
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws LogoutAuthenticationExceptionException
     * @throws XMLStreamException
     */
    @Test(groups = "wso2.mb", description = "Tenant dead letter channel test case for queues")
    public void performTenantDeadLetterChannelQueueTestCase()
            throws JMSException, IOException, NamingException, AndesClientConfigurationException,
            AndesClientException, LoginAuthenticationExceptionException,
            XPathExpressionException,
            AndesAdminServiceBrokerManagerAdminException, URISyntaxException, SAXException,
            LogoutAuthenticationExceptionException, XMLStreamException, AutomationUtilException {

        int sendMessageCount = 1;

        Queue tenantUserDlcQueue;

        Queue superAdminDlcQueue;

        String destinationName = "dlctenant1.com/tenantQueue";

        // Get the automation context for the dlctenant1
        AutomationContext tenantContext = new AutomationContext("MB", "mb001", "dlctenant1",
                                                                "dlctenantuser1");

        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(tenantContext);
        String sessionCookie = loginLogoutClient.login();
        AndesAdminClient andesClient =
                new AndesAdminClient(super.backendURL, sessionCookie);
        loginLogoutClient.logout();

        // purge if there are any dlc messages in dlctenant1 user
        andesClient.purgeQueue(tenantDlcQueueName);

        // Get the automation context for the superTenant
        AutomationContext superTenantContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY,
                                      FrameworkConstants.SUPER_TENANT_ADMIN);

        LoginLogoutClient loginLogoutSuperTenant = new LoginLogoutClient(superTenantContext);
        String SuperTenantSessionCookie = loginLogoutSuperTenant.login();
        AndesAdminClient andesAdminClient =
                new AndesAdminClient(super.backendURL, SuperTenantSessionCookie
                );
        loginLogoutSuperTenant.logout();

        // purge if there are any dlc messages in super tenant admin
        andesClient.purgeQueue(superTenantDlcQueueName);


        // Create a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "dlctenantuser1!dlctenant1.com",
                                                        "dlctenantuser1", ExchangeType.QUEUE,
                                                        destinationName);
        // Add manual client acknowledgement in configuration
        consumerConfig
                .setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        // Acknowledge a message only after 200 messages are received
        consumerConfig
                .setAcknowledgeAfterEachMessageCount(200L);
        consumerConfig.setPrintsPerMessageCount(sendMessageCount);
        consumerConfig.setAsync(false);

        // Create consumer client with given consumerConfig
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        // Start consumer client
        consumerClient.startClient();

        // Create a publisher client configuration
        AndesJMSPublisherClientConfiguration tenantPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "dlctenantuser1!dlctenant1.com",
                                                         "dlctenantuser1", ExchangeType.QUEUE,
                                                         destinationName);
        tenantPublisherConfig.setNumberOfMessagesToSend(sendMessageCount);
        tenantPublisherConfig.setPrintsPerMessageCount(sendMessageCount);

        // Create a publisher client with given configuration
        AndesClient tenantPublisherClient = new AndesClient(tenantPublisherConfig, true);
        // Start publisher client
        tenantPublisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient,
                                                    AndesClientConstants.DEFAULT_RUN_TIME);

        // Get tenant's dlc queue
        tenantUserDlcQueue = andesClient.getDlcQueue();

        // Get super tenant dlc queue
        superAdminDlcQueue = andesAdminClient.getDlcQueue();

        // Evaluating
        Assert.assertEquals(tenantUserDlcQueue.getMessageCount(), sendMessageCount,
                            "failure on tenant dlc queue path");
        Assert.assertEquals(superAdminDlcQueue.getMessageCount(), 0,
                            "failure on super tenant dlc queue path");
    }

    /**
     * This test case will test the functionality of messages being moved to tenant dead letter channel in a durable
     * topic subscription scenario.
     * 1. Add a durable subscription for a topic in tenant.
     * 1. Publish 1 message to the topic.
     * 3. Consumer do not acknowledge for the message.
     * 4. Message will put into tenant dlc after retry sending queue message 10 times.
     * 5. Number of messages in tenant dlc should be equal to 1.
     * 6. Number of messages in super tenant dlc should be equal to 0.
     *
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws LoginAuthenticationExceptionException
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws LogoutAuthenticationExceptionException
     * @throws XMLStreamException
     */
    @Test(groups = "wso2.mb", description = "Tenant dead letter channel test case for durable subscriptions")
    public void performTenantDeadLetterChannelDurableTopicSubscriptionTestCase()
            throws JMSException, IOException, NamingException, AndesClientConfigurationException,
            AndesClientException, LoginAuthenticationExceptionException,
            XPathExpressionException,
            AndesAdminServiceBrokerManagerAdminException, URISyntaxException, SAXException,
            LogoutAuthenticationExceptionException, XMLStreamException, AutomationUtilException {

        int sendMessageCount = 1;
        String topicName = "dlctenant1.com/tenantTopic";
        String subscriptionId = "dlctenant1.com/tenantSub";

        // Get the automation context for the dlctenant1
        AutomationContext tenantContext = new AutomationContext("MB", "mb001", "dlctenant1", "dlctenantuser1");

        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(tenantContext);
        String sessionCookie = loginLogoutClient.login();
        AndesAdminClient andesClient = new AndesAdminClient(super.backendURL, sessionCookie);
        loginLogoutClient.logout();

        // purge if there are any dlc messages in dlctenant1 user
        andesClient.purgeQueue(tenantDlcQueueName);

        // Get the automation context for the superTenant
        AutomationContext superTenantContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY,
                        FrameworkConstants.SUPER_TENANT_ADMIN);

        LoginLogoutClient loginLogoutSuperTenant = new LoginLogoutClient(superTenantContext);
        String SuperTenantSessionCookie = loginLogoutSuperTenant.login();
        AndesAdminClient andesAdminClient = new AndesAdminClient(super.backendURL, SuperTenantSessionCookie);
        loginLogoutSuperTenant.logout();

        // purge if there are any dlc messages in super tenant admin
        andesClient.purgeQueue(superTenantDlcQueueName);


        // Create a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), "dlctenantuser1!dlctenant1.com",
                        "dlctenantuser1", ExchangeType.TOPIC, topicName);
        // Add manual client acknowledgement in configuration
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig.setDurable(true, subscriptionId);
        consumerConfig.setSubscriptionID(subscriptionId);
        // Acknowledge a message only after 200 messages are received
        consumerConfig.setAcknowledgeAfterEachMessageCount(200L);
        consumerConfig.setPrintsPerMessageCount(sendMessageCount);
        consumerConfig.setAsync(false);

        // Create consumer client with given consumerConfig
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        // Start consumer client
        consumerClient.startClient();

        // Create a publisher client configuration
        AndesJMSPublisherClientConfiguration tenantPublisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), "dlctenantuser1!dlctenant1.com",
                        "dlctenantuser1", ExchangeType.TOPIC, topicName);
        tenantPublisherConfig.setNumberOfMessagesToSend(sendMessageCount);
        tenantPublisherConfig.setPrintsPerMessageCount(sendMessageCount);

        // Create a publisher client with given configuration
        AndesClient tenantPublisherClient = new AndesClient(tenantPublisherConfig, true);
        // Start publisher client
        tenantPublisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Get tenant's dlc queue
        Queue tenantUserDlcQueue = andesClient.getDlcQueue();

        // Get super tenant dlc queue
        Queue superAdminDlcQueue = andesAdminClient.getDlcQueue();

        // Evaluating
        Assert.assertEquals(tenantUserDlcQueue.getMessageCount(), sendMessageCount,
                "failure on tenant dlc durable topic subscription path");
        Assert.assertEquals(superAdminDlcQueue.getMessageCount(), 0,
                "failure on super tenant dlc durable topic subscription path");
    }

}
