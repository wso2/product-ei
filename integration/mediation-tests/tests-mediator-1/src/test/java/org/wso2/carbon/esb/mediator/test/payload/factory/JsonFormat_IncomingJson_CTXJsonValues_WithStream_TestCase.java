/*
*Copyright (c) 2005, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.payload.factory;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.*;
import java.net.URL;

import static org.testng.Assert.assertTrue;

public class JsonFormat_IncomingJson_CTXJsonValues_WithStream_TestCase extends ESBIntegrationTest {
    private String responsePayload;
    String proxyName;
    private final String JSON_TYPE = "application/json";
    private final String JSON_PAYLOAD =
            "{\n" + "    \"id_str\": \"84315710834212866\",\n" + "    \"entities\": {\n" + "        \"urls\": [\n"
                    + "\n" + "        ],\n" + "        \"hashtags\": [\n" + "            {\n"
                    + "                \"text\": \"wso2\",\n" + "                \"indices\": [\n"
                    + "                    35,\n" + "                    45\n" + "                ]\n"
                    + "            }\n" + "        ],\n" + "        \"user_mentions\": [\n" + "\n" + "        ]\n"
                    + "    },\n" + "\n" + "    \"text\": \"Maybe he'll finally find his keys. #peterfalk\",\n"
                    + "    \"user\": {\n" + "        \"id_str\": \"819797\",\n" + "        \"id\": 819797\n" + "    }\n"
                    + "}";

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @BeforeClass(alwaysRun = true) public void uploadSynapseConfig()
            throws Exception {
        super.init();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE }) @Test(groups = {"wso2.esb" },
            description = "With Stream B&F, json value, json evaluators, incoming json, outgoing json ", enabled = true)
    public void incomingJsonTransformJsonPayloadByJsonTypeProperties()
            throws Exception {
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig"
                        + File.separator + "payload" + File.separator + "factory" + File.separator
                        + "jsonFormat_JsonExpressions_CTX.xml");
        proxyName = "jsonFormat_JsonExpressions_CTX";
        postRequestWithJsonPayload(JSON_PAYLOAD, JSON_TYPE, proxyName);
        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");

    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE }) @Test(groups = {"wso2.esb" },
            description = "With Stream B&F, xml format, json value, incoming json, outgoing xml ", enabled = true)
    public void incomingJsonTransformXmlPayloadByJsonTypeProperties()
            throws Exception {
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig"
                        + File.separator + "payload" + File.separator + "factory" + File.separator
                        + "xmlFormat_JsonExpressions_CTX.xml");
        proxyName = "Dummy";
        postRequestWithJsonPayload(JSON_PAYLOAD, JSON_TYPE, proxyName);
        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");
    }

    private void postRequestWithJsonPayload(String payload, String contentType, String proxyName) throws Exception {
        String url = "http://localhost:8480/services/" +proxyName;
        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();
        responsePayload = HttpURLConnectionClient
                .sendPostRequestAndReadResponse(data, new URL(url), writer, contentType);
    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @AfterClass(alwaysRun = true) private void destroy() throws Exception {
        try {
            cleanup();
        } finally {
            Thread.sleep(3000);
        }
    }
}
