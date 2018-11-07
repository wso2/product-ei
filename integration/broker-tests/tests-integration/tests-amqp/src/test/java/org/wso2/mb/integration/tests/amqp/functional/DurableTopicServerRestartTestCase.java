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
package org.wso2.mb.integration.tests.amqp.functional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.xml.xpath.XPathExpressionException;

/**
 * The following test class contains durable topic test cases with relation to restarting the
 * server. H2 in-memory mode will not work as restarting the server will not hold the sent messages.
 */
public class DurableTopicServerRestartTestCase extends MBIntegrationBaseTest {

    /**
     * The amount messages to be sent by publisher.
     */
    private static final long SEND_COUNT = 1000L;

    /**
     * The amount of messages expected by the receiver.
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * The amount of time to wait till a process is finished. The process could be publishing or
     * messages or receiving of messages. This is used as a safety precaution.
     */
    private static final long WAITING_TIME_TILL_PROCESS_FINISHES = 5000L;

    /**
     * Initializing test case
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * The test case checks for the durability of a durable topic when a server is restarted.
     * 1. Create a durable topic subscriber.
     * 2. Close the durable topic subscriber.
     * 3. Publish message for the durable topic destination.
     * 4. Restart the server.
     * 5. Create the same durable topic subscriber again.
     * 6. Check whether messages are received.
     *
     * @throws Exception
     * @see <a href="https://wso2.org/jira/browse/MB-941">MB-941</a>
     */
    @Test(groups = {"wso2.mb", "durableTopic"})
    public void performDurablePublishRestartServerTestCase()
            throws Exception {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableServerRestartTopic");
        consumerConfig.setDurable(true, "restartServerSub");
        consumerConfig.setAsync(false);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "durableServerRestartTopic");
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Starting the first durable subscription.
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        // Wait till consumer starts listening
        AndesClientUtils.sleepForInterval(WAITING_TIME_TILL_PROCESS_FINISHES);

        // Stopping the subscription
        initialConsumerClient.stopClient();

        // Wait till consumer stops
        AndesClientUtils.sleepForInterval(WAITING_TIME_TILL_PROCESS_FINISHES);

        // Creating the publisher and publishing
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.sleepForInterval(WAITING_TIME_TILL_PROCESS_FINISHES);

        // Restarting the server
        super.restartServer();

        // Starting the second durable subscription
        AndesClient secondaryConsumerClient = new AndesClient(consumerConfig, true);
        secondaryConsumerClient.startClient();

        // Waiting till all the messages are received
        AndesClientUtils
                .waitForMessagesAndShutdown(secondaryConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating the amount of messages published
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        // Evaluating the amount of messages received
        Assert.assertEquals(secondaryConsumerClient
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed for client 1.");

    }
}
