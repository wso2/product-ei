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

package org.wso2.carbon.esb.rest.test.api;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * Related to Patch Automation  https://wso2.org/jira/browse/APIMANAGER-769
 * This test class test the Rest URI template patterns like uri-template="/view/*"
 */
public class URITemplateRESTTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "rest"
                + File.separator + "uri-template-synapse.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message Via REST to test uri template fix")
    public void testRESTURITemplate() throws Exception {
        /*
        sending parameter /view/WSO2 to match /view/{symbol} in uri-template
         */
        HttpResponse response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("stockquote") + "/view/IBM", null);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatch");
        Assert.assertTrue(response.getData().contains("IBM"), "Response message is not as expected.");
        Assert.assertTrue(response.getData().contains("IBM Company"), "Response message is not as expected");

        /*
        sending parameter /view/stockQuote to match /view/* in uri-template
         */
        response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("getquote") + "/view/getStockQuote", null);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatch");
        Assert.assertTrue(response.getData().contains("WSO2"), "Response message is not as expected.");
        Assert.assertTrue(response.getData().contains("WSO2 Company"), "Response message is not as expected");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}

