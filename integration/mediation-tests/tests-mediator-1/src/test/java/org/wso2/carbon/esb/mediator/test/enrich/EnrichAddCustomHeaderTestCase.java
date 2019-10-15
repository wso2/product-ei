/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.enrich;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SecureServiceClient;

import java.rmi.RemoteException;

/**
 * This test case verifies adding custom header with enrich mediator.
 */
public class EnrichAddCustomHeaderTestCase extends ESBIntegrationTest {
    private SecureServiceClient secureAxisServiceClient;
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config14/custom_header_add.xml");
        secureAxisServiceClient = new SecureServiceClient();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

    }

    @Test(groups = "wso2.esb", description = "This verifies adding custom header with enrich mediator")
    public void testSecuredProxySecuredService() throws Exception {

        logViewerClient.clearLogs();
        applySecurity(); //only https available

        String response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps
                ("StockQuoteProxy"), 1, "WSO2").toString();

        Assert.assertTrue(response.contains("<result xmlns=\"http://ws.apache.org/ns/synapse\">true</result>"),
                "Payload mismatched");
        boolean isCustomHeaderAvailable = isPropertyContainedInLog("<urn:userName>foo</urn:userName>");
        Assert.assertTrue(isCustomHeaderAvailable, "Header mismatched");

    }

    /**
     * This method check whether given property contains in the logs.
     *
     * @param property required property which needs to be validate if exists or not.
     * @return A Boolean
     */
    private boolean isPropertyContainedInLog(String property) throws RemoteException {
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean containsProperty = false;
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(property)) {
                containsProperty = true;
                break;
            }
        }
        return containsProperty;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private void applySecurity() throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
            InterruptedException {
        applySecurity("StockQuoteProxy", 1, getUserRole());
    }
}
