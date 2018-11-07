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
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
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
 * This tests the deletion of a queue from management console
 */
public class QueueDeleteTestCase extends MBIntegrationUiBaseTest {

    /**
     * Initializes test case.
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * Tests the queue deletion from UI
     * <p/>
     * Test Steps:
     * - login to management console
     * - create a queue
     * - Go to queue browse page
     * - Delete console
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test()
    public void testCase() throws XPathExpressionException, IOException {

        String qName = "testQ";
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(qName), true);

        QueuesBrowsePage queuesBrowsePage = homePage.getQueuesBrowsePage();
        queuesBrowsePage.deleteQueue(qName);
        queuesBrowsePage = homePage.getQueuesBrowsePage();

        Assert.assertTrue(!queuesBrowsePage.isQueuePresent(qName));

        logout();
    }

    /**
     * 1. Creates a queue.
     * 2. Publish messages to the queue.
     * 3. Delete the queue.
     * 4. Check if queue exists still without refreshing the page.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     * @throws AndesClientConfigurationException
     */
    @Test()
    public void performPublishDeleteCheck() throws XPathExpressionException, IOException, JMSException,
            NamingException, AndesClientException, AndesClientConfigurationException {
        String queueName = "Delete-queue-ui";

        // Logging into management console
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        // Adding the queue
        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(queueName), true);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(1000);
        publisherConfig.setPrintsPerMessageCount(100L);

        // Publishing messages
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        // Delete queue
        QueuesBrowsePage queuesBrowsePage = homePage.getQueuesBrowsePage();
        queuesBrowsePage.deleteQueue(queueName);
        queuesBrowsePage = homePage.getQueuesBrowsePage();

        Assert.assertTrue(!queuesBrowsePage.isQueuePresent(queueName));
        // Logout
        logout();
    }

    /**
     * Shuts down selenium web driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }

}
