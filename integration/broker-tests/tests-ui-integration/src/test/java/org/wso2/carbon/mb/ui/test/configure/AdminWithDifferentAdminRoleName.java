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

package org.wso2.carbon.mb.ui.test.configure;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.configure.AddUserStep1Page;
import org.wso2.mb.integration.common.utils.ui.pages.configure.AddUserStep2Page;
import org.wso2.mb.integration.common.utils.ui.pages.configure.ConfigurePage;
import org.wso2.mb.integration.common.utils.ui.pages.configure.UserManagementPage;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueuesBrowsePage;

import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;

/**
 * test for fix https://wso2.org/jira/browse/MB-850
 * This test case test whether admin with a different admin role name is recognised as an admin
 * role in the server.
 */
public class AdminWithDifferentAdminRoleName extends MBIntegrationUiBaseTest {

    /**
     * Initializes the test case with a different name for admin role.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws IOException
     */
    @BeforeClass()
    public void initialize() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
        restartServerWithDifferentAdminRoleName();
    }

    /**
     *  Test Steps:
     *  - Admin role name is changed to "Administrator" using user-mgt.xml and restart server
     *  - Log in to admin account and create a queue, "testQueue"
     *  - New user, John, created with admin privileges.
     *  - Log out from admin account and log back in with john account
     *  - Browse the "testQueue".
     * Since john is an admin he should be able to view the queue.
     *
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Test()
    public void testForDifferentAdminUser() throws IOException, XPathExpressionException {
        String queueName = "testQueue";
        String userName = "john";
        String password = "112358";

        driver.get(getLoginURL());

        // login as admin
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getAdminUserName(), getAdminPassword());

        // create queue
        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertTrue("Adding a new queue failed.", queueAddPage.addQueue(queueName, true));

        // create new user, John
        ConfigurePage configurePage = queueAddPage.getConfigurePage();
        UserManagementPage userManagementPage = configurePage.getUserManagementPage();
        AddUserStep1Page addUserStep1Page = userManagementPage.getAddNewUserPage();
        addUserStep1Page.addUserDetails(userName, password, password);
        AddUserStep2Page addUserStep2Page = addUserStep1Page.next();
        addUserStep2Page.selectRole(CUSTOM_ADMIN_ROLE_NAME);

        Assert.assertTrue(addUserStep2Page.finish());

        // logout from admin account
        loginPage = addUserStep2Page.logout();

        // login as john, (admin user)
        homePage = loginPage.loginAs(userName, password);
        QueuesBrowsePage browsePage = homePage.getQueuesBrowsePage();

        // try to browse,
        // NOTE: John doesn't have explicit consume or publish permission. But an admin user.
        // should be authorised to browse queue content.
        browsePage.browseQueue(queueName);
    }

    /**
     * Shuts down the selenium driver and restarts the server in previous configuration.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException {
        driver.quit();
        restartInPreviousConfiguration();
    }

}
