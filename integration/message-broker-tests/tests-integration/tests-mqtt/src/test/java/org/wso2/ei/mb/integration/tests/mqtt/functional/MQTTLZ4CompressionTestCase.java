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
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ei.mb.integration.common.clients.ClientMode;
import org.wso2.ei.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * This class contains tests for MQTT message content validity, with compression.
 */
public class MQTTLZ4CompressionTestCase extends MBIntegrationBaseTest {

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
     * Set allowCompression to true so that broker will compress messages before storing into the database, and
     * reduce maximum content chunk size to 100 from default value (65500); to create more content chunks
     * from compressed content, to check chunk data retrieval.
     *
     * @throws XPathExpressionException
     * @throws java.io.IOException
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException
     */
    @BeforeClass
    public void setupConfiguration() throws XPathExpressionException, IOException, ConfigurationException,
            AutomationUtilException {

        super.serverManager = new ServerConfigurationManager(automationContext);

        ConfigurationEditor configurationEditor = new ConfigurationEditor(getBrokerConfigurationPath());

        configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_ALLOW_COMPRESSION, "true");
        configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_MAX_CONTENT_CHUNK_SIZE, "100");

        configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);
    }

    /**
     * Send a single mqtt message on qos {@link QualityOfService#LEAST_ONCE} and receive.
     *
     * @throws MqttException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Single mqtt message send compress receive test case")
    public void performBasicSendCompressReceiveTestCase() throws MqttException, XPathExpressionException,
            IOException {

        String topic = "MQTTLZ4CompressionTestCase";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        boolean saveMessages = true;
        //Input file size is 256KB
        int messageSize = 250 * 1024;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        //create the subscribers
        mqttClientEngine.createSubscriberConnection(topic, QualityOfService.LEAST_ONCE, noOfSubscribers, saveMessages,
                ClientMode.BLOCKING, automationContext);

        byte[] payload = new byte[messageSize];
        new Random().nextBytes(payload);

        mqttClientEngine.createPublisherConnection(topic, QualityOfService.LEAST_ONCE, payload, noOfPublishers,
                noOfMessages, ClientMode.BLOCKING, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");
        Assert.assertEquals(receivedMessages.get(0).getPayload(), payload, "The received message is incorrect");
    }

    /**
     * Restore to the previous configurations when the message content compression test is complete.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    @AfterClass
    public void tearDown() throws IOException, AutomationUtilException {
        super.serverManager.restoreToLastConfiguration(true);
    }
}
