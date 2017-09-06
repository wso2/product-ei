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
 */
package org.wso2.carbon.esb.jms.securevault.test;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.esb.jms.utils.JMSBroker;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Test to verify JMS Transport functionality when SecureVault is used to encrypt configuration parameters such as
 * passwords.
 */
public class JMSTransportSecureVaultTest extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    private static final String AXIS2_XML_FILE = "axis2.xml";
    private static final String CIPHER_TOOL_PROPERTIES_FILE = "cipher-tool.properties";
    private static final String CIPHER_TEXT_PROPERTIES_FILE = "cipher-text.properties";
    private static final String SECRET_CONF_PROPERTIES_FILE = "secret-conf.properties";
    private static final String PASSWORD_FILE = "password-tmp";
    private static final String SYNAPSE_CONFIG_FILE = "JMSSecureVaultTestProxy.xml";
    private static final String PROXY_NAME = "JMSSecureVaultTestProxy";
    private static final String JAR_DIR = "/artifacts/ESB/jar/";

    private static final String ACTIVEMQ_BROKER = "activemq-broker-5.9.1.jar";
    private static final String ACTIVEMQ_CLIENT = "activemq-client-5.9.1.jar";
    private static final String GERONIMO_J2EE_MANAGEMENT = "geronimo-j2ee-management_1.1_spec-1.0.1.jar";
    private static final String GERONIMO_JMS = "geronimo-jms_1.1_spec-1.1.1.jar";
    private static final String HAWTBUF = "hawtbuf-1.9.jar";
    private static final String ACTIVEMQ_KAHADB_STORE = "activemq-kahadb-store-5.9.1.jar";
    private static final String SLF4J_API = "slf4j-api-1.7.13.jar";
    private static final String ACTIVEIO_CORE = "activeio-core-3.1.4.jar";

    /**
     * The embedded broker instance used for the test.
     */
    private JMSBroker activeMQBroker;
    /**
     * The simple authentication plugin for the broker to require logging in.
     */
    private SimpleAuthenticationPlugin simpleAuthenticationPlugin;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        String secureVaultConfDir = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator
                + "ESB" + File.separator  + "jms" + File.separator + "securevault" + File.separator;
        String carbonSecurityDir = CarbonBaseUtils.getCarbonHome() + File.separator + "conf" + File.separator
                + "security" + File.separator;

        String srcFileLocation = secureVaultConfDir + CIPHER_TOOL_PROPERTIES_FILE;
        String targetFileLocation = carbonSecurityDir + CIPHER_TOOL_PROPERTIES_FILE;
        File srcFile = new File(srcFileLocation);
        File targetFile = new File(targetFileLocation);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcFile, targetFile, true);

        srcFileLocation = secureVaultConfDir + CIPHER_TEXT_PROPERTIES_FILE;
        targetFileLocation = carbonSecurityDir + CIPHER_TEXT_PROPERTIES_FILE;
        srcFile = new File(srcFileLocation);
        targetFile = new File(targetFileLocation);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcFile, targetFile, true);

        srcFileLocation = secureVaultConfDir + SECRET_CONF_PROPERTIES_FILE;
        targetFileLocation = carbonSecurityDir + SECRET_CONF_PROPERTIES_FILE;
        srcFile = new File(srcFileLocation);
        targetFile = new File(targetFileLocation);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcFile, targetFile, true);

        srcFileLocation = secureVaultConfDir + AXIS2_XML_FILE;
        targetFileLocation = CarbonBaseUtils.getCarbonHome() + File.separator + "conf" + File.separator + "axis2"
                + File.separator + AXIS2_XML_FILE;
        srcFile = new File(srcFileLocation);
        targetFile = new File(targetFileLocation);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcFile, targetFile, true);

        srcFileLocation = secureVaultConfDir + PASSWORD_FILE;
        targetFileLocation = CarbonBaseUtils.getCarbonHome() + File.separator + PASSWORD_FILE;
        srcFile = new File(srcFileLocation);
        targetFile = new File(targetFileLocation);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcFile, targetFile, false);

        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + ACTIVEMQ_BROKER)
                .getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + ACTIVEMQ_CLIENT)
                .getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR
                + GERONIMO_J2EE_MANAGEMENT).getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + GERONIMO_JMS)
                .getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + HAWTBUF).getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + ACTIVEMQ_KAHADB_STORE)
                .getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + SLF4J_API).getPath()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_DIR + ACTIVEIO_CORE)
                .getPath()));

        serverConfigurationManager.restartGracefully();
        super.init();
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator +
                "jms" + File.separator + "securevault" + File.separator + SYNAPSE_CONFIG_FILE);
        isProxyDeployed(PROXY_NAME);
    }

    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        return JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration();
    }

    /**
     * This test will test the JMS transport functionality (with password encrypted with SecureVault):
     * A user with username "system" and password "manager" is introduced to the broker here, and the axis2.xml
     * also includes this configuration, where the password ("manager") has been encrypted with SecureVault.
     * Thus an exception (SecurityException) thrown while connecting to send would indicate a decryption failure.
     */
    @Test(groups = "wso2.esb", description = "Test JMS transport using parameters secured using SecureVault")
    public void testJMSTransportWithSecureVault() throws InterruptedException {
        try {
            activeMQBroker = new JMSBroker("localhost", getJMSBrokerConfiguration());
            simpleAuthenticationPlugin = new SimpleAuthenticationPlugin();
            simpleAuthenticationPlugin.setAnonymousAccessAllowed(false);
            List<AuthenticationUser> users = new ArrayList<AuthenticationUser>();
            users.add(new AuthenticationUser("system", "manager", "users,admins"));
            simpleAuthenticationPlugin.setUsers(users);
            boolean isBrokerStarted = activeMQBroker.startWithPlugins(new BrokerPlugin[]{simpleAuthenticationPlugin});
            Assert.assertTrue(isBrokerStarted, "ActiveMQ broker is not started");
            OMElement payload = axis2Client.createPlaceOrderRequest(100.0, 7500, "JMS");
            axis2Client.sendRobust(getProxyServiceURLHttp(PROXY_NAME), null, "placeOrder", payload);
        } catch (SecurityException e) {
            Assert.fail("Error Connecting to ActiveMQ ", e);
        } catch (AxisFault e) {
            Assert.fail("Failed to send to queue in ActiveMQ ", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        try {
            super.cleanup();
            activeMQBroker.stop();
        } finally {
            serverConfigurationManager.removeFromComponentLib(ACTIVEMQ_BROKER);
            serverConfigurationManager.removeFromComponentLib(ACTIVEMQ_CLIENT);
            serverConfigurationManager.removeFromComponentLib(GERONIMO_J2EE_MANAGEMENT);
            serverConfigurationManager.removeFromComponentLib(GERONIMO_JMS);
            serverConfigurationManager.removeFromComponentLib(HAWTBUF);
            serverConfigurationManager.removeFromComponentLib(ACTIVEMQ_KAHADB_STORE);
            serverConfigurationManager.removeFromComponentLib(SLF4J_API);
            serverConfigurationManager.removeFromComponentLib(ACTIVEIO_CORE);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }
}
