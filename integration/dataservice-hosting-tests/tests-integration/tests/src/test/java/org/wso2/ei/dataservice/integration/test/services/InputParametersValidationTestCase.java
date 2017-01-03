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

public class InputParametersValidationTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(InputParametersValidationTestCase.class);
    private final String serviceName = "InputParamsValidationTest";

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples", "ns1");

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));

        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "MySql" + File.separator
                                     + "InputParamsValidationTest.dbs", sqlFileLis));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void validateFieldLengthMin() throws XPathExpressionException {
        validateLastName("BB");
        log.info("Field minimum length verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void validateFieldLengthMax() throws XPathExpressionException {
        validateLastName("BBBBBBBBBBBB CCCCCCCC");
        log.info("Field maximum length verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void validateEmail() throws XPathExpressionException {
        validateEmail("aaabbb.com");
        validateEmail("aaa@bbb");
        log.info("Email address pattern verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void validatePrimaryKey() throws AxisFault, XPathExpressionException {
        addEmployee("1");
        boolean validatePrimaryKey = false;
        try {
            addEmployee("1");
        } catch (AxisFault e) {
            validatePrimaryKey = true;
            Assert.assertTrue(e.getMessage().contains("DATABASE_ERROR"), "DATABASE_ERROR Not Found in " +
                                                                         "error " +
                                                                         "message");
            /* Assert.assertTrue(e.getMessage().contains("Duplicate entry"),
         "Message Duplicate entry Not " +
         "Found in error message");*/

        }
        Assert.assertTrue(validatePrimaryKey, "primary key validation failed in Employees table");
        log.info("Primary key validation verified");
    }


    private void validateLastName(String lastNameValue) throws XPathExpressionException {
        OMElement payload = fac.createOMElement("addEmployee", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText("127");
        payload.addChild(empNo);

        OMElement lastName = fac.createOMElement("lastName", omNs);
        lastName.setText(lastNameValue);
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

        boolean validationStatus = false;
        try {
            new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "addEmployee");
            Assert.fail("This Request must throws a AxisFault due to VALIDATION_ERROR");
        } catch (AxisFault e) {
            validationStatus = true;
            Assert.assertTrue(e.getMessage().contains("VALIDATION_ERROR"), "VALIDATION_ERROR Not Found in " +
                                                                           "error message");
            Assert.assertTrue(e.getMessage().contains("Field Name: lastName"),
                              "Field Name: lastName Not Found in error message");
            Assert.assertTrue(e.getMessage().contains("The value length must be between 3 and 20"),
                              "Validation Message: The value length must be between 3 and 20 Not Found in error message");
            log.info("Last Name length validated");
        }
        Assert.assertTrue(validationStatus, "Last Name length Not validated");

    }


    private void validateEmail(String emailAddress) throws XPathExpressionException {
        OMElement payload = fac.createOMElement("addEmployee", omNs);

        OMElement empNo = fac.createOMElement("employeeNumber", omNs);
        empNo.setText("124");
        payload.addChild(empNo);

        OMElement lastName = fac.createOMElement("lastName", omNs);
        lastName.setText("BBB");
        payload.addChild(lastName);

        OMElement fName = fac.createOMElement("firstName", omNs);
        fName.setText("AAA");
        payload.addChild(fName);

        OMElement email = fac.createOMElement("email", omNs);
        email.setText(emailAddress);
        payload.addChild(email);

        OMElement salary = fac.createOMElement("salary", omNs);
        salary.setText("50000");
        payload.addChild(salary);

        boolean validationStatus = false;
        try {
            new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "addEmployee");
            Assert.fail("This Request must throws a AxisFault due to VALIDATION_ERROR");
        } catch (AxisFault e) {
            validationStatus = true;
            Assert.assertTrue(e.getMessage().contains("VALIDATION_ERROR"), "VALIDATION_ERROR Not Found in " +
                                                                           "error message");
            Assert.assertTrue(e.getMessage().contains("Field Name: email"), "Field Name: email Not Found in" +
                                                                            " error message");
            Assert.assertTrue(e.getMessage().contains("Pattern"), "Validation Message: Pattern " +
                                                                  "Not Found " +
                                                                  "in " +
                                                                  "error message");

            log.info("email length validated");
        }
        Assert.assertTrue(validationStatus, "email address Not validated");
    }

    private void addEmployee(String employeeNumber) throws AxisFault, XPathExpressionException {
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

        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "addEmployee");

    }

}
