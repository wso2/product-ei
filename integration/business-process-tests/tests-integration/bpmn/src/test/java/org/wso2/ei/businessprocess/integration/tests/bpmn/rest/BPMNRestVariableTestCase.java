/*
 *     Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.ei.businessprocess.integration.tests.bpmn.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.tests.bpmn.BPMNTestUtils;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNInstance;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNProcess;
import org.wso2.carbon.bpmn.stub.BPMNInstanceService;

import java.io.IOException;

public class BPMNRestVariableTestCase extends BPSMasterTest {



    private WorkflowServiceClient workflowServiceClient;
    private BPMNInstance targetProcessInstance;
    private String taskId;
    private String targetExecutionId;

    private static final String BPMNTestPackageName = "oneTaskProcess";
    private static final Log log = LogFactory.getLog(BPMNRestVariableTestCase.class);
    private static final String variable1Name = "testvar1";
    private static final String variable2Name = "testvar2";
    private static final String taskVariableName = "taskVar2";
    private static final String execVariableName = "execVar";
    private static final String instanceUrl = "runtime/process-instances";
    private static final String taskUrl = "runtime/tasks";
    private static final String executionUrl = "runtime/executions";

    @BeforeTest
    protected void init() throws Exception {
        super.init();

        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        loginLogoutClient.login();

        uploadBPMNForTest(BPMNTestPackageName);

        int deploymentCount = 0;
        if (workflowServiceClient.getDeployments() != null) {
            deploymentCount = workflowServiceClient.getDeployments().length;
        }

        //wait till oneTaskProcess get deployed
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, BPMNTestPackageName, deploymentCount);

        //Create new process instance
        BPMNInstance[] bpmnInstances = startProcessInstance(BPMNTestPackageName);

        Assert.assertNotNull(bpmnInstances, "BPMN process instance creation failed");
        Assert.assertEquals(bpmnInstances.length, 1, "More than one instance get created");

        log.info("Process instance creation success. Instance ID: " +bpmnInstances[0].getInstanceId());

        targetProcessInstance = bpmnInstances[0];

    }

    @AfterTest(alwaysRun = true)
    public void removeArtifacts() throws Exception {
        workflowServiceClient.undeploy(BPMNTestPackageName);
    }


    /**
     * Test creating new process variables
     * TEST : POST runtime/process-instances/{processInstanceId}/variables
     * @throws Exception
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test variable creation with charset", priority = 1,
            singleThreaded = true)
    public void testCreateVariables () throws Exception {

        String createVarRequest =   "[\n" +
                "    { \n" +
                "      \"name\":\""+variable1Name+"\",\n" +
                "      \"type\":\"integer\",\n" +
                "      \"value\":15000 \n" +
                "    },\n" +
                "    { \n" +
                "      \"name\":\""+variable2Name+"\",\n" +
                "      \"type\":\"string\",\n" +
                "      \"value\":\"test Variable text\" \n" +
                "    }\n" +
                "]";
        String postUrl = backEndUrl + instanceUrl + "/" + targetProcessInstance.getInstanceId() + "/variables";
        HttpResponse response = BPMNTestUtils.postRequest(postUrl, new JSONArray(createVarRequest));

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201, "Variable creation failed. Casue : " +
                response.getStatusLine().getReasonPhrase());
    }


    /**
     * Test updating multiple process variables
     * TEST : PUT runtime/process-instances/{processInstanceId}/variables
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test variable update with charset", priority = 2,
            singleThreaded = true)
    public void testUpdateMultipleVariables () throws JSONException, IOException {

        int newVar1Value = 10000;
        String newVar2Value = "test Variable text updated";
        JSONObject responseJsonObj;

        String createVarRequest =   "[\n" +
                "    { \n" +
                "      \"name\":\""+variable1Name+"\",\n" +
                "      \"type\":\"integer\",\n" +
                "      \"value\":"+ newVar1Value+ " \n" +
                "    },\n" +
                "    { \n" +
                "      \"name\":\""+variable2Name+"\",\n" +
                "      \"type\":\"string\",\n" +
                "      \"value\":\""+ newVar2Value+ "\" \n" +
                "    }\n" +
                "]";

        //Make variable update request
        String putUrl = backEndUrl + instanceUrl + "/" + targetProcessInstance.getInstanceId() + "/variables";
        HttpResponse response = BPMNTestUtils.putRequest(putUrl, new JSONArray(createVarRequest));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201, "Variable update failed. Cause : " +
                response.getStatusLine().getReasonPhrase());

        //Verify variable update success
        String var1GetUrl = backEndUrl + instanceUrl + "/" + targetProcessInstance.getInstanceId() + "/variables/" + variable1Name;
        response = BPMNTestUtils.getRequestResponse(var1GetUrl);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed. Cause:" +
                response.getStatusLine().getReasonPhrase());
        responseJsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(responseJsonObj.getInt("value"), newVar1Value, "Variable content update failed");

        String var2GetUrl = backEndUrl + instanceUrl + "/" + targetProcessInstance.getInstanceId() + "/variables/" + variable2Name;
        response = BPMNTestUtils.getRequestResponse(var2GetUrl);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed. Cause:" +
                response.getStatusLine().getReasonPhrase());
        responseJsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(responseJsonObj.getString("value"), newVar2Value, "Variable content update failed");
    }


    /**
     * Test updating signle process variable
     * TEST: PUT runtime/process-instances/{processInstanceId}/variables/{variableName}
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test single variable update with charset", priority = 3,
            singleThreaded = true)
    public void testUpdateSingleVariable () throws JSONException, IOException {

        int newVar1Value = 20000;
        JSONObject responseJsonObj;

        String createVarRequest =   "    { \n" +
                "      \"name\":\""+variable1Name+"\",\n" +
                "      \"type\":\"integer\",\n" +
                "      \"value\":"+ newVar1Value+ " \n" +
                "    }\n";

        //Make variable update request
        String putUrl = backEndUrl + instanceUrl + "/" + targetProcessInstance.getInstanceId() + "/variables/" + variable1Name;
        HttpResponse response = BPMNTestUtils.putRequest(putUrl, new JSONObject(createVarRequest));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable update failed. Cause : " +
                response.getStatusLine().getReasonPhrase());

        //Verify variable update success
        String var1GetUrl = backEndUrl + instanceUrl + "/" + targetProcessInstance.getInstanceId() + "/variables/" + variable1Name;
        response = BPMNTestUtils.getRequestResponse(var1GetUrl);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed. Cause:" +
                response.getStatusLine().getReasonPhrase());
        responseJsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(responseJsonObj.getInt("value"), newVar1Value, "Variable content update failed");
    }


    /**
     * Test creating task variables
     * TEST: POST runtime/tasks/{taskId}/variables
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test creating task variables with charset", priority = 4,
            singleThreaded = true)
    public void testCreateTaskVariables () throws IOException, JSONException {

        String requestUrl;
        String requestPayload;
        HttpResponse response;

        //get task id of the target process instance
        requestUrl = backEndUrl + taskUrl + "?processInstanceId=" + targetProcessInstance.getInstanceId();
        response = BPMNTestUtils.getRequestResponse(requestUrl);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Task retrieval failed");
        JSONObject taskInfo = new JSONObject(EntityUtils.toString(response.getEntity()));
        //((JSONObject) ((JSONArray) taskInfo.get("data")).get(0)).get("id")
        JSONArray taskList = (JSONArray) taskInfo.get("data");
        taskId = ((JSONObject)taskList.get(0)).getString("id");

        Assert.assertNotNull(taskId, "Task Id retrieval failed");

        //create task variable
        requestPayload =    "[\n" +
                "  {\n" +
                "    \"name\" : \""+taskVariableName+"\",\n" +
                "    \"scope\" : \"local\",\n" +
                "    \"type\" : \"string\",\n" +
                "    \"value\" : \"This is task variable text\"\n" +
                "  }\n" +
                "]";
        requestUrl = backEndUrl + taskUrl + "/" + taskId + "/variables";
        response = BPMNTestUtils.postRequest(requestUrl, new JSONArray(requestPayload));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201, "Task variable creation failed");

        //verify variable creation
        requestUrl = backEndUrl + taskUrl + "/" + taskId + "/variables/" + taskVariableName;
        response = BPMNTestUtils.getRequestResponse(requestUrl);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed");
        JSONObject responsePayload = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(responsePayload.getString("value"), "This is task variable text", "Variable value is not set correctly");
    }


    /**
     * Test updating single task variable
     * TEST: PUT runtime/tasks/{taskId}/variables/{variableName}
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test creating task variables with charset", priority = 5,
            singleThreaded = true)
    public void testUpdateSingleTaskVariable () throws JSONException, IOException {

        String requestUrl = backEndUrl + taskUrl + "/" + taskId + "/variables/" + taskVariableName;
        String requestPayload = "  {\n" +
                "    \"name\" : \""+taskVariableName+"\",\n" +
                "    \"scope\" : \"local\",\n" +
                "    \"type\" : \"string\",\n" +
                "    \"value\" : \"This is task variable text UPDATE\"\n" +
                "  }\n";

        HttpResponse response = BPMNTestUtils.putRequest(requestUrl, new JSONObject(requestPayload));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable update failed. Cause : " +
                response.getStatusLine().getReasonPhrase());

        //verify variable creation
        requestUrl = backEndUrl + taskUrl + "/" + taskId + "/variables/" + taskVariableName;
        response = BPMNTestUtils.getRequestResponse(requestUrl);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed");
        JSONObject responsePayload = new JSONObject(EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(responsePayload.getString("value"), "This is task variable text UPDATE", "Variable value is not set correctly");
    }


    /**
     * Test creating execution variables
     * TEST: POST runtime/executions/{executionId}/variables
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test creating task variables with charset", priority = 6,
            singleThreaded = true)
    public void testCreateExecutionVariable () throws IOException, JSONException {
        HttpResponse response;
        String requestPayload;
        JSONObject responsePayload;
        JSONArray responseData;
        String requestUrl = backEndUrl + executionUrl +"?processInstanceId=" +targetProcessInstance.getInstanceId();

        //get executions
        response = BPMNTestUtils.getRequestResponse(requestUrl);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed");

        responsePayload = new JSONObject(EntityUtils.toString(response.getEntity()));
        responseData = responsePayload.getJSONArray("data");

        Assert.assertNotNull(responseData, "Execution list empty");
        Assert.assertEquals(responseData.length(), 1, "Expected number of execution not found");

        targetExecutionId = responseData.getJSONObject(0).getString("id");

        Assert.assertNotNull(targetExecutionId, "Unable to retieve execution ID");

        //create excution variable
        requestUrl = backEndUrl + executionUrl + "/" + targetExecutionId + "/variables";
        requestPayload =    "[\n" +
                "  {\n" +
                "    \"name\" : \""+execVariableName+"\",\n" +
                "    \"scope\" : \"local\",\n" +
                "    \"type\" : \"string\",\n" +
                "    \"value\" : \"This is execution variable text\"\n" +
                "  }\n" +
                "]";
        response = BPMNTestUtils.postRequest(requestUrl, new JSONArray(requestPayload));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201, "Execution variable creation failed");

        //verify created variable
        requestUrl = backEndUrl + executionUrl + "/" + targetExecutionId + "/variables/" +execVariableName;
        response = BPMNTestUtils.getRequestResponse(requestUrl);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed");

        responsePayload = new JSONObject(EntityUtils.toString(response.getEntity()));

        Assert.assertEquals(responsePayload.getString("value"), "This is execution variable text", "Variable value is not set correctly");
    }


    /**
     * Test updating execution variables
     * TEST: PUT runtime/executions/{executionId}/variables
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test creating task variables with charset", priority = 7,
            singleThreaded = true)
    public void testUpdateExecutionVariables () throws JSONException, IOException {
        HttpResponse response;
        String requestPayload;
        JSONObject responsePayload;
        String requestUrl;

        //create excution variable
        requestUrl = backEndUrl + executionUrl + "/" + targetExecutionId + "/variables";
        requestPayload =    "[\n" +
                "  {\n" +
                "    \"name\" : \""+execVariableName+"\",\n" +
                "    \"scope\" : \"local\",\n" +
                "    \"type\" : \"string\",\n" +
                "    \"value\" : \"This is execution variable text UPDATE\"\n" +
                "  }\n" +
                "]";
        response = BPMNTestUtils.putRequest(requestUrl, new JSONArray(requestPayload));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201, "Execution variable update failed");

        //verify update variable
        requestUrl = backEndUrl + executionUrl + "/" + targetExecutionId + "/variables/" +execVariableName;
        response = BPMNTestUtils.getRequestResponse(requestUrl);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed");

        responsePayload = new JSONObject(EntityUtils.toString(response.getEntity()));

        Assert.assertEquals(responsePayload.getString("value"), "This is execution variable text UPDATE", "Variable value is not updated correctly");
    }


    /**
     * Test updating single execution variable
     * TEST: PUT runtime/executions/{executionId}/variables/{variableName}
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.bps.bpmn.rest.variableTest"}, description = "test creating task variables with charset", priority = 8,
            singleThreaded = true)
    public void testUpdateSingleExecutionVariables () throws JSONException, IOException {
        HttpResponse response;
        String requestPayload;
        JSONObject responsePayload;
        String requestUrl;

        //create excution variable
        requestUrl = backEndUrl + executionUrl + "/" + targetExecutionId + "/variables/" + execVariableName;
        requestPayload =    "  {\n" +
                "    \"name\" : \""+execVariableName+"\",\n" +
                "    \"scope\" : \"local\",\n" +
                "    \"type\" : \"string\",\n" +
                "    \"value\" : \"This is execution variable text UPDATE 2\"\n" +
                "  }\n";
        response = BPMNTestUtils.putRequest(requestUrl, new JSONObject(requestPayload));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 201, "Execution variable update failed");

        //verify update variable
        requestUrl = backEndUrl + executionUrl + "/" + targetExecutionId + "/variables/" +execVariableName;
        response = BPMNTestUtils.getRequestResponse(requestUrl);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Variable retrieval failed");

        responsePayload = new JSONObject(EntityUtils.toString(response.getEntity()));

        Assert.assertEquals(responsePayload.getString("value"), "This is execution variable text UPDATE 2", "Variable value is not updated correctly");
    }



    /**
     * This function will start process instance of target bpmn process
     * @param processId : process id of the deployed process [<process id="oneTaskProcess" .. ></process>]
     * @return returns current all available process instances if successfully created otherwise returns null
     */
    private BPMNInstance[] startProcessInstance (String processId) throws Exception {
        BPMNProcess[] processes = workflowServiceClient.getProcesses();
        BPMNInstanceService bpmnInstanceService = workflowServiceClient.getInstanceServiceStub();
        BPMNInstance[] instances = bpmnInstanceService.getProcessInstances();

        int previousInstanceCount = instances == null ? 0 : instances.length;
        for (BPMNProcess process : processes) {
            if (process.getProcessId().split(":")[0].equals(processId)) {
                bpmnInstanceService.startProcess(process.getProcessId());
                break;
            }
        }

        instances = bpmnInstanceService.getProcessInstances();
        if (instances.length > previousInstanceCount) {
            return instances;
        }

        return null;
    }


}
