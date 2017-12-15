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

package org.wso2.ei.dataservice.integration.test.scheduletask;

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
import org.wso2.carbon.dataservices.task.ui.stub.xsd.DSTaskInfo;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceTaskClient;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;


public class AddScheduleTaskTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(AddScheduleTaskTestCase.class);

    private final String scheduleTaskName = "testScheduleTaskTestCase";
    private final int taskInterval = 5000;
    private final String employeeId = "1";
    private double empSalary;
    private String serviceName = "ScheduleTaskTest";
    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/" +
                                                           "rdbms_sample", "ns1");
    private String serviceEndPoint;
    private DataServiceTaskClient dssTaskClient;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        String resourceLocation =getResourceLocation();

        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName,
                      createArtifact(resourceLocation + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "MySql" + File.separator
                                     + "ScheduleTaskTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName);

        dssTaskClient = new DataServiceTaskClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @Test(groups = "wso2.dss", description = "check whether the service is deployed")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
    }


    @Test(groups = "wso2.dss", dependsOnMethods = {"testServiceDeployment"})
    public void serviceInvocation() throws AxisFault {
        deleteEmployees();
        addEmployee(employeeId);
        getEmployeeById(employeeId);
        IncreaseEmployeeSalary(employeeId);
        empSalary = getEmployeeSalary(getEmployeeById(employeeId));
        log.info("service invocation success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"serviceInvocation"})
    public void addScheduleTask() throws RemoteException {
        DSTaskInfo dsTaskInfo = new DSTaskInfo();
        String[] taskNames = dssTaskClient.getAllTaskNames();

        if (taskNames != null) {
            for (String task : taskNames) {
                if (scheduleTaskName.equals(task)) {
                    dssTaskClient.deleteTask(scheduleTaskName);
                    log.info(scheduleTaskName + " already exists. scheduleTask deleted");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        log.error("InterruptedException :", e);
                        Assert.fail("InterruptedException :" + e);
                    }
                    break;
                }
            }
        }

        dsTaskInfo.setName(scheduleTaskName);
        dsTaskInfo.setServiceName(serviceName);
        dsTaskInfo.setOperationName("incrementEmployeeSalary");
        dsTaskInfo.setTaskInterval(taskInterval);
        dsTaskInfo.setTaskCount(4);

        dssTaskClient.scheduleTask(dsTaskInfo);
        log.info("Task Scheduled");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {

        }

    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"addScheduleTask"})
    public void startScheduleTask() throws AxisFault {
        //if task count is 4
        for (int i = 0; i < 5; i++) {
            double currentSalary = getEmployeeSalary(getEmployeeById(employeeId));
            log.info("current salary after task: " + currentSalary);
            Assert.assertEquals(currentSalary, (empSalary = empSalary + 10000), "Task not properly Executed");
            try {
                Thread.sleep(taskInterval);
            } catch (InterruptedException e) {
                log.error("InterruptedException :", e);
                Assert.fail("InterruptedException :" + e);
            }
        }
        log.info("ScheduleTask verifying Success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"startScheduleTask"})
    public void verifyTaskCount() throws AxisFault {
        for (int i = 0; i < 5; i++) {
            double currentSalary = getEmployeeSalary(getEmployeeById(employeeId));
            log.info("current salary after exceeding task count " + currentSalary);
            Assert.assertEquals(empSalary, currentSalary, "Task Repeat Counter not properly Executed");
            try {
                Thread.sleep(taskInterval);
            } catch (InterruptedException e) {
                log.error("InterruptedException :", e);
                Assert.fail("InterruptedException :" + e);
            }
        }
        log.info("Task Count Verified");
    }

//    @Test(groups = "wso2.dss", dependsOnMethods = {"verifyTaskCount"})
//    public void deleteTask() throws RemoteException {
//        dssTaskClient.deleteTask(scheduleTaskName);
//        log.info("Task Deleted");
//    }

    @AfterClass(alwaysRun = true)
    public void testCleanup() throws Exception {
        dssTaskClient.deleteTask(scheduleTaskName);
        log.info("Task Deleted");
        deleteService(serviceName);
    }

    private void addEmployee(String employeeNumber) throws AxisFault {
        OMElement payload = fac.createOMElement("addEmployee", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(employeeNumber);
        payload.addChild(empNo);

        OMElement lastName = fac.createOMElement("lastName", omNs);
        lastName.setText("BBB");
        payload.addChild(lastName);

        OMElement fName = fac.createOMElement("firstName", omNs);
        fName.setText("AAA");
        payload.addChild(fName);

        OMElement email = fac.createOMElement("email", omNs);
        email.setText("aaa@ccc.com");
        payload.addChild(email);

        OMElement salary = fac.createOMElement("salary", omNs);
        salary.setText("50000");
        payload.addChild(salary);

        new AxisServiceClient().sendRobust(payload, serviceEndPoint, "addEmployee");

    }

    private OMElement getEmployeeById(String employeeNumber) throws AxisFault {
        OMElement payload = fac.createOMElement("employeesByNumber", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(employeeNumber);
        payload.addChild(empNo);

        OMElement result = new AxisServiceClient().sendReceive(payload, serviceEndPoint, "employeesByNumber");
        Assert.assertNotNull(result, "Employee record null");
        Assert.assertTrue(result.toString().contains("<first-name>AAA</first-name>"), "Expected Result Mismatched");
        return result;
    }

    private void deleteEmployees() throws AxisFault {
        OMElement payload = fac.createOMElement("deleteEmployees", omNs);

        new AxisServiceClient().sendRobust(payload, serviceEndPoint, "deleteEmployees");

    }

    private void IncreaseEmployeeSalary(String employeeNumber) throws AxisFault {
        OMElement payload = fac.createOMElement("incrementEmployeeSalary", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(employeeNumber);
        payload.addChild(empNo);

        OMElement salary = fac.createOMElement("increment", omNs);
        salary.setText("10000");
        payload.addChild(salary);

        new AxisServiceClient().sendRobust(payload, serviceEndPoint, "incrementEmployeeSalary");

        OMElement result = getEmployeeById(employeeNumber);
        Assert.assertTrue(result.toString().contains("<salary>60000.0</salary>"), "Expected Result Mismatched");

    }

    private double getEmployeeSalary(OMElement employeeRecord) {
        OMElement employee = employeeRecord.getFirstElement();
        OMElement salary = (OMElement) employee.getChildrenWithLocalName("salary").next();
        return Double.parseDouble(salary.getText());
    }
}
