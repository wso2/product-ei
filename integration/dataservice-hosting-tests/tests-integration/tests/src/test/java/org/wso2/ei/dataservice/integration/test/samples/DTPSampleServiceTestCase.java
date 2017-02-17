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
import org.wso2.carbon.dataservices.samples.dtp_sample.DTPSampleServiceStub;
import org.wso2.carbon.dataservices.samples.dtp_sample.DataServiceFault;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ws.dataservice.samples.dtp_sample.Entry;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class DTPSampleServiceTestCase extends DSSIntegrationTest {

    private final String serviceName = "DTPSampleService";
    private static final Log log = LogFactory.getLog(DTPSampleServiceTestCase.class);
    private String serverEpr;


    @Factory(dataProvider = "userModeDataProvider")
    public DTPSampleServiceTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(userMode);
        serverEpr = getServiceUrlHttp(serviceName);
        String resourceFileLocation;
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "rdbms" + File.separator +
                                              "DTPSampleService.dbs")));
        log.info(serviceName + " uploaded");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }


    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = "testServiceDeployment")
    public void testGetProducts() throws DataServiceFault, RemoteException {
        DTPSampleServiceStub stub = new DTPSampleServiceStub(serverEpr);
        stub._getServiceClient().getOptions().setManageSession(true);
        stub._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);


        Entry[] entry1 = stub.addAccountToBank1(1000.00);
        Entry[] entry2 = stub.addAccountToBank2(2000.00);

        stub.begin_boxcar();

        stub.addToAccountBalanceInBank1(entry1[0].getID().intValue(), -100.00);

        //this line will cases dss fault due to service input parameter validation
        stub.addToAccountBalanceInBank2(entry2[0].getID().intValue(), 10000.00);

        try {
            stub.end_boxcar();
        } catch (Exception dssFault) {
            log.error("DSS fault ignored");
        }

        assertEquals(stub.getAccountBalanceFromBank1(entry1[0].getID().intValue()), 1000.00);
        assertEquals(stub.getAccountBalanceFromBank2(entry2[0].getID().intValue()), 2000.00);
    }
}
