package org.wso2.carbon.esb.scenario.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonToSoapUsingXSLTTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(JsonToSoapUsingXSLTTest.class);

    private String cappName = "scenario_1_3-synapse-configCompositeApplication";
    private String cappNameWithVersion = "scenario_1_3-synapse-configCompositeApplication_1.0.0";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        deployCarbonApplication(cappNameWithVersion);
    }

    @Test(description = "1.3.1.1", enabled = true)
    public void testMessageTransformation() throws IOException {
        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>(1);
        String payload = "{\"getQuote\":{\"request\":{\"symbol\":\"WSO2\"}}}";
        HttpResponse response = httpClient.
                doPost(getApiInvocationURLHttp("jsontosoap/usingXSLT"), headers, payload, "application/json");
        String responseMsg = httpClient.getResponsePayload(response);
        log.info("Response message: " + responseMsg);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Json to SOAP transformation failed");

        JSONObject responseJsonObj = new JSONObject(responseMsg);
        Assert.assertEquals(responseJsonObj.getJSONObject("getQuoteResponse").getJSONObject("return").get("symbol"),
                "WSO2", "Expected response not received");

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }
}
