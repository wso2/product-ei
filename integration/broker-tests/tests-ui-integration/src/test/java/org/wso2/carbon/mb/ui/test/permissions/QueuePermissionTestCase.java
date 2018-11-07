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
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueuesBrowsePage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The following class contains UI related to permission and queues.
 */
public class QueuePermissionTestCase extends MBIntegrationUiBaseTest {
    /**
     * Permission path for creating a queue
     */
    private static final String ADD_QUEUE_PERMISSION = "/permission/admin/manage/queue";

    /**
     * Permission path for logging in to management console.
     */
    private static final String LOGIN_PERMISSION = "/permission/admin/login";

    /**
     * Role for the test case scenarios
     */
    private static final String CREATE_QUEUE_PERMISSION_ROLE = "create_queue_role";

    /**
     * Initializes the test case
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * Creates a queue by giving queue creation rights to the user.
     * 1. User is in a role with no permissions.
     * 2. Admin gives permissions to the role to create queues and for logging in.
     * 3. User creates a queue.
     * 4. Validates whether queue is created.
     *
     * @throws IOException
     * @throws UserAdminUserAdminException
     * @throws XPathExpressionException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void createQueuePermissionTestCase() throws IOException, UserAdminUserAdminException,
            XPathExpressionException {
        String queueName = "queueCreationPermission";

        AutomationContext authAutomationContext =
                new AutomationContext("MB", "mb001", FrameworkConstants.SUPER_TENANT_KEY,
                                                                                "queueAuthUser");
        User contextUser = authAutomationContext.getContextTenant().getContextUser();

        String[] createPermissionUsers = new String[]{contextUser.getUserNameWithoutDomain()};

        // Logging into user management as admin
        UserManagementClient userManagementClient =
                new UserManagementClient(backendURL, "admin", "admin");

        // Removing admin permission for user
        userManagementClient
                .updateUserListOfRole(FrameworkConstants.ADMIN_ROLE, null, createPermissionUsers);

        // Adding roles along with user
        userManagementClient.addRole(CREATE_QUEUE_PERMISSION_ROLE, createPermissionUsers,
                                                                new String[]{ADD_QUEUE_PERMISSION, LOGIN_PERMISSION});

        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);

        // Logging in to the the management console
        HomePage homePage = loginPage.loginAs(contextUser.getUserNameWithoutDomain(), contextUser.getPassword());

        QueueAddPage queueAddPage = homePage.getQueueAddPage();

        // Creating a queue by the user and check whether valid dialog pop up is shown
        Assert.assertEquals(queueAddPage.addQueue(queueName), true);
        QueuesBrowsePage queuesBrowsePage = homePage.getQueuesBrowsePage();

        // Checks whether queue is created in the browsing page
        Assert.assertEquals(queuesBrowsePage.isQueuePresent(queueName), true);

    }

    /**
     * Closing down the driver
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
