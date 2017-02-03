/*
 *
 *   Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */
package org.wso2.bps.integration.tests.bpel.bpelactivities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.bps.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class BPELRPCPartnerInvocationTest extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPELRPCPartnerInvocationTest.class);

    BpelPackageManagementClient bpelPackageManagementClient;
    RequestSender requestSender;


    public void setEnvironment() throws Exception {
        init();
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }

    @BeforeClass(alwaysRun = true, groups = "wso2.bps.bpelactivities")
    public void deployArtifact()
            throws Exception {
        setEnvironment();
        uploadBpelForTest("RPCServiceProcess");
        uploadBpelForTest("RPCClientProcess");
        requestSender.waitForProcessDeployment(backEndUrl + "RPCClientProcessService");
        requestSender.waitForProcessDeployment(backEndUrl + "RPCServiceProcessService");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "invoke RPC client process")
    private void invokeRPCClientProcess()
            throws Exception {
        String payload = "<rpc:RPCClientProcessRequest xmlns:rpc=\"http://wso2.org/bps/rpcclient\">" +
                "<rpc:input>Hello</rpc:input></rpc:RPCClientProcessRequest>";
        String operation = "hello";
        String serviceName = "RPCClientProcessService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add(">Hello World<");
        log.info("Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("RPCServiceProcess");
        bpelPackageManagementClient.undeployBPEL("RPCClientProcess");
        this.loginLogoutClient.logout();
    }
}
