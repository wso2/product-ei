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

package org.wso2.carbon.esb.mediator.test.callTemplate;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class CallTemplateIntegrationParamsWithValuesTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/call_template/synapse_param_with_values.xml");
    }

    @Test(groups = { "wso2.esb" },
          description = "Call Template Mediator Sample Parameters with" + " values assigned test")
    public void testTemplatesParameter() throws Exception {
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "IBM");
        boolean requestLog = false;
        boolean responseLog = false;
        long startTime = System.currentTimeMillis();
        while (startTime + 30000 > System.currentTimeMillis()) {
            LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
            for (LogEvent log : logs) {
                if (log.getMessage().contains("REQUEST PARAM VALUE")) {
                    requestLog = true;
                    continue;
                } else if(log.getMessage().contains("RESPONSE PARAM VALUE")) {
                    responseLog = true;
                    continue;

                }
            }
            if(requestLog && requestLog) {
                break;
            }
        }
        Assert.assertTrue((requestLog && responseLog), "Relevant log not found in carbon logs");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
