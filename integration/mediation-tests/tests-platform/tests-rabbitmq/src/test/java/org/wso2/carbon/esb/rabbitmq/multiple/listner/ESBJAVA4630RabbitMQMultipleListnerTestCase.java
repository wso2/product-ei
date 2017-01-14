/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.rabbitmq.multiple.listner;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQTestUtils;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQConsumerClient;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;
import org.wso2.esb.integration.common.utils.servers.RabbitMQServer;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is to test multiple message listners are running per proxy or not
 */
public class ESBJAVA4630RabbitMQMultipleListnerTestCase extends ESBIntegrationTest {

    private static final String RABBIT_MQ_MULTIPLE_LISTNER_PROXY = "RabbitMQMultipleListnerProxy";
    private RabbitMQProducerClient sender;
    private ProxyServiceAdminClient proxyServiceAdminClient;
    private RabbitMQServer rabbitMQServer;
    private ServerConfigurationManager configurationManagerAxis2;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        configurationManagerAxis2 =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        File customAxisConfigAxis2 = new File(getESBResourceLocation() + File.separator +
                                              "axis2config" + File.separator + "axis2.xml");
        configurationManagerAxis2.applyConfiguration(customAxisConfigAxis2);
        super.init();

        rabbitMQServer = RabbitMQTestUtils.getRabbitMQServerInstance();
        //This is to stop existing rabbitMQ server instances
        rabbitMQServer.stop();
        rabbitMQServer.start();
        Assert.assertTrue(rabbitMQServer.isRabbitMQStarted(90), "Failed to start rabbitMQ server properly within given timeout");
        initRabbitMQBroker();
        loadESBConfigurationFromClasspath("/artifacts/ESB/rabbitmq/multipleListner/RabbitMQMultipleListnerProxy.xml");
        proxyServiceAdminClient = new ProxyServiceAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    private void initRabbitMQBroker() throws Exception {
        //Retrying rabbitMQ connection until timeout happens
        for (int i = 1; i * 5 < 90; i++) {
            sender = new RabbitMQProducerClient("localhost", 5672, "guest", "guest");
            try {
                sender.declareAndConnect("qosExchange", "qosQueue");
                log.info("Successfully connected to rabbitMQ broker in " + i + " attempt");
                return;
            } catch (IOException e) {
                log.warn("Error connecting to the broker in " + i + " attempt - " + e.getMessage(), e);
                Thread.sleep(300);//sleep for 5 seconds
            }
        }
        Assert.fail("Could not connect to RabbitMQ broker");
    }

    @Test(groups = {"wso2.esb"}, description = "Test for multiple message listners per proxy")
    public void testRabbitMQMultipleMessageListners() throws Exception {

        proxyServiceAdminClient.stopProxyService(RABBIT_MQ_MULTIPLE_LISTNER_PROXY);
        RabbitMQConsumerClient consumer = new RabbitMQConsumerClient("localhost");

        try {
            consumer.declareAndConnect("qosExchange", "qosQueue");
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }

        consumer.popAllMessages(); //this is to remove already existing messages

        try {
            sender.declareAndConnect("qosExchange", "qosQueue");
            String message = "<sample><message>This is a sample message</message></sample>";
            for (int i = 0; i < 200; i++) {
                sender.sendMessage(message, "application/xml");
            }
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        } finally {
            sender.disconnect();
        }

        proxyServiceAdminClient.startProxyService(RABBIT_MQ_MULTIPLE_LISTNER_PROXY);

        long beforeGettingMessages = System.currentTimeMillis();

        List<String> messages = consumer.popAllMessages();

        /**
         * Here logic is proxy is set with auto ack false and qos 5 and thread sleep of 120000 millis, so when we activate the proxy
         * it won't send a ack until 120000 millis passed, so up until then, message broker won't send another message to proxy
         * hence message broker should contain 150 messages(which we get through "RabbitMQConsumerClient")
         * 150 = total send message count - messages came to ESB(which is qos value * number of listners)
         *
         * messages sent to queue = 200
         * number of listners = 10
         * qos = 5
         */

        long afterGettingMessages = System.currentTimeMillis();
        if ((afterGettingMessages - beforeGettingMessages) < 120000) {
            Assert.assertEquals(messages.size(), 150, "Wrong number of messages exist in queue");
            log.info("ESBJAVA4630RabbitMQMultipleListnerTestCase testRabbitMQQOSConsumer passed");
        }
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
        try {
            rabbitMQServer.stop();
            sender = null;
            configurationManagerAxis2.restoreToLastConfiguration();
        } catch (AutomationUtilException e) {
            log.info("Error cleaning up - " + e.getMessage(), e);
        }
    }
}
