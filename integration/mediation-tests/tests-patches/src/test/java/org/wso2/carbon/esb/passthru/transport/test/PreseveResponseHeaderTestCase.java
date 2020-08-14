/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;


public class PreseveResponseHeaderTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator + "passthru" +
                                                               File.separator + "transport" + File.separator + "preseveResponseHeaders" + File.separator + "passthru-http.properties"));
        super.init();
        verifyAPIExistence("ResponseHeaderPreserveAPI");
    }
    @Test(groups = "wso2.esb", description = "Preserve response header test", enabled = true)
    public void testPreserveContentTypeHeader() throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(getApiInvocationURL("ResponseHeaderPreserveAPI"));
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
        } finally {
            httpclient.clearRequestInterceptors();
        }
        Assert.assertTrue("Did't receive 200 OK response", "HTTP/1.1 200 OK".equals(response.getStatusLine().toString()));
        Assert.assertTrue("Response header modified", "application/json".equals(response.getHeaders("Content-Type")[0].getValue()));

    }
}
