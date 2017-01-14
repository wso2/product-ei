/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.security.policy;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.Header;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.ConfigurationContextProvider;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class UTResponseCode401UTauthFailure extends ESBIntegrationTest {

    String requestPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://services.samples/xsd\" xmlns:ser=\"http://services.samples\"><soapenv:Header " +
            "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"> " +
            "<wsa:Action>urn:getQuote</wsa:Action><wsa:MessageID>uuid:d0d8c61d-b8c9-4493-ac8f-44de7245dc0f</wsa" +
            ":MessageID> </soapenv:Header><soapenv:Body>\n" +
            "      <ser:getQuote>\n" +
            "         <!--Optional:-->\n" +
            "         <ser:request>\n" +
            "            <!--Optional:-->\n" +
            "            <xsd:symbol>aaa</xsd:symbol>\n" +
            "         </ser:request>\n" +
            "      </ser:getQuote>\n" +
            "   </soapenv:Body></soapenv:Envelope>";
    OMElement payload = null;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/securityPolicy/sequence.xml");
        payload = AXIOMUtil.stringToOM(requestPayload);
    }

    @Test(groups = {"wso2.esb", "localOnly"}, description = "Test UT scenario without basic auth headers")
    public void testWithoutAuthHeaders() throws Exception {
        String addUrl = getProxyServiceURLHttps("Hello");
        try {
            axis2Client.clearHttpHeader();
            axis2Client.addHttpHeader("Content-type" , "text/xml;charset=UTF-8");
            OMElement response = axis2Client.send(addUrl, null, "getQuote", payload);
        } catch (AxisFault fault) {
            assertEquals("Transport error: 401 Error: Unauthorized", fault.getMessage());
        }
    }

    @Test(groups = {"wso2.esb", "localOnly"}, description = "Test UT scenario with correct basic auth headers")
    public void testWithAuthHeaders() throws Exception {
        String addUrl = getProxyServiceURLHttps("Hello");
        axis2Client.clearHttpHeader();
        axis2Client.addHttpHeader("Content-type" , "text/xml;charset=UTF-8");
        axis2Client.addHttpHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        try {
            axis2Client.addHttpHeader("Content-type" , "text/xml;charset=UTF-8");
            OMElement response = axis2Client.send(null, addUrl, "getQuote", payload);
        } catch (AxisFault fault) {
            assertNotEquals("Transport error: 401 Error: Unauthorized", fault.getMessage());
        }
    }

    @Test(groups = {"wso2.esb", "localOnly"}, description = "Test UT scenario with incorrect basic auth headers")
    public void testWithWrongCredentials() throws Exception {
        String addUrl = getProxyServiceURLHttps("Hello");
        axis2Client.clearHttpHeader();
        axis2Client.addHttpHeader("Authorization", "Basic YWRtaW46YddWRtaW4=");
        try {
            axis2Client.addHttpHeader("Content-type" , "text/xml;charset=UTF-8");
            OMElement response = axis2Client.send(null, addUrl, "getQuote", payload);
        } catch (AxisFault fault) {
            assertEquals("Transport error: 401 Error: Unauthorized", fault.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
