/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.mb.integration.common.utils.ui.pages.configure.AddSecondaryUserStorePage;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.io.IOException;

public class UserStoreManagementPage {
    private static final Log log = LogFactory.getLog(UserStoreManagementPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public UserStoreManagementPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!driver.findElement(By.id(uiElementMapper.getElement("configure.user.store.management.header.id")))
                                                                            .getText().contains("Add New User Store")) {
            throw new IllegalStateException("This is not the User Store Management page");
        }
    }

    public AddSecondaryUserStorePage addSecondaryUserStore() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("configure.user.store.management.add.secondary.userstore"))).click();
        return new AddSecondaryUserStorePage(driver);
    }
}
