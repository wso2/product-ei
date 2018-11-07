/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.mb.integration.tests.amqp.functional;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
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
import java.rmi.RemoteException;

/**
 * This class includes test cases to test expired message deletion in DLC
 */
public class DLCMessageExpiryTestCase extends MBIntegrationBaseTest {
    /**
     * Test queue name
     */
    private static final String TEST_QUEUE_DLC_EXPIRY = "DLCTestQueue";

    /**
     * The default andes acknowledgement wait timeout.
     */
    private String defaultAndesAckWaitTimeOut = null;

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
     * Set topicMessageDeliveryStrategy to DISCARD_ALLOWED so that broker will simulate an acknowledgement
     * if some subscribers are slow to acknowledge the message
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws ConfigurationException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws URISyntaxException
     * @throws AutomationUtilException
     */
    @BeforeClass
    public void setupConfiguration() throws XPathExpressionException, IOException,
            ConfigurationException, SAXException, XMLStreamException, LoginAuthenticationExceptionException,
            URISyntaxException, AutomationUtilException {
        super.serverManager = new ServerConfigurationManager(automationContext);
        String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() +
                File.separator + "repository" + File.separator + "conf" + File.separator + "broker.xml";
        ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);
        configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_PRE_DELIVERY_EXPIRY_DELETION_INTERVAL,
                "60");
        configurationEditor.updateProperty(AndesConfiguration
                .PERFORMANCE_TUNING_PERIODIC_EXPIRY_MESSAGE_DELETION_INTERVAL, "60");
        configurationEditor.updateProperty(AndesConfiguration.TRANSPORTS_AMQP_MAXIMUM_REDELIVERY_ATTEMPTS, "1");
        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
        // Get current "AndesAckWaitTimeOut" system property.
        defaultAndesAckWaitTimeOut = System.getProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);
        // Setting system property "AndesAckWaitTimeOut" for andes
        System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, "3000");
    }

    /**
     * Let the messages in the DLC to expire and check whether those are detected and deleted by the periodic message
     * expiry deletion task.
     *
     * @throws XPathExpressionException
     * @throws AutomationUtilException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.mb", description = "Messages in DLC got expired and eventually deleted by the periodic "
            + "deletion task")
    public void messageExpiryInDLCTestCase() throws XPathExpressionException, AutomationUtilException,
            AndesClientConfigurationException, IOException, AndesClientException, JMSException, NamingException,
            AndesAdminServiceBrokerManagerAdminException, InterruptedException {

        //Setting values for the sent and received message counts
        long sendMessageCount = 10L;

        // Logging in
        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(super.automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, TEST_QUEUE_DLC_EXPIRY);
        consumerConfig.setAcknowledgeMode(JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE);

        //Creating 2 consumers with the 2 configurations
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();

        // Creating publisher configuration with destination queue = 'DLCTestQueue' and message count = 1000
        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, TEST_QUEUE_DLC_EXPIRY);
        publisherConfig.setNumberOfMessagesToSend(sendMessageCount);
        // Set the message expiry to 3 minutes
        publisherConfig.setJMSMessageExpiryTime(180000L);

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();
        // 2 min sleep to let the messages to move DLC
        Thread.sleep(120000L);
        // Since maximum redelivery attempts reached those 10 messages should be routed to DLC
        Assert.assertEquals(getDLCMessageCount(sessionCookie),10L,"DLC Message routing failed");
        // 2 min sleep to let the expiry task delete the expired messages in DLC
        Thread.sleep(120000L);
        // Since all the messages in DLC is expired, those should be deleted by periodic task
        Assert.assertEquals(getDLCMessageCount(sessionCookie), 0, "DLC Message expiry failed");
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
    private long getDLCMessageCount(String sessionCookie) throws AutomationUtilException, RemoteException,
            AndesAdminServiceBrokerManagerAdminException{

        AndesAdminClient andesAdminClient = new AndesAdminClient(backendURL, sessionCookie);
        long messageCount = andesAdminClient.getDlcQueue().getMessageCount();
        return messageCount;
    }

    /**
     * Revert changed configurations
     *
     * @throws AutomationUtilException
     * @throws IOException
     */
    @AfterClass()
    public void cleanup() throws AutomationUtilException, IOException {

        if (StringUtils.isBlank(defaultAndesAckWaitTimeOut)) {
            System.clearProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY);
        } else {
            System.setProperty(AndesClientConstants.ANDES_ACK_WAIT_TIMEOUT_PROPERTY, defaultAndesAckWaitTimeOut);
        }

        //Revert back to original configuration.
        super.serverManager.restoreToLastConfiguration(true);

    }


}
