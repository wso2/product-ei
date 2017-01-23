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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.ConnectionData;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Sample 12: One-Way Messaging in a Fire-and-Forget Mode through ESB
 */
public class Sample12TestCase extends ESBIntegrationTest {

    private TCPMonListener listener1;
    private TCPMonListener listener2;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(12);
        //let server to persist the file
        Thread.sleep(3000);
        SynapseConfigAdminClient client =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        String oldConfig = client.getConfiguration();
        oldConfig = oldConfig.replace("localhost:9000", "localhost:9001");
        client.updateConfiguration(oldConfig);
        Thread.sleep(3000);

        listener1 = new TCPMonListener(8481, "localhost", 8480);
        listener1.start();

        listener2 = new TCPMonListener(9001, "localhost", 9000);
        listener2.start();
    }

    @Test(groups = { "wso2.esb" }, description = "One-Way Messaging in a Fire-and-Forget Mode" +
                                                 " through ESB")
    public void testOneWayMessaging() throws Exception {

        axis2Client.sendPlaceOrderRequest("http://localhost:8481", null, null);
        Thread.sleep(5000);

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
}
