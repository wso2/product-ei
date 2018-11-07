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

package org.wso2.carbon.mb.ui.test.topic;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicsBrowsePage;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * This tests the creation of a topic from management console
 */
public class TopicCreationTestCase extends MBIntegrationUiBaseTest {

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
     * Tests the topic creation from UI with colon sign for the name
     * <p/>
     * Test Steps:
     * - Login to management console
     * - Create a topic
     * - Go to topic browse page
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test()
    public void testCase() throws XPathExpressionException, IOException {

        String topicName = "TopicCreationTestTopic";
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        TopicAddPage topicAddPage = homePage.getTopicAddPage();
        Assert.assertEquals(topicAddPage.addTopic(topicName), true);
        TopicsBrowsePage topicBrowsePage = homePage.getTopicsBrowsePage();
        Assert.assertEquals(topicBrowsePage.isTopicPresent(topicName), true);
    }

    /**
     * Shuts down the selenium web driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
