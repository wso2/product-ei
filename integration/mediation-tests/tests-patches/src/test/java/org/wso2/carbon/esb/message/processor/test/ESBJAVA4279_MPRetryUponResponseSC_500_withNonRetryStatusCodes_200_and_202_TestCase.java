/*
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.message.processor.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The Message processor is configured with 200,202 as non retry status codes.
 * If the back end returns 500 SC, then the MP keeps on retrying till the max
 * delivery attempt is reached. This test case is used to verify that behavior.
 */
public class ESBJAVA4279_MPRetryUponResponseSC_500_withNonRetryStatusCodes_200_and_202_TestCase
        extends ESBIntegrationTest {
    private static final String PROXY_SERVICE_NAME = "NonRetrySCProxy";
    private static final String EXPECTED_ERROR_MESSAGE = "Message forwarding failed";
    private static final String EXPECTED_MP_DEACTIVATION_MSG =
            "Successfully deactivated the message processor [Processor1]";
    private static final int RETRY_COUNT = 4;
    private LogViewerClient logViewerClient;
    private static final String CLIENT_REQUEST = "client-request";

    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        activeMQServer.startJMSBroker();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/messageProcessorConfig/MessageProcessorRetryUpon_500_ResponseWith_200And_202As_Non_retry_SC.xml");
        isProxyDeployed(PROXY_SERVICE_NAME);
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" },
            description = "Test whether a Message Processor retries sending the message to the EP when the response status code is 500 and MP is configured with 200,202 as non-retry status codes.")
    public void testMPRetryUponHTTP_SC_500_response_with_200_And_202_AsNonRetrySCs() throws Exception {
        boolean isRetriedUpon_500_response = false;
        boolean isRetryCompleted = false;
        boolean isMpDeactivated = false;
        int retryAttempts = 0;
        clearLogsAndSendRequest(PROXY_SERVICE_NAME);

        // Wait till the log appears
        Thread.sleep(20000);

        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(EXPECTED_ERROR_MESSAGE)) {
                isRetriedUpon_500_response = true;
                retryAttempts++;
            } else if (message.contains(EXPECTED_MP_DEACTIVATION_MSG)) {
                isMpDeactivated = true;
            }
        }

        if (retryAttempts == RETRY_COUNT) {
            isRetryCompleted = true;
        }
        Assert.assertTrue(isRetriedUpon_500_response && isRetryCompleted && isMpDeactivated,
                          "MP does not retry sending the request upon receiving HTTP SC 500 response");
    }

    @Test(dependsOnMethods = { "testMPRetryUponHTTP_SC_500_response_with_200_And_202_AsNonRetrySCs" },
            description = "Ensure message processor retry with original message envelope "
                    + "https://github.com/wso2/product-ei/issues/4992")
    public void testRetryMessageEnvelope() throws Exception {

        String messageAtBackEnd = "message at 500 backend = " + CLIENT_REQUEST;
        int retiredWithResponseMsg;
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        retiredWithResponseMsg = (int) Arrays.stream(logs).map(LogEvent::getMessage).filter(
                message -> message.contains(messageAtBackEnd)).count();
        Assert.assertEquals(RETRY_COUNT, retiredWithResponseMsg,
                            "Message processor didn't retry with original message envelope.");
    }

    @Test(dependsOnMethods = { "testRetryMessageEnvelope" },
            description = "test message enveloped stored in fail msg store https://github.com/wso2/product-ei/issues/5072")
    public void testMsgStoredInFailMsgStore() throws Exception {

        clearLogsAndSendRequest("fail-message-store-test-proxy");
        String messageAfterFailOver = "message at 200 backend = " + CLIENT_REQUEST;

        // Wait till the log appears
        Thread.sleep(20000);

        boolean isOrginalMsgStoredInFailOverStore = false;
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(messageAfterFailOver)) {
                isOrginalMsgStoredInFailOverStore = true;
                break;
            }
        }
        Assert.assertTrue(isOrginalMsgStoredInFailOverStore,
                          "Original message envelope is not stored in fail over msg store.");
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
        activeMQServer.stopJMSBroker();
    }

    private void clearLogsAndSendRequest(String proxyName) throws Exception {

        logViewerClient.clearLogs();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/xml");
        String inputPayload = "<request>" + CLIENT_REQUEST + "</request>";
        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(proxyName)), inputPayload, headers);
    }
}
