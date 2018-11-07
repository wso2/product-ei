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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.configure.*;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Creates a new user with login permission using the super user admin account.
 * Then logs into the created new user account with new users user credentials
 */
public class NewUserPermissionTest extends MBIntegrationUiBaseTest{

    /**
     * Initializes the test case
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * Creates a new user with login role and logs in with that permissions.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test()
    public void createNewUser() throws XPathExpressionException, IOException {
        // login to admin account
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        //create a new login user role with login permission
        ConfigurePage configurePage = homePage.getConfigurePage();
        UserManagementPage usrMgtPage = configurePage.getUserManagementPage();
        AddRoleStep1Page step1 = usrMgtPage.getAddRolePage();
        step1.setDetails("loginRole");
        AddRoleStep2Page step2 = step1.next();
        step2.selectPermission("usr.mgt.add.role.step2.login.role.xpath");
        AddRoleStep3Page step3 = step2.next();

        // assert whether the role was successfully created
        Assert.assertEquals(step3.finish(), true);

        // create a new user and assign newly created login user role
        configurePage = new ConfigurePage(driver);
        usrMgtPage = configurePage.getUserManagementPage();
        AddUserStep1Page addUserStep1Page = usrMgtPage.getAddNewUserPage();
        addUserStep1Page.addUserDetails("loginUser", "password", "password");
        AddUserStep2Page addUserStep2Page = addUserStep1Page.next();
        addUserStep2Page.selectRole("loginRole");
        addUserStep2Page.finish();
        loginPage = logout();

        // login with new user account
        homePage = loginPage.loginAs("loginUser", "password");
        homePage.logout();
    }

    /**
     * Shuts down the selenium web driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }

}
