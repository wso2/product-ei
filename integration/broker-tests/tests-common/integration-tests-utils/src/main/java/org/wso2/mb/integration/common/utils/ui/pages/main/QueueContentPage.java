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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

/**
 * This page represents 'Queues-> Browse' page in MB management console.
 *
 */
public class QueueContentPage {

    private static final Log log = LogFactory.getLog(QueueContentPage.class);
    private WebDriver driver;

    public QueueContentPage(WebDriver driver) throws IOException {
        this.driver = driver;
        // Check that we're on the right page.
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("mb.queue.content.page.header.xpath"))).getText().contains("Queue Content")) {
            throw new IllegalStateException("This is not the Queue Content page");
        }
    }

    /**
     * Navigate to message content page related to the message on the given row index within the page. (e.g. 1st row)
     * @param listIndex
     */
    public MessageContentPage viewFullMessage(int listIndex) throws IOException {

        String xpath = UIElementMapper.getInstance().getElement("mb.queue.content.page.row.xpath").replaceAll("#REPLACE#",String.valueOf(listIndex));

        driver.findElement(By.xpath(xpath)).click();

        return new MessageContentPage(this.driver);
    }

}
