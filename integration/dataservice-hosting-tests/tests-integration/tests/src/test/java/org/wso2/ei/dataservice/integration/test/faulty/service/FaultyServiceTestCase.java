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

package org.wso2.ei.dataservice.integration.test.faulty.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceFileUploaderClient;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;


public class FaultyServiceTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(FaultyServiceTestCase.class);

    private String serviceName = "FaultyDataService";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        String resourceFileLocation;
        resourceFileLocation = getResourceLocation();
        DataServiceFileUploaderClient dataServiceAdminClient =
                new DataServiceFileUploaderClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        dataServiceAdminClient.uploadDataServiceFile("FaultyDataService.dbs",
                                                     new DataHandler(new URL("file:///" + resourceFileLocation +
                                                                             File.separator + "dbs" + File.separator +
                                                                             "rdbms" + File.separator + "MySql" + File.separator +
                                                                             "FaultyDataService.dbs")));
        log.info(serviceName + " uploaded");
    }


    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void isServiceFaulty() throws Exception {
        assertTrue(isServiceFaulty(serviceName));
        log.info(serviceName + " is faulty");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        log.info(serviceName + " deleted");
    }
}