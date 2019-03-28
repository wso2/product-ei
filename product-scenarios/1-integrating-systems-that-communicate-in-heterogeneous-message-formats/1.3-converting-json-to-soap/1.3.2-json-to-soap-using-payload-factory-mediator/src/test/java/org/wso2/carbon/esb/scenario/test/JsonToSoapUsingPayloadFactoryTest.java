/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;

import java.io.IOException;

public class JsonToSoapUsingPayloadFactoryTest extends ScenarioTestBase {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "1.3.2.1")
    public void validJsonToSoapTransformation() throws IOException {
        String request = "{\"getQuote\":{\"request\":{\"symbol\":\"WSO2\"}}}";
        String messageId = "1.3.2.1";
        String apiInvocationUrl = getApiInvocationURLHttp("1_3_2_API_json_to_soap_using_payload_factory");

        JSONObject responseJsonObj = HTTPUtils.invokeApiAndGetResponse(apiInvocationUrl, request, HttpConstants.MEDIA_TYPE_APPLICATION_JSON);

        Assert.assertEquals(responseJsonObj.getJSONObject("getQuoteResponse").getJSONObject("return").get("symbol"),
                            "WSO2", "Expected response not received in " + messageId);
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
