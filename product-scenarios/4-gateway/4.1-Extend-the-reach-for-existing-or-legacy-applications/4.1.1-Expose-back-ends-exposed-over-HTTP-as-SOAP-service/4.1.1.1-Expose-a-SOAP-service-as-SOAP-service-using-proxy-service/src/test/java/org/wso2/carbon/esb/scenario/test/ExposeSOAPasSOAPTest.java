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

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;
import org.wso2.carbon.esb.scenario.test.common.http.SOAPClient;

import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * This test class test exposing a SOAP service as SOAP service using proxy service
 */
public class ExposeSOAPasSOAPTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(ExposeSOAPasSOAPTest.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "4.1.1.1.1 Front the back-end using pass-through proxy template")
    public void testPassThroughProxyTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_1_Proxy_SimplePassThroughTemplate"));
    }

    @Test(description = "4.1.1.1.2 Front the back-end using custom proxy template")
    public void testCustomProxyTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_2_Proxy_SimpleCustomTemplate"));
    }

    @Test(description = "4.1.1.1.3 Front the back-end using Log Forward proxy template")
    public void testLogAndForwardTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_3_Proxy_SimpleLogForwardTemplate"));
    }

    @Test(description = "4.1.1.1.4 Front the back-end using WSDL-Based proxy proxy template")
    public void testWSDLTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_4_Proxy_SimpleWSDLProxyTemplate"));
    }

    @Test(description = "4.1.1.1.5 Front the back-end using proxy specifying service URL using named endpoint within " +
            "endpoint tag")
    public void testPassThroughWithNamedEP() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_5_Proxy_PassThroughWithNamedEP"));
    }

    @Test(description = "4.1.1.1.6 Front the back-end using specifying service URL using named endpoint via \"endpoint\" " +
            "attribute in target tag")
    public void testPassThroughEPinTarget() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_6_Proxy_PassThroughProxyEPinTarget"));
    }


    @Test(description = "4.1.1.1.7 Publishing WSDL of the service by loading from the registry")
    public void testPublishingWSDLFromRegistry() throws IOException, XMLStreamException {
        String wsdlDoc = "SimpleStockQuoteService";
        String url = getProxyServiceURLHttp("4_1_1_1_7_Proxy_PublishWSDLFromRegistry");

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        OMElement responseOM = HTTPUtils.getOMFromResponse(response);

        Assert.assertEquals(responseOM.getFirstChildWithName(new QName("http://schemas.xmlsoap.org/wsdl/",
                "documentation")).getText(), wsdlDoc, "WSDL from the proxy differ from expected");

    }

    @Test(description = "4.1.1.1.8 Publishing WSDL of the service by loading from uri")
    public void testPublishingWSDLFromURL() throws IOException, XMLStreamException {
        String wsdlDoc = "SimpleStockQuoteService";
        String url = getProxyServiceURLHttp("4_1_1_1_8_Proxy_PublishWSDLFromURL");

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        OMElement responseOM = HTTPUtils.getOMFromResponse(response);

        Assert.assertEquals(responseOM.getFirstChildWithName(new QName("http://schemas.xmlsoap.org/wsdl/",
                "documentation")).getText(), wsdlDoc, "WSDL from the proxy differ from expected");

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

    private void invokeSOAPProxyAndAssert(String url) throws IOException, XMLStreamException {
        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:getQuote>\n" +
                "         <ser:request>\n" +
                "            <xsd:symbol>WSO2</xsd:symbol>\n" +
                "         </ser:request>\n" +
                "      </ser:getQuote>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        SOAPClient soapClient = new SOAPClient();
        HttpResponse response = soapClient.sendSimpleSOAPMessage(url,payload, "urn:getQuote");

        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement respElement = HTTPUtils.getOMFromResponse(response);
        OMElement symbolElement = respElement.getFirstElement().getFirstElement().getFirstElement().
                getFirstChildWithName(new QName("http://services.samples/xsd","symbol"));

        Assert.assertNotNull(symbolElement, "Invalid response");
        Assert.assertEquals(symbolElement.getText(), "WSO2");
    }
}
