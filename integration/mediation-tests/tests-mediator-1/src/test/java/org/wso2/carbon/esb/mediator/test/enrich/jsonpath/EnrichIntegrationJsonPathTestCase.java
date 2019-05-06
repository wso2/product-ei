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

package org.wso2.carbon.esb.mediator.test.enrich.jsonpath;

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
 * This class tests Enrich Mediator Jsonpath support with Json Payloads
 */
public class EnrichIntegrationJsonPathTestCase extends ESBIntegrationTest {

    private String input;
    private JsonParser parser;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        setJsonPathConfiguration();
        input = FileUtils.readFileToString(new File(getESBResourceLocation() + File.separator + "json" +
                File.separator + "enrichSampleInput.json"));
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" +
                File.separator + "mediatorconfig" + File.separator + "enrich" + File.separator + "api" +
                File.separator + "enrich_json_api_configurations.xml");
        parser = new JsonParser();

    }

    @Test(groups = "wso2.esb", description = "Take the response json and add it as a child to the same json " +
            "specified by the json path")
    public void testAddBodyToChildJsonpath() throws Exception {
        String expectedOutput = "{\n" +
                "    \"patient\": {\n" +
                "        \"name\": \"John Doe\",\n" +
                "        \"address\": null,\n" +
                "        \"phone\": [\n" +
                "            8770586755,\n" +
                "            35352399,\n" +
                "            null\n" +
                "        ],\n" +
                "        \"email\": \"johndoe@gmail.com\"\n" +
                "    },\n" +
                "    \"doctor\": \"thomas collins\",\n" +
                "    \"hospital\": {\n" +
                "        \"patient\": {\n" +
                "            \"name\": \"John Doe\",\n" +
                "            \"address\": null,\n" +
                "            \"phone\": [\n" +
                "                8770586755,\n" +
                "                35352399,\n" +
                "                null\n" +
                "            ],\n" +
                "            \"email\": \"johndoe@gmail.com\"\n" +
                "        },\n" +
                "        \"doctor\": \"thomas collins\",\n" +
                "        \"hospital\": \"grand oak community hospital\",\n" +
                "        \"appointment_date\": \"2017-04-02\"\n" +
                "    },\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich1", expectedOutput,
                "Setting json message body as a child to the original json payload failed");

    }

    @Test(groups = "wso2.esb", description = "Add a child json object to the source payload")
    public void testAddChildUsingJsonPath() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        8770586755,\n" +
                "        35352399,\n" +
                "        null\n" +
                "    ],\n" +
                "    \"email\": \"johndoe@gmail.com\",\n" +
                "    \"doctor\": {\n" +
                "        \"name\": \"thomas collins\",\n" +
                "        \"doctorid\": \"76DA-856\"\n" +
                "    },\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\",\n" +
                "    \"appointment_id\": \"1\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich2", expectedOutput,
                "Adding a child json object failed");

    }

    @Test(groups = "wso2.esb", description = "Add json objects as a child to a json array and json object")
    public void testAddChildJsonpath() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        8770586755,\n" +
                "        35352399,\n" +
                "        null,\n" +
                "        {\n" +
                "            \"countryCode\": 94\n" +
                "        }\n" +
                "    ],\n" +
                "    \"email\": \"johndoe@gmail.com\",\n" +
                "    \"doctor\": {\n" +
                "        \"name\": \"thomas collins\",\n" +
                "        \"doctorid\": \"76DA-856\",\n" +
                "        \"active\": true\n" +
                "    },\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich3", expectedOutput,
                "Adding a json object child to json object and json array failed");

    }

    @Test(groups = "wso2.esb", description = "Enrich value to property, enrich inline json object to body " +
            "and swap the former property")
    public void testEnrichToPropertyReplaceBodyandEnrichPropertyBack() throws Exception {
        String expectedOutput = "{\n" +
                "    \"person\": {\n" +
                "        \"name\": \"Alice\",\n" +
                "        \"email\": \"johndoe@gmail.com\"\n" +
                "    }\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich7", expectedOutput,
                "Enrich property back and forth from the message body failed");
    }

    @Test(groups = "wso2.esb", description = "Enrich json body to property and enrich property to body bacck")
    public void testEnrichToPropertyandEnrichBodyBack() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        8770586755,\n" +
                "        35352399,\n" +
                "        null\n" +
                "    ],\n" +
                "    \"email\": \"johndoe@gmail.com\",\n" +
                "    \"doctor\": {\n" +
                "        \"name\": \"thomas collins\",\n" +
                "        \"doctorid\": \"76DA-856\"\n" +
                "    },\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich8", expectedOutput,
                "Enriching json body to property and vice versa failed");

    }

    @Test(groups = "wso2.esb", description = "Enrich Json body to Property, replace body " +
            "and enrich the property back to body")
    public void testEnrichBodyToPropertyReplaceBodyandEnrichPropertyBack() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        8770586755,\n" +
                "        35352399,\n" +
                "        null\n" +
                "    ],\n" +
                "    \"email\": \"johndoe@gmail.com\",\n" +
                "    \"doctor\": {\n" +
                "        \"name\": \"thomas collins\",\n" +
                "        \"doctorid\": \"76DA-856\"\n" +
                "    },\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich9", expectedOutput,
                "Enriching json body to property, replacing body and enriching property back to body failed");

    }

    @Test(groups = "wso2.esb", description = "Enrich a child value to its parent and replace")
    public void testEnrichChildPropertyToParent() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        8770586755,\n" +
                "        35352399,\n" +
                "        null\n" +
                "    ],\n" +
                "    \"email\": \"johndoe@gmail.com\",\n" +
                "    \"doctor\": \"thomas collins\",\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich11", expectedOutput,
                "Enriching the child value to the parent failed");

    }

    @Test(groups = "wso2.esb", description = "Enrich a json payload to property, receive a xml body " +
            "from backend and enrich the json property back to body")
    public void testEnrichJsonToPropertyAndReplaceXmlBody() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        8770586755,\n" +
                "        35352399,\n" +
                "        null\n" +
                "    ],\n" +
                "    \"email\": \"johndoe@gmail.com\",\n" +
                "    \"doctor\": {\n" +
                "        \"name\": \"thomas collins\",\n" +
                "        \"doctorid\": \"76DA-856\"\n" +
                "    },\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich12", expectedOutput,
                "Enriching a json property to a xml payload failed");

    }

    @Test(groups = "wso2.esb", description = "Enrich several inline objects to properties and enrich them to body")
    public void testEnrichInlineToPropertyAndBody() throws Exception {
        String expectedOutput = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"address\": null,\n" +
                "    \"phone\": [\n" +
                "        32535235,\n" +
                "        35353897,\n" +
                "        null\n" +
                "    ],\n" +
                "    \"email\": \"alice@integrator.net\",\n" +
                "    \"doctor\": {\n" +
                "        \"name\": \"adam eve\",\n" +
                "        \"doctorid\": \"934T-76A\"\n" +
                "    },\n" +
                "    \"hospital\": \"grand oak community hospital\",\n" +
                "    \"appointment_date\": \"2017-04-02\"\n" +
                "}";

        executeSequenceAndAssertResponse("testenrich14", expectedOutput,
                "Enriching several inline objects to properties and enrich them to body failed");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    private void executeSequenceAndAssertResponse(String apiName, String expectedOutput, String errorMessage)
            throws Exception {
        URL endpoint = new URL(getApiInvocationURL(apiName));

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
