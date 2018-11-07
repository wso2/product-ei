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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

import java.io.IOException;

/**
 * Home page class holds the information of product page you got once login
 * NOTE: To navigate to a page Don't use direct links to pages. To ensure there is a UI element to navigate to
 * that page.
 */
public class HomePage extends MBPage {

    /**
     * Checks whether the current page is the home page. if not throws a runtime exception
     *
     * @param driver WebDriver
     */
    public HomePage(WebDriver driver) {
        super(driver);
        // Check that we're on the right page.
        if (!driver.findElement(By.id(
                UIElementMapper.getInstance().getElement("home.dashboard.middle.text"))).getText()
                                                                                .contains("Home")) {
            throw new IllegalStateException("This is not the home page");
        }
    }

    /**
     * Clicks on the dead letter channel browsing page.
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.DLCBrowsePage}
     * @throws IOException
     */
    public DLCBrowsePage getDLCBrowsePage() throws IOException {
        this.clickOnMenuItem("home.mb.dlc.browse.xpath");
        return new DLCBrowsePage(driver);
    }

    /**
     * Clicks on the queue browsing page.
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.QueuesBrowsePage}
     * @throws IOException
     */
    public QueuesBrowsePage getQueuesBrowsePage() throws IOException {
        this.clickOnMenuItem("home.mb.queues.browse.xpath");
        return new QueuesBrowsePage(driver);
    }

    /**
     * Clicks on the queue browsing page.
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.QueueSubscriptionsPage}
     * @throws IOException
     */
    public QueueSubscriptionsPage getQueueSubscriptionsPage() throws IOException {
        this.clickOnMenuItem("home.mb.queues.subscriptions.xpath");
        return new QueueSubscriptionsPage(driver);
    }

    /**
     * Clicks on the queue adding page.
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage}
     * @throws IOException
     */
    public QueueAddPage getQueueAddPage() throws IOException {
        this.clickOnMenuItem("home.mb.queues.add.xpath");
        return new QueueAddPage(driver);
    }

    /**
     * Clicks on the topic adding page.
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.TopicAddPage}
     * @throws IOException
     */
    public TopicAddPage getTopicAddPage() throws IOException {
        this.clickOnMenuItem("home.mb.topics.add.xpath");
        return new TopicAddPage(driver);
    }

    /**
     * Click on the topic browsing page
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.TopicsBrowsePage}
     * @throws IOException
     */
    public TopicsBrowsePage getTopicsBrowsePage() throws IOException {
        this.clickOnMenuItem("home.mb.topics.browse.xpath");
        return new TopicsBrowsePage(driver);
    }

    /**
     * Clicks on the topic adding page
     *
     * @param xPathForTopicMenuItem The xPath of the topic adding page.
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.TopicAddPage}
     */
    public TopicAddPage getTopicAddPage(String xPathForTopicMenuItem) {
        this.clickOnMenuItem(xPathForTopicMenuItem);
        return new TopicAddPage(driver);
    }

    /**
     * Click on the topic browsing page
     *
     * @param xPathForTopicMenuItem The xPath of the topic browsing page.
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.TopicsBrowsePage}
     * @throws IOException
     */
    public TopicsBrowsePage getTopicsBrowsePage(String xPathForTopicMenuItem) throws IOException {
        this.clickOnMenuItem(xPathForTopicMenuItem);
        return new TopicsBrowsePage(driver);
    }

    /**
     * Click on the topic subscriptions page.
     *
     * @return A {@link org.wso2.mb.integration.common.utils.ui.pages.main.TopicSubscriptionsPage}
     * @throws IOException
     */
    public TopicSubscriptionsPage getTopicSubscriptionsPage() throws IOException {
        this.clickOnMenuItem("home.mb.topic.subscriptions.xpath");
        return new TopicSubscriptionsPage(driver);
    }

    /**
     * Clicks on a menu item with a given xPath
     *
     * @param xPathForMenuItem The xPath for the menu item.
     */
    private void clickOnMenuItem(String xPathForMenuItem) {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement(xPathForMenuItem)))
                .click();
    }
}
