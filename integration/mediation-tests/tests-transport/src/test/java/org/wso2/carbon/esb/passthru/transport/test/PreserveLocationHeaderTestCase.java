/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.HttpHeaders;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Test case for preserve Http Location header when the following property is configured in passthru-http.properties
 * http.headers.preserve=Location
 *
 * https://wso2.org/jira/browse/ESBJAVA-3677
 */
public class PreserveLocationHeaderTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private WireMonitorServer wireMonitorServer;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        verifyProxyServiceExistence("PreserveLocationHeaderTestProxy");
        serverConfigurationManager = new ServerConfigurationManager(
                new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        applyPassThroughProperty();
        wireMonitorServer = new WireMonitorServer(6771);
        wireMonitorServer.start();
    }

    /**
     * Test for preserve Location header with/without the http.headers.preserve=Location property
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test to verify preserve 'Location' header")
    public void testPreserveLocationHeader() throws Exception {

        String proxyServiceUrl = getProxyServiceURLHttp("PreserveLocationHeaderTestProxy");

        String requestPayload = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body/>\n" +
                "</soapenv:Envelope>";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "text/xml");
        headers.put(HTTPConstants.HEADER_SOAP_ACTION, "urn:mediate");
        headers.put("Location", "http://localhost:8000");

        HttpRequestUtil.doPost(new URL(proxyServiceUrl), requestPayload, headers);

        String wireResponse = wireMonitorServer.getCapturedMessage();
        String[] wireResponseList = wireResponse.split(System.lineSeparator());

        Assert.assertTrue(wireResponse.contains("Location"));
        for (String line : wireResponseList) {
            if (line.contains("Location")) {
                Assert.assertTrue(line.contains("http://localhost:8000"), "Location header has been modified : " + line);
            }
        }
        // Reaching this line means Location header is in expected state, hence passing the test
        Assert.assertTrue(true);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration(false);
    }

    /**
     * Configure http.headers.preserve=Location in passthru-http.properties
     * @throws Exception
     */
    private void applyPassThroughProperty() throws Exception {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        File passthroughProperties = new File(carbonHome + File.separator + "conf" +
                File.separator + "passthru-http.properties");

        File destinationFile = new File(passthroughProperties.getName());
        Properties properties = new Properties();
        properties.load(new FileInputStream(passthroughProperties));
        properties.setProperty("http.headers.preserve", "Location");
        properties.store(new FileOutputStream(destinationFile), null);
        serverConfigurationManager.applyConfigurationWithoutRestart(destinationFile);
    }
}