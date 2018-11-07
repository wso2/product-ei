/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.integration.tests.amqp.functional;

import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.andes.server.queue.DLCQueueUtils;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;

import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.wso2.carbon.andes.stub.admin.types.Message;
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
 * Testing tasks relevant to queue purging and queue deletion
 * Once a queue is purged or deleted, messages both in the queue and the DLC should be deleted
 */
public class PurgeMessagesTestCase extends MBIntegrationBaseTest {

    private static final String TEST_QUEUE_PURGE = "purgeTestQueue";
    private static final String TEST_QUEUE_DELETE = "deleteTestQueue";

    /**
     * Initializes test case
     *
     * @throws XPathExpressionException
     */
    @BeforeClass()
    public void init() throws XPathExpressionException, MalformedURLException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Set values TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS = 1 and
     * ANDES_ACK_WAIT_TIMEOUT_PROPERTY = 1
     * so that the time taken for the massages to get moved into DLC is at a minimum
     */
    @BeforeClass
    public void setupConfiguration() throws XPathExpressionException, IOException,
            ConfigurationException, SAXException, XMLStreamException, LoginAuthenticationExceptionException,
            URISyntaxException, AutomationUtilException {
        ;
        super.serverManager = new ServerConfigurationManager(automationContext);

        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() +
                File.separator + "repository" + File.separator + "conf" + File.separator + "broker.xml";

        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);

        //Set values TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS = 1 and
        //ANDES_ACK_WAIT_TIMEOUT_PROPERTY = 1
        //so that the time taken for the massages to get moved into DLC is at a minimum
        configurationEditor.updateProperty(AndesConfiguration
                .TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS, "1");
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "1");

        //andesAdminClient.deleteQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString(TEST_QUEUE_PURGE));
        //We should restart the server witht he new configuration values
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }


    /**
     * Test case 1
     * 1. Start 2 publishers and consumers for 2 separate queues, 'purgeTestQueue' and 'deleteTestQueue' with both
     * consumers set to CLIENT_ACKNOWLEDGE mode
     * 2. Publish 25 messages to 'purgeTestQueue' and 75 messages to 'deleteTestQueue'
     * 2. 100 messages should get moved into DLC after a while -test
     * 3. Purge queue 'puregeTestQueue'
     * 4. only 75 messages should be remaining in DLC -test
     * 5. Delete 'deleteTestQueue'
     * 6. No messages should be remaining in DLC - test
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */

    @Test(groups = "wso2.mb", description = "Let messages get moved to DLC and check messages deleted in DLC in queue"
                                            + " purge and deletion")
    public void performQueuePurgeTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
            AndesClientException, AndesAdminServiceBrokerManagerAdminException, AutomationUtilException,
            LogoutAuthenticationExceptionException, URISyntaxException, SAXException, XPathExpressionException,
            LoginAuthenticationExceptionException, XMLStreamException, CloneNotSupportedException {

        //Setting values for the sent and received message counts
        long sendToPurgeQueueCount = 25L;
        long sendToDeleteQueueCount = 75L;
        long expectedMessageCount = 1000;

        // Logging in
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig1 =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, TEST_QUEUE_PURGE);
        consumerConfig1.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig1.setMaximumMessagesToReceived(expectedMessageCount);
        consumerConfig1.setPrintsPerMessageCount(expectedMessageCount / 100L);
        consumerConfig1.setAsync(false);

        //Cloning consumer configuration with a different destination queue
        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();
        consumerConfig2.setDestinationName(TEST_QUEUE_DELETE);

        //Creating 2 consumers with the 2 configurations
        AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
        consumerClient1.startClient();
        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        // Creating publisher configuration with destination queue = 'purgeTestQueue' and message count = 25
        AndesJMSPublisherClientConfiguration publisherConfig1 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, TEST_QUEUE_PURGE);
        publisherConfig1.setNumberOfMessagesToSend(sendToPurgeQueueCount);
        publisherConfig1.setPrintsPerMessageCount(sendToPurgeQueueCount / 5L);

        // Creating publisher configuration with destination queue = 'deleteTestQueue' and message count = 75
        AndesJMSPublisherClientConfiguration publisherConfig2 =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, TEST_QUEUE_DELETE);
        publisherConfig2.setNumberOfMessagesToSend(sendToDeleteQueueCount);
        publisherConfig2.setPrintsPerMessageCount(sendToDeleteQueueCount / 5L);

        //Creating publishers
        AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
        publisherClient1.startClient();
        AndesClient publisherClient2 = new AndesClient(publisherConfig2, true);
        publisherClient2.startClient();

        //Receiving messages until message count gets stagnant and
        //Once done, stop client
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

        //Creating admin client
        AndesAdminClient admin = new AndesAdminClient(super.backendURL, sessionCookie);

        //testing if DLC queue is created
        Assert.assertNotNull(admin.getDlcQueue(), "DLC queue not created");

        //Testing if 100 messages are moved to DLC
        Message[] messagesInDLC = admin.browseQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString
                (TEST_QUEUE_PURGE), 0, 200);
        Assert.assertEquals(messagesInDLC.length, 100, "Messages have not been moved to DLC.");

        //Purging 'purgeTestQueue' queue
        admin.purgeQueue(TEST_QUEUE_PURGE);

        //Put a thread sleep so that we can make sure that the queue is deleted before testing its existence
        AndesClientUtils.sleepForInterval(1000);

        //Testing if the queue is deleted, it should not have been deleted
        Assert.assertNotNull(admin.getQueueByName(TEST_QUEUE_PURGE), "The queue has been deleted");

        //Testing if messages for queue are removed from DLC as well
        //Remaining number of messages in DLC should be 75 (destined to deleteTestQueue)
        messagesInDLC = admin.browseQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString
                (TEST_QUEUE_PURGE), 0, 200);
        Assert.assertEquals(messagesInDLC.length, 75, "Messages in DLC for " + TEST_QUEUE_PURGE + " not deleted");

        //Deleting 'deleteTestQueue'
        admin.deleteQueue(TEST_QUEUE_DELETE);

        //Put a thread sleep so that we can make sure that the queue is deleted before testing its existence
        AndesClientUtils.sleepForInterval(1000);

        //Testing if queue is not deleted
        Assert.assertNull(admin.getQueueByName(TEST_QUEUE_DELETE), "Queue is not deleted");

        //Testing if messages in DLC are all deleted
        messagesInDLC = admin.browseQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString
                (TEST_QUEUE_DELETE), 0, 200);
        Assert.assertNull(messagesInDLC, "Messages remaining in DLC");

        //Logging out
        loginLogoutClientForAdmin.logout();
    }

    /**
     * Revert changed configuration, purge and delete the queue.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @AfterClass()
    public void cleanup() throws Exception {
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient =
                new AndesAdminClient(super.backendURL, sessionCookie);

        andesAdminClient.deleteQueue(TEST_QUEUE_PURGE);
        andesAdminClient.deleteQueue(DLCQueueUtils.identifyTenantInformationAndGenerateDLCString(TEST_QUEUE_PURGE));

        loginLogoutClientForAdmin.logout();
        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);

    }
}