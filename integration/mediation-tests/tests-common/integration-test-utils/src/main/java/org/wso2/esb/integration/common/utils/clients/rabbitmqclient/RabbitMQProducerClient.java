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

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class RabbitMQProducerClient {
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Channel channel = null;
    private String exchangeName;
    private String routeKey;

    public RabbitMQProducerClient(String host, int port, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public void declareAndConnect(String exchangeName, String routeKey) throws IOException {
        connection = factory.newConnection();
        channel = connection.createChannel();

        this.exchangeName = exchangeName;
        this.routeKey = routeKey;

        try {
            channel.exchangeDeclarePassive(exchangeName);
        } catch (IOException e) {
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

    public void sendMessage(String message, String contentType) throws IOException {
        channel.basicPublish(exchangeName, routeKey,
                new AMQP.BasicProperties.Builder().contentType(contentType).build(), message.getBytes());
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

