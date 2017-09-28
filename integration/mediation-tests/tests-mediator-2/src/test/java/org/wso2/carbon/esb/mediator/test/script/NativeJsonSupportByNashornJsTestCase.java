/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.script;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import static org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil.doPost;

/**
 * This test case verifies that, native json operations are supported by NashornJs.
 */
public class NativeJsonSupportByNashornJsTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a JSON message Via REST and manipulate with NashornJS")
    public void testSendingPayloadJson() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/json");
        String payload = "{\n" + "\"name\": \"John Doe\",\n" + "\"dob\": \"1990-03-19\",\n" + "\"ssn\": "
                + "\"234-23" + "-525\",\n" + "\"address\": \"California\",\n" + "\"phone\": \"8770586755\",\n"
                + "\"email\":" + " \"johndoe@gmail.com\",\n" + "\"doctor\": \"thomas collins\",\n" + "\"hospital\": "
                + "\"grand oak " + "community hospital\",\n" + "\"cardNo\": \"7844481124110331\",\n"
                + "\"appointment_date\": " + "\"2017-04-02\"\n" + "}";
        HttpResponse response = doPost(new URL(getApiInvocationURL("nashornJsNativeJSONSupport")), payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("California")), "Response does not contain "
                + "the keyword \"California\". Response: " + response.getData());

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
