/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.mb.integration.common.clients;

import org.wso2.mb.integration.common.clients.configurations.AndesJMSClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Properties;

/**
 * The base class for JMS publishers and consumers. This class creates the initial context which
 * is required in creating the publishers and consumers.
 */
public abstract class AndesJMSBase {
    /**
     * The configuration file used in creating the JMS publishers and consumers.
     */
    protected final AndesJMSClientConfiguration jmsConfig;

    /**
     * The initial context used for creating the publishers and consumers.
     */
    private InitialContext initialContext;

    /**
     * Creates the initial context.
     *
     * @param config The configuration.
     * @throws NamingException
     */
    protected AndesJMSBase(AndesJMSClientConfiguration config) throws NamingException {
        this.jmsConfig = config;

        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, AndesClientConstants.ANDES_ICF);
        properties.put(AndesClientConstants.CF_NAME_PREFIX + AndesClientConstants.CF_NAME, jmsConfig.getConnectionString());
        properties.put(jmsConfig.getExchangeType().getType() + "." + jmsConfig.getDestinationName(), jmsConfig.getDestinationName());

        initialContext = new InitialContext(properties);
    }

    /**
     * Gets the initial context.
     *
     * @return The initial context.
     */
    public InitialContext getInitialContext() {
        return initialContext;
    }

    /**
     * Starts up the publisher or consumer.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     */
    public abstract void startClient()
            throws JMSException, NamingException, IOException, AndesClientException;

    /**
     * Stops the publisher or consumer
     *
     * @throws JMSException
     */
    public abstract void stopClient() throws JMSException, AndesClientException;

    /**
     * Gets the configuration.
     *
     * @return The configuration.
     */
    public abstract AndesJMSClientConfiguration getConfig();
}
