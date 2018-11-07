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

package org.wso2.mb.platform.tests.clustering.mqtt;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.*;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.platform.tests.clustering.mqtt.DataProvider.QualityOfServiceDataProvider;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

/**
 * Test class to test MQTT message compatibility of compression enabled and disabled packs, in cluster mode. Main aim
 * of this test is, to get a prediction about compatibility of MB 3.1.0 and MB 3.0.0.
 * Before starting cluster test cases, need to start two clusters, one is without enabling compression and the other
 * one is with compression.
 * In this test case, publishing without compression. Then, sending message from the compression enabled server to
 * the subscriber, and checking the other way also.
 */
public class LZ4CompressCompatibilityMQTTClusterTestCase extends MQTTPlatformBaseTest {

    /**
     * Prepare environment for tests.
     *
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws XPathExpressionException, URISyntaxException, SAXException, XMLStreamException,
            LoginAuthenticationExceptionException, IOException, AutomationUtilException {

        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

    }

    /**
     * Send messages from one node and received messages from another node. One node has enabled compression, and the
     * other node hasn't enabled compression.
     *
     * @throws MqttException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.mb", description = "Single topic two node test compress compatibility test case",
            dataProvider = "QualityOfServiceDataProvider", dataProviderClass = QualityOfServiceDataProvider.class)
    public void testSingleTopicTwoNodeCompressCompatibility(QualityOfService qualityOfService)
            throws MqttException, XPathExpressionException, IOException {

        // Get 2 instances. One is a compression enabled node, and the other one is a node without enabling compression
        AutomationContext automationContextForMB2 = getAutomationContextWithKey("mb002");
        AutomationContext automationContextForMB3 = getAutomationContextWithKey("mb003");

        String topic = "lz4CompressCompatibilityTopic";
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        //Input file size is 256KB
        int messageSize = 250 * 1024;

        byte[] payload = new byte[messageSize];
        new Random().nextBytes(payload);

        // Checking the 1st way

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode2 =
                buildConfiguration(automationContextForMB2);
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode3 =
                buildConfiguration(automationContextForMB3);

        // Create the subscriber
        mqttClientEngine.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, true,
                ClientMode.BLOCKING, clientConnectionConfigurationForNode2);

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        // Create the publisher
        mqttClientEngine.createPublisherConnection(topic, qualityOfService, payload, noOfPublishers, noOfMessages,
                ClientMode.BLOCKING, clientConnectionConfigurationForNode3);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();
        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");
        Assert.assertEquals(receivedMessages.get(0).getPayload(), payload, "The received message is incorrect");

        // Checking the 2nd way: opposite of the 1st way

        MQTTClientEngine mqttClientEngine2 = new MQTTClientEngine();
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode4 =
                buildConfiguration(automationContextForMB2);
        MQTTClientConnectionConfiguration clientConnectionConfigurationForNode5 =
                buildConfiguration(automationContextForMB3);

        // Create the subscriber
        mqttClientEngine2.createSubscriberConnection(topic, qualityOfService, noOfSubscribers, true,
                ClientMode.BLOCKING, clientConnectionConfigurationForNode5);

        AndesClientUtils.sleepForInterval(AndesClientConstants.DEFAULT_CLUSTER_SYNC_TIME);

        // Create the publisher
        mqttClientEngine2.createPublisherConnection(topic, qualityOfService, payload, noOfPublishers, noOfMessages,
                ClientMode.BLOCKING, clientConnectionConfigurationForNode4);

        mqttClientEngine2.waitUntilAllMessageReceivedAndShutdownClients();
        List<MqttMessage> receivedMessages2 = mqttClientEngine2.getReceivedMessages();

        Assert.assertEquals(receivedMessages2.size(), noOfMessages, "The received message count is incorrect.");
        Assert.assertEquals(receivedMessages2.get(0).getPayload(), payload, "The received message is incorrect");
    }
}