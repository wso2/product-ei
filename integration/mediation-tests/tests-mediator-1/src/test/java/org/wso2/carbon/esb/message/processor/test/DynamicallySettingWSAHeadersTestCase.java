/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.message.processor.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

/**
 * Test case to test whether message processor is able to handle messages when their WSA headers are set dynamically.
 */
public class DynamicallySettingWSAHeadersTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Testing message Processor handling message when setting wsa headers dynamically")
    public void testForwardingWithInMemoryStore() throws Exception {

        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewer.clearLogs();
        Reader data = new StringReader("<request><element>Test</element></request>");
        Writer writer = new StringWriter();
        HttpURLConnectionClient
                .sendPostRequestAndReadResponse(data, new URL(getProxyServiceURLHttp("MessageProcessorWSATestProxy")),
                        writer, "application/xml");
        Assert.assertTrue(Utils.checkForLog(logViewer, "MessageProcessorWSAProxy Request Received", 20),
                "Message processor unable to handle the message!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}