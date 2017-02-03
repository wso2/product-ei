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

public class BPELStructuredActivitiesTest extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPELStructuredActivitiesTest.class);

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
        uploadBpelForTest("TestForEach");
        uploadBpelForTest("TestPickOneWay");
        uploadBpelForTest("TestFlowLinks");
        requestSender.waitForProcessDeployment(backEndUrl + "FlowLinkTest");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "for each in structured activities",
            dependsOnMethods = "flowLinks")
    private void forEach() throws Exception {
        String payload = "<input xmlns=\"http://www.example.org/jms\">in</input>";
        String operation = "start";
        String serviceName = "ForEachService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("123");

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "flow links in structured activities")
    private void flowLinks() throws Exception {
        String payload = "<ns1:ExecuteWorkflow xmlns:ns1=\"workflowns\"><value>foo</value>" +
                "</ns1:ExecuteWorkflow>";
        String operation = "ExecuteWorkflow";
        String serviceName = "FlowLinkTest";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("foo");

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "pick one way in structured activities",
            dependsOnMethods = "forEach")
    private void pickOneWay() throws Exception {
        dealDeck();
        pickDiamond();
    }

    private void dealDeck() throws Exception {
        String payload = "<pic:dealDeck xmlns:pic=\"http://www.stark.com/PickService\"><pic:Deck>one</pic:Deck></pic:dealDeck>";
        String operation = "dealDeck";
        String serviceName = "PickService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add(">one<");

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    private void pickDiamond() throws Exception {
        String payload = "<pic:pickDiamond xmlns:pic=\"http://www.stark.com/PickService\"><pic:Deck>one</pic:Deck></pic:pickDiamond>";
        String operation = "pickDiamond";
        String serviceName = "PickService";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, new ArrayList<String>(), false);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("TestFlowLinks");
        bpelPackageManagementClient.undeployBPEL("TestForEach");
        bpelPackageManagementClient.undeployBPEL("TestPickOneWay");
        this.loginLogoutClient.logout();
    }
}
