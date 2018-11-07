/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.andes.event.stub.service.AndesEventAdminServiceEventAdminException;
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
 * This class contains the test cases related to user authorization and sub topics
 */
public class SubTopicUserAuthorizationTestCase extends MBIntegrationBaseTest {

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
     * Cleans up the test case effects. Created roles and internal role related roles are deleted.
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
     * authUser1 user creates a sub topic and then publishes and consumes messages.
     * authUser1 should be able to create and publish to sub topic.
     *
     * @throws IOException
     * @throws UserAdminUserAdminException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     * @throws AndesClientConfigurationException
     */
    @Test(groups = {"wso2.mb"})
    public void performSubTopicPermissionWithAuthorizedUserTestCase()
            throws IOException, UserAdminUserAdminException, XPathExpressionException,
            NamingException, JMSException, AndesClientException,
            AndesClientConfigurationException {
        this.createPublishAndSubscribeFromUsers("authUser1", "authUser1", "authTopic1.authSubTopic1");
    }


    /**
     * authUser2 user creates topic and publish messages.
     * authUser2 user creates sub topic under existing topic and publish messages.
     * authUser2 should be able to create subtopic.
     *
     * @throws IOException
     * @throws UserAdminUserAdminException
     * @throws XPathExpressionException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     * @throws AndesClientConfigurationException
     */
    @Test(groups = {"wso2.mb"})
    public void performSubTopicPermissionWithDifferentAuthorizedUserTestCase()
            throws IOException, UserAdminUserAdminException, XPathExpressionException,
            NamingException, JMSException, AndesClientException,
            AndesClientConfigurationException {
        this.createPublishAndSubscribeFromUsers("authUser2", "authUser2", "authTopic2");
        this.createPublishAndSubscribeFromUsers("authUser2", "authUser2", "authTopic2.authSubTopic2");

    }

    /**
     * User1 creates a topic and then publishes and consumes messages.
     * User2 tries to create sub topic user existing topic created by User1. But unable to succeed.
     * JMSException exception is expected.
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
        this.createPublishAndSubscribeFromUsers("authUser1", "authUser1" , "authTopic3");
        this.createPublishAndSubscribeFromUsers("authUser3", "authUser3", "authTopic3.authSubTopic3");
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

        loginLogoutClientForUser.logout();

    }



    /**
     * Runs a test case where a consumer and publisher is created and published with a given user
     * key from the automation.xml.
     *
     * @param topicConsumeUser user defined in automation.xml for consume from a topic.
     * @param topicPublishUser user defined in automation.xml for publish to a topic.
     * @param destinationName topic destination.
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws AndesClientException
     * @throws NamingException
     */
    private void createPublishAndSubscribeFromUsers(String topicConsumeUser, String topicPublishUser,
                                                    String destinationName)
            throws XPathExpressionException, AndesClientConfigurationException, IOException,
            JMSException, AndesClientException, NamingException {

        long sendCount = 100L;
        long expectedCount = 100L;

        // get context for topic consume user
        AutomationContext topicConsumeUserAutomationContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY, topicConsumeUser);
        User consumeUser = topicConsumeUserAutomationContext.getContextTenant().getContextUser();

        // get context for topic publisher user
        AutomationContext topicPublishUserAutomationContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY, topicPublishUser);
        User publishUser = topicPublishUserAutomationContext.getContextTenant().getContextUser();


        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration
                consumerConfig =
                new AndesJMSConsumerClientConfiguration( getAMQPPort(),
                        consumeUser.getUserNameWithoutDomain(), consumeUser.getPassword(),
                        ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration( getAMQPPort(),
                        publishUser.getUserNameWithoutDomain(), publishUser.getPassword(),
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
                    "failed for user : " + consumeUser.getUserNameWithoutDomain());
            Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message " +
                    "receiving failed for user : " + publishUser.getUserNameWithoutDomain());
    }

}
