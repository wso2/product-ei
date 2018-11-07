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
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

import java.util.List;

/**
 * UI test class related to the second page of the queue add wizard of Management console
 */
public class AddUserStep2Page extends MBPage{

    private static final Log log = LogFactory.getLog(AddUserStep2Page.class);

    /**
     * Checks whether the current page is the correct add user step 2 page. if not throws a
     * runtime exception (IllegalStateException)
     * @param driver WebDriver
     */
    public AddUserStep2Page(WebDriver driver) {
        super(driver);
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("usr.mgt.add.user.step2.sub.header.xpath"))).getText().contains("Step 2 : Select roles of the user")) {
            throw new IllegalStateException("This is not the Add User step2 page");
        }
    }

    public boolean selectRole(final String role){
        WebElement tableData = driver.findElement(By.xpath(UIElementMapper.getInstance()
                                                          .getElement("usr.mgt.add.user.step2.select.roles.td.xpath")));
        List<WebElement> inputList = tableData.findElements(By.tagName("input"));
        for(WebElement e: inputList){
            if((e.getAttribute("type").compareTo("checkbox") == 0) && (e.getAttribute("value").compareTo(role) == 0)) {
                e.click();
                return true;
            }
        }
        return false;
    }

    public boolean finish() {
        boolean isSuccessful = false;
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("usr.mgt.add.user.step2.finish.button"))).click();
        String dialog = driver.getWindowHandle();
        driver = driver.switchTo().window(dialog);

        WebElement e = driver.findElement(By.id("messagebox-info"));
        if(e.findElement(By.tagName("p")).getText().contains("is added successfully")) {
            isSuccessful = true;
        }

        // find ok button in popup dialog and click it
        List<WebElement> buttonList = driver.findElements(By.tagName("button"));
        for (WebElement okButton : buttonList) {
            if (okButton.getText().compareToIgnoreCase("ok") == 0) {
                okButton.click();
                break;
            }
        }
        dialog = driver.getWindowHandle();
        driver = driver.switchTo().window(dialog);
        return isSuccessful;
    }

}
