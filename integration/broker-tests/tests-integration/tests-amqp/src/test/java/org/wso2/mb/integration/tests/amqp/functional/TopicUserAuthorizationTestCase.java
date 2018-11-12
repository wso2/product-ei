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
import org.wso2.carbon.andes.event.stub.core.TopicRolePermission;
import org.wso2.carbon.andes.event.stub.service.AndesEventAdminServiceEventAdminException;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
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
 * This class contains the test cases related to user authorization and topics
 */
public class TopicUserAuthorizationTestCase extends MBIntegrationBaseTest {
    /**
     * The logger used to log information, warnings, errors, etc.
     */
    private static final Logger log = LoggerFactory.getLogger(TopicUserAuthorizationTestCase.class);

    /**
     * Permission path for creating a topic
     */
    private static final String ADD_TOPIC_PERMISSION = "/permission/admin/manage/topic/add";

    /**
     * Roles for the test case scenarios
     */
    private static final String CREATE_PUB_SUB_TOPIC_ROLE = "create_pub_sub_topic_role";
    private static final String PUB_SUB_TOPIC_ROLE = "pub_sub_topic_role";
    private static final String NO_PERMISSION_TOPIC_ROLE = "no_permission_topic_role";

    /**
     * Prefix for internal roles for topics
     */
    private static final String TOPIC_PREFIX = "T_";

    private UserManagementClient userManagementClient;

    /**
     * Initializes before a test method. Removes users of admin group if exists. Adds new roles with
     * permissions.
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

        // Adding roles along with users if roles does not exist.
        userManagementClient
                .addRole(CREATE_PUB_SUB_TOPIC_ROLE, createPubSubUsers, new String[]{ADD_TOPIC_PERMISSION});
        userManagementClient.addRole(PUB_SUB_TOPIC_ROLE, pubSubUsers, new String[]{});
        userManagementClient.addRole(NO_PERMISSION_TOPIC_ROLE, noPermissionUsers, new String[]{});
    }

    /**
     * Cleans up the test case effects. Created roles and internal role related roles are created.
     *
     * @throws RemoteException
     * @throws UserAdminUserAdminException
     */
    @AfterMethod(alwaysRun = true)
    public void cleanUp() throws RemoteException, UserAdminUserAdminException {
        // Deleting roles of the users used in the test case
        userManagementClient.deleteRole(CREATE_PUB_SUB_TOPIC_ROLE);
        userManagementClient.deleteRole(PUB_SUB_TOPIC_ROLE);
        userManagementClient.deleteRole(NO_PERMISSION_TOPIC_ROLE);

        // Deleting internal roles specific to topics
        FlaggedName[] allRoles = userManagementClient.getAllRolesNames("*", 10);
        for (FlaggedName allRole : allRoles) {
            if (allRole.getItemName().contains(TOPIC_PREFIX)) {
                userManagementClient.deleteRole(allRole.getItemName());
            }
        }
    }

