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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.MBPage;

/**
 * UI test class related to queue subscriptions page of Management console.
 */
public class QueueSubscriptionsPage extends MBPage{

    private static Log log = LogFactory.getLog(QueueSubscriptionsPage.class);

    /**
     * Checks whether the current page the WebDriver is in is the correct queue subscriptions page.
     * if not throws a runtime exception (IllegalStateException)
     *
     * @param driver WebDriver
     */
    protected QueueSubscriptionsPage(WebDriver driver) {
        super(driver);
        if(!driver.findElement(By.xpath(UIElementMapper.getInstance().
                getElement("mb.queue.manage.subscriptions.page.xpath"))).getText().contains("Queue Subscription List")){
            throw new IllegalStateException("This is not the Queue Subscriptions page");
        }
    }

    /**
     * Search queue subscriptions according to the search criteria.
     *
     * @param queueNamePattern string pattern of the queue name (* for all)
     * @param identifierPattern string pattern of the identifier (* for all)
     * @param ownNodeIdIndex index of the node Id in the dropdown the subscriptions belong to
     * @return number of subscriptions listed under search result
     */
    public int searchQueueSubscriptions(String queueNamePattern, String identifierPattern, int ownNodeIdIndex,
                                        boolean isNameExactMatch, boolean isIdentifierExactMatch) {

        WebElement queueNamePatternField = driver.findElement(By.name(UIElementMapper.getInstance()
                .getElement("mb.search.queue.name.pattern.tag.name")));
        queueNamePatternField.clear();
        queueNamePatternField.sendKeys(queueNamePattern);

        WebElement queueNameExactMatchField = driver.findElement(
                By.name(UIElementMapper.getInstance().getElement("mb.search.queue.name.exactmatch.tag.name")));
        // Set the name exact match check box state based on the test input
        if (isNameExactMatch != queueNameExactMatchField.isSelected()) {
            queueNameExactMatchField.click();
        }
        WebElement queueIdentifierExactMatchField = driver.findElement(
                By.name(UIElementMapper.getInstance().getElement("mb.search.queue.identifier.exactmatch.tag.name")));
        // Set the identifier exact match check box state based on the test input
        if (isIdentifierExactMatch != queueIdentifierExactMatchField.isSelected()) {
            queueIdentifierExactMatchField.click();
        }

        WebElement queueIdentifierPatternField = driver.findElement(By.name(UIElementMapper.getInstance()
                .getElement("mb.search.queue.identifier.pattern.tag.name")));
        queueIdentifierPatternField.clear();
        queueIdentifierPatternField.sendKeys(identifierPattern);

        Select ownNodeIdDropdown = new Select(driver.findElement(By.id(UIElementMapper.getInstance()
                .getElement("mb.search.queue.own.node.id.element.id"))));
        ownNodeIdDropdown.selectByIndex(ownNodeIdIndex);

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.search.queue.search.button.xpath"))).click();

        return getSubscriptionCount();

    }

    /**
     * Gets the number of queue subscriptions.
     *
     * @return the number of subscriptions.
     */
    public int getSubscriptionCount() {
        int numberOfSubscribers = 0;

        WebElement subscriptionTable = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.subscriptions.queue.table.xpath")));
        // Checks whether the table exists.
        if ("table".equals(subscriptionTable.getTagName())) {
            numberOfSubscribers = subscriptionTable.findElement(By.tagName("tbody")).findElements(By
                    .tagName("tr")).size();
        }
        if (numberOfSubscribers == 0) {
            log.warn("Queue Subscriptions table does not exists.");
        }
        return numberOfSubscribers;
    }

    /**
     * Close the first subscription out of the subscriptions listed on queue subscriptions page
     *
     * @return true if successful and false otherwise
     */
    public boolean closeTopSubscription() {

        String deletingMessageID = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.subscriptions.table.delete.subid"))).getText();

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.subscriptions.table.delete.button"))).click();

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.subscriptions.close.confirm"))).click();
        boolean successMessageReceived = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.subscription.close.result"))).getText()
                .contains("Successfully closed subscription");

        driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.subscription.close.result.confirm"))).click();

        boolean queueSubscriptionSuccessfullyRemoved = false;

        String firstSubscriptionIDAfterDelete = driver.findElement(By.xpath(UIElementMapper.getInstance()
                .getElement("mb.queue.subscriptions.table.delete.subid"))).getText();

        if(!(firstSubscriptionIDAfterDelete.equals(deletingMessageID)) && successMessageReceived) {
            queueSubscriptionSuccessfullyRemoved = true;
        }

        return queueSubscriptionSuccessfullyRemoved;

    }
}
