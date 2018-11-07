/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.integration.common.utils.ui.pages.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.io.IOException;

/**
 * This page represents 'Dead Letter Channel -> Browse' page in MB management console.
 */
public class DLCBrowsePage {
    private static final Log log = LogFactory.getLog(DLCBrowsePage.class);
    private WebDriver driver;

    /**
     * Retrieve Page consists DeadLetterChannel
     *
     * @param driver selenium web driver used to run the test
     * @throws IOException if mapper.properties file not found
     */
    public DLCBrowsePage(WebDriver driver) throws IOException {
        this.driver = driver;
        // Check that we're on the right page.
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                                                 .getElement("home.dlc.header.xpath"))).getText()
                .contains("Dead Letter Channel")) {
            throw new IllegalStateException("This is not the DLC page");
        }
    }

    /**
     * Browse for content of DeadLetter Channel
     * Retrieve 'Dead Letter Channel -> Browse -> Queue Content' page
     *
     * @return Content of DeadLetter Channel
     * @throws IOException if mapper.properties file not found
     */
    public DLCContentPage getDLCContent() throws IOException {
        driver.findElement(By.xpath(UIElementMapper.getInstance()
                                            .getElement("mb.dlc.browse.table.browse.button.xpath"))).click();
        return new DLCContentPage(driver);
    }

    /**
     * Check whether dead letter channel created or not
     *
     * @return true when dead letter channel created
     */
    public boolean isDLCCreated() {
        boolean isDLCCreated = false;
        WebElement queueTable;
        queueTable = driver.findElement(By.xpath(UIElementMapper.getInstance()
                                                         .getElement("mb.dlc.browse.table.xpath")));
        if (queueTable != null) {
            isDLCCreated = true;
        }
        return isDLCCreated;
    }
}
