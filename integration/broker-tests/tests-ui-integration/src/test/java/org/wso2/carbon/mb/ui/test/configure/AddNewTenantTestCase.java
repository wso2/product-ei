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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.configure.AddNewTenantPage;
import org.wso2.mb.integration.common.utils.ui.pages.configure.ConfigurePage;
import org.wso2.mb.integration.common.utils.ui.pages.configure.TenantHomePage;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Login as an admin user and creates a new tenant and logs out then login as the new tenant and
 * logout
 */
public class AddNewTenantTestCase extends MBIntegrationUiBaseTest {

    /**
     * Initializes the test case.
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
     * Test to create the new tenant account and login to the newly created tenant account.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test()
    public void addNewTenantTest() throws XPathExpressionException, IOException {
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        ConfigurePage configurePage = homePage.getConfigurePage();
        AddNewTenantPage addNewTenantPage = configurePage.getAddNewTenantPage();

        String domain = "tenant1.com";
        String usagePlanName = "demo";
        String firstName = "Bob";
        String lastName = "Dillon";
        String adminUserName = "bob";
        String adminPassword = "password";
        String adminPasswordRepeat = "password";
        String adminEmail = "bob.dilon@gmail.com";

        addNewTenantPage.add(domain, firstName, lastName, adminUserName,
                adminPassword, adminPasswordRepeat, adminEmail);
        homePage.logout();

        driver.get(getLoginURL());
        loginPage = new LoginPage(driver);
        TenantHomePage tenantHomePage = loginPage.loginAsTenant(adminUserName, domain, adminPassword);
        tenantHomePage.logout();
    }

    /**
     * Stops the web driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
