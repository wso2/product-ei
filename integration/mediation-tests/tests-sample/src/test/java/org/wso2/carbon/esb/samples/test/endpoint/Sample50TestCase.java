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
package org.wso2.carbon.esb.samples.test.endpoint;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.ConnectionData;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Sample 50: POX to SOAP conversion
 */
public class Sample50TestCase extends ESBIntegrationTest {
    private TCPMonListener listener;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(50);

        listener = new TCPMonListener(8281, "localhost", 8280);
        listener.start();
    }

    @Test(enabled = false, groups = { "wso2.esb" }, description = "POX to SOAP conversion")
    public void testPoxToSaopConversion() throws Exception {
        OMElement response =
            axis2Client.sendSimpleStockQuoteRequestREST(
                "http://localhost:8281/services/StockQuote", null, "WSO2");

        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.toString().contains("GetQuoteResponse"),
                          "GetQuoteResponse not found");

        boolean verificationPass = false;
        for (ConnectionData connectionData : listener.getConnectionData().values()) {
            Assert.assertFalse(
                connectionData.getInputText().toString().contains("soapenv:Envelope"),
                "soapenv found");
            Assert.assertTrue(connectionData.getInputText().toString().contains("getQuote"),
                              "getQuote not found");
            verificationPass = true;
        }

        Assert.assertTrue(verificationPass, "Verification failed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

        listener.stop();
    }
}


