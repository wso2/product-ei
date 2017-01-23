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
package org.wso2.carbon.esb.mediator.test.factory;

import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

public class JsonFormat_IncomingJson_ArgsXmlExpression_WithOutStream_TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/jsonFormat_XmlExpressiosns.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "WithoutStream B&F, json format, xml evaluators, incoming json, outgoing json ")
    public void transformPayloadByArgsValue() throws Exception {

        SimpleHttpClient httpClient=new SimpleHttpClient();
        String payload = "{\n" +
                "    \"id_str\": \"84315710834212866\",\n" +
                "    \"entities\": {\n" +
                "        \"urls\": [\n" +
                "\n" +
                "        ],\n" +
                "        \"hashtags\": [\n" +
                "            {\n" +
                "                \"text\": \"wso2\",\n" +
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
                "}";

        String url="http://localhost:8480/services/Dummy";
        String contentType="application/json";
        HttpResponse httpResponse = httpClient.doPost(url, null, payload, contentType);
        String responsePayload = httpClient.getResponsePayload(httpResponse);


        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }

}
