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

package org.wso2.carbon.mb.ui.test.dlc;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.andes.server.queue.DLCQueueUtils;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.DLCBrowsePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.DLCContentPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueuesBrowsePage;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This test case will test following 3 scenarios from ui which are currently
 * available in dlc browse page.
 *      Restore message from dlc.
 *      Deleting message from dlc.
 *      Rerouting message from dlc.
 */
public class DLCQueueTestCase extends MBIntegrationUiBaseTest {
    private static final Log log = LogFactory.getLog(DLCQueueTestCase.class);

    /**
     * The index of the messageID column in queue content table. Assumed to be the second column
     */
    private static final int MESSAGE_ID_COLUMN_IN_QUEUE = 1;

    /**
     * The index of the messageID column in DLC content table. Assumed to be the third column
     */
    private static final int MESSAGE_ID_COLUMN_IN_DLC = 2;

    /**
     * The message count that will be sent by the publisher
     */
    private static final long SEND_COUNT = 15L;

    /**
     * The message count that is expected to be received by the consumer
     */
    private static final long EXPECTED_COUNT = 15L;

    /**
     * Home page instance for the test case
     */
    private HomePage homePage;

    /**
     * DLC test queue name
     */
    private static final String DLC_TEST_QUEUE = "DLCTestQueue";

    /**
     * The Queue name of the queue for which the message is being re-routed
     */
    private static final String REROUTE_QUEUE = "rerouteTestQueue";

    /**
     * The XPath for the message content table once the dlc is browsed
     */
    private static final String DLC_MESSAGE_CONTENT_TABLE = "mb.dlc.browse.content.table";

    /**
     * The XPath for the message content table once a queue is browsed
     */
    private static final String QUEUE_MESSAGE_CONTENT_TABLE = "mb.queue.browse.content.table";


