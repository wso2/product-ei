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

package org.wso2.ei.dataservice.integration.test.syntax;

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
import org.wso2.carbon.automation.test.utils.concurrency.test.exception.ConcurrencyTestFailedError;
import org.wso2.carbon.automation.test.utils.concurrency.test.exception.ExceptionHandler;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

//https://wso2.org/jira/browse/CARBON-12361
public class ReturnRequestStatusTest extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(ReturnRequestStatusTest.class);
    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/" +
                                                           "rdbms_sample", "ns1");
    private static String serviceName = "ReturnRequestStatusTest";
    private static String serviceEndPoint;
    private DSSTestCaseUtils dssTest;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        dssTest = new DSSTestCaseUtils();

        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "MySql" + File.separator
                                     + "ReturnRequestStatusTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName);
    }

    @Test(groups = "wso2.dss", description = "check whether the service is deployed")
    public void testServiceDeployment() throws Exception {

        assertTrue(dssTest.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(), sessionCookie,
                                             serviceName));
    }


    @Test(groups = "wso2.dss", dependsOnMethods = {"testServiceDeployment"}, description = "add employees")
    public void requestStatusNameSpaceQualifiedForInsertOperation() throws AxisFault {

        addEmployee(serviceEndPoint, String.valueOf(180));
        log.info("Insert Operation Success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"requestStatusNameSpaceQualifiedForInsertOperation"})
    public void requestStatusNameSpaceQualifiedForDeleteOperation() throws AxisFault {
        deleteEmployeeById(String.valueOf(180));
        log.info("Delete operation success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"requestStatusNameSpaceQualifiedForDeleteOperation"}, timeOut = 1000 * 60 * 2)
    public void inOperationConcurrencyTest() throws InterruptedException,
                                                    ConcurrencyTestFailedError {

        final ExceptionHandler handler = new ExceptionHandler();
        final int concurrencyNumber = 50;
        final int numberOfIterations = 1;
        Thread[] clientThread = new Thread[concurrencyNumber];
        final AxisServiceClient serviceClient = new AxisServiceClient();
        for (int i = 0; i < concurrencyNumber; i++) {
            final int empNo = i + 50;
            clientThread[i] = new Thread(new Runnable() {
                public void run() {
                    for (int j = 0; j < numberOfIterations; j++) {
                        try {
                            OMElement response = serviceClient.sendReceive(getAddEmployeePayload(empNo + ""),
                                                                           serviceEndPoint, "addEmployee");
                            Assert.assertTrue(response.toString().contains("SUCCESSFUL"), "Response Not Successful");
                            OMNamespace nameSpace = response.getNamespace();
                            Assert.assertNotNull(nameSpace, "Response Message NameSpace not qualified");
                        } catch (AxisFault axisFault) {
                            log.error(axisFault);
                            handler.setException(axisFault);
                        }
                    }
                }
            });
            clientThread[i].setUncaughtExceptionHandler(handler);

        }

        for (int i = 0; i < concurrencyNumber; i++) {
            clientThread[i].start();
        }

        for (int i = 0; i < concurrencyNumber; i++) {
            try {
                clientThread[i].join();
            } catch (InterruptedException e) {
                throw new InterruptedException("Exception Occurred while joining Thread");
            }
        }

        if (!handler.isTestPass()) {
            throw new ConcurrencyTestFailedError(handler.getFailCount() + " service invocation/s failed out of "
                                                 + concurrencyNumber * numberOfIterations + " service invocations.\n"
                                                 + "Concurrency Test Failed for Thread Group=" + concurrencyNumber
                                                 + " and loop count=" + numberOfIterations, handler.getException());
        }
    }

    @AfterClass(alwaysRun = true)
    public void testCleanup() throws Exception {
        dssTest.deleteService(dssContext.getContextUrls().getBackEndUrl(),sessionCookie, serviceName);
    }


    private void addEmployee(String serviceEndPoint, String employeeNumber) throws AxisFault {
        OMElement result;

        result = new AxisServiceClient().sendReceive(getAddEmployeePayload(employeeNumber), serviceEndPoint, "addEmployee");
        Assert.assertTrue(result.toString().contains("SUCCESSFUL"), "Response Not Successful");
        OMNamespace nameSpace = result.getNamespace();
        Assert.assertNotNull(nameSpace, "Response Message NameSpace not qualified");
        Assert.assertNotNull(nameSpace.getPrefix(), "Invalid prefix. prefix value null");
        Assert.assertNotSame(nameSpace.getPrefix(), "", "Invalid prefix");
        Assert.assertEquals(nameSpace.getNamespaceURI(), "http://ws.wso2.org/dataservice", "Invalid NamespaceURI");


    }

    private void deleteEmployeeById(String employeeNumber) throws AxisFault {
        OMElement result;
        OMElement payload = fac.createOMElement("deleteEmployeeById", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText(employeeNumber);
        payload.addChild(empNo);

        result = new AxisServiceClient().sendReceive(payload, serviceEndPoint, "deleteEmployeeById");

        Assert.assertTrue(result.toString().contains("SUCCESSFUL"), "Response Not Successful");
        OMNamespace nameSpace = result.getNamespace();
        Assert.assertNotNull(nameSpace.getPrefix(), "Invalid prefix. prefix value null");
        Assert.assertNotSame(nameSpace.getPrefix(), "", "Invalid prefix");
        Assert.assertEquals(nameSpace.getNamespaceURI(), "http://ws.wso2.org/dataservice", "Invalid NamespaceURI");
    }

    private OMElement getAddEmployeePayload(String employeeNumber) {
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

        return payload;
    }
}
