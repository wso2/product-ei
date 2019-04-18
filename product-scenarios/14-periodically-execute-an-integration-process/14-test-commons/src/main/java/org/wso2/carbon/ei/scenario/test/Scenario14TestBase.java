/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.SkipException;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.io.IOException;

/**
 * Base test class for test cases for scenario 14.
 * <p>
 * This generates the unique id for the test run, Asserts the number of times the back-end has been invoked, and
 * cleans up the invocation count in the back-end.
 */
public class Scenario14TestBase extends ScenarioTestBase {

    private String invocationId;
    private String url;
    private static final Log LOG = LogFactory.getLog(Scenario14TestBase.class);

    public void init(String invocationUuid) throws Exception {
        invocationId = invocationUuid;
        url = ScenarioConstants.BACKEND_REST_SERVICE1_URL + "invocationCount/" + invocationUuid;
        super.init();
    }

    public void executeTest() throws IOException, InterruptedException {
        RESTClient restClient = new RESTClient();

        //clearing invocation history before assertion
        restClient.doDelete(url);
        LOG.info("Successfully cleared invocation history before assertion. Invocation uuid=" + invocationId);
        /*task interval = 3s, sleep for 7 seconds so that there will be 2 or 3 task runs*/
        Thread.sleep(7000);

        long startTime = System.currentTimeMillis();
        HttpResponse httpResponse = restClient.doGet(url);
        long delay = System.currentTimeMillis() - startTime;
        LOG.info("Network delay=" + delay + "ms");
        String responsePayload = HTTPUtils.getResponsePayload(httpResponse);
        JSONObject responseJson = new JSONObject(responsePayload);
        int receivedInvocationCount = responseJson.getInt("invocationCount");
        //the task is configured to run with an interval of 3s. Hence, there could be 2 or 3 task runs i.e., (2 when the
        // task runs at the 2nd and 5th second, 3 when the task runs at 1st, 4th and 7th second)
        boolean invocationCountReceived = receivedInvocationCount == 2 || receivedInvocationCount == 3;
        Assert.assertTrue(invocationCountReceived, "Incorrect response received. Response: " + responsePayload);
    }

    public void cleanup() throws Exception {
        super.cleanup();
        //Reset invocation history after assertion
        RESTClient restClient = new RESTClient();
        restClient.doDelete(url);
        LOG.info("Successfully cleared invocation history after assertion. Invocation uuid=" + invocationId);
    }
}
