package org.wso2.carbon.esb.json;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.utils.clients.JSONClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;



import static org.testng.Assert.assertEquals;


public class ESBJAVA3183TestCase extends ESBIntegrationTest {
    private JSONClient jsonclient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        jsonclient = new JSONClient();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/endpoint-template.xml");
    }

    @Test(groups = {"wso2.esb", "localOnly"}, description = "Http Endpoint Test")
    public void testRelativeLocationHeader() throws Exception {
        String addUrl = getProxyServiceURLHttp("iteratetest");
        String request = "{\n" +
                "  \"request\": {\n" +
                "    \"terms\": {\n" +
                "      \"term\": [\n" +
                "        \"one\",\n" +
                "        \"two\",\n" +
                "        \"three\",\n" +
                "        \"four\",\n" +
                "        \"five\",\n" +
                "        \"six\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String expectedResult = "{\"response\":{\"response\":[\"Hello, From Service One\",\"Hello, From Service Two\",\"Hello, From Service Five\",\"Hello, From Service Four\",\"Hello, From Service Six\",\"Hello, From Service Three\"]}}";
        String actualResult = jsonclient.sendUserDefineRequest(addUrl, request).toString();
        int i = 0;
        if (actualResult != null) {
            if (actualResult.contains("One")) {
                ++i;
            }
            if (actualResult.contains("Two")) {
                ++i;
            }
            if (actualResult.contains("Three")) {
                ++i;
            }
            if (actualResult.contains("Four")) {
                ++i;
            }
            if (actualResult.contains("Five")) {
                ++i;
            }
            if (actualResult.contains("Six")) {
                ++i;
            }
        }
        boolean result = i > 0 ? true : false;
        assertEquals(result, true, "Could not process relative Location header.");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }


}

