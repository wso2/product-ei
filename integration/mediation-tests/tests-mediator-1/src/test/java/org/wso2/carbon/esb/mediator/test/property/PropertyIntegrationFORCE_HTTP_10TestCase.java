/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

/**
 * This tests can be used to test the functionality of FORCE_HTTP_1.0 property
 */
public class PropertyIntegrationFORCE_HTTP_10TestCase extends ESBIntegrationTest {

    private TCPMonListener tcpMonListener;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        tcpMonListener = new TCPMonListener(9005, context.getDefaultInstance().getHosts().
                get("default"), 9000);

        tcpMonListener.start();
    }


    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        tcpMonListener.stop();
        super.cleanup();
    }


    @Test(groups = "wso2.esb", description = "Test-with FORCE_HTTP_1.0 Property enabled false " +
                                             "scenario")
    public void testWithForceHTTP10EnabledFalseTest() throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/FORCE_HTTP_1.0_DISABLED.xml");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("Axis2ProxyService"), null, "WSO2");

        assertTrue(response.toString().contains("WSO2 Company"));

        String esbOutGoingMsg = tcpMonListener.getConnectionData().get(1).
                getInputText().toString();

        assertTrue(esbOutGoingMsg.contains("HTTP/1.1"), "TCP mon incoming message" +
                                                        " should be in HTTP/1.1");
    }

    @Test(groups = "wso2.esb", description = "Test-with FORCE_HTTP_1.0 Property enabled true scenario"
            , dependsOnMethods = "testWithForceHTTP10EnabledFalseTest")
    public void testWithForceHTTP10EnabledTrueTest() throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/FORCE_HTTP_1.0_ENABLED.xml");

        tcpMonListener.clear();

        Thread.sleep(3000);

        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("Axis2ProxyService"), null, "WSO2");

        assertTrue(response.toString().contains("WSO2 Company"));

        String esbOutGoingMsg = tcpMonListener.getConnectionData().get(1).
                getInputText().toString();

        assertTrue(esbOutGoingMsg.contains("HTTP/1.0"), "TCP mon incoming message " +
                                                        "should be in HTTP/1.0");
    }
}
