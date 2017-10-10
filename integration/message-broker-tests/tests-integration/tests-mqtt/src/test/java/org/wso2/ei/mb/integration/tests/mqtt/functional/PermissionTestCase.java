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
package org.wso2.ei.mb.integration.tests.mqtt.functional;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.ei.mb.integration.common.clients.ClientMode;
import org.wso2.ei.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.ei.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.ei.mb.integration.common.clients.MQTTConstants;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * The following test cases are related to permissions of users and tenant domains.
 */
public class PermissionTestCase extends MBIntegrationBaseTest {

    /**
     * Initialize super class.
     *
     * @throws XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
    }

    /**
     * Publishes and subscribes from admin in carbon.super domain.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"})
    public void performAdminPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, FrameworkConstants.SUPER_TENANT_ADMIN, "admin-topic");
    }

    /**
     * Publishes and subscribes from a user in carbon.user domain.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"})
    public void performUserPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, "user1", "user-topic");
    }

    /**
     * Publishes and subscribes from admin in carbon.super domain.
     * Publishes and subscribes from a user in carbon.user domain to the same topic.
     * Exception should occur.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"}, expectedExceptions = MqttException.class)
    public void performAdminAndUserPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, FrameworkConstants.SUPER_TENANT_ADMIN, "user-admin-topic");
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, "user1", "user-admin-topic");
    }

    /**
     * Publishes and subscribes from admin in a tenant(testtenant1.com") domain.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"})
    public void performTenantAdminPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase("tenant1", "admin", "tenant-admin-topic");
    }

    /**
     * Publishes and subscribes from a user in a tenant(testtenant1.com") domain.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"})
    public void performTenantUserPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase("tenant1", "tenant1user1", "tenant-user-topic");
    }

    /**
     * Publishes and subscribes from admin in a tenant(testtenant1.com") domain.
     * Publishes and subscribes from a user in a tenant(testtenant1.com") domain to the same topic.
     * Exception should occur.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"}, expectedExceptions = MqttException.class)
    public void performTenantAdminAndUserPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase("tenant1", "admin", "tenant-user-admin-topic");
        runTestCase("tenant1", "tenant1user1", "tenant-user-admin-topic");
    }

    /**
     * Publishes and subscribes from admin in carbon.super domain.
     * Publishes and subscribes from admin in a tenant(testtenant1.com") domain to the same topic.
     * Exception should occur.
     * Redo the same test by changing the order.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"}, expectedExceptions = MqttException.class)
    public void performDomainAcrossAdminPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, FrameworkConstants.SUPER_TENANT_ADMIN, "cross-admin-topic-1");
        runTestCase("tenant1", "admin", "cross-admin-topic-1");

        runTestCase("tenant1", "admin", "cross-admin-topic-2");
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, FrameworkConstants.SUPER_TENANT_ADMIN, "cross-admin-topic-2");
    }

    /**
     * Publishes and subscribes from a user in carbon.super domain.
     * Publishes and subscribes from a user in a tenant(testtenant1.com") domain to the same topic.
     * Exception should occur.
     * Redo the same test by changing the order.
     *
     * @throws MqttException            
     * @throws XPathExpressionException 
     */
    @Test(groups = {"wso2.mb", "mqtt"}, expectedExceptions = MqttException.class)
    public void performDomainAcrossUserPermissionTestCase() throws MqttException, XPathExpressionException {
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, "user1", "cross-user-topic-1");
        runTestCase("tenant1", "tenant1user1", "cross-user-topic-1");

        runTestCase("tenant1", "tenant1user1", "cross-user-topic-2");
        runTestCase(FrameworkConstants.SUPER_TENANT_KEY, "user1", "cross-user-topic-1");

    }

    /**
     * Publishes and receives mqtt messages from a user of a tenant group to a destination.
     *
     * @param tenantDomainKey The tenant domain key of the user. Refers to the key in automation.xml
     * @param userKey         The user's key in automation.xml
     * @param destinationName The topic destination.
     * @throws MqttException
     * @throws XPathExpressionException
     */
    public void runTestCase(String tenantDomainKey, String userKey, String destinationName) throws MqttException,
                                                                                            XPathExpressionException {

        // Get user's automation context.
        AutomationContext userAutomationContext = new AutomationContext("MB", "mb001", tenantDomainKey, userKey);
        User contextUser = userAutomationContext.getContextTenant().getContextUser();

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        // Setting username and password for the client.
        MQTTClientConnectionConfiguration mqttClientConnectionConfiguration =
                                                            mqttClientEngine.getConfigurations(userAutomationContext);
        mqttClientConnectionConfiguration.setBrokerUserName(contextUser.getUserName());
        mqttClientConnectionConfiguration.setBrokerPassword(contextUser.getPassword());

        // Creating subscribers
        mqttClientEngine.createSubscriberConnection(destinationName, QualityOfService.LEAST_ONCE, 1, true,
                                                                ClientMode.BLOCKING, mqttClientConnectionConfiguration);

        // Creating publishers
        mqttClientEngine.createPublisherConnection(destinationName, QualityOfService.LEAST_ONCE,
                        MQTTConstants.TEMPLATE_PAYLOAD, 1, 10, ClientMode.BLOCKING, mqttClientConnectionConfiguration);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        // Evaluating
        Assert.assertEquals(receivedMessages.size(), 10, "The received message count is incorrect.");
        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD, "The received " +
                                                                                              "message is incorrect");
    }
}
