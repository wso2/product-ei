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
package org.wso2.esb.integration.common.extensions.jmsserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.JMSBrokerController;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;

public class ActiveMQServerExtension extends ExecutionListenerExtension {
    private static final Log log = LogFactory.getLog(ActiveMQServerExtension.class);
    private static JMSBrokerController activeMqBroker;

    @Override
    public void initiate() throws AutomationFrameworkException {
        activeMqBroker = new JMSBrokerController("localhost", getJMSBrokerConfiguration());
    }

    @Override
    public void onExecutionStart() throws AutomationFrameworkException {
        if (!JMSBrokerController.isBrokerStarted()) {
            log.info("Starting JMS Broker...");
            activeMqBroker.start();
        }
    }

    @Override
    public void onExecutionFinish() throws AutomationFrameworkException {
        if (activeMqBroker != null) {
            log.info("Stopping JMS broker...");
            activeMqBroker.stop();
        }
    }

    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        return JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration();
    }

    public static JMSBrokerController getJMSBrokerController() {
        return activeMqBroker;
    }

    public static void startMQServer() {
        activeMqBroker.start();
    }

    public static void stopMQServer() {
        activeMqBroker.stop();

    }
}
