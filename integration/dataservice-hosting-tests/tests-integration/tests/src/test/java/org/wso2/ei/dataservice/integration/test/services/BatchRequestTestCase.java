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
package org.wso2.ei.dataservice.integration.test.services;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

public class BatchRequestTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(BatchRequestTestCase.class);

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
    private final String serviceName = "BatchRequestTest";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                                          + "rdbms" + File.separator + "MySql" + File.separator
                                                          + "BatchRequestTest.dbs", sqlFileLis));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"})
    public void insertOperation() throws AxisFault, XPathExpressionException {
        for (int i = 1; i < 6; i++) {
            addEmployee(i);
        }
        for (int i = 1; i < 6; i++) {
            Assert.assertEquals("1", employeeExists(i + ""), "Employee Not Found");
        }
        log.info("Insert Operation verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"insertOperation"})
    public void deleteOperation() throws AxisFault, XPathExpressionException {
        for (int i = 1; i < 6; i++) {
            deleteEmployee(i);
        }
        for (int i = 1; i < 6; i++) {
            Assert.assertEquals("0", employeeExists(i + ""), "Employee Found. deletion failed");
        }
        log.info("Delete Operation Success");
    }

    @Test(groups = {"wso2.dss"})
    public void insertBatchRequest() throws AxisFault, XPathExpressionException {
        addEmployeeBatchRequest();
        for (int i = 10; i < 36; i++) {
            Assert.assertEquals("1", employeeExists(i + ""), "Employee Not Found");
        }
        log.info("Insert Batch Request verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"insertBatchRequest"})
    public void deleteBatchRequest() throws AxisFault, XPathExpressionException {
        deleteEmployeeBatchRequest();

        for (int i = 10; i < 36; i++) {
            Assert.assertEquals("0", employeeExists(i + ""), "Employee Found. batch deletion failed");
        }
        log.info("Delete Batch Request Success");
    }


    private void deleteEmployee(int employeeNo) throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("deleteEmployee", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        payload.addChild(empNo);
        empNo.setText(employeeNo + "");

        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "deleteEmployee");

    }

    private String employeeExists(String empId) throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("employeeExists", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(empId);
        payload.addChild(empNo);

        OMElement response = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "employeeExists");
        assertNotNull("Response null " + response);
        return response.getFirstElement().getFirstElement().getText();
    }

    private void addEmployee(int employeeNo) throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("addEmployee", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(employeeNo + "");
        payload.addChild(empNo);

        OMElement email = fac.createOMElement("email", omNs);
        email.setText("testemail@wso2.com");
        payload.addChild(email);

        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "addEmployee");

    }

    private void addEmployeeBatchRequest() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("addEmployee_batch_req", omNs);

        for (int i = 10; i < 36; i++) {
            OMElement batchRequest = fac.createOMElement("addEmployee", omNs);

            OMElement empNo = fac.createOMElement("employeeNumber", omNs);
            empNo.setText(i + "");
            batchRequest.addChild(empNo);

            OMElement email = fac.createOMElement("email", omNs);
            email.setText("testemail@wso2.com");
            batchRequest.addChild(email);

            payload.addChild(batchRequest);

        }
        if (log.isDebugEnabled()) {
            log.debug(payload);
        }
        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "addEmployee_batch_req");


    }

    private void deleteEmployeeBatchRequest() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("deleteEmployee_batch_req", omNs);

        for (int i = 10; i < 36; i++) {
            OMElement batchRequest = fac.createOMElement("addEmployee", omNs);

            OMElement empNo = fac.createOMElement("employeeNumber", omNs);
            empNo.setText(i + "");
            batchRequest.addChild(empNo);

            payload.addChild(batchRequest);

        }
        if (log.isDebugEnabled()) {
            log.debug(payload);
        }
        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "deleteEmployee_batch_req");


    }


}
