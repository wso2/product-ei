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
import org.openqa.selenium.WebElement;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.util.List;

/**
 * Abstraction of the Add new tenant page of the UI.
 */
public class AddNewTenantPage {

    private static final Log log = LogFactory.getLog(AddNewTenantPage.class);
    private WebDriver driver;

    public AddNewTenantPage(WebDriver driver) {
        this.driver = driver;
        // Check that we're on the right page.
        if (!driver.findElement(By.id(UIElementMapper.getInstance()
                .getElement("home.dashboard.middle.text"))).getText().contains("Register A New Organization")) {
            throw new IllegalStateException("Not in add new tenant page.");
        }
    }

    /**
     * Add a new tenant
     * @param domain domain of the tenant
     * @param firstName tenant first name
     * @param lastName tenant last name
     * @param adminUserName admin users' user name
     * @param adminPassword admin users' password
     * @param adminPasswordRepeat admin users' password (if repeat password doesn't match operation
     *                            must be unsuccessful)
     * @param adminEmail admin users' email
     * @return true if tenant successfully created. false otherwise
     */
    public boolean add(final String domain, final String firstName, final String lastName,
                       final String adminUserName, final String adminPassword, final String adminPasswordRepeat, final String adminEmail) {
        boolean isSuccessful = false;

        // fill the form
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.domain.field.id"))).sendKeys(domain);
        //todo implement usage plan select
//        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.usage.plan.field.id"))).(usagePlanName);
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.first.name.field.id"))).sendKeys(firstName);
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.last.name.field.id"))).sendKeys(lastName);
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.admin.user.name.field.id"))).sendKeys(adminUserName);
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.admin.password.field.id"))).sendKeys(adminPassword);
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.admin.password.repeat.field.id"))).sendKeys(adminPasswordRepeat);
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("add.tenant.admin.email.field.id"))).sendKeys(adminEmail);

        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("add.tenant.save.button.xpath"))).click();

        // handle confirmation popup
        String dialog = driver.getWindowHandle();
        driver.switchTo().window(dialog);

        if (driver.findElement(By.id(UIElementMapper.getInstance().getElement("mb.popup.dialog.id"))).getText().contains("successful")) {
            isSuccessful = true; // got success confirmation box
        }

        // find ok button in popup dialog and click it
        List<WebElement> buttonList = driver.findElements(By.tagName("button"));
        for (WebElement okButton : buttonList) {
            if (okButton.getText().compareToIgnoreCase("ok") == 0) {
                okButton.click();
                break;
            }
        }

        return isSuccessful;
    }

}
