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
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DS1209EqualElementNamesInJSONHierarchyTestCase extends DSSIntegrationTest {
    private String serviceEndPoint;
    private final String serviceName = "EqualNamedJSONElements";
    private SimpleHttpClient client;
    Map<String, String> headers;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileList = new ArrayList<>();
        sqlFileList.add(selectSqlFile("CreateTables.sql"));
        sqlFileList.add(selectSqlFile("Students.sql"));
        client = new SimpleHttpClient();
        headers = new HashMap<>();

        deployService(serviceName, createArtifact(
                getResourceLocation() + File.separator + "samples" + File.separator + "dbs" + File.separator + "rdbms"
                        + File.separator + "EqualNamedJSONElements.dbs", sqlFileList));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @Test(groups = "wso2.dss",
            description = "Sending a GET request expecting a response with more than one records")
    public void sendGetRequest() throws Exception {
        headers.put("Accept", "application/json");
        org.apache.http.HttpResponse response = client.doGet(serviceEndPoint + "getstudent", headers);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

}
