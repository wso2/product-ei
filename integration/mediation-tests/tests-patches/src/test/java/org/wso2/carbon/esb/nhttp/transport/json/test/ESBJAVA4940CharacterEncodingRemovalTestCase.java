/*
*  Copyright (c) 2016.year, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.nhttp.transport.json.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This test case is to test the behaviour of when setCharacterEncoding property is set and not.
 * When setCharacterEncoding is true, ESB should append default character encoding(UTF-8) to the
 * outgoing request and when this property is false, ESB should not forcefully append a default
 * character encoding.
 */
public class ESBJAVA4940CharacterEncodingRemovalTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewer;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(
                new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        // Apply nhttp configuration
        serverConfigurationManager.applyConfigurationWithoutRestart(
                Paths.get(getESBResourceLocation(), "nhttp", "transport", "ESBJAVA4940", "axis2.xml").toFile());

        // Enable wire logs
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        File log4jProperties = Paths.get(carbonHome, "repository", "conf", "log4j.properties").toFile();
        applyProperty(log4jProperties, "log4j.logger.org.apache.synapse.transport.http.wire", "DEBUG");
        serverConfigurationManager.restartGracefully();
        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/nhttp/transport/ESBJAVA4940/synapseConfig.xml");

        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }


    @Test(groups = "wso2.esb",
          description = "Test charset encoding removal when SetEncoding is true",
          enabled = true)
    public void testRemoveCharsetSetEncodingPropertyTrue() throws Exception {
        Assert.assertTrue(isCharsetEncodingPresent(getProxyServiceURLHttp("ESBJAVA4940SetEncodingTrue")),
                "Charset Encoding is not present in the request sent");
    }

    @Test(groups = "wso2.esb",
          description = "Test charset encoding removal when SetEncoding is false",
          enabled = true,
          dependsOnMethods = "testRemoveCharsetSetEncodingPropertyTrue")
    public void testRemoveCharsetSetEncodingPropertyFalse() throws Exception {
        Assert.assertFalse(isCharsetEncodingPresent("ESBJAVA4940SetEncodingFalse"),
                "Charset Encoding is present in the request sent");
    }

    private boolean isCharsetEncodingPresent(String proxyURL) throws Exception {
        logViewer.clearLogs();
        String payload = "{\"sampleJson\" : \"sampleValue\"}";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "application/json");
        try {
            HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(proxyURL)), payload, requestHeader);
            Thread.sleep(30000);
        } catch (Exception e) {
            //Ignored
            log.error(e);
        }

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        for (LogEvent log : logs) {
            if (log.getMessage().contains("Content-Type: application/json; charset=UTF-8")) {
                return true;
            }
        }
        return false;
    }

    private void applyProperty(File srcFile, String key, String value) throws Exception {
        File destinationFile = new File(srcFile.getName());
        Properties properties = new Properties();
        properties.load(new FileInputStream(srcFile));
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(destinationFile), null);
        serverConfigurationManager.applyConfigurationWithoutRestart(destinationFile);
    }

    @AfterClass
    public void cleanUp() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
