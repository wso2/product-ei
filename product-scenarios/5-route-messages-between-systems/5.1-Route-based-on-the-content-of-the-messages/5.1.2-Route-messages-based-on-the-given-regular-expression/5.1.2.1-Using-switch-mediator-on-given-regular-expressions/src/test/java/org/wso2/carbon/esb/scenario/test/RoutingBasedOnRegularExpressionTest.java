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
 * This test class tests routing messages into relevant backends based on the regex by using switch mediator.
 */

public class RoutingBasedOnRegularExpressionTest extends ScenarioTestBase {

    public static final Log log = LogFactory.getLog(RoutingBasedOnRegularExpressionTest.class);
    String url = getApiInvocationURLHttp("5_1_API_Routing_messages_based_on_content_of_message_test" +
            "/regex_test_with_switchM");

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "5.1.2.1.1")
    public void routeMessagesBasedOnValidRegexWithSwitchM() throws Exception {
        String header = "basic_xml";
        String request = "<m:GetPrice xmlns:m=\"https://www.w3schools.com/prices\">\n"
                + "   <m:Item>Apple</m:Item>\n"
                + "</m:GetPrice>";
        String expectedResponse = "<m:GetPriceResponse xmlns:m=\"https://www.w3schools.com/prices\">\n"
                + "   <m:Price>1.90</m:Price>\n"
                + "</m:GetPriceResponse>";

        HTTPUtils.invokePoxEndpointAndAssert(url, request, HttpConstants.MEDIA_TYPE_TEXT_XML, header, expectedResponse,
                200, "Switch messages based on a valid regex");
    }

    @Test(description = "5.1.2.1.2")
    public void routeMessagesBasedOnInvalidRegexWithSwitchM() throws Exception {
        String header = "5_1_2_1_2";
        String request = "<m:GetPrice xmlns:m=\"https://www.w3schools.com/prices\">\n"
                       + "<m:Item>Orange</m:Item>\n"
                       + "</m:GetPrice>";

        String expectedResponse = "<message xmlns=\"http://ws.apache.org/ns/synapse\">\n"
                                + "<error>Regular expression not exists or value is empty</error>\n"
                                + "</message>";

        HTTPUtils.invokePoxEndpointAndAssert(url, request, HttpConstants.MEDIA_TYPE_TEXT_XML, header, expectedResponse,
                200, "Switch messages based on a valid regex");
    }

    @Test(description = "5.1.2.1.3")
    public void routeMessagesBasedOnEmptyRegexWithSwitchM() throws Exception {
        String header = "5_1_2_1_3";
        String request = "<m:GetPrice xmlns:m=\"https://www.w3schools.com/prices\">\n"
                       + "<m:Item></m:Item>\n"
                       + "</m:GetPrice>";

        String expectedResponse = "<message xmlns=\"http://ws.apache.org/ns/synapse\">\n"
                                + "<error>Regular expression not exists or value is empty</error>\n"
                                + "</message>";

        HTTPUtils.invokePoxEndpointAndAssert(url, request, HttpConstants.MEDIA_TYPE_TEXT_XML, header, expectedResponse,
                200, "Switch messages based on a valid regex");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}