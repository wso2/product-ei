/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.aggregate;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

/**
 * Testcase for aggregate mediator NPE for JSON payload with maximum message count = 1
 */
public class AggregateWithJSONAndMaxMinLimits extends ESBIntegrationTest {

    String serviceUrl;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        serviceUrl = getApiInvocationURL("devices");

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/aggregate/aggregate_json.xml");
    }

    @Test
    public void testAggregateMaximumCountOne() {
        String response = sendRequest(serviceUrl + "/getDevices/1");

        Assert.assertEquals(response, "{\"rootelement\":{\"deviceId\":\"device\"}}");
    }

    @Test
    public void testAggregateMaximumCountGreaterThanOne() {
        String response = sendRequest(serviceUrl + "/getDevices/2");

        Assert.assertEquals(response, "{\"rootelement\":[{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}]}");

        response = sendRequest(serviceUrl + "/getDevices/6");

        Assert.assertEquals(response, "{\"rootelement\":[{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}," +
                                      "{\"deviceId\":\"device\"},{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}]}");
    }

    @Test
    public void testAggregateMaximumCountLessThanOne() {
        String response = sendRequest(serviceUrl + "/getDevices/0");

        Assert.assertEquals(response, "{\"rootelement\":[{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}," +
                                      "{\"deviceId\":\"device\"},{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}]}");

        response = sendRequest(serviceUrl + "/getDevices/-1");

        Assert.assertEquals(response, "{\"rootelement\":[{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}," +
                                      "{\"deviceId\":\"device\"},{\"deviceId\":\"device\"},{\"deviceId\":\"device\"}]}");
    }

    private String sendRequest(String requestUrl) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(requestUrl);
            get.addHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(get);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
