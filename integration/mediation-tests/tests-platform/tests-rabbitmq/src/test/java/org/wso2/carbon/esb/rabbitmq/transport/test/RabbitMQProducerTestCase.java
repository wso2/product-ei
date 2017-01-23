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

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQConsumerClient;

import java.io.IOException;
import java.util.List;

public class RabbitMQProducerTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/rabbitmq/transport/rabbitmq_endpoint_proxy.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Test ESB as a RabbitMQ Consumer ")
    public void testRabbitMQProducer() throws Exception {
        AxisServiceClient client = new AxisServiceClient();
        RabbitMQConsumerClient consumer = new RabbitMQConsumerClient("localhost");

        try {
            consumer.declareAndConnect("exchange1", "queue1");
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }

        List<String> messages = consumer.popAllMessages();
        int beforeMessageCount = messages.size();

        for (int i = 0; i < 5; i++) {
            client.sendRobust(Utils.getStockQuoteRequest("RMQ"), getProxyServiceURLHttp("RabbitMQProducerProxy"), "getQuote");
        }
        Thread.sleep(10000);

        messages = consumer.popAllMessages();
        int afterMessagesCount = messages.size();

        if (messages.size() == 0) {
            Assert.fail("Messages not received at RabbitMQ Broker");
        } else {
            Assert.assertEquals(afterMessagesCount - beforeMessageCount, 5, "Messages not received at RabbitMQBroker");
            for (int i = beforeMessageCount; i < afterMessagesCount; i++) {
                Assert.assertNotNull(messages.get(i), "Message not found. message sent by proxy service not reached to the destination Queue");
                Assert.assertTrue(messages.get(i).contains("<ns:getQuote xmlns:ns=\"http://services.samples\"><" +
                        "ns:request><ns:symbol>RMQ</ns:symbol></ns:request></ns:getQuote>")
                        , "Message mismatched");
            }
        }
        consumer.disconnect();
    }

    @Test(groups = {"wso2.esb"}, description = "Test ESB as a RabbitMQ Consumer with large messages ~10KB ")
    public void testRabbitMQProducerLargeMessages() throws Exception {
        AxisServiceClient client = new AxisServiceClient();
        RabbitMQConsumerClient consumer = new RabbitMQConsumerClient("localhost");

        try {
            consumer.declareAndConnect("exchange1", "queue1");
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }

        List<String> messages = consumer.popAllMessages();
        int beforeMessageCount = messages.size();
        String symbol = getFixedSizeSymbolKB("RMQ", 10);
        OMElement payload = Utils.getStockQuoteRequest(symbol);

        for (int i = 0; i < 5; i++) {
            client.sendRobust(payload, getProxyServiceURLHttp("RabbitMQProducerProxy"), "getQuote");
        }
        Thread.sleep(10000);

        messages = consumer.popAllMessages();
        int afterMessagesCount = messages.size();

        if (messages.size() == 0) {
            Assert.fail("Messages not received at RabbitMQ Broker");
        } else {
            Assert.assertEquals(afterMessagesCount - beforeMessageCount, 5, "Messages not received at RabbitMQBroker");
            for (int i = beforeMessageCount; i < afterMessagesCount; i++) {
                Assert.assertNotNull(messages.get(i), "Message not found. message sent by proxy service not reached to the destination Queue");
                Assert.assertTrue(messages.get(i).contains("<ns:getQuote xmlns:ns=\"http://services.samples\"><" +
                        "ns:request><ns:symbol>" + symbol + "</ns:symbol></ns:request></ns:getQuote>")
                        , "Message mismatched");
            }
        }
        consumer.disconnect();
    }

    private String getFixedSizeSymbolKB(String symbol, double size) {
        StringBuilder sb = new StringBuilder();
        int length = symbol.length();
        double bytes = size * 1024;
        sb.append(" WSO2 ");
        for (int i = 0; i < bytes; i += length) {
            sb.append(symbol);
        }
        return sb.toString();
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
    }
}
