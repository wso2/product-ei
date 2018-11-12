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
import org.wso2.andes.server.queue.DLCQueueUtils;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.andes.stub.admin.types.Message;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Test cases where session.recover() is in play
 */
public class AMQPSessionRecoverTestCase extends MBIntegrationBaseTest {


    private final String TEST_SESSION_RECOVER_WITHOUT_ACK = "recoverTestQueueWithoutAck";
    private final String TEST_SESSION_RECOVER_WITH_ACK = "recoverTestQueueWithAck";
    private final String TEST_SESSION_RECOVER_AND_DLC = "testRecoverAndDlc";
    /**
     * Initializes test case
     *
     * @throws XPathExpressionException on an issue reading XPATH elements in config
     */
    @BeforeClass()
    public void init() throws XPathExpressionException, MalformedURLException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Set values TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS = 3 and
     * ANDES_ACK_WAIT_TIMEOUT_PROPERTY = 10000
     * so that once a message is seen by application for 3 times it goes to DLC
     * and ack timeout is out of the equation
     */
    @BeforeClass
    public void setupConfiguration() throws XPathExpressionException, IOException,
            ConfigurationException, SAXException, XMLStreamException, LoginAuthenticationExceptionException,
            URISyntaxException, AutomationUtilException {

        super.serverManager = new ServerConfigurationManager(automationContext);

        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() +
                File.separator + "wso2" + File.separator + "broker" + File.separator + "conf" + File.separator + "broker.xml";

        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);

