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

public class AddRoleStep3Page {

    private static final Log log = LogFactory.getLog(AddRoleStep3Page.class);
    private WebDriver driver;

    public AddRoleStep3Page(WebDriver driver){
        this.driver = driver;
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("usr.mgt.add.role.step3.sub.header.xpath"))).getText().contains("Step 3")) {
            throw new IllegalStateException("This is not the Add Role step3 page");
        }
    }

    public boolean finish(){
        boolean isSuccessful = false;
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("usr.mgt.add.role.step3.finish.button.xpath"))).click();
        // handle delete confirmation popup
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

    public void selectUsers(List<String> userXpathList) {
        for(String userXpath: userXpathList){
            driver.findElement(By.xpath(userXpath)).click();
        }
    }
}
