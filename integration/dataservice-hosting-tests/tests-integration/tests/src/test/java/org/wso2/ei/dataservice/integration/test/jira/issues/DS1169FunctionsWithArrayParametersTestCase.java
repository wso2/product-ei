/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.dataservice.integration.test.jira.issues;

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

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/DS-1169
 * Throwing invalid colum index exception when there is a function in dataservice with 'in' and 'out' array parameters
 */

public class DS1169FunctionsWithArrayParametersTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS1169FunctionsWithArrayParametersTestCase.class);

    private final String serviceName = "FunctionsWithArrayParametersTest";
    OMFactory fac = OMAbstractFactory.getOMFactory();
    OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");

    /*
    This testcase is disabled as this needs an oracle database server to run the test.
     */

    @BeforeClass(alwaysRun = true, enabled = false)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("UserDefinedTypes.sql"));
        sqlFileLis.add(selectSqlFile("Functions.sql"));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs"
                        + File.separator + "rdbms" + File.separator + "oracle"
                        + File.separator + serviceName + ".dbs", sqlFileLis));
    }

    @AfterClass(enabled = false)
    public void clean() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = { "wso2.dss" }, enabled = false)
    public void testForArrayParameters() throws AxisFault {
        OMElement payload = fac.createOMElement("getStudentGrades", omNs);

        OMElement className = fac.createOMElement("class_name", omNs);
        className.setText("classA");
        payload.addChild(className);

        OMElement studentName1 = fac.createOMElement("student_names", omNs);
        studentName1.setText("John");
        payload.addChild(studentName1);

        OMElement studentName2 = fac.createOMElement("student_names", omNs);
        studentName2.setText("Tom");
        payload.addChild(studentName2);

        OMElement result = null;
        try {
            result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "getStudentGrades");
        } catch (XPathExpressionException e) {
            log.info("FunctionsWithArrayParametersTestCase failed ",e);
        }
        Assert.assertNotNull(result, "Response message null ");
        Assert.assertTrue(result.toString().contains("<total_score>40</total_score><grades><grade>10</grade>" +
                "<grade>20</grade></grades>"), "Expected not same");
    }
}
