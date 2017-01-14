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

import org.apache.axiom.om.OMElement;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class JsonFormat_IncomingJson_ArgsJsonExpression_WithStream_TestCase extends ESBIntegrationTest{

    private ServerConfigurationManager serverManager = null;
    String responsePayload;
    private final String JSON_TYPE="application/json";
    private final String XML_TYPE="application/xml";
    private final String JSON_PAYLOAD = "{\n" +
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

    private final String XML_PAYLOAD = "<xml><id_str>84315710834212866</id_str><entities><hashtags><text>wso2</text><indices>35</indices>"
            +"<indices>45</indices>"
            +"</hashtags>"
            +"</entities> "
            +"<text>Maybe he'll finally find his keys. #peterfalk</text>"
            +        "<user>"
            +"<id_str>819797</id_str>"
            +"<id>819797</id>"
            +"</user></xml>";

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(context);
//        serverManager.applyConfiguration(new File(getClass().getResource("/artifacts/ESB/mediatorconfig/payload/factory/axis2/axis2.xml").getPath()));
        super.init();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "With Stream B&F, json format, json evaluators, incoming json, outgoing json ")
    public void incomingJsontransformJsonPayloadByArgsJsonExpressions() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/jsonFormat_JsonExpressiosns.xml");
        postRequestWithJsonPayload(JSON_PAYLOAD,JSON_TYPE);
        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");

        }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "With Stream B&F, xml format, json evaluators, incoming json, outgoing xml ")
    public void incomingJsontransformXmlPayloadByArgsJsonExpressions() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/xmlFormat_JsonExpressiosns.xml");
        postRequestWithJsonPayload(JSON_PAYLOAD,JSON_TYPE);
        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "With Stream B&F, json format, json evaluators, incoming json, outgoing json ")
    public void incomingJsontransformJsonPayloadByArgsJsonXmlExpressionsValues() throws Exception {

        loadESBConfigurationFromClasspath("artifacts/ESB/mediatorconfig/payload/factory/jsonFormat_JsonXmlExpressions_values.xml");
        postRequestWithJsonPayload(JSON_PAYLOAD,JSON_TYPE);
        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");
        assertTrue(responsePayload.contains("MSFT"), "Symbol MSFT not found in response message");


    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "With Stream B&F, json format, json evaluators, incoming json, outgoing json ")
    public void incomingXmltransformJsonPayloadByArgsJsonXmlExpressionsValues() throws Exception {

        loadESBConfigurationFromClasspath("artifacts/ESB/mediatorconfig/payload/factory/jsonFormat_JsonXmlExpressions_values.xml");
        postRequestWithJsonPayload(XML_PAYLOAD,XML_TYPE);
        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");
        assertTrue(responsePayload.contains("MSFT"), "Symbol MSFT not found in response message");
    }



    private void postRequestWithJsonPayload(String payload,String contentType) throws Exception{

        SimpleHttpClient httpClient=new SimpleHttpClient();
        String url="http://localhost:8480/services/Dummy";
        HttpResponse httpResponse = httpClient.doPost(url, null, payload, contentType);
        responsePayload = httpClient.getResponsePayload(httpResponse);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        try {
            cleanup();
        } finally {
            Thread.sleep(3000);
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }
    }

}
