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

/**
 * This class represents the user management class.
 */
public class UserManagementPage {
    private WebDriver driver;

    /**
     * Creates a user management class.
     *
     * @param driver The selenium web driver.
     */
    public UserManagementPage(WebDriver driver) {
        this.driver = driver;

        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("configure.user.mgt.header.xpath"))).getText().contains("Add Users and Roles")) {

            throw new IllegalStateException("This is not the User Management page");
        }
    }

    /**
     * Gets the first page in creating a new role
     *
     * @return The page that appears first when creating a new role
     */
    public AddRoleStep1Page getAddRolePage() {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("configure.usr.mgt.roles.link.xpath")))
                .click();
        return new AddRoleStep1Page(driver);
    }

    /**
     * Gets the first page in creating a new new
     *
     * @return The page that appears first when creating a new user
     */
    public AddUserStep1Page getAddNewUserPage() {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("configure.usr.mgt.users.link.xpath")))
                .click();
        return new AddUserStep1Page(driver);
    }
}
