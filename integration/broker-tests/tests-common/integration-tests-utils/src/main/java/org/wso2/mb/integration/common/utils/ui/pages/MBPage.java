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

package org.wso2.mb.integration.common.utils.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.configure.ConfigurePage;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.monitor.MonitorPage;

import java.io.IOException;

/**
 * The base class for all the pages. Navigation functions that can be done from any page should
 * be implemented within this class (to avoid duplication of common functionality throughout
 * pages).
 */
public abstract class MBPage {

    /**
     * Web driver used by selenium framework to do UI operations pragmatically
     */
    protected WebDriver driver;

    /**
     * Constructor. Takes the reference of web driver instance.
     *
     * @param driver The selenium Web Driver
     */
    protected MBPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Selects a Configuration tab and returns the configuration tab related page
     *
     * @return ConfigurePage The configuration page.
     * @throws IOException
     */
    public ConfigurePage getConfigurePage() throws IOException {
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("configure.tab.id"))).click();
        return new ConfigurePage(driver);
    }

    /**
     * Selects the Monitor tab and returns the monitor tab related page
     *
     * @return MonitorPage The monitoring page.
     * @throws IOException
     */
    public MonitorPage getMonitorPage() throws IOException {
        driver.findElement(By.id(UIElementMapper.getInstance().getElement("mb.tab.button.monitor.id"))).click();
        return new MonitorPage(driver);
    }

    /**
     * Log out from current account and returns the LoginPage
     *
     * @return LoginPage The login page.
     * @throws IOException
     */
    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("home.mb.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }
}
