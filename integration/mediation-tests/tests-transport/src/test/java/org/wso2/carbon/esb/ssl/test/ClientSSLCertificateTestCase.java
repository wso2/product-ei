/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.ssl.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.generic.MutualSSLClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case to test finding client certificate in the axis2 message context after mutual ssl authentication.
 */
public class ClientSSLCertificateTestCase extends ESBIntegrationTest {

    private final String CONFIG_LOCATION = getESBResourceLocation() + File.separator + "ssl" + File.separator;
    private ServerConfigurationManager serverManager;

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(context);
        serverManager.applyConfigurationWithoutRestart(new File(CONFIG_LOCATION + "axis2.xml"));
        serverManager.restartGracefully();
        super.init();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          description = "Send Soap Message to test client's SSL certificate")
    public void testMutualSSLClientCertificate() throws Exception {
        String trustStoreName = "client-truststore.jks";
        String keyStoreName = "wso2carbon.jks";
        String keyStorePassword = "wso2carbon";
        String trustStorePassword = "wso2carbon";

        String soapMessage = "<soapenv:Envelope "
                + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:echo=\"http://echo.services.core.carbon.wso2.org\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <echo:echoString>\n"
                + "         <in>WSO2 Stock</in>\n"
                + "      </echo:echoString>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        //load key store file
        MutualSSLClient.loadKeyStore(CONFIG_LOCATION + keyStoreName, keyStorePassword);
        //load trust store file
        MutualSSLClient.loadTrustStore(CONFIG_LOCATION + trustStoreName, trustStorePassword);
        //create ssl socket factory instance with given key/trust stores
        MutualSSLClient.initMutualSSLConnection();

        Map<String, String> reqProps = new HashMap<>();
        reqProps.put("Content-type", "text/xml; charset=utf-8");
        reqProps.put("SOAPAction", "urn:echoString");
        String response;
        try {
            String PROXY_SERVICE = "ClientSSLCertTestProxy";
            response = MutualSSLClient.sendPostRequest(getProxyServiceURLHttps(PROXY_SERVICE), soapMessage, reqProps);
            log.info("Response received : " + response);
        } catch (IOException ioException) {
            log.error("Error sending Post request to proxy service", ioException);
            response = "";
        }
        Assert.assertTrue(response.contains("certs-true"), "Client SSL certificate is not found!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }

    }
}
