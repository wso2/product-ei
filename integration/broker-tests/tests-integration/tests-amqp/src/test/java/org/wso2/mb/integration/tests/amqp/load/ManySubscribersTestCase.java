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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.load;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * This class contains tests for receiving messages through a large number of subscribers.
 */
public class ManySubscribersTestCase extends MBIntegrationBaseTest {

    /**
     * Message count to send
     */
    private static final long SEND_COUNT = 100000L;

    /**
     * Expected message count
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * Number of subscribers
     */
    private static final int NUMBER_OF_SUBSCRIBERS = 1000;

    /**
     * Number of publishers
     */
    private static final int NUMBER_OF_PUBLISHERS = 1;

    /**
     * Initialize the test as super tenant user.
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Test message sending to 1000 subscribers at the same time.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Message content validation test case")
    public void performMillionMessageManyConsumersTestCase()
            throws Exception {

        try {
            // Creating a consumer client configuration
            AndesJMSConsumerClientConfiguration consumerConfig =
                    new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "singleQueueMillion");
            consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
            consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);

            // Creating a consumer client configuration
            AndesJMSPublisherClientConfiguration publisherConfig =
                    new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "singleQueueMillion");
            publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
            publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

            AndesClient consumerClient =
                    new AndesClient(consumerConfig, NUMBER_OF_SUBSCRIBERS, true);
            consumerClient.setStartDelay(100L); // Use a starting delay between consumers
            consumerClient.startClient();

            AndesClient publisherClient =
                    new AndesClient(publisherConfig, NUMBER_OF_PUBLISHERS, true);
            publisherClient.startClient();

            AndesClientUtils
                    .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

            // Evaluating
            Assert.assertEquals(publisherClient
                                        .getSentMessageCount(), SEND_COUNT * NUMBER_OF_SUBSCRIBERS, "Message sending failed");
            Assert.assertEquals(consumerClient
                                        .getReceivedMessageCount(), EXPECTED_COUNT * NUMBER_OF_SUBSCRIBERS, "Message receiving failed.");
        } catch (OutOfMemoryError e) {
            restartServer();
        }
    }
}
