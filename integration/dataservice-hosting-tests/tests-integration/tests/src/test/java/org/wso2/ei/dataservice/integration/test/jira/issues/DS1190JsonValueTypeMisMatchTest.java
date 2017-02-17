/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * This test is to verify the fix for the jira https://wso2.org/jira/browse/DS-1190
 * and https://wso2.org/jira/browse/DS-1192
 * When we invoke the rest endpoint with Numeric, Binary/Blob data types, Exception is thrown.
 */
public class DS1190JsonValueTypeMisMatchTest extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(DS1190JsonValueTypeMisMatchTest.class);

    private final String serviceName = "JsonValueTypeMismatchTest";
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTableJsonTest.sql"));
        deployService(serviceName, createArtifact(
                getResourceLocation() + File.separator + "samples" + File.separator + "dbs" + File.separator + "rdbms"
                        + File.separator + serviceName + ".dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"}, description = "Check whether the service returns with wrong error message", alwaysRun = true)
    public void jsonInputWithWrongValueTypeTestCase() throws Exception {
        HttpResponse response1 = this.getHttpResponse(serviceEndPoint + "_postpersons", "application/json",
                "{\"_postpersons\": {\"PersonID\" : 14,\"LastName\": \"Smith\",\"FirstName\": \"Will\"," +
                        "\"Address\": \"will@google.com\",\"Image\": \"iVBORw0KGgoAAAANSUhEUgAAAyEAAAD\"}}");


        HttpResponse response2 = this.getHttpResponse(serviceEndPoint + "_postpersons", "application/json",
                "{\"_postpersons\": {\"PersonID\" : \"14\",\"LastName\": \"Smith\",\"FirstName\": \"Will\"," +
                        "\"Address\": \"will@google.com\",\"Image\": \"iVBORw0KGgoAAAANSUhEUgAAAyEAAAD\"}}");

        assertTrue(202 == response1.getResponseCode());

        log.info("--------------- Test for json input with decimal/base64Binary parameter types is successfull -----------");

        assertTrue(responseDataEvaluator(response2.getData(), "Value type miss match, Expected value type", "decimal", "STRING"));
        assertTrue(500 == response2.getResponseCode());

        log.info("--------------- Test for json input with wrong parameter types is successfull ----------------------------");
    }

    /**
     * private method to evaluate the error response which came back from the server
     *
     * @param data
     * @param errorMsg
     * @param expected
     * @param found
     * @return
     * @throws Exception
     */
    private boolean responseDataEvaluator(String data, String errorMsg, String expected, String found) throws Exception {
        String result[] = data.split("java.lang.IllegalArgumentException:");
        String errorMessage[] = result[1].split("-");
        String msg = errorMessage[0].trim();
        if (!msg.equals(errorMsg)) {
            return false;
        }
        String realExpected = errorMessage[1].split("'")[1].trim();
        if (!realExpected.equals(expected)) {
            return false;
        }
        String realFound = errorMessage[2].split("'")[1].trim();
        if (!realFound.equals(found)) {
            return false;
        }
        return true;
    }

    /**
     * private method to call the back end service and get the error response from the server
     *
     * @param endpoint
     * @param contentType
     * @param data
     * @return
     * @throws Exception
     */
    private HttpResponse getHttpResponse(String endpoint, String contentType, String data) throws Exception {
        if (endpoint.startsWith("http://")) {
            String urlStr = endpoint;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Accept", contentType);
            conn.setRequestProperty("charset", "UTF-8");
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.close();
            conn.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException ignored) {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            return new HttpResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }
}
