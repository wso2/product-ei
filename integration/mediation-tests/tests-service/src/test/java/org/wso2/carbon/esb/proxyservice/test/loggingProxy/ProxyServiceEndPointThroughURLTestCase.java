/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.proxyservice.test.loggingProxy;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ProxyServiceEndPointThroughURLTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/loggingProxy/proxy_service_with_end_point_through_url.xml");
        isProxyDeployed("StockQuoteLoggingProxy");

    }

    @Test(groups = "wso2.esb", description = "Proxy service with providing endpoint through url")
    public void testLoggingProxy() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("StockQuoteLoggingProxy"), null, "WSO2");

        String lastPrice = response.getFirstElement().
                getFirstChildWithName(new QName("http://services.samples/xsd", "last")).getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().
                getFirstChildWithName(new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");
    }

    @Test(groups = "wso2.esb", description = "VerifyLogs")
    public void testVerifyLogs() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(context.getContextUrls().getBackEndUrl(),
                getSessionCookie());
        assertNotNull(logViewerClient.getRemoteLogs("INFO", "getQuote", "", ""),
                      "Request INFO log entry not found");
        assertNotNull(logViewerClient.getRemoteLogs("INFO", "<ns:symbol>WSO2</ns:symbol>", "", ""),
                      "Request INFO log entry not found");
        assertNotNull(logViewerClient.getRemoteLogs("INFO", "ns:getQuoteResponse", "", ""),
                      "Request INFO log entry not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
