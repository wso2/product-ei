/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.samples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservice.integration.test.requestBox.RequestBoxJsonTestCase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains test cases to check the json functionality in sending batch request and single request to
 * data service without wrapping the json payload with _postemployee or _postemployee_batch_req
 * ie: the request patWithOptionalParameterh + request method.
 *
 * Contains test cases to check the functionality of optional attribute in query-param, where certain elements in
 * defined in the query can be inserted or removed as per requirement of the user.
 */
public class JSONPayloadSampleTestCase extends DSSIntegrationTest {

    private final String serviceName = "JSONSampleTest";
    private String serviceEndPoint;
    private static final Log log = LogFactory.getLog(RequestBoxJsonTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
                "rdbms" + File.separator + "h2" + File.separator +
                "JSONSampleTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = "wso2.dss", description = "Invoking POST Request with JSON payload without postemployee tag")
    public void performJsonPostRequest() {

        String postInsertPayload = "{\n" +
                "\"employee\":{\n" +
                "\"employeeNumber\":52,\n" +
                "\"lastName\":\"Karunaratne\",\n" +
                "\"firstName\":\"Sangeeth\",\n" +
                "\"email\":\"sangeeth@wso2.com\",\n" +
                "\"salary\":18400.00\n" +
                "}\n" +
                "}";
        getHttpResponse(serviceEndPoint + "employee", "POST", postInsertPayload);
        String response = getHttpResponse(serviceEndPoint + "employee/52", "GET", null);
        Assert.assertTrue(response.contains(
                "{\"employees\":{\"employee\":[{\"lastName\":\"Karunaratne\",\"firstName\":\"Sangeeth\"," +
                        "\"salary\":18400.0}]}}"), "POST method failed");
    }

    @Test(groups = "wso2.dss", description = "Invoking BATCH request with JSON payload with out " +
            "_postemployee_batch_req and _postemployee tags ")
    public void performJsonBatchRequest() {

        String postInsertPayload = "{\n" +
                "\"employees\":{\n" +
                "\"employee\":[\n" +
                "{\n" +
                "\"employeeNumber\" :53,\n" +
                "\"lastName\": \"Sangeeth\",\n" +
                "\"firstName\": \"Karunaratne\",\n" +
                "\"email\": \"sangeeth@wso2.com\",\n" +
                "\"salary\": 18400.00 \n" +
                "},\n" +
                "{\n" +
                "\"employeeNumber\" :101,\n" +
                "\"lastName\": \"Smitth\",\n" +
                "\"firstName\": \"Will\",\n" +
                "\"email\": \"will@smith.com\",\n" +
                "\"salary\": 15500.00\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "}";
        getHttpResponse(serviceEndPoint + "employee_batch_req", "POST", postInsertPayload);
        String response_employee1 = getHttpResponse(serviceEndPoint + "employee/53", "GET", null);
        String response_employee2 = getHttpResponse(serviceEndPoint + "employee/101", "GET", null);
        Assert.assertTrue(response_employee1.contains("{\"employees\":{\"employee\":[{\"lastName\":\"Sangeeth\"," +
                "\"firstName\":\"Karunaratne\",\"salary\":18400.0}]}}"),"Batch request POST method failed");
        Assert.assertTrue(response_employee2.contains("{\"employees\":{\"employee\":[{\"lastName\":\"Smitth\"," +
                "\"firstName\":\"Will\",\"salary\":15500.0}]}}"),"Batch request POST method failed");
    }

    @Test(groups = "wso2.dss", description = "Invoking PUT Request without optional fields in JSON payload")
    public void performJsonPutRequestWithoutOptionaParameter() {

        String payload = "{\n" +
                "\"employee\":{\n" +
                "\"employeeNumber\":52,\n" +
                "\"salary\":21400.00\n" +
                "}\n" +
                "}";
        getHttpResponse(serviceEndPoint + "employee", "PUT", payload);
        String response = getHttpResponse(serviceEndPoint + "employee/52", "GET", null);
        Assert.assertTrue(response.contains(
                "{\"employees\":{\"employee\":[{\"lastName\":\"Karunaratne\",\"firstName\":\"Sangeeth\"," +
                        "\"salary\":21400.0}]}}"), "Put method without optional parameters failed");
    }

    @Test(groups = "wso2.dss", description = "Invoking PUT Request with optional fields in JSON payload as " +
            "declared in the update query")
    public void performJsonPutRequestWithOptionalParameter() {

        String payload = "{\n" +
                "\"employee\":{\n" +
                "\"employeeNumber\":52,\n" +
                "\"firstName\":\"Sangeeth\",\n" +
                "\"email\":\"sangeeth@wso2.com\",\n" +
                "\"salary\":18400.00\n" +
                "}\n" +
                "}";
        getHttpResponse(serviceEndPoint + "employee", "PUT", payload);
        String response = getHttpResponse(serviceEndPoint + "employee/52", "GET", null);
        Assert.assertTrue(response.contains("{\"employees\":{\"employee\":[{\"lastName\":\"Karunaratne\"," +
                "\"firstName\":\"Sangeeth\",\"salary\":18400.0}]}}"), "Put method with optional parameters failed");
    }

    private String getHttpResponse(String endpoint, String requestMethod, String payload) {

        StringBuilder jsonString = new StringBuilder();
        BufferedReader br = null;
        HttpURLConnection connection = null;
        try {
            String line;
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("charset", "UTF-8");
            connection.setReadTimeout(10000);
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Accept", "application/json");
            if (null != payload) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(payload.length()));
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.write(payload.getBytes());
                writer.close();
            }
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while (null != (line = br.readLine())) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            log.error("IO exception occurred, " + e.getMessage(), e);
        } finally {
            try {
                if (null != br) {
                    br.close();
                    connection.disconnect();
                }
            } catch (IOException e) {
                log.error("IO exception occurred while closing the reader, " + e.getMessage(), e);
            }
        }
        return jsonString.toString();
    }

}
