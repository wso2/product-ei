package org.wso2.carbon.esb.scenario.test;

import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RoutingBasedOnXpathTest extends ScenarioTestBase {

    private String cappNameWithVersion = "scenario_5_1_1_CompositeApplication_1.0.0";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        deployCarbonApplication(cappNameWithVersion);
    }

    @Test(description = "5.1.1.1.1", enabled = true, dataProvider = "5.1.1.1.1")
    public void routeMessagesBasedOnXpathWithValidCaseName(String request, String expectedResponse, String header) throws Exception {
        log.info("==== Executing test case 5.1.1.1.1 ====");

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put(ScenarioConstants.MESSAGE_ID, header);

        HttpResponse response = httpClient.
                doPost(getApiInvocationURLHttp("RouteMessageAPI"), headers, request, "text/xml");

        String responseMsg = httpClient.getResponsePayload(response);

        log.info("=== Actual response received 5.1.1.1.1: === " + responseMsg);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Message route to the correct endpoint");

        JSONObject jsonExpectedResponse = new JSONObject(expectedResponse);
        JSONObject jsonActualResponse = new JSONObject(responseMsg.trim());

        String expectedString = jsonExpectedResponse.toString();
        String actualString = jsonActualResponse.toString();

        Assert.assertEquals(expectedString, actualString);

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }

    @DataProvider(name = "5.1.1.1.1")
    public Iterator<Object[]> RouteMessage_5_1_1_1_1() throws Exception {
        String testCase = "5.1.1.1.1";
        return getRequestResponseHeaderList(testCase).iterator();
    }





}
