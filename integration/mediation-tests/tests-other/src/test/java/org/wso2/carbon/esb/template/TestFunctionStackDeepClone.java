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

package org.wso2.carbon.esb.template;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import static org.testng.Assert.assertTrue;

/**
 * This test case is for an intermittent issue. So passing the test case doesn't guarantee
 * that the fix is included in the pack.
 * Increase of number of iterations will increase the probability of reproducing the issue
 */
public class TestFunctionStackDeepClone extends ESBIntegrationTest {

    private LogViewerClient logViewer;

    private final int iterations = 10;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/template/functionStackDeepCloneTestSynapse.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = {"wso2.esb"}, priority = 1, description = "Test function stack is properly deep cloned")
    public void testFunctionStackDeepClone() throws Exception {

        for (int i = 0 ; i < iterations ; i++) {
            SimpleHttpClient httpClient = new SimpleHttpClient();
            // have to create SimpleHttpClient instance for each request as it doesn't handle concurrent requests properly

            httpClient.doGet(contextUrls.getServiceUrl() + "/CallerProxy", null);
        }

        //This sleep is added to wait till all the necessary logs are printed
        Thread.sleep(5000);

        LogEvent[] logs = logViewer.getAllSystemLogs();
        int call1Count = 0, call2Count = 0, call3Count = 0;

        for (LogEvent log : logs) {
            if (log.getMessage().contains("Call-1")) {
                call1Count++;
            } else if (log.getMessage().contains("Call-2")) {
                call2Count++;
            } else if (log.getMessage().contains("Call-3")) {
                call3Count++;
            }
        }
        // Within the template configuration same log statement is printed 3 times
        boolean isCallCountCorrect = (call1Count == 3 * iterations) &&
                (call2Count == 3 * iterations) && (call3Count == 3 * iterations);
        assertTrue(isCallCountCorrect, "Function Stack is not properly deep cloned");

    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
