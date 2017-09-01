/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.mediator.test.call;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4469
 * This class will make sure that call mediator submit the request body to backend when
 * OUT_ONLY is true
 */

public class ESBJAVA4469CallMediatorWithOutOnlyTestCase extends ESBIntegrationTest {

    private String messageBody;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/call/ESBJAVA4469.xml");
        messageBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                             "xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                             "   <soapenv:Header/>\n" +
                             "   <soapenv:Body>\n" +
                             "      <ser:placeOrder>\n" +
                             "         <ser:order>\n" +
                             "            <xsd:price>10</xsd:price>\n" +
                             "            <xsd:quantity>20</xsd:quantity>\n" +
                             "            <xsd:symbol>WSO2</xsd:symbol>\n" +
                             "         </ser:order>\n" +
                             "      </ser:placeOrder>\n" +
                             "   </soapenv:Body>\n" +
                             "</soapenv:Envelope>";

    }

    @Test(groups = {"wso2.esb"})
    public void outOnlyWithoutContentAwareMediatorTest() throws Exception {
        WireMonitorServer wireMonitorServer = new WireMonitorServer(3828);
        Map<String, String> headers = new HashMap<>();

        wireMonitorServer.start();

        headers.put(HttpHeaders.CONTENT_TYPE, "text/xml");
        headers.put(HTTPConstants.HEADER_SOAP_ACTION, "urn:placeOrder");

        HttpResponse response = HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp("ESBJAVA4469"))
                , messageBody, headers);

        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED, "Response code should be 202");
        String outGoingMessage = wireMonitorServer.getCapturedMessage();
        Assert.assertTrue(outGoingMessage.contains(">WSO2<")
                , "Outgoing message is empty or invalid content " + outGoingMessage);

    }

    @Test(groups = {"wso2.esb"})
    public void outOnlyWithContentAwareMediatorTest() throws Exception {
        WireMonitorServer wireMonitorServer = new WireMonitorServer(3829);
        Map<String, String> headers = new HashMap<>();

        wireMonitorServer.start();

        headers.put(HttpHeaders.CONTENT_TYPE, "text/xml");
        headers.put(HTTPConstants.HEADER_SOAP_ACTION, "urn:placeOrder");

        HttpResponse response = HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp("ESBJAVA4469WithLogMediator"))
                , messageBody, headers);

        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED, "Response code should be 202");
        String outGoingMessage = wireMonitorServer.getCapturedMessage();
        Assert.assertTrue(outGoingMessage.contains(">WSO2<")
                , "Outgoing message is empty or invalid content " + outGoingMessage);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
