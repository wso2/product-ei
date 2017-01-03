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

package org.wso2.ei.dataservice.integration.test.samples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.dataservices.samples.dtp_sample.DataServiceFault;
import org.wso2.carbon.dataservices.samples.file_service.FileServiceStub;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ws.dataservice.samples.file_service.file_names.File6;
import org.wso2.ws.dataservice.samples.file_service.file_size.File;

import javax.activation.DataHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;


public class FileServiceSampleTestCase extends DSSIntegrationTest {
    private final String serviceName = "FileService";
    private final Log log = LogFactory.getLog(FileServiceSampleTestCase.class);
    private String serverEpr;
    private String resourceFileLocation;


    @Factory(dataProvider = "userModeDataProvider")
    public FileServiceSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(userMode);
        serverEpr = getServiceUrlHttp(serviceName);
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              java.io.File.separator + "samples" + java.io.File.separator +
                                              "dbs" + java.io.File.separator + "rdbms" + java.io.File.separator +
                                              "FileService.dbs")));
        log.info(serviceName + " uploaded");
    }


    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = "testServiceDeployment")
    public void testGetProducts() throws DataServiceFault, RemoteException, MalformedURLException,
                                         org.wso2.carbon.dataservices.samples.file_service.DataServiceFault {

        String fileName = "transform.xslt";

        FileServiceStub stub = new FileServiceStub(serverEpr);
        DataHandler dataHandler = new DataHandler(new URL("file:///" + resourceFileLocation + java.io.File.separator +
                                                          "xslt" + java.io.File.separator + fileName));

        stub._getcreatenewfile(fileName, "xslt");
        stub._postappenddatatofile(fileName, dataHandler);

        File6[] filesName = stub._getgetfilenames();
        assertEquals(fileName, filesName[0].getFileName(), "file has not been created");

        File[] files = stub._getgetfilesize(fileName);
        assertTrue(files[0].getFileSize().intValue() >= 500);

        assertTrue(stub._getcheckfileexists(fileName)[0].getFileExists().intValue() == 1);
        assertEquals(stub._getgetfiletype(fileName)[0].getType(), "xslt");
        assertNotNull(stub._getgetfilerecords(fileName));

        stub._getdeletefile(fileName);
        File6[] filesNameDeleted = stub._getgetfilenames();
        assertNull("File has not been deleted", filesNameDeleted);
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
        log.info(serviceName + " deleted");
    }
}
