/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.bps.integration.common.clients.bpmn;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This Class contains methods which uses the BPMN Rest Services to carryout BPMN Task
 *
 * @author WSO2
 * @version 1.0
 */
public class ActivitiRestClient {

    private static final Log log = LogFactory.getLog(ActivitiRestClient.class);
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static String USER_CLAIM = "paul";
    private final static String USER_DELEGATE = "will";
    private final static String NOT_AVAILABLE = "Not Available";
    private final static String AVAILABLE = "Available";
    private final static String ID = "id";
    private final static String NAME = "name";
    private int port;
    private String hostname = "";
    private URL serviceURL;

    public ActivitiRestClient(String portM, String hostnameM) throws MalformedURLException {
        port = Integer.parseInt(portM);
        hostname = hostnameM;
        serviceURL = new URL("http", hostname, port, "/bpmn/");
    }


    /**
     * This Method is used to deploy BPMN packages to the BPMN Server
     *
     * @param fileName The name of the Package to be deployed
     * @param filePath The location of the BPMN package to be deployed
     * @throws java.io.IOException
     * @throws org.json.JSONException
     * @returns String array with status, deploymentID and Name
     */
    public String[] deployBPMNPackage(String filePath, String fileName)
            throws RestClientException, IOException, JSONException {
        String url = serviceURL + "repository/deployments";
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", new File(filePath),
                              ContentType.MULTIPART_FORM_DATA, fileName);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        HttpResponse response = httpClient.execute(httpPost);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        if (status.contains(Integer.toString(HttpStatus.SC_CREATED)) || status.contains(Integer.toString(HttpStatus.SC_OK))) {
            String deploymentID = jsonResponseObject.getString(ID);
            String name = jsonResponseObject.getString(NAME);
            return new String[]{status, deploymentID, name};
        } else if (status.contains(Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR))) {
            String errorMessage = jsonResponseObject.getString("errorMessage");
            throw new RestClientException(errorMessage);
        } else {
            throw new RestClientException("Failed to deploy package " + fileName);
        }
    }

    /**
     * Method is used to acquire deployment details using the deployment ID
     *
     * @param deploymentID Deployment ID of the BPMN Package
     * @throws java.io.IOException
     * @throws org.json.JSONException
     * @returns String Array with status, deploymentID and Name
     */
    public String[] getDeploymentInfoById(String deploymentID)
            throws RestClientException, IOException, JSONException {
        String url = serviceURL + "repository/deployments/"
                     + deploymentID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        if (status.contains(Integer.toString(HttpStatus.SC_CREATED)) || status.contains(Integer.toString(HttpStatus.SC_OK))) {
            String depID = jsonResponseObject.getString(ID);
            String name = jsonResponseObject.getString(NAME);
            return new String[]{status, depID, name};
        } else if (status.contains(Integer.toString(HttpStatus.SC_NOT_FOUND))) {
            throw new RestClientException(NOT_AVAILABLE);
        } else {
            throw new RestClientException("Cannot find deployment");
        }
    }

    /**
     * Method is used to undeploy/remove a deployment from the server
     *
     * @param deploymentID used to identify the deployment to be removed
     * @return String with the Status
     * @throws IOException
     */
    public String unDeployBPMNPackage(String deploymentID) throws IOException {
        String url = serviceURL + "repository/deployments/"
                     + deploymentID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpDelete httpDelete = new HttpDelete(url);
        HttpResponse response = httpClient.execute(httpDelete);
        return response.getStatusLine().toString();
    }

    /**
     * Method to find the definitionID which is necessary to start a process instance
     *
     * @param deploymentID the deployment id is used to identify the deployment uniquely
     * @return String Array containing status and definitionID
     * @throws IOException
     * @throws JSONException
     */
    public String[] findProcessDefinitionInfoById(String deploymentID)
            throws IOException, JSONException {
        String url = serviceURL + "repository/process-definitions";
        String definitionId = "";
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        JSONArray data = jsonResponseObject.getJSONArray("data");
        int responseObjectSize = Integer.parseInt(jsonResponseObject.get("total").toString());
        for (int j = 0; j < responseObjectSize; j++) {
            if (data.getJSONObject(j).getString("deploymentId").equals(deploymentID)) {
                definitionId = data.getJSONObject(j).getString(ID);
            }
        }
        return new String[]{status, definitionId};
    }

    /**
     * Methods used to test/search if the specify process instance is present
     *
     * @param processDefintionID used to start a processInstance
     * @return String which contains the status
     * @throws IOException
     */
    public String searchProcessInstanceByDefintionID(String processDefintionID) throws IOException {
        String url = serviceURL + "query/process-instances";
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity("{\"processDefinitionId\":\""
                                               + processDefintionID
                                               + "\"}", ContentType.APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response = httpClient.execute(httpPost);
        return response.getStatusLine().toString();
    }

    /**
     * This method is used to validate/check if the process instance is present or not
     *
     * @param processDefinitionID used to identify the process instance
     * @return a String value of the status
     * @throws IOException
     * @throws JSONException
     */
    public String validateProcessInstanceById(String processDefinitionID)
            throws IOException, JSONException, RestClientException {
        String url = serviceURL + "runtime/process-instances";
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        JSONArray data = jsonResponseObject.getJSONArray("data");
        int responseObjectSize = Integer.parseInt(jsonResponseObject.get("total").toString());
        for (int j = 0; j < responseObjectSize; j++) {
            if (data.getJSONObject(j).getString("processDefinitionId").equals(processDefinitionID)) {
                return AVAILABLE;
            }
        }
        throw new RestClientException(NOT_AVAILABLE);
    }

    /**
     * Method use to instantiate a process instance using the definition ID
     *
     * @param processDefintionID used to start a processInstance
     * @throws IOException
     * @throws JSONException
     * @returns String Array which contains status and processInstanceID
     */
    public String[] startProcessInstanceByDefintionID(String processDefintionID)
            throws IOException, JSONException, RestClientException {
        String url = serviceURL + "runtime/process-instances";
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity("{\"processDefinitionId\":\""
                                               + processDefintionID + "\"}",
                                               ContentType.APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response = httpClient.execute(httpPost);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        if (status.contains(Integer.toString(HttpStatus.SC_CREATED)) || status.contains(Integer.toString(HttpStatus.SC_OK))) {
            String processInstanceID = jsonResponseObject.getString(ID);
            return new String[]{status, processInstanceID};
        }
        throw new RestClientException("Cannot Find Process Instance");
    }

    /**
     * Method used to activate a process instance
     *
     * @param processDefintionID used to identify the process instance to activate
     * @throws IOException
     */
    public String activateProcessInstanceById(String processDefintionID) throws IOException {
        String url = serviceURL + "runtime/process-instances/" + processDefintionID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpPut httpPut = new HttpPut(url);
        StringEntity params = new StringEntity("{\"action\":\"activate\"}",
                                               ContentType.APPLICATION_JSON);
        httpPut.setEntity(params);
        HttpResponse response = httpClient.execute(httpPut);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * Method used to suspend a process instance
     *
     * @param processInstanceID used to identify the process instance to suspend
     * @return String array containing the status and the current state
     * @throws IOException
     * @throws JSONException
     */
    public String[] suspendProcessInstanceById(String processInstanceID)
            throws IOException, JSONException, RestClientException {
        String url = serviceURL + "runtime/process-instances/" + processInstanceID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpPut httpPut = new HttpPut(url);
        StringEntity params = new StringEntity("{\"action\":\"suspend\"}",
                                               ContentType.APPLICATION_JSON);
        httpPut.setEntity(params);
        HttpResponse response = httpClient.execute(httpPut);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        if (status.contains(Integer.toString(HttpStatus.SC_CREATED)) || status.contains(Integer.toString(HttpStatus.SC_OK))) {
            String state = jsonResponseObject.getString("suspended");
            return new String[]{status, state};
        }
        throw new RestClientException("Cannot Suspend Process");
    }

    /**
     * Method is used to find thw suspended state of a process instance
     *
     * @param processInstanceID used to identify the process instance
     * @return String containing the suspended state of the process instance
     * @throws IOException
     * @throws JSONException
     */
    public String getSuspendedStateOfProcessInstanceByID(String processInstanceID)
            throws IOException, JSONException {
        String url = serviceURL + "runtime/process-instances/" + processInstanceID;
        String responseData;
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        responseData = EntityUtils.toString(response.getEntity());
        JSONObject resObj = new JSONObject(responseData);
        return resObj.getString("suspended");
    }

    /**
     * Method to get the value of a variable in the process instance
     *
     * @param processInstanceId To identify the process instance
     * @param variable          to identify the variable name
     * @return String Array containing status, name and the value of the variable
     * @throws IOException
     * @throws JSONException
     */
    public String[] getValueOfVariableOfProcessInstanceById(String processInstanceId,
                                                            String variable)
            throws IOException, JSONException {
        String url = serviceURL + "runtime/process-instances/"
                     + processInstanceId + "/variables/" + variable;
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject resObj = new JSONObject(responseData);
        String status = response.getStatusLine().toString();
        String name = resObj.getString(NAME);
        String value = resObj.getString("value");
        return new String[]{status, name, value};
    }

    /**
     * Method used to remove/delete a process instance
     *
     * @param processInstanceId used to identify a process instance
     * @return String value containing the status of the request.
     * @throws IOException
     */
    public String deleteProcessInstanceByID(String processInstanceId) throws IOException {
        String url = serviceURL + "runtime/process-instances/"
                     + processInstanceId;
        DefaultHttpClient httpClient = getHttpClient();
        HttpDelete httpDelete = new HttpDelete(url);
        HttpResponse response = httpClient.execute(httpDelete);
        return response.getStatusLine().toString();
    }

    /**
     * Method used to get the delegation state of a task
     *
     * @param taskID used to identify the task
     * @return String which contains the state of the task
     * @throws IOException
     * @throws JSONException
     */
    public String getDelegationsStateByTaskId(String taskID) throws IOException, JSONException {
        String url = serviceURL + "runtime/tasks/" + taskID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject resObj = new JSONObject(responseData);
        return resObj.getString("delegationState");
    }

    /**
     * Method used to resolve a task
     *
     * @param taskID used to identify the task
     * @return String which contains the status of the request
     * @throws IOException
     */
    public String resolveTaskByTaskId(String taskID) throws IOException {
        String url = serviceURL + "runtime/tasks/" + taskID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity("{\"action\" : \"resolve\"}",
                                               ContentType.APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response = httpClient.execute(httpPost);
        return response.getStatusLine().toString();
    }

    /**
     * Method to get the asignee of a task
     *
     * @param taskID used to identify the task
     * @return String containing the Asignee
     * @throws IOException
     * @throws JSONException
     */
    public String getAssigneeByTaskId(String taskID) throws IOException, JSONException {
        String url = serviceURL + "runtime/tasks/" + taskID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject resObj = new JSONObject(responseData);
        return resObj.getString("assignee");
    }

    /**
     * This method is used to get the comment by comment id and task id
     *
     * @param taskID    used to identify the task where the comment is made
     * @param commentID used to identify the comment uniquely
     * @return String containing the comment
     * @throws IOException
     * @throws JSONException
     */
    public String getCommentByTaskIdAndCommentId(String taskID, String commentID)
            throws IOException, JSONException {
        String url = serviceURL + "runtime/tasks/" + taskID + "/comments/" + commentID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject resObj = new JSONObject(responseData);
        return resObj.getString("message");
    }

    /**
     * Method used to find task by using the process instance ID
     *
     * @param processInstanceId used to identify task through a process instance
     * @return String Array containing status and the taskID
     * @throws IOException
     * @throws JSONException
     */
    public String[] findTaskIdByProcessInstanceID(String processInstanceId)
            throws IOException, JSONException {
        String url = serviceURL + "runtime/tasks";
        String taskId = "";
        DefaultHttpClient httpClient = getHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        JSONArray data = jsonResponseObject.getJSONArray("data");
        int responseObjectSize = Integer.parseInt(jsonResponseObject.get("total")
                                                          .toString());
        for (int j = 0; j < responseObjectSize; j++) {
            if (data.getJSONObject(j).getString("processInstanceId").equals(
                    processInstanceId)) {
                taskId = data.getJSONObject(j).getString(ID);
            }
        }
        return new String[]{status, taskId};
    }

    /**
     * Method to claim task by a user
     *
     * @param taskID used to identify the task to be claimed
     * @return String Array containing status
     * @throws IOException
     */
    public String claimTaskByTaskId(String taskID) throws IOException {
        String url = serviceURL + "runtime/tasks/" + taskID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity("{\"action\" : \"claim\"," +
                                               "\"assignee\" :\"" + USER_CLAIM + "\"}",
                                               ContentType.APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response = httpClient.execute(httpPost);
        return response.getStatusLine().toString();
    }

    /**
     * Mehtod to delegate a task to certain user
     *
     * @param taskID used to identify the task to be delegated
     * @return String with the status of the delegation
     * @throws IOException
     */
    public String delegateTaskByTaskId(String taskID) throws IOException {
        String url = serviceURL + "runtime/tasks/" + taskID;
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity("{\"action\" : \"delegate\"," +
                                               "\"assignee\" :\"" + USER_DELEGATE + "\"}",
                                               ContentType.APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response;
        response = httpClient.execute(httpPost);
        return response.getStatusLine().toString();
    }

    /**
     * Method used to add a new comment to a task
     *
     * @param taskID  used to identify the task
     * @param comment comment to be added
     * @return String Array containing status and the message
     * @throws IOException
     * @throws JSONException
     */
    public String[] addNewCommentOnTaskByTaskId(String taskID, String comment)
            throws RestClientException, IOException, JSONException {
        String url = serviceURL + "runtime/tasks/" + taskID + "/comments";
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity("{\"message\" : \"" + comment
                                               + "\",\"saveProcessInstanceId\" : true}",
                                               ContentType.APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response = httpClient.execute(httpPost);
        String status = response.getStatusLine().toString();
        String responseData = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponseObject = new JSONObject(responseData);
        if (status.contains(Integer.toString(HttpStatus.SC_CREATED)) || status.contains(Integer.toString(HttpStatus.SC_OK))) {
            String message = jsonResponseObject.getString("message");
            String commentID = jsonResponseObject.getString(ID);
            return new String[]{status, message, commentID};
        } else {
            throw new RestClientException("Cannot Add Comment");
        }
    }

    /**
     * Method to delete a task
     *
     * @param taskId         used to identify a task
     * @param cascadeHistory boolean to either delete the task history or not
     * @param deleteReason   reason for deleteing the task
     * @return String containing the status of the request
     * @throws IOException
     */
    public String deleteTaskByTaskId(String taskId, boolean cascadeHistory, String deleteReason)
            throws IOException {
        String url = serviceURL + "runtime/tasks/"
                     + taskId + "?cascadeHistory=" + cascadeHistory + "&deleteReason="
                     + deleteReason;
        DefaultHttpClient httpClient = getHttpClient();
        HttpDelete httpDelete = new HttpDelete(url);
        HttpResponse response = httpClient.execute(httpDelete);
        return response.getStatusLine().toString();
    }


    private DefaultHttpClient getHttpClient() {
        HttpHost target = new HttpHost(hostname, port, "http");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials
                (new AuthScope(target.getHostName(), target.getPort()),
                 new UsernamePasswordCredentials(USERNAME, PASSWORD));
        return httpClient;
    }
}
