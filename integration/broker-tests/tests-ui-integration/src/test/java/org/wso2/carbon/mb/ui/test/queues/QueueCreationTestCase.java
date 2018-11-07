/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueuesBrowsePage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * This tests the creation of a queue from management console
 */
public class QueueCreationTestCase extends MBIntegrationUiBaseTest {

    /**
     * Initializes the test case.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * Tests the queue creation functionality from UI
     * <p/>
     * Test Steps:
     * - Login to management console
     * - Create a queue
     * - Go to queue browse page
     *
     * -Create a queue with colon symbol
     * -Go to queue browse page
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Test()
    public void queueCreationTestCase() throws IOException, XPathExpressionException {

        String qName = "QueueCreationTestCase";
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(qName), true);
        QueuesBrowsePage queuesBrowsePage = homePage.getQueuesBrowsePage();
        Assert.assertEquals(queuesBrowsePage.isQueuePresent(qName), true);

        qName = "QueueCreationTestCase:QueueCreationTestCase";

        queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(qName), true);
        queuesBrowsePage = homePage.getQueuesBrowsePage();
        Assert.assertEquals(queuesBrowsePage.isQueuePresent(qName), true);

    }

    /**
     * Shuts down the selenium web driver
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
