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
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.wso2.mb.integration.common.utils.ui.pages.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

import java.util.List;

/**
 * The class for topic subscriptions. Provides functions available in the topic subscriptions page.
 */
public class TopicSubscriptionsPage extends MBPage {
    private static final Log log = LogFactory.getLog(QueueContentPage.class);

    /**
     * Constructor. Takes the reference of web driver instance.
     *
     * @param driver The selenium Web Driver
     */
    protected TopicSubscriptionsPage(WebDriver driver) {
        super(driver);

        // Check that we're on the right page.
        if (!driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.subscriptions.topics.page.header.xpath"))).getText().contains("Topic Subscription List")) {
            throw new IllegalStateException("This is not the Topic Subscriptions page");
        }
    }

    /**
     * Gets the number of durable active subscriptions.
     *
     * @return The number of subscriptions.
     */
    public int getDurableActiveSubscriptionsCount() {
        List<WebElement> tempDurableActiveTables = driver.findElements(By.xpath(UIElementMapper.getInstance()
                                        .getElement("mb.subscriptions.topics.page.durable.active.table.xpath")));
        // Checks whether the table exists.
        if (0 < tempDurableActiveTables.size()) {
            return tempDurableActiveTables.get(0).findElement(By.tagName("tbody")).findElements(By.tagName("tr")).size();
        } else {
            log.warn("Durable Active Subscriptions table does not exists.");
            return 0;
        }
    }

    /**
     * Gets the number of durable in-active subscriptions.
     *
     * @return The number of subscriptions.
     */
    public int getDurableInActiveSubscriptionsCount() {
        List<WebElement> tempDurableInActiveTables = driver.findElements(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.subscriptions.topics.page.durable.inactive.table.xpath")));
        // Checks whether the table exists.
        if (0 < tempDurableInActiveTables.size()) {
            return tempDurableInActiveTables.get(0).findElement(By.tagName("tbody")).findElements(By.tagName("tr")).size();
        } else {
            log.warn("Durable In-Active Subscriptions table does not exists.");
            return 0;
        }
    }

    /**
     * Search topic subscriptions according to the search criteria.
     *
     * @param queueNamePattern string pattern of the topic name (* for all)
     * @param identifierPattern string pattern of the identifier (* for all)
     * @param ownNodeIdIndex index of the node Id in the dropdown the subscriptions belong to
     * @return number of subscriptions listed under search result
     */
    public void searchTopicSubscriptions(String queueNamePattern, String identifierPattern, int
            ownNodeIdIndex, boolean isNameExactMatch, boolean isIdentifierExactMatch) {

        WebElement queueNamePatternField = driver.findElement(By.name(UIElementMapper.getInstance()
                .getElement("mb.search.topic.name.pattern.tag.name")));
        queueNamePatternField.clear();
        queueNamePatternField.sendKeys(queueNamePattern);

        WebElement queueIdentifierPatternField = driver.findElement(By.name(UIElementMapper.getInstance()
                .getElement("mb.search.topic.identifier.pattern.tag.name")));
        queueIdentifierPatternField.clear();
        queueIdentifierPatternField.sendKeys(identifierPattern);

        WebElement topicNameExactMatchField = driver.findElement(
                By.name(UIElementMapper.getInstance().getElement("mb.search.topic.name.exactmatch.tag.name")));
        // Set the name exact match check box state based on the test input
        if (isNameExactMatch != topicNameExactMatchField.isSelected()) {
            topicNameExactMatchField.click();
        }
        WebElement topicIdentifierExactMatchField = driver.findElement(
                By.name(UIElementMapper.getInstance().getElement("mb.search.topic.identifier.exactmatch.tag.name")));
        // Set the identifier exact match check box state based on the test input
        if (isIdentifierExactMatch != topicIdentifierExactMatchField.isSelected()) {
            topicIdentifierExactMatchField.click();
        }

        Select ownNodeIdDropdown = new Select(driver.findElement(By.id(UIElementMapper.getInstance()
                .getElement("mb.search.topic.own.node.id.element.id"))));
        ownNodeIdDropdown.selectByIndex(ownNodeIdIndex);

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.search.topic.search.button.xpath"))).click();

    }

    /**
     * Gets the number of temporary active subscriptions.
     *
     * @return The number of subscriptions.
     */
    public int getNonDurableSubscriptionsCount() {
        int numberOfSubscribers = 0;
        List<WebElement> tempNonDurableActiveTables = driver.findElements(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.subscriptions.topics.page.temporary.table.xpath")));
        // Checks whether the table exists.
        if (0 < tempNonDurableActiveTables.size()) {
            for (WebElement tempNonDurableActiveTable : tempNonDurableActiveTables) {
                if ("table".equals(tempNonDurableActiveTable.getTagName())) {
                    numberOfSubscribers = tempNonDurableActiveTable.findElement(By.tagName("tbody")).findElements(By
                            .tagName("tr")).size();
                }
            }

        }

        if (numberOfSubscribers == 0) {
            log.warn("Durable Active Subscriptions table does not exists.");
        }
        return numberOfSubscribers;
    }

    /**
     * Forcibly close non durable topic subscription. This will delete the first subscription listed
     * on non durable subscriptions list
     * @return true if subscription removal is successful, otherwise false
     */
    public boolean closeNonDurableTopicSubscription() {
        String deletingMessageID = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.tempTopic.subscriptions.table.delete.subid"))).getText();

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.tempTopic.subscriptions.table.delete.button"))).click();

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.tempTopic.subscriptions.close.confirm"))).click();
        boolean successMessageReceived = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.tempTopic.subscription.close.result"))).getText()
                .contains("Successfully closed subscription");

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.tempTopic.subscription.close.result.confirm"))).click();

        boolean queueSubscriptionSuccessfullyRemoved = false;

        String firstSubscriptionIDAfterDelete = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.tempTopic.subscriptions.table.delete.subid"))).getText();

        if(!(firstSubscriptionIDAfterDelete.equals(deletingMessageID)) && successMessageReceived) {
            queueSubscriptionSuccessfullyRemoved = true;
        }

        return queueSubscriptionSuccessfullyRemoved;
    }

    /**
     * Forcibly close non durable topic subscription. This will delete the first subscription listed
     * on non durable subscriptions list. This will also check if subscription has moved to inactive
     * state when closed
     * @return true if subscription removal is successful, otherwise false
     */
    public boolean closeDurableTopicSubscription() {
        String deletingMessageID = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscriptions.table.delete.subid"))).getText();

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscriptions.table.delete.button"))).click();

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscriptions.close.confirm"))).click();
        boolean successMessageReceived = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscription.close.result"))).getText()
                .contains("Successfully closed subscription");

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscription.close.result.confirm"))).click();

        boolean queueSubscriptionSuccessfullyRemoved = false;

        String firstSubscriptionIDAfterDelete = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscriptions.table.delete.subid"))).getText();

        String firstInactiveSubID = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.durableTopic.subscription.close.inactive.subid"))).getText();

        if(!(firstSubscriptionIDAfterDelete.equals(deletingMessageID))
                && deletingMessageID.equals(firstInactiveSubID)
                && successMessageReceived) {

            queueSubscriptionSuccessfullyRemoved = true;
        }

        return queueSubscriptionSuccessfullyRemoved;
    }
}
