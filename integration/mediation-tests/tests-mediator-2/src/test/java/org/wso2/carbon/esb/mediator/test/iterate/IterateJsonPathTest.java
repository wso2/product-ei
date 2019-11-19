/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * This class tests Iterate Mediator Jsonpath support with Json Payloads
 */
public class IterateJsonPathTest extends ESBIntegrationTest {

    private String input;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        input = FileUtils.readFileToString(new File(getESBResourceLocation() + File.separator + "json" +
                File.separator + "inputESBIterateJson.json"));
        loadESBConfigurationFromClasspath(File.separator + "artifacts" +  File.separator +
                "ESB" + File.separator + "mediatorconfig" + File.separator + "iterate" + File.separator +
                "iterate_jsonpath.xml");
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with basic configuration")
    public void testBasicIterateMediatorFlow() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample1") + "/iteratejson1");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with PreservePayload attribute")
    public void testIterateMediatorFlowWithPreservePayload() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample2") + "/iteratejson2");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with AttachPath attribute")
    public void testIterateMediatorFlowWithAttachPath() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample3") + "/iteratejson3");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with Different AttachPath attribute")
    public void testIterateMediatorFlowWithDifferentAttachPath() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample4") + "/iteratejson4");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with AttachPath as `$`")
    public void testIterateMediatorFlowWithRootAttachPath() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample5") + "/iteratejson5");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with ContinueParent attribute")
    public void testIterateMediatorFlowWithContinueParent() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample6") + "/iteratejson6");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with Call mediator inside target")
    public void testIterateMediatorFlowWithCallMediator() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample7") + "/iteratejson7");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with Sequential attribute")
    public void testIterateMediatorFlowWithSequential() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample8") + "/iteratejson8");
        executeSequenceAndAssertResponse(endpoint);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    private void executeSequenceAndAssertResponse(URL endpoint) throws Exception {

        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");

        HttpResponse httpResponse = HttpRequestUtil.doPost(endpoint, input, header);

        assertTrue(httpResponse.getData().contains("Alice"), "Required element not found in aggregrated payload");
        assertTrue(httpResponse.getData().contains("Bob"), "Required element not found in aggregrated payload");
        assertTrue(httpResponse.getData().contains("Camry"), "Required element not found in aggregrated payload");
    }
}
