package org.wso2.carbon.esb.scenario.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SendMediatorTest extends ScenarioTestBase{

    private final String carFileName = "2_1_1_3_httpToHttpProtocolTranslationCompositeApplication_1.0.0";
    private String proxyServiceUrl;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = getProxyServiceURLHttp("2_1_1_3_httpToHttpProtocolTranslationProxy");
        deployCarbonApplication(carFileName);
    }

    @Test(description = "2.1.1.3 test case execution process started", enabled = true, dataProvider = "2.1.1.3")
    public void  httpEndpointViaSendMediator(String request, String expectedResponse, String header) throws Exception {
        log.info("Executing test case 2.1.1.3");
        SimpleHttpClient httpClient = new SimpleHttpClient();
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, headers, request, HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);
        log.info("Actual response received 1.1.2.1: " + responsePayload);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "HTTP to HTTP transformation failed");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }

    @DataProvider(name = "2.1.1.3")
    public Iterator<Object[]> http_2_1_1_3() throws Exception {
        String testCase = "2.1.1.3";
        return getRequestResponseHeaderList(testCase).iterator();
    }
}
