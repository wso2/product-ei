/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.scenario.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class is to test if xml payload can be enriched before it goes to the backend server by adding sibling elements
 * to the payload. This can be done using enrich mediator. This class focusses on various ways of achieving that.
 */
public class AddSiblingTest extends ScenarioTestBase {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    //This test is to verify if payload can be modified by adding sibling elements inline to the payload.
    @Test(description = "Adding an inline content as a sibling to the message body")
    public void AddSiblingInline() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_2_1_Proxy_Add_Sibling_Inline");

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                        + "   <soapenv:Header />\n"
                        + "   <soapenv:Body>\n"
                        + "      <company>WSO2</company>\n"
                        + "      <first_name>Jay</first_name>\n"
                        + "      <last_name>Cleark</last_name>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <company>WSO2</company>\n"
                        + "      <first_name>Jay</first_name>\n"
                        + "      <last_name>Cleark</last_name>\n"
                        + "      <gender>M</gender>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, ScenarioConstants.MESSAGE_ID, expectedResponse, 200,
                "urn:mediate", "AddSiblingInline");
    }

    @AfterClass(description = "Server Cleanup",
                alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}

