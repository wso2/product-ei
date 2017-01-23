/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.rabbitmq.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;

import java.io.IOException;

public class RabbitMQJSONConsumerTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;
    private RabbitMQProducerClient sender;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        initRabbitMQBroker();
        loadESBConfigurationFromClasspath("/artifacts/ESB/rabbitmq/transport/rabbitmq_consumer_json.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    private void initRabbitMQBroker() {
        sender = new RabbitMQProducerClient("localhost", 5672, "guest", "guest");
        try {
            sender.declareAndConnect("exchange2", "queue2");
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }
    }

    @Test(groups = {"wso2.esb"}, description = "Test ESB as a RabbitMQ consumer for JSON messages ")
    public void testRabbitMQJSONConsumer() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        try {
            sender.declareAndConnect("exchange2", "queue2");
            String message = "'{\"name\":\"device1\"}'";
            sender.sendMessage(message, "application/json");
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }

        Thread.sleep(20000);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        int count = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("received = true")) {
                count++;
                System.out.println("message = " + message);
            }
        }

        Assert.assertEquals(count, 1, "All messages are not received from queue");
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
        sender.disconnect();
        sender = null;
        logViewer = null;
    }
}
