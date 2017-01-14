/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.servlet.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.http.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.RequestInterceptor;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpServer;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


/**
 * This test case starts an ESB instance with the servlet transport and performs a SOAP to
 * POX conversion through it. It makes sure that POX messages are passed to backend service
 * properly. This test case verifies the fixes done for CARBON-5993.
 */
public class POXOverServletTransportTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private SimpleHttpServer httpServer;
    private TestRequestInterceptor interceptor;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                                                               + "synapseconfig" + File.separator + "servletTransport" + File.separator + "pox_servlet_transport_axis2.xml"));
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                                          + File.separator + "synapseconfig" + File.separator + "servletTransport" + File.separator + "soap_2_pox.xml");
        httpServer = new SimpleHttpServer(8098, new Properties());
        httpServer.start();
        Thread.sleep(5000);

        interceptor = new TestRequestInterceptor();
        httpServer.getRequestHandler().setInterceptor(interceptor);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests SOAP to POX Conversion" , enabled = false)
    public void testSoapToPOXConversion() throws IOException, InterruptedException {


        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("SOAP2POX"),
                null, "WSO2");
        log.info("Response received: " + response);
        Assert.assertEquals(interceptor.getLastRequestURI(), "/services/SimpleStockQuoteService");

    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        try {
            httpServer.stop();
        } catch (IOException e) {
            log.warn("Error while shutting down the HTTP server", e);
        }
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
        }


    }

    private class TestRequestInterceptor implements RequestInterceptor {

        private String lastRequestURI;

        public void requestReceived(HttpRequest request) {
            lastRequestURI = request.getRequestLine().getUri();
        }

        public String getLastRequestURI() {
            return lastRequestURI;
        }
    }

}
