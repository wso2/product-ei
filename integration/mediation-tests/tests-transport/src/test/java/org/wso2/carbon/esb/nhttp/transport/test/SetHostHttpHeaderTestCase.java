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

package org.wso2.carbon.esb.nhttp.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This is an patch automation test .This tests whether the host http header for outgoing messages can
 * be set both with port or without port.
 */
public class SetHostHttpHeaderTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        AutomationContext autoCtx = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(autoCtx);
        serverConfigurationManager.applyConfiguration(
                new File(getClass().getResource("/artifacts/ESB/nhttp/transport/axis2.xml").getPath()));
        super.init();
    }

    @Test(groups = {"wso2.esb"}, description = "Creating Test Case tests REQUEST_HOST_HEADER property functionality. " +
            "This make sure that the header can be formatted without the port number")
    public void testSetHostHttpHeaderTestCase() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/http_transport/set_host_http_header.xml");
        OMElement response;
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("HttpHostHeaderSetProxy"),
                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        assertTrue(response.toString().contains("WSO2"));

    }

    /**
     * This tests whether the http host header can be set using the "REQUEST_HOST_HEADER" property, Where host can be
     * formatted with or without the port number.
     */
    @Test(groups = {"wso2.esb"}, description = "Creating Test Case tests REQUEST_HOST_HEADER property functionality. " +
            "This make sure that the header can be formatted with the port number too")
    public void testSetHostHttpHeaderWithPortTestCase() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/http_transport/set_host_http_header_with_port.xml");
        OMElement response;
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("HttpHostHeaderSetProxyWithPort")
                , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        assertTrue(response.toString().contains("WSO2"));

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        try {
            super.cleanup();
        } finally {
            serverConfigurationManager.restoreToLastConfiguration(true);
            serverConfigurationManager = null;
        }
    }
}

