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
import org.wso2.esb.integration.common.utils.ESBTestConstant;

/**
 * Sample 2: CBR with the Switch-Case Mediator Using Message Properties
 */
public class Sample2TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(2);

    }

    @Test(groups = { "wso2.esb" },
          description = "Sample 2: CBR with the Switch-case mediator, using message properties")
    public void testSample2() throws Exception {
        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "IBM");

        Assert.assertTrue(response.toString().contains("IBM"),
                          "Requested Symbol not found in Response");

        response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "MSFT");

        Assert.assertTrue(response.toString().contains("MSFT"),
                          "Requested Symbol not found in Response");

        response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");

        Assert.assertTrue(response.toString().contains("WSO2"),
                          "Requested Symbol not found in Response");

        String logMessage1 = "symbol = Great stock - IBM";
        String logMessage2 = "symbol = Are you sure? - MSFT";
        String logMessage3 = "symbol = Normal Stock - WSO2";

        boolean logMessage1Found = false, logMessage2Found = false, logMessage3Found = false;
        LogEvent[] logEvents = logViewerClient.getAllSystemLogs();
        for (LogEvent event : logEvents) {
            if (!logMessage1Found && event.getMessage().contains(logMessage1)) {
                logMessage1Found = true;
            }

            if (!logMessage2Found && event.getMessage().contains(logMessage2)) {
                logMessage2Found = true;
            }

            if (!logMessage3Found && event.getMessage().contains(logMessage3)) {
                logMessage3Found = true;
            }

            if (logMessage1Found && logMessage2Found && logMessage3Found) {
                break;
            }
        }

        Assert.assertTrue(logMessage1Found, "Log message not found - " + logMessage1);
        Assert.assertTrue(logMessage2Found, "Log message not found - " + logMessage2);
        Assert.assertTrue(logMessage3Found, "Log message not found - " + logMessage3);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
