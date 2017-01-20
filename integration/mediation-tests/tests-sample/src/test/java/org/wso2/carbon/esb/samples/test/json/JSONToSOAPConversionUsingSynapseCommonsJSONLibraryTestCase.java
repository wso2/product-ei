/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.samples.test.json;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JSONClient;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class JSONToSOAPConversionUsingSynapseCommonsJSONLibraryTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverManager = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        File sourceFile = new File(FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "ESB" + File.separator + "json" + File.separator + "axis2.xml");
        serverManager.applyConfiguration(sourceFile);
        super.init();
        loadSampleESBConfiguration(440);
    }

    @Test(groups = "wso2.esb", description = "JSON to SOAP conversion using sample 440")
    public void testJSONToSOAPConversion() throws Exception {
        JSONClient jsonClient = new JSONClient();
        JSONObject response = jsonClient.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("JSONProxy"), "IBM", "getQuote");
        assertNotNull(response, "Response is null");
        JSONObject returnElement = response.getJSONObject("getQuoteResponse");
        assertNotNull(returnElement, "return element contents is null");
        assertEquals(returnElement.getJSONObject("return").getString("symbol"), "IBM", "Symbol is mismatch");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverManager.restoreToLastConfiguration();
    }


}
