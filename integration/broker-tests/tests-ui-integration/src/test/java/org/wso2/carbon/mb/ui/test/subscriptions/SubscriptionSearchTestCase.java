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

package org.wso2.carbon.mb.ui.test.subscriptions;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueSubscriptionsPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicSubscriptionsPage;

import java.io.IOException;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;

/**
 * This class test the search functionality of queue and topic subscription pages.
 */
public class SubscriptionSearchTestCase extends MBIntegrationUiBaseTest {
    /**
     * Keeps homepage page to navigate to the other pages
     */
    HomePage homePage;

    /**
     * Initialises the test case.
     *
     * @throws org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws java.net.MalformedURLException
     */
    @Override
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
        //Log into broker
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());
    }

    /**
     * Following test cases are done in this test case.
     * 1. Create three queue subscribers for three different queues.
     * 2. Search subscribers using the queue name pattern, identifier and own node Id
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "queue"}, priority = 0)
    public void performQueueSubscriptionSearchTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, CloneNotSupportedException, AndesClientException, JMSException, IOException,
            NamingException {
        String queueName = "subSearchQueue1";
        long expectedMessageCount = 1000L;
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 10L);
        consumerConfig1.setRunningDelay(200);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setDestinationName("subSearchQueue2");

        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();
        consumerConfig3.setDestinationName("subSearchQueue3");

        // Creating consumer clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        QueueSubscriptionsPage queueSubscriptionsPage = homePage.getQueueSubscriptionsPage();
        int result = queueSubscriptionsPage.searchQueueSubscriptions("1", "", 0, false, false);
        Assert.assertEquals(result, 1);

        //Test exact match of queue name
        result = queueSubscriptionsPage.searchQueueSubscriptions("subSearchQueue3", "", 0, true, false);
        Assert.assertEquals(result, 1);

        result = queueSubscriptionsPage.searchQueueSubscriptions("SearchQueue", "", 0, false, false);
        Assert.assertEquals(result, 3);

        consumerClient1.stopClient();
        consumerClient2.stopClient();
        consumerClient3.stopClient();
    }

    /**
     * Following test cases are done in this test case.
     * 1. Create three topic subscribers for three different topics.
     * 2. Search subscribers using the topic name pattern, identifier and own node Id
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "topic"}, priority = 1)
    public void performTemporaryTopicSubscriptionSearchTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, CloneNotSupportedException, AndesClientException, JMSException, IOException,
            NamingException {
        String topicName = "subSearchTopic1";
        long expectedMessageCount = 1000L;
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 10L);
        consumerConfig1.setRunningDelay(200);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setDestinationName("subSearchTopic2");

        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();
        consumerConfig3.setDestinationName("subSearchTopic3");

        // Creating consumer clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        topicSubscriptionsPage.searchTopicSubscriptions("1", "", 0, false, false);
        int result = topicSubscriptionsPage.getNonDurableSubscriptionsCount();
        Assert.assertEquals(result, 1);

        topicSubscriptionsPage.searchTopicSubscriptions("subSearchTopic2", "", 0, true, false);
        result = topicSubscriptionsPage.getNonDurableSubscriptionsCount();
        Assert.assertEquals(result, 1);

        topicSubscriptionsPage.searchTopicSubscriptions("SearchTopic", "", 0, false, false);
        result = topicSubscriptionsPage.getNonDurableSubscriptionsCount();
        Assert.assertEquals(result, 3);

        consumerClient1.stopClient();
        consumerClient2.stopClient();
        consumerClient3.stopClient();
    }

    /**
     * Following test cases are done in this test case.
     * 1. Create three durable topic subscribers for three different topics.
     * 2. Search active subscribers using the queue name pattern, identifier and own node Id
     * 3. Stop the subscribers
     * 4. Search inactive subscribers using the queue name pattern, identifier and own node Id
     *
     * @throws AndesClientConfigurationException
     * @throws XPathExpressionException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     * @throws JMSException
     * @throws IOException
     * @throws NamingException
     */
    @Test(groups = {"wso2.mb", "durable"}, priority = 2)
    public void performDurableTopicSubscriptionSearchTestCase() throws AndesClientConfigurationException,
            XPathExpressionException, CloneNotSupportedException, AndesClientException, JMSException, IOException,
            NamingException {
        String topicName = "subSearchDurable1";
        long expectedMessageCount = 1000L;
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 10L);
        consumerConfig1.setRunningDelay(200);
        consumerConfig1.setAsync(false);
        consumerConfig1.setDurable(true, "subSearchDurable1SubId" );

        //Create two more JMS consumer configurations
        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setDestinationName("subSearchDurable2");
        consumerConfig1.setDurable(true, "subSearchDurable2SubId" );

        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();
        consumerConfig3.setDestinationName("subSearchDurable3");
        consumerConfig1.setDurable(true, "subSearchDurable3SubId");

        // Creating consumer clients and starts the clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        //Go to topic subscriptions page and search with different criteria.
        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        topicSubscriptionsPage.searchTopicSubscriptions("1", "", 0, false, false);
        int result = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        Assert.assertEquals(result, 1);

        topicSubscriptionsPage.searchTopicSubscriptions("SearchDurable", "", 0, false, false);
        result = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        Assert.assertEquals(result, 3);

        // test exact match of topic name.
        topicSubscriptionsPage.searchTopicSubscriptions("subSearchDurable3", "", 0, true, false);
        result = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        Assert.assertEquals(result, 1);

        //Stop the clients
        consumerClient1.stopClient();
        consumerClient2.stopClient();
        consumerClient3.stopClient();

        //Go to topic subscriptions page and search with different criteria.
        topicSubscriptionsPage.searchTopicSubscriptions("", "", 0, false, false);
        result = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        Assert.assertEquals(result, 0);

        topicSubscriptionsPage.searchTopicSubscriptions("subSearchDurable", "", 0, false, false);
        result = topicSubscriptionsPage.getDurableInActiveSubscriptionsCount();
        Assert.assertEquals(result, 3);
        result = topicSubscriptionsPage.getDurableActiveSubscriptionsCount();
        Assert.assertEquals(result, 0);
    }

    /**
     * Shuts down the selenium web driver.
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException {
        driver.quit();
    }
}

