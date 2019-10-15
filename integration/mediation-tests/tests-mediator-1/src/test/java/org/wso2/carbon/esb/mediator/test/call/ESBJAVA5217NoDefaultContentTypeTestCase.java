/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied. See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.wso2.carbon.esb.mediator.test.call;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * When we are sending GET requests through call mediator, it will add default content type. By using
 * "<property name="NoDefaultContentType" scope="axis2" value="true"/>", can avoid adding default content type
 * This test class will test removing default content type in call mediator using this property.
 */
public class ESBJAVA5217NoDefaultContentTypeTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        verifyProxyServiceExistence("CallMediatorNoDefaultContentTypeTestProxy");
        verifyProxyServiceExistence("CallMediatorReplyNoDefaultContentTypeTestProxy");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb",
          description = "Test to verify whether bocking enabled calls not going to set default content type")
    public void testSettingDefaultContentType() throws Exception {
        boolean isContentTypeAvailable = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil
                .sendGetRequest(getProxyServiceURLHttp("CallMediatorNoDefaultContentTypeTestProxy"),
                        null);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            if (logEvent.getPriority().equals("INFO")) {
                String message = logEvent.getMessage();
                if (message.contains("Default_ContentType_Test_ContentType = null")) {
                    isContentTypeAvailable = true;
                    break;
                }
            }
        }
        Assert.assertTrue(isContentTypeAvailable, "Call mediator set default content type for get a request");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
