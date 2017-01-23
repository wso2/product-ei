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
package org.wso2.carbon.esb.samples.test.proxy;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.ConnectionData;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Sample 155: Dual Channel Invocation on Both Client Side and Server Side of Synapse with
 * Proxy Services
 */
public class Sample155TestCase extends ESBIntegrationTest {

    private TCPMonListener listener1;
    private TCPMonListener listener2;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        updateESBConfiguration(loadAndEditSample(155));

        listener1 = new TCPMonListener(8481, "localhost", 8480);
        listener1.start();

        listener2 = new TCPMonListener(9001, "localhost", 9000);
        listener2.start();
    }

    @Test(groups = { "wso2.esb" }, description = "Dual Channel Invocation on Both Client Side " +
                                                 "and Server Side of Synapse with Proxy Services")
    public void testDualChannelInvocation() throws Exception {

        axis2Client.sendDualQuoteRequest(null, "http://localhost:8481/services/StockQuoteProxy",
                                         "WSO2");
        Thread.sleep(10000);

        boolean foundHTTP202Accepted = false;
        boolean getQuoteResponseNotFound = false;
        for (ConnectionData connection : listener1.getConnectionData().values()) {
            foundHTTP202Accepted =
                connection.getOutputText().toString().contains("HTTP/1.1 202 Accepted");
            getQuoteResponseNotFound =
                !connection.getOutputText().toString().contains("getQuoteResponse");
        }
        Assert.assertTrue(getQuoteResponseNotFound, "getQuoteResponse found");
        Assert.assertTrue(foundHTTP202Accepted,
                          "SimpleStockQuoteService to ESB reply 'HTTP 202 Accepted' not found");
        boolean foundHTTP202OK = false;
        getQuoteResponseNotFound = false;
        for (ConnectionData connection : listener2.getConnectionData().values()) {
            foundHTTP202OK = connection.getOutputText().toString().contains("HTTP/1.1 202 OK");
            getQuoteResponseNotFound =
                !connection.getOutputText().toString().contains("getQuoteResponse");
        }

        Assert.assertTrue(getQuoteResponseNotFound, "getQuoteResponse found");
        Assert.assertTrue(foundHTTP202OK, "ESB to client reply 'HTTP 202 OK' not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        listener1.stop();
        listener2.stop();
    }

    /**
     * This method is to update the original Sample 155 configuration end point port 9000 to 9001 in order to run
     * with TCPMonListener
     */
    private OMElement loadAndEditSample(int sampleNo) throws Exception {
        OMElement synapseConfig = loadSampleESBConfigurationWithoutApply(sampleNo);
        String updatedConfig = synapseConfig.toString().replace("localhost:9000", "localhost:9001");
        synapseConfig = AXIOMUtil.stringToOM(updatedConfig);
        return synapseConfig;
    }
}
