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
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;

/**
 * This test class tests routing messages into relevant backends based on the XPath by using filter mediator.
 */

public class RoutingBasedOnXpathWithFilterMTest extends ScenarioTestBase {

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "5.1.1.3.1")
    public void routeBasedOnValidXpathWithFilterM() throws Exception {
        String header = "5_1";
        String url = getApiInvocationURLHttp("5_1_API_Routing_messages_based_on_content_of_message_test" +
                "/valid_xpath_test_with_filterM");
        String request = ScenarioConstants.COMMON_ROUTING_REQUEST;
        String expectedResponse = "<m:GetStockPriceResponse xmlns:m=\"http://www.example.org/stock\">\n"
                                + "  <m:Price>34.5</m:Price>\n"
                                + "</m:GetStockPriceResponse>";

        HTTPUtils.invokePoxEndpointAndAssert(url, request, HttpConstants.MEDIA_TYPE_TEXT_XML, header, expectedResponse,
                200, "Route messages based on given Xpath with valid case name");
    }

    @Test(description = "5.1.1.3.2")
    public void routeBasedOnInvalidXpathWithFilterM() throws Exception {
        String header = "5_1";
        String url = getApiInvocationURLHttp("5_1_API_Routing_messages_based_on_content_of_message_test" +
                "/Invalid_xpath_test_with_filterM");
        String request = ScenarioConstants.COMMON_ROUTING_REQUEST;
        String expectedResponse = "<m:GetStockPriceResponse xmlns:m=\"http://www.example.org/stock\">\n"
                + "  <m:Price>34.5</m:Price>\n"
                + "</m:GetStockPriceResponse>";

        HTTPUtils.invokePoxEndpointAndAssert(url, request, HttpConstants.MEDIA_TYPE_TEXT_XML, header, expectedResponse,
                200, "Route messages based on given Xpath with valid case name");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
