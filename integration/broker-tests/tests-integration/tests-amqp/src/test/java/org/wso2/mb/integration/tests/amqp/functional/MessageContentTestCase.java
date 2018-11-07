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
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSMessageType;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

/**
 * This class contains tests for message content validity.
 */
public class MessageContentTestCase extends MBIntegrationBaseTest {

    /**
     * 256KB size. This is to create more than 3 message content chunks to check chunk data
     * retrieval.
     */
    private static final int SIZE_TO_READ = 250 * 1024;
    /**
     * Message sent count.
     */
    private static final long SEND_COUNT = 1L;

    /**
     * Message expected count.
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

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
     * Set allowCompression to false, so that broker won't compress messages
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
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() +
                File.separator + "repository" + File.separator + "conf" + File.separator + "broker.xml";

        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);

        configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_ALLOW_COMPRESSION, "false");

        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * Test the message content integrity of a single message by comparing the sent and received
     * message content which spreads over several message content chunks.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Message content validation test case")
    public void performQueueContentSendReceiveTestCase()
            throws AndesClientConfigurationException, IOException, JMSException, NamingException,
                   AndesClientException, XPathExpressionException {

        // Reading message content
        char[] inputContent = new char[SIZE_TO_READ];
        try {
            BufferedReader inputFileReader =
                    new BufferedReader(new FileReader(AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB));
            inputFileReader.read(inputContent);
        } catch (FileNotFoundException e) {
            log.warn("Error locating input content from file : " + AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB);
        } catch (IOException e) {
            log.warn("Error reading input content from file : " + AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB);
        }

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "QueueContentSendReceive");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        // writing received messages.
        consumerConfig
                .setFilePathToWriteReceivedMessages(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "QueueContentSendReceive");

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        // message content will be read from this path and published
        publisherConfig
                .setReadMessagesFromFilePath(AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Reading received message content
        char[] outputContent = new char[SIZE_TO_READ];

        try {
            BufferedReader inputFileReader =
                    new BufferedReader(new FileReader(AndesClientConstants.FILE_PATH_TO_WRITE_RECEIVED_MESSAGES));
            inputFileReader.read(outputContent);
        } catch (FileNotFoundException e) {
            log.warn("Error locating output content from file : " + AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB);
        } catch (IOException e) {
            log.warn("Error reading output content from file : " + AndesClientConstants.MESSAGE_CONTENT_INPUT_FILE_PATH_1MB);
        }

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
        Assert.assertEquals(new String(outputContent), new String(inputContent), "Message content has been modified.");
    }

    /**
     * Test if Map messages containing multiple entries with 250K String sizes can be sent and received.
     *
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Message content validation test case")
    public void performLargeStringMapMessageSendReceiveTestCase()
            throws AndesClientConfigurationException, IOException, JMSException, NamingException,
            AndesClientException, XPathExpressionException {

        String queueName = "LargeMapMessageQueue";
        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, queueName);

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setJMSMessageType(JMSMessageType.MAP);

        //Write large map message to input file
        BufferedWriter br = new BufferedWriter(new FileWriter(new File((AndesClientConstants
                .MAP_MESSAGE_CONTENT_INPUT_FILE_PATH))));
        for (int i = 0; i < 3; i++) {
            StringBuilder builder = new StringBuilder("");
            for (int j = 0; j < 250000; j++) {
                builder.append("a");
            }
            br.write(builder.toString());
            br.newLine();
        }
        br.close();

        // message content will be read from this path and published
        publisherConfig.setReadMessagesFromFilePath(AndesClientConstants.MAP_MESSAGE_CONTENT_INPUT_FILE_PATH);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();
        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), SEND_COUNT, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), EXPECTED_COUNT, "Message receiving failed.");
    }

    /**
     * Restore to the previous configurations when the message content test is complete.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass
    public void tearDown() throws IOException, AutomationUtilException {
        super.serverManager.restoreToLastConfiguration(true);
    }
}
