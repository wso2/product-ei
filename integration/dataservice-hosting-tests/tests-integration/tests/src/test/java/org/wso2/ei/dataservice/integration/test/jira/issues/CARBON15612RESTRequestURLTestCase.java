/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CARBON15612RESTRequestURLTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON15612RESTRequestURLTestCase.class);

    private final String serviceName = "RESTRequestURLTest";
    OMFactory fac = OMAbstractFactory.getOMFactory();
    OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        sqlFileLis.add(selectSqlFile("Employees.sql"));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs"
                        + File.separator + "rdbms" + File.separator + "h2"
                        + File.separator + serviceName + ".dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    /*
    This testcase is disabled as the fix is on axis2.
     */
    @Test(groups = {"wso2.dss"}, enabled = false)
    public void getRequestAsQueryParam() throws Exception {
        listEmployeesAsQueryParam();
        log.info("GET Request verified");
    }

    /*
    This testcase is disabled as the fix is on axis2.
     */
    @Test(groups = {"wso2.dss"}, enabled = false)
    public void getRequestAsURLParam() throws Exception {
        listEmployeesAsURLParam();
        log.info("GET Request verified");
    }

    private void listEmployeesAsQueryParam() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint + "employee?employeeNumber=1611");
        Assert.assertNotNull(result, "Response null");
        Assert.assertTrue(result.toString().contains("<employee><last-name>Fixter</last-name>" +
                "<first-name>Andy</first-name><email>afixter@classicmodelcars.com</email>" +
                "<salary>2000.0</salary></employee>"), "Expected result not found");
    }

    private void listEmployeesAsURLParam() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint + "employee/1611");
        Assert.assertNotNull(result, "Response null");
        Assert.assertTrue(result.toString().contains("<employee><last-name>Fixter</last-name>" +
                "<email>afixter@classicmodelcars.com</email><salary>2000.0</salary>" +
                "</employee>"), "Expected result not found");
    }

}
