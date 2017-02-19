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
import java.util.ArrayList;
import java.util.List;

public class BPELMessageRoutingTest extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPELMessageRoutingTest.class);

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
        uploadBpelForTest("TestCorrelationUnique");
        uploadBpelForTest("MyRoleMexTestProcess");
        requestSender.waitForProcessDeployment(backEndUrl + "MyRoleMexTestProcessService");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "correlation opaque init foo in message routing test")
    private void correlationOpaqueInitFoo()
            throws Exception {
        String payload = "<un:init xmlns:un=\"http://example.com/bpel/counter\"><name>foo</name>" +
                "<alias>foo.alias</alias></un:init>";
        String operation = "init";
        String serviceName = "counterService2";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("initResponse");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "correlation opaque get bar in message routing test",
            dependsOnMethods = "correlationOpaqueGetAndIncrementFoo")
    private void correlationOpaqueGetBar()
            throws Exception {
        String payload = "<un:get xmlns:un=\"http://example.com/bpel/counter\">" +
                "<name>bar</name><alias>get.alias</alias></un:get>";
        String operation = "get";
        String serviceName = "counterService2";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("<name>get.alias</name>");
        expectedOutput.add("<value>0.0</value>");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    /*    @Test(groups = {"wso2.bps"}, description = "correlation opaque get bar fault in message routing test",
              dependsOnMethods = "correlationOpaqueGetBar", expectedExceptions = AxisFault.class)*/
    private void correlationOpaqueGetBarFault() throws XMLStreamException, AxisFault {
        String payload = "<un:get xmlns:un=\"http://example.com/bpel/counter\">" +
                "<name>bar</name><alias>get.alias</alias></un:get>";
        String operation = "get";
        String serviceName = "counterService2";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, "AxisFault", true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "correlation opaque init bar in message routing test",
            dependsOnMethods = "correlationOpaqueInitFoo")
    private void correlationOpaqueInitBar()
            throws Exception {
        String payload = "<un:init xmlns:un=\"http://example.com/bpel/counter\">" +
                "<name>bar</name><alias>bar.alias</alias></un:init>";
        String operation = "init";
        String serviceName = "counterService2";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("initResponse");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "correlation opaque get and increment foo in message" +
            " routing test", dependsOnMethods = "correlationOpaqueInitBar")
    private void correlationOpaqueGetAndIncrementFoo()
            throws Exception {
        String payload = "<un:getAndIncrement xmlns:un=\"http://example.com/bpel/counter\">" +
                "<name>foo</name><alias>incr.alias</alias></un:getAndIncrement>";
        String operation = "getAndIncrement";
        String serviceName = "counterService2";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("<name>foo</name>");
        expectedOutput.add("<value>0.0</value>");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    /*    @Test(groups = {"wso2.bps"}, description = "correlation opaque GetAndIncrementFooFault in message routing test",
              dependsOnMethods = "correlationOpaqueGetAndIncrementFoo", expectedExceptions = AxisFault.class)*/
    private void correlationOpaqueGetAndIncrementFooFault() throws XMLStreamException, AxisFault {
        String payload = "<un:getAndIncrement xmlns:un=\"http://example.com/bpel/counter\">" +
                "<name>foo</name><alias>incr.alias</alias></un:getAndIncrement>";
        String operation = "getAndIncrement";
        String serviceName = "counterService2";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, "AxisFault", true);
    }

    /*    @Test(groups = {"wso2.bps"}, description = "correlation opaque InitFooFault in message routing test",
              dependsOnMethods = "correlationOpaqueInitFoo", expectedExceptions = AxisFault.class)*/
    private void correlationOpaqueInitFooFault() throws XMLStreamException, AxisFault {
        String payload = "<un:init xmlns:un=\"http://example.com/bpel/counter\"><name>foo</name>" +
                "<alias>foo.alias</alias></un:init>";
        String operation = "init";
        String serviceName = "counterService2";

        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, "AxisFault", true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "my Role MEX retention in message routing test",
            dependsOnMethods = "correlationOpaqueGetBar")
    private void myRoleMEXRetention() throws Exception {
        // https://wso2.org/jira/browse/CARBON-9659

        String payload = "<sam:MyRoleMexTestProcessRequest xmlns:sam=\"http://wso2.org/bpel/sample\">" +
                "<sam:input>test</sam:input></sam:MyRoleMexTestProcessRequest>";
        String operation = "init";
        String serviceName = "MyRoleMexTestProcessService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add(">test<");

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);

        payload = "<sam:process xmlns:sam=\"http://wso2.org/bpel/sample\"><sam:in>test</sam:in>" +
                "</sam:process>";
        operation = "process";

        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("TestCorrelationUnique");
        bpelPackageManagementClient.undeployBPEL("MyRoleMexTestProcess");
        this.loginLogoutClient.logout();
    }
}
