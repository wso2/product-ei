/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.ei.scenario.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.StringUtil;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests a load balance endpoint group with 2 endpoints.
 */
public class LoadBalanceEndpointsWithLoadBalanceGroupTest extends ScenarioTestBase {

    private final String[] responses = new String[]{"{\"name\": \"John\",\"age\": 30,\"server\": 1,\"cars\": "
                                                    + "{\"car1\": \"Ford\",\"car2\": \"BMW\",\"car3\": \"Fiat\"}}",
                                                    "{\"name\": \"John\",\"age\": 30,\"server\": 2,\"cars\": "
                                                    + "{\"car1\": \"Ford\",\"car2\": \"BMW\",\"car3\": \"Fiat\"}}"};

    private final String LOAD_BALANCE_MESSAGE_HEADER = "loadBalanceMsg";

    @BeforeClass()
    public void init() throws Exception {
        skipTestsForIncompatibleProductVersions(ScenarioConstants.VERSION_490);
        super.init();
    }

    /**
     * Tests a load balance endpoint group with two endpoints and default configurations.
     *
     * @throws Exception if any error occurs during the execution of the test
     */
    @Test(description = "5.3.1.1.1")
    public void testLoadBalanceEndpointGroup() throws Exception {
        testLoadBalanceGroup("5_3_1_1_1_API_load_balance_group_test");
    }

    /**
     * Tests a load balance endpoint group with two endpoints and policy set to roundRobin.
     *
     * @throws Exception if any error occurs during the execution of the test
     */
    @Test(description = "5.3.1.1.2")
    public void testLoadBalanceEndpointGroupWithPolicyRoundRobin() throws Exception {
        testLoadBalanceGroup("5_3_1_1_2_API_load_balance_group_define_policy_test");
    }

    /**
     * Tests a load balance endpoint group with two endpoints and default configurations.
     *
     * @throws Exception if any error occurs during the execution of the test
     */
    @Test(description = "5.3.1.1.3")
    public void testLoadBalanceEndpointGroupWithNoPolicyNoAlgorithm() throws Exception {
        testLoadBalanceGroup("5_3_1_1_3_API_load_balance_group_define_nothing");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

    /**
     * Sends a request to the provided API with a load balance endpoint group.
     *
     * @param apiInvocationUrl the api to be invoked
     * @throws IOException if an error occurs while invoking the exception
     */
    private void testLoadBalanceGroup(String apiInvocationUrl) throws IOException {

        //Asserts that the first response was forwarded to server 1
        String responsePayload = getResponse(apiInvocationUrl);
        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenJsonTags(responsePayload),
                            StringUtil.trimTabsSpaceNewLinesBetweenJsonTags(responses[0]),
                            "Actual Response and Expected Response mismatch!");

        /*Asserts the second response. In the case of a standalone deployment this should be forwarded to server2.
        But in a 2 node cluster, this will still be sent to server1 by the second EI node.
         */
        boolean expectedResponseReceived = false;
        String responsePayload2 = "";
        for (int i = 0; i < 2; i++) {
            responsePayload2 = getResponse(apiInvocationUrl);
            if (StringUtil.trimTabsSpaceNewLinesBetweenJsonTags(responses[1])
                          .equals(StringUtil.trimTabsSpaceNewLinesBetweenJsonTags(responsePayload2))) {
                expectedResponseReceived = true;
                break;
            } else if (!StringUtil.trimTabsSpaceNewLinesBetweenJsonTags(responses[0])
                                  .equals(StringUtil.trimTabsSpaceNewLinesBetweenJsonTags(responsePayload2))) {
                Assert.fail("Unexpected response received. Expected: " + responses[1] + " or " + responses[0]
                            + ", actual: " + responsePayload);

            }
        }
        Assert.assertTrue(expectedResponseReceived, "Expected response not received. Expected: "
                                                    + responses[1] + ", actual: " + responsePayload2);
    }

    private String getResponse(String apiInvocationUrl) throws IOException {
        apiInvocationUrl = getApiInvocationURLHttp(apiInvocationUrl);
        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<>();
        headers.put(ScenarioConstants.MESSAGE_ID, LOAD_BALANCE_MESSAGE_HEADER);
        HttpResponse httpResponse = httpClient.doPost(apiInvocationUrl, headers,
                                                      ScenarioConstants.BASIC_JSON_MESSAGE,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_JSON);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200,
                            "Load balance test failed, incorrect response code received");
        return httpClient.getResponsePayload(httpResponse);
    }

}
