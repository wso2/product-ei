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
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import static org.testng.Assert.*;

/**
 * Sample 4: Specifying a Fault Sequence with a Regular Mediation Sequence
 */
public class Sample4TestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(4);

        logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

    }

    @Test(groups = { "wso2.esb" }, description = "Sample 4: Introduction to error handling.")
    public void testErrorHandling() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
            "IBM");

        assertTrue(response.toString().contains("IBM"), "IBM not found");

        logViewerClient.clearLogs();
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "MSFT");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            assertEquals(expected.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL,
                         "Error message not contain message > The input stream for an incoming " +
                         "message is null");
        }

        String logMessage =
            "text = An unexpected error occured, message = Couldn't find the endpoint with the " +
            "key : bogus";

        verifyLogMessages(logMessage);

        logViewerClient.clearLogs();
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "SUN");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            assertEquals(expected.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL,
                         "Error message not contain message > The input stream for an incoming " +
                         "message is null");
        }

        logMessage =
            "text = An unexpected error occured for stock SUN, message = Couldn't find the " +
            "endpoint with the key : sunPort";

        verifyLogMessages(logMessage);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private void verifyLogMessages(String logMessage) throws Exception {

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

}
