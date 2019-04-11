/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.SimpleSocketServer;

import java.util.HashMap;
import java.util.Map;

/**
 * A response with a custom status description should not be replaced by default description.

 */
public class ESBJAVA4423CustomStatusDescriptionTest extends ESBIntegrationTest {

    private SimpleSocketServer simpleSocketServer;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        verifyProxyServiceExistence("ESBJAVA4423HttpCustomProxyTest");
    }

    @Test(groups = "wso2.esb", description = "Test custom status description", enabled = true)
    public void testCustomStatusDescription() throws Exception {
        String expectedResponse = "HTTP/1.1 417 Custom response\r\nServer: testServer\r\n" +
                "Content-Type: text/xml; charset=UTF-8\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "\r\n" + "\"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<test></test>";
        //start socket server
        simpleSocketServer = new SimpleSocketServer(5389, expectedResponse);
        simpleSocketServer.start();

        String endpoint = getProxyServiceURLHttp("ESBJAVA4423HttpCustomProxyTest");
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "text/xml");

        HttpResponse httpResponse = HttpRequestUtil.doGet(endpoint, header);
        Assert.assertTrue("Could not find custom status description in response",
                httpResponse.getResponseMessage().contains("Custom response"));
    }

    @AfterClass
    public void cleanUp() throws Exception {
        if (simpleSocketServer != null) {
            simpleSocketServer.shutdown();
        }
        super.cleanup();
    }
}
