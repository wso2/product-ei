/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package org.wso2.carbon.esb.passthru.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests for force validation passthru-http properties introduced for JSON (force.json.message.validation)
 * and XML (force.xml.message.validation) messages.
 */
public class ForceMessageValidationTestCase extends ESBIntegrationTest {

    private static final String API_NAME = "ForceMessageValidationTestAPI";

    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient;

    /**
     * Add force.xml.message.validation and force.json.message.validation properties to passthru-http.properties, and
     * deploy synapse configurations.
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(
                new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));

        serverConfigurationManager.applyConfiguration(new File(
                getESBResourceLocation().replace("//", "/") + File.separator + "passthru" + File.separator + "transport"
                        + File.separator + "forceMessageValidation" + File.separator + "passthru-http.properties"));
        super.init();

        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "passthru" +
                        File.separator + "transport" + File.separator + "forceMessageValidation" + File.separator
                        + "ForceMessageValidationTestAPI.xml");

        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    /**
     * Test for invalid JSON message with force.json.message.validation property.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test for invalid JSON payload with force.json.message.validation "
            + "property.")
    public void testInvalidJSONMessage() throws Exception {
        logViewerClient.clearLogs();

        String inputPayload = "{\"abc\" :\"123\" } xyz";
        String expectedOutput = "Error while building the message.\n" + "{\"abc\" :\"123\" } xyz";

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "application/json");

        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_NAME)), inputPayload, requestHeader);

        Assert.assertTrue(Utils.assertIfSystemLogContains(logViewerClient, expectedOutput), "Test fails for forcing "
                + "JSON validation with force.json.message.validation passthru-http property.");
    }

    /**
     * Test for invalid XML message with force.xml.message.validation property.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test for invalid XML payload with force.xml.message.validation property.")
    public void testInvalidXMLMessage() throws Exception {
        logViewerClient.clearLogs();

        String inputPayload = "<foo>\n" + "  <bar>xyz</bar>\n" + "</foo>\n" + "</bar>";
        String expectedOutput =
                "Error while building the message.\n" + "<foo>\n" + "  <bar>xyz</bar>\n" + "</foo>\n" + "</bar>";

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "application/xml");

        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_NAME)), inputPayload, requestHeader);
        Assert.assertTrue(Utils.assertIfSystemLogContains(logViewerClient, expectedOutput),
                "Test fails for forcing XML validation with force.xml.message.validation passthru-http property.");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
