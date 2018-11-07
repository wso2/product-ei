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

package org.wso2.mb.integration.common.utils.ui.pages.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

/**
 * The class for topic creation page. Provides functions available in the topic creation page.
 */
public class TopicAddPage extends MBPage {
    /**
     * Constructor. Takes the reference of web driver instance.
     *
     * @param driver WebDriver
     */
    protected TopicAddPage(WebDriver driver) {
        super(driver);
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance().
                getElement("mb.add.topics.page.header.xpath"))).getText().contains("Add Topic")) {
            throw new IllegalStateException("This is not the Add Topic page");
        }
    }

    /**
     * Adds a new topic.
     *
     * @param topicName The new topic name.
     * @return true if topic successfully added, false otherwise.
     */
    public boolean addTopic(String topicName) {
        boolean isSuccessful = false;

        // Setting topic name value
        WebElement topicNameField = driver.findElement(By.id(UIElementMapper.getInstance()
                                            .getElement("mb.add.topics.page.topic.name.field.id")));
        topicNameField.sendKeys(topicName);

        driver.getWindowHandle();

        // Clicking the "Add Topic" button
        driver.findElement(By.xpath(UIElementMapper.getInstance()
                                            .getElement("mb.add.topics.page.add.button.xpath")))
                .click();
        String dialog = driver.getWindowHandle();
        driver.switchTo().window(dialog);

        // Checking if valid message is prompt on the dialog
        if (driver.findElement(By.id(UIElementMapper.getInstance()
             .getElement("mb.popup.dialog.id"))).getText().contains("Topic added successfully")) {
            isSuccessful = true;
        }

        // Clicking ok button of the dialog
        driver.findElement(By.xpath(UIElementMapper.getInstance()
                                .getElement("mb.add.topic.page.ontopicadd.okbutton.xpath"))).click();

        return isSuccessful;
    }

}
