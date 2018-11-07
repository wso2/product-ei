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

package org.wso2.carbon.mb.ui.test.subscriptions;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueSubscriptionsPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.TopicSubscriptionsPage;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Following UI test is to test if close subscription button works for queue subscriptions
 */
public class SubscriptionDeleteTestCase extends MBIntegrationUiBaseTest {

    /**
     * Keeps homepage page to navigate to the other pages
     */
    HomePage homePage;

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

        //log into broker
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());
    }


    /**
     * 1. Create 3 queue subscribers for queue "subDeleteQueue"
     * 2. Log into UI and close one subscriptions
     * 3. Verify subscription close is successful and only 2 other subscription entries are shown
     * 4. Verify one of subscribers does not get new messages
     * @throws IOException
     * @throws XPathExpressionException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue"})
    public void performQueueSubscriptionCloseTestCase() throws IOException, XPathExpressionException,
            JMSException, NamingException, AndesClientException, AndesClientConfigurationException,
            CloneNotSupportedException {

        String queueName = "subDeleteQueue";
        long expectedMessageCount = 1000L;
        long messageCountToSend = 1000L;
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 10L);
        consumerConfig1.setRunningDelay(200);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();

        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        publisherConfig.setPrintsPerMessageCount(messageCountToSend / 10L);
        publisherConfig.setNumberOfMessagesToSend(messageCountToSend);

        // Creating consumer clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        //start publisher client
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        //Wait until some messages receive
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //ignore
        }

        //verify all subscribers got at least one message
        boolean allSubscribersGotMessages = false;
        if(consumerClient1.getReceivedMessageCount() > 0
                && consumerClient2.getReceivedMessageCount() > 0
                && consumerClient3.getReceivedMessageCount() > 0) {
            allSubscribersGotMessages = true;
        }

        Assert.assertEquals(allSubscribersGotMessages, true, "All queue subscribers are not receiving messages");

        QueueSubscriptionsPage queueSubscriptionsPage = homePage.getQueueSubscriptionsPage();
        boolean result = queueSubscriptionsPage.closeTopSubscription();

        Assert.assertEquals(result, true, "Closing queue subscriber was not successful");

        //verify one of the subscribers now does not get new messages

        long messageCountSub1 = consumerClient1.getReceivedMessageCount();
        long messageCountSub2 = consumerClient2.getReceivedMessageCount();
        long messageCountSub3 = consumerClient3.getReceivedMessageCount();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //ignore
        }

        long messageCountNewSub1 = consumerClient1.getReceivedMessageCount();
        long messageCountNewSub2 = consumerClient2.getReceivedMessageCount();
        long messageCountNewSub3 = consumerClient3.getReceivedMessageCount();

        boolean oneReceiverIsStopped = false;

        if(messageCountNewSub1 == messageCountSub1) {
            oneReceiverIsStopped = true;
        } else if(messageCountNewSub2 == messageCountSub2) {
            oneReceiverIsStopped = true;
        } else if(messageCountNewSub3 == messageCountSub3) {
            oneReceiverIsStopped = true;
        }

        Assert.assertEquals(oneReceiverIsStopped, true, "None of the queue subscribers stopped even if"
                + "subscriber is stopped via UI ");

        //close all subscriptions
        consumerClient1.stopClient();
        consumerClient2.stopClient();
        consumerClient3.stopClient();
    }


    /**
     * 1. Create 3 topic subscribers for topic "subDeleteTopic"
     * 2. Log into UI and close one subscriptions
     * 3. Verify subscription close is successful and only 2 other subscription entries are shown
     * 4. Verify one of subscribers does not get new messages
     * @throws IOException
     * @throws XPathExpressionException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicSubscriptionCloseTestCase() throws IOException, XPathExpressionException,
            JMSException, NamingException, AndesClientException, AndesClientConfigurationException,
            CloneNotSupportedException {

        String topicName = "subDeleteTopic";
        long expectedMessageCount = 1000L;
        long messageCountToSend = 1000L;
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 10L);
        consumerConfig1.setRunningDelay(200);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();

        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        publisherConfig.setPrintsPerMessageCount(messageCountToSend / 10L);
        publisherConfig.setNumberOfMessagesToSend(messageCountToSend);

        // Creating consumer clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        //start publisher client
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        //Wait until some messages receive
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //ignore
        }

        //verify all subscribers got at least one message
        boolean allSubscribersGotMessages = false;
        if(consumerClient1.getReceivedMessageCount() > 0
                && consumerClient2.getReceivedMessageCount() > 0
                && consumerClient3.getReceivedMessageCount() > 0) {
            allSubscribersGotMessages = true;
        }

        Assert.assertEquals(allSubscribersGotMessages, true, "All topic subscribers are not "
                + "receiving messages");

        //close one subscription
        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        boolean result = topicSubscriptionsPage.closeNonDurableTopicSubscription();

        Assert.assertEquals(result, true, "Closing topic subscriber was not successful");

        //verify one of the subscribers now does not get new messages

        long messageCountSub1 = consumerClient1.getReceivedMessageCount();
        long messageCountSub2 = consumerClient2.getReceivedMessageCount();
        long messageCountSub3 = consumerClient3.getReceivedMessageCount();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //ignore
        }

        long messageCountNewSub1 = consumerClient1.getReceivedMessageCount();
        long messageCountNewSub2 = consumerClient2.getReceivedMessageCount();
        long messageCountNewSub3 = consumerClient3.getReceivedMessageCount();

        boolean oneReceiverIsStopped = false;

        if(messageCountNewSub1 == messageCountSub1) {
            oneReceiverIsStopped = true;
        } else if(messageCountNewSub2 == messageCountSub2) {
            oneReceiverIsStopped = true;
        } else if(messageCountNewSub3 == messageCountSub3) {
            oneReceiverIsStopped = true;
        }

        Assert.assertEquals(oneReceiverIsStopped, true, "None of the topic subscribers stopped even if"
                + "subscriber is stopped via UI ");

        //close all subscriptions
        consumerClient1.stopClient();
        consumerClient2.stopClient();
        consumerClient3.stopClient();
    }

    /**
     * 1. Create 3 topic subscribers for topic "subDeleteDurableTopic"
     * 2. Log into UI and close one subscriptions
     * 3. Verify subscription close is successful and only 2 other subscription entries are shown
     * 4. Verify closed subscriber is shown as inactive
     * 5. Verify one of subscribers does not get new messages
     * @throws IOException
     * @throws XPathExpressionException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performDurableTopicSubscriptionCloseTestCase() throws IOException, XPathExpressionException,
            JMSException, NamingException, AndesClientException, AndesClientConfigurationException,
            CloneNotSupportedException {

        String topicName = "subDeleteDurableTopic";
        long expectedMessageCount = 1000L;
        long messageCountToSend = 1000L;
        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setDurable(true, "sub1");
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 10L);
        consumerConfig1.setRunningDelay(200);
        consumerConfig1.setAsync(false);

        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setSubscriptionID("sub2");

        AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();
        consumerConfig3.setSubscriptionID("sub3");

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, topicName);
        publisherConfig.setPrintsPerMessageCount(messageCountToSend / 10L);
        publisherConfig.setNumberOfMessagesToSend(messageCountToSend);

        // Creating consumer clients
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
        consumerClient3.startClient();

        //start publisher client
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        //Wait until some messages receive
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //ignore
        }

        //verify all subscribers got at least one message
        boolean allSubscribersGotMessages = false;
        if(consumerClient1.getReceivedMessageCount() > 0
                && consumerClient2.getReceivedMessageCount() > 0
                && consumerClient3.getReceivedMessageCount() > 0) {
            allSubscribersGotMessages = true;
        }

        Assert.assertEquals(allSubscribersGotMessages, true, "All durable topic subscribers are not "
                + "receiving messages");

        //close one subscription
        TopicSubscriptionsPage topicSubscriptionsPage = homePage.getTopicSubscriptionsPage();
        boolean result = topicSubscriptionsPage.closeDurableTopicSubscription();

        Assert.assertEquals(result, true, "Closing durable topic subscriber was not successful");

        //verify one of the subscribers now does not get new messages

        long messageCountSub1 = consumerClient1.getReceivedMessageCount();
        long messageCountSub2 = consumerClient2.getReceivedMessageCount();
        long messageCountSub3 = consumerClient3.getReceivedMessageCount();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //ignore
        }

        long messageCountNewSub1 = consumerClient1.getReceivedMessageCount();
        long messageCountNewSub2 = consumerClient2.getReceivedMessageCount();
        long messageCountNewSub3 = consumerClient3.getReceivedMessageCount();

        boolean oneReceiverIsStopped = false;

        if(messageCountNewSub1 == messageCountSub1) {
            oneReceiverIsStopped = true;
        } else if(messageCountNewSub2 == messageCountSub2) {
            oneReceiverIsStopped = true;
        } else if(messageCountNewSub3 == messageCountSub3) {
            oneReceiverIsStopped = true;
        }

        Assert.assertEquals(oneReceiverIsStopped, true, "None of the durable topic subscribers stopped even if"
                + "subscriber is stopped via UI ");

        //close all subscriptions
        consumerClient1.stopClient();
        consumerClient2.stopClient();
        consumerClient3.stopClient();
    }

    /**
     * Stops the selenium driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }


}
