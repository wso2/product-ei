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

package org.wso2.mb.integration.tests.amqp.functional;

import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class contains tests for AMQP message content validity, with compression.
 */
public class LZ4CompressionTestCase extends MBIntegrationBaseTest {

    /**
     * Input file size is 256KB
     */
    private static final int SIZE_TO_READ = 250 * 1024;
    /**
     * Message sent count
     */
    private static final int SEND_COUNT = 1;

    /**
     * Message expected count
     */
    private static final int EXPECTED_COUNT = SEND_COUNT;

    /**
     * Initialize the test as super tenant user
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Set allowCompression to true so that broker will compress messages before storing into the database, and
     * reduce maximum content chunk size to 100 from default value (65500); to create more content chunks
     * from compressed content, to check chunk data retrieval.
     *
     * @throws XPathExpressionException
     * @throws java.io.IOException
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException
     */
    @BeforeClass
    public void setupConfiguration() throws XPathExpressionException, IOException, ConfigurationException,
            AutomationUtilException {

        super.serverManager = new ServerConfigurationManager(automationContext);
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() + File.separator + "wso2" +
                                            File.separator + "broker" + File.separator + "conf" + File.separator +
                                            "broker.xml";

        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);

        configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_ALLOW_COMPRESSION, "true");
        configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_MAX_CONTENT_CHUNK_SIZE, "100");

        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * Test the Queue message content integrity of a single message when compression is enabled, by comparing the
     * sent and received message content which spreads over several message content chunks.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Compressed queue message content validation test case")
    public void performQueueContentSendCompressQueueReceiveTestCase() throws AndesClientConfigurationException, IOException,
            JMSException, NamingException, AndesClientException, XPathExpressionException {

        // Generating message content
        String inputContentAsString = AndesClientUtils.createRandomString(SIZE_TO_READ, 100);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, "QueueContentSendCompressReceive");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.QUEUE, "QueueContentSendCompressReceive");

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        // Set message content to publish
        publisherConfig.setMessagesContentOfConfiguration(inputContentAsString);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Reading received message content
        List<String> receivedMessage = consumerClient.getReceivedMessages();

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertEquals(receivedMessage.get(0), inputContentAsString, "Message content has been modified");
    }


    /**
     * Test the durable topic message content integrity of a single message when compression is enabled, by comparing
     * the sent and received message content which spreads over several message content chunks.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Compressed durable topic message content validation test case")
    public void performQueueContentSendCompressDurableTopicReceiveTestCase() throws AndesClientConfigurationException, IOException,
            JMSException, NamingException, AndesClientException, XPathExpressionException {

        // Generating message content
        String inputContentAsString = AndesClientUtils.createRandomString(SIZE_TO_READ, 100);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.TOPIC, "DurableTopicContentSendCompressReceive");
        consumerConfig.setDurable(true, "compression-new1");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        // writing received messages.
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.TOPIC, "DurableTopicContentSendCompressReceive");

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Set message content to publish
        publisherConfig.setMessagesContentOfConfiguration(inputContentAsString);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Reading received message content
        List<String> receivedMessage = consumerClient.getReceivedMessages();

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertEquals(receivedMessage.get(0), inputContentAsString, "Message content has been modified.");
    }




    /**
     * Test the topic message content integrity of a single message when compression is enabled, by comparing
     * the sent and received message content which spreads over several message content chunks.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Compressed topic message content validation test case")
    public void performQueueContentSendCompressTopicReceiveTestCase() throws AndesClientConfigurationException, IOException,
            JMSException, NamingException, AndesClientException, XPathExpressionException {

        // Generating message content
        String inputContentAsString = AndesClientUtils.createRandomString(SIZE_TO_READ, 100);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.TOPIC, "TopicContentSendCompressReceive");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        // writing received messages.
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(getAMQPPort()
                , ExchangeType.TOPIC, "TopicContentSendCompressReceive");

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        // Set message content to publish
        publisherConfig.setMessagesContentOfConfiguration(inputContentAsString);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Reading received message content
        List<String> receivedMessage = consumerClient.getReceivedMessages();

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertEquals(receivedMessage.get(0), inputContentAsString, "Message content has been modified.");
    }

    /**
     * Restore to the previous configurations when the message content compression test is complete.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass
    public void tearDown() throws IOException, AutomationUtilException {
        super.serverManager.restoreToLastConfiguration(true);
    }
}
