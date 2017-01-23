/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.samples.test.mediation.jason;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JSONClient;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class Sample441TestCase extends ESBIntegrationTest {

    private String JSONRequest = "{\"symbol\":\"WSO2\", \"ID\":\"StockQuote\"}";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        //loadESBConfigurationFromClasspath("/artifacts/ESB/json/synapse_sample_441.xml");
        loadSampleESBConfiguration(441);
    }

    @Test(groups = "wso2.esb", description = "JSON to XML conversion using sample 441")
    public void testJSONToXMLConversion() throws Exception {

        JSONClient jsonClient = new JSONClient();
        JSONObject response = jsonClient.sendUserDefineRequest(getProxyServiceURLHttp("JSONProxy"), JSONRequest);
        assertNotNull(response, "Response is null");
        JSONObject returnElement = response.getJSONObject("Quote");
        assertNotNull(returnElement, "Quote element contents is null");
        log.debug(returnElement.toString());
        assertTrue(returnElement.toString().contains("WSO2"), "Response JSON invalid");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
