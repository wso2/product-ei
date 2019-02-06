package org.wso2.carbon.esb.scenario.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;

/**
 * This test class test routing messages based on the regular expression with valid, Invalid, Null or empty cases
 */

public class RoutingBasedOnRegularExpressionTest extends ScenarioTestBase {

    public static final Log log = LogFactory.getLog(RoutingBasedOnRegularExpressionTest.class);

    @BeforeClass
    public void init() throws Exception {
        super.init();

    }

    @Test(description = "5.1.2.1.1")
    public void routeMessagesBasedOnValidRegex() throws Exception {
        String header = "5_1";
        String url = getApiInvocationURLHttp("5_1_1_Routing_messages_based_on_content_of_message_test");

        String request ="<m:GetStockPrice xmlns:m=\"http://www.example.org/stock\">\n" +
                "   <m:StockName>IBM</m:StockName>\n" +
                "</m:GetStockPrice>";

        String expectedResponse = "<m:GetStockPriceResponse xmlns:m=\"http://www.example.org/stock\">\n" +
                "    <m:Price>34.5</m:Price>\n" +
                "</m:GetStockPriceResponse>";

        HTTPUtils.invokePoxEndpointAndAssert(url, request, HttpConstants.MEDIA_TYPE_TEXT_XML, header, expectedResponse,
                200, "Switch messages based on a valid regex");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
