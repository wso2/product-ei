/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.ei.scenario.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.elasticsearch.ElasticSearchClient;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.io.IOException;
import java.util.UUID;

/**
 * Test class to test asynchronous point to point invocation via message queue with ActiveMQ
 */
public class AsyncHttpToHttpViaActiveMqQueueTest extends ScenarioTestBase {

    @BeforeClass
    public void init() throws Exception {
        skipTestsIfStandaloneDeployment();
        super.init();
    }

    /**
     * Test Scenario : Receive HTTP request, store in a queue, receive jms message over a jms proxy and forward to an
     * http endpoint
     *
     * @throws IOException
     */
    @Test(description = "11.1.1.1.1.1")
    public void testAsyncHttpToHttpViaQueueAndListeningJMSProxy() throws IOException {

        UUID uuid = UUID.randomUUID();
        String msgID = getTestRunUUID() + "_11_1_1_1_1_1_" + uuid;
        String request =
                "{" +
                "  \"Request\" : {" +
                "     \"UUID\" : \"" + msgID + "\"," +
                "     \"MESSAGE\" : \"This is sample request to test : Receive HTTP request, store in a queue, " +
                                           "receive jms message over a jms proxy and forward to an http endpoint\"" +
                "  }" +
                "}";

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doPost(getApiInvocationURLHttp("11_1_1_1_1_1_API_testAsyncHttpToHttpViaQueue"),
                request, HttpConstants.MEDIA_TYPE_APPLICATION_JSON);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 202, "API request " +
                getApiInvocationURLHttp("11_1_1_1_1_1_API_testAsyncHttpToHttpViaQueue") + " to server failed" );

        //Assert logs to verify the message is picked by JMS listener proxy from the ActiveMQ queue
        ElasticSearchClient.assertForSingleLogEntry(getElasticSearchHostname(), getDeploymentStackName(),msgID);
    }

    /**
     * Test Scenario : Receive HTTP request, store in a queue, receive jms message over a jms inbound endpoint and
     * forward to an http endpoint
     *
     * @throws IOException
     */
    @Test(description = "11.1.1.1.1.2")
    public void testAsyncHttpToHttpViaQueueAndReceiveMessageOverInboundEndpoint() throws IOException {
        UUID uuid = UUID.randomUUID();
        String msgID = getTestRunUUID() + "_11_1_1_1_1_2_" + uuid;
        String request = "{\n" +
                         "  \"Request\": {\n" +
                         "    \"UUID\": \"" + msgID + "\"," +
                         "    \"MESSAGE\": \"This is sample request to test : Receive HTTP request, store in a queue, receive jms message over a jms inbound endpoint and forward to an http endpoint\"\n" +
                         "  }\n" +
                         "}";

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doPost(getApiInvocationURLHttp("11_1_1_1_1_2_API_testAsyncHttpToHttpViaQueue"),
                                                  request, HttpConstants.MEDIA_TYPE_APPLICATION_JSON);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 202, "API request " +
                            getApiInvocationURLHttp("11_1_1_1_1_2_API_testAsyncHttpToHttpViaQueue") + " to server failed" );

        ElasticSearchClient.assertForSingleLogEntry(getElasticSearchHostname(), getDeploymentStackName(), msgID);
    }
}
