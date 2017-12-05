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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.tests.bpmn.BPMNTestUtils;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNInstance;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNProcess;
import org.wso2.carbon.bpmn.stub.BPMNInstanceServiceStub;

public class BPMNRestProcessInstancesTest extends BPSMasterTest{

    WorkflowServiceClient workflowServiceClient;
    public static final String instanceUrl = "runtime/process-instances";

    @BeforeTest
    public void init() throws Exception {
        super.init();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        loginLogoutClient.login();
    }

    @AfterTest
    public void afterTest() throws Exception {
    }

    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get process instances", priority = 1, singleThreaded =
            true)
    public void testGetProcessInstances() throws Exception {
        BPMNProcess[] bpmnProcesses = workflowServiceClient.getProcesses();
        BPMNInstanceServiceStub bpmnInstanceServiceStub = workflowServiceClient.getInstanceServiceStub();
        for (BPMNProcess process : bpmnProcesses) {
            bpmnInstanceServiceStub.startProcess(process.getProcessId());
        }

        //HTTP get request
        String result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl);
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertEquals("runtime/process-instances/ test", workflowServiceClient.getProcessInstances().length, jsonObject.getInt("total"));
        Assert.assertTrue("runtime/process-instances/ test",
                jsonObject.getString("data").contains(bpmnProcesses[0].getProcessId()));
        Assert.assertTrue("runtime/process-instances/ test",
                jsonObject.getString("data").contains(bpmnProcesses[1].getProcessId()));

        //send request with ?id= parameter
        result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "?id=" + workflowServiceClient.getProcessInstances()[0].getInstanceId());
        jsonObject = new JSONObject(result);
        Assert.assertEquals("runtime/process-instances?id= test", workflowServiceClient.getProcessInstances()[0].getInstanceId(), ((JSONObject)((JSONArray)(jsonObject.get("data"))).get(0)).getString("id"));

        //send request with "?processDefinitionId=" parameter
        result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "?processDefinitionId=" + bpmnProcesses[0].getProcessId());
        jsonObject = new JSONObject(result);
        Assert.assertEquals("runtime/process-instances?processDefinitionId= test", bpmnProcesses[0].getProcessId(), ((JSONObject)((JSONArray)(jsonObject.get("data"))).get(0)).getString("processDefinitionId"));

        result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "?processDefinitionId=" + "nonExistingId");
        jsonObject = new JSONObject(result);
        Assert.assertEquals("runtime/process-instances?processDefinitionId= test", 0, jsonObject.getInt("total"));

        //send request with "?suspended=" parameter
        result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "?suspended=false");
        jsonObject = new JSONObject(result);
        Assert.assertEquals("runtime/process-instances?suspended= test", workflowServiceClient.getProcessInstances().length, jsonObject.getInt("total"));

        //send request with /{instanceId}
        result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "/" + workflowServiceClient.getProcessInstances()[0].getInstanceId());
        jsonObject = new JSONObject(result);
        Assert.assertEquals("runtime/process-instances/{instanceId} test",
                workflowServiceClient.getProcessInstances()[0].getInstanceId(), jsonObject.getString("id"));

    }

    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "start process instances", priority = 1, singleThreaded =
            true)
    public void testStartProcessInstances() throws Exception {
        BPMNProcess[] bpmnProcesses = workflowServiceClient.getProcesses();
        String payloadString = "{\n" + "\"processDefinitionId\":\"" + bpmnProcesses[0].getProcessId() + "\","
                + "\"businessKey\":\"myBusinessKey\"" + "}";
        JSONObject jsonPayload = new JSONObject(payloadString);
        HttpResponse response = BPMNTestUtils.postRequest(backEndUrl + instanceUrl, jsonPayload);
        String resultSet = EntityUtils.toString(response.getEntity());
        JSONObject jsonResult = new JSONObject(resultSet);
        Assert.assertEquals("runtime/process-instances POST test", "myBusinessKey", jsonResult.get("businessKey"));
        String result = BPMNTestUtils.getRequest(backEndUrl + instanceUrl);
        //should include an instance with key myBusinessKey
        Assert.assertTrue("runtime/process-instances POST test", result.contains("myBusinessKey"));
    }

    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get instance resources", priority = 1, singleThreaded =
            true)
    public void testGetInstanceResources() throws Exception {
        //get the process id using local key
        String processId = null;
        for (BPMNProcess process : workflowServiceClient.getProcesses()) {
            if (process.getKey().equals("simpleProcess")) {
                processId = process.getProcessId();
                break;
            }
        }
        //start a instance
        workflowServiceClient.getInstanceServiceStub().startProcess(processId);
        BPMNInstance[] bpmnInstances = workflowServiceClient.getProcessInstances();
        HttpResponse result = BPMNTestUtils.getRequestResponse(backEndUrl + instanceUrl + "/" + bpmnInstances[0].getInstanceId() + "/diagram");
        Assert.assertEquals("runtime/process-instances/{instanceId}/diagram test", "image/png", result.getEntity().getContentType().getValue());

        //get identity links
        String resultStr = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "/" + bpmnInstances[0].getInstanceId() + "/identitylinks");
        resultStr = "{ \"data\":" + resultStr + "}";
        JSONObject jsonObject = new JSONObject(resultStr);
        Assert.assertEquals("runtime/process-instances/{instanceId}/identitykinks test", 0, ((JSONArray)jsonObject.get("data")).length());

        //add identityLink
        String userRequest = "{" + "\"user\":\"kermit\"," + "\"type\":\"participant\"" + "}";
        result = BPMNTestUtils.postRequest(
                backEndUrl + instanceUrl + "/" + bpmnInstances[0].getInstanceId() + "/identitylinks", new JSONObject(userRequest));
        //should return 201 response
        Assert.assertEquals("POST runtime/process-instances/{instanceId}/identitykinks test", 201, result.getStatusLine().getStatusCode());

        //get identity links
        resultStr = BPMNTestUtils.getRequest(backEndUrl + instanceUrl + "/" + bpmnInstances[0].getInstanceId() + "/identitylinks");
        resultStr = "{ \"data\":" + resultStr + "}";
        jsonObject = new JSONObject(resultStr);
        Assert.assertEquals("runtime/process-instances/{instanceId}/identitylinks test", "kermit", ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).getString("user"));

        result = BPMNTestUtils.getRequestResponse(backEndUrl + instanceUrl + "/" + bpmnInstances[0].getInstanceId()
                + "/identitylinks/users/kermit/participant");
        Assert.assertEquals("runtime/process-instances//{processInstanceId}/identitylinks/users/{identityId}/{type} test", 200, result.getStatusLine().getStatusCode());

        //remove identity
        result = BPMNTestUtils.deleteRequest(backEndUrl + instanceUrl + "/" + bpmnInstances[0].getInstanceId() + "/identitylinks/users/kermit/participant");
        Assert.assertEquals("runtime/process-instances/{instanceId}/identitylinks test", 204, result.getStatusLine().getStatusCode());

    }

    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "update instance resources", priority = 1, singleThreaded =
            true)
    public void testUpdateInstanceResources() throws Exception {
        //activate or suspend process tests
        //try to activate none existing resource
        String activateString = "{\"action\":\"activate\"}";
        String suspendString = "{\"action\":\"suspend\"}";
        HttpResponse result = BPMNTestUtils.putRequest(backEndUrl + instanceUrl + "/" + "0", new JSONObject(activateString));
        //should respond with 404
        Assert.assertEquals("runtime/process-instances/{instanceId} PUT test", 404, result.getStatusLine().getStatusCode());

        //start a process
        BPMNProcess[] processes = workflowServiceClient.getProcesses();
        workflowServiceClient.getInstanceServiceStub().startProcess(processes[0].getProcessId());
        String instanceId = workflowServiceClient.getProcessInstances()[0].getInstanceId();

        //suspend the instance
        result = BPMNTestUtils.putRequest(backEndUrl + instanceUrl + "/" + instanceId, new JSONObject(suspendString));
        //should respond with 200
        Assert.assertEquals("runtime/process-instances/{instanceId} PUT test", 200, result.getStatusLine().getStatusCode());

        //activate it
        result = BPMNTestUtils.putRequest(backEndUrl + instanceUrl + "/" + instanceId, new JSONObject(activateString));
        //should respond with 200
        Assert.assertEquals("runtime/process-instances/{instanceId} PUT test", 200, result.getStatusLine().getStatusCode());

        //try to activate again
        result = BPMNTestUtils.putRequest(backEndUrl + instanceUrl + "/" + instanceId, new JSONObject(activateString));
        //should respond with 409 TODO:this should be fixed in API. It is returning 500
        Assert.assertEquals("runtime/process-instances/{instanceId} PUT test", 500, result.getStatusLine().getStatusCode());

        //delete instance
        result = BPMNTestUtils.deleteRequest(
                backEndUrl + instanceUrl + "/" + instanceId);
        //response 204 indicates the process instance was found and deleted.
        Assert.assertEquals("runtime/process-instances/{instanceId} DELETE test", 204, result.getStatusLine().getStatusCode());
    }

}
