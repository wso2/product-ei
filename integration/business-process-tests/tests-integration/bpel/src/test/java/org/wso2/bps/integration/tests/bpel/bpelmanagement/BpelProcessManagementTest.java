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
package org.wso2.bps.integration.tests.bpel.bpelmanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpel.BpelInstanceManagementClient;
import org.wso2.bps.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.bps.integration.common.clients.bpel.BpelProcessManagementClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.bps.integration.common.utils.RequestSender;
import org.wso2.carbon.bpel.stub.mgt.ProcessManagementException;

import java.rmi.RemoteException;

public class BpelProcessManagementTest extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BpelProcessManagementTest.class);

    BpelPackageManagementClient bpelPackageManagementClient = null;
    BpelProcessManagementClient bpelProcessManagementClient = null;
    BpelInstanceManagementClient bpelInstanceManagementClient = null;
    RequestSender requestSender = null;


    public void setEnvironment() throws Exception {
        init();
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        bpelProcessManagementClient = new BpelProcessManagementClient(backEndUrl, sessionCookie);
        bpelInstanceManagementClient = new BpelInstanceManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }

    @BeforeClass(alwaysRun = true)
    public void deployArtifact() throws Exception {
        setEnvironment();
        uploadBpelForTest("LoanService");
        requestSender.waitForProcessDeployment(backEndUrl + "XKLoanService");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Set process to retired State", priority = 1, singleThreaded = true)
    public void testServiceRetire() throws ProcessManagementException, RemoteException {
        try {
            String processID = bpelProcessManagementClient.getProcessId("XKLoanService");
            bpelProcessManagementClient.setStatus(processID, "RETIRED");
            Thread.sleep(5000);
            Assert.assertTrue(bpelProcessManagementClient.getStatus(processID).equals("RETIRED"), "BPEL process is not set as RETIRED");
            Assert.assertFalse(requestSender.isServiceAvailable(backEndUrl + "XKLoanService"), "Service is still available");
        } catch (InterruptedException e) {
            log.debug("Thread interrupted");
        }
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Set service to Active State", dependsOnMethods = "testServiceRetire", singleThreaded = true)
    public void testServiceActive() throws ProcessManagementException, RemoteException {
        try {
            String processID = bpelProcessManagementClient.getProcessId("XKLoanService");
            bpelProcessManagementClient.setStatus(processID, "ACTIVE");
            Thread.sleep(5000);
            Assert.assertTrue(bpelProcessManagementClient.getStatus(processID).equals("ACTIVE"), "BPEL process is not set as ACTIVE");
            Assert.assertTrue(requestSender.isServiceAvailable(backEndUrl + "XKLoanService"), "Service is not available");
        } catch (InterruptedException e) {
            log.error("Process management failed" + e);
            Assert.fail("Process management failed" + e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts() throws Exception {
        bpelPackageManagementClient.undeployBPEL("LoanService");
        this.loginLogoutClient.logout();
    }
}
