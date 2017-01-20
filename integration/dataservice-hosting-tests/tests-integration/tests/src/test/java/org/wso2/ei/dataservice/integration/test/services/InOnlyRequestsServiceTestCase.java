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
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;


public class InOnlyRequestsServiceTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(InOnlyRequestsServiceTestCase.class);
    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
    private final String serviceName = "InOnlyRequestsServiceTest";
    private String serverEpr;

    @Factory(dataProvider = "userModeDataProvider")
    public InOnlyRequestsServiceTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init(userMode);
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        String resourceFileLocation;
        serverEpr = getServiceUrlHttp(serviceName);
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                createArtifact(getResourceLocation()
                        + File.separator + "dbs" + File.separator
                        + "rdbms" + File.separator + "MySql" + File.separator
                        + "InOnlyRequestsServiceTest.dbs", sqlFileLis));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"})
    public void insertOperation() throws AxisFault, XPathExpressionException {
        for (int i = 1; i <= 10; i++) {
            addStudent(i);
        }
        log.info("Insert Operation finished");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"insertOperation"})
    public void countOperation() throws AxisFault, XPathExpressionException {
        Assert.assertEquals("10", getStudentCount(), "Student Count Verified");
        log.info("Insert Operation verified");
    }

    private void addStudent(int studentNo) throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("addStudent", omNs);

        OMElement stdNo = fac.createOMElement("studentNumber", omNs);
        stdNo.setText(studentNo + "");
        payload.addChild(stdNo);

        OMElement stdName = fac.createOMElement("name", omNs);
        stdName.setText("Student" + studentNo);
        payload.addChild(stdName);

        OMElement stdPhone = fac.createOMElement("phone", omNs);
        stdPhone.setText("+1 650 219 478" + studentNo);
        payload.addChild(stdPhone);

        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "addStudent");

    }

    private String getStudentCount() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("getStudentCount", omNs);

        OMElement response = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "getStudentCount");
        assertNotNull("Response null " + response);
        return response.getFirstElement().getFirstElement().getText();
    }

}
