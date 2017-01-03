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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.dataservices.samples.excel_sample_service.DataServiceFault;
import org.wso2.carbon.dataservices.samples.excel_sample_service.ExcelSampleService;
import org.wso2.carbon.dataservices.samples.excel_sample_service.ExcelSampleServiceStub;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class ExcelSampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(ExcelSampleTestCase.class);

    private final String serviceName = "ExcelSampleService";
    private String serverEpr;


    @Factory(dataProvider = "userModeDataProvider")
    public ExcelSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(userMode);
        String resourceFileLocation;
        serverEpr = getServiceUrlHttp(serviceName);
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "excel" + File.separator +
                                              "ExcelSampleService.dbs")));
        log.info(serviceName + " uploaded");
    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = "testServiceDeployment", description = "invoke excel sample service")
    public void testGetProducts() throws RemoteException, DataServiceFault {

        log.info("Running ExcelSampleServiceTestCase#testGetProducts");
        ExcelSampleService stub = new ExcelSampleServiceStub(serverEpr);
        assertTrue(stub.getProducts().length > 0,
                   "No of products should be greater than zero");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = "testGetProducts", description = "Get product classifications", enabled = false)
    public void testGetProductClassifications() throws RemoteException, DataServiceFault {

        ExcelSampleService stub = new ExcelSampleServiceStub(serverEpr);
        assertTrue(stub.getProductClassifications().length > 0,
                   "No of product classifications should be greater than zero");
    }

    @AfterClass(alwaysRun = true, groups = "wso2.dss", description = "delete service")
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }
}
