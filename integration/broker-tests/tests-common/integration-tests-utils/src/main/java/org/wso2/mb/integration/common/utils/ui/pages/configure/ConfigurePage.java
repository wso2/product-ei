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

package org.wso2.mb.integration.common.utils.ui.pages.configure;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.io.IOException;

/**
 * Abstraction of the Configuration tab page of the UI.
 */
public class ConfigurePage {
    private WebDriver driver;

    /**
     * Initializes configuration page.
     * @param driver The selenium web driver.
     * @throws IOException
     */
    public ConfigurePage(WebDriver driver) throws IOException {
        this.driver = driver;
        // Check that we're on the right page.
        String attr = driver.findElement(By.id(UIElementMapper.getInstance().getElement("configure.panel.button.id"))).getAttribute("class");

        if (attr.compareTo("menu-panel-buttons selected") != 0) {
            throw new IllegalStateException("This is not the Configure page");
        }
    }

    /**
     * User store Management store page is selected from UI and returned
     *
     * @return The user management store page.
     * @throws IOException
     */
    public UserStoreManagementPage getUserStoreManagementPage() throws IOException {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("configure.user.store.management.xpath"))).click();
        return new UserStoreManagementPage(driver);
    }

    /**
     * New Tenant creation page link is selected from Configure tab page in UI and AddNewTenantPage
     * is returned
     *
     * @return The tenant adding page.
     */
    public AddNewTenantPage getAddNewTenantPage() {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("configure.multitenancy.add.new.tenant.xpath"))).click();
        return new AddNewTenantPage(driver);
    }

    /**
     * User Management page link is selected from Configure tab page in UI
     *
     * @return The user management page to add users and roles.
     */
    public UserManagementPage getUserManagementPage()  {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("configure.users.and.roles.button.xpath"))).click();
        return new UserManagementPage(driver);
    }
}
