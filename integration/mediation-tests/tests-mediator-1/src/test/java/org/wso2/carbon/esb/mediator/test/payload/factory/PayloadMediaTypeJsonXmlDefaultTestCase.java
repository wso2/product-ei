/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.payload.factory;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class can be used to nativesupportforjson 'Native Support for JSON' scenarios using
 * request Media-Type as xml/json/default
 */
public class PayloadMediaTypeJsonXmlDefaultTestCase extends ESBIntegrationTest {

    private static String serviceURL;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        // applying changes to esb - source view
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/payloadmediatype/" +
                "media_type_xml_json_default.xml");
        serviceURL = this.getProxyServiceURLHttp("Dummy");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "sending xml request using media-type application/xml")
    public void invokeServiceFromXmlRequest() throws Exception {

        // xml request payload
        String payload = "<xml><id_str>84315710834212866</id_str>\n" +
                "\t<entities>\n" +
                "\t\t<hashtags>\n" +
                "\t\t\t<text>peterfalk</text>\n" +
                "\t\t\t<indices>35</indices>\n" +
                "\t\t\t<indices>45</indices>\n" +
                "\t\t</hashtags>\n" +
                "\t</entities>\n" +
                "\t<text>Maybe he'll finally find his keys. #peterfalk</text>\n" +
                "\t<user>\n" +
                "\t\t<id_str>819797</id_str>\n" +
                "\t\t<id>819797</id>\n" +
                "\t</user></xml>";

        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                new URL(serviceURL), writer, "application/xml");

        assertNotNull(response, "Response is null");

        assertTrue(response.contains("\"id_str\": \"peterfalk\""));
    }

    @Test(groups = "wso2.esb", description = "sending json request using media-type application/json",
            dependsOnMethods = "invokeServiceFromXmlRequest")
    public void invokeServiceFromJsonRequest() throws Exception {

        // json request payload
        String payload = "{\n" +
                "    \"id_str\": \"84315710834212866\",\n" +
                "    \"entities\": {\n" +
                "        \"urls\": [\n" +
                "\n" +
                "        ],\n" +
                "        \"hashtags\": [\n" +
                "            {\n" +
                "                \"text\": \"peterfalk\",\n" +
                "                \"indices\": [\n" +
                "                    35,\n" +
                "                    45\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"user_mentions\": [\n" +
                "\n" +
                "        ]\n" +
                "    },\n" +
                "\n" +
                "    \"text\": \"Maybe he'll finally find his keys. #peterfalk\",\n" +
                "    \"user\": {\n" +
                "        \"id_str\": \"819797\",\n" +
                "        \"id\": 819797\n" +
                "    }\n" +
                "}\n";

        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                new URL(serviceURL), writer, "application/json");

        assertNotNull(response, "Response is null");

        assertTrue(response.contains("\"id_str\": \"peterfalk\""));
    }

    @Test(groups = "wso2.esb", description = "sending default request using no value media-type ",
            dependsOnMethods = "invokeServiceFromJsonRequest")
    public void invokeServiceFromDefaultRequest() throws Exception {

        // xml request payload
        String payload = "<xml><id_str>84315710834212866</id_str>\n" +
                "\t<entities>\n" +
                "\t\t<hashtags>\n" +
                "\t\t\t<text>peterfalk</text>\n" +
                "\t\t\t<indices>35</indices>\n" +
                "\t\t\t<indices>45</indices>\n" +
                "\t\t</hashtags>\n" +
                "\t</entities>\n" +
                "\t<text>Maybe he'll finally find his keys. #peterfalk</text>\n" +
                "\t<user>\n" +
                "\t\t<id_str>819797</id_str>\n" +
                "\t\t<id>819797</id>\n" +
                "\t</user></xml>";

        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                new URL(serviceURL), writer, "");
        // default - no value is specified for the media-type

        assertNotNull(response, "Response is null");

        assertTrue(response.contains("\"id_str\": \"peterfalk\""));
    }
}
