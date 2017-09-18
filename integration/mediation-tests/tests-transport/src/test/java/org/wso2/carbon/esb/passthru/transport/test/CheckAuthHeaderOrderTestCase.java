/*
 * Copyright (c)2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.File;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

/**
 * This test case is written to track the issue reported in
 * https://wso2.org/jira/browse/ESBJAVA-5121.This checks whether the order of
 * the auth headers is correct before sending the request to the endpoint
 */

public class CheckAuthHeaderOrderTestCase extends ESBIntegrationTest {

    public WireMonitorServer wireServer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        wireServer = new WireMonitorServer(8991);
        wireServer.start();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "nhttp" + File.separator + "transport" + File.separator
                                          + "auth-headers.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message Via REST to check the order of the auth headers")
    public void testAuthHeaderOrder() throws Exception {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(getApiInvocationURL("stockquote") + "/order/");
        httpPost.addHeader("WWW-Authenticate", "NTLM");
        httpPost.addHeader("WWW-Authenticate", "Basic realm=\"BasicSecurityFilterProvider\"");
        httpPost.addHeader("WWW-Authenticate", "ANTLM3");

        httpPost.setEntity(new StringEntity("<request/>"));
        httpClient.execute(httpPost);

        String response = wireServer.getCapturedMessage();

        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains(
                "WWW-Authenticate: NTLM\r\nWWW-Authenticate: Basic realm=\"BasicSecurityFilterProvider\"\r\nWWW-Authenticate: ANTLM3"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
