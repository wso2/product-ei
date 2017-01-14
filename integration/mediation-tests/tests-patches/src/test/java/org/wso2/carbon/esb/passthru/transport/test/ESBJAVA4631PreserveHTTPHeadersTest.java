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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.File;

/**
 * Testcase for check whether ESB preserves the Content-Type header.
 */
public class ESBJAVA4631PreserveHTTPHeadersTest extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private WireMonitorServer wireServer;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        /*
           * If test run in external distributed deployment you need to copy
           * following configFiles/preserveContentType/passthru-http.properties resource
           */

        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator + "passthru" +
                File.separator + "transport" + File.separator + "preserveheaders" + File.separator + "passthru-http.properties"));
        super.init();
        wireServer = new WireMonitorServer(8992);
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/preserveheaders/ContentTypePreserveAPI.xml");
    }

    @Test(groups = "wso2.esb", description = "Preserve Content-Type header test", enabled = true)
    public void testPreserveContentTypeHeader() throws Exception {
        wireServer.start();

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getApiInvocationURL("ContentTypePreserveAPI"));

        StringEntity postingString = new StringEntity("{\"sampleJson\" : \"sampleValue\"}");
        httppost.setEntity(postingString);
        httppost.setHeader("Content-type", "application/json");

        try {
            httpclient.execute(httppost);
        } finally {
            httpclient.clearRequestInterceptors();
        }


        String wireResponse = wireServer.getCapturedMessage();
        String[] wireResponseLines = wireResponse.split(System.lineSeparator());
        boolean isContentTypePresent = false;
        for (String line : wireResponseLines) {
            if (line.contains("Content-Type")) {
                isContentTypePresent = true;
                //charset encoding is appended to content-type header even preserve the content-type header as it is
                //This checks charset encoding is appended or not
                Assert.assertFalse(line.contains(";"), "Content-Type header was modified - " + line);
            }
        }
        //coming to this line means content type header is in expected state, hence passing the test
        Assert.assertTrue(isContentTypePresent, "Content-Type header is not present in the ESB request");
    }

    @AfterClass
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
