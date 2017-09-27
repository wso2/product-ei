/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.rabbitmq.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQConsumerClient;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.xpath.XPathExpressionException;

public class RabbitMQServerInstance {
    private static final Log log = LogFactory.getLog(RabbitMQServerInstance.class);
    private static final String RABBITMQ_HOST_XPATH = "//rabbitmq/host";
    private static final String RABBITMQ_PORT_XPATH = "//rabbitmq/port";
    private static final String DOCKER_HOST = "DOCKER_HOST";
    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";

    /**
     * Returns the host on which docker is running.
     * <p>
     * If the docker instance was started by passing an environment variable to set the host, the given host will be
     * returned. If not, localhost will get returned.
     *
     * @return the ip of the host on which docker is running.
     */
    public static String getHost() {
        String host = "localhost";
        try {
            if (DOCKER_HOST.equalsIgnoreCase(
                    TestConfigurationProvider.getAutomationContext().getConfigurationValue(RABBITMQ_HOST_XPATH))) {
                String dockerHost = System.getenv("DOCKER_HOST");
                if ((null != dockerHost) && (!StringUtils.isEmpty(dockerHost))) {
                    URI uri;
                    try {
                        uri = new URI(dockerHost);
                        host = uri.getHost();
                    } catch (URISyntaxException e) {
                        log.error("Error getting DOCKER_HOST", e);
                    }
                }
            }

        } catch (XPathExpressionException e) {
            log.warn("Error reading the rabbitmq host in automation.xml. Proceed with default value " + host);
        }
        return host;
    }

    public static int getPort() {
        //set default port
        int port = 5672;
        try {
            port = Integer.parseInt(
                    TestConfigurationProvider.getAutomationContext().getConfigurationValue(RABBITMQ_PORT_XPATH));
        } catch (XPathExpressionException e) {
            log.warn("Error reading the rabbitmq port in automation.xml. Proceed with default value " + port);
        }
        return port;
    }

    /**
     * Initialize rabbitmq broker by declaring the the exchange and a routing key that the producer will publishing
     * to.
     *
     * @return the rabbitmq producer that was used to declaration.
     */
    public static RabbitMQProducerClient createProducerWithDeclaration(String exchange, String routingKey)
            throws IOException {
        RabbitMQProducerClient sender = new RabbitMQProducerClient(getHost(), getPort(), USER_NAME, PASSWORD);
        sender.declareAndConnect(exchange, routingKey);
        return sender;
    }

    /**
     * Initialize rabbitmq broker by declaring the the exchange and a routing key that the consumer will be bound to.
     *
     * @return the rabbitmq consumer that was used to declaration.
     */
    public static RabbitMQConsumerClient createConsumerWithDeclaration(String exchange, String routingKey)
            throws IOException {
        RabbitMQConsumerClient consumer = new RabbitMQConsumerClient(getHost());
        consumer.declareAndConnect(exchange, routingKey);
        return consumer;
    }
}
