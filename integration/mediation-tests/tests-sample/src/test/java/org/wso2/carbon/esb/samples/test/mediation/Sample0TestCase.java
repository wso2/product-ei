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
 * Sample 0: Introduction to ESB
 */
public class Sample0TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(0);

    }

    @Test(groups = { "wso2.esb" }, description = "Test log mediator")
    public void testSample0() throws Exception {
        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
            "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"),
                          "GetQuoteResponse not found");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "WSO2 Company not found");

        String requestLog = "<ns:symbol>WSO2</ns:symbol>";
        String responseLog = "<ax21:name>WSO2 Company</ax21:name>";

        boolean isRequestLogFound = false, isResponseLogFound = false;
        LogEvent[] logEvents = logViewerClient.getAllSystemLogs();
        for (LogEvent event : logEvents) {
            if (!isRequestLogFound && event.getMessage().contains(requestLog)) {
                isRequestLogFound = true;
            }

            if (!isResponseLogFound && event.getMessage().contains(responseLog)) {
                isResponseLogFound = true;
            }

            if (isRequestLogFound && isResponseLogFound) {
                break;
            }
        }

        Assert.assertTrue(isRequestLogFound, "Request log not found");
        Assert.assertTrue(isResponseLogFound, "Response log not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
