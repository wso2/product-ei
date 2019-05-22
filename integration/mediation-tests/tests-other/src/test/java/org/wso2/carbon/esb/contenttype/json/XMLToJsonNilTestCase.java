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

package org.wso2.carbon.esb.contenttype.json;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4467
 *
 * Test case for transforming XML payload containing <field nil="true"/> to json {"field":null}
 * when the following  property is set in synapse.properties
 *
 * synapse.commons.enableXmlNilReadWrite=true
 */
public class XMLToJsonNilTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator + "json" +
                File.separator + "deployment.toml"));
        super.init();
        verifyAPIExistence("xmltoJsonNilTestAPI");
    }

    @Test(groups = {"wso2.esb"}, description = "Test XML to JSON with nil='true'")
    public void testXmlToJsonNil() throws Exception {

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "text/xml");
        HttpResponse response = HttpRequestUtil.
                doGet(getApiInvocationURL("xmltoJsonNilTestAPI"), requestHeader);

        Assert.assertTrue(response.getData().contains("\"ResponseDescription\":null"),
                "Invalid XML to JSON transformation. " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }
}
