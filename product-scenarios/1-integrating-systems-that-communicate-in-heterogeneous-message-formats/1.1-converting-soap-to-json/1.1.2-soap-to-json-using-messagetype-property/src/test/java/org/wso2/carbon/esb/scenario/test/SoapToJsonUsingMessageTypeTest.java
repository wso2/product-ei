/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.scenario.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;

import java.util.Iterator;

/**
 * This test class is to test SOAP to JSON Transformation using MessageType Property. Once a SOAP request is sent to
 * proxy service, the SOAP message will be transformed to JSON using MessageType Property and send the message to
 * backend service. The backend service will send back a JSON response and it will be transformed to SOAP format.
 */

public class SoapToJsonUsingMessageTypeTest extends ScenarioTestBase {

    private String proxyServiceName = "1_1_2_Proxy_soap_to_json_using_message_type";
    private String proxyServiceUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = getProxyServiceURLHttp(proxyServiceName);
    }

    @Test(description = "1.1.2.1")
    public void convertValidSoapToJsonUsingMessageType() throws Exception {

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                         "    <soapenv:Body>\n" +
                         "        <getQuote xmlns=\"http://services.samples\">\n" +
                         "            <request>\n" +
                         "                <symbol>WSO2</symbol>\n" +
                         "            </request>\n" +
                         "        </getQuote>\n" +
                         "    </soapenv:Body>\n" +
                         "</soapenv:Envelope>";

        String expectedResponse = "<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope " +
                                  "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
                                  "<jsonObject><getQuoteResponse><return><change>3.961306710115824</change>" +
                                  "<earnings>-9.474775008224704</earnings><high>157.6906144430487</high>" +
                                  "<last>152.24464430136305</last>" +
                                  "<lastTradeTimestamp>Fri Nov 30 14:05:46 IST 2018</lastTradeTimestamp>" +
                                  "<low>158.97317787701516</low><marketCap>5.498486260437736E7</marketCap>"+
                                  "<name>WSO2 Company</name><open>-150.4303028558359</open>" +
                                  "<peRatio>25.43149084018825</peRatio>" +
                                  "<percentageChange>-2.746033637119583</percentageChange>" +
                                  "<prevClose>-144.2555785394889</prevClose><symbol>WSO2</symbol>"+
                                  "<volume>15328</volume></return></getQuoteResponse></jsonObject></soapenv:Body>" +
                                  "</soapenv:Envelope>";

        String header = "1_1_2_1_1";

        HTTPUtils.invokeSoapActionAndAssert(proxyServiceUrl, request, header, expectedResponse,
                                            200, "urn:mediate",
                                            "Valid Soap To Json transformation Using MessageType property");
    }

    @Test(description = "1.1.2.2")
    public void convertMalformedSoapToJsonUsingMessageType() throws Exception {

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                         "   <soapenv:Body>\n" +
                         "      <getQuote xmlns=\"http://services.samples\">\n" +
                         "      <request>\n" +
                         "         <symbol>WSO2</symbol>\n" +
                         "      </request>\n" +
                         "   </soapenv:Body>\n" +
                         "</soapenv:Envelope>";

        String header = "1_1_2_2_1";

        String responseSubstring = "<faultstring>com.ctc.wstx.exc.WstxParsingException: Unexpected close tag " +
                               "&lt;/soapenv:Body>; expected &lt;/getQuote>.</faultstring>";

        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, request, header, responseSubstring,500,
                                                   "urn:mediate",
                                                   "Malformed Soap to Json Transformation Using MessageType property");
    }

    @Test(description = "1.1.2.3", enabled = false, dataProvider = "1.1.2.3")
    public void convertLargeSoapToJsonUsingMessageType(String request, String expectedResponse, String header)
            throws Exception {
        HTTPUtils.invokeSoapActionAndAssert(proxyServiceUrl, request, header, expectedResponse,
                                            200, "urn:mediate",
                                            "Large Soap to Json Using MessageType property");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

    @DataProvider(name = "1.1.2.3")
    public Iterator<Object[]> soapToJson_1_1_2_3() throws Exception {
        String testCase = "1.1.2.3";
        return getRequestResponseHeaderList(testCase).iterator();
    }
}
