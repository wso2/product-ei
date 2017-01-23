/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.esb.mediators.callout;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.common.WireMonitorServer;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test Scenario: If there are headers different in case (uppercase and lowercase), Axis2 HTTPSender processes them as
 * separate headers. This causes problems when using blocking transport on ESB. This test case takes into account the
 * "SOAPAction" header and checks whether there are duplicates for the same.
 */
public class CARBON15119DuplicateSOAPActionHeader extends ESBIntegrationTest {

    WireMonitorServer wireMonitorServer;

    @BeforeTest(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        wireMonitorServer = new WireMonitorServer(6769);
        wireMonitorServer.start();

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/callout/DuplicateSOAPActionHeader.xml");
    }

    @Test(groups = "wso2.esb", description = "Test to check whether there are duplicate SOAPAction headers in the request to the service from callout mediator")
    public void testCheckForDuplicateSOAPActionHeaders() throws Exception {

        String proxyServiceUrl = getProxyServiceURLHttp("DuplicateSOAPActionHeader");

        String requestPayload = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' >"
                + "<soapenv:Body xmlns:ser='http://services.samples' xmlns:xsd='http://services.samples/xsd'> "
                + "<ser:getQuote> <ser:request> <xsd:symbol>WSO2</xsd:symbol> </ser:request> </ser:getQuote> "
                + "</soapenv:Body></soapenv:Envelope> ";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Soapaction", "urn:getQuote");

        HttpRequestUtil.doPost(new URL(proxyServiceUrl), requestPayload, headers);
        String capturedMsg = wireMonitorServer.getCapturedMessage();
	    Assert.assertFalse(capturedMsg.contains("Soapaction"));
	    Assert.assertTrue(capturedMsg.contains("SOAPAction"));
    }

    @AfterTest(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
