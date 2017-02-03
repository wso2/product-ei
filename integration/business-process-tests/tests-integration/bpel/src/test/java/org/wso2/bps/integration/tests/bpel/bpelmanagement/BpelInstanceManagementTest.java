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

import org.apache.axis2.addressing.EndpointReference;
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
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.bpel.stub.mgt.InstanceManagementException;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.mgt.ProcessManagementException;
import org.wso2.carbon.bpel.stub.mgt.types.LimitedInstanceInfoType;
import org.wso2.carbon.bpel.stub.mgt.types.PaginatedInstanceList;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

public class BpelInstanceManagementTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BpelInstanceManagementTest.class);
    LimitedInstanceInfoType instanceInfo = null;
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
        uploadBpelForTest("TestPickOneWay");
        requestSender.waitForProcessDeployment(backEndUrl + "PickService");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Set service to Active State", priority = 1)
    public void testCreateInstance()
            throws InterruptedException, XMLStreamException, RemoteException,
            ProcessManagementException, InstanceManagementException, LoginAuthenticationExceptionException {
        EndpointReference epr = new EndpointReference(backEndUrl + "PickService");
        requestSender.sendRequest("<pic:dealDeck xmlns:pic=\"http://www.stark.com/PickService\">" +
                "   <pic:Deck>testPick</pic:Deck>" +
                "</pic:dealDeck>", epr);

        PaginatedInstanceList instanceList = bpelInstanceManagementClient.filterPageInstances(bpelProcessManagementClient.getProcessId("PickProcess"));
        instanceInfo = instanceList.getInstance()[0];
        if (instanceList.getInstance().length == 0) {
            Assert.fail("Instance failed to create");
        }
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Suspends The Service", dependsOnMethods = "testCreateInstance", singleThreaded = true)
    public void testSuspendInstance()
            throws InterruptedException, InstanceManagementException, RemoteException, LoginAuthenticationExceptionException {
        bpelInstanceManagementClient.performAction(instanceInfo.getIid(), BpelInstanceManagementClient.InstanceOperation.SUSPEND);
        Assert.assertTrue(bpelInstanceManagementClient.getInstanceInfo(instanceInfo.getIid()).getStatus().getValue().equals("SUSPENDED"), "The Service Is not Suspended");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Resume The Service", dependsOnMethods = "testSuspendInstance", singleThreaded = true)
    public void testResumeInstance()
            throws InterruptedException, InstanceManagementException, RemoteException, LoginAuthenticationExceptionException {
        bpelInstanceManagementClient.performAction(instanceInfo.getIid(), BpelInstanceManagementClient.InstanceOperation.RESUME);
        Assert.assertTrue(bpelInstanceManagementClient.getInstanceInfo(instanceInfo.getIid()).getStatus().getValue().equals("ACTIVE"), "The Service Is not Suspended");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Terminate The Service", dependsOnMethods = "testResumeInstance", singleThreaded = true)
    public void testTerminateInstance()
            throws InterruptedException, InstanceManagementException, RemoteException, LoginAuthenticationExceptionException {
        bpelInstanceManagementClient.performAction(instanceInfo.getIid(), BpelInstanceManagementClient.InstanceOperation.TERMINATE);
        Assert.assertTrue(bpelInstanceManagementClient.getInstanceInfo(instanceInfo.getIid()).getStatus().getValue().equals("TERMINATED"), "The Service Is not Terminated");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.manage"}, description = "Delete the instance", dependsOnMethods = "testTerminateInstance")
    public void testDeleteInstance()
            throws InterruptedException, InstanceManagementException, RemoteException, LoginAuthenticationExceptionException {

        bpelInstanceManagementClient.deleteInstance(instanceInfo.getIid());
        Thread.sleep(5000);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LoginAuthenticationExceptionException, LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("TestPickOneWay");
        this.loginLogoutClient.logout();
    }
}
