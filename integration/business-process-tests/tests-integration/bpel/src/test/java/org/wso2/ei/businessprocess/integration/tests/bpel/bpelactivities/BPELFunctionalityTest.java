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

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

public class BPELFunctionalityTest extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPELFunctionalityTest.class);

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
        uploadBpelForTest("TestAlarm");
        uploadBpelForTest("DynPartner");
        uploadBpelForTest("TestForEach");
        uploadBpelForTest("TestPickOneWay");
        requestSender.waitForProcessDeployment(backEndUrl + "PickService");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "onAlarm BPEL functionality test case")
    public void processProperty() throws AxisFault, XMLStreamException {
        String payload = "<exam:start xmlns:exam=\"http://ode.apache.org/example\">4</exam:start>";
        String operation = "receive";
        String serviceName = "CanonicServiceForClient";
        String expectedOutput = "start";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "Dynamic Partner Links and Dynamic Addressing in BPEL")
    public void dynamicPartner() throws AxisFault, XMLStreamException {
        String payload = "<ns2:dummy xmlns:ns2=\"http://ode/bpel/responder.wsdl\">fire!</ns2:dummy>";
        String operation = "execute";
        String serviceName = "DynMainService";
        String expectedOutput = "OK";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "forEach BPEL functionality test case")
    public void forEach() throws AxisFault, XMLStreamException {
        String payload = "<jms:input xmlns:jms=\"http://www.example.org/jms\">testIf</jms:input>";
        String operation = "start";
        String serviceName = "ForEachService";
        String expectedOutput = "testIf123";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "Pick BPEL functionality test case")
    public void pick() throws AxisFault, XMLStreamException {
        //create new instance
        String payload = "<pic:dealDeck xmlns:pic=\"http://www.stark.com/PickService\">" +
                "         <pic:Deck>testPick</pic:Deck>" +
                "      </pic:dealDeck>";
        String operation = "dealDeck";
        String serviceName = "PickService";
        String expectedOutput = "testPick";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);

        //try the pick service
        payload = "<pic:pickClub xmlns:pic=\"http://www.stark.com/PickService\">" +
                "         <pic:Deck>testPick</pic:Deck>" +
                "      </pic:pickClub>";
        operation = "pickClub";
        serviceName = "PickService";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("TestAlarm");
        bpelPackageManagementClient.undeployBPEL("DynPartner");
        bpelPackageManagementClient.undeployBPEL("TestForEach");
        bpelPackageManagementClient.undeployBPEL("TestPickOneWay");
        this.loginLogoutClient.logout();
    }
}
