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

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelInstanceManagementClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelProcessManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.BPSTestConstants;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.mgt.ProcessManagementException;
import org.wso2.carbon.bpel.stub.mgt.types.ProcessStatus;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BpelVersioningTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BpelVersioningTest.class);

    BpelPackageManagementClient bpelPackageManagementClient;
    BpelProcessManagementClient bpelProcessManagementClient;
    BpelInstanceManagementClient bpelInstanceManagementClient;
    private final int MAX_TIME = 1000;

    private RequestSender requestSender;
    private LinkedList<String> activeStatus;


    public void setEnvironment() throws Exception {
        init();
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        bpelProcessManagementClient = new BpelProcessManagementClient(backEndUrl, sessionCookie);
        bpelInstanceManagementClient = new BpelInstanceManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }

    @BeforeClass(alwaysRun = true)
    public void deployArtifact()
            throws Exception {
        setEnvironment();
        uploadBpelForTest("HelloWorld2");
        requestSender.waitForProcessDeployment(backEndUrl + "HelloService");

    }

    @Test(groups = {"wso2.bps", "wso2.bps.deployment"}, description = "Tests uploading Bpel Service with In memory", singleThreaded = true)
    public void getVersion() throws RemoteException, XMLStreamException, InterruptedException,
            ProcessManagementException {

        Awaitility.await()
                .pollInterval(50, TimeUnit.MILLISECONDS)
                .atMost(MAX_TIME, TimeUnit.SECONDS)
                .until(isInforListAvailable("HelloWorld2"));
        List<String> processBefore = bpelProcessManagementClient.getProcessInfoList("HelloWorld2");
        activeStatus = new LinkedList<String>();
        for (String processid : processBefore) {
            if (bpelProcessManagementClient.getStatus(processid).equals("ACTIVE")) {
                activeStatus.add(processid);
            }
        }
        sendRequest();
    }

    private void sendRequest() throws XMLStreamException, AxisFault {
        String payLoad = " <p:hello xmlns:p=\"http://ode/bpel/unit-test.wsdl\">\n" +
                "      <!--Exactly 1 occurrence--><TestPart>test</TestPart>\n" +
                "   </p:hello>";

        String operation = "hello";
        String serviceName = "HelloService";
        String expectedBefore = "World";
        String expectedAfter = "World-Version";
        requestSender.assertRequest(backEndUrl + serviceName, operation, payLoad,
                1, expectedBefore, true);
    }


    @Test(groups = {"wso2.bps", "wso2.bps.deployment"}, description = "Tests uploading BPEL process in-memory setting", dependsOnMethods = "getVersion")
    public void checkVersion() throws InterruptedException, XMLStreamException, RemoteException,
            ProcessManagementException, PackageManagementException {


        final String artifactLocation = FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS
                + File.separator + BPSTestConstants.DIR_BPEL + File.separator + "VersioningSamples";
        uploadBpelForTest("HelloWorld2", artifactLocation);
        List<String> processAfter = null;
        for (int a = 0; a <= 10; a++) {

            Awaitility.await()
                    .pollInterval(50, TimeUnit.MILLISECONDS)
                    .atMost(MAX_TIME, TimeUnit.SECONDS)
                    .until(isInforListAvailable("HelloWorld2"));
            processAfter = bpelProcessManagementClient.getProcessInfoList("HelloWorld2");

            Awaitility.await()
                    .pollInterval(20, TimeUnit.MILLISECONDS)
                    .atMost(MAX_TIME, TimeUnit.SECONDS)
                    .until(isProcessRetired());
            if (bpelProcessManagementClient.getStatus(activeStatus.get(0)).equals(ProcessStatus.RETIRED.toString()))
                break;
        }

        for (String process : activeStatus)

        {
            Assert.assertTrue(bpelProcessManagementClient.getStatus(process).equals(ProcessStatus.RETIRED.toString()), "Versioning failed : Previous Version " + process + "is still active");
        }

        for (String processInfo : processAfter) {
            if (bpelProcessManagementClient.getStatus(processInfo).equals("ACTIVE")) {
                for (String process : activeStatus) {
                    Assert.assertFalse(process.equals(processInfo), "Versioning failed : Previous Version " + processInfo + " is still active");
                }
            }
        }

        sendRequest();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("HelloWorld2");
        this.loginLogoutClient.logout();
    }

    private Callable<Boolean> isInforListAvailable(final String packageName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (bpelProcessManagementClient.getProcessInfoList(packageName) != null) {
                    return true;
                }
                    return false;
            }
        };
    }

    private Callable<Boolean> isProcessRetired() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (bpelProcessManagementClient.getStatus(activeStatus.get(0)).equals(ProcessStatus.RETIRED.toString())) {
                    return true;
                }
                    return false;
            }
        };
    }

}