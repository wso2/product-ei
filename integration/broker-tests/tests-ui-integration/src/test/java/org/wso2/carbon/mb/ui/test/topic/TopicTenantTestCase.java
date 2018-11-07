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
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.mb.ui.test.topic;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.ClientMode;
import org.wso2.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.mb.integration.common.clients.MQTTConstants;
import org.wso2.mb.integration.common.clients.QualityOfService;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationUiBaseTest;
import org.wso2.mb.integration.common.utils.ui.pages.configure.AddNewTenantPage;
import org.wso2.mb.integration.common.utils.ui.pages.configure.ConfigurePage;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;
import org.wso2.mb.integration.common.utils.ui.pages.main.HomePage;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * UI test case(s) for tenant related topics.
 */
public class TopicTenantTestCase extends MBIntegrationUiBaseTest {

    /**
     * Initializes the test case.
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass()
    public void init() throws AutomationUtilException, XPathExpressionException, IOException {
        super.init();
    }

    /**
     * 1. Create the new tenant account
     * 2. Publish and subscribe MQTT messages using the created tenant admin account.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "queue", "mqtt"}, description = "https://wso2.org/jira/browse/MB-1180")
    public void performAddNewTenantPublishSubscribeQueue() throws XPathExpressionException, IOException,
            AndesClientConfigurationException, JMSException, NamingException, AndesClientException, MqttException {
        driver.get(getLoginURL());
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(getCurrentUserName(), getCurrentPassword());

        ConfigurePage configurePage = homePage.getConfigurePage();
        AddNewTenantPage addNewTenantPage = configurePage.getAddNewTenantPage();

        String domain = "home.com";
        String firstName = "Bob";
        String lastName = "Marley";
        String adminUserName = "bob";
        String adminPassword = "marleyandme";
        String adminPasswordRepeat = "marleyandme";
        String adminEmail = "bob.marley@home.com";

        addNewTenantPage.add(domain, firstName, lastName, adminUserName,
                adminPassword, adminPasswordRepeat, adminEmail);
        homePage.logout();

        String destinationName = "home.com/bobs-tenant-topic";

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        MQTTClientConnectionConfiguration mqttClientConnectionConfiguration =
                mqttClientEngine.getConfigurations(mbServer);
        mqttClientConnectionConfiguration.setBrokerUserName("bob!home.com");
        mqttClientConnectionConfiguration.setBrokerPassword("marleyandme");

        //create the subscribers
        mqttClientEngine.createSubscriberConnection(destinationName, QualityOfService.LEAST_ONCE, 1, true,
                ClientMode.BLOCKING, mqttClientConnectionConfiguration);

        mqttClientEngine.createPublisherConnection(destinationName, QualityOfService.LEAST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1, 1,
                ClientMode.BLOCKING, mqttClientConnectionConfiguration);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), 1, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                "The received message is incorrect");
    }

    /**
     * Stops the web driver.
     */
    @AfterClass()
    public void tearDown() {
        driver.quit();
    }
}
