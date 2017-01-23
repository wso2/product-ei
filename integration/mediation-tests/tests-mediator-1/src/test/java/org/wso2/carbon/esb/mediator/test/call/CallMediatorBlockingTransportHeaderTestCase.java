/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.call;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.File;

/**
 * This test case is written to track the issue reported in
 * https://wso2.org/jira/browse/ESBJAVA-2671 With this test case it will load a
 * proxy configuration which send messages to an endpoint using call out
 * mediatior. With that there is a transport header set and test check whether
 * it gets added to the request send to the endpoint.
 */

public class CallMediatorBlockingTransportHeaderTestCase extends ESBIntegrationTest {
    public WireMonitorServer wireServer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        wireServer = new WireMonitorServer(8991);
        wireServer.start();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig" + File.separator + "call" + File.separator + "CallMediatorBlockingTransportHeader.xml");
    }

    @Test(groups = "wso2.esb", description = "Transport header is set in request for soap 1.1")
    public void testContentTypeSoap11() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    getProxyServiceURLHttp("SimpleStockQuote"), null,
                    "transport_header_test");
        } catch (Exception e) {

        }
        String response = wireServer.getCapturedMessage();
        Assert.assertNotNull(response);
        Assert.assertTrue(response
                .contains("Authorization: Basic cHVubmFkaTpwYXNzd29yZA=="));
    }

    @Test(groups = "wso2.esb", description = "Transport header is set in request for soap 1.2")
    public void testContentTypeSoap12() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    getProxyServiceURLHttp("SimpleStockQuote"), null,
                    "transport_header_test");
        } catch (Exception e) {

        }
        String response = wireServer.getCapturedMessage();
        Assert.assertNotNull(response);
        Assert.assertTrue(response
                .contains("Authorization: Basic cHVubmFkaTpwYXNzd29yZA=="));
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
