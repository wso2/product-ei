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
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.util.List;

public class AddRoleStep2Page {

    private static final Log log = LogFactory.getLog(AddRoleStep2Page.class);
    private WebDriver driver;

    public AddRoleStep2Page(WebDriver driver){
        this.driver = driver;
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("usr.mgt.add.role.step2.sub.header.xpath")))
                .getText().contains("Step 2 : Select permissions to add to Role")) {
            throw new IllegalStateException("This is not the Add Role step2 page");
        }
    }

    public void selectPermission(String permissionXpath) {
           driver.findElement(By.xpath(UIElementMapper.getInstance().getElement(permissionXpath))).click();
    }

    public AddRoleStep3Page next() {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("usr.mgt.add.role.step2.next.button.xpath"))).click();
        return new AddRoleStep3Page(driver);
    }

}
