/*
 *     Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.carbon.esb.rabbitmq.securevault;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQConsumerClient;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;
import org.wso2.esb.integration.common.utils.servers.RabbitMQServer;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The purpose of this test case is to verify functionality of RabbitMQ sender and receiver with secured password
 * (in axis2.xml configuration) using cipher tool
 *
 * Note: Need to create following user in rabbitMQ before running this test
 *          Username : esb
 *          Password : admin123
 *          Virtual host : /,	Configure regexp : .*,	Write regexp : .*,	Read regexp	: .*
 */
public class TestRabbitMQSecureVaultSupport extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    private static final String CIPHER_TOOL_PROP_FILE = "cipher-tool.properties";
    private static final String CIPHER_TEXT_PROP_FILE = "cipher-text.properties";
    private static final String SECRET_CONF_PROP_FILE = "secret-conf.properties";
    private static final String PASSWD_FILE = "password-tmp";
    private static final String AXIS2_XML_FILE = "axis2.xml";
    private static final String CATELINA_SERVER_XML_FILE = "catalina-server.xml";
    private static final String SYNAPSE_CONFIG_FILE = "RabbitMQListenSendProxy.xml";

    private static final String RABBITMQ_HOST = "localhost";
    private static final int RABBITMQ_PORT = 5672;
    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "guest";
    private static final String RABBITMQ_EXCHANGE = "exchangeRx";
    private static final String RABBITMQ_RX_QUEUE = "queueRx";
    private static final String RABBITMQ_RESULT_QUEUE = "queueResult";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        serverConfigurationManager = new ServerConfigurationManager(context);
        String secureVaultConfDir = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                                                                "ESB" + File.separator + "securevault" + File.separator;
        String carbonSecurityDir = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                                                                "conf" + File.separator + "security" + File.separator;


        //copy cipher-tool.properties
        String srcCipherTool = secureVaultConfDir + CIPHER_TOOL_PROP_FILE;
        String targetCipherTool = carbonSecurityDir + CIPHER_TOOL_PROP_FILE;

        File srcCipherToolFile = new File(srcCipherTool);
        File targetCipherToolFile = new File(targetCipherTool);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcCipherToolFile, targetCipherToolFile, true);

        //copy cipher-text.properties
        String srcCipherText = secureVaultConfDir + CIPHER_TEXT_PROP_FILE;
        String targetCipherText = carbonSecurityDir + CIPHER_TEXT_PROP_FILE;

        File srcCipherTextFile = new File(srcCipherText);
        File targetCipherTextFile = new File(targetCipherText);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcCipherTextFile, targetCipherTextFile, true);

        //copy secret-conf.properties
        String srcSecretConf = secureVaultConfDir + SECRET_CONF_PROP_FILE;
        String targetSecretConf = carbonSecurityDir + SECRET_CONF_PROP_FILE;

        File srcSecretConfFile = new File(srcSecretConf);
        File targetSecretConfFile = new File(targetSecretConf);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcSecretConfFile, targetSecretConfFile, true);


        //copy axis2.xml
        String srcAxis2Xml = secureVaultConfDir + AXIS2_XML_FILE;
        String targetAxis2Xml = CarbonBaseUtils.getCarbonConfigDirPath() + File.separator + "axis2" +
                                                                                File.separator + AXIS2_XML_FILE;

        File srcAxis2XmlFile = new File(srcAxis2Xml);
        File targetAxis2XmlFile = new File(targetAxis2Xml);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcAxis2XmlFile, targetAxis2XmlFile, true);

        //copy catalina-server.xml
        String srcCatelinaServerXml = secureVaultConfDir + CATELINA_SERVER_XML_FILE;
        String targetCatelinaServerXml = CarbonBaseUtils.getCarbonConfigDirPath() + File.separator + "tomcat" +
                                                                        File.separator + CATELINA_SERVER_XML_FILE;

        File srcCatelinaServerXmlFile = new File(srcCatelinaServerXml);
        File targetCatelinaServerXmlFile = new File(targetCatelinaServerXml);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcCatelinaServerXmlFile,
                                                                                    targetCatelinaServerXmlFile, true);

        //copy password-tmp
        String srcPasswordTemp = secureVaultConfDir + PASSWD_FILE;
        String targetPasswordTemp = CarbonBaseUtils.getCarbonHome() + File.separator + PASSWD_FILE;

        File srcPasswordTempFile = new File(srcPasswordTemp);
        File targetPasswordTempFile = new File(targetPasswordTemp);
        serverConfigurationManager.applyConfigurationWithoutRestart(srcPasswordTempFile, targetPasswordTempFile, false);

        //start server with new configs
        serverConfigurationManager.restartGracefully();

        super.init();

        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator +
                                                                "securevault" + File.separator + SYNAPSE_CONFIG_FILE);
    }

    /**
     * This test will test both sender and receiver functionality (with password secured with secure vault):
     * Send message to queueRx in rabbitMQ, which ESB proxy (RabbitMQProxy.xml) listens to it.
     * Then the ESB proxy will put the received message (from exchangeRx) to queueResult in same RabbitMQ
     *
     * Note: Need to create following user in rabbitMQ before running this test
     *          Username : esb
     *          Password : admin123
     *          Virtual host : /,	Configure regexp : .*,	Write regexp : .*,	Read regexp	: .*
     * @throws InterruptedException
     */
    @Test(groups = "wso2.esb",
          description = "Test RabbitMQ listener and sender by securing password parameters secured using secure vault")
    public void testRabbitMQWithSecureVault() throws InterruptedException {
        //Put message to rabbitMQ receiver queue (queueRx)
        RabbitMQProducerClient sender = new RabbitMQProducerClient(RABBITMQ_HOST, RABBITMQ_PORT,
                                                                                RABBITMQ_USERNAME, RABBITMQ_PASSWORD);
        String message =
                "<ser:placeOrder xmlns:ser=\"http://services.samples\">\n" +
                        "<ser:order>\n" +
                        "<ser:price>100</ser:price>\n" +
                        "<ser:quantity>2000</ser:quantity>\n" +
                        "<ser:symbol>RMQ</ser:symbol>\n" +
                        "</ser:order>\n" +
                        "</ser:placeOrder>";
        try {
            sender.declareAndConnect(RABBITMQ_EXCHANGE, RABBITMQ_RX_QUEUE);
            sender.sendMessage(message, "text/plain");
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }

        Thread.sleep(10000);

        //Read from result queue (queueResult)
        RabbitMQConsumerClient consumer = new RabbitMQConsumerClient(RABBITMQ_HOST);

        try {
            consumer.declareAndConnect(RABBITMQ_EXCHANGE, RABBITMQ_RESULT_QUEUE);
        } catch (IOException e) {
            Assert.fail("Could not connect to RabbitMQ broker");
        }

        List<String> messages = null;
        try {
            messages = consumer.popAllMessages();
        } catch (IOException e) {
            Assert.fail("Error occurred while retrieving messages from the queue");
        }

        if (messages != null) {
            log.info("Retrieved messages from the queue : " + RABBITMQ_RESULT_QUEUE);
            for (String str : messages) {
                log.info(str);
            }
        }

        Assert.assertNotNull(messages, "No messages retrieved from the queue : " + RABBITMQ_RESULT_QUEUE);

        Assert.assertEquals(messages.size(), 1, "Message put to " + RABBITMQ_RX_QUEUE + " queue, was not published to "
                                                                            + RABBITMQ_RESULT_QUEUE + " by the proxy");

        Assert.assertEquals(messages.get(0), message, "Popped message from " + RABBITMQ_RESULT_QUEUE +
                                                                            " differ from the original message");

    }
    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        try {
            super.cleanup();
        } finally {
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }

    }
}
