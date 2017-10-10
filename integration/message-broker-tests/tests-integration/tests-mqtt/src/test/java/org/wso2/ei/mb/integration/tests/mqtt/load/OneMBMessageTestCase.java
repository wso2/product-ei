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

package org.wso2.ei.mb.integration.tests.mqtt.load;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.mb.integration.common.clients.AndesMQTTClient;
import org.wso2.ei.mb.integration.common.clients.ClientMode;
import org.wso2.ei.mb.integration.common.clients.MQTTClientEngine;
import org.wso2.ei.mb.integration.common.clients.MQTTConstants;
import org.wso2.ei.mb.integration.common.clients.QualityOfService;
import org.wso2.ei.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.ei.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.ei.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.wso2.ei.mb.integration.tests.mqtt.DataProvider.QualityOfServiceDataProvider;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test case that sends 10 1MB messages in MQTT protocol.
 */
public class OneMBMessageTestCase extends MBIntegrationBaseTest {

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
     * Send and receive 1MB of message via QOS {@link QualityOfService#MOST_ONCE},
     * {@link QualityOfService#LEAST_ONCE} and {@link QualityOfService#EXACTLY_ONCE}.
     *
     * @param qualityOfService The Quality of service to test
     * @throws MqttException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "mqtt"}, description = "Send and receive large Message of 1 MB",
            dataProvider = "QualityOfServiceDataProvider", dataProviderClass = QualityOfServiceDataProvider.class)
    public void performOneMBLoadTestCase(QualityOfService qualityOfService)
            throws MqttException, IOException, XPathExpressionException {
        int sendCount = 10;
        int noOfSubscribers = 1;
        int noOfPublishers = 1;

        MQTTClientEngine mqttClientEngine = new MQTTClientEngine();
        String topicName = "OneMBLoadTestTopic";

        // Creating a file of 10MB
        AndesClientUtils.createMockFile(AndesClientConstants.FILE_PATH_FOR_ONE_KB_SAMPLE_FILE,
                AndesClientConstants.FILE_PATH_FOR_CREATING_A_NEW_FILE, 1024);

        byte[] oneMBBytes = Files.readAllBytes(Paths.get(AndesClientConstants.FILE_PATH_FOR_CREATING_A_NEW_FILE));

        //create the subscribers
        mqttClientEngine.createSubscriberConnection(topicName, qualityOfService, noOfSubscribers, false,
                ClientMode.BLOCKING, automationContext);

        mqttClientEngine.createPublisherConnection(topicName, qualityOfService, oneMBBytes, noOfPublishers, sendCount,
                ClientMode.BLOCKING, automationContext);

        mqttClientEngine.waitUntilAllMessageReceivedAndShutdownClients();

        for (MqttMessage mqttMessage : mqttClientEngine.getSubscriberList().get(0).getReceivedMessages()) {
            Assert.assertEquals(mqttMessage.getPayload(), oneMBBytes,
                    "The received message is incorrect");
        }
    }
}
