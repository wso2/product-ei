/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/CARBON-15046
 * Retriving null values on GET in JSON objects
 */
public class CARBON15046JsonGsonNUllValueTest extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON15046JsonGsonNUllValueTest.class);

    private final String serviceName = "JsonNullValueOnGET";
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("Birds.sql"));
        deployService(serviceName, createArtifact(
                getResourceLocation() + File.separator + "samples" + File.separator + "dbs" + File.separator + "rdbms"
                        + File.separator + "JsonNullValueOnGET.dbs", sqlFileLis));

        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = { "wso2.dss" }, description = "Check whether the null received successfully", alwaysRun = true)
    public void returnNullValueInJsonObjectsOnGETTestCase() throws Exception {
        HttpResponse response = this.getHttpResponse(serviceEndPoint + "_getbird", "application/json");
        JSONObject result = new JSONObject(response.getData());
        assertNotNull(result, "Response is null");
        //Response JSON object will be {"Birds":{"Bird":[{"weight":"30","color":null,"name":"Bird1"}]}}
        JSONArray bidsList = (JSONArray) ((JSONObject) result.get("Birds")).get("Bird");
        JSONObject birdDetails = (JSONObject) bidsList.get(0);
        assertTrue(birdDetails.isNull("color"), "Null value retrieved successful");
        log.info("Null value retrieved from GET successful");
    }

    /**
     * This method will "Accept" header Types "application/json", etc..
     * @param endpoint service endpoint
     * @param contentType header type
     * @return HttpResponse
     * @throws Exception
     */
    private HttpResponse getHttpResponse(String endpoint, String contentType) throws Exception {

        if (endpoint.startsWith("http://")) {
            String urlStr = endpoint;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", contentType);
            conn.setRequestProperty("charset", "UTF-8");
            conn.setReadTimeout(10000);
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
            } catch (FileNotFoundException ignored) {
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
