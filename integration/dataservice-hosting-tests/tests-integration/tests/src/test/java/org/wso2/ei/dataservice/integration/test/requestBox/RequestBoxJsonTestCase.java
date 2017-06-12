/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.requestBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;


/**
 * This class contains test cases to check the json functionality in request box operation.
 */
public class RequestBoxJsonTestCase extends DSSIntegrationTest {
    private final String serviceName = "JSONSample";
    private String serviceEndPoint;

    private static final Log log = LogFactory.getLog(RequestBoxJsonTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        sqlFileLis.add(selectSqlFile("Offices.sql"));
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
                "rdbms" + File.separator + "h2" + File.separator +
                "JSONSample.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = "wso2.dss", description = "Invoking Insert Request with POST method with request_box response")
    public void performJsonInsertWithRequestBoxResponseTest() {
        String postInsertPaymentPayload =
                "{\"request_box\" : {\n" + "\t\"_postemployee\": {\n" + "    \"employeeNumber\" : 14001,\n"
                        + "    \"lastName\": \"Smith\",\n" + "    \"firstName\": \"Will\",\n"
                        + "    \"email\": \"will@google.com\",\n" + "    \"salary\": 15500.0\n" + "  }\n" + "}\n" + "}";
        String response = getHttpResponse(serviceEndPoint + "request_box", "POST", postInsertPaymentPayload);
        Assert.assertTrue(response.contains("DATA_SERVICE_REQUEST_BOX_RESPONSE"), "POST method failed");
    }

    @Test(groups = "wso2.dss", description = "Invoking Insert Request with POST method with request_box response",
            dependsOnMethods = "performJsonInsertWithRequestBoxResponseTest")
    public void performJsonInsertAndUpdateWithRequestBoxResponseTest() {
        String postInsertPaymentPayload =
                "{\"request_box\" : {\n" + "\t\"_postemployee\": {\n" + "    \"employeeNumber\" : 14002,\n"
                        + "    \"lastName\": \"Smith\",\n" + "    \"firstName\": \"Will\",\n"
                        + "    \"email\": \"will@google.com\",\n" + "    \"salary\": 15500.0\n"
                        + "  }, \"_putemployee\": {\n" + "    \"employeeNumber\" : 14001,\n"
                        + "    \"lastName\": \"Gunasekara\",\n" + "    \"firstName\": \"Madhawa\",\n"
                        + "    \"email\": \"madhawa30@gmail.com\",\n" + "    \"salary\": 78500.0\n"
                        + "  },  \"_getemployee_employeenumber\" : {\n" + "  \t\"employeeNumber\": 14001\n" + "  }\n"
                        + "}\n" + "}";
        String response = getHttpResponse(serviceEndPoint + "request_box", "POST", postInsertPaymentPayload);
        Assert.assertTrue(response.contains(
                "{\"DATA_SERVICE_REQUEST_BOX_RESPONSE\":{\"employees\":{\"employee\":[{\"lastName\":\"Gunasekara\","
                        + "\"firstName\":\"Madhawa\",\"salary\":78500.0}]}}}"),
                "POST method failed");
    }

    private String getHttpResponse(String endpoint, String requestMethod, String payload) {
        StringBuilder jsonString = new StringBuilder();
        BufferedReader br = null;
        try {
            String line;
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("charset", "UTF-8");
            connection.setReadTimeout(10000);
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Accept", "application/json");
            if (null != payload) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(payload.length()));
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();
            }
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while (null != (line = br.readLine())) {
                jsonString.append(line);
            }
            connection.disconnect();
        } catch (IOException e) {
            log.error("IO exception occurred, " + e.getMessage(), e);
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("IO exception occurred while closing the reader, " + e.getMessage(), e);
            }
        }
        return jsonString.toString();
    }

}
