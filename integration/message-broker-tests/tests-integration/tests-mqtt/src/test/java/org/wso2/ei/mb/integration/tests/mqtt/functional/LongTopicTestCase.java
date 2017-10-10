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

import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * Verifies message send/receive functionality within an extended topic hierarchy.
 */
public class LongTopicTestCase extends MBIntegrationBaseTest {

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
     * Send message and receive for a long topic hierarchy.
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Send message and receive for a long topic hierarchy")
    public void performLongTopicTestCase() throws MqttException, XPathExpressionException {
        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();

        String longTopic = "1/2/3/this_is_a_long_topic_that_needs_to/work/4/5/6/7/8";

        //create the subscribers
        mqttClientEngine.createSubscriberConnection(longTopic, QualityOfService.LEAST_ONCE, 1, true,
                ClientMode.BLOCKING, automationContext);

        mqttClientEngine.createPublisherConnection(longTopic, QualityOfService.LEAST_ONCE,
                MQTTConstants.TEMPLATE_PAYLOAD, 1, 1,
                ClientMode.BLOCKING, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        List<MqttMessage> receivedMessages = mqttClientEngine.getReceivedMessages();

        Assert.assertEquals(receivedMessages.size(), 1, "The received message count is incorrect.");

        Assert.assertEquals(receivedMessages.get(0).getPayload(), MQTTConstants.TEMPLATE_PAYLOAD,
                "The received message is incorrect");

    }
}
