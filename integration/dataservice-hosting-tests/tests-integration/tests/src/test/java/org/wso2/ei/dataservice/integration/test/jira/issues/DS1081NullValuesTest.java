/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.dataservice.integration.test.jira.issues;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DS1081NullValuesTest extends DSSIntegrationTest {


    private static final Log log = LogFactory.getLog(DS1081NullValuesTest.class);

    private final String serviceName = "NullTest_DataService";
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTableNullTest.sql"));
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator + "rdbms" +
                                                  File.separator + "h2" + File.separator + serviceName + ".dbs",
                                                  sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = "wso2.dss", description = "validate retrieval of null values")
    public void validateNullValues() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        Object[] response = ODataTestUtils.sendGET(serviceEndPoint + "select", headers);
        org.testng.Assert.assertEquals(response[0], 200);
        Assert.assertTrue(response[1].toString().contains("\"PersonID\":null,\"LastName\":\"WSO2 Inc.\",\"City\":null,\"Weight\":null"));
    }
}
