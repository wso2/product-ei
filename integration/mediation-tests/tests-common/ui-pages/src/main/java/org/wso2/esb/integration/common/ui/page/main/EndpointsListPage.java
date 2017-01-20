/*
 * Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.esb.integration.common.ui.page.main;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.wso2.esb.integration.common.ui.page.util.UIElementMapper;

/**
 * Endpoints list page class - contains methods to test endpoints page
 */
public class EndpointsListPage {
    private static final Log log = LogFactory.getLog(EndpointsListPage.class);
    private WebDriver driver;

    public EndpointsListPage(WebDriver driver) throws IOException {
        this.driver = driver;
        // Check whether we are on the correct page.
        log.info("Endpoints list page");
        if (!driver
                .findElement(
                        By.xpath(UIElementMapper.getInstance().getElement("endpoint.page.middle.text")))
                .getText().contains("Manage Endpoints")) {
            throw new IllegalStateException("This is not the correct Endpoints List Page");
        }
    }

    public void listDynamicEndpoint() throws IOException {
        driver.findElement(
                By.xpath(UIElementMapper.getInstance().getElement("endpoint.tab.dynamicEndpoint")))
                .click();
        // test should fail if page load fails
        try {
            if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                                          .getElement("endpoint.tab.dynamicEndpoint.middle.text")))
                    .getText().contains("Dynamic Endpoints Saved in Registry")) {
                throw new IOException("Dynamic Endpoint Page loading failed");
            }
        } catch (NoSuchElementException e) {
            throw new IOException("Page loading failed", e);
        }
    }
}