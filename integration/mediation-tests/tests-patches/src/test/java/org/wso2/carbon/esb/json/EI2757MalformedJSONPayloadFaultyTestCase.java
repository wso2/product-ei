/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.json;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;
import java.io.File;
import static org.testng.Assert.assertEquals;
/*
This testcase is to test the fix done for https://github.com/wso2/product-ei/issues/2757
*/
public class EI2757MalformedJSONPayloadFaultyTestCase extends ESBIntegrationTest {
    private final SimpleHttpClient httpClient = new SimpleHttpClient();
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" +
                File.separator + "json" + File.separator + "malformedJsonFaulty.xml");
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "test malformed json in faulty sequence",  enabled = true)
    public void testMalformedJSONPayloadInFaultySequence() throws Exception {
        String payload = "{\"Symbol\": \"IBM}";
        HttpResponse response = httpClient.doPost(getApiInvocationURL("malformedJson")
                , null, payload, "application/json");
        assertEquals(response.getStatusLine().getStatusCode(), 500,
                "The status code set in the faulty sequence is not received");
    }
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}