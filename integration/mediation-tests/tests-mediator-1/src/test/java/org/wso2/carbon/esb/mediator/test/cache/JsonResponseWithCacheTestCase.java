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

package org.wso2.carbon.esb.mediator.test.cache;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Testcase for ei-691. Issue in processing JSON array (payload) with a single
 * element when cache mediator is used with JsonBuilder and JSONFormatter. Testcase will
 * 1. deploy an API returning required Json
 * 2. deploy an API calling above API including a cache mediator
 * 3. test when invoked for the second time (cache is used) Json response is received correctly
 */
public class JsonResponseWithCacheTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + "/mediatorconfig/cache/" +
                "axis2.xml"));
        super.init();
    }

    @Test(groups = "wso2.esb", description = "Test cache mediator with  Json response having a single element array", enabled = false)
    public void testJsonResponseWithCacheMediator() throws IOException, AutomationFrameworkException {

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "application/json");

        //will not be a cache hit
        HttpRequestUtil.doGet((getApiInvocationURL("cachingEnabledApi") + "/singleElementArrayBackend"), requestHeader);

        //will be a cache hit
        HttpResponse response = HttpRequestUtil.
                doGet((getApiInvocationURL("cachingEnabledApi") + "/singleElementArrayBackend"), requestHeader);

        //check if [] are preserved in response
        Assert.assertTrue(response.getData().contains("[ \"water\" ]"), "Expected response was not"
                + " received. Got " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        try {
            super.cleanup();
        } finally {
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }

}
