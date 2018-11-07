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

package org.wso2.carbon.mb.ui.test.topic;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicSubscriptionsPage;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The following class contains test cases related to durable topics with shared subscriptions.
 */
public class SharedDurableSubscriptionTestCase extends MBIntegrationUiBaseTest {

    /**
     * Initializes the test case and modifying broker.xml to allow shared subscriptions.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void initialize() throws Exception {
        super.init();

        super.serverManager = new ServerConfigurationManager(mbServer);
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() + File.separator + "repository" +
                                            File.separator + "conf" + File.separator + "broker.xml";
        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);
        // Changing "allowSharedSubscription" value to "true" in broker.xml
        configurationEditor.updateProperty(AndesConfiguration.ALLOW_SHARED_SHARED_SUBSCRIBERS, "true");
        // Restarting server
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * With "allowSharedSubscriptions" on following steps are done :
     * 1. Created a durable subscription with ID  "client-id-shared-1" and check whether UI is correct.
     * 2. Disconnect the subscriber and check whether UI is correct.
     * 3. Created 3 durable subscription with ID  "client-id-shared-1" and check whether UI is correct.
     * 4. Disconnect the subscribers and check whether UI is correct.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performSharedDurableTopicTestCase() throws Exception {

        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());
        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        int durableActiveSubscriptionsCountBeforeTest = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        int durableInActiveSubscriptionsCountBeforeTest = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "sharedDurableTopic1");
        consumerConfig.setDurable(true, "client-id-shared-1");

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        int durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        int durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 1,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest,
                "Inactive subscription list had modified.");

        consumerClient.stopClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest,
                "Subscription has been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest + 1,
                "Inactive subscription list has not updated.");

        // Creating clients
        consumerClient = new AndesClient(consumerConfig, 3, true);
        consumerClient.startClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 3,
                "Subscriptions(3) has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest,
                "Inactive subscription list has modified.");

        consumerClient.stopClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest,
                "Subscription has been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest + 1,
                "Inactive subscription list has not updated.");

        homePage.logout();

    }

    /**
     * With "allowSharedSubscriptions" on following steps are done :
     * 1. Created 3 durable subscription with ID "client-id-shared-4" to topic "sharedDurableTopic4" and check whether
     * UI is correct.
     * 2. Created 3 durable subscription with ID "client-id-shared-5" to topic "sharedDurableTopic4" and check whether
     * UI is correct.
     * 3. Disconnect the subscribers with "client-id-shared-4" client ID and check whether UI is correct.
     * 4. Disconnect the subscribers with "client-id-shared-5" client ID and check whether UI is correct.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performSharedSameDurableTopicTestCase() throws Exception {

        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());
        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        int durableActiveSubscriptionsCountBeforeTest = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        int durableInActiveSubscriptionsCountBeforeTest = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "sharedDurableTopic4");
        consumerConfig1.setDurable(true, "client-id-shared-4");

        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "sharedDurableTopic4");
        consumerConfig2.setDurable(true, "client-id-shared-5");

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, 3, true);
        consumerClient1.startClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        int durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        int durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 3,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest,
                "Inactive subscription list had modified.");

        // Creating clients
        AndesClient consumerClient2 = new AndesClient(consumerConfig2, 3, true);
        consumerClient2.startClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 6,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest,
                "Inactive subscription list had modified.");

        consumerClient1.stopClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 3,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest + 1,
                "Inactive subscription list had modified.");

        consumerClient2.stopClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest + 2,
                "Inactive subscription list had modified.");

        homePage.logout();
    }

    /**
     * With "allowSharedSubscriptions" on following steps are done :
     * 1. Created 3 durable subscription with ID "client-id-shared-2" to topic "sharedDurableTopic2" and check whether
     * UI is correct.
     * 2. Created 3 durable subscription with ID "client-id-shared-3" to topic "sharedDurableTopic3" and check whether
     * UI is correct.
     * 3. Disconnect the subscribers with "client-id-shared-2" client ID and check whether UI is correct.
     * 4. Disconnect the subscribers with "client-id-shared-3" client ID and check whether UI is correct.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performSharedMultipleDurableTopicTestCase() throws Exception {

        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());
        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        int durableActiveSubscriptionsCountBeforeTest = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        int durableInActiveSubscriptionsCountBeforeTest = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        // Creating a JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "sharedDurableTopic2");
        consumerConfig1.setDurable(true, "client-id-shared-2");

        AndesJMSConsumerClientConfiguration consumerConfig2 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "sharedDurableTopic3");
        consumerConfig2.setDurable(true, "client-id-shared-3");

        // Creating clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, 3, true);
        consumerClient1.startClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        int durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        int durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 3,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest,
                "Inactive subscription list had modified.");

        // Creating clients
        AndesClient consumerClient2 = new AndesClient(consumerConfig2, 3, true);
        consumerClient2.startClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 6,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest,
                "Inactive subscription list had modified.");

        consumerClient1.stopClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest + 3,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest + 1,
                "Inactive subscription list had modified.");

        consumerClient2.stopClient();

        AndesClientUtils.sleepForInterval(3000);

        topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        durableActiveSubscriptionsCount = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        durableInActiveSubscriptionsCount = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();

        Assert.assertEquals(durableActiveSubscriptionsCount, durableActiveSubscriptionsCountBeforeTest,
                "Subscription has not been added to Active list");
        Assert.assertEquals(durableInActiveSubscriptionsCount, durableInActiveSubscriptionsCountBeforeTest + 2,
                "Inactive subscription list had modified.");

        homePage.logout();
    }

    /**
     * Shuts down the selenium web driver.
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException {
        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);
        driver.quit();
    }
}
