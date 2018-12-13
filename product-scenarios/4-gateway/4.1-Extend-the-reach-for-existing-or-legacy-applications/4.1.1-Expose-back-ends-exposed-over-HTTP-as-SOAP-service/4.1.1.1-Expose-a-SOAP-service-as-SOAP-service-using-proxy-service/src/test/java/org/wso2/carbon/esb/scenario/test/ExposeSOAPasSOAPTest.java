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
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        invokeSOAPProxyAndAssert(getProxyServiceURLHttp("4_1_1_1_Proxy_SimplePassThroughTemplate"));
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

    private void invokeSOAPProxyAndAssert(String url) throws IOException, XMLStreamException {
        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("SOAPAction","urn:getQuote");

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

        HttpResponse response =  httpClient.doPost(url, headers, payload, HTTPConstants.MEDIA_TYPE_TEXT_XML);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Response failed");

        OMElement respElement = AXIOMUtil.stringToOM(httpClient.getResponsePayload(response));
        OMElement symbolElement = respElement.getFirstElement().getFirstElement().getFirstElement().
                getFirstChildWithName(new QName("http://services.samples/xsd","symbol"));

        Assert.assertNotNull(symbolElement, "Invalid response");
        Assert.assertEquals(symbolElement.getText(), "WSO2");
    }
}
