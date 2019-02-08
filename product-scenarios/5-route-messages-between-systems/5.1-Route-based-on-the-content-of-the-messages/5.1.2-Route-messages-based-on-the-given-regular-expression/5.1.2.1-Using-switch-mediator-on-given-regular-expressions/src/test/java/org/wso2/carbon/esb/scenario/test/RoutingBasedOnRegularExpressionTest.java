/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
 * This test class tests routing the messages into relevant backends based on the regular expressions by using switch mediator and filter mediator.
 * This includes the test cases of valid, Invalid, Null or empty cases of regular expressions.
 */

public class RoutingBasedOnRegularExpressionTest extends ScenarioTestBase {

    public static final Log log = LogFactory.getLog(RoutingBasedOnRegularExpressionTest.class);

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "5.1.2.1.1")
    public void routeMessagesBasedOnValidRegex() throws Exception {
        String header = "5_1_1_1_1";
        String url = getApiInvocationURLHttp("5_1_2_1_1_Routing_messages_based_on_invalid_regex_test");

        String request = "<m:GetStockPrice xmlns:m=\"http://www.example.org/stock\">\n" +
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
