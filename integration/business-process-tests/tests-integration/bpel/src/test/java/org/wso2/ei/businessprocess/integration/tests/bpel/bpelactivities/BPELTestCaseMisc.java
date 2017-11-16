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
package org.wso2.ei.businessprocess.integration.tests.bpel.bpelactivities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPELTestCaseMisc extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPELTestCaseMisc.class);

    BpelPackageManagementClient bpelPackageManagementClient;
    RequestSender requestSender;


    public void setEnvironment() throws Exception {
        init();
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }

    @BeforeClass(alwaysRun = true)
    public void deployArtifact()
            throws Exception {
        setEnvironment();
        uploadBpelForTest("Async-Client");
        requestSender.waitForProcessDeployment(backEndUrl + "TestCorrelationWithAttribute");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "Async BPEL sample test case")
    public void testAsyncBPELSample() throws Exception {
        String payload = "<p:ClientRequest xmlns:p=\"urn:ode-apache-org:example:async:client\">\n" +
                "      <p:id>1</p:id>\n" +
                "      <p:input>2</p:input>\n" +
                "   </p:ClientRequest>";
        String operation = "process";
        String serviceName = "ClientService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("Server says 2");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "Correlation with attribute sample test case")
    public void testCorrelationWithAttribute() throws Exception {
        String payload = " <p:TestCorrelationWithAttributeRequest xmlns:p=\"http://wso2.org/bps/sample\">\n" +
                "      <!--Exactly 1 occurrence-->\n" +
                "      <input xmlns=\"http://wso2.org/bps/sample\">99ee992</input>\n" +
                "   </p:TestCorrelationWithAttributeRequest>";
        String operation = "process";
        String serviceName = "TestCorrelationWithAttribute";
        List<String> expectedOutput = Collections.emptyList();

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, false);

        Thread.sleep(2000);
        payload = " <p:CallbackOperation xmlns:p=\"http://www.example.org/callback/\">\n" +
                "      <!--Exactly 1 occurrence-->\n" +
                "      <xsd:in xmlns:xsd=\"http://www.example.org/callback/\">99ee992</xsd:in>\n" +
                "   </p:CallbackOperation>";
        operation = "CallbackOperation";
        serviceName = "CallbackService";
        expectedOutput = new ArrayList<String>();
        expectedOutput.add("99ee992");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("Async-Client");
        this.loginLogoutClient.logout();
    }
}