    /**
     * Initializes the test case and changes the number of delivery attempts of a message to 1.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void initialize() throws AutomationUtilException, XPathExpressionException, IOException,
                                                                                                ConfigurationException {
        super.init();

        // Updating the redelivery attempts to 1 to speed up the test case.
        super.serverManager = new ServerConfigurationManager(mbServer);
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() + File.separator + "repository" +
                                            File.separator + "conf" + File.separator + "broker.xml";
        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);
        // Changing "maximumRedeliveryAttempts" value to "1" in broker.xml
        configurationEditor.updateProperty(AndesConfiguration.TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS, "1");
        // Restarting server
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * This test case will test restore,delete and reroute messages of
     * DeadLetter Channel from ui.
     * 1. Initially this test case will create a new queue to reroute messages.
     * 2. Delete queue message from dlc and check if message exist in dlc queue.
     * 3. Reroute queue message from dlc and check if queue message exist in browse queue ui.
     * 4. Reroute queue message from dlc and check if that queue message exist in reroute
     * browse queue ui.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     * @throws AutomationUtilException
     * @throws LogoutAuthenticationExceptionException
     */
    @Test()
    public void performDeadLetterChannelTestCase() throws XPathExpressionException, IOException,
            AndesAdminServiceBrokerManagerAdminException, AndesClientConfigurationException, JMSException,
            NamingException, AndesClientException, AutomationUtilException, LogoutAuthenticationExceptionException {

        // Number of checks for an update in DLC message count.
        int tries = 15;

        // Getting message count in DLC prior adding new messages to DLC.
        long messageCountPriorSendingMessages = this.getDLCMessageCount();

        log.info("Message count in DLC before sending messages : " + messageCountPriorSendingMessages);

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, DLC_TEST_QUEUE);
        // Amount of message to receive
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT + 200L);
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig.setAcknowledgeAfterEachMessageCount(EXPECTED_COUNT + 210L);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, DLC_TEST_QUEUE);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        // Waiting until the message count in DLC is different after the message were published.
        while (EXPECTED_COUNT > this.getDLCMessageCount()) {
            if (0 == tries) {
                Assert.assertEquals(this.getDLCMessageCount(), EXPECTED_COUNT,
                        "Did not receive the expected number of message to DLC.");
            }
            // Reducing try count
            tries--;
            //Thread sleep until message count in DLC is changed
            AndesClientUtils.sleepForInterval(15000L);
            log.info("Waiting for message count change.");
        }

        log.info("Message count in DLC after sending messages : " + this.getDLCMessageCount());

        // Stops consuming messages
        consumerClient.stopClient();

        String deletingMessageID;
        String restoringMessageID;
        String reroutingMessageID;

        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        homePage = loginPage.loginAs(mbServer.getContextTenant().getContextUser().getUserName(),
                                     mbServer.getContextTenant().getContextUser().getPassword());

        // Add an queue to test rerouting messages of DLC
        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        Assert.assertEquals(queueAddPage.addQueue(REROUTE_QUEUE), true);
        DLCBrowsePage dlcBrowsePage = homePage.getDLCBrowsePage();

        // Make sure that the dead letter channel is created
        Assert.assertNotNull(dlcBrowsePage.isDLCCreated(), "DeadLetter Channel not created. " + DLC_TEST_QUEUE);

        // Testing delete messages
        deletingMessageID = dlcBrowsePage.getDLCContent().deleteFunction();

        // Waiting till back end completes
        AndesClientUtils.sleepForInterval(5000);

        // Check if message is still present in the dead letter channel
        Assert.assertFalse(checkMessages(deletingMessageID,
                        DLCQueueUtils.identifyTenantInformationAndGenerateDLCString(DLC_TEST_QUEUE)),
                "Deleting messages from DLC is unsuccessful. Message present in DLC.");

        // Check if the message is present in the original queue
        Assert.assertFalse(checkMessages(deletingMessageID, DLC_TEST_QUEUE),
                           "Deleting messages from DLC is unsuccessful. Message present in queue.");

        log.info("Deleting messages in DLC is successful.");

        // Testing restore messages
        restoringMessageID = homePage.getDLCBrowsePage().getDLCContent().restoreFunction();

        // Waiting till back end completes
        AndesClientUtils.sleepForInterval(5000);

        // Check if the message is present in the original queue
        Assert.assertTrue(checkMessages(restoringMessageID, DLC_TEST_QUEUE),
                          "Restoring messages of DeadLetter Channel is unsuccessful. Message not present in queue.");

        // Check if message is deleted from the dead letter channel
        Assert.assertFalse(checkMessages(restoringMessageID,
                        DLCQueueUtils.identifyTenantInformationAndGenerateDLCString(DLC_TEST_QUEUE)),
                "Restoring messages of DeadLetter Channel is unsuccessful. Message present in DLC.");

        log.info("Restoring messages of DeadLetter Channel is successful.");

        // Testing reroute messages
        reroutingMessageID = homePage.getDLCBrowsePage().getDLCContent().rerouteFunction(REROUTE_QUEUE);

        // Waiting till back end completes
        AndesClientUtils.sleepForInterval(5000);

        // Check if the message is present in the re-routed queue
        Assert.assertTrue(checkMessages(reroutingMessageID, REROUTE_QUEUE),
                "Re-routing messages of DeadLetter Channel is unsuccessful. Message not present in queue.");

        // Check if message is deleted from the dead letter channel
        Assert.assertFalse(checkMessages(reroutingMessageID,
                        DLCQueueUtils.identifyTenantInformationAndGenerateDLCString(DLC_TEST_QUEUE)),
                "Re-routing messages of DeadLetter Channel is unsuccessful. Message present in DLC.");

        log.info("Re-routing messages of DeadLetter Channel is successful.");
    }

    /**
     * Check whether element is present or not.
     *
     * @param id which element check for its availability
     * @return availability of the element
     */
    public boolean isElementPresent(String id) {
        return driver.findElements(By.xpath(id)).size() != 0;
    }

    /**
     * Search messageID through all messages in a given queue.
     *
     * @param messageID searching messageID
     * @param queueName searching queue
     * @return true if the message is present
     */
    public boolean checkMessages(String messageID, String queueName) throws IOException {
        //if the queue is the DLC, check messages in DLC content table
        if (DLCQueueUtils.isDeadLetterQueue(queueName)) {
            //browse the dlc
            homePage.getDLCBrowsePage().getDLCContent();

            //if the table isn't empty, check whether the message is present
            if (isElementPresent(UIElementMapper.getInstance().getElement(DLC_MESSAGE_CONTENT_TABLE))) {

                return checkMessagesInTable(driver.findElement(By.xpath(UIElementMapper.getInstance().
                        getElement(DLC_MESSAGE_CONTENT_TABLE))), messageID, MESSAGE_ID_COLUMN_IN_DLC);
            } else {
                log.debug("No messages in: " + queueName);
                return false;
            }
        }
        //if the queue is a storage queue, check messages in the queue content table
        else {
            //browse the queue
            homePage.getQueuesBrowsePage().browseQueue(queueName);

            //if the table isn't empty, check whether the message is present the queue content table
            if (isElementPresent(UIElementMapper.getInstance().getElement(QUEUE_MESSAGE_CONTENT_TABLE))) {

                return checkMessagesInTable(driver.findElement(By.xpath(UIElementMapper.getInstance().
                        getElement(QUEUE_MESSAGE_CONTENT_TABLE))), messageID, MESSAGE_ID_COLUMN_IN_QUEUE);
            } else {
                log.debug("No messages in: " + queueName);
                return false;
            }
        }
    }

    /**
     * Search message ID through a message content table.
     *
     * @param table           the table in which the message should be in
     * @param messageID       messageID to be located
     * @param messageIdColumn the index of the column which has messageIDs
     * @return true if message is found
     */
    private boolean checkMessagesInTable(WebElement table, String messageID, int messageIdColumn) {
        List<WebElement> rowElementList = table.findElements(By.tagName("tr"));
        // Go through table rows and find the messageID
        for (WebElement row : rowElementList) {
            List<WebElement> columnList = row.findElements(By.tagName("td"));
            if ((columnList.get(messageIdColumn)).getText().equals(messageID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This class will restore andes acknowledgement time out system property
     * and quit the ui web driver.
     *
     * @throws IOException
     * @throws AutomationUtilException
     * @throws LogoutAuthenticationExceptionException
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException, LogoutAuthenticationExceptionException {
        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);
        driver.quit();
    }

    /**
     * Gets the number of messages in the DLC queue.
     *
     * @return The number of messages.
     * @throws AutomationUtilException
     * @throws RemoteException
     * @throws LogoutAuthenticationExceptionException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    private long getDLCMessageCount() throws AutomationUtilException, RemoteException,
            LogoutAuthenticationExceptionException, AndesAdminServiceBrokerManagerAdminException {
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(mbServer);
        String sessionCookie = loginLogoutClientForAdmin.login();
        AndesAdminClient andesAdminClient = new AndesAdminClient(backendURL, sessionCookie);
        long messageCount = andesAdminClient.getDlcQueue().getMessageCount();
        loginLogoutClientForAdmin.logout();

        return messageCount;
    }
}
