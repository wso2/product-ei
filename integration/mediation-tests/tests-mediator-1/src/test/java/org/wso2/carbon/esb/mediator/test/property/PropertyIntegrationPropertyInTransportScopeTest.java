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
package org.wso2.carbon.esb.mediator.test.property;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import static org.testng.Assert.assertTrue;

/**
 * Property Mediator Property In Transport Scope Test
 */

public class PropertyIntegrationPropertyInTransportScopeTest extends ESBIntegrationTest {

    private WireMonitorServer wireServer;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        wireServer = new WireMonitorServer(8991);
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/transport_scope_property.xml");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test- Property in transport scope")
    public void testPropertyInTransportScope() throws Exception {

        wireServer.start();
        try {
            axis2Client.sendSimpleStockQuoteRequest
                    (getProxyServiceURLHttp("Axis2ProxyService"),
                     null,
                     "WSO2");
        } catch (Exception e) {
        }
        String response = wireServer.getCapturedMessage();
        assertTrue(response.contains("TransportProperty: testProperty"), "Property not set");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
