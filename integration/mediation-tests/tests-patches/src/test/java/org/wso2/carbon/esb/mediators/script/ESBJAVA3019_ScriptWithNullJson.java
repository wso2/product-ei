/*
* Copyright 2004,2014 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.esb.mediators.script;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;

import static org.testng.Assert.assertFalse;

public class ESBJAVA3019_ScriptWithNullJson extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
        verifyProxyServiceExistence("ScriptWithNullJsonValueTest");
    }

    @Test(groups = "wso2.esb", description = "Test for check http status code can be retrived form HTTP_SC")
    public void testScriptWithNullJsonValue() throws Exception {

        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ScriptWithNullJsonValueTest"), null, "IBM");
        } catch (Exception e) {
            // This is just to handle the read timeout error
        }

        boolean jsonNullErrorFound = false;

        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            if (logEvent.getMessage().contains("testNull")) {
                jsonNullErrorFound = true;
                break;
            }
        }

        assertFalse(jsonNullErrorFound);
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }


}
