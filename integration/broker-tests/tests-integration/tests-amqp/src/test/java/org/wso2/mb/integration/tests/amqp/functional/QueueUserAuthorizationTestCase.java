/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.functional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.andes.stub.admin.types.QueueRolePermission;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.carbon.user.mgt.stub.types.carbon.FlaggedName;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
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
 * This class contains the test cases related to user authorization and queues
 */
public class QueueUserAuthorizationTestCase extends MBIntegrationBaseTest {
    /**
     * The logger used to log information, warnings, errors, etc.
     */
    private static final Logger log = LoggerFactory.getLogger(QueueUserAuthorizationTestCase.class);

    /**
     * Permission path for creating a queue
     */
    private static final String ADD_QUEUE_PERMISSION = "/permission/admin/manage/queue/add";

    /**
     * Roles for the test case scenarios
     */
    private static final String CREATE_PUB_SUB_QUEUE_ROLE = "create_pub_sub_queue_role";
    private static final String PUB_SUB_QUEUE_ROLE = "pub_sub_queue_role";
    private static final String NO_PERMISSION_QUEUE_ROLE = "no_permission_queue_role";

    /**
     * Prefix for internal roles for topics
     */
    private static final String QUEUE_PREFIX = "Q_";

    private UserManagementClient userManagementClient;

    /**
     * Initializes before a test method. Removes users of admin group if exists. Adds new roles
     * with permissions.
     *
     * @throws Exception
     */
    @BeforeMethod(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);

        String[] createPubSubUsers = new String[]{"authUser1", "authUser2"};
        String[] pubSubUsers = new String[]{"authUser3", "authUser4"};
        String[] noPermissionUsers = new String[]{"authUser5"};
        String[] allUsers =
                new String[]{"authUser1", "authUser2", "authUser3", "authUser4", "authUser5"};

        // Logging into user management as admin
        userManagementClient = new UserManagementClient(backendURL, "admin", "admin");

        // Removing admin permission for all users
        userManagementClient.updateUserListOfRole(FrameworkConstants.ADMIN_ROLE, null, allUsers);

