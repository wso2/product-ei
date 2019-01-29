/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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
public class AddSiblingUsingEnrichMediatorTest extends ScenarioTestBase {

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    //This test is to verify if payload can be modified by adding sibling elements inline to the payload.
    @Test(description = "1.6.2.1")
    public void addSiblingInline() throws IOException, XMLStreamException {
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
                "urn:mediate", "addSiblingInline");
    }

    //This test is to verify if payload can be modified by adding sibling elements defined by xpath to the payload.
    @Test(description = "1.6.2.2")
    public void addSiblingXpath() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_2_2_Proxy_Add_Sibling_xpath");
        String testcaseID = "1.6.2.2";
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                        + "   <soapenv:Header />\n"
                        + "   <soapenv:Body>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "      <ser:request>\n"
                        + "         <!--Optional:-->\n"
                        + "         <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "      </ser:request>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, testcaseID, expectedResponse, 200,
                "urn:mediate", "addSiblingXpath");
    }

  //This test is to verify if payload can be modified by adding current message body as the sibling to the resulting message body.
    @Test(description = "1.6.2.3")
    public void addSiblingToTargetBody() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_2_3_Proxy_Add_Sibling_toTargetBody");

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                        + "   <soapenv:Header />\n"
                        + "   <soapenv:Body>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, ScenarioConstants.MESSAGE_ID, expectedResponse, 200,
                "urn:mediate", "addSiblingToTargetBody");
    }

    //This test is to verify if payload can be modified by adding an element to the payload as a sibling which is stored in a property.
    @Test(description = "1.6.2.4")
    public void addSiblingMessageBodyStoredInProperty() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_2_4_Proxy_Add_Sibling_toMessageBodyStoredInProperty");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                        + "   <soapenv:Header />\n"
                        + "   <soapenv:Body>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <ser:getQuote>\n"
                        + "         <!--Optional:-->\n"
                        + "         <ser:request>\n"
                        + "            <!--Optional:-->\n"
                        + "            <xsd:symbol>WSO2</xsd:symbol>\n"
                        + "            <productid>IC002</productid>\n"
                        + "         </ser:request>\n"
                        + "      </ser:getQuote>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, ScenarioConstants.MESSAGE_ID, expectedResponse, 200,
                "urn:mediate", "addSiblingMessageBodyStoredInProperty");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}

