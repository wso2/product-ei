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
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;


/**
 * Send messages using SSL and receive messages using SSL
 */
public class SSLSendReceiveTestCase extends MBIntegrationBaseTest {

    /**
     * Message count to send
     */
    private static final long SEND_COUNT = 100L;

    /**
     * Message count expected
     */
    private static final long EXPECTED_COUNT = SEND_COUNT;

    /**
     * Initializes test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * 1. Creates a queue named "SSLSingleQueue".
     * 2. Consumer listens to receiving messages using an ssl connection.
     * 3. Publisher publishes messages using an ssl connection.
     * 4. Consumer should receive all messages sent.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue", "security"})
    public void performSingleQueueSendReceiveTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, XPathExpressionException {
        // Creating ssl connection string elements
        // The following keystore path and truststore path should be as follows regardless the OS(platform).
        String keyStorePath = System.getProperty("carbon.home").replace("\\", "/") + "/repository/resources/security/" +
                              "wso2carbon.jks";
        String trustStorePath = System.getProperty("carbon.home").replace("\\", "/") + "/repository/resources/" +
                                "security/client-truststore.jks";
        String keyStorePassword = "wso2carbon";
        String trustStorePassword = "wso2carbon";

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(
                        "admin", "admin", "127.0.0.1", getSecureAMQPPort(), ExchangeType.QUEUE, "SSLSingleQueue",
                        "RootCA", trustStorePath, trustStorePassword, keyStorePath,
                        keyStorePassword);
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(
                        "admin", "admin", "127.0.0.1", getSecureAMQPPort(), ExchangeType.QUEUE, "SSLSingleQueue",
                        "RootCA", trustStorePath, trustStorePassword, keyStorePath,
                        keyStorePassword);

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating consumer client
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                                    .getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(consumerClient
                                    .getReceivedMessageCount(), EXPECTED_COUNT, "Message receive error from consumerClient");
    }


    /**
     * 1. Creates a queue named "SSLSingleQueue".
     * 2. Consumer listens to receiving messages using an ssl connection where consumer does not provide the keystore
     * to connect.
     * 3. Publisher publishes messages using an ssl connection.
     * 4. Consumer should receive all messages sent.
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "queue", "security"})
    public void performKeyStoreOptionalTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, XPathExpressionException {
        // Creating ssl connection string elements
        // The following truststore path should be as follows regardless the OS(platform).
        String trustStorePath = System.getProperty("carbon.home").replace("\\", "/") + "/repository/resources/" +
                                "security/client-truststore.jks";
        String trustStorePassword = "wso2carbon";

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(
                        "admin", "admin", "127.0.0.1", getSecureAMQPPort(), ExchangeType.QUEUE, "SSLSingleQueue",
                        "RootCA", trustStorePath, trustStorePassword, null,
                        null);
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT);
        consumerConfig.setPrintsPerMessageCount(EXPECTED_COUNT / 10L);
        consumerConfig.setAsync(false);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(
                        "admin", "admin", "127.0.0.1", getSecureAMQPPort(), ExchangeType.QUEUE, "SSLSingleQueue",
                        "RootCA", trustStorePath, trustStorePassword, null,
                        null);

        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);
        publisherConfig.setPrintsPerMessageCount(SEND_COUNT / 10L);

        // Creating consumer client
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        AndesClientUtils
                .waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient
                .getSentMessageCount(), SEND_COUNT, "Message sending failed");
        Assert.assertEquals(consumerClient
                .getReceivedMessageCount(), EXPECTED_COUNT, "Message receive error from consumerClient");
    }
}
