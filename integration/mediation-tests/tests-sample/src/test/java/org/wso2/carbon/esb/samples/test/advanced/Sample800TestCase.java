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

package org.wso2.carbon.esb.samples.test.advanced;

import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

public class Sample800TestCase extends ESBIntegrationTest {

    private final String contentType ="application/text";
    private String responsePayload;
    private String url;
    private HttpResponse httpResponse;


    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(800);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Invoke REST API ")
    public void invokeRESTAPI() throws Exception {

        SimpleHttpClient httpClient=new SimpleHttpClient();

        url="http://127.0.0.1:8480/stockquote/view/IBM";
        httpResponse = httpClient.doGet(url, null);
        responsePayload = httpClient.getResponsePayload(httpResponse);
        assertTrue(responsePayload.contains("IBM"), "Symbol IBM not found in response message");


        url="http://127.0.0.1:8480/stockquote/view/MSFT";
        httpResponse = httpClient.doGet(url, null);
        responsePayload = httpClient.getResponsePayload(httpResponse);
        assertTrue(responsePayload.contains("MSFT"), "Symbol MSFT not found in response message");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            cleanup();
        } finally {
        }
    }
}
