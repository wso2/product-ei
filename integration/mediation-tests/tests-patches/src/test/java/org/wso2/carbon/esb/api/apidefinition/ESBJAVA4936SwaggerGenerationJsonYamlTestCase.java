/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.api.apidefinition;

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * ESBJAVA4936SwaggerGenerationJsonYamlTestCase tests whether Swagger API definitions are correctly generated for
 * both JSON and Yaml formats.
 */
public class ESBJAVA4936SwaggerGenerationJsonYamlTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private final String FULL_RESOURCE_PATH = getESBResourceLocation() + File.separator + "restapi" +
            File.separator + "apidefinition" + File.separator;
    private final String RELATIVE_RESOURCE_PATH = File.separator + "artifacts" + File.separator + "ESB" + File
            .separator + "restapi" + File.separator + "apidefinition" + File.separator + "swaggergeneration" + File
            .separator;
    private final String JAR_FILE_NAME = "snakeyaml_1.16.0.wso2v1.jar";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfigurationWithoutRestart(new File(FULL_RESOURCE_PATH + "carbon.xml"));
        serverConfigurationManager.copyToComponentDropins
                (new File(getClass().getResource(RELATIVE_RESOURCE_PATH + JAR_FILE_NAME).toURI()));
        serverConfigurationManager.restartGracefully();
        super.init();
        loadESBConfigurationFromClasspath(RELATIVE_RESOURCE_PATH + "StockQuoteApi.xml");
    }

    @Test(groups = "wso2.esb", description = "Test API definition for JSON is generated correctly")
    public void jsonDefinitionTest() throws Exception {
        String restURL = getMainSequenceURL() + "StockQuoteAPI?swagger.json";
        SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.doGet(restURL, null);
        String payload = httpClient.getResponsePayload(response);

        log.info("JSON Definition Response : " + payload);
        Assert.assertTrue("Swagger JSON definition did not contained in the response", payload
                .contains("API Definition of StockQuoteAPI"));
    }

    @Test(groups = "wso2.esb", description = "Test API definition for Yaml is generated correctly")
    public void yamlDefinitionTest() throws Exception {
        String restURL = getMainSequenceURL() + "StockQuoteAPI?swagger.json";
        SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.doGet(restURL, null);
        String payload = httpClient.getResponsePayload(response);

        log.info("Yaml Definition Response : " + payload);
        Assert.assertTrue("Swagger Yaml definition did not contained in the response", payload
                .contains("API Definition of StockQuoteAPI"));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.removeFromComponentLib(RELATIVE_RESOURCE_PATH + JAR_FILE_NAME);
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }
}
