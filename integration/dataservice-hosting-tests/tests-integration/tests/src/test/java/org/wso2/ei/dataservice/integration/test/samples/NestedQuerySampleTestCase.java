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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.dataservices.samples.nested_query_sample.DataServiceFault;
import org.wso2.carbon.dataservices.samples.nested_query_sample.NestedQuerySampleStub;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ws.dataservice.samples.nested_query_sample.Office;
import org.wso2.ws.dataservice.samples.nested_query_sample.Order;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

public class NestedQuerySampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(NestedQuerySampleTestCase.class);
    private final String serviceName = "NestedQuerySample";
    private NestedQuerySampleStub stub;


    @Factory(dataProvider = "userModeDataProvider")
    public NestedQuerySampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init(userMode);
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + getResourceLocation() + File.separator + "samples"
                                              + File.separator + "dbs" + File.separator
                                              + "rdbms" + File.separator + "NestedQuerySample.dbs")));
        stub = new NestedQuerySampleStub(getServiceUrlHttp(serviceName));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }


    @Test(groups = {"wso2.dss"})
    public void getCustomerOrdersNestedQuery() throws RemoteException, DataServiceFault {
        for (int i = 1; i < 6; i++) {
            Order[] orders = stub.customerOrders();
            Assert.assertNotNull(orders, "Orders Object null");
            Assert.assertTrue(orders.length > 0, "No Order found");
        }
        log.info("Customer Orders Nested Query verified");
    }


    @Test(groups = {"wso2.dss"})
    public void listOfficeNestedQueryOperation() throws RemoteException, DataServiceFault {
        for (int i = 1; i < 6; i++) {
            Office[] offices = stub.listOffices();
            Assert.assertNotNull(offices, "Offices Object null");
            Assert.assertTrue(offices.length > 0, "No office found");
        }
        log.info("List Office Nested Query verified");
    }

}
