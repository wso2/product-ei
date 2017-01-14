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

package org.wso2.carbon.esb.nhttp.transport.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

public class OCSPCertificateValidationTestCase extends ESBIntegrationTest{

    private SimpleHttpClient httpClient;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        AutomationContext autoCtx = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(autoCtx);
        serverConfigurationManager.applyConfiguration(
                new File(getClass().getResource("/artifacts/ESB/nhttp/transport/axis2.xml").getPath()));
        super.init();
        httpClient = new SimpleHttpClient();
        loadESBConfigurationFromClasspath("/artifacts/ESB/nhttp/transport/certificatevalidation/simple_proxy.xml");
    }

    @Test(groups = {"wso2.esb"},
            description = "Sends https request to the backend with OCSP certificate validation " +
                    "enabled ", enabled = false)
    public void sendHTTPSRequest() throws Exception {

        //HttpResponse response = HttpRequestUtil.sendGetRequest(esbServer.getServiceUrl().replace("/services","") + "/slive/echo/WSO2", null);
        String epr = contextUrls.getServiceUrl().replace("/services","") + "/slive/echo/WSO2";
        HttpResponse response = httpClient.doGet(epr, null);
        System.out.println("This is the response");
        System.out.println(response.toString());
        Assert.assertTrue(response.toString().contains("WSO2"), "Asserting response for string 'WSO2'");
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        try {
            super.cleanup();
        } finally {
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }
}
