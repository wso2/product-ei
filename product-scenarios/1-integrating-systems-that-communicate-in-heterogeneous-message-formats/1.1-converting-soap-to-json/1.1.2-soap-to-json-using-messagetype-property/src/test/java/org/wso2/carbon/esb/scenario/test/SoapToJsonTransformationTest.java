package org.wso2.carbon.esb.scenario.test;

import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.Iterator;

public class SoapToJsonTransformationTest extends ScenarioTestBase {

    private final String carFileName = "soap_to_json_using_messagetype_propertyCompositeApplication_1.0.0";
    private String proxyServiceUrl;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = infraProperties.getProperty(ESB_HTTP_URL) + "/" + "ToJSON";
        deployCarbonApplication(carFileName);
    }

    @Test(description = "1.1.2.1", enabled = true, dataProvider = "1.1.2.1")
    public void convertValidSoapToJson(String request, String expectedResponse) throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();

        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, null, request, "application/xml");
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        JSONObject jsonExpectedResponse = new JSONObject(expectedResponse);
        JSONObject jsonActualResponse = new JSONObject(responsePayload);

        String expectedString = jsonExpectedResponse.toString();
        String actualString = jsonActualResponse.toString();

        Assert.assertEquals(expectedString, actualString);
    }

    @Test(description = "1.1.2.2", enabled = true, dataProvider = "1.1.2.2")
    public void convertMalformedSoapToJson(String request, String expectedResponse) throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();

        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, null, request, "application/xml");
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        JSONObject jsonExpectedResponse = new JSONObject(expectedResponse);
        JSONObject jsonActualResponse = new JSONObject(responsePayload);

        String expectedString = jsonExpectedResponse.toString();
        String actualString = jsonActualResponse.toString();

        Assert.assertEquals(expectedString, actualString);
    }

    @AfterClass(description = "Server Cleanup")
    public void cleanup() throws Exception {

    }

    @DataProvider(name = "1.1.2.1")
    public Iterator<Object[]> soapToJson_1_1_2_1() throws Exception {
        String testCase = "1.1.2.1";
        return getRequestResponseList(testCase).iterator();
    }

    @DataProvider(name = "1.1.2.2")
    public Iterator<Object[]> soapToJson_1_1_2_2() throws Exception {
        String testCase = "1.1.2.2";
        return getRequestResponseList(testCase).iterator();
    }
}
