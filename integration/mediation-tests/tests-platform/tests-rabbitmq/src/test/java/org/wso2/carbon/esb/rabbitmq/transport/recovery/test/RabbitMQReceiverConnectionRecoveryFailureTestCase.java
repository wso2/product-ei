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

package org.wso2.carbon.esb.rabbitmq.transport.recovery.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQTestUtils;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQConsumerClient;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;
import org.wso2.esb.integration.common.utils.servers.RabbitMQServer;

import java.io.File;
import java.io.IOException;

public class RabbitMQReceiverConnectionRecoveryFailureTestCase extends ESBIntegrationTest {

    private RabbitMQServer rabbitMQServer;
    private RabbitMQProducerClient sender;
    private RabbitMQConsumerClient consumer;
    private LogViewerClient logViewer;
    private ServerConfigurationManager configurationManagerAxis2;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        rabbitMQServer = RabbitMQTestUtils.getRabbitMQServerInstance();
        sender = new RabbitMQProducerClient("localhost", 5672, "guest", "guest");
        consumer = new RabbitMQConsumerClient("localhost");

        configurationManagerAxis2 =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        File customAxisConfigAxis2 = new File(getESBResourceLocation() + File.separator +
                "axis2config" + File.separator + "axis2.xml");
        configurationManagerAxis2.applyConfiguration(customAxisConfigAxis2);
        super.init();

        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        loadESBConfigurationFromClasspath("/artifacts/ESB/rabbitmq/transport/rabbitmq_consumer_proxy.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Test ESB as a RabbitMQ Consumer with connection recovery - fail case")
    public void testRabbitMQConsumerRecoveryFailure() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        rabbitMQServer.start();
        rabbitMQServer.initialize();

        //publish 10 messages to broker and wait for ESB to pick up the messages
        publishMessages(10);
        Thread.sleep(30000);

        //Stop rabbitmq server
        rabbitMQServer.stop();

        //Recovery time is 10000(retry interval) * 5 (retry count) ms. Therefore wait past the recovery time.
        Thread.sleep(70000);

        //Restart the server
        rabbitMQServer.start();
        rabbitMQServer.initialize();

        //publish another 10 messages to broker and wait for ESB to pick up the messages
        publishMessages(10);
        Thread.sleep(30000);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        int count = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("received = true")) {
                count++;
            }
        }

        //Only 10 messages are received, before the connection is closed. Other messages are not received
        // since the connection was not recovered.
        Assert.assertEquals(count, 10, "Connection recovery has an error");

        consumer.declareAndConnect("exchange2", "queue2");
        Assert.assertEquals(consumer.popAllMessages().size(), 10, "Last set of messages are not published to broker");

        rabbitMQServer.stop();
    }

    /**
     * Wait for server to start and publish messages
     *
     * @param messageCount number of messages to publish
     */
    private void publishMessages(int messageCount) throws InterruptedException {
        try {
            sender.declareAndConnect("exchange2", "queue2");
            for (int i = 0; i < messageCount; i++) {
                String message =
                        "<ser:placeOrder xmlns:ser=\"http://services.samples\">\n" +
                                "<ser:order>\n" +
                                "<ser:price>100</ser:price>\n" +
                                "<ser:quantity>2000</ser:quantity>\n" +
                                "<ser:symbol>RMQ</ser:symbol>\n" +
                                "</ser:order>\n" +
                                "</ser:placeOrder>";
                sender.sendMessage(message, "text/plain");
            }
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        } finally {
            sender.disconnect();
        }
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
        logViewer = null;
        rabbitMQServer.stop();
        rabbitMQServer = null;
        sender = null;
        consumer = null;
        configurationManagerAxis2.restoreToLastConfiguration();
    }
}
