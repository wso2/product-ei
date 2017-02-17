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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class ReScheduleTaskTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(ReScheduleTaskTestCase.class);

    private DSTaskInfo dsTaskInfo;
    private final String scheduleTaskName = "testScheduleTask";
    private final int taskInterval = 10000;
    private final String employeeId = "1";
    private double empSalary;

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/rdbms_sample", "ns1");
    private String serviceName = "ScheduleTaskTest";
    private String serviceEndPoint;
    private DataServiceTaskClient dssTaskClient;

    @BeforeClass(alwaysRun = true, enabled = false)
    public void initialize() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        String resourceLocation = getResourceLocation();

        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName,
                      createArtifact(resourceLocation + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "MySql" + File.separator
                                     + "ScheduleTaskTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName);

        dssTaskClient = new DataServiceTaskClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @Test(groups = "wso2.dss", description = "check whether the service is deployed", enabled = false)
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"testServiceDeployment"}, enabled = false)
    public void serviceInvocation() throws AxisFault, InterruptedException {
        deleteEmployees();
        addEmployee(employeeId);
        getEmployeeById(employeeId);
        IncreaseEmployeeSalary(employeeId);

        empSalary = getEmployeeSalary(getEmployeeById(employeeId));
        log.info("service invocation success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"serviceInvocation"}, description = "schedule a task",
            enabled = false)
    public void addScheduleTask() throws RemoteException, InterruptedException {
        dsTaskInfo = new DSTaskInfo();

        String[] taskNames = dssTaskClient.getAllTaskNames();
        empSalary = getEmployeeSalary(getEmployeeById(employeeId));
        if (taskNames != null) {
            for (String task : taskNames) {
                if (scheduleTaskName.equals(task)) {
                    dssTaskClient.deleteTask(scheduleTaskName);
                    log.info(scheduleTaskName + " already in scheduled. schedule task deleted");
                    Thread.sleep(10000);
                    break;
                }
            }
        }

        dsTaskInfo.setName(scheduleTaskName);
        dsTaskInfo.setServiceName(serviceName);
        dsTaskInfo.setOperationName("incrementEmployeeSalary");
        dsTaskInfo.setTaskInterval(taskInterval);
        dsTaskInfo.setTaskCount(-1);

        dssTaskClient.scheduleTask(dsTaskInfo);
        log.info("task scheduled");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignore) {

        }
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"addScheduleTask"}, description = "Verify the task execution",
            enabled = false)
    public void startScheduleTask() throws AxisFault, InterruptedException {
        //if task count is 9
        for (int i = 0; i < 4; i++) {
            double currentSalary = getEmployeeSalary(getEmployeeById(employeeId));
            log.info("current salary after task: " + currentSalary);
            log.info("Not salary ################ + " + empSalary);
            empSalary = empSalary + 10000;
            Assert.assertEquals(currentSalary, empSalary, "Task not properly Executed");
            if (i == 3) {
                break;
            }
            Thread.sleep(taskInterval + 1);
        }
        log.info("ScheduleTask verifying Success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"startScheduleTask"},
          description = "Task repeat count set to zero - This method has been added to avoid task repetition" ,enabled = false)
    public void testSetRepeatCountToZero() throws RemoteException, InterruptedException {
        dsTaskInfo.setTaskCount(0);
        dsTaskInfo.setTaskInterval(5000);
        assertTrue(dssTaskClient.rescheduleTask(dsTaskInfo));

        log.info("Task rescheduled with zero repeat count");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {

        }
        empSalary = getEmployeeSalary(getEmployeeById(employeeId)); //empSalary reset to current salary .
        log.info("Employee salary is " + empSalary + " after setting task repeat count to zero");
        Thread.sleep(5000);
        log.info("Employee salary is " + empSalary + " after setting task repeat count to zero");
    }



    @Test(groups = "wso2.dss", dependsOnMethods = {"testSetRepeatCountToZero"}, description = "reschedule the task", enabled = false)
    public void reScheduleTask() throws RemoteException {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.SECOND, startTime.get(Calendar.SECOND) + 40);
        dsTaskInfo.setTaskCount(9);
        dsTaskInfo.setTaskInterval(5000);
        dsTaskInfo.setStartTime(startTime);

        log.info("Schedule Task Start time " + getTime(startTime));
        log.info("Current Time " + getTime(Calendar.getInstance()));
        assertTrue(dssTaskClient.rescheduleTask(dsTaskInfo));

        log.info("Task rescheduled");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {

        }
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"reScheduleTask"},
          description = "Check whether the task has been stopped properly", enabled = false)
    public void stopScheduleTask() throws AxisFault, InterruptedException {
        for (int i = 0; i < 5; i++) {
            double currentSalary = getEmployeeSalary(getEmployeeById(employeeId));
            log.info("current salary after rescheduling task #######" + currentSalary);
            log.info("Emp salary ########### " + empSalary);
            Assert.assertEquals(currentSalary, empSalary, "Task not properly Stopped after rescheduling task");
            Thread.sleep(5000);
        }
        log.info("schedule task stopped");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"stopScheduleTask"}, description = "Recheck the task invocation", enabled = false)
    public void reStartScheduleTask() throws AxisFault, InterruptedException {
        while (dsTaskInfo.getStartTime().getTimeInMillis() >= Calendar.getInstance().getTimeInMillis()) {
            Thread.sleep(1000);
        }
        log.info("Current Time " + getTime(Calendar.getInstance()));
        log.info("task stared");
        for (int i = 0; i < 10; i++) {
            double currentSalary = getEmployeeSalary(getEmployeeById(employeeId));
            log.info("current salary after task rescheduling: " + currentSalary);
            Assert.assertEquals(currentSalary, (empSalary = empSalary + 10000), "Task not properly rescheduled");
            Thread.sleep(5000);
        }
        log.info("reschedule task verified");
        //for testing taskCount
        for (int i = 0; i < 5; i++) {
            double currentSalary = getEmployeeSalary(getEmployeeById(employeeId));
            log.info("current salary after exceeding task count " + currentSalary);
            Assert.assertEquals(currentSalary, empSalary, "Task Repeat Counter not properly Executed");
            Thread.sleep(taskInterval);
        }
        log.info("Task Count verified");
    }

