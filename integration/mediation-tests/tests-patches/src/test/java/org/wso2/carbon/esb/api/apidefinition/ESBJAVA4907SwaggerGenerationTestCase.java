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
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * ESBJAVA4907SwaggerGenerationTestCase tests whether API definition requests (eg. http://localhost:8280/<APIName>?swagger.json)
 * is routed correctly to the HttpGetProcessor.
 */
public class ESBJAVA4907SwaggerGenerationTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb", description = "Test API definition request is served correctly")
    public void apiDefinitionRequestTest() throws Exception {
        String restURL = getMainSequenceURL() + "swaggerGenerationTestApi?swagger.json";
        SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.doGet(restURL, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        response.getEntity().writeTo(baos);

        log.info("Swagger Definition Response : " + baos.toString());
        Assert.assertTrue("Swagger definition did not contained in the response", baos.toString()
                .contains("API Definition of swaggerGenerationTestApi"));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
