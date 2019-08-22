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
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class PropertyIntegrationXPathExtensionTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;
    private Boolean status = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/XPATHEXTENSION.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "RESPONSETEnabledTrue scenario")
    public void testRESPONSETEnabledTrue() throws IOException {

        // before deployment of car app
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;
        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("Hello"), null, "WSO2");

        assertTrue(response.toString().contains("WSO2"));

        // after deployment of car app
        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            String msg = "Transport-scope = This scope is trasport, Operation-scope = " +
                         "This scope is operation, System-scope = /, Axis2Client-scope = null, " +
                         "Synapse-scope = This scope is synapse, Registry-scope =";
            if (logs[i].getMessage().contains(msg)) {
                status = true;
                break;
            }

        }

        assertTrue(status, "Expected output received");
    }
}



