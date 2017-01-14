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

package org.wso2.carbon.esb.endpoint.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

public class ESBJAVA3340QueryParamHttpEndpointTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "rest"
                + File.separator + "query_params_api.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message Via REST to test query param works with space character", enabled = true)
    public void testPassParamsToEndpoint() throws Exception {
        String requestString = "/context?queryParam=some%20value";
        boolean isSpaceCharacterEscaped = false;
        logViewerClient.clearLogs();

        try {
            HttpRequestUtil.sendGetRequest(getApiInvocationURL("test") + requestString, null);
        } catch (Exception timeout) {
            //a timeout is expected
        }

        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("queryParam = some%20value")) {
                isSpaceCharacterEscaped = true;
                break;
            }
        }

        Assert.assertTrue(isSpaceCharacterEscaped,
                "Fail to send a message via REST when query parameter consist of space character");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
