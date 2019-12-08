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

package org.wso2.carbon.esb.mediator.test.foreach;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * Testcases to test the native JSON support for Foreach mediator
 */
public class ForEachnativeJSONTestCase extends ESBIntegrationTest {
    private String input;
    private JsonParser parser;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        setJsonPathConfiguration();
        input = FileUtils.readFileToString(new File(getESBResourceLocation() + File.separator + "json" +
                File.separator + "foreachSampleInput.json"));
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" +
                File.separator + "mediatorconfig" + File.separator + "foreach" +
                File.separator + "foreach_native_json_configurations.xml");
        parser = new JsonParser();
    }

    @Test(groups = "wso2.esb", description = "Test the foreach native json support basic flow " +
            "by transforming the elements of the json array")
    public void testForeachNativeJsonBasicFlow() throws Exception {
        String expectedOutput = "{\n" +
                "    \"students\": {\n" +
                "        \"studentlist\": [\n" +
                "            {\n" +
                "                \"candidate\": {\n" +
                "                    \"Name\": \"Alice\",\n" +
                "                    \"Age\": 35\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"candidate\": {\n" +
                "                    \"Name\": \"Bob\",\n" +
                "                    \"Age\": 32\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"candidate\": {\n" +
                "                    \"Name\": \"Camry\",\n" +
                "                    \"Age\": 27,\n" +
                "                    \"Status\": [\n" +
                "                        \"Married\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        executeSequenceAndAssertResponse("foreachsample", "/foreachjson1", expectedOutput,
                "Foreach mediator native json flow testcase failed");

    }

    @Test(groups = "wso2.esb", description = "Test the foreach native json support with different " +
            "type of elements in the JSON array")
    public void testForeachNativeJsonWithDifferentTypeElements() throws Exception {
        String expectedOutput = "{\n" +
                "    \"getQuote\": [\n" +
                "        \"check\",\n" +
                "        {\n" +
                "            \"hello\": \"world\"\n" +
                "        },\n" +
                "        true,\n" +
                "        1\n" +
                "    ]\n" +
                "}";

        executeSequenceAndAssertResponse("foreachsample", "/foreachjson2", expectedOutput,
                "Foreach mediator native json with different elements failed");

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    private void executeSequenceAndAssertResponse(String apiName, String context, String expectedOutput,
                                                  String errorMessage) throws Exception {
        URL endpoint = new URL(getApiInvocationURL(apiName) + context);
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");
        HttpResponse httpResponse = HttpRequestUtil.doPost(endpoint, input, header);
        assertEqualJsonObjects(httpResponse.getData(), expectedOutput, errorMessage);
    }

    //set the basic json configurations so that we can use the gson parser to assert json payloads
    private void setJsonPathConfiguration() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new GsonJsonProvider(new GsonBuilder().serializeNulls().create());
            private final MappingProvider mappingProvider = new GsonMappingProvider();

            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    private void assertEqualJsonObjects(String json1, String json2, String errorMessage) {
        JsonElement element1 = parser.parse(json1);
        JsonElement element2 = parser.parse(json2);

        assertEquals(element1, element2, errorMessage);
    }
}
