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

/**
 * Test RabbitMQ receiver with different content-types and with content type service parameter
 */
public class RabbitMQContentTypeTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/rabbitmq/transport/rabbitmq_consumer_proxy.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test RabbitMQ consumer with no content type")
    public void testContentTypeEmpty() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        RabbitMQProducerClient sender = new RabbitMQProducerClient("localhost", 5672, "guest", "guest");

        try {
            sender.declareAndConnect("exchange2", "queue2");
            String message =
                    "<ser:placeOrder xmlns:ser=\"http://services.samples\">\n" +
                            "<ser:order>\n" +
                            "<ser:price>100</ser:price>\n" +
                            "<ser:quantity>2000</ser:quantity>\n" +
                            "<ser:symbol>RMQ</ser:symbol>\n" +
                            "</ser:order>\n" +
                            "</ser:placeOrder>";
            sender.sendMessage(message, null);
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        } finally {
            sender.disconnect();
        }

        Thread.sleep(20000);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        boolean setDefaultContentType = false;
        int count = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("Unable to determine content type for message")
                    && message.contains("setting to text/plain")) {
                setDefaultContentType = true;
            }
            if (message.contains("received = true")) {
                count++;
            }
        }

        Assert.assertTrue(setDefaultContentType, "Default content type is not set to text/plain");
        Assert.assertEquals(count, 1, "All messages are not received from queue");
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
        logViewer = null;
    }
}
