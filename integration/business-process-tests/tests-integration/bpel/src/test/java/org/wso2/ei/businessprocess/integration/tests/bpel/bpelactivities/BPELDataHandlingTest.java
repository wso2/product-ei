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

public class BPELDataHandlingTest extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPELDataHandlingTest.class);

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
        uploadBpelForTest("TestComposeUrl");
        uploadBpelForTest("FlexibleAssign");
        uploadBpelForTest("TestIgnoreMissingFromData");
        uploadBpelForTest("TestCombineUrl");
        uploadBpelForTest("TestExpandTemplate");
        uploadBpelForTest("TestAssignActivity1");
        uploadBpelForTest("TestAssignDate");
        uploadBpelForTest("TestXslTransform");
        uploadBpelForTest("TestCounter");
        uploadBpelForTest("TestSplit");
        uploadBpelForTest("XMLAttributeProcess");
        requestSender.waitForProcessDeployment(backEndUrl + "XMLAttributeProcessService");
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "this test will use " +
            "composeUrl with the element argument")
    public void composeURLWithElement() throws XMLStreamException, AxisFault {
        String payload = "<un:composeUrl xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<template>http://example.com/{user}/{tag}/{a_missing_var}</template>" +
                "<name/><value/><pairs><user>bill</user><tag>ruby</tag></pairs>" +
                "</un:composeUrl>";
        String operation = "composeUrl";
        String serviceName = "TestComposeUrlService";
        String expectedOutput = "http://example.com/bill/ruby/";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "this test will use composeUrl with a list of " +
            "name, value, name, value, etc")
    public void composeURL() throws XMLStreamException, AxisFault {
        String payload = "<un:composeUrl xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<template>http://example.com/{user}/{a_missing_var}</template>" +
                "<name>user</name><value>bill</value><pairs /></un:composeUrl>";
        String operation = "composeUrl";
        String serviceName = "TestComposeUrlService";
        String expectedOutput = "http://example.com/bill/";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    public void flexibleAssign() throws XMLStreamException, AxisFault {
        String payload = "<flex:typeA xmlns:flex=\"http://wso2.org/bps/schema/FlexibleAssign\">" +
                "<flex:paramA>ee</flex:paramA></flex:typeA>";
        String operation = "operation1";
        String serviceName = "FlexibleAssign";
        String expectedOutput = "ee";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    public void ignoreMissingFromData() throws XMLStreamException, AxisFault {
        String payload = "<test:Input xmlns:test=\"test:test\">yy</test:Input>";
        String operation = "process";
        String serviceName = "TestIgnoreMissingFromDataService";
        String expectedOutput = "Test passed.";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "Combine URL on data handling test case")
    public void combineUrl() throws XMLStreamException, AxisFault {
        String payload = "<un:combineUrl xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<base>http://example.com/html/about.html</base>" +
                "<relative>../experts/</relative></un:combineUrl>";
        String operation = "combineUrl";
        String serviceName = "TestCombineUrlService";
        String expectedOutput = "http://example.com/experts/";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "expanding template with element")
    public void expandTemplateWithElement()
            throws XMLStreamException, AxisFault {
        String payload = "<un:expandTemplate xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<template>http://example.com/{user}/{tag}/{a_missing_var}/" +
                "{another_missing_var=but_with_a_default}</template><name/><value/>" +
                "<pairs><user>bill</user><tag>ruby</tag></pairs></un:expandTemplate>";
        String operation = "expandTemplate";
        String serviceName = "TestExpandTemplateService";
        String expectedOutput = "http://example.com/bill/ruby/{a_missing_var}/but_with_a_default";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "expanding template")
    public void expandTemplate() throws XMLStreamException, AxisFault {
        String payload = "<un:expandTemplate xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<template>http://example.com/{user}/{a_missing_var}/" +
                "{another_missing_var=but_with_a_default}</template><name>user</name>" +
                "<value>bill</value><pairs/></un:expandTemplate>";
        String operation = "expandTemplate";
        String serviceName = "TestExpandTemplateService";
        String expectedOutput = "http://example.com/bill/{a_missing_var}/but_with_a_default";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "assign activity in data handling test case")
    public void assignActivity1() throws XMLStreamException, AxisFault {
        String payload = "<un:testAssign xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<TestPart>Hello</TestPart></un:testAssign>";
        String operation = "testAssign";
        String serviceName = "TestAssignService";
        String expectedOutput = "Hello World7true3";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "assign activity in data handling test case")
    public void assignActivity2() throws XMLStreamException, AxisFault {
        String payload = "<un:testAssign xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<TestPart>Hello</TestPart></un:testAssign>";
        String operation = "testAssign";
        String serviceName = "TestAssignService";
        String expectedOutput = "Hello World7true3";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "assign data in data handling test case")
    public void assignDate() throws XMLStreamException, AxisFault {
        String payload = "<ns1:TaskRequest xmlns:ns1=\"http://example.com/NewDiagram/Pool\">" +
                "start</ns1:TaskRequest>";
        String operation = "Task";
        String serviceName = "TestAssignDateService";
        String expectedOutput = "OK";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "XSLT transform in data handling test case")
    public void XSLTransform() throws XMLStreamException, AxisFault {
        String payload = "<un:helloXsl xmlns:un=\"http://ode/bpel/unit-test.wsdl\"><TestPart>" +
                "<content>Hello</content></TestPart></un:helloXsl>";
        String operation = "helloXsl";
        String serviceName = "HelloXslService";
        String expectedOutput = "HelloXsl World";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "split in data handling test case")
    public void split() throws AxisFault, XMLStreamException {
        String payload = "<un:split xmlns:un=\"http://ode/bpel/unit-test.wsdl\">" +
                "<TestPart>split,me,this,please</TestPart></un:split>";
        String operation = "split";
        String serviceName = "TestSplitService";
        String expectedOutput = "<chunk>split</chunk>" +
                "<chunk>me</chunk>" +
                "<chunk>this</chunk>" +
                "<chunk>please</chunk>";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities", "wso2.bps.bpelactivities"}, description = "counter in data handling test case")
    public void counter() throws AxisFault, XMLStreamException {
        String payload = "<un:initialize xmlns:un=\"http://example.com/bpel/counter\">" +
                "<counterName>foo</counterName></un:initialize>";
        String operation = "initialize";
        String serviceName = "counterService";
        String expectedOutput = "10.0";

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.assertRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.bpelactivities"}, description = "test attribute manipulations in data handling test case")
    public void processProperty() throws Exception {
        String payload = "<p:XMLAttributeProcessRequest xmlns:p=\"http://eclipse.org/bpel/sample\">\n" +
                "      <p:input>1</p:input>\n" +
                "   </p:XMLAttributeProcessRequest>";
        String operation = "initiate";
        String serviceName = "XMLAttributeProcessService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("testUserIdAttribute=\"1\"");
        expectedOutput.add("testAttribute=\"testAttributeValue\"");

        log.info("Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, payload,
                1, expectedOutput, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
            LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("TestComposeUrl");
        bpelPackageManagementClient.undeployBPEL("FlexibleAssign");
        bpelPackageManagementClient.undeployBPEL("TestIgnoreMissingFromData");
        bpelPackageManagementClient.undeployBPEL("TestCombineUrl");
        bpelPackageManagementClient.undeployBPEL("TestExpandTemplate");
        bpelPackageManagementClient.undeployBPEL("TestAssignActivity1");
        bpelPackageManagementClient.undeployBPEL("TestAssignDate");
        bpelPackageManagementClient.undeployBPEL("TestXslTransform");
        bpelPackageManagementClient.undeployBPEL("TestCounter");
        bpelPackageManagementClient.undeployBPEL("TestSplit");
        bpelPackageManagementClient.undeployBPEL("TestXslTransform");
        bpelPackageManagementClient.undeployBPEL("XMLAttributeProcess");
        this.loginLogoutClient.logout();
    }
}
