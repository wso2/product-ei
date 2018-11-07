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

package org.wso2.mb.integration.common.utils.ui.pages.monitor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.io.IOException;

public class MonitorPage {
    private WebDriver driver;

    public MonitorPage(WebDriver driver) throws IOException {
        this.driver = driver;
        // Check that we're on the right page.
        if (!driver.findElement(By.id(UIElementMapper.getInstance().getElement("mb.tab.button.monitor.id"))).getAttribute("class").contains(UIElementMapper.getInstance().getElement("mb.tab.button.selected.class"))) {
            throw new IllegalStateException("This is not the Monitor page");
        }
    }

    public ApplicationLogsPage getApplicationLogsPage() throws IOException {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("mb.monitor.button.logs.application.button.xpath"))).click();
        return new ApplicationLogsPage(driver);
    }

    public SystemLogsPage getSystemLogsPage() throws IOException {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("mb.monitor.button.logs.system.button.xpath"))).click();
        return new SystemLogsPage(driver);
    }
}
