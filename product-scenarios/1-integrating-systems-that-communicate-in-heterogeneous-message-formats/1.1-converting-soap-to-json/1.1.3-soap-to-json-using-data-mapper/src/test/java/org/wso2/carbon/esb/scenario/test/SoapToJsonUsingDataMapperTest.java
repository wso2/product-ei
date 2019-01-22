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
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

import java.util.Iterator;

/**
 * This test class is to test SOAP to JSON Transformation using Data Mapper Mediator. Once a SOAP request is sent to
 * proxy service, the SOAP message will be transformed to JSON using Data Mapper and send the message to
 * backend service. The backend service will send back a JSON response and it will be transformed to SOAP format.
 */

public class SoapToJsonUsingDataMapperTest extends ScenarioTestBase {

    private String proxyServiceName = "1_1_3_Proxy_soap_to_json_using_data_mapper";
    private String proxyServiceUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        // WSO2 ESB 4.9.0 does not support Datamapper mediator
        skipTestsForIncompatibleProductVersions(ScenarioConstants.VERSION_490);

        super.init();
        proxyServiceUrl = getProxyServiceURLHttp(proxyServiceName);
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);
    }

    @Test(description = "1.1.3.1")
    public void convertValidSoapToJsonUsingDataMapper() throws Exception {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                         "    <soapenv:Body>\n" +
                         "        <breakfast_menu>\n" +
                         "            <food>\n" +
                         "                <name>Berry-Berry Belgian Waffles</name>\n" +
                         "                <price>$8.95</price>\n" +
                         "                <description>Light Belgian waffles covered with an assortment of fresh " +
                         "berries and whipped cream</description>\n" +
                         "                <calories>900</calories>\n" +
                         "                <orgin>Belgian</orgin>\n" +
                         "                <veg>true</veg>\n" +
                         "            </food>\n" +
                         "        </breakfast_menu>\n" +
                         "    </soapenv:Body>\n" +
                         "</soapenv:Envelope>";

        String expectedResponse = "<?xml version='1.0' encoding='UTF-8'?>" +
                                  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                  "<soapenv:Body><breakfast_menu><food><name>Berry-Berry Belgian Waffles</name>" +
                                  "<price>$8.95</price><description>Light Belgian waffles covered with an assortment " +
                                  "of fresh berries and whipped cream</description><calories>900</calories>" +
                                  "<orgin>Belgian</orgin><veg>true</veg></food></breakfast_menu>" +
                                  "</soapenv:Body></soapenv:Envelope>";

        String header = "1_1_3_1_1";

        HTTPUtils.invokeSoapActionAndAssert(proxyServiceUrl, request, header, expectedResponse,
                                         200, "urn:mediate",
                                         "Valid Soap To Json transformation Using Data Mapper");
    }

    @Test(description = "1.1.3.2")
    public void convertMalformedValidSoapToJsonUsingDataMapper() throws Exception {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                         "    <soapenv:Body>\n" +
                         "        <breakfast_menu>\n" +
                         "            <food>\n" +
                         "            <name>Berry-Berry Belgian Waffles</name>\n" +
                         "            <price>$8.95</price>\n" +
                         "            <description>Light Belgian waffles covered with an assortment of fresh berries " +
                         "and whipped cream</description>\n" +
                         "            <calories>900</calories>\n" +
                         "            <orgin>Belgian</orgin>\n" +
                         "            <veg>true</veg>\n" +
                         "        </breakfast_menu>\n" +
                         "    </soapenv:Body>\n" +
                         "</soapenv:Envelope>";

        String header = "1_1_3_2_1";

        String responseSubstring = "<faultstring>Could not build full log message: com.ctc.wstx.exc.WstxParsingException:" +
                               " Unexpected close tag &lt;/breakfast_menu>; expected &lt;/food>.</faultstring>";
        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, request, header, responseSubstring, 500,
                                                   "urn:mediate",
                                                   "Malformed Soap To Json transformation Using Data Mapper");
    }

    @Test(description = "1.1.3.3", enabled = false, dataProvider = "1.1.3.3")
    public void convertLargeSoapToJsonUsingDataMapper(String request, String expectedResponse, String header)
            throws Exception {
        HTTPUtils.invokeSoapActionAndAssert(proxyServiceUrl, request, header, expectedResponse,
                                         200, "urn:mediate",
                                         "Large Soap To Json transformation Using Data Mapper");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

    @DataProvider(name = "1.1.3.3")
    public Iterator<Object[]> soapToJson_1_1_3_3() throws Exception {
        String testCase = "1.1.3.3";
        return getRequestResponseHeaderList(testCase).iterator();
    }

}
