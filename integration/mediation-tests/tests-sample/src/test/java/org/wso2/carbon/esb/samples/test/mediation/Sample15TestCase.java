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
package org.wso2.carbon.esb.samples.test.mediation;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Sample 15: Using the Enrich Mediator for Message Copying and Content Enrichment
 */
public class Sample15TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(15);
    }

    @Test(groups = "wso2.esb")
    public void testMessageCopyingAndContentEnrichment() throws Exception {

        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        OMElement response = axis2Client
            .sendSimpleStockQuoteRequest(getProxyServiceURLHttps("StockQuote"), null, "WSO2");

        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.toString().contains("GetQuoteResponse"),
                          "GetQuoteResponse not found");

        LogEvent logMessageEvent1 = null;
        LogEvent logMessageEvent2 = null;

        LogEvent[] logEvents = logViewerClient.getAllSystemLogs();
        for (LogEvent event : logEvents) {
            if (logMessageEvent1 == null &&
                event.getMessage()
                     .contains("Direction: response, Original Request Symbol = WSO2")) {
                logMessageEvent1 = event;
            }
            if (logMessageEvent2 == null &&
                event.getMessage().contains("Direction: response, Envelope:")) {
                logMessageEvent2 = event;
            }
            if (logMessageEvent1 != null && logMessageEvent2 != null) {
                break;
            }
        }

        Assert.assertNotNull(logMessageEvent1, "logMessageEvent1 is null");
        Assert.assertNotNull(logMessageEvent2, "logMessageEvent2 is null");

        Assert.assertTrue(
            logMessageEvent2.getMessage().contains("<soapenv:Header><ax21:lastTradeTimestamp"),
            "lastTradeTimestamp is not added as a soap header");
        Assert.assertTrue(logMessageEvent2.getMessage().contains("<ax21:symbol>MSFT</ax21:symbol>"),
                          " The original IBM request has not changed to MSFT");

        Assert.assertTrue(logMessageEvent1.getMessage().contains("Original Request Symbol = WSO2"),
                          "Original Request Symbol not found");
        Assert.assertTrue(logMessageEvent1.getMessage().contains("Request Payload = <ns:getQuote"),
                          "Original Request payload not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}