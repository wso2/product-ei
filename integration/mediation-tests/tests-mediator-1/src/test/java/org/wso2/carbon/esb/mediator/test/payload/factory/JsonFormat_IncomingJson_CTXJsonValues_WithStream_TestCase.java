package org.wso2.carbon.esb.mediator.test.payload.factory;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import static org.testng.Assert.assertTrue;

public class JsonFormat_IncomingJson_CTXJsonValues_WithStream_TestCase extends ESBIntegrationTest{
	private String responsePayload;
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
	}

	@SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
	@Test(groups = {"wso2.esb"}, description = "With Stream B&F, json value, json evaluators, incoming json, outgoing json " , enabled = false)
	public void incomingJsontransformJsonPayloadByJsonTypeProperties() throws Exception {

		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/jsonFormat_JsonExpressions_CTX.xml");
		postRequestWithJsonPayload(JSON_PAYLOAD,JSON_TYPE);
		assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");

	}

	@SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
	@Test(groups = {"wso2.esb"}, description = "With Stream B&F, xml format, json value, incoming json, outgoing xml ", enabled = false)
	public void incomingJsontransformXmlPayloadByJsonTypeProperties() throws Exception {

		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/xmlFormat_JsonExpressions_CTX.xml");
		postRequestWithJsonPayload(JSON_PAYLOAD,JSON_TYPE);
		assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");
	}

	private void postRequestWithJsonPayload(String payload,String contentType) throws Exception{


		String url="http://localhost:8480/services/Dummy";
		Reader data = new StringReader(JSON_PAYLOAD);
		Writer writer = new StringWriter();

		responsePayload = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
				new URL(url), writer, JSON_TYPE);

        /*SimpleHttpClient httpClient = new SimpleHttpClient();
        String url="http://localhost:8280/services/Dummy";
        HttpResponse httpResponse = httpClient.doPost(url, null, payload, contentType);
        responsePayload = httpClient.getResponsePayload(httpResponse);*/
	}

	@SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
	@AfterClass(alwaysRun = true)
	private void destroy() throws Exception {
		try {
			cleanup();
		} finally {
			Thread.sleep(3000);
//            serverManager.restoreToLastConfiguration();
//            serverManager = null;
		}
	}
}
