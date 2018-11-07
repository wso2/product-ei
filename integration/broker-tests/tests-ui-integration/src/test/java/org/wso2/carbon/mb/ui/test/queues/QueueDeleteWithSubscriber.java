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

package org.wso2.carbon.mb.ui.test.queues;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueuesBrowsePage;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The following UI test cases would attempt to delete a queue while subscribers were active and then once the
 * subscribers have left, the queue should be deletable.
 * JIRA - https://wso2.org/jira/browse/MB-1069
 */
public class QueueDeleteWithSubscriber extends MBIntegrationUiBaseTest {

    /**
     * Initialises the test case.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Override
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * 1. Creates a queue named 'deleteQueueWithSubscriber'.
     * 2. Subscriber starts listening to the queue.
     * 3. User tries to delete the queue. This should fail as there are active subscribers.
     * 4. Subscriber stop listening to the queue. Implying no subscribers for the queue.
     * 5. User should be able to delete the queue successfully.
     *
     * @throws IOException
     * @throws XPathExpressionException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performDeleteQueueWithSubscriberTestCase() throws IOException, XPathExpressionException,
            JMSException, NamingException, AndesClientException {

        String queueName = "deleteQueueWithSubscriber";

        // Logging into the management console
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        // Creating a new queue
        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(queueName), true);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);

        // Creating a subscriber and listens.
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        // Tries to delete the queue.
        QueuesBrowsePage queuesBrowsePage = homePage.getQueuesBrowsePage();
        Assert.assertFalse(queuesBrowsePage.deleteQueue(queueName), "Queue was deleted while subscribers were active.");

        // Removing the subscribers.
        consumerClient.stopClient();

        // Deletes the queue successfully.
        Assert.assertTrue(queuesBrowsePage.deleteQueue(queueName), "Queue could not be deleted while there were no " +
                                                                   "subscribers.");
    }

    /**
     * Stops the selenium driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
