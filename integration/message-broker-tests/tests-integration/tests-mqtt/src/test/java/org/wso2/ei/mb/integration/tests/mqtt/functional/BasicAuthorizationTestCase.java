/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.mb.integration.tests.mqtt.functional;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.um.ws.api.stub.UserStoreExceptionException;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.ei.mb.integration.common.clients.ClientMode;
import org.wso2.ei.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.ei.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.ei.mb.integration.common.clients.MQTTConstants;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.wso2.ei.mb.integration.tests.mqtt.functional.util.RemoteAuthorizationManagerServiceClient;
import org.wso2.ei.mb.integration.tests.mqtt.functional.util.ResourceAdminServiceClient;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Verifies basic mqtt message transactions are functional.
 * <p/>
 * Send a single mqtt messages with unauthorized user and not recieve.
 * Send messages with authorized user and receive them.
 */
public class BasicAuthorizationTestCase extends MBIntegrationBaseTest {

	private UserManagementClient userMgtClient;
	private ResourceAdminServiceClient resourceAdminServiceClient;
	private static final int MAX_LEVELS = 50;
	private static final int UNAUTHORIZED_USERS = 10;

	/**
	 * Initialize super class.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public void prepare() throws Exception {
		super.init(TestUserMode.SUPER_TENANT_USER);
	}

	/**
	 * Setup test configuration by creating users and providing permission to access topics.
	 */
	@BeforeClass
	public void setupConfiguration() throws XPathExpressionException, IOException, ConfigurationException,
											AutomationUtilException, UserAdminUserAdminException,
											UserStoreExceptionException, ResourceAdminServiceExceptionException {

		super.serverManager = new ServerConfigurationManager(automationContext);

		ConfigurationEditor configurationEditor = new ConfigurationEditor(getBrokerConfigurationPath());

		configurationEditor.updateProperty(AndesConfiguration.TRANSPORTS_MQTT_USER_AUTHENTICATION, "REQUIRED");
		configurationEditor.updateProperty(AndesConfiguration.TRANSPORTS_MQTT_USER_AUTHORIZATION, "REQUIRED");
		configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);

		LoginLogoutClient loginLogoutClient = new LoginLogoutClient(automationContext);
		String sessionCookie = loginLogoutClient.login();
		userMgtClient = new UserManagementClient(backendURL, sessionCookie);
		resourceAdminServiceClient = new ResourceAdminServiceClient(backendURL, sessionCookie);
		resourceAdminServiceClient.addCollection("/_system/governance/permission/admin/mqtt", "connect", "", "");
		resourceAdminServiceClient.addCollection("/_system/governance/permission/admin/mqtt/topic/authorization",
												 "test", "", "");
		String resource = "/_system/governance/permission/admin/mqtt/topic/authorization/test";
		String usersList[] = new String[MAX_LEVELS];
		RemoteAuthorizationManagerServiceClient remoteAuthorizationManagerServiceClient =
				new RemoteAuthorizationManagerServiceClient(backendURL, sessionCookie);
		for (int i = 0; i < MAX_LEVELS; i++) {
			//create user
			userMgtClient.addUser("user" + i + "-mqtt", "passWord1@", null, "default");

			//create permission resource
			resourceAdminServiceClient.addCollection(resource, "" + i, "", "");
			resource = resource + "/" + i;

			//create role
			String users[] = new String[1];
			users[0] = "user" + i + "-mqtt";
			// Create roles
			usersList[i] = ("user" + i + "-mqtt");
			userMgtClient.addRole("mqtt-publish-" + i, users, null);
			userMgtClient.addRole("mqtt-subscribe-" + i, users, null);

			remoteAuthorizationManagerServiceClient.authorizeRole("mqtt-publish-" + i,
				resource.split("/_system/governance")[1], "publish");
			remoteAuthorizationManagerServiceClient.authorizeRole("mqtt-subscribe-" + i,
				resource.split("/_system/governance")[1], "subscribe");
		}
		remoteAuthorizationManagerServiceClient.authorizeRole("mqtt-connect", "/permission/admin/mqtt/connect",
				"authorize");