    /**
     * User creates a topic and then publishes and consumes messages.
     *
     * @throws IOException
     * @throws UserAdminUserAdminException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     * @throws AndesClientConfigurationException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicPermissionTestCase()
            throws IOException, UserAdminUserAdminException, XPathExpressionException,
                   NamingException, JMSException, AndesClientException,
                   AndesClientConfigurationException {
        this.createPublishAndSubscribeFromUser("authUser1", "authTopic1");

    }

    /**
     * User1 and User2 exists in the same role where create topic permission is assigned.
     * User1 creates a topic  and then publishes and consumes messages.
     * User2 tries to publish and consume messages. But unable to succeed.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     */
    @Test(groups = {"wso2.mb", "topic"}, expectedExceptions = JMSException.class,
            expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performTopicPermissionSameRoleUsersWithNoPublishOrConsume()
            throws AndesClientConfigurationException, NamingException, IOException,
                   XPathExpressionException, AndesClientException, JMSException {
        this.createPublishAndSubscribeFromUser("authUser1", "authTopic2");
        this.createPublishAndSubscribeFromUser("authUser2", "authTopic2");
    }

    /**
     * User1 and User2 exists in the same role where create topic  permission is assigned.
     * User1 creates a topic  and then publishes and consumes messages.
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
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicPermissionSameRoleUsersWithPublishOrConsume()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            AndesEventAdminServiceEventAdminException, XMLStreamException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException,
            AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        this.createPublishAndSubscribeFromUser("authUser1", "authTopic3");

        // Adding publish subscribe permissions of 'authTopic3' to 'create_pub_sub_topic_role' role.
        TopicRolePermission topicRolePermission = new TopicRolePermission();
        topicRolePermission.setRoleName(CREATE_PUB_SUB_TOPIC_ROLE);
        topicRolePermission.setAllowedToSubscribe(true);
        topicRolePermission.setAllowedToPublish(true);
        this.updateTopicRoleConsumePublishPermission("authTopic3", topicRolePermission);
        log.info("Consumer and publish permissions updated for " + CREATE_PUB_SUB_TOPIC_ROLE);

        this.createPublishAndSubscribeFromUser("authUser2", "authTopic3");
    }

    /**
     * User1 and User2 exists in the same role where create topic permission is assigned.
     * Admin(UI) creates a topic and then publishes and consumes messages.
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
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicPermissionSameRoleUsersWithAdminCreated()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            AndesEventAdminServiceEventAdminException, XMLStreamException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException,
            AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        // "superAdmin" refers to the admin
        this.createPublishAndSubscribeFromUser("superAdmin", "authTopic8");

        // Adding publish subscribe permissions of 'authTopic8' to 'create_pub_sub_topic_role' role.
        TopicRolePermission topicRolePermission = new TopicRolePermission();
        topicRolePermission.setRoleName(CREATE_PUB_SUB_TOPIC_ROLE);
        topicRolePermission.setAllowedToSubscribe(true);
        topicRolePermission.setAllowedToPublish(true);
        this.updateTopicRoleConsumePublishPermission("authTopic8", topicRolePermission);
        log.info("Consumer and publish permissions updated for " + CREATE_PUB_SUB_TOPIC_ROLE);

        this.createPublishAndSubscribeFromUser("authUser1", "authTopic8");
        this.createPublishAndSubscribeFromUser("authUser2", "authTopic8");
    }

    /**
     * Admin add subscription to topic and subscribe.
     * Admin unsubscribe from topic after receiving expected message count
     * Delete topic admin created
     * User1 create topic with the same name
     *
     * Expected results - User1 must be able to successfully create and subscribe to topic
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicPermissionWithAdminCreateAndUnscribe()
            throws AndesClientConfigurationException, NamingException, JMSException, AndesClientException,
            XPathExpressionException, IOException, AutomationUtilException, AndesEventAdminServiceEventAdminException {
        // "superAdmin" refers to the admin
        this.createPublishSubscribeAndUnsubscribeFromUser("superAdmin", "authTopic10");

        // delete topic admin created
        LoginLogoutClient loginLogoutClientForUser = new LoginLogoutClient(this.automationContext);
        String sessionCookie = loginLogoutClientForUser.login();
        TopicAdminClient topicAdminClient =
                new TopicAdminClient(this.backendURL, sessionCookie);
        topicAdminClient.removeTopic("authTopic10");

        // user1 subscribe with same topic name where previously created, unsubscribe and deleted by admin
        this.createPublishSubscribeAndUnsubscribeFromUser("authUser1", "authTopic10");
    }

    /**
     * User1 is in Role1 where there is topic  creating permissions.
     * User5 is in Role2 where there are no create topic  permissions.
     * User1 creates a topic  and then publishes and consumes messages.
     * User5 tries to publish and consume messages. User5 fails.
     *
     * @throws IOException
     * @throws UserAdminUserAdminException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     * @throws AndesClientConfigurationException
     */
    @Test(groups = {"wso2.mb", "topic"}, expectedExceptions = JMSException.class,
            expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performTopicPermissionDifferentRoleUsersWithNoPermissions()
            throws IOException, UserAdminUserAdminException, XPathExpressionException,
                   NamingException, JMSException, AndesClientException,
                   AndesClientConfigurationException {
        this.createPublishAndSubscribeFromUser("authUser1", "authTopic4");
        this.createPublishAndSubscribeFromUser("authUser5", "authTopic4");
    }

    /**
     * User1 exists in a role where create topic  permission is assigned.
     * User1 creates a topic  and then publishes and consumes messages.
     * User1 is removed from the role.
     * User1 tries to publish and consume messages. User1 fails.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     * @throws UserAdminUserAdminException
     */
    @Test(groups = {"wso2.mb", "topic"}, expectedExceptions = JMSException.class,
            expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performTopicPermissionSameUserRemovedFromRole()
            throws AndesClientConfigurationException, NamingException, IOException,
                   XPathExpressionException, AndesClientException, JMSException,
                   UserAdminUserAdminException {
        this.createPublishAndSubscribeFromUser("authUser1", "authTopic5");

        // Removing authUser1 from create_pub_sub_topic_role and Internal/T_authTopic5
        userManagementClient.addRemoveRolesOfUser("authUser1", new String[]{NO_PERMISSION_TOPIC_ROLE},
                                      new String[]{CREATE_PUB_SUB_TOPIC_ROLE, "Internal/T_authtopic5"});
        log.info("Removing authUser1 from " + CREATE_PUB_SUB_TOPIC_ROLE + " and Internal/T_authtopic5");

        this.createPublishAndSubscribeFromUser("authUser1", "authTopic5");
    }

    /**
     * User1 and User2 exists in the same role where create topic  permission is assigned.
     * User1 creates a topic  and then publishes and consumes messages.
     * Admin assigns publishing and consuming  permissions to the role in which User1 and User2 are in.
     * User1 is removed from the role.
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
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicPermissionSameRoleAssignedPermissions()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            AndesEventAdminServiceEventAdminException, XMLStreamException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException,
            AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        this.createPublishAndSubscribeFromUser("authUser1", "authTopic6");

        // Adding publish subscribe permissions of 'authTopic6' to 'create_pub_sub_topic_role' role.
        TopicRolePermission topicRolePermission = new TopicRolePermission();
        topicRolePermission.setRoleName(CREATE_PUB_SUB_TOPIC_ROLE);
        topicRolePermission.setAllowedToSubscribe(true);
        topicRolePermission.setAllowedToPublish(true);
        updateTopicRoleConsumePublishPermission("authTopic6", topicRolePermission);
        log.info("Consumer and publish permissions updated for " + CREATE_PUB_SUB_TOPIC_ROLE);

        // Removing authUser1 from create_pub_sub_topic_role and Internal/T_authTopic6
        userManagementClient
                .addRemoveRolesOfUser("authUser1", new String[]{NO_PERMISSION_TOPIC_ROLE},
                                  new String[]{CREATE_PUB_SUB_TOPIC_ROLE, "Internal/T_authtopic6"});

        this.createPublishAndSubscribeFromUser("authUser2", "authTopic6");

    }

    /**
     * User3 is in Role2 where there are no create topic permissions.
     * Admin creates a topic and then publishes and consumes messages.
     * Admin assigns publishing and consuming permissions to Role2.
     * User3 tries to publish and consume messages. User3 succeeds.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     * @throws UserAdminUserAdminException
     * @throws LoginAuthenticationExceptionException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicPermissionDifferentRolesAssignedPermissions()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            AndesEventAdminServiceEventAdminException, XMLStreamException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException,
            AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        this.createPublishAndSubscribeFromUser("superAdmin", "authTopic7");

        // Adding publish subscribe permissions of 'authTopic7' to 'pub_sub_topic_role' role.
        TopicRolePermission topicRolePermission = new TopicRolePermission();
        topicRolePermission.setRoleName(PUB_SUB_TOPIC_ROLE);
        topicRolePermission.setAllowedToSubscribe(true);
        topicRolePermission.setAllowedToPublish(true);
        this.updateTopicRoleConsumePublishPermission("authTopic7", topicRolePermission);
        log.info("Consumer and publish permissions updated for " + PUB_SUB_TOPIC_ROLE);

        this.createPublishAndSubscribeFromUser("authUser3", "authTopic7");
    }

    /**
     * User1 is in Role1 where there are create topic permissions.
     * User3 is in Role2 where there are no create topic permissions.
     * Admin creates a topic and then publishes and consumes messages.
     * Admin assigns publishing and consuming permissions to Role2.
     * User1 tries to publish and consume messages. User1 fails.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AndesClientException
     * @throws JMSException
     * @throws UserAdminUserAdminException
     * @throws LoginAuthenticationExceptionException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws XMLStreamException
     * @throws LogoutAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    @Test(groups = {"wso2.mb", "topic"}, expectedExceptions = JMSException.class,
            expectedExceptionsMessageRegExp = ".*Permission denied.*")
    public void performTopicPermissionDifferentRolesNoPermissions()
            throws AndesClientConfigurationException, NamingException, IOException,
            XPathExpressionException, AndesClientException, JMSException,
            UserAdminUserAdminException, LoginAuthenticationExceptionException,
            AndesEventAdminServiceEventAdminException, XMLStreamException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException,
            AndesAdminServiceBrokerManagerAdminException, AutomationUtilException {
        this.createPublishAndSubscribeFromUser("superAdmin", "authTopic9");

        // Adding publish subscribe permissions of 'authTopic9' to 'pub_sub_topic_role' role.
        TopicRolePermission topicRolePermission = new TopicRolePermission();
        topicRolePermission.setRoleName(PUB_SUB_TOPIC_ROLE);
        topicRolePermission.setAllowedToSubscribe(true);
        topicRolePermission.setAllowedToPublish(true);
        this.updateTopicRoleConsumePublishPermission("authTopic9", topicRolePermission);
        log.info("Consumer and publish permissions updated for " + PUB_SUB_TOPIC_ROLE);

        this.createPublishAndSubscribeFromUser("authUser1", "authTopic9");
    }

    /**
     * Deleting the topics that were created.
     *
     * @throws XPathExpressionException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XMLStreamException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AndesEventAdminServiceEventAdminException
     * @throws LogoutAuthenticationExceptionException
     */
    @AfterClass()
    public void cleanUpTopics()
            throws XPathExpressionException, LoginAuthenticationExceptionException, IOException,
            XMLStreamException, URISyntaxException, SAXException,
            AndesEventAdminServiceEventAdminException,
            LogoutAuthenticationExceptionException, AutomationUtilException {
        LoginLogoutClient loginLogoutClientForUser = new LoginLogoutClient(this.automationContext);
        String sessionCookie = loginLogoutClientForUser.login();
        TopicAdminClient topicAdminClient =
                new TopicAdminClient(this.backendURL, sessionCookie);
        topicAdminClient.removeTopic("authTopic1");
        topicAdminClient.removeTopic("authTopic2");
        topicAdminClient.removeTopic("authTopic3");
        topicAdminClient.removeTopic("authTopic4");
        topicAdminClient.removeTopic("authTopic5");
        topicAdminClient.removeTopic("authTopic6");
        topicAdminClient.removeTopic("authTopic7");
        topicAdminClient.removeTopic("authTopic8");
        topicAdminClient.removeTopic("authTopic9");

        loginLogoutClientForUser.logout();

    }

    /**
     * Runs a test case where a consumer and publisher is created and published with a given user
     * key from the automation.xml.
     *
     * @param userKey         The user key mentioned in the automation.xml for a specific user.
     * @param destinationName The destination name of the topic.
     * @throws XPathExpressionException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws IOException
     * @throws javax.jms.JMSException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientException
     * @throws javax.naming.NamingException
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
                new AndesJMSConsumerClientConfiguration( getAMQPPort(),
                                contextUser.getUserNameWithoutDomain(), contextUser.getPassword(),
                                ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration( getAMQPPort(),
                        contextUser.getUserNameWithoutDomain(), contextUser.getPassword(),
                        ExchangeType.TOPIC, destinationName);
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
     * Runs a test case where a consumer and publisher is created and published with a given user
     * key from the automation.xml. Subscriber get unsubscribe after receiving expected message count.
     *
     * @param userKey         The user key mentioned in the automation.xml for a specific user.
     * @param destinationName The destination name of the topic.
     * @throws XPathExpressionException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException
     * @throws IOException
     * @throws javax.jms.JMSException
     * @throws org.wso2.mb.integration.common.clients.exceptions.AndesClientException
     * @throws javax.naming.NamingException
     */
    private void createPublishSubscribeAndUnsubscribeFromUser(String userKey, String destinationName)
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
                new AndesJMSConsumerClientConfiguration( getAMQPPort(),
                        contextUser.getUserNameWithoutDomain(), contextUser.getPassword(),
                        ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setUnSubscribeAfterEachMessageCount(expectedCount);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration( getAMQPPort(),
                        contextUser.getUserNameWithoutDomain(), contextUser.getPassword(),
                        ExchangeType.TOPIC, destinationName);
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
     * Assigning consuming publishing permissions of a topic to a role.
     *
     * @param topicName   The topic name
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
    public void updateTopicRoleConsumePublishPermission(String topicName,
                                                        TopicRolePermission permissions)
            throws XPathExpressionException, IOException, URISyntaxException, SAXException,
            XMLStreamException, LoginAuthenticationExceptionException,
            AndesAdminServiceBrokerManagerAdminException,
            LogoutAuthenticationExceptionException,
            UserAdminUserAdminException,
            AndesEventAdminServiceEventAdminException, AutomationUtilException {

        LoginLogoutClient loginLogoutClientForUser = new LoginLogoutClient(automationContext);
        String sessionCookie = loginLogoutClientForUser.login();
        TopicAdminClient topicAdminClient =
                new TopicAdminClient(backendURL, sessionCookie);
        topicAdminClient.updatePermissionForTopic(topicName, permissions);
        loginLogoutClientForUser.logout();
    }
}
