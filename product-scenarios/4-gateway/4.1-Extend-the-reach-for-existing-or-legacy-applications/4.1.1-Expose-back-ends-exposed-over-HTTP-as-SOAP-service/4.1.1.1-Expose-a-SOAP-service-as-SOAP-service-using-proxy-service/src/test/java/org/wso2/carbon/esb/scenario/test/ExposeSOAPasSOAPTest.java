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

import org.apache.axiom.om.OMAttribute;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * This test class test exposing a SOAP service as SOAP service using proxy service
 */
public class ExposeSOAPasSOAPTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(ExposeSOAPasSOAPTest.class);
    private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

    @BeforeClass(description = "Test init")
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "4.1.1.1.1")
    public void testPassThroughProxyTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_1_Proxy_SimplePassThroughTemplate"));
    }

    @Test(description = "4.1.1.1.2")
    public void testCustomProxyTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_2_Proxy_SimpleCustomTemplate"));
    }

    @Test(description = "4.1.1.1.3")
    public void testLogAndForwardTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_3_Proxy_SimpleLogForwardTemplate"));
    }

    @Test(description = "4.1.1.1.4")
    public void testWSDLTemplate() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_4_Proxy_SimpleWSDLProxyTemplate"));
    }

    @Test(description = "4.1.1.1.5")
    public void testPassThroughWithNamedEP() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_5_Proxy_PassThroughWithNamedEP"));
    }

    @Test(description = "4.1.1.1.6")
    public void testPassThroughEPinTarget() throws IOException, XMLStreamException {
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_6_Proxy_PassThroughProxyEPinTarget"));
    }

    @Test(description = "4.1.1.1.7")
    public void testPublishingWSDLFromRegistry() throws IOException, XMLStreamException {
        String wsdlDoc = "SimpleStockQuoteService From Registry";
        String url = getProxyServiceURLHttp("4_1_1_1_7_Proxy_PublishWSDLFromRegistry");

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement responseOM = HTTPUtils.getOMFromResponse(response);
        Assert.assertEquals(responseOM.getFirstChildWithName(new QName(WSDL_NS, "documentation")).getText(),
                wsdlDoc, "WSDL from the proxy differ from expected");
    }

    @Test(description = "4.1.1.1.8")
    public void testPublishingWSDLFromURL() throws IOException, XMLStreamException {
        String wsdlDoc = "SimpleStockQuoteService";
        String url = getProxyServiceURLHttp("4_1_1_1_8_Proxy_PublishWSDLFromURL");

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement responseOM = HTTPUtils.getOMFromResponse(response);
        Assert.assertEquals(responseOM.getFirstChildWithName(new QName(WSDL_NS, "documentation")).getText(),
                wsdlDoc, "WSDL from the proxy differ from expected");
    }

    @Test(description = "4.1.1.1.10")
    public void testPublishingWSDLInLine() throws IOException, XMLStreamException {
        String wsdlDoc = "SimpleStockQuoteService InLine";
        String url = getProxyServiceURLHttp("4_1_1_1_10_Proxy_PublishWSDLInline");

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement responseOM = HTTPUtils.getOMFromResponse(response);
        Assert.assertEquals(responseOM.getFirstChildWithName(new QName(WSDL_NS, "documentation")).getText(),
                wsdlDoc, "WSDL from the proxy differ from expected");
    }

    @Test(description = "4.1.1.1.11")
    public void testPublishingWSDLWithResourcesFromReg() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("4_1_1_1_11_Proxy_PublishWSDLWithResource");

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement responseOM = HTTPUtils.getOMFromResponse(response);
        Iterator schemas = responseOM.getFirstChildWithName(new QName(WSDL_NS, "types")).
                getChildrenWithName(new QName("http://www.w3.org/2001/XMLSchema", "schema"));

        if (schemas != null) {
            String[] nsStrArray = {"http://services.samples", "http://services.samples/xsd"};
            List<String> nsArrList = new ArrayList<String>(Arrays.asList(nsStrArray));

            while (schemas.hasNext()) {
                OMElement schemaElement = (OMElement) schemas.next();
                OMAttribute targetNs = schemaElement.getAttribute(new QName("targetNamespace"));
                nsArrList.remove(targetNs.getAttributeValue());
            }

            Assert.assertTrue(nsArrList.isEmpty(), "Imported schemas not available");
            return;
        }
        Assert.fail("Unable to find schema elements in the WSDL");
    }

    @Test(description = "4.1.1.1.13")
    public void testPublishingWSDLUseOriginalwsdl() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("4_1_1_1_13_Proxy_UseOriginalWSDL");
        String originalServiceName = "SimpleStockQuoteService";

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement responseOM = HTTPUtils.getOMFromResponse(response);
        OMElement serviceElement = responseOM.getFirstChildWithName(new QName(WSDL_NS, "service"));
        Assert.assertNotNull(serviceElement, "Unable to extract service element from WSDL");

        OMAttribute serviceNameAttr = serviceElement.getAttribute(new QName("name"));
        Assert.assertNotNull(serviceNameAttr, "Unable to extract service name attribute");
        Assert.assertEquals(serviceNameAttr.getAttributeValue(), originalServiceName, "Original Service name not " +
                "available");
    }

    @Test(description = "4.1.1.1.14")
    public void testPublishingWSDLModifyUserWSDLPortAddres() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("4_1_1_1_14_Proxy_PublishWSDLModifyUserWSDLPortAddress");
        String backendURL = "http://ei-backend.scenarios.wso2.org:8080/axis2/services/SimpleStockQuoteService";

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(url + "?wsdl", null);
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");

        OMElement responseOM = HTTPUtils.getOMFromResponse(response);
        OMElement serviceElement = responseOM.getFirstChildWithName(new QName(WSDL_NS, "service"));
        Assert.assertNotNull(serviceElement, "Unable to extract service element from WSDL");

        OMElement portElement = serviceElement.getFirstChildWithName(new QName(WSDL_NS, "port"));
        Assert.assertNotNull(portElement, "Unable to extract port element from WSDL");

        if (portElement.getFirstElement() != null) {
            Assert.assertEquals(portElement.getFirstElement().getLocalName(), "address", "address not available under " +
                    "port in the WSDL");
            OMAttribute locationAttr = portElement.getFirstElement().getAttribute(new QName("location"));
            Assert.assertTrue(locationAttr.getAttributeValue().trim().startsWith(backendURL), "Service location " +
                    "modified by the EI");
            return;
        }
        Assert.fail("Expected \"address\" element not found under port tag");
    }

    @AfterClass(description = "Server Cleanup")
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