		userMgtClient.addRole("mqtt-connect", usersList, null);
		for (int i = 0; i < UNAUTHORIZED_USERS; i++)  {
			userMgtClient.addUser("un_user" + i + "-mqtt", "passWord1@", null, "default");
		}
	}

	/**
	 * Send a single mqtt message on qos {@link QualityOfService#LEAST_ONCE} and receive.
	 *
	 * @throws MqttException
	 */
	@Test(groups = {"wso2.mb", "mqtt"}, description = "Test Subscription and Authorization with wildcard topics")
	public void performAuthorizationBasedOnWildCardTopics()
			throws MqttException, XPathExpressionException, LogViewerLogViewerException, RemoteException {
		int noOfMessages = 1;
		boolean saveMessages = true;
		String topicPrefix = "authorization/test/0/#";
		MQTTClientEngine mqttClientEngineSub;
		mqttClientEngineSub = new MQTTClientEngine();
		MQTTClientConnectionConfiguration mqttClientConnectionConfiguration =
				mqttClientEngineSub.getConfigurations(automationContext);
		mqttClientConnectionConfiguration.setBrokerUserName("user0-mqtt");
		mqttClientConnectionConfiguration.setBrokerPassword("passWord1@");
		mqttClientEngineSub.createSubscriberConnection(mqttClientConnectionConfiguration, topicPrefix,
				QualityOfService.LEAST_ONCE, saveMessages, ClientMode.BLOCKING);
		topicPrefix = "authorization/test/0";
		for (int i = 1; i < MAX_LEVELS; i++) {
			topicPrefix = topicPrefix + "/" + i;
			MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
			mqttClientConnectionConfiguration = mqttClientEngine.getConfigurations(automationContext);
			mqttClientConnectionConfiguration.setBrokerUserName("user" + i + "-mqtt");
			mqttClientConnectionConfiguration.setBrokerPassword("passWord1@");
			mqttClientEngine.createPublisherConnection(mqttClientConnectionConfiguration, topicPrefix,
				QualityOfService.LEAST_ONCE, MQTTConstants.TEMPLATE_PAYLOAD, noOfMessages, ClientMode.BLOCKING);
		}

		mqttClientEngineSub.waitUntilAllMessageReceivedAndShutdownClients();
		List<MqttMessage> receivedMessages = mqttClientEngineSub.getReceivedMessages();
		Assert.assertEquals(receivedMessages.size(), MAX_LEVELS - 1, "The received message count is incorrect.");
		Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
							"The received message is incorrect");

	}

	/**
	 * This will test the publish and subscribe flow with the authorized users.
	 */
	@Test(groups = {"wso2.mb", "mqtt"}, description = "Test Publish and Subscribe with Authorization")
	public void performAuthorizationForPublishAndSubscribe()
			throws MqttException, XPathExpressionException, LogViewerLogViewerException, RemoteException {
		int noOfMessages = 500;
		boolean saveMessages = true;

		String topicPrefix = "authorization/test";
		for (int i = 0; i < 10; i++) {
			topicPrefix = topicPrefix + "/" + i;
			MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
			MQTTClientConnectionConfiguration mqttClientConnectionConfiguration =
					mqttClientEngine.getConfigurations(automationContext);
			mqttClientConnectionConfiguration.setBrokerUserName("user" + i + "-mqtt");
			mqttClientConnectionConfiguration.setBrokerPassword("passWord1@");
			mqttClientEngine.createSubscriberConnection(mqttClientConnectionConfiguration, topicPrefix,
														QualityOfService.LEAST_ONCE, saveMessages,
														ClientMode.BLOCKING);
			mqttClientEngine.createPublisherConnection(mqttClientConnectionConfiguration, topicPrefix,
													   QualityOfService.LEAST_ONCE,
													   MQTTConstants.TEMPLATE_PAYLOAD, noOfMessages,
													   ClientMode.BLOCKING);
			mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();
			List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();
			Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");
			Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
								"The received message is incorrect");
		}
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
