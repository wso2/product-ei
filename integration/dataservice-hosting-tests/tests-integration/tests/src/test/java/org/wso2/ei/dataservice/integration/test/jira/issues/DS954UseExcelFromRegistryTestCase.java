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

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.ResourceAdminServiceClient;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/CARBON-15046
 * Retriving null values on GET in JSON objects
 */
public class DS954UseExcelFromRegistryTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS954UseExcelFromRegistryTestCase.class);

    private final String serviceName = "ExcelFromReg";
    private String serviceEndPoint;
    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateEmailUsersTable.sql"));
        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl()
                , sessionCookie);
        resourceAdmin.deleteResource("/_system/governance/excel");
        resourceAdmin.addResource("/_system/governance/excel/Products.xls",
                "application/vnd.ms-excel", "",
                new DataHandler(new URL("file:///" + getResourceLocation()
                        + File.separator + "resources" + File.separator
                        + "Products.xls")));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                        + "excel" + File.separator
                        + serviceName + ".dbs", sqlFileLis));


        serviceEndPoint = getServiceUrlHttp(serviceName);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"}, description = "test the use of registry excel sheet as registry resource in dbs", alwaysRun = true)
    public void useExcelFromRegistryTest() throws Exception {
        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint + ".HTTPEndpoint/" + "products");
        Assert.assertNotNull(result, "Response null");
        Assert.assertTrue(result.toString().contains("<product><id>"), "Expected result not found");
        log.info("data service returns correct response when data source used as a registry resource");
    }


}
