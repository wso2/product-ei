/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.common.utils;

import com.google.common.primitives.Longs;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.xa.Xid;

/**
 * Util class with common helper methods when writing client code
 */
public class JMSClientHelper {

    /**
     * Queue connection factory name used
     */
    public static final String QUEUE_XA_CONNECTION_FACTORY = "andesQueueXAConnectionfactory";

    /**
     * Topic connection factory name used
     */
    static final String TOPIC_XA_CONNECTION_FACTORY = "andesTopicXAConnectionfactory";

    /**
     * Queue connection factory name used
     */
    public static final String QUEUE_CONNECTION_FACTORY = "andesQueueConnectionfactory";

    /**
     * Topic connection factory name used
     */
    static final String TOPIC_CONNECTION_FACTORY = "andesTopicConnectionfactory";

    public static AtomicLong GLOBAL_ID_GENERATOR =  new AtomicLong();

    /**
     * Return a different Xid each time this method is invoked
     *
     * @return Xid
     */
    public static Xid getNewXid() {
        return new TestXidImpl(100, Longs.toByteArray(GLOBAL_ID_GENERATOR.incrementAndGet()), new byte[] { 0x01 });
    }

    public static InitialContextBuilder createInitialContextBuilder(String username, String password, String brokerHost,
            int brokerPort) {
        return new InitialContextBuilder(username, password, brokerHost, brokerPort);
    }

    public static class InitialContextBuilder {
        /**
         * Full qualified class name of the andes initial context factory
         */
        static final String ANDES_INITIAL_CONTEXT_FACTORY = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";

        private final String username;
        private final String password;
        private final String brokerHost;
        private final int brokerPort;
        private final Properties contextProperties;
        private boolean useNullClientId = false;

        InitialContextBuilder(String username, String password, String brokerHost, int brokerPort) {
            this.username = username;
            this.password = password;
            this.brokerHost = brokerHost;
            this.brokerPort = brokerPort;

            contextProperties = new Properties();
            contextProperties.put(Context.INITIAL_CONTEXT_FACTORY, ANDES_INITIAL_CONTEXT_FACTORY);
        }

        /**
         * Add Queue name to initial context
         *
         * @return Initial Context builder
         */
        public InitialContextBuilder withQueue(String queueName) {
            contextProperties.put("queue." + queueName, queueName);
            return this;
        }

        /**
         * Add Topic name to initial context
         *
         * @return Initial Context builder
         */
        public InitialContextBuilder withTopic(String topicName) {
            contextProperties.put("topic." + topicName, topicName);
            return this;
        }

        /**
         * Remove client ID from the broker URL
         *
         * @return Initial Context builder
         */
        public InitialContextBuilder withNoClientId() {
            useNullClientId = true;
            return this;
        }

        /**
         * Build the initial context according to builder parameters
         *
         * @return Initial context
         * @throws NamingException if there is an error in builder parameters
         */
        public InitialContext build() throws NamingException {
            String connectionString = getBrokerConnectionString(username, password, brokerHost, brokerPort);
            contextProperties.put("connectionfactory." + QUEUE_CONNECTION_FACTORY, connectionString);
            contextProperties.put("connectionfactory." + TOPIC_CONNECTION_FACTORY, connectionString);
            contextProperties.put("xaconnectionfactory." + QUEUE_XA_CONNECTION_FACTORY, connectionString);
            contextProperties.put("xaconnectionfactory." + TOPIC_XA_CONNECTION_FACTORY, connectionString);
            return new InitialContext(contextProperties);
        }

        /**
         * Generate the broker URL
         *
         * @param username   username used to connect
         * @param password   password used to connect
         * @param brokerHost hostname of the primary broker
         * @param brokerPort AMQP port used byy the primary broker
         * @return broker URL
         */
        private String getBrokerConnectionString(String username, String password, String brokerHost, int brokerPort) {
            String clientIdString = useNullClientId ? "" : "clientID";

            return "amqp://" + username + ":" + password + "@" + clientIdString + "/carbon?brokerlist='tcp://"
                    + brokerHost + ":" + brokerPort + "'";
        }
    }
}
