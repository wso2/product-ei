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
 * KIND, either express or implied. See the License for the
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

/**
 * Test case to browse the queue with a user with a domain name.
 * User will have a domain name attached to the user name
 * eg: WSO2/admin
 */
public class BrowseQueueWithUserWithDomainName extends MBIntegrationUiBaseTest {

    /**
     * Initializes test case and starts up MB server with WSO2 domain name.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws IOException
     */
    @BeforeClass()
    public void initialize() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
        restartServerWithDomainName();  // start with WSO2 domain name
    }

    /**
     * This test case will add a queue to MB and navigate to browse the queue
     * content.
     *
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Test()
    public void navigateQueueContentPage() throws IOException, XPathExpressionException {

        String queueName = "testQcontent";
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(mbServer.getContextTenant().getContextUser().getUserName(),
                mbServer.getContextTenant().getContextUser().getPassword());

        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(queueName), true);
        QueuesBrowsePage queuesBrowsePage = homePage.getQueuesBrowsePage();
        Assert.assertNotNull(queuesBrowsePage.browseQueue(queueName), "Unable to browse Queue " + queueName);
        Assert.assertEquals(homePage.getQueuesBrowsePage().deleteQueue(queueName), true, "Unable to delete the queue " +
                                                                                         queueName + " after browsing");
    }

    /**
     * Shuts down selenium driver and restored earlier configuration.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException {
        driver.quit();
        restartInPreviousConfiguration();
    }
}
