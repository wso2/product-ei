/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.common.clients.operations.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMSClientHelper {
    /**
     * Full qualified class name of the andes initial context factory
     */
    public static final String ANDES_INITIAL_CONTEXT_FACTORY = "org.wso2.andes.jndi" +
                                                               ".PropertiesFileInitialContextFactory";

    /**
     * Queue connection factory name used
     */
    public static final String QUEUE_CONNECTION_FACTORY = "andesQueueConnectionfactory";

    /**
     * Topic connection factory name used
     */
    public static final String TOPIC_CONNECTION_FACTORY = "andesTopicConnectionfactory";

    /**
     * Create a inital context with the given parameters
     *
     * @param username
     *         Username
     * @param password
     *         Password
     * @param brokerHost
     *         Hostname or IP address of the broker
     * @param port
     *         Port used for AMQP transport
     * @param queueName
     *         Queue name
     * @return InitialContext
     * @throws NamingException
     */
    public static InitialContext getInitialContextForQueue(String username,
                                                           String password,
                                                           String brokerHost,
                                                           String port,
                                                           String queueName) throws NamingException {

        Properties contextProperties = new Properties();
        contextProperties.put(Context.INITIAL_CONTEXT_FACTORY, ANDES_INITIAL_CONTEXT_FACTORY);
        String connectionString = getBrokerConnectionString(username, password, brokerHost, port);
        contextProperties.put("connectionfactory." + QUEUE_CONNECTION_FACTORY, connectionString);
        contextProperties.put("queue." + queueName, queueName);

        return new InitialContext(contextProperties);
    }

    /**
     * Create a inital context with the given parameters
     *
     * @param username
     *         Username
     * @param password
     *         Password
     * @param brokerHost
     *         Hostname or IP address of the broker
     * @param port
     *         Port used for AMQP transport
     * @param topicName
     *         Topic name
     * @return InitialContext
     * @throws NamingException
     */
    public static InitialContext getInitialContextForTopic(String username,
                                                           String password,
                                                           String brokerHost,
                                                           String port,
                                                           String topicName) throws NamingException {

        Properties contextProperties = new Properties();
        contextProperties.put(Context.INITIAL_CONTEXT_FACTORY, ANDES_INITIAL_CONTEXT_FACTORY);
        String connectionString = getBrokerConnectionString(username, password, brokerHost, port);
        contextProperties.put("connectionfactory." + TOPIC_CONNECTION_FACTORY, connectionString);
        contextProperties.put("topic." + topicName, topicName);

        return new InitialContext(contextProperties);
    }

    /**
     * Generate broker connection string
     *
     * @param userName
     *         Username
     * @param password
     *         Password
     * @param brokerHost
     *         Hostname of broker (E.g. localhost)
     * @param port
     *         Port (E.g. 5672)
     * @return Broker Connection String
     */
    private static String getBrokerConnectionString(String userName, String password,
                                                    String brokerHost, String port) {

        return "amqp://" + userName + ":" + password + "@clientID/carbon?brokerlist='tcp://"
               + brokerHost + ":" + port + "'";
    }
}