        configurationEditor.updateProperty(AndesConfiguration
                .TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS, "3");
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "10000");

        //We should restart the server with the new configuration values
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * Send 40 messages to a queue. Consume message with client ack set and call session.recover after 20 messages
     * received. All messages should be received again (with auto ack).
     *
     * @throws AndesClientConfigurationException on invalid configs passed for client config
     * @throws JMSException on an issue running underlying JMS clients
     * @throws NamingException on lookup JNDI objects
     * @throws IOException on reading config files
     * @throws AndesClientException on running Andes client wrapping up JMS clients
     * @throws AndesAdminServiceBrokerManagerAdminException on ian issue calling admin services
     * @throws AutomationUtilException on an issue performing utils on automation framework
     * @throws LogoutAuthenticationExceptionException on an issue creating managed session with broker
     * @throws XPathExpressionException on an issue reading config elements
     * @throws LoginAuthenticationExceptionException on an issue log into broker
     */
    @Test(groups = "wso2.mb", description = "Let session recover call after every message is consumed")
    public void performSessionRecoverWithoutAckTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, AndesAdminServiceBrokerManagerAdminException, AutomationUtilException,
            LogoutAuthenticationExceptionException, XPathExpressionException,
            LoginAuthenticationExceptionException {

        //Setting values for the sent and received message counts
        long sendToRecoverQueueCount = 40L;
        long expectedMessageCount = 30;

        // Logging in
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();

        // Creating a consumer client configuration. Recover session after each message read. Apply a delay between
        // each read.
        AndesJMSConsumerClientConfiguration consumerConfig1 = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_WITHOUT_ACK);
        consumerConfig1.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setRecoverAfterEachMessageCount(20);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 5L);
        consumerConfig1.setAsync(false);

        //Creating a consumer with config
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        // Creating publisher configuration with destination queue = 'recoverTestQueueWithoutAck' and message count = 40
        AndesJMSPublisherClientConfiguration publisherConfig1 = new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_WITHOUT_ACK);
        publisherConfig1.setNumberOfMessagesToSend(sendToRecoverQueueCount);
        publisherConfig1.setPrintsPerMessageCount(sendToRecoverQueueCount / 5L);

        //Creating publishers
        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        publisherClient1.startClient();

        //Receiving messages until message count gets stagnant and
        //Once done, stop client
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        //Creating admin client
        AndesAdminClient admin = new AndesAdminClient(super.backendURL, sessionCookie);

        //make sure remaining message count is 40
        long remainingMessageCount = admin.getQueueByName(TEST_SESSION_RECOVER_WITHOUT_ACK).getMessageCount();
        Assert.assertEquals(remainingMessageCount, 40, "Remaining Message count is not expected");



        //create a AUTO_ACKNOWLEDGE consumer and consume

        AndesJMSConsumerClientConfiguration consumerConfig2 = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_WITHOUT_ACK);
        consumerConfig2.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
        expectedMessageCount = sendToRecoverQueueCount;
        consumerConfig2.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig2.setPrintsPerMessageCount(expectedMessageCount / 5L);
        consumerConfig2.setAsync(false);

        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        //make sure all messages are received again after recover
        Assert.assertEquals(consumerClient2.getReceivedMessageCount(), sendToRecoverQueueCount,
                "Received unexpected message count " + expectedMessageCount
                        + ". Received " + expectedMessageCount);

        //make sure message count is 0
        long remainingMessageCountAfterResubscribe =
                admin.getQueueByName(TEST_SESSION_RECOVER_WITHOUT_ACK).getMessageCount();
        Assert.assertEquals(remainingMessageCountAfterResubscribe, 0,
                "Remaining Message count is not zero");


        //Purging 'purgeTestQueue' queue
        admin.purgeQueue(TEST_SESSION_RECOVER_WITHOUT_ACK);

        //Put a thread sleep so that we can make sure that the queue is deleted before testing its existence
        AndesClientUtils.sleepForInterval(1000);

        //Logging out
        loginLogoutClientForAdmin.logout();
    }


    /**
     * Send 40 messages to a queue. Consume message and call session.recover per each message received. All messages
     * should be received. No message should land in DLC (as application see it only once). No more messages should be
     * received. After the test message count in queue should be zero.
     *
     * @throws AndesClientConfigurationException on invalid configs passed for client config
     * @throws JMSException on an issue running underlying JMS clients
     * @throws NamingException on lookup JNDI objects
     * @throws IOException on reading config files
     * @throws AndesClientException on running Andes client wrapping up JMS clients
     * @throws AndesAdminServiceBrokerManagerAdminException on ian issue calling admin services
     * @throws AutomationUtilException on an issue performing utils on automation framework
     * @throws LogoutAuthenticationExceptionException on an issue creating managed session with broker
     * @throws XPathExpressionException on an issue reading config elements
     * @throws LoginAuthenticationExceptionException on an issue log into broker
     */
    @Test(groups = "wso2.mb", description = "Let session recover call after every message is consumed")
    public void performSessionRecoverWithAckTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, AndesAdminServiceBrokerManagerAdminException, AutomationUtilException,
            LogoutAuthenticationExceptionException, XPathExpressionException,
            LoginAuthenticationExceptionException {

        //Setting values for the sent and received message counts
        long sendToRecoverQueueCount = 40L;
        long expectedMessageCount = 40;

        // Logging in
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();

        // Creating a consumer client configuration. Recover session after each message read. Apply a delay between
        // each read.
        AndesJMSConsumerClientConfiguration consumerConfig1 = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_WITH_ACK);
        consumerConfig1.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setRecoverAfterEachMessageCount(1);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 5L);
        consumerConfig1.setAsync(false);

        //Creating a consumer with above config
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        // Creating publisher configuration with destination queue = 'recoverTestQueueWithAck' and message count = 40
        AndesJMSPublisherClientConfiguration publisherConfig1 = new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_WITH_ACK);
        publisherConfig1.setNumberOfMessagesToSend(sendToRecoverQueueCount);
        publisherConfig1.setPrintsPerMessageCount(sendToRecoverQueueCount / 5L);

        //Creating publishers
        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        publisherClient1.startClient();

        //Receiving messages until message count gets stagnant and
        //Once done, stop client
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedMessageCount,
                "Failed to receive expected message count " + expectedMessageCount
                        + ". Received " + consumerClient1.getReceivedMessageCount());

        //Creating admin client
        AndesAdminClient admin = new AndesAdminClient(super.backendURL, sessionCookie);

        //testing if DLC queue is created
        Assert.assertNotNull(admin.getDlcQueue(), "DLC queue not created");

        //Testing if no messages are moved to DLC
        Message[] messagesInDLC = admin.browseQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString
                (TEST_SESSION_RECOVER_WITH_ACK), 0, 200);
        Assert.assertNull(messagesInDLC, "Messages have been moved to DLC.");

        //make sure no more messages are received
        consumerConfig1.setRecoverAfterEachMessageCount(Long.MAX_VALUE);
        consumerConfig1.setRunningDelay(0);
        AndesClient consumerClient2 = new AndesClient(consumerConfig1, true);
        consumerClient2.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        Assert.assertEquals(consumerClient2.getReceivedMessageCount(), 0,
                "Received more messages than expected message count " + expectedMessageCount
                        + ". Received " + (expectedMessageCount + consumerClient2.getReceivedMessageCount()));

        //make sure message count is 0
        long remainingMessageCount = admin.getQueueByName(TEST_SESSION_RECOVER_WITH_ACK).getMessageCount();
        Assert.assertEquals(remainingMessageCount, 0, "Remaining Message count is not zero");


        //Purging 'purgeTestQueue' queue
        admin.purgeQueue(TEST_SESSION_RECOVER_WITH_ACK);

        //Put a thread sleep so that we can make sure that the queue is deleted before testing its existence
        AndesClientUtils.sleepForInterval(1000);

        //Logging out
        loginLogoutClientForAdmin.logout();
    }


    /**
     * Send 10 messages to a queue. Consume message and call session.recover per each message received (with Client
     * ack). Once this is done 4*5 times first 5 messages will go to DLC. Once client is started again next 5 messages
     * will go to DLC. After that no messages should be received.
     *
     * @throws AndesClientConfigurationException on invalid configs passed for client config
     * @throws JMSException on an issue running underlying JMS clients
     * @throws NamingException on lookup JNDI objects
     * @throws IOException on reading config files
     * @throws AndesClientException on running Andes client wrapping up JMS clients
     * @throws AndesAdminServiceBrokerManagerAdminException on ian issue calling admin services
     * @throws AutomationUtilException on an issue performing utils on automation framework
     * @throws LogoutAuthenticationExceptionException on an issue creating managed session with broker
     * @throws XPathExpressionException on an issue reading config elements
     * @throws LoginAuthenticationExceptionException on an issue log into broker
     */
    @Test(groups = "wso2.mb", description = "Let session recover call after every message is consumed with client ack")
    public void performSessionRecoverAndDLCTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, AndesAdminServiceBrokerManagerAdminException, AutomationUtilException,
            LogoutAuthenticationExceptionException, XPathExpressionException,
            LoginAuthenticationExceptionException {

        //Setting values for the sent and received message counts
        long sendToRecoverQueueCount = 10L;
        long expectedMessageCount = ((3 +1) * 5 ) + 2;

        // Logging in
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();

        // Creating a consumer client configuration. Recover session after each message read. Apply a delay between
        // each read. Use CLIENT_ACKNOWLEDGE. Never ACK.
        AndesJMSConsumerClientConfiguration consumerConfig1 = new AndesJMSConsumerClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_AND_DLC);
        consumerConfig1.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setRecoverAfterEachMessageCount(1);
        //consumerConfig1.setRunningDelay(200);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 5L);
        consumerConfig1.setAsync(false);

        //Creating a consumer with above config
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();

        // Creating publisher configuration with destination queue = 'testRecoverAndDlc'
        AndesJMSPublisherClientConfiguration publisherConfig1 = new AndesJMSPublisherClientConfiguration(getAMQPPort(),
                ExchangeType.QUEUE, TEST_SESSION_RECOVER_AND_DLC);
        publisherConfig1.setNumberOfMessagesToSend(sendToRecoverQueueCount);
        publisherConfig1.setPrintsPerMessageCount(sendToRecoverQueueCount / 5L);

        //Creating publishers
        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        publisherClient1.startClient();

        //Receiving messages until message count gets stagnant and
        //Once done, stop client
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);

        //as we do not ACK and as we recover after each consume messages will be received as expected
        Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedMessageCount,
                "Failed to receive expected message count " + expectedMessageCount
                        + ". Received " + consumerClient1.getReceivedMessageCount());

        //Creating admin client
        AndesAdminClient admin = new AndesAdminClient(super.backendURL, sessionCookie);

        //testing if DLC queue is created
        Assert.assertNotNull(admin.getDlcQueue(), "DLC queue not created");

        //Testing if 5 messages are moved to DLC
        Message[] messagesInDLC = admin.browseQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString
                (TEST_SESSION_RECOVER_AND_DLC), 0, 200);
        Assert.assertEquals(messagesInDLC.length, 5, "Messages not have been moved to DLC.");


        //make sure remaining message count is 5
        long remainingMessageCount = admin.getQueueByName(TEST_SESSION_RECOVER_AND_DLC).getMessageCount();
        Assert.assertEquals(remainingMessageCount, 5, "Remaining Message count is not expected value");

        //start consuming again. Wait for some long to see how many messages are received.
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount + 10);
        AndesClient consumerClient2 = new AndesClient(consumerConfig1, true);
        consumerClient2.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);


        //make sure message count is 0
        long remainingMessageCount2 = admin.getQueueByName(TEST_SESSION_RECOVER_AND_DLC).getMessageCount();
        Assert.assertEquals(remainingMessageCount2, 0, "Remaining Message count is not zero");

        //make sure DLC has all 10 messages
        Message[] messagesInDLCAfterSecondRound = admin.browseQueue(DLCQueueUtils
                .identifyTenantInformationAndGenerateDLCString
                (TEST_SESSION_RECOVER_AND_DLC), 0, 200);
        Assert.assertEquals(messagesInDLCAfterSecondRound.length, 10, "Unexpected message count in DLC");

        //Purging 'purgeTestQueue' queue
        admin.purgeQueue(TEST_SESSION_RECOVER_AND_DLC);

        //Put a thread sleep so that we can make sure that the queue is deleted before testing its existence
        AndesClientUtils.sleepForInterval(1000);

        //Logging out
        loginLogoutClientForAdmin.logout();
    }

    /**
     * Revert changed configuration, purge and delete the queue.
     *
     * @throws XPathExpressionException on an issue reading XPATH elements in config
     * @throws IOException on an file issue reading with config files
     */
    @AfterClass()
    public void cleanup() throws Exception {
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient =
                new AndesAdminClient(super.backendURL, sessionCookie);

        andesAdminClient.deleteQueue(TEST_SESSION_RECOVER_WITHOUT_ACK);
        andesAdminClient.deleteQueue(TEST_SESSION_RECOVER_WITH_ACK);
        andesAdminClient.deleteQueue(TEST_SESSION_RECOVER_AND_DLC);

        loginLogoutClientForAdmin.logout();
        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);

    }
}
