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

package org.wso2.carbon.esb.rabbitmq.qos.jira;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQServerInstance;
import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQConsumerClient;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is to test rabbitMQ qos settings applied correctly or not.
 */
public class ESBJAVA4571RabbitMQQOSTestCase extends ESBIntegrationTest {

    private RabbitMQProducerClient sender;
    private ProxyServiceAdminClient proxyServiceAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        sender = RabbitMQServerInstance.createProducerWithDeclaration("qosExchange", "qosQueue");
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator
                                          + "ESB" + File.separator + "qos" + File.separator + "rabbitMQ"
                                          + File.separator
                                          + "RabbitMQQOSProxy.xml");
        proxyServiceAdminClient = new ProxyServiceAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test ESB as a RabbitMQ consumer QOS applied")
    public void testRabbitMQQOSConsumer() throws Exception {

        proxyServiceAdminClient.stopProxyService("RabbitMQQOSProxy");
        RabbitMQConsumerClient consumer = RabbitMQServerInstance.createConsumerWithDeclaration("qosExchange",
                                                                                               "qosQueue");

        //remove already existing messages
        consumer.popAllMessages();

        try {
            sender.declareAndConnect("qosExchange", "qosQueue");
            String message = "<sample><message>This is a sample message</message></sample>";
            for (int i = 0; i < 100; i++) {
                sender.sendMessage(message, "application/xml");
            }
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        } finally {
            sender.disconnect();
        }

        proxyServiceAdminClient.startProxyService("RabbitMQQOSProxy");

        long beforeGettingMessages = System.currentTimeMillis();

        List<String> messages = consumer.popAllMessages();

        /**
         * Here logic is proxy is set with auto ack false and qos 5 and thread sleep of 120000 millis, so when we activate the proxy
         * it won't send a ack until 120000 millis passed, so up until then, message broker won't send another message to proxy
         * hence message broker should contain 95 messages(which we get through "RabbitMQConsumerClient")
         * 95 = total send message count - messages came to ESB(which is qos value)
         */

        long afterGettingMessages = System.currentTimeMillis();
        if ((afterGettingMessages - beforeGettingMessages) < 120000) {
            Assert.assertEquals(messages.size(), 95, "Wrong number of messages exist in queue");
            log.info("ESBJAVA4571RabbitMQQOSTestCase testRabbitMQQOSConsumer passed");
        }
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
    }
}
