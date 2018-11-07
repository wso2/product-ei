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
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.mb.ui.test.permissions;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicsBrowsePage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The following class contains UI related to permission and topics.
 */
public class TopicPermissionTestCase extends MBIntegrationUiBaseTest {
    /**
     * Permission path for creating a topic
     */
    private static final String ADD_TOPIC_PERMISSION = "/permission/admin/manage/topic";

    /**
     * Permission path for logging in to management console.
     */
    private static final String LOGIN_PERMISSION = "/permission/admin/login";

    /**
     * Role for the test case scenarios
     */
    private static final String CREATE_TOPIC_PERMISSION_ROLE = "create_topic_role";

    /**
     * Initializes test case
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * Creates a topic by giving topic creation rights to the user.
     * 1. User is in a role with no permissions.
     * 2. Admin gives permissions to the role to create topics and for logging in.
     * 3. User creates a topic.
     * 4. Validates whether topic is created.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws UserAdminUserAdminException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void createTopicPermissionTestCase() throws XPathExpressionException, IOException, UserAdminUserAdminException {
        String topicName = "topicCreationPermission";

        AutomationContext authAutomationContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY,
                                                                                    "topicAuthUser");
        User contextUser = authAutomationContext.getContextTenant().getContextUser();

        String[] createPermissionUser = new String[]{contextUser.getUserNameWithoutDomain()};

        // Logging into user management as admin
        UserManagementClient userManagementClient =
                new UserManagementClient(super.backendURL, "admin", "admin");

        // Removing admin permission for user
        userManagementClient.updateUserListOfRole(FrameworkConstants.ADMIN_ROLE, null, createPermissionUser);

        // Adding roles along with users
        userManagementClient
                .addRole(CREATE_TOPIC_PERMISSION_ROLE, createPermissionUser, new String[]{ADD_TOPIC_PERMISSION, LOGIN_PERMISSION});

        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        // Logging in to the the management console
        HomePage homePage = loginPage.loginAs(contextUser.getUserNameWithoutDomain(), contextUser.getPassword());

        TopicAddPage topicAddPage =
                homePage.getTopicAddPage("home.mb.topics.add.without.queue.xpath");

        // Creating a topic by the user and check whether valid dialog pop up is shown
        Assert.assertEquals(topicAddPage.addTopic(topicName), true);

        TopicsBrowsePage topicsBrowsePage = homePage.getTopicsBrowsePage("home.mb.topics.browse.without.queue.xpath");

        // Checks whether topic is created in the browsing page
        Assert.assertEquals(topicsBrowsePage.isTopicPresent(topicName), true);
    }

    /**
     * Closing down the driver
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
