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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.dataservice.integration.test.odata;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.CREATED;
import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendGET;
import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendPOST;

/**
 * This class contains OData specific test cases. to verify the functionality of odata datatypes.
 */
public class ODataDataTypeSupportTestCase extends DSSIntegrationTest {
    private final String serviceName = "ODataDataTypesSampleService";
    private final String configId = "default";
    private String webAppUrl;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateODataTables.sql"));
        sqlFileLis.add(selectSqlFile("Customers.sql"));
        sqlFileLis.add(selectSqlFile("FIlesWithFIlesRecords.sql"));
        sqlFileLis.add(selectSqlFile("ODataDataTypes.sql"));
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
                                                  "odata" + File.separator + "ODataDataTypesSampleService.dbs",
                                                  sqlFileLis));
        webAppUrl = dssContext.getContextUrls().getWebAppURL();
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = "wso2.dss", description = "Data type retrieval test", dependsOnMethods = "InsertionTestCase")
    public void RetrievalTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/TESTTABLE";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], 200);
    }

    @Test(groups = "wso2.dss", description = "Data type insertion test")
    public void InsertionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/TESTTABLE";
        String content = "{ \n" +
                         "\"ID\" : 1, \n" +
                         "\"TESTSMALLINT\" : 12, \n" +
                         "\"TESTVARCHAR\" : \"SASAS\", \n" +
                         "\"TESTDOUBLE\" : 2121.12121, \n" +
                         "\"TESTBOOLEAN\" : true, \n" +
                         "\"TESTCHAR\" : \"c\", \n" +
                         "\"TESTMEDIUMINT\" : 32, \n" +
                         "\"TESTTINYINT\": 32, \n" +
                         "\"TESTFLOAT\" : 21.21,  \n" +
                         "\"TESTDECIMAL\": 21.33 ,\n" +
                         "\"TESTDATE\" : \"2000-01-01\" ,\n" +
                         "\"TESTTIME\" : \"21:45:00\" , \n" +
                         "\"TESTTIMESTAMP\" : \"2000-01-01T16:00:00.000Z\" ,\n" +
                         "\"TESTBLOB\" : \"T0RhdGE\" \n" +
                         "}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendPOST(endpoint, content, headers);
        Assert.assertEquals(response[0], CREATED);
    }


}