        // Adding roles along with users
        userManagementClient
                .addRole(CREATE_PUB_SUB_QUEUE_ROLE, createPubSubUsers, new String[]{ADD_QUEUE_PERMISSION});
        userManagementClient.addRole(PUB_SUB_QUEUE_ROLE, pubSubUsers, new String[]{});
        userManagementClient.addRole(NO_PERMISSION_QUEUE_ROLE, noPermissionUsers, new String[]{});
    }

    /**
     * Cleans up the test case effects. Created roles and internal queue related roles are created.
     *
     * @throws java.rmi.RemoteException
     * @throws UserAdminUserAdminException
     */
    @AfterMethod(alwaysRun = true)
    public void cleanUpAfterScenario() throws RemoteException, UserAdminUserAdminException {
        // Deleting roles of the users used in the test case
        userManagementClient.deleteRole(CREATE_PUB_SUB_QUEUE_ROLE);
        userManagementClient.deleteRole(PUB_SUB_QUEUE_ROLE);
        userManagementClient.deleteRole(NO_PERMISSION_QUEUE_ROLE);

        // Deleting internal roles specific to queues
        FlaggedName[] allRoles = userManagementClient.getAllRolesNames("*", 10);
        for (FlaggedName allRole : allRoles) {
            if (QUEUE_PREFIX.contains(allRole.getItemName())) {
                userManagementClient.deleteRole(allRole.getItemName());
            }
        }
    }

    /**
     * User creates a queue and then publishes and consumes messages.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueuePermissionTestCase()
            throws AndesClientConfigurationException, NamingException, IOException,
                   XPathExpressionException, AndesClientException, JMSException {
        this.createPublishAndSubscribeFromUser("authUser1", "authQueue1");
    }

    /**
     * User1 and User2 exists in the same role where create queue permission is assigned.
     * User1 creates a queue and then publishes and consumes messages.
     * User2 tries to publish and consume messages. But unable to succeed.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     */
    @Test(groups = {"wso2.mb", "queue"}, expectedExceptions = JMSException.class, expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performQueuePermissionSameRoleUsersWithNoPublishOrConsume()
            throws AndesClientConfigurationException, NamingException, IOException,
                   XPathExpressionException, AndesClientException, JMSException {
        this.createPublishAndSubscribeFromUser("authUser1", "authQueue2");
        this.createPublishAndSubscribeFromUser("authUser2", "authQueue2");
    }

    /**
     * User1 and User2 exists in the same role where create queue permission is assigned.
     * User1 creates a queue and then publishes and consumes messages.
     * Add publish and consume permissions to the role in which User1 exists.
     * User2 tries to publish and consume messages. User2 succeeds.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     * @throws UserAdminUserAdminException
     * @throws LoginAuthenticationExceptionException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueuePermissionSameRoleUsersWithPublishOrConsume()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            XMLStreamException, LogoutAuthenticationExceptionException, URISyntaxException,
            SAXException, AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        this.createPublishAndSubscribeFromUser("authUser1", "authQueue3");

        // Adding publish subscribe permissions of 'authQueue3' to 'create_pub_sub_queue_role' role.
        QueueRolePermission queueRolePermission = new QueueRolePermission();
        queueRolePermission.setRoleName(CREATE_PUB_SUB_QUEUE_ROLE);
        queueRolePermission.setAllowedToConsume(true);
        queueRolePermission.setAllowedToPublish(true);
        this.updateQueueRoleConsumePublishPermission("authQueue3", queueRolePermission);
        log.info("Consume and publish permissions updated for " + CREATE_PUB_SUB_QUEUE_ROLE);

        this.createPublishAndSubscribeFromUser("authUser2", "authQueue3");
    }

    /**
     * User1 and User2 exists in the same role where create queue permission is assigned.
     * Admin(UI) creates a queue and then publishes and consumes messages.
     * Add publish and consume permissions to the role in which User1 and User2 exists.
     * User1 and User2 tries to publish and consume messages. User2 succeeds.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     * @throws UserAdminUserAdminException
     * @throws LoginAuthenticationExceptionException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueuePermissionSameRoleUsersWithAdminCreated()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            XMLStreamException, LogoutAuthenticationExceptionException, URISyntaxException,
            SAXException, AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        // "superAdmin" refers to the admin
        this.createPublishAndSubscribeFromUser("superAdmin", "authQueue8");

        // Adding publish subscribe permissions of 'authQueue8' to 'create_pub_sub_queue_role' role.
        QueueRolePermission queueRolePermission = new QueueRolePermission();
        queueRolePermission.setRoleName(CREATE_PUB_SUB_QUEUE_ROLE);
        queueRolePermission.setAllowedToConsume(true);
        queueRolePermission.setAllowedToPublish(true);
        this.updateQueueRoleConsumePublishPermission("authQueue8", queueRolePermission);
        log.info("Consumer and publish permissions updated for " + CREATE_PUB_SUB_QUEUE_ROLE);

        this.createPublishAndSubscribeFromUser("authUser1", "authQueue8");
        this.createPublishAndSubscribeFromUser("authUser2", "authQueue8");
    }

    /**
     * User1 is in Role1 where there is queue creating permissions.
     * User5 is in Role2 where there are no create queue permissions.
     * User1 creates a queue and then publishes and consumes messages.
     * User5 tries to publish and consume messages. User5 fails.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "queue"}, expectedExceptions = JMSException.class, expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performQueuePermissionDifferentRoleUsersWithNoPermissions()
            throws JMSException, NamingException, AndesClientConfigurationException,
                   AndesClientException, XPathExpressionException, IOException {
        this.createPublishAndSubscribeFromUser("authUser1", "authQueue4");
        this.createPublishAndSubscribeFromUser("authUser5", "authQueue4");
    }

    /**
     * User1 exists in a role where create queue permission is assigned.
     * User1 creates a queue and then publishes and consumes messages.
     * User1 is removed from the role.
     * User1 tries to publish and consume messages. User1 fails.
     *
     * @throws RemoteException
     * @throws UserAdminUserAdminException
     */
    @Test(groups = {"wso2.mb", "queue"}, expectedExceptions = JMSException.class, expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performQueuePermissionSameUserRemovedFromRole()
            throws IOException, UserAdminUserAdminException, JMSException, NamingException,
                   AndesClientConfigurationException, AndesClientException,
                   XPathExpressionException {
        this.createPublishAndSubscribeFromUser("authUser1", "authQueue5");

        // Removing authUser1 from create_pub_sub_queue_role and Internal/Q_authQueue5
        userManagementClient
                .addRemoveRolesOfUser("authUser1", new String[]{NO_PERMISSION_QUEUE_ROLE},
                                      new String[]{CREATE_PUB_SUB_QUEUE_ROLE, "Internal/Q_authqueue5"});
        log.info("Removing authUser1 from " + CREATE_PUB_SUB_QUEUE_ROLE + " and Internal/Q_authqueue5");

        this.createPublishAndSubscribeFromUser("authUser1", "authQueue5");
    }

    /**
     * User1 and User2 exists in the same role where create queue permission is assigned.
     * User1 creates a queue and then publishes and consumes messages.
     * Admin assigns publishing and consuming  permissions to the role in which User1 and User2 are
     * in.
     * User1 is removed from the role.
     * User2 tries to publish and consume messages. User2 succeeds.
     *
     * @throws IOException
     * @throws LoginAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws LogoutAuthenticationExceptionException
     * @throws XMLStreamException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws UserAdminUserAdminException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueuePermissionSameRoleAssignedPermissions()
            throws IOException, LoginAuthenticationExceptionException, URISyntaxException,
            LogoutAuthenticationExceptionException, XMLStreamException,
            AndesAdminServiceBrokerManagerAdminException, SAXException,
            XPathExpressionException, UserAdminUserAdminException, JMSException,
            AndesClientConfigurationException, AndesClientException, NamingException, AutomationUtilException {
        this.createPublishAndSubscribeFromUser("authUser1", "authQueue6");

        // Adding publish subscribe permissions of 'authQueue6' to 'create_pub_sub_queue_role' role.
        QueueRolePermission queueRolePermission = new QueueRolePermission();
        queueRolePermission.setRoleName(CREATE_PUB_SUB_QUEUE_ROLE);
        queueRolePermission.setAllowedToConsume(true);
        queueRolePermission.setAllowedToPublish(true);
        this.updateQueueRoleConsumePublishPermission("authQueue6", queueRolePermission);
        log.info("Consumer and publish permissions updated for " + CREATE_PUB_SUB_QUEUE_ROLE);

        // Removing authUser1 from create_pub_sub_queue_role and Internal/Q_authQueue6
        userManagementClient
                .addRemoveRolesOfUser("authUser1", new String[]{NO_PERMISSION_QUEUE_ROLE},
                                      new String[]{CREATE_PUB_SUB_QUEUE_ROLE, "Internal/Q_authqueue6"});
        log.info("Removing authUser1 from " + CREATE_PUB_SUB_QUEUE_ROLE + " and Internal/Q_authqueue6");

        this.createPublishAndSubscribeFromUser("authUser2", "authQueue6");
    }

    /**
     * User3 is in Role2 where there are no create queue permissions.
     * Admin creates a queue and then publishes and consumes messages.
     * Admin assigns publishing and consuming permissions to Role2.
     * User3 tries to publish and consume messages. User3 succeeds.
     *
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws UserAdminUserAdminException
     * @throws LoginAuthenticationExceptionException
     * @throws LogoutAuthenticationExceptionException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueuePermissionDifferentRolesAssignedPermissions()
            throws IOException, XPathExpressionException,
            AndesAdminServiceBrokerManagerAdminException, URISyntaxException, SAXException,
            XMLStreamException, UserAdminUserAdminException,
            LoginAuthenticationExceptionException, LogoutAuthenticationExceptionException,
            JMSException, AndesClientConfigurationException, AndesClientException,
            NamingException, AutomationUtilException {
        // "superAdmin" refers to the admin
        this.createPublishAndSubscribeFromUser("superAdmin", "authQueue7");

        // Adding publish subscribe permissions of 'authQueue7' to 'pub_sub_queue_role' role.
        QueueRolePermission queueRolePermission = new QueueRolePermission();
        queueRolePermission.setRoleName(PUB_SUB_QUEUE_ROLE);
        queueRolePermission.setAllowedToConsume(true);
        queueRolePermission.setAllowedToPublish(true);
        this.updateQueueRoleConsumePublishPermission("authQueue7", queueRolePermission);
        log.info("Consumer and publish permissions updated for " + PUB_SUB_QUEUE_ROLE);

        this.createPublishAndSubscribeFromUser("authUser3", "authQueue7");
    }

    /**
     * User1 is in Role1 where there are create queue permissions.
     * User3 is in Role2 where there are no create queue permissions.
     * Admin creates a queue and then publishes and consumes messages.
     * Admin assigns publishing and consuming permissions to Role2.
     * User1 tries to publish and consume messages. User1 fails.
     *
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws UserAdminUserAdminException
     * @throws LoginAuthenticationExceptionException
     * @throws LogoutAuthenticationExceptionException
     * @throws JMSException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"}, expectedExceptions = JMSException.class, expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performQueuePermissionDifferentRolesNoPermissions()
            throws IOException, XPathExpressionException,
            AndesAdminServiceBrokerManagerAdminException, URISyntaxException, SAXException,
            XMLStreamException, UserAdminUserAdminException,
            LoginAuthenticationExceptionException, LogoutAuthenticationExceptionException,
            JMSException, AndesClientConfigurationException, AndesClientException,
            NamingException, AutomationUtilException {
        // "superAdmin" refers to the admin
        this.createPublishAndSubscribeFromUser("superAdmin", "authQueue9");

        // Adding publish subscribe permissions of 'authQueue9' to 'pub_sub_queue_role' role.
        QueueRolePermission queueRolePermission = new QueueRolePermission();
        queueRolePermission.setRoleName(PUB_SUB_QUEUE_ROLE);
        queueRolePermission.setAllowedToConsume(true);
        queueRolePermission.setAllowedToPublish(true);
        this.updateQueueRoleConsumePublishPermission("authQueue9", queueRolePermission);
        log.info("Consumer and publish permissions updated for " + PUB_SUB_QUEUE_ROLE);

        this.createPublishAndSubscribeFromUser("authUser1", "authQueue9");
    }

    /**
     * Deleting the queues that were created.
     *
     * @throws IOException
     * @throws XPathExpressionException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @AfterClass()
    public void cleanUpQueues()
            throws IOException, XPathExpressionException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException,
            XMLStreamException, LoginAuthenticationExceptionException,
            AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient =
                new AndesAdminClient(super.backendURL, sessionCookie);

        andesAdminClient.deleteQueue("authQueue1");
        andesAdminClient.deleteQueue("authQueue2");
        andesAdminClient.deleteQueue("authQueue3");
        andesAdminClient.deleteQueue("authQueue4");
        andesAdminClient.deleteQueue("authQueue5");
        andesAdminClient.deleteQueue("authQueue6");
        andesAdminClient.deleteQueue("authQueue7");
        andesAdminClient.deleteQueue("authQueue8");
        andesAdminClient.deleteQueue("authQueue9");

        loginLogoutClientForAdmin.logout();
    }

    /**
     * Runs a test case where a consumer and publisher is created and published with a given user
     * key from the automation.xml.
     *
     * @param userKey         The user key mentioned in the automation.xml for a specific user.
     * @param destinationName The destination name of the queue.
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    private void createPublishAndSubscribeFromUser(String userKey, String destinationName)
            throws XPathExpressionException, AndesClientConfigurationException, IOException,
                   JMSException, AndesClientException, NamingException {
        long sendCount = 10L;
        long expectedCount = 10L;

        AutomationContext userAutomationContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY, userKey);
        User contextUser = userAutomationContext.getContextTenant().getContextUser();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration
                consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                        contextUser.getUserNameWithoutDomain(), contextUser.getPassword(),
                        ExchangeType.QUEUE, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                        contextUser.getUserNameWithoutDomain(), contextUser.getPassword(),
                        ExchangeType.QUEUE, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending " +
                                    "failed for user : " + contextUser.getUserNameWithoutDomain());
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message " +
                             "receiving failed for user : " + contextUser.getUserNameWithoutDomain());
    }

    /**
     * Assigning consuming publishing permissions of a queue to a role.
     *
     * @param queueName   The queue name
     * @param permissions New permissions for the role. can be publish, consume.
     * @throws XPathExpressionException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws LogoutAuthenticationExceptionException
     * @throws UserAdminUserAdminException
     */
    public void updateQueueRoleConsumePublishPermission(String queueName,
                                                        QueueRolePermission permissions)
            throws XPathExpressionException, IOException, URISyntaxException, SAXException,
            XMLStreamException, LoginAuthenticationExceptionException,
            AndesAdminServiceBrokerManagerAdminException,
            LogoutAuthenticationExceptionException,
            UserAdminUserAdminException, AutomationUtilException {

        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient =
                new AndesAdminClient(super.backendURL, sessionCookie);
        andesAdminClient.updatePermissionForQueue(queueName, permissions);
        loginLogoutClientForAdmin.logout();
    }
}
