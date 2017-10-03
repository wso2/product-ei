/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.esb.integration.common.utils.clients.jmsclient;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Helper class to assist in writing JMS clients
 */
public class JmsClientHelper {

    /**
     * Queue connection factory JNDI name
     */
    public static final String QUEUE_CONNECTION_FACTORY = "QueueConnectionFactory";

    /**
     * Initial context factory JNDI name of ActiveMQ
     */
    private static final String ACTIVE_MQ_INITIAL_CONTEXT_FACTORY
            = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    /**
     * Get a Initial context with ActiveMQ defaults
     *
     * @return initial context with ActiveMQ defaults
     * @throws NamingException if used JNDI names are incorrect
     */
    public static InitialContext getActiveMqInitialContext() throws NamingException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, ACTIVE_MQ_INITIAL_CONTEXT_FACTORY);
        properties.put("connectionfactory." + QUEUE_CONNECTION_FACTORY, "tcp://127.0.0.1:61616");
        properties.put(Context.PROVIDER_URL, "tcp://127.0.0.1:61616");
        return new InitialContext(properties);
    }
}
