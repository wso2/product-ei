/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
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
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.DLCBrowsePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.DLCContentPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;
import org.wso2.mb.integration.common.utils.ui.pages.main.QueueAddPage;

import java.io.File;
import java.io.IOException;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;

/**
 * This test case will test dead letter channel reroute all function for durable topic messages.
 */
public class DLCReRouteAllTestCase extends MBIntegrationUiBaseTest {
    private static final Log log = LogFactory.getLog(DLCReRouteAllTestCase.class);
    private static final long SEND_COUNT = 2L;
    private static final long EXPECTED_COUNT = 2L;

    /**
     * DLC test queue name
     */
    private static final String DLC_TEST_DURABLE_TOPIC = "DLCReRouteAllTopic";

    /**
     * DLC reroute queue name
     */
    private static final String REREOUTE_QUEUE_NAME = "DLCReRouteAllQueue";

    /**
     * The home page of MB management console
     */
    private HomePage homePage = null;

    /**
     * The default andes acknowledgement wait timeout.
     */
    private String defaultAndesAckWaitTimeOut = null;

    /**
     * Initializes test. This class will initialize web driver and
     * restart server with altered broker.xml
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws IOException
     */
    @BeforeClass()
    public void initialize() throws AutomationUtilException, XPathExpressionException, IOException, ConfigurationException {
        super.init();

        super.serverManager = new ServerConfigurationManager(mbServer);
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() + File.separator + "repository" +
                                            File.separator + "conf" + File.separator + "broker.xml";
        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);
        // Changing "maximumRedeliveryAttempts" value to "2" in broker.xml
        configurationEditor.updateProperty(AndesConfiguration.TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS, "1");
        configurationEditor.updateProperty(AndesConfiguration.MANAGEMENT_CONSOLE_ALLOW_REREOUTE_ALL_IN_DLC, "true");
        // Restarting server
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * Purge all messages in dlc before test starts using ui.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @BeforeMethod()
    public void cleanDeadLetterChannel() throws XPathExpressionException, IOException {
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        homePage = loginPage.loginAs(mbServer.getContextTenant()
                .getContextUser().getUserName(), mbServer
                .getContextTenant().getContextUser()
                .getPassword());

        DLCBrowsePage dlcBrowsePage = homePage.getDLCBrowsePage();
        //Testing delete messages
        DLCContentPage dlcContentPage = dlcBrowsePage.getDLCContent();
        dlcContentPage.deleteAllDLCMessages();
    }

    /**
     * Create a queue to reroute the messages
     *
     * @throws IOException
     */
    @BeforeMethod(dependsOnMethods = {"cleanDeadLetterChannel"})
    public void createReRouteQueue() throws IOException {
        QueueAddPage queueAddPage = homePage.getQueueAddPage();
        queueAddPage.addQueue(REREOUTE_QUEUE_NAME);
    }

    /**
     * This method will add durable topic messages to dead letter channel.
     *
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientException
     */
    @BeforeMethod(dependsOnMethods = {"createReRouteQueue"})
    public void addDurableTopicMessagesToDLC()
            throws AndesClientConfigurationException, NamingException,
            JMSException, IOException, AndesClientException, XPathExpressionException {

        // Get current "AndesAckWaitTimeOut" system property.
        defaultAndesAckWaitTimeOut = System.getProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);

        // Setting system property "AndesAckWaitTimeOut" for andes
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "0");

        // Creating a initial JMS consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new
                AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, DLC_TEST_DURABLE_TOPIC);
        // Amount of message to receive
        consumerConfig.setDurable(true, DLC_TEST_DURABLE_TOPIC);
        consumerConfig.setSubscriptionID("durable-reroute-topic-sub-1");
        consumerConfig.setMaximumMessagesToReceived(EXPECTED_COUNT + 200L);
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);
        consumerConfig.setAcknowledgeAfterEachMessageCount(EXPECTED_COUNT + 200L);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, DLC_TEST_DURABLE_TOPIC);
        publisherConfig.setNumberOfMessagesToSend(SEND_COUNT);

        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();

        int tries = 15;

        while (4L != consumerClient.getReceivedMessageCount()) {
            if(0 == tries){
                Assert.fail("Expected amount of messages were not received");
            }
            // Reducing try count
            tries--;
            //Thread sleep until message count in DLC is changed
            AndesClientUtils.sleepForInterval(15000L);
            log.info("Waiting for message count change.");
        }
    }


    /**
     * This test will verify reroute all messages functions in dlc for durable topic messages.
     *
     * @throws java.io.IOException
     */
    @Test()
    public void performDurableTopicDLCReRouteAllTestCase() throws IOException {
        DLCBrowsePage dlcBrowsePage = homePage.getDLCBrowsePage();
        Assert.assertNotNull(dlcBrowsePage.isDLCCreated(), "DeadLetter Channel not created. " + DLC_TEST_DURABLE_TOPIC);

        //Testing delete messages
        DLCContentPage dlcContentPage = dlcBrowsePage.getDLCContent();

        dlcContentPage.rerouteAllFunction("carbon:durable-reroute-topic-sub-1", REREOUTE_QUEUE_NAME);

        int messageCount = homePage.getQueuesBrowsePage().getMessageCount(REREOUTE_QUEUE_NAME);

        Assert.assertEquals(SEND_COUNT, messageCount, "Messages were not rerouted");
    }

    /**
     * This method will restore all the configurations back.
     * Following configurations will be restored.
     * 1. AndesAckWaitTimeOut system property.
     * 2. Restore default broker.xml and restart server.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass()
    public void tearDown() throws IOException, AutomationUtilException {

        // Setting system property "AndesAckWaitTimeOut" to default value.
        // This will set andes ack wait timeout to 0. To send messages to
        // DLC fast wait time has set to 0.
        if (StringUtils.isBlank(defaultAndesAckWaitTimeOut)) {
            System.clearProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY,
                    defaultAndesAckWaitTimeOut);
        }

        restartInPreviousConfiguration();
        driver.quit();
    }
}
