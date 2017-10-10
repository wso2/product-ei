/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.ei.mb.integration.common.clients.MQTTConstants;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.clients.ClientMode;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.wso2.ei.mb.integration.tests.mqtt.DataProvider.QualityOfServiceDataProvider;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * Check for basic publish/subscribe on QOS 0,1 and 2.
 */
public class QOSTestCase extends MBIntegrationBaseTest {

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
     * Send a message and receive in QOS {@link QualityOfService#MOST_ONCE}, {@link QualityOfService#LEAST_ONCE} and
     * {@link QualityOfService#EXACTLY_ONCE}.
     *
     * @param qualityOfService The Quality of service to test
     * @throws MqttException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Send message and receive in QOS 0",
            dataProvider = "QualityOfServiceDataProvider", dataProviderClass = QualityOfServiceDataProvider.class)
    public void performQOS0TestCase(QualityOfService qualityOfService)
            throws MqttException, XPathExpressionException {
        String topicName = "QOSTestCase" + qualityOfService.getValue();
        int noOfSubscribers = 1;
        int noOfPublishers = 1;
        int noOfMessages = 1;
        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        //create the subscribers
        mqttClientEngine.createSubscriberConnection(topicName, qualityOfService, noOfSubscribers, true,
                ClientMode.ASYNC, automationContext);

        mqttClientEngine.createPublisherConnection(topicName, qualityOfService, MQTTConstants.TEMPLATE_PAYLOAD,
                noOfPublishers, noOfMessages, ClientMode.ASYNC, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), noOfMessages, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                "The received message is incorrect");
    }

}