//    @Test(groups = "wso2.dss", dependsOnMethods = {"reStartScheduleTask"}, description = "delete the task")
//    public void deleteTask() throws RemoteException {
//        dssTaskClient.deleteTask(scheduleTaskName);
//        log.info(scheduleTaskName + " deleted");
//    }

    @AfterClass(alwaysRun = true, enabled = false)
    public void testCleanup() throws Exception {
        dssTaskClient.deleteTask(scheduleTaskName);
        log.info(scheduleTaskName + " deleted");
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

    private OMElement getEmployeeById(String employeeNumber) throws AxisFault,
                                                                    InterruptedException {
        OMElement payload = fac.createOMElement("employeesByNumber", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(employeeNumber);
        payload.addChild(empNo);
        int count = 0;
        OMElement result = null;
        while (count < 2) {
            result = new AxisServiceClient().sendReceive(payload, serviceEndPoint, "employeesByNumber");
            count++;
        }
        Assert.assertNotNull(result, "Employee record null");
        System.out.println("Employee ID " + result.toString());
        Assert.assertTrue(result.toString().contains("<first-name>AAA</first-name>"), "Expected Result Mismatched");
        return result;
    }

    private void deleteEmployees() throws AxisFault {
        OMElement payload = fac.createOMElement("deleteEmployees", omNs);

        new AxisServiceClient().sendRobust(payload, serviceEndPoint, "deleteEmployees");

    }

    private void IncreaseEmployeeSalary(String employeeNumber)
            throws AxisFault, InterruptedException {
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

    private String getTime(Calendar time) {
        Format formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return formatter.format(time.getTimeInMillis());

    }
}
