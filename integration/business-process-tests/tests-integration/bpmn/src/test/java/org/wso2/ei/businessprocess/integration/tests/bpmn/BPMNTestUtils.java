/*
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.businessprocess.integration.tests.bpmn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;

import java.io.IOException;
import java.nio.charset.Charset;

public class BPMNTestUtils {

    public static final String ADMIN = "admin";
    private static final Log log = LogFactory.getLog(BPMNTestUtils.class);
    private static final String BACKEND_URL_SUFFIX = "services";
    private static final String BPMN_REST_URL_SUFFIX = "bpmn";
    private static HttpClient client;
    private static HttpGet request;
    private static HttpPost post;
    private static HttpPut put;


    /**
     * BPMN does not expose any service to check deployment is done.
     * So we are using deployment count to check completeness of process deployment
     *
     * @param workflowServiceClient
     * @param bpmnPackageName
     * @param previousDeploymentCount
     * @throws Exception
     */
    public static void waitForProcessDeployment(WorkflowServiceClient workflowServiceClient, String bpmnPackageName,
                                                int previousDeploymentCount) throws Exception {
        int serviceTimeOut = 0;

        while (true) {
            if (workflowServiceClient.getDeployments() != null && workflowServiceClient.getDeployments().length >
                    previousDeploymentCount) {
                return;
            }
            if (serviceTimeOut == 0) {
                log.info("Waiting for BPMN package" + bpmnPackageName + " to deploy.");
            } else if (serviceTimeOut > BPMNTestConstants.PROCESS_DEPLOYMENT_MAX_RETRY_COUNT) {
                String errMsg = bpmnPackageName + " package is not found";
                log.error(errMsg);
                throw new Exception(errMsg);
            }
            try {
                serviceTimeOut++;
                Thread.sleep(BPMNTestConstants.PROCESS_DEPLOYMENT_WAIT_TIME_PER_RETRY);
            } catch (InterruptedException e) {
                //Log the interrupt exception and wait for process deployment until timeout
                log.error("Error while waiting for process deployment " + e);
            }
        }
    }

    /**
     * Returns BPMN rest endpoint from bps backend url
     *
     * @param backEndUrl
     * @return bpmnUrl
     */
    public static String getRestEndPoint(String backEndUrl) {
        return backEndUrl.replaceFirst(BACKEND_URL_SUFFIX, BPMN_REST_URL_SUFFIX);
    }

    /**
     * Returns HTTP GET response entity string from given url using admin credentials
     *
     * @param url request url suffix
     * @return HttpResponse from get request
     */
    public static String getRequest(String url) throws IOException {

        String restUrl = getRestEndPoint(url);
        log.info("Sending HTTP GET request: " + restUrl);
        client = new DefaultHttpClient();
        request = new HttpGet(restUrl);

        request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("admin", "admin"),
                Charset.defaultCharset().toString(), false));
        client.getConnectionManager().closeExpiredConnections();
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * Returns HTTP GET response from given url using admin credentials
     *
     * @param url request url suffix
     * @return
     * @throws IOException
     */
    public static HttpResponse getRequestResponse(String url) throws IOException {

        String restUrl = getRestEndPoint(url);
        log.info("Sending HTTP GET request: " + restUrl);
        client = new DefaultHttpClient();
        request = new HttpGet(restUrl);

        request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("admin", "admin"),
                Charset.defaultCharset().toString(), false));
        client.getConnectionManager().closeExpiredConnections();
        HttpResponse response = client.execute(request);
        return response;
    }

    /**
     * Returns response after the given POST request
     *
     * @param url     string url suffix to post the request
     * @param payload request payload
     * @return HttpResponse for the post request
     * @throws IOException
     */
    public static HttpResponse postRequest(String url, JSONObject payload) throws IOException {
        return doPost(url, payload, ADMIN, ADMIN);
    }

    /**
     * Returns response after the given POST request
     *
     * @param url          string url suffix to post the request
     * @param payloadArray request payload
     * @return HttpResponse for the post request
     * @throws IOException
     */
    public static HttpResponse postRequest(String url, JSONArray payloadArray) throws IOException, JSONException {
        return doPost(url, payloadArray, ADMIN, ADMIN);
    }

    public static HttpResponse doPost(String url, Object json, String user, String password) throws IOException {
        String restUrl = getRestEndPoint(url);
        log.info("Sending HTTP POST request: " + restUrl);
        client = new DefaultHttpClient();
        post = new HttpPost(restUrl);
        post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(user, password),
                Charset.defaultCharset().toString(), false));
        post.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));
        client.getConnectionManager().closeExpiredConnections();
        HttpResponse response = client.execute(post);
        return response;
    }

    /**
     * Returns response after the given DELETE request
     *
     * @param url string url suffix to delete the request
     * @return HttpResponse for the post request
     * @throws IOException
     */
    public static HttpResponse deleteRequest(String url) throws IOException {
        String restUrl = getRestEndPoint(url);
        log.info("Sending HTTP DELETE request: " + restUrl);
        HttpClient client = new DefaultHttpClient();
        HttpDelete delete = new HttpDelete(restUrl);
        delete.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("admin", "admin"),
                Charset.defaultCharset().toString(), false));
        client.getConnectionManager().closeExpiredConnections();
        HttpResponse response = client.execute(delete);
        return response;
    }

    /**
     * @param url
     * @param payload
     * @return
     * @throws IOException
     */
    public static HttpResponse putRequest(String url, JSONObject payload) throws IOException {
        return doPut(url, payload, ADMIN, ADMIN);
    }

    /**
     * Function to make http PUT request with JSONArray
     *
     * @param url
     * @param payload
     * @return
     * @throws IOException
     */
    public static HttpResponse putRequest(String url, JSONArray payload) throws IOException {
        return doPut(url, payload, ADMIN, ADMIN);
    }

    public static HttpResponse doPut(String url, Object payload, String user, String password) throws IOException {
        String restUrl = getRestEndPoint(url);
        HttpClient client = new DefaultHttpClient();
        HttpPut put = new HttpPut(restUrl);
        put.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(user, password), "UTF-8", false));
        put.setEntity(new StringEntity(payload.toString(), ContentType.APPLICATION_JSON));
        client.getConnectionManager().closeExpiredConnections();
        HttpResponse response = client.execute(put);
        return response;
    }
}
