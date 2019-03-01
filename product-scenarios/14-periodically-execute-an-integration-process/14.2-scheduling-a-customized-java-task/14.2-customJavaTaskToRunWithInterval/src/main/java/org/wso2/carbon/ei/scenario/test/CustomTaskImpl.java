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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.synapse.task.Task;
import org.json.JSONObject;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;

import java.io.IOException;
import java.io.InputStream;

public class CustomTaskImpl implements Task {

    private String url;

    private static final Log LOG = LogFactory.getLog(CustomTaskImpl.class);

    /**
     * Holds the Message to be injected.
     */
    private String message = null;

    /**
     * UUID to uniquely identify the test run.
     */
    private String uuid = null;

    @Override
    public void execute() {

        try (DefaultHttpClient httpClient = new DefaultHttpClient()) {

            //prepare the request
            HttpPost postRequest = new HttpPost(url);
            StringEntity input = new StringEntity(message);
            input.setContentType(HttpConstants.MEDIA_TYPE_APPLICATION_JSON);
            postRequest.setEntity(input);

            //Invoke post request and receive response
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error(
                        "Error invoking incrementing the invocation count. Unexpected response received: " + response);
            }

            //build response string
            String responseAsString = "";
            if (response.getEntity() != null) {
                InputStream in = response.getEntity().getContent();
                int length;
                byte[] tmp = new byte[2048];
                StringBuilder buffer = new StringBuilder();
                while ((length = in.read(tmp)) != -1) {
                    buffer.append(new String(tmp, 0, length));
                }
                responseAsString = buffer.toString();
            }

            //extract number of invocations
            JSONObject responseJson = new JSONObject(responseAsString);
            int receivedInvocationCount = responseJson.getInt("invocationCount");
            LOG.info("number of invocations for " + uuid + "=" + receivedInvocationCount);
        } catch (IOException e) {
            LOG.error("Error incrementing the invocation count. Unexpected response received: ", e);
        }
    }

    /**
     * Sets the message to be used by the task.
     *
     * @param messageProperty message defined in task properties
     */
    public void setMessage(String messageProperty) {
        message = messageProperty;
    }

    /**
     * Sets a unique id for the task execution.
     *
     * @param invocationUuid
     */
    public void setUuid(String invocationUuid) {
        uuid = invocationUuid;
        url = ScenarioConstants.BACKEND_REST_SERVICE1_URL + "invocationCount/" + uuid;
    }
}
