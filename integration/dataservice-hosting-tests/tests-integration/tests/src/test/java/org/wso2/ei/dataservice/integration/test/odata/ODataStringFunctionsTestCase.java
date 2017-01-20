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

import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendGET;

public class ODataStringFunctionsTestCase extends DSSIntegrationTest {
    private final String serviceName = "ODataQuerySampleService";
    private final String configId = "default";
    private String webAppUrl;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateODataTables.sql"));
        sqlFileLis.add(selectSqlFile("Customers.sql"));
        sqlFileLis.add(selectSqlFile("FIlesWithFIlesRecords.sql"));
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
                                                  "odata" + File.separator + "ODataQuerySampleService.dbs",
                                                  sqlFileLis));
        webAppUrl = dssContext.getContextUrls().getWebAppURL();
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = "wso2.dss", description = "select query test")
    public void validateContainsFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=contains(CUSTOMERNAME,%27Dragon%27)%20eq%20true";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
        Assert.assertTrue(!response[1].toString().contains("FILENAME"));
    }

    @Test(groups = "wso2.dss", description = "select query test")
    public void validateEndsWithFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=endswith(CUSTOMERNAME,%27Stores%27)%20eq%20true";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
    }

    @Test(groups = "wso2.dss", description = "select query test")
    public void validateStartsWithFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=startswith(CUSTOMERNAME,%27Gift%27)%20eq%20true";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
    }

    @Test(groups = "wso2.dss", description = "select query test")
    public void validateLengthFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=length(CUSTOMERNAME)%20eq%2012";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
    }

    @Test(groups = "wso2.dss", description = "select query test")
    public void validateIndexOfFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=indexof(CUSTOMERNAME,%27Gift%27)%20eq%201";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
    }


    @Test(groups = "wso2.dss", description = "select query test")
    public void validateToLowerFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=tolower(CUSTOMERNAME)%20eq%20%27signal%20gift%20stores%27";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
        Assert.assertTrue(!response[1].toString().contains("FILENAME"));
    }

    @Test(groups = "wso2.dss", description = "select query test")
    public void validateToUpperFunctionTestCase() throws Exception {
        String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=toupper(CUSTOMERNAME)%20eq%20%27SIGNAL%20GIFT%20STORES%27";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = sendGET(endpoint, headers);
        Assert.assertEquals(response[0], ODataTestUtils.OK);
        Assert.assertTrue(!response[1].toString().contains("FILENAME"));
    }
}
