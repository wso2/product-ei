/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.ei.dataservice.integration.test.samples;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

public class ResourcesSampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(ResourcesSampleTestCase.class);

    private final String serviceName = "ResourcesSample";
    private String serviceEndPoint;
    private int productId;


    @Factory(dataProvider = "userModeDataProvider")
    public ResourcesSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init(userMode);
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + getResourceLocation() + File.separator + "samples"
                                              + File.separator + "dbs" + File.separator
                                              + "rdbms" + File.separator + "ResourcesSample.dbs")));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
        //to avoid conflict of primary key violation in the database when running user and tenant modes
        if (isTenant()) {
            //add product form 50
            productId = 50;
        } else {
            //add product form 30
            productId = 30;
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"})
    public void postRequest() throws Exception {
        addProduct();
        log.info("POST Request verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"postRequest"})
    public void getRequest() throws Exception {
        listProduct();
        log.info("GET Request verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"getRequest"})
    public void getRequestWithParam() throws Exception {
        OMElement response;
        for (int i = productId; i < productId + 10; i++) {
            response = getProductByCode(i + "");
            Assert.assertTrue(response.toString().contains("<productName>product" + i + "</productName>"), "Expected result not found");
            Assert.assertTrue(response.toString().contains("<productLine>2</productLine>"), "Expected result not found");
        }
        log.info("GET Request with parameter verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"getRequestWithParam"})
    public void putRequest() throws Exception {
        editProduct();
        OMElement response;
        for (int i = productId; i < productId + 10; i++) {
            response = getProductByCode(i + "");
            Assert.assertTrue(response.toString().contains("<productName>product" + i + " edited</productName>"), "Expected result not found");
            Assert.assertTrue(response.toString().contains("<buyPrice>15.0</buyPrice>"), "Expected result not found");
        }
        log.info("PUT Request verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"putRequest"})
    public void deleteRequest() throws Exception {
        deleteProduct();
        log.info("DELETE Request verified");
    }

    private void deleteProduct() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        for (int i = productId; i < productId + 10; i++) {
            httpClient.delete(serviceEndPoint + "product/", "" + i);
        }
    }

    private void listProduct() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint + "_getproducts");
        Assert.assertNotNull(result, "Response null");
        for (int i = productId; i < productId + 10; i++) {
            Assert.assertTrue(result.toString().contains("<productCode>" + i + "</productCode>"), "Expected result not found");
        }
    }

    private void editProduct() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        for (int i = productId; i < productId + 10; i++) {

            String para = "productCode=" + i
                          + "&" + "productName=" + "product" + i + " edited"
                          + "&" + "productLine=2"
                          + "&" + "quantityInStock=200"
                          + "&" + "buyPrice=15";
            httpClient.put(serviceEndPoint + "_putproduct", para);
        }
    }

    private OMElement getProductByCode(String productId) throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();

        return httpClient.get(serviceEndPoint + "product/" + productId);
    }

    private void addProduct() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        for (int i = productId; i < productId + 10; i++) {

            String para = "productCode=" + i
                          + "&" + "productName=" + "product" + i
                          + "&" + "productLine=2"
                          + "&" + "quantityInStock=200"
                          + "&" + "buyPrice=10";
            httpClient.post(serviceEndPoint + "_postproduct", para);
        }
    }
}
