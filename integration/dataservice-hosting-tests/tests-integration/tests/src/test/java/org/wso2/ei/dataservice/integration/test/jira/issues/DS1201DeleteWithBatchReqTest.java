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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * This test is to verify the fix for the jira https://wso2.org/jira/browse/DS-1201
 * When we invoke the DELETE endpoint with batchrequests enabled, java.lang.StringIndexOutOfBoundsException is thrown
 */
public class DS1201DeleteWithBatchReqTest extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(DS1201DeleteWithBatchReqTest.class);

    private final String serviceName = "DELETEWithBatchTest";
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTableDeleteTest.sql"));
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

    @Test(groups = {"wso2.dss" }, description = "Check the service returns correct code", alwaysRun = true)
    public void deleteWithBatchReqTestCase() throws Exception {
        HttpResponse response1 = this.getHttpResponse(serviceEndPoint + "filter/" + Integer.toString(2));
        assertTrue(202 == response1.getResponseCode());
    }

    /**
     * private method to call the back end service and get the error response from the server
     *
     * @param endpoint
     * @return
     * @throws Exception
     */
    private HttpResponse getHttpResponse(String endpoint) throws Exception {
        if (endpoint.startsWith("http://")) {
            String urlStr = endpoint;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("charset", "UTF-8");
            connection.setReadTimeout(10000);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");

            connection.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException ignored) {
                rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            return new HttpResponse(sb.toString(), connection.getResponseCode());
        }
        return null;
    }
}
