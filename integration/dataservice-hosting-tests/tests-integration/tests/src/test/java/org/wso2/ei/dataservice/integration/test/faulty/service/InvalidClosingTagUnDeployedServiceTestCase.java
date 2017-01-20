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


import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.dataservices.ui.fileupload.stub.ExceptionException;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceFileUploaderClient;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class InvalidClosingTagUnDeployedServiceTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(InvalidClosingTagUnDeployedServiceTestCase.class);

    private final String serviceName = "FaultyDataService";
    private final String serviceFile = "FaultyDataService.dbs";
    private String resourceFileLocation;
    private DataServiceFileUploaderClient dataServiceAdminClient;
    private DataHandler dhArtifact;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        resourceFileLocation = getResourceLocation();
        String serviceFilePath = resourceFileLocation + File.separator + "dbs" + File.separator +
                                 "rdbms" + File.separator + "MySql" + File.separator + serviceFile;
        
        createArtifact(serviceFilePath, getSqlScript());
        dataServiceAdminClient =
                new DataServiceFileUploaderClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        ByteArrayDataSource dbs;
        String content = FileManager.readFile(serviceFilePath);
        Assert.assertTrue(content.contains("</query>"), "query tag missing");
        content = content.replaceFirst("</query>", "</que>");
        dbs = new ByteArrayDataSource(content.getBytes());
        dhArtifact = new DataHandler(dbs);

    }

    @Test(groups = "wso2.dss", description = "deploy invalid dbs", expectedExceptions = AxisFault.class)
    public void testDeployService() throws ExceptionException, RemoteException {
        Assert.assertTrue(dataServiceAdminClient.uploadDataServiceFile(serviceFile, dhArtifact)
                , "Service Deployment Failed while uploading service file");

    }


    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not",
          dependsOnMethods = "testDeployService")
    public void isServiceDeployed() throws Exception {
        assertFalse(isServiceDeployed(serviceName));
        log.info(serviceName + " is faulty");
    }


    private ArrayList<File> getSqlScript() throws XPathExpressionException {
        ArrayList<File> al = new ArrayList<File>();
        al.add(selectSqlFile("CreateTables.sql"));
        al.add(selectSqlFile("Offices.sql"));
        return al;
    }
}
