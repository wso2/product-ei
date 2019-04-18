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
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

/**
 * This test class is to test SOAP to JSON Transformation using Payload Factory Mediator. Once a SOAP request is sent
 * to API and it will transform the SOAP Request to JSON using Payload Factory Mediator and send to backend
 * service. Backend Service will respond with JSON response and API will transform tha JSON Response to SOAP.
 */
public class SoapToJsonUsingPayloadFactoryTest extends ScenarioTestBase {

    private String proxyServiceName = "1_1_1_Proxy_soap_to_json_using_payload_factory";
    private String proxyServiceUrl;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceUrl = getProxyServiceURLHttp(proxyServiceName);
        log.info("proxyServiceUrl is set as : " + proxyServiceUrl);
    }

    @Test(description = "1.1.1.1")
    public void convertValidSoapToJsonUsingPayloadFactory() throws Exception {
        String request ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                        "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' " +
                        "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
                        "xmlns:s='http://www.w3.org/2001/XMLSchema'>\n" +
                        "<SOAP-ENV:Body>\n" +
                        "    <LookupCityRequest xmlns=\"http://tempuri.org\">\n" +
                        "        <LookupCity>\n" +
                        "            <Zip>60601</Zip>\n" +
                        "        </LookupCity>\n" +
                        "    </LookupCityRequest>\n" +
                        "</SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";

        String expectedResponse = "<?xml version='1.0' encoding='UTF-8'?>" +
                                  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                                  "<soapenv:Body><LookupCityResult xmlns=\"http://tempuri.org\"><LookupCity>" +
                                  "<City>Chicago</City><State>IL</State><Zip>60601</Zip></LookupCity>" +
                                  "</LookupCityResult></soapenv:Body></soapenv:Envelope>";

        String header = "1_1_1_1_1";

        HTTPUtils.invokeSoapActionAndAssert(proxyServiceUrl, request, header, expectedResponse,
                                            200, "urn:mediate",
                                            "Valid Soap To Json transformation Using Payload Factory Mediator");
    }

    @Test(description = "1.1.1.2")
    public void convertMalformedSoapToJsonUsingPayloadfactory() throws Exception {
        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                         "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' " +
                         "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
                         "xmlns:s='http://www.w3.org/2001/XMLSchema'>\n" +
                         "   <SOAP-ENV:Body>\n" +
                         "      <LookupCityRequest xmlns=\"http://tempuri.org\">\n" +
                         "         <LookupCity>\n" +
                         "            <Zip>60601</Zip>\n" +
                         "         </LookupCity>\n" +
                         "   </SOAP-ENV:Body>\n" +
                         "</SOAP-ENV:Envelope>";

        String header = "1_1_1_2_1";

        String responseSubstring = "<faultstring>com.ctc.wstx.exc.WstxParsingException: " +
                               "Unexpected close tag &lt;/SOAP-ENV:Body>; expected &lt;/LookupCityRequest>." +
                               "</faultstring>";
        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, request, header, responseSubstring, 500,
                                                   "urn:mediate",
                                            "Malformed Soap to Json Transformation Using Payload Factory Mediator");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
