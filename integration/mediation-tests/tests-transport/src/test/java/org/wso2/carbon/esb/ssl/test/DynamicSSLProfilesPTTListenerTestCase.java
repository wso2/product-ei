/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.ssl.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.automation.test.utils.generic.MutualSSLClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JMXClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import javax.management.MalformedObjectNameException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.AssertJUnit.assertTrue;

/**
 * TestCase for Dynamic SSL Profiles re-loading using Mutual SSL Client connection.
 */
public class DynamicSSLProfilesPTTListenerTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager = null;

    private String trustStoreName = "client-truststore.jks";
    private String keyStoreName = "wso2carbon.jks";
    private String keyStorePassword = "wso2carbon";
    private String trustStorePassword = "wso2carbon";
    private String proxyService = "sslTestProxy";
    private String serviceName =
            "org.apache.synapse:Type=ListenerSSLProfileReloader," + "Name=PassThroughHttpMultiSSLListener";
    private String configLocation =
            getESBResourceLocation() + File.separator + "dynamicsslprofiles" + File.separator + "pttlistener"
                    + File.separator;

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(context);
        serverManager.applyConfigurationWithoutRestart(new File(
                getESBResourceLocation() + File.separator + "dynamicsslprofiles" + File.separator + "pttlistener"
                        + File.separator + "axis2.xml"));
        serverManager.restartGracefully();
        super.init();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          description = "Testing Mutual SSL Connection with dynamically loaded SSL configuration")
    public void testMutualSSLConnectionWithUpdatedProfile() throws Exception {
        String soapMessage = "<soapenv:Envelope "
                + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:echo=\"http://echo.services.core.carbon.wso2.org\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <echo:echoString>\n"
                + "         <in>WSO2 Stock</in>\n"
                + "      </echo:echoString>\n"
                + "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        //load key store file
        MutualSSLClient.loadKeyStore(configLocation + keyStoreName, keyStorePassword);
        //load trust store file
        MutualSSLClient.loadTrustStore(configLocation + trustStoreName, trustStorePassword);
        //create ssl socket factory instance with given key/trust stores
        MutualSSLClient.initMutualSSLConnection();

        updateProfileConfigurationFiles();
        invokeJMXMethod();

        Map<String, String> reqProps = new HashMap<String, String>();
        reqProps.put("Content-type", "text/xml; charset=utf-8");
        reqProps.put("SOAPAction", "urn:echoString");

        String response;
        try {
            response = MutualSSLClient.sendPostRequest(getProxyServiceURLHttps(proxyService), soapMessage, reqProps);
        } catch (Exception e) {
            log.info("Error sending Post request to proxy service", e);
            response = "";
        }

        rollbackProfileConfigurationFiles();
        assertTrue("Mutual SSL Error because of incorrect key", response.contains("WSO2 Stock"));
    }

    /**
     * Connect to the JMX service using provided credentials and execute method invocation
     */
    private void invokeJMXMethod() {
        JMXClient jmxClient = null;

        try {
            jmxClient = new JMXClient(serviceName, "127.0.0.1", "11311", "10199", "admin", "admin");
        } catch (MalformedObjectNameException e) {
            log.error("Error creating JMXClient ", e);
        }

        if (jmxClient != null) {
            try {
                jmxClient.connect();
                jmxClient.invoke("notifyFileUpdate", null, null);
                log.info("Successfully invoked JMX service operation");
            } catch (Exception e) {
                log.info("JMX service operation invocation failed ", e);
            }

        } else {
            log.error("JMX service operation invocation failed due to JMXClient instance unavailable ");
        }
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.reloadSessionCookie();
        deleteProxyService(proxyService);
        super.cleanup();
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }
    }

    /**
     * Copy content of Dynamic SSL profiles configuration files to the file configured in axis2.xml
     *
     * @throws IOException
     */
    private void updateProfileConfigurationFiles() throws Exception {
        File sourceFile = new File(configLocation + "updatedlistenerprofiles.xml");
        FileManager.deleteFile(configLocation + "listenerprofiles.xml");

        try {
            FileManager.copyFile(sourceFile, (configLocation + "listenerprofiles.xml"));
        } catch (IOException e) {
            log.error("Error updating Dynamic SSL Profiles configuration ", e);
            throw new Exception(e);
        }
    }

    /**
     * Restore Dynamic SSL configuration files to its original content
     *
     * @throws IOException
     */
    private void rollbackProfileConfigurationFiles() throws Exception {
        File sourceFile = new File(configLocation + "restorelistenerprofiles.xml");
        FileManager.deleteFile(configLocation + "listenerprofiles.xml");

        try {
            FileManager.copyFile(sourceFile, (configLocation + "listenerprofiles.xml"));
        } catch (IOException e) {
            log.error("Error restoring Dynamic SSL Profiles configuration ", e);
            throw new Exception(e);
        }
    }
}