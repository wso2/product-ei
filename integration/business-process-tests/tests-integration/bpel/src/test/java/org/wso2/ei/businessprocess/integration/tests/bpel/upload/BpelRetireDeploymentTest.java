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
package org.wso2.ei.businessprocess.integration.tests.bpel.upload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.mgt.types.LimitedInstanceInfoType;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelInstanceManagementClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelProcessManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BpelRetireDeploymentTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BpelRetireDeploymentTest.class);

    LimitedInstanceInfoType instanceInfo = null;
    BpelPackageManagementClient bpelPackageManagementClient;
    BpelProcessManagementClient bpelProcessManagementClient;
    BpelInstanceManagementClient bpelInstanceManagementClient;

    RequestSender requestSender;


    public void setEnvironment() throws Exception {
        init();
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        bpelProcessManagementClient = new BpelProcessManagementClient(backEndUrl, sessionCookie);
        bpelInstanceManagementClient = new BpelInstanceManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }


    public void deployArtifact()
            throws Exception {
        setEnvironment();
        uploadBpelForTest("HelloWorld-retire");

    }

    @Test(groups = {"wso2.bps", "wso2.bps.deployment"}, description = "Tests uploading Bpel with retire true", priority = 1)
    public void testRetireClient() throws Exception {
        // wait till the uploaded process deploy
        deployArtifact();

        Awaitility.await()
                  .pollInterval(50, TimeUnit.MILLISECONDS)
                  .atMost(150, TimeUnit.SECONDS)
                  .until(isServiceDeployed("HelloWorld-retire"));

        String processId = bpelProcessManagementClient.getProcessId("HelloWorld-retire");
        String status = bpelProcessManagementClient.getStatus(processId);
        Assert.assertTrue(status.equals("RETIRED".toUpperCase()), "Process State is still Active");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("HelloWorld-retire");
        this.loginLogoutClient.logout();
    }

    private Callable<Boolean> isServiceDeployed(final String packageName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (bpelProcessManagementClient.getProcessId(packageName) != null) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }
}
