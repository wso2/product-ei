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

package org.wso2.mb.integration.common.utils.ui.pages.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

import java.io.IOException;
import java.util.List;

/**
 * UI test class related to queue add page of Management console.
 */
public class QueueAddPage extends MBPage {

    /**
     * Checks whether the current page the WebDriver is in is the correct queue add page. if not
     * throws a runtime exception (IllegalStateException)
     *
     * @param driver WebDriver
     */
    public QueueAddPage(WebDriver driver) {
        super(driver);
        if(!driver.findElement(By.xpath(UIElementMapper.getInstance().
                getElement("mb.add.queue.page.header.xpath"))).getText().contains("Add Queue")){
            throw new IllegalStateException("This is not the Add Queue page");
        }
    }

    /**
     * Adds a queue with all without changing privileges for the queue
     * @param qName queue name
     * @return true if successful and false otherwise
     * @throws IOException
     */
    public boolean addQueue(final String qName) throws IOException {
        return addQueue(qName, true);
    }

    /**
     * Adds a queue without privileges to any role or not explicitly specified
     * @param qName queue name
     * @param withoutPrivileges without privileges set to roles to consume or publish
     * @return true if successful and false otherwise
     * @throws IOException
     */
    public boolean addQueue(final String qName, boolean withoutPrivileges) throws IOException{
        boolean isSuccessful = false;

        WebElement qNameField = driver.findElement(By.id(UIElementMapper.getInstance()
                .getElement("mb.add.queue.page.qname.field.id")));
        qNameField.sendKeys(qName);

        if(withoutPrivileges) {

            // get permission table
            WebElement table = driver.findElement(By.xpath(UIElementMapper.getInstance()
                    .getElement("mb.add.queue.page.permission.table")));

            // get role name related publish consume checkboxes list for all the roles
            List<WebElement> checkBoxList = table.findElements(By.tagName("input"));

            // make all the permissions unchecked
            for (WebElement element: checkBoxList) {
                if(element.isSelected()) {
                    element.click(); // uncheck checkbox
                }
            }
        }

        driver.getWindowHandle();
        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.add.queue.page.add.button.xpath"))).click();
        String dialog = driver.getWindowHandle();
        driver.switchTo().window(dialog);
        if(driver.findElement(By.id(UIElementMapper.getInstance()
                .getElement("mb.popup.dialog.id"))).getText().contains("Queue added successfully")) {
            isSuccessful =true;
        }
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("mb.add.queue.page" +
                ".onqueueadd.okbutton.xpath"))).click();

        return isSuccessful;
    }
}
