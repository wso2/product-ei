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
package org.wso2.bps.integration.tests.bpmn.rest;

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.bps.integration.tests.bpmn.BPMNTestUtils;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNInstance;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNProcess;

import java.util.List;

public class BPMNRestExecutionTest extends BPSMasterTest {

    WorkflowServiceClient workflowServiceClient;
    public static final String executionsUrl = "runtime/executions";

    @BeforeTest
    public void init() throws Exception {
        super.init();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        loginLogoutClient.login();
        uploadBPMNForTest("ExecutionResourceTest");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "ExecutionResourceTest", 0);
    }

    @AfterTest (alwaysRun = true)
    public void cleanServer () throws Exception {
        workflowServiceClient.undeploy("ExecutionResourceTest");
    }

    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get executions", priority = 1, singleThreaded =
            true)
    public void testGetExecutions() throws Exception {
        BPMNProcess[] bpmnProcesses = workflowServiceClient.getProcesses();
        String processId = null;
        //start a ExecutionResourceTest process
        for (BPMNProcess process : bpmnProcesses) {
            if (process.getKey().equals("processOneExec")) {
                processId = process.getProcessId();
                workflowServiceClient.getInstanceServiceStub().startProcess(processId);
                break;
            }
        }

        HttpResponse response = BPMNTestUtils.getRequestResponse(backEndUrl + executionsUrl);
        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        //at least two executions should return, processOne starts two executions
        Assert.assertTrue("runtime/executions test", (jsonObject.getInt("total") > 1));

        List<BPMNInstance> bpmnInstances = workflowServiceClient.getProcessInstancesByProcessId(processId);

        //request with process id
        response = BPMNTestUtils.getRequestResponse(backEndUrl + executionsUrl + "?processDefinitionId=" + processId);
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        JSONArray dataArray = (JSONArray) (jsonObject.get("data"));
        //assuming only two instances exist
        for (int i = 0; i < jsonObject.getInt("size"); i++) {
            String instanceId = ((JSONObject) dataArray.get(i)).getString("processInstanceId");
            Assert.assertTrue("runtime/executions?processDefinitionId test",
                    instanceId.equals(bpmnInstances.get(0).getInstanceId()) || instanceId.equals(bpmnInstances.get(1).getInstanceId()));
        }

        //{executionId}
        String executionId = ((JSONObject) dataArray.get(0)).getString("id");
        response = BPMNTestUtils.getRequestResponse(backEndUrl + executionsUrl + "/" + executionId);
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals("runtime/executions/{executionId} test", executionId, jsonObject.getString("id"));

        response = BPMNTestUtils.getRequestResponse(backEndUrl + executionsUrl + "/" + executionId + "/activities");
        String result = EntityUtils.toString(response.getEntity());
        Assert.assertTrue("runtime/executions/{executionId}/activities test", result.contains("processTask"));

        //TODO:post requests do not work with given payload for variables, returns 400 saying no variables found (worked well with a rest client (postman) )
        //        String variable = "[{\"name\":\"intProcVar\", \"type\":\"integer\", \"value\":123, \"scope\":\"local\"}]";
        //        response = BPMNTestUtils.postRequest(backEndUrl + executionsUrl + "/" + executionId + "/variables",new JSONArray(variable));
        //        result = EntityUtils.toString(response.getEntity());

        for (BPMNProcess process : bpmnProcesses) {
            if (process.getKey().equals("processOneSignal")) {
                processId = process.getProcessId();
                workflowServiceClient.getInstanceServiceStub().startProcess(processId);
                break;
            }
        }

        bpmnInstances = workflowServiceClient.getProcessInstancesByProcessId(processId);

        String action = "{" + "\"action\":\"signal\"" + "}";
        response = BPMNTestUtils.putRequest(backEndUrl + executionsUrl + "/" + bpmnInstances.get(0).getInstanceId(),
                new JSONObject(action));
        Assert.assertEquals("PUT runtime/executions/{executionId} test", 200, response.getStatusLine().getStatusCode());
        //get the executin again to check the state
        response = BPMNTestUtils
                .getRequestResponse(backEndUrl + executionsUrl + "/" + bpmnInstances.get(0).getInstanceId());
        jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        //should go to the wait state after a signal
        Assert.assertEquals("PUT runtime/executions/{executionId} test", "anotherWaitState",
                jsonObject.getString("activityId"));
    }

}
