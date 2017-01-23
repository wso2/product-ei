package org.wso2.carbon.esb.rest.test.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class APIHeadMethod extends ESBIntegrationTest {
    private LogViewerClient logViewerClient = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "rest"
                + File.separator + "headTest.xml");

    }

    @Test(groups = "wso2.esb", description = "API HTTP HEAD Method" )
    public void apiHTTPHeadMethodTest() throws Exception {
        String restURL = "http://localhost:8480/headTest";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpHead httpHead = new HttpHead(restURL);
        HttpResponse response = httpclient.execute(httpHead);

        Assert.assertTrue(stringExistsInLog("API_HIT"));

        // http head method should return a 200 OK
        assertTrue(response.getStatusLine().getStatusCode() == 200);
        // it should not contain a message body
        assertTrue(response.getEntity() == null);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    protected boolean stringExistsInLog(String string) throws Exception {
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean logFound = false;
        for (LogEvent item : logs) {
            if (item.getPriority().equals("INFO")) {
                String message = item.getMessage();
                if (message.contains(string)) {
                    logFound = true;
                    break;
                }
            }
        }

        return logFound;
    }
}
