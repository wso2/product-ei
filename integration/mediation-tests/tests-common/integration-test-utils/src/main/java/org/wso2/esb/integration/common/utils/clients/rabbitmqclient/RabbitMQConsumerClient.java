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

package org.wso2.esb.integration.common.utils.clients.rabbitmqclient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RabbitMQConsumerClient {

    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Channel channel = null;
    private String routeKey;

    public RabbitMQConsumerClient(String host) {
        factory = new ConnectionFactory();
        factory.setHost(host);
    }

    public void declareAndConnect(String exchangeName, String routeKey) throws IOException {
        connection = factory.newConnection();
        channel = connection.createChannel();
        this.routeKey = routeKey;

        try {
            //Throws an exception if the channel is not available.
            channel.exchangeDeclarePassive(exchangeName);
        } catch (IOException e) {
            //Channel is closed if exchange is not available. Therefore, create the channel here again.
            if (!channel.isOpen()) {
                channel = connection.createChannel();
            }
            channel.exchangeDeclare(exchangeName, "direct");
        }

        try {
            channel.queueDeclarePassive(routeKey);
        } catch (IOException e) {
            if (!channel.isOpen()) {
                channel = connection.createChannel();
            }
            channel.queueDeclare(routeKey, false, false, false, null);
        }

        channel.queueBind(routeKey, exchangeName, routeKey);
    }

    public List<String> popAllMessages() throws IOException, InterruptedException {
        List<String> messages = new ArrayList<>();
        GetResponse response;

        while ((response = channel.basicGet(routeKey, true)) != null) {
            messages.add(new String(response.getBody()));
        }
        return messages;
    }

    public void disconnect() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
