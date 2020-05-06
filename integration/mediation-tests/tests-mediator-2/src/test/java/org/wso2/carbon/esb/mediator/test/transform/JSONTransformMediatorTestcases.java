/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.mediator.test.transform;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;

import static org.testng.Assert.assertEquals;

/**
 * Testcase to test the basic functionality of JSON Transform Mediator with a complex schema
 */
public class JSONTransformMediatorTestcases extends ESBIntegrationTest {

    private Map<String, String> httpHeaders = new HashMap<>();
    private JsonParser parser;

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        ResourceAdminServiceClient resourceAdminServiceClient =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        URL complexSchemaURL = new URL("file:///" + getESBResourceLocation() + File.separator + "mediatorconfig"
                + File.separator + "transform" + File.separator + "complexSchema.json");
        resourceAdminServiceClient.addResource("/_system/config/complexSchema.json", "", "JSON Schema"
                , new DataHandler(complexSchemaURL));
        httpHeaders.put("Content-Type", "application/json");
        parser = new JsonParser();
    }

    @Test(groups = "wso2.esb", description = "Performing a JSON to JSON transformation using a complex schema " +
            "stored in registry")
    public void testComplexJsonSchema() throws Exception {
        String payload = "{\n" +
                "  \"fruit\"           : \"12345\",\n" +
                "  \"price\"           : \"7.5\",\n" +
                "  \"simpleObject\"    : {\"age\":\"234\"},\n" +
                "  \"simpleArray\"     : [\"true\",\"false\",\"true\"],\n" +
                "  \"objWithArray\"    : {\"marks\":[\"34\",\"45\",\"56\",\"67\"]},\n" +
                "  \"arrayOfObjects\"  : [{\"maths\":\"90\"},{\"physics\":\"95\"},{\"chemistry\":\"65\"}],\n" +
                "  \"singleObjArray\"  : 1.618,\n" +
                "  \"nestedObject\"    : {\"Lahiru\" :{\"age\":\"27\"},\"Nimal\" :" +
                "{\"married\" :\"true\"}, \"Kamal\" : {\"scores\": [\"24\",45,\"67\"]}},\n" +
                "  \"nestedArray\"     : [[12,\"23\",34],[\"true\",false],[\"Linking Park\",\"Coldplay\"]],\n" +
                "  \"allNumericArray\" : [\"3\",\"1\",\"4\"],\n" +
                "  \"Hello\"           : 890,\n" +
                "  \"league_goals\"    : \"10\"\n" +
                "}";

        String expectedOutput = "{\n" +
                "    \"fruit\": \"12345\",\n" +
                "    \"price\": 7.5,\n" +
                "    \"simpleObject\": {\n" +
                "        \"age\": 234\n" +
                "    },\n" +
                "    \"simpleArray\": [\n" +
                "        true,\n" +
                "        false,\n" +
                "        \"true\"\n" +
                "    ],\n" +
                "    \"objWithArray\": {\n" +
                "        \"marks\": [\n" +
                "            34,\n" +
                "            45,\n" +
                "            56,\n" +
                "            67\n" +
                "        ]\n" +
                "    },\n" +
                "    \"arrayOfObjects\": [\n" +
                "        {\n" +
                "            \"maths\": 90\n" +
                "        },\n" +
                "        {\n" +
                "            \"physics\": 95\n" +
                "        },\n" +
                "        {\n" +
                "            \"chemistry\": 65\n" +
                "        }\n" +
                "    ],\n" +
                "    \"singleObjArray\": [\n" +
                "        1.618\n" +
                "    ],\n" +
                "    \"nestedObject\": {\n" +
                "        \"Lahiru\": {\n" +
                "            \"age\": 27\n" +
                "        },\n" +
                "        \"Nimal\": {\n" +
                "            \"married\": true\n" +
                "        },\n" +
                "        \"Kamal\": {\n" +
                "            \"scores\": [\n" +
                "                24,\n" +
                "                45,\n" +
                "                67\n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"nestedArray\": [\n" +
                "        [\n" +
                "            12,\n" +
                "            23,\n" +
                "            34\n" +
                "        ],\n" +
                "        [\n" +
                "            true,\n" +
                "            false\n" +
                "        ],\n" +
                "        [\n" +
                "            \"Linking Park\",\n" +
                "            \"Coldplay\"\n" +
                "        ]\n" +
                "    ],\n" +
                "    \"allNumericArray\": [\n" +
                "        3,\n" +
                "        1,\n" +
                "        4\n" +
                "    ],\n" +
                "    \"Hello\": 890,\n" +
                "    \"league_goals\": 10\n" +
                "}";
        HttpResponse response = HttpRequestUtil.doPost(
                new URL(getProxyServiceURLHttp("transformMediatorBasic")), payload, httpHeaders);
        assertEqualJsonObjects(response.getData(), expectedOutput,
                "JSON to JSON transformation with schema did not happen properly");
    }

    private void assertEqualJsonObjects(String json1, String json2, String errorMessage) {
        JsonElement element1 = parser.parse(json1);
        JsonElement element2 = parser.parse(json2);
        assertEquals(element1, element2, errorMessage);
    }
}
