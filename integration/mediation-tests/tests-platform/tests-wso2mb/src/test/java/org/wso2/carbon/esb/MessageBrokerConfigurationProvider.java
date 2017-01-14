/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MessageBrokerConfigurationProvider {
    protected Log log = LogFactory.getLog(MessageBrokerConfigurationProvider.class);

    private static MessageBrokerConfigurationProvider messageBrokerConfigurationProvider = new MessageBrokerConfigurationProvider();
    private Properties jndiProperties;

    public enum ConnectionFactory {QueueConnectionFactory, TopicConnectionFactory}

    private MessageBrokerConfigurationProvider() {
        try {
            this.jndiProperties = new Properties();
            this.jndiProperties.load(new FileInputStream(
                    TestConfigurationProvider.getResourceLocation("ESB") + File.separator + "conf"
                    + File.separator + "jndi.properties"));
        } catch (IOException e) {
            log.error("Error reading jndi.properties file", e);
        }
    }

    public static JMSBrokerConfiguration getBrokerConfig(ConnectionFactory connectionFactory) {
        JMSBrokerConfiguration messageBroker;
        messageBroker = new JMSBrokerConfiguration();
        messageBroker.setInitialNamingFactory("org.wso2.andes.jndi.PropertiesFileInitialContextFactory");
        messageBroker.setProviderURL(messageBrokerConfigurationProvider.jndiProperties.getProperty
                ("connectionfactory." + connectionFactory.toString()));
        return messageBroker;
    }

    public static JMSBrokerConfiguration getBrokerConfig() {
        return getBrokerConfig(ConnectionFactory.QueueConnectionFactory);
    }
}
