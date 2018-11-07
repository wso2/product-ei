package org.wso2.mb.integration.tests.amqp.functional;

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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Test class to test temporary topics work along with subscriptions.
 */
public class TemporaryTopicSubscriptionVerificationTestCase extends MBIntegrationBaseTest {

    /**
     * Initializes test case
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Put a topic subscriber
     * 2. Receive some of the messages and close.
     * 3. Resubscribe to same topic and see if messages are received.
     * 4. Messages should not be receiving
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"},
            description = "Single topic subscriber subscribe-close-re-subscribe test case")
    public void performSingleTopicSubscribeCloseResubscribeTest()
            throws AndesClientConfigurationException, NamingException, JMSException, IOException,
                   AndesClientException, XPathExpressionException {

        long sendCount = 1000L;
        long expectedCount = 200L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration initialConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.TOPIC, "singleSubscribeAndCloseTopic");
        initialConsumerConfig.setMaximumMessagesToReceived(expectedCount);
        initialConsumerConfig.setPrintsPerMessageCount(expectedCount / 10L);
        initialConsumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.TOPIC, "singleSubscribeAndCloseTopic");
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(initialConsumerConfig, true);
        initialConsumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(initialConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);


        // Creating a second consumer client configuration
        AndesJMSConsumerClientConfiguration secondaryConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.TOPIC, "singleSubscribeAndCloseTopic");
        secondaryConsumerConfig.setMaximumMessagesToReceived(1L);
        secondaryConsumerConfig.setAsync(false);

        // Creating a seconds publisher client configuration
        AndesClient secondaryConsumerClient = new AndesClient(secondaryConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(secondaryConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), sendCount, "Message send failed");
        Assert.assertEquals(initialConsumerClient
                                    .getReceivedMessageCount(), expectedCount, "Initial consumer failed to receive messages");
        Assert.assertEquals(secondaryConsumerClient
                                    .getReceivedMessageCount(), 0, "Message received after re-subscribing for a temporary topic.");
    }

    /**
     * 1. Put a topic subscriber
     * 2. Put another topic subscriber. It will receive some of the messages and close
     * 3. Resubscribe to same topic and see if messages are received. (while first subscriber is still getting messages)
     * 4. Messages should not be receiving
     *
     * @throws ExecutionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"}, description = "Single topic subscriber subscribe-close-" +
                                                       "re-subscribe test case with multiple " +
                                                       "subscriptions")
    public void performMultipleTopicSubscribeCloseResubscribeTest()
            throws ExecutionException, AndesClientConfigurationException, NamingException,
                   JMSException,
                   IOException, AndesClientException, XPathExpressionException {

        long sendCount = 1000L;
        long expectedCountByClient1 = 1000L;
        long expectedCountByClient2 = 200L;

        ExecutorService service = Executors.newSingleThreadExecutor();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration initialConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.TOPIC, "multiSubscribeAndCloseTopic");
        initialConsumerConfig.setMaximumMessagesToReceived(expectedCountByClient1);
        initialConsumerConfig.setPrintsPerMessageCount(expectedCountByClient1 / 10L);
        initialConsumerConfig.setRunningDelay(100L); // Setting a delay in consuming each message.
        initialConsumerConfig.setAsync(false);

        AndesJMSConsumerClientConfiguration secondaryConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                        ExchangeType.TOPIC, "multiSubscribeAndCloseTopic");
        secondaryConsumerConfig.setMaximumMessagesToReceived(expectedCountByClient2);
        secondaryConsumerConfig.setPrintsPerMessageCount(expectedCountByClient2 / 10L);
        secondaryConsumerConfig.setRunningDelay(100L); // Setting a delay in publishing each message.
        secondaryConsumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                                                         ExchangeType.TOPIC, "multiSubscribeAndCloseTopic");
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig.setNumberOfMessagesToSend(sendCount);

        // Creating consumer clients
        AndesClient initialConsumerClient = new AndesClient(initialConsumerConfig, true);
        initialConsumerClient.startClient();

        AndesClient secondaryConsumerClient = new AndesClient(secondaryConsumerConfig, true);
        secondaryConsumerClient.startClient();

        // Schedule another subscriber to run after 'first client is closed'
        ConcurrentReceiverTask receiverTask = new ConcurrentReceiverTask();
        Future<Boolean> future = service.submit(receiverTask);

        // Creating publisher client
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(initialConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.shutdownClient(secondaryConsumerClient);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), sendCount, "Message send failed");

        Assert.assertEquals(initialConsumerClient
                                    .getReceivedMessageCount(), expectedCountByClient1, "Message receiving failed by client 1.");
        Assert.assertEquals(secondaryConsumerClient
                                    .getReceivedMessageCount(), expectedCountByClient2, "Message receiving failed by client 2.");

        try {
            Boolean newReceivingClientResult = future.get();
            Assert.assertFalse(newReceivingClientResult,
                               "Message received after re-subscribing for a temporary topic when" +
                               " another subscription to same topic is around.");
        } catch (InterruptedException e) {
            //ignore
        }
    }

    /**
     * Inner class that run another consumer
     */
    private class ConcurrentReceiverTask implements Callable<Boolean> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Boolean call() throws Exception {
            // waiting
            TimeUnit.SECONDS.sleep(30);
            // Re-subscribe and see if messages are coming
            // Creating a  consumer client configuration
            AndesJMSConsumerClientConfiguration newConsumerConfig =
                    new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                                                            ExchangeType.TOPIC, "multiSubscribeAndCloseTopic");
            newConsumerConfig.setMaximumMessagesToReceived(1L);
            newConsumerConfig.setAsync(false);

            // Creating clients
            AndesClient newConsumerClient = new AndesClient(newConsumerConfig, true);
            newConsumerClient.startClient();

            AndesClientUtils
                    .waitForMessagesAndShutdown(newConsumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

            return newConsumerClient.getReceivedMessageCount() == 1L;
        }
    }
}
