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
import org.openqa.selenium.WebElement;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

import java.util.List;

/**
 * The class for topic content browsing page. Provides functions available in the topic browsing
 * page.
 */
public class TopicsBrowsePage extends MBPage {
    /**
     * Constructor. Takes the reference of web driver instance.
     *
     * @param driver WebDriver
     */
    protected TopicsBrowsePage(WebDriver driver) {
        super(driver);
        // Check that we're on the right page.
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                                                 .getElement("mb.topic.browse.page.header.xpath")))
                                                            .getText().contains("Topic List")) {
            throw new IllegalStateException("This is not the Topic List page");
        }
    }

    /**
     * Validates whether a give topic exists.
     *
     * @param topicName The topic name
     * @return true if topic is available, false otherwise.
     */
    public boolean isTopicPresent(String topicName) {
        boolean isTopicPresent = false;

        // Gets the topic tree element
        WebElement topicTree = driver.findElement(By.xpath(UIElementMapper.getInstance()
                                                        .getElement("mb.topic.browse.topictree")));

        // Gets all 'ul' elements.
        List<WebElement> ulList = topicTree.findElements(By.tagName("ul"));
        for (WebElement ulNode : ulList) {
            // Gets all 'li' elements
            List<WebElement> liList = ulNode.findElements(By.tagName("li"));
            for (WebElement liNode : liList) {
                // Gets the element which has the topic name
                WebElement topicNameNode = liNode.findElement(By.className("treeNode"));
                if (topicName.toLowerCase().equals(topicNameNode.getText())) {
                    isTopicPresent = true;
                }
            }

        }
        return isTopicPresent;
    }
}
