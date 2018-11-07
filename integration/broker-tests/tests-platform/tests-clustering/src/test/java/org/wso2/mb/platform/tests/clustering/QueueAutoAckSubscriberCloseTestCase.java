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

package org.wso2.mb.platform.tests.clustering;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Load test in MB clustering for queues using auto acknowledge.
 */
public class QueueAutoAckSubscriberCloseTestCase extends MBPlatformBaseTest {

    private static final long SEND_COUNT = 100000L;
    private static final long EXPECTED_COUNT = SEND_COUNT;
    private static final int NO_OF_SUBSCRIBERS = 50;
    private static final int NO_OF_PUBLISHERS = 50;
    private static final long NO_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS = 10;
    private static final int NO_OF_SUBSCRIBERS_TO_CLOSE = NO_OF_SUBSCRIBERS / 10;
    private static final long NO_OF_MESSAGES_TO_EXPECT =
            EXPECTED_COUNT - NO_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS;
    private static final int NO_OF_NON_CLOSING_SUBSCRIBERS =
            NO_OF_SUBSCRIBERS - NO_OF_SUBSCRIBERS_TO_CLOSE;

    /**
     * Initialize the test as super tenant user.
     *
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws LoginAuthenticationExceptionException, IOException, XPathExpressionException,
            URISyntaxException, SAXException, XMLStreamException, AutomationUtilException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        super.initAndesAdminClients();
    }

    /**
     * Create 50 subscriptions for a queue and publish one million messages. Then close 10% of the subscribers while
     * messages are retrieving and check if all the messages are received by other subscribers.
     *
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "50 subscriptions for a queue and 50 publishers. Then close " +
                                            "10% of the subscribers ", enabled = true)
    public void performMillionMessageTenPercentSubscriberCloseTestCase()
            throws XPathExpressionException, AndesClientConfigurationException, NamingException,
                   JMSException,
                   IOException, AndesClientException {

        String randomInstanceKeyForReceiver = getRandomMBInstance();

        AutomationContext tempContextForReceiver =
                getAutomationContextWithKey(randomInstanceKeyForReceiver);

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(tempContextForReceiver.getInstance()
                                                                .getHosts().get("default"),
                                                        Integer.parseInt(tempContextForReceiver
                                                                                 .getInstance()
                                                                                 .getPorts()
                                                                                 .get("amqp")),
                                                        ExchangeType.QUEUE, "TenPercentSubscriberCloseQueue");
        consumerConfig.setMaximumMessagesToReceived(NO_OF_MESSAGES_TO_EXPECT);
        consumerConfig.setPrintsPerMessageCount(NO_OF_MESSAGES_TO_EXPECT / 10L);

        AndesJMSConsumerClientConfiguration consumerClosingConfig =
                new AndesJMSConsumerClientConfiguration(tempContextForReceiver.getInstance()
                                                                .getHosts().get("default"),
                                                        Integer.parseInt(tempContextForReceiver
                                                                                 .getInstance()
                                                                                 .getPorts()
                                                                                 .get("amqp")),
                                                        ExchangeType.QUEUE, "TenPercentSubscriberCloseQueue");
        consumerClosingConfig
                .setMaximumMessagesToReceived(NO_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS);
        consumerClosingConfig
                .setPrintsPerMessageCount(NO_OF_MESSAGES_TO_RECEIVE_BY_CLOSING_SUBSCRIBERS / 10L);

        String randomInstanceKeyForSender = getRandomMBInstance();
        AutomationContext tempContextForSender =
                getAutomationContextWithKey(randomInstanceKeyForSender);
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(tempContextForSender.getInstance()
                                                                 .getHosts().get("default"),
                                                         Integer.parseInt(tempContextForSender
                                                                                  .getInstance()
                                                                                  .getPorts()
                                                                                  .get("amqp")),
                                                         ExchangeType.QUEUE, "TenPercentSubscriberCloseQueue");
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        AndesClient consumerClient =
                new AndesClient(consumerConfig, NO_OF_NON_CLOSING_SUBSCRIBERS, true);
        consumerClient.startClient();

        AndesClient consumerClosingClient =
                new AndesClient(consumerClosingConfig, NO_OF_SUBSCRIBERS_TO_CLOSE, true);
        consumerClosingClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, NO_OF_PUBLISHERS, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(consumerClosingClient);

        log.info("Total Received Messages [" + consumerClient.getReceivedMessageCount() + "]");

        long totalReceivedMessages =
                consumerClient.getReceivedMessageCount() + consumerClosingClient
                        .getReceivedMessageCount();

        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(totalReceivedMessages, EXPECTED_COUNT, "Message receiving failed.");
    }
}
