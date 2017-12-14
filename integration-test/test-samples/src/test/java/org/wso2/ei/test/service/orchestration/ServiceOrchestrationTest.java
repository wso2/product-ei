/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.test.service.orchestration;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.test.utils.TestUtils;

import java.io.IOException;

/**
 * Service orchestration test.
 */
public class ServiceOrchestrationTest {
    private static final Logger log = LoggerFactory.getLogger(ServiceOrchestrationTest.class);

    private boolean serverStarted;

    @BeforeClass
    public void startServer() {
        serverStarted = TestUtils.startServer("samples/service-orchestration/orchestration.balx");
    }

    @Test
    public void testServiceOrchestration() {
        if (!serverStarted) {
            Assert.fail("Error running the test, server is not started");
        }

        String vehicleId = "11111";
        String requestPayload = "{\"Vehicle\":{\"ID\":\"" + vehicleId + "\"\n" + "   },\n" + "   \"card\":{\n"
                + "      \"no\":\"1234098618781768\",\n" + "      \"cvv\":\"123\"\n" + "   }\n" + "}";

        String url = "http://localhost:9090/license/renew";

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);

        try {
            RequestEntity requestEntity = new StringRequestEntity(requestPayload, "application/json", "UTF-8");

            post.setRequestEntity(requestEntity);

            int status = client.executeMethod(post);
            Assert.assertEquals(status, 200);
            String responseBody = post.getResponseBodyAsString();
            String responseVehicleId = responseBody.split("Vehicle ID\":\"")[1].split("\"}")[0].trim();
            Assert.assertEquals(responseVehicleId, vehicleId);
        } catch (IOException e) {
            log.error("Error while invoking the server ", e);
        }
    }

    @AfterClass
    public void stopServer() {
        if (!TestUtils.stopServer()) {
            log.error("Error stopping the server");
        }
    }

}
