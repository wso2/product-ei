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
import javax.xml.stream.XMLStreamException;

public class BPELBasicActivitiesTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BPELBasicActivitiesTest.class);

    BpelPackageManagementClient bpelPackageManagementClient;
    RequestSender requestSender;


    public void setEnvironment() throws Exception {
        init();
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }

    @BeforeClass(alwaysRun = true, groups =  "wso2.bps.bpelactivities")
    public void deployArtifact()
            throws Exception {
        setEnvironment();
        Assert.assertTrue(requestSender.isServiceAvailable(backEndUrl + "TestIf"));
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "if true test case")
    public void ifTrue() throws XMLStreamException, InterruptedException, AxisFault {
        String payLoad = " <p:hello xmlns:p=\"http://ode/bpel/unit-test.wsdl\">\n" +
                "      <!--Exactly 1 occurrence-->\n" +
                "      <TestPart>2.00</TestPart>\n" +
                "   </p:hello>";

        String operation = "hello";
        String serviceName = "TestIf";
        String expectedBefore = "Worked";
        requestSender.assertRequest(backEndUrl + serviceName, operation, payLoad,
                1, expectedBefore, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "if false test case")
    public void ifFalse() throws XMLStreamException, InterruptedException, AxisFault {
        String payLoad = " <p:hello xmlns:p=\"http://ode/bpel/unit-test.wsdl\">\n" +
                "      <!--Exactly 1 occurrence-->\n" +
                "      <TestPart>1.00</TestPart>\n" +
                "   </p:hello>";

        String operation = "hello";
        String serviceName = "TestIf";
        String expectedBefore = "Failed";
        requestSender.assertRequest(backEndUrl + serviceName, operation, payLoad,
                1, expectedBefore, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        this.loginLogoutClient.logout();
    }
}
