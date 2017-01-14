/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.esb.integration.common.utils.servers;

import org.testng.Assert;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.JMSBrokerController;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class ActiveMQServer extends ESBIntegrationTest {

    private JMSBrokerController activeMqBroker;
    private ServerConfigurationManager serverManager = null;

    private final String ACTIVEMQ_BROKER = "activemq-broker-5.9.1.jar";
    private final String GERONIMO_J2EE_MANAGEMENT = "geronimo-j2ee-management_1.1_spec-1.0.1.jar";
    private final String GERONIMO_JMS = "geronimo-jms_1.1_spec-1.1.1.jar";
    private final String HAWTBUF = "hawtbuf-1.9.jar";
    private final String ACTIVEMQ_CLIENT = "activemq-client-5.9.1.jar";

    public void startJMSBrokerAndConfigureESB() throws Exception {
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverManager = new ServerConfigurationManager(context);

        activeMqBroker = new JMSBrokerController("localhost", getJMSBrokerConfiguration());
        if (!JMSBrokerController.isBrokerStarted()) {
            Assert.assertTrue(activeMqBroker.start(), "JMS Broker(ActiveMQ) stating failed");
        }

        //coppingAxisServiceClient  dependency jms jar files to component/lib
        serverManager.copyToComponentLib(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jar" + File.separator + ACTIVEMQ_BROKER));

        serverManager.copyToComponentLib(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                                                  + File.separator + "jar" + File.separator + GERONIMO_J2EE_MANAGEMENT));

        serverManager.copyToComponentLib(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                                                  + File.separator + "jar" + File.separator + GERONIMO_JMS));

        serverManager.copyToComponentLib(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                                                  + File.separator + "jar" + File.separator + HAWTBUF));

        serverManager.copyToComponentLib(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                                                  + File.separator + "jar" + File.separator + ACTIVEMQ_CLIENT));

        //enabling jms transport with ActiveMQ
        serverManager.applyConfiguration(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                                                                                                                           + File.separator + "jms" + File.separator + "transport"
                                                                                                                           + File.separator + "axis2config" + File.separator
                                                                                                                           + "activemq" + File.separator + "axis2.xml"));
    }

    public void stopJMSBrokerRevertESBConfiguration() throws Exception {
        try {
            //reverting the changes done to esb sever
            Thread.sleep(10000); //let server to clear the artifact undeployment
            if (serverManager != null) {
                serverManager.removeFromComponentLib(ACTIVEMQ_BROKER);
                serverManager.removeFromComponentLib(GERONIMO_J2EE_MANAGEMENT);
                serverManager.removeFromComponentLib(GERONIMO_JMS);
                serverManager.removeFromComponentLib(HAWTBUF);
                serverManager.removeFromComponentLib(ACTIVEMQ_CLIENT);
                serverManager.restoreToLastConfiguration();
            }

        } finally {
            if (activeMqBroker != null) {
               assertTrue(activeMqBroker.stop(), "JMS Broker(ActiveMQ) Stopping failed");
            }
        }
    }

    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        return JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration();
    }
}

