/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.FileUtil;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * This class tests the functionality of ConcurrentConsumers and MaxConcurrentConsumers properties
 */

public class PropertyIntegrationAxis2PropertiesTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager = null;
    private ActiveMQServer activeMqServer
            = new ActiveMQServer();


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        activeMqServer.startJMSBrokerAndConfigureESB();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverManager = new ServerConfigurationManager(context);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String confDir = carbonHome + File.separator + "repository" + File.separator + "conf"
                + File.separator;
        File configTemp = new File(confDir + "axis2" + File.separator + "property_axis2_server.xml");
        FileUtils.deleteQuietly(configTemp);
        activeMqServer.stopJMSBrokerRevertESBConfiguration();
        super.init();
        super.cleanup();
        serverManager.restoreToLastConfiguration();
    }


    @Test(groups = {"wso2.esb"}, description = "Send messages using  ConcurrentConsumers " +
                                               "and MaxConcurrentConsumers Axis2 level properties")
    public void maxConcurrentConsumersTest() throws Exception {

        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String confDir = carbonHome + File.separator + "repository" + File.separator + "conf"
                + File.separator;

        //enabling jms transport with ActiveMQ
        File originalConfig = new File(TestConfigurationProvider.getResourceLocation()
                + File.separator + "artifacts" + File.separator
                + "AXIS2" + File.separator + "config" +
                File.separator + "property_axis2_server.xml");
        File destDir = new File(confDir + "axis2" + File.separator);
        FileUtils.copyFileToDirectory(originalConfig,destDir);

        serverManager.restartGracefully();

        super.init();  // after restart the server instance initialization
        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi://" +
                                  context.getDefaultInstance().getHosts().get("default") +
                                  ":11311/jndi/rmi://" + context.getDefaultInstance().getHosts().
                        get("default") + ":10199/jmxrmi");

        HashMap<String, String[]> environment = new HashMap<String, String[]>();
        String[] credentials = new String[]{"admin", "admin"};
        environment.put(JMXConnector.CREDENTIALS, credentials);

        MBeanServerConnection mBeanServerConnection = JMXConnectorFactory.
                connect(url, environment).getMBeanServerConnection();

        int beforeThreadCount = (Integer) mBeanServerConnection.getAttribute(
                new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "ThreadCount");


        String queueName = "SimpleStockQuoteService";

        for (int x = 0; x < 200; x++) {
            JMSQueueMessageProducer sender = new JMSQueueMessageProducer
                    (JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());

            try {
                sender.connect(queueName);
                for (int i = 0; i < 3; i++) {
                    sender.pushMessage("<?xml version='1.0' encoding='UTF-8'?>" +
                                       "<soapenv:Envelope xmlns:soapenv=\"http://schemas." +
                                       "xmlsoap.org/soap/envelope/\"" +
                                       " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"" +
                                       "http://services.samples/xsd\">" +
                                       "   <soapenv:Header/>" +
                                       "   <soapenv:Body>" +
                                       "      <ser:placeOrder>" +
                                       "         <ser:order>" +
                                       "            <xsd:price>100</xsd:price>" +
                                       "            <xsd:quantity>2000</xsd:quantity>" +
                                       "            <xsd:symbol>JMSTransport</xsd:symbol>" +
                                       "         </ser:order>" +
                                       "      </ser:placeOrder>" +
                                       "   </soapenv:Body>" +
                                       "</soapenv:Envelope>");
                }
            } finally {
                sender.disconnect();
            }
        }

        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/mediatorconfig/property/" +
                                                  "ConcurrentConsumers.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));


        int afterThreadCount = (Integer) mBeanServerConnection.getAttribute(
                new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "ThreadCount");


        assertTrue((afterThreadCount - beforeThreadCount) <= 150, "Expected thread count range" +
                                                                  " not met");
    }
}

