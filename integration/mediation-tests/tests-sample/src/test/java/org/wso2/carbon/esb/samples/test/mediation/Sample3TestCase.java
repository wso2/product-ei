/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * Sample 3: Local Registry Entry Definitions, Reusable Endpoints and Sequences
 */
public class Sample3TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception {
        super.init();
        loadSampleESBConfiguration(3);
    }

    @Test(groups = { "wso2.esb" },
          description = "Sample 3: Add a static value as an inline text entry With get-property " +
                        "expression, use that value inside property mediator to be included as " +
                        "a part of a log")
    public void testLocalValueInPropertyMediator() throws Exception {
        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"),
                          "GetQuoteResponse not found");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "WSO2 Company not found");

        String logMessage = "Text = Sending quote request, version = 0.1, direction = incoming";

        boolean logMessageFound = false;

        LogEvent[] logEvents = logViewerClient.getAllSystemLogs();
        for (LogEvent event : logEvents) {
            if (event.getMessage().contains(logMessage)) {
                logMessageFound = true;
                break;
            }
        }

        Assert.assertTrue(logMessageFound, "Log message not found - " + logMessage);

    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        super.cleanup();
    }

}
