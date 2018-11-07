/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.mb.platform.tests.clustering.mqtt;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Tenant;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.mb.integration.common.clients.ClientMode;
import org.wso2.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.mb.integration.common.clients.MQTTConstants;
import org.wso2.mb.integration.common.clients.QualityOfService;
import org.wso2.mb.integration.common.clients.operations.mqtt.blocking.MQTTBlockingPublisherClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;


/**
 * Includes test cases to verify MQTT security. Following test cases will run all modes in {@link TestUserMode} 
 */
public class ClusteredSecurityTestCase extends MQTTPlatformBaseTest {

    private static final Log log = LogFactory.getLog(ClusteredSecurityTestCase.class);
    
    /**
     * Holds information about cluster marked with 'mb002' in automation.xml.
     */
    private AutomationContext automationContextForMB2;
    
    /**
     * The current test user mode
     */
    private TestUserMode userMode;
    
    
    /**
     * Instantiates the testcase class with specified user mode
     * @param userMode supplied user mode ( by data provider)
     */
    @Factory(dataProvider = "userModeProvider")
    public ClusteredSecurityTestCase(TestUserMode userMode){
        
        this.userMode = userMode;
        
    }
    
    /**
     * Initialize super class.
     * @throws XPathExpressionException 
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void prepare() throws XPathExpressionException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        automationContextForMB2 = getAutomationContextWithKey("mb002");
    }

    /**
     * Try to connect to MB using a invalid user name and a password for in
     * super user mode and another modes specified in 
     * {@link ClusteredSecurityTestCase#userModeProvider()}.
     *
     * @throws MqttException this is expected only if it's due to a bad user name or a password
     * @throws XPathExpressionException if test frame work can't read the configurations.
     */
    @Test(groups = { "wso2.mb", "mqtt" }, description = "Try to connect to MB using a invalid user name and a password", 
            expectedExceptions = MqttException.class, expectedExceptionsMessageRegExp = ".*Bad user name or password.*")
    public void performInvalidUserCredentialsTestCase() throws MqttException, XPathExpressionException {
        String topic = "topic";
        int inValidNumberOfMessages = 1; // we don't really expect to send
                                         // messages.

        String invalidUserName = "invalidUserName";        
        Tenant currentTenant = automationContextForMB2.getContextTenant();

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        
        MQTTClientConnectionConfiguration configuration =
                                                          mqttClientEngine.getConfigurations(automationContextForMB2);
                
        if ( automationContextForMB2.getSuperTenant().getDomain().equals(currentTenant.getDomain())){
            invalidUserName = invalidUserName + "@" +  currentTenant.getDomain();
        }
        
        configuration.setBrokerUserName(invalidUserName);
        configuration.setBrokerPassword("invalidPassword");

        new MQTTBlockingPublisherClient(configuration, mqttClientEngine.generateClientID(), topic,
                                        QualityOfService.LEAST_ONCE,
                                        MQTTConstants.TEMPLATE_PAYLOAD, inValidNumberOfMessages);

    }

    
    /**
     * Send a single mqtt message on qos {@link QualityOfService#LEAST_ONCE} and
     * receive it. However testcase will be run for all user modes specified in
     * {@link ClusteredSecurityTestCase#userModeProvider()}
     *
     * @throws MqttException if an error occurs 
     * @throws XPathExpressionException if test frame work can't read the configurations.
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Single mqtt message send receive test case with non admin user, super tenant")
    public void performBasicSendReceiveTestCaseWithNonAdminCredentials() throws MqttException, XPathExpressionException {
        String topic = "topic";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;
        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        
        MQTTClientConnectionConfiguration configuration =
                mqttClientEngine.getConfigurations(automationContextForMB2);
        
        Tenant tenant = automationContextForMB2.getContextTenant();
        User user = tenant.getContextUser();
        configuration.setBrokerUserName(user.getUserName());
        configuration.setBrokerPassword(user.getPassword());
        
        //create the subscribers
        mqttClientEngine.createSubscriberConnection(topic, QualityOfService.LEAST_ONCE, noOfSubscribers, saveMessages,
                ClientMode.BLOCKING, configuration);

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        mqttClientEngine.createPublisherConnection(topic, QualityOfService.LEAST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, noOfPublishers,
                noOfMessages, ClientMode.BLOCKING, configuration);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                "The received message is incorrect");

    }
    
    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][] { new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                                      new TestUserMode[]{TestUserMode.SUPER_TENANT_USER},
                                      new TestUserMode[]{TestUserMode.TENANT_ADMIN},
                                      new TestUserMode[]{TestUserMode.TENANT_USER},};
    }

    
    
    
}
