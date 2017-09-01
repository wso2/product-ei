/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.payload.factory;

import org.apache.http.HttpResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * The payload factory mediator escapes the special characters in a string value of a json.
 * The string values is identified based on whether the replacement entry is starting with "{" or "[".
 * In some case the json string starts with whitespace or new line. so it will not satisfy the correct condition
 * so the double quotes will be escaped.
 */
public class JsonFormat_IncomingJson_StartsWithSpaceTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig" + File.separator + "payload"
                        + File.separator + "factory" + File.separator + "jsonFormat_JsonStartsWithSpace.xml");
    }

    @Test
    public void testJsonStringStartsWithWhiteSpace() throws Exception {

        String jsonStringStartingWithSpace = " {\"hello\":\"world\"}"; // note that there is whitespace before the {

        String responseJsonString = postJSONPayload(jsonStringStartingWithSpace);

        assertEquals(responseJsonString, "{\"hello\":\"world\"}", "Response is escaped incorrectly");
    }

    @Test
    public void testJsonStringStartsWithNewLine() throws Exception {

        String jsonStringStartingWithSpace = "\n{\"hello\":\"world\"}";

        String responseJsonString = postJSONPayload(jsonStringStartingWithSpace);

        assertEquals(responseJsonString, "{\"hello\":\"world\"}", "Response is escaped incorrectly");
    }

    private String postJSONPayload(String payload) throws Exception {

        SimpleHttpClient httpClient = new SimpleHttpClient();
        String url = getMainSequenceURL() + "startsWithSpaceTest";
        HttpResponse httpResponse = httpClient.doPost(url, null, payload, "application/json");
        return httpClient.getResponsePayload(httpResponse);
    }

}