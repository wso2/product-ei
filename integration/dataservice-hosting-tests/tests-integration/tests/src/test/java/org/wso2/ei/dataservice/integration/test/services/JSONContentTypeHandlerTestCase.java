/*
 *  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.ei.dataservice.integration.test.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.generic.GenericJSONClient;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JSONContentTypeHandlerTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(JSONContentTypeHandlerTestCase.class);
    private final String serviceName = "ResourcesServiceTest";
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                        + "rdbms" + File.separator + "MySql" + File.separator
                        + "ResourcesServiceTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void postRequest() throws Exception {
        this.addProducts();
        log.info("POST Request verified");
    }


    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"postRequest"}, enabled = false)
    public void getAllProductsWithMappedXMLNotation() throws Exception {
        this.listProducts("application/json");
        log.info("GET Request verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"getAllProductsWithMappedXMLNotation"}, enabled = false)
    public void getProductWithMappedXMLNotation() throws Exception {
        this.getProductByCode("N10_1671");
        log.info("GET Request verified");
    }


    private void addProducts() throws Exception {
        GenericJSONClient jsonClient = new GenericJSONClient();
        String queryString = "{\"_postproduct\":{\"productCode\":\"N10_1671\",\"productName\":\"Honda Civic\",\"productLine\":\"Cars\",\"quantityInStock\":\"10\",\"buyPrice\":\"45.54\"}}";
        jsonClient.doPost(this.getServiceEndpoint() + "_postproduct", queryString,
                "application/json");

        queryString = "{\"_postproduct\":{\"productCode\":\"N10_1672\",\"productName\":\"Honda Accord\",\"productLine\":\"Cars\",\"quantityInStock\":\"24\",\"buyPrice\":\"55.64\"}}";
        jsonClient.doPost(this.getServiceEndpoint() + "_postproduct", queryString,
                "application/json");

        queryString = "{\"_postproduct\":{\"productCode\":\"N10_1673\",\"productName\":\"Honda Aria\",\"productLine\":\"Cars\",\"quantityInStock\":\"30\",\"buyPrice\":\"48.57\"}}";
        jsonClient.doPost(this.getServiceEndpoint() + "_postproduct", queryString,
                "application/json");

        queryString = "{\"_postproduct\":{\"productCode\":\"N10_1674\",\"productName\":\"Honda Insight\",\"productLine\":\"Cars\",\"quantityInStock\":\"15\",\"buyPrice\":\"35.55\"}}";
        jsonClient.doPost(this.getServiceEndpoint() + "_postproduct", queryString,
                "application/json");

        queryString = "{\"_postproduct\":{\"productCode\":\"N10_1675\",\"productName\":\"Honda Airwave\",\"productLine\":\"Cars\",\"quantityInStock\":\"20\",\"buyPrice\":\"25.58\"}}";
        jsonClient.doPost(this.getServiceEndpoint() + "_postproduct", queryString,
                "application/json");
    }

    private void listProducts(String contentType) throws Exception {
        GenericJSONClient jsonClient = new GenericJSONClient();
        String expectedResult = "{\"Products\":{\"Product\":[{\"quantityInStock\":\"10\",\"productLine\":\"Cars\",\"productCode\":\"N10_1671\",\"buyPrice\":\"45.54\",\"productName\":\"Honda Civic\"},{\"quantityInStock\":\"24\",\"productLine\":\"Cars\",\"productCode\":\"N10_1672\",\"buyPrice\":\"55.64\",\"productName\":\"Honda Accord\"},{\"quantityInStock\":\"30\",\"productLine\":\"Cars\",\"productCode\":\"N10_1673\",\"buyPrice\":\"48.57\",\"productName\":\"Honda Aria\"},{\"quantityInStock\":\"15\",\"productLine\":\"Cars\",\"productCode\":\"N10_1674\",\"buyPrice\":\"35.55\",\"productName\":\"Honda Insight\"},{\"quantityInStock\":\"20\",\"productLine\":\"Cars\",\"productCode\":\"N10_1675\",\"buyPrice\":\"25.58\",\"productName\":\"Honda Airwave\"}]}}";
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(this.getServiceEndpoint() + "_getproducts", null, "application/json");
        JSONObject result = new JSONObject(response.getData());
        Assert.assertNotNull(result, "Response is null");
        for (int i = 1; i < 6; i++) {
            Assert.assertTrue(expectedResult.contains(result.toString()),
                    "Expected result not found");
        }
    }

    private void getProductByCode(String producCode) throws Exception {
        GenericJSONClient jsonClient = new GenericJSONClient();
        String expectedResult = "{\"_postproduct\":{\"productCode\":\"" + producCode + "\",\"productName\":\"Honda Civic\",\"productLine\":\"Cars\",\"quantityInStock\":\"10\",\"buyPrice\":\"45.54\"}}";
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(this.getServiceEndpoint() + "_getproduct_productcode=","{\"_getproduct_productcode\":{\"productCode\":\"N10_1671\"}}","application/json");
        JSONObject result = new JSONObject(response.getData());
        Assert.assertNotNull(result, "Response is null");
        Assert.assertTrue(expectedResult.equals(result.toString()), "Expected result not found");
    }

    public String getServiceEndpoint() {
        return serviceEndPoint;
    }

}
