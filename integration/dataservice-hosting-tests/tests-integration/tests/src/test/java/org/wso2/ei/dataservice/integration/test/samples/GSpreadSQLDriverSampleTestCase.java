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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class GSpreadSQLDriverSampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(GSpreadSQLDriverSampleTestCase.class);

    private final String serviceName = "GSpreadSQLDriverSample";
    private String serverEpr;
    private String inputValue = String.valueOf(System.currentTimeMillis());


    @Factory(dataProvider = "userModeDataProvider")
    public GSpreadSQLDriverSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }


    @BeforeClass(alwaysRun = true, enabled = false)
    public void initialize() throws Exception {
        super.init(userMode);
        String resourceFileLocation;
        serverEpr = getServiceUrlHttp(serviceName);
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "gspread" + File.separator +
                                              "GSpreadSQLDriverSample.dbs")));
        log.info(serviceName + " uploaded");
    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not", enabled = false)
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }



    private OMElement addEmployeePayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/" +
                                                 "gspread_sql_driver_sample_service", "gsp");
        OMElement addCustomerSQL = fac.createOMElement("addCustomerSQL", omNs);
        OMElement customerNumber = fac.createOMElement("customerNumber", omNs);
        OMElement customerName = fac.createOMElement("customerName", omNs);
        OMElement contactLastName = fac.createOMElement("contactLastName", omNs);
        OMElement contactFirstName = fac.createOMElement("contactFirstName", omNs);
        OMElement phone = fac.createOMElement("phone", omNs);
        OMElement addressLine1 = fac.createOMElement("addressLine1", omNs);
        OMElement addressLine2 = fac.createOMElement("addressLine2", omNs);
        OMElement city = fac.createOMElement("city", omNs);
        OMElement state = fac.createOMElement("state", omNs);
        OMElement postalCode = fac.createOMElement("postalCode", omNs);
        OMElement country = fac.createOMElement("country", omNs);
        OMElement salesRepEmployeeNumber = fac.createOMElement("salesRepEmployeeNumber", omNs);
        OMElement creditLimit = fac.createOMElement("creditLimit", omNs);

        customerNumber.setText(inputValue);
        customerName.setText(inputValue);
        contactLastName.setText(inputValue);
        contactFirstName.setText(inputValue);
        phone.setText(inputValue);
        addressLine1.setText(inputValue);
        addressLine2.setText(inputValue);
        city.setText(inputValue);
        state.setText(inputValue);
        postalCode.setText(inputValue);
        salesRepEmployeeNumber.setText(inputValue);
        country.setText(inputValue);
        creditLimit.setText(inputValue);

        addCustomerSQL.addChild(customerNumber);
        addCustomerSQL.addChild(customerName);
        addCustomerSQL.addChild(contactLastName);
        addCustomerSQL.addChild(contactFirstName);
        addCustomerSQL.addChild(phone);
        addCustomerSQL.addChild(addressLine1);
        addCustomerSQL.addChild(addressLine2);
        addCustomerSQL.addChild(city);
        addCustomerSQL.addChild(state);
        addCustomerSQL.addChild(postalCode);
        addCustomerSQL.addChild(salesRepEmployeeNumber);
        addCustomerSQL.addChild(country);
        addCustomerSQL.addChild(creditLimit);

        return addCustomerSQL;

    }

    private OMElement deleteCustomer() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/" +
                                                 "gspread_sql_driver_sample_service", "gsp");
        OMElement deleteCustomerSQL = fac.createOMElement("deleteCustomerSQL", omNs);
        OMElement customerNumber = fac.createOMElement("customerNumber", omNs);

        customerNumber.setText(inputValue);
        deleteCustomerSQL.addChild(customerNumber);
        return deleteCustomerSQL;
    }

    @Test(groups = "wso2.dss", description = "add customer", dependsOnMethods = "testServiceDeployment", enabled = false)
    public void testAddEmployee() throws RemoteException {
        new AxisServiceClient().sendRobust(addEmployeePayload(), serverEpr, "addCustomerSQL");
        OMElement result = new AxisServiceClient().sendReceive(getPayload(), serverEpr, "getCustomersSQL");
        assertTrue(result.toString().contains("<customerName>" + inputValue + "</customerName>"));
    }

    @Test(groups = "wso2.dss", description = "Delete customer", dependsOnMethods = "testAddEmployee", enabled = false)
    public void testDeleteCustomer() throws RemoteException {
        new AxisServiceClient().sendRobust(deleteCustomer(), serverEpr, "deleteCustomerSQL");
        OMElement result = new AxisServiceClient().sendReceive(getPayload(), serverEpr, "getCustomersSQL");
        assertFalse(result.toString().contains("<customerName>" + inputValue + "</customerName>"));
    }

    private OMElement getPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/gspread_sql_driver_sample_service", "gsp");
        return fac.createOMElement("getCustomersSQL", omNs);
    }

    @AfterClass(alwaysRun = true, enabled = false)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
        log.info(serviceName + " deleted");
    }

}
