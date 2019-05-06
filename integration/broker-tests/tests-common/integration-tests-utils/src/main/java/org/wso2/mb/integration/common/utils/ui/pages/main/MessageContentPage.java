/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;

import java.io.IOException;

/**
 * This page represents 'Queues-> Browse' page in MB management console.
 */
public class MessageContentPage {

    private WebDriver driver;

    public MessageContentPage(WebDriver driver) throws IOException {
        this.driver = driver;
        // Check that we're on the right page.
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.content.page.header.xpath"))).getText().contains("Message Content")) {
            throw new IllegalStateException("This is not the Message Content page");
        }
    }

    /**
     * Get the message length displayed in the text area.
     *
     * @return Displayed message length
     */
    public int getDisplayedMessageLength() {
        return driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.message.content.textarea.xpath"))).getText().length();
    }

    /**
     * Get the message content displayed in the text area.
     *
     * @return Displayed message content
     */
    public String getDisplayedMessageContent() {
        return driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.message.content.textarea.xpath"))).getText();
    }
}
