/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.businessprocess.integration.tests.bpmn.rest;

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.tests.bpmn.BPMNTestUtils;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNProcess;


public class BPMNRestTasksTest extends BPSMasterTest {

    WorkflowServiceClient workflowServiceClient;
    public static final String tasksUrl = "runtime/tasks";

    @BeforeTest
    public void init() throws Exception {
        super.init();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        loginLogoutClient.login();
        uploadBPMNForTest("oneTaskProcess");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "oneTaskProcess", workflowServiceClient.getDeployments().length);
    }


    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get tasks", priority = 1, singleThreaded =
            true)
    public void testGetTasks() throws Exception {
        String processId = null;
        //start one task process
        for (BPMNProcess process : workflowServiceClient.getProcesses()) {
            if (process.getKey().equals("oneTaskProcess")) {
                processId = process.getProcessId();
                workflowServiceClient.getInstanceServiceStub().startProcess(processId);
                break;
            }
        }

        HttpResponse response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl);
        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("runtime/tasks test", 200, response.getStatusLine().getStatusCode());
        Assert.assertTrue("runtime/tasks test", jsonObject.getInt("total") > 0);

        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "?name=my+task");
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("runtime/tasks?name= test", "my task", ((JSONObject)((JSONArray) jsonObject.get("data")).get(
                0)).getString("name"));

        //start another instance, which will start another task
        workflowServiceClient.getInstanceServiceStub().startProcess(processId);

        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "?order=desc");
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertTrue("runtime/tasks?order=desc test",
                ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).getInt("id")
                        > ((JSONObject) ((JSONArray) jsonObject.get("data")).get(1)).getInt("id"));


        int taskId = ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).getInt("id");
        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "/" + taskId);
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("runtime/tasks/{taskId} test", taskId, jsonObject.getInt("id"));

        //update task
        String updateBody = "{\"owner\":\"owner\", \"priority\":\"20\"}";
        response = BPMNTestUtils.putRequest(backEndUrl + tasksUrl + "/" + taskId, new JSONObject(updateBody));
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("PUT runtime/tasks/{taskId} test", 20, jsonObject.getInt("priority"));
        Assert.assertEquals("PUT runtime/tasks/{taskId} test", "owner", jsonObject.getString("owner"));

        //update none existing task
        response = BPMNTestUtils.putRequest(backEndUrl + tasksUrl + "/0", new JSONObject(updateBody));
        Assert.assertEquals("PUT runtime/tasks/{taskId} test", 404, response.getStatusLine().getStatusCode());

        //task actions
        String claimRequest = "{\"action\":\"claim\",\"assignee\":\"userWhoClaims\"}";
        response = BPMNTestUtils.postRequest(backEndUrl + tasksUrl + "/" + taskId, new JSONObject(claimRequest));
        Assert.assertEquals("POST runtime/tasks/{taskId}  claim test", 200, response.getStatusLine().getStatusCode());
        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "/" + taskId);
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("POST runtime/tasks/{taskId}  claim test", "userWhoClaims", jsonObject.getString("assignee"));

        String delegateRequest = "{\"action\":\"delegate\",\"assignee\":\"delegatedUser\"}";
        response = BPMNTestUtils.postRequest(backEndUrl + tasksUrl + "/" + taskId, new JSONObject(delegateRequest));
        Assert.assertEquals("POST runtime/tasks/{taskId}  delegate test", 200, response.getStatusLine().getStatusCode());
        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "/" + taskId);
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("POST runtime/tasks/{taskId}  claim test", "delegatedUser", jsonObject.getString("assignee"));

        //resolve
        String resolve = "{\"action\":\"resolve\"}";
        response = BPMNTestUtils.postRequest(backEndUrl + tasksUrl + "/" + taskId, new JSONObject(resolve));
        Assert.assertEquals("POST runtime/tasks/{taskId}  resolve test", 200, response.getStatusLine().getStatusCode());
        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "/" + taskId);
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("POST runtime/tasks/{taskId}  claim test", "owner", jsonObject.getString("assignee"));

        //claim
        claimRequest = "{\"action\":\"claim\"}";
        BPMNTestUtils.postRequest(backEndUrl + tasksUrl + "/" + taskId, new JSONObject(claimRequest));

        //complete
        String complete = "{\"action\":\"complete\"}";
        response = BPMNTestUtils.postRequest(backEndUrl + tasksUrl + "/" + taskId, new JSONObject(complete));
        Assert.assertEquals("POST runtime/tasks/{taskId}  resolve test", 200, response.getStatusLine().getStatusCode());
        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "/" + taskId);
        //should not exist such task
        Assert.assertEquals("POST runtime/tasks/{taskId}  claim test", 404, response.getStatusLine().getStatusCode());

        //delete a task
        workflowServiceClient.getInstanceServiceStub().startProcess(processId);
        String result = BPMNTestUtils.getRequest(backEndUrl + tasksUrl);
        jsonObject = new JSONObject(result);
        taskId = ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).getInt("id");
        response = BPMNTestUtils.deleteRequest(backEndUrl + tasksUrl + "/" + taskId);
        Assert.assertEquals("DELETE runtime/tasks/{taskId}  claim test", 200, response.getStatusLine().getStatusCode());
        response = BPMNTestUtils.getRequestResponse(backEndUrl + tasksUrl + "/" + taskId);
        Assert.assertEquals("DELETE runtime/tasks/{taskId}  claim test", 404, response.getStatusLine().getStatusCode());
    }
}
