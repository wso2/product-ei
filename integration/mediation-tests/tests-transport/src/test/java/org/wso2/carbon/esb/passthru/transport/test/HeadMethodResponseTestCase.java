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


package org.wso2.carbon.esb.passthru.transport.test;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class HeadMethodResponseTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/HeadMethodResponseTestSynapse.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }


    @Test(groups = "wso2.esb", description = " Checking response for HEAD request contains a body")
    public void testResponseBodyOfHEADRequest() throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();
        httpClient.doGet(contextUrls.getServiceUrl() + "/ClientProxy", null);

        LogEvent[] logs = logViewer.getAllSystemLogs();
        boolean errorLogFound = false;
        for (LogEvent log : logs) {
            if (log.getMessage().contains("HTTP protocol violation")) {
                errorLogFound = true;
                break;
            }
        }
        assertFalse(errorLogFound, "HTTP protocol violation for Http HEAD request, " +
                "Response for HEAD request contains a body");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
