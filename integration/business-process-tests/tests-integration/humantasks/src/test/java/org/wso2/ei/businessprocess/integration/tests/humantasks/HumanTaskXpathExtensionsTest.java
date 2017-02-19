/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.businessprocess.integration.tests.humantasks;

import org.apache.axis2.databinding.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.humantasks.HumanTaskClientApiClient;
import org.wso2.ei.businessprocess.integration.common.clients.humantasks.HumanTaskPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.BPSTestConstants;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.*;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration test for human task Xpath functions:concat(), concatWithDelimiter(), leastFrequentOccurrence(),
 * mostFrequentOccurrence(), voteOnString(),and(), or(), vote(),max(), min(), avg(), sum()
 * Test deploys artifacts from test6 folder and they depends on ClaimService service.
 */
public class HumanTaskXpathExtensionsTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(HumanTaskPeopleAssignment.class);
    //Test Automation API Clients
    private HumanTaskClientApiClient clerk1Client, clerk2Client, clerk3Client, manager1Client, manager3Client;
    private HumanTaskPackageManagementClient humanTaskPackageManagementClient;
    private UserManagementClient userManagementClient;
    private RequestSender requestSender;
    private URI taskID = null;

    /**
     * Setup the test environment.
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();  //init master class
        humanTaskPackageManagementClient = new HumanTaskPackageManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
        initialize();

        //initialize HT Client API for Clerk1 user
        AutomationContext clerk1AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "clerk1");
        LoginLogoutClient clerk1LoginLogoutClient = new LoginLogoutClient(clerk1AutomationContext);
        String clerk1SessionCookie = clerk1LoginLogoutClient.login();

        clerk1Client = new HumanTaskClientApiClient(backEndUrl, clerk1SessionCookie);

        //initialize HT Client API for Clerk2 user
        AutomationContext clerk2AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "clerk2");
        LoginLogoutClient clerk2LoginLogoutClient = new LoginLogoutClient(clerk2AutomationContext);
        String clerk2SessionCookie = clerk2LoginLogoutClient.login();

        clerk2Client = new HumanTaskClientApiClient(backEndUrl, clerk2SessionCookie);

        //initialize HT Client API for Clerk3 user
        AutomationContext clerk3AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "clerk3");
        LoginLogoutClient clerk3LoginLogoutClient = new LoginLogoutClient(clerk3AutomationContext);
        String clerk3SessionCookie = clerk3LoginLogoutClient.login();

        clerk3Client = new HumanTaskClientApiClient(backEndUrl, clerk3SessionCookie);

        //initialize HT Client API for Manager1 user
        AutomationContext manager1AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "manager1");
        LoginLogoutClient manager1LoginLogoutClient = new LoginLogoutClient(manager1AutomationContext);
        String manager1SessionCookie = manager1LoginLogoutClient.login();
        manager1Client = new HumanTaskClientApiClient(backEndUrl, manager1SessionCookie);

        //initialize HT Client API for Manager3 user
        AutomationContext manager3AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "manager3");
        LoginLogoutClient manager3LoginLogoutClient = new LoginLogoutClient(manager3AutomationContext);
        String manager3SessionCookie = manager3LoginLogoutClient.login();
        manager3Client = new HumanTaskClientApiClient(backEndUrl, manager3SessionCookie);

        createTask();

    }

    /**
     * Initialize the test by deploying required artifacts.
     * @throws Exception
     */
    @BeforeGroups(groups = { "wso2.bps.task.people.assignment" })
    protected void initialize() throws Exception {
        log.info("Initializing HumanTask task creation Test...");
        userManagementClient = new UserManagementClient(backEndUrl, sessionCookie);
        addRoles();
        humanTaskPackageManagementClient = new HumanTaskPackageManagementClient(backEndUrl, sessionCookie);
        log.info("Add users success !");
        humanTaskPackageManagementClient
                .unDeployHumanTask(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, "ApproveClaim");
        deployArtifact();
        requestSender.waitForProcessDeployment(backEndUrl + "ClaimService");
    }

    /**
     * deployArtifact() test1 sample Generic Human Roles. potentialOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:owners
     * businessAdministrators - htd:union(htd:getInput("ClaimApprovalRequest")/test10:cust/test10:globleAdmins,htd:getInput("ClaimApprovalRequest")/test10:cust/test10:regionalAdmins)
     * excludedOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:excludedOwners
     * @throws Exception
     */
    public void deployArtifact() throws Exception {
        final String artifactLocation =
                FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS + File.separator
                        + BPSTestConstants.DIR_HUMAN_TASK + File.separator
                        + HumanTaskTestConstants.DIR_PEOPLE_ASSIGNMENT + File.separator + "test6";
        uploadHumanTaskForTest(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, artifactLocation);
    }

    /**
     * Add required user roles to the test server
     * @throws Exception
     */
    private void addRoles() throws Exception {
        String[] rc1 = new String[] { HumanTaskTestConstants.CLERK1_USER, HumanTaskTestConstants.CLERK2_USER,
                HumanTaskTestConstants.CLERK3_USER };
        String[] rc2 = new String[] { HumanTaskTestConstants.CLERK3_USER, HumanTaskTestConstants.CLERK4_USER,
                HumanTaskTestConstants.CLERK5_USER };
        String[] rc3 = new String[] { HumanTaskTestConstants.CLERK4_USER, HumanTaskTestConstants.CLERK5_USER,
                HumanTaskTestConstants.CLERK6_USER };
        String[] rm1 = new String[] { HumanTaskTestConstants.MANAGER1_USER, HumanTaskTestConstants.MANAGER2_USER };
        String[] rm2 = new String[] { HumanTaskTestConstants.MANAGER2_USER, HumanTaskTestConstants.MANAGER3_USER };

        userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE, rc1,
                new String[] { "/permission/admin/login", "/permission/admin/manage/humantask/viewtasks" }, false);
        userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_2, rc2,
                new String[] { "/permission/admin/login", "/permission/admin/manage/humantask/viewtasks" }, false);
        userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_3, rc3,
                new String[] { "/permission/admin/login", "/permission/admin/manage/humantask/viewtasks" }, false);
        userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE, rm1,
                new String[] { "/permission/admin/login", "/permission/admin/manage/humantask" }, false);
        userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE_2, rm2,
                new String[] { "/permission/admin/login", "/permission/admin/manage/humantask" }, false);
    }

    /**
     * Create a new human task
     * @throws Exception
     */
    private void createTask() throws Exception {
        String soapBody =
                "<sch:ClaimApprovalData xmlns:sch=\"http://www.example.com/claims/schema\" xmlns:ns=\"http://docs.oasis-open.org/ns/bpel4people/ws-humantask/types/200803\">\n"
                        +
                        "         <sch:cust>\n" +
                        "            <sch:id>123</sch:id>\n" +
                        "            <sch:initial>A</sch:initial>\n" +
                        "            <sch:initial>B</sch:initial>\n" +
                        "            <sch:initial>C</sch:initial>\n" +
                        "            <sch:firstname>Hasitha</sch:firstname>\n" +
                        "            <sch:lastname>Aravinda</sch:lastname>\n" +
                        "            <sch:othername>name1</sch:othername>" +
                        "            <sch:othername>name2</sch:othername>" +
                        "            <sch:othername>name3</sch:othername>" +
                        "            <sch:custRegion>LK</sch:custRegion>" +
                        "            <sch:custRegion>UK</sch:custRegion>" +
                        "            <sch:custRegion>LK</sch:custRegion>" +
                        "            <sch:custRegion>LK</sch:custRegion>" +
                        "            <sch:custRegion>UK</sch:custRegion>" +
                        "            <sch:custRegion>US</sch:custRegion>" +
                        "            <sch:custRegion>IN</sch:custRegion>" +
                        "            <sch:custRegion>DK</sch:custRegion>" +
                        "            <sch:custRegion>DK</sch:custRegion>" +
                        "            <sch:custArrears>arr_DK</sch:custArrears>" +
                        "            <sch:custArrears>arr_DK</sch:custArrears>" +
                        "            <sch:custArrears>arr_DK</sch:custArrears>" +
                        "            <sch:custArrears>arr_LK</sch:custArrears>" +
                        "            <sch:custArrears>arr_LK</sch:custArrears>" +
                        "            <sch:custArrears>arr_LK</sch:custArrears>" +
                        "            <sch:custArrears>arr_UK</sch:custArrears>" +
                        "            <sch:custArrears>arr_UK</sch:custArrears>" +
                        "            <sch:custArrears>arr_SW</sch:custArrears>" +
                        "            <sch:boolTrue>true</sch:boolTrue>" +
                        "            <sch:boolTrue>1</sch:boolTrue>" +
                        "            <sch:boolTrue>TRUE</sch:boolTrue>" +
                        "            <sch:boolTrue>True</sch:boolTrue>" +
                        "            <sch:boolFalse>false</sch:boolFalse>" +
                        "            <sch:boolFalse>0</sch:boolFalse>" +
                        "            <sch:boolFalse>False</sch:boolFalse>" +
                        "            <sch:boolFalse>FALSE</sch:boolFalse>" +
                        "            <sch:boolMix>FALSE</sch:boolMix>" +
                        "            <sch:boolMix>true</sch:boolMix>" +
                        "            <sch:boolMix>0</sch:boolMix>" +
                        "            <sch:boolMix>1</sch:boolMix>" +
                        "            <sch:boolMix>1</sch:boolMix>" +
                        "            <sch:amount>2500</sch:amount>" +
                        "            <sch:amount>2000</sch:amount>" +
                        "            <sch:amount>500</sch:amount>" +
                        "            <sch:amount>-500</sch:amount>" +
                        "            <sch:amount>4000</sch:amount>" +
                        "            <sch:amount>3500</sch:amount>" +
                        "            <sch:owners>\n" +
                        "               <ns:group>" + HumanTaskTestConstants.REGIONAL_CLERKS_ROLE + "</ns:group>\n" +
                        "            </sch:owners>\n" +
                        "            <sch:excludedOwners>\n" +
                        "               <ns:user>" + HumanTaskTestConstants.CLERK3_USER + "</ns:user>\n" +
                        "            </sch:excludedOwners>\n" +
                        "            <sch:globleAdmins>\n" +
                        "               <ns:group>" + HumanTaskTestConstants.REGIONAL_MANAGER_ROLE + "</ns:group>\n" +
                        "            </sch:globleAdmins>\n" +
                        "            <sch:regionalAdmins>\n" +
                        "               <ns:group>" + HumanTaskTestConstants.REGIONAL_MANAGER_ROLE_2 + "</ns:group>\n" +
                        "            </sch:regionalAdmins>\n" +
                        "         </sch:cust>\n" +
                        "         <sch:amount>2500</sch:amount>\n" +
                        "         <sch:region>lk</sch:region>\n" +
                        "         <sch:priority>7</sch:priority>\n" +
                        "      </sch:ClaimApprovalData>";
        String operation = "approve";
        String serviceName = "ClaimService";
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("taskid>");
        log.info("Calling Service: " + backEndUrl + serviceName);
        requestSender.sendRequest(backEndUrl + serviceName, operation, soapBody, 1, expectedOutput, true);
        setTaskID();
    }

    private void setTaskID() throws Exception {
        //Clerk1 can claim this task.
        TSimpleQueryInput queryInput = new TSimpleQueryInput();
        queryInput.setPageNumber(0);
        queryInput.setSimpleQueryCategory(TSimpleQueryCategory.CLAIMABLE);

        //Query as Clerk1 user
        TTaskSimpleQueryResultSet taskResults = clerk1Client.simpleQuery(queryInput);

        TTaskSimpleQueryResultRow[] rows = taskResults.getRow();
        TTaskSimpleQueryResultRow b4pTask = rows[0];
        this.taskID = b4pTask.getId();
    }

    /**
     * Test the Human task Xpath string functions - concat(),concatWithDelimiter();mostFrequentOccurrence(),leastFrequentOccurrence()
     * @throws Exception
     */
    @Test(groups = {
            "wso2.bps.task.xpath" }, description = "Test Xpath string operations", priority = 10, singleThreaded = true)
    public void testStringFunctions()
            throws Exception {
        TTaskAbstract humanTask = manager1Client.loadTask(taskID);
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("ABC"),
                "Concat() method test, subject should contain ABC");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("name1 name2 name3"),
                "concatWithDelimiter() method test, should contain name1 name2 name3");

        //happy scenario for mostFrequentOccurrence()
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("LK"),
                "mostFrequentOccurrence() test, should contain LK");
        //when there is a tie, empty is returned
        Assert.assertFalse(humanTask.getPresentationSubject().getTPresentationSubject().contains("arr_DK") || humanTask
                        .getPresentationSubject().getTPresentationSubject().contains("arr_LK") || humanTask
                        .getPresentationSubject().getTPresentationSubject().contains("arr_UK") || humanTask
                        .getPresentationSubject().getTPresentationSubject().contains("arr_US"),
                "mostFrequentOccurrence() test, should not contain any arr_* since a tie");

        //happy scenario for leastFrequentOccurrence()
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("arr_SW"),
                "leastFrequentOccurrence() test, should contain arr_US");
        //when there is a tie empty is returned
        Assert.assertFalse(
                humanTask.getPresentationDescription().getTPresentationDescription().contains("LK") || humanTask
                        .getPresentationDescription().getTPresentationDescription().contains("UK") || humanTask
                        .getPresentationDescription().getTPresentationDescription().contains("IN") || humanTask
                        .getPresentationDescription().getTPresentationDescription().contains("DK") || humanTask
                        .getPresentationDescription().getTPresentationDescription().contains("US"),
                "leastFrequentOccurrence() test, should not contain any region since a tie");

        //voteOnString with 40%, has only 33%
        Assert.assertFalse(humanTask.getPresentationSubject().getTPresentationSubject().contains("vote40_LK"),
                "vote() should not return highest occurrence LK, since low percentage");
        //vote on 20%
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("vote20_LK"),
                "vote() should return LK");
        //empty nodeset
        Assert.assertFalse(humanTask.getPresentationSubject().getTPresentationSubject().contains("voteEmpty_LK"),
                "vote() should return empty string for empty list");
    }

    /**
     * Test the Human task Xpath boolean functions - and(),or(),vote()
     * @throws Exception
     */
    @Test(groups = {
            "wso2.bps.task.xpath" }, description = "Test Xpath boolean operations", priority = 10, singleThreaded = true)
    public void testBooleanFunctions()
            throws Exception {
        TTaskAbstract humanTask = manager1Client.loadTask(taskID);
        //And function
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("andTrue_true"),
                "and() should return true when all true");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("andFalse_false"),
                "and() should return false when all false");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("andMix_false"),
                "and() should return false when mix boolean inputs");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("andEmpty_false"),
                "an() should return false when empty node set");
        //or function
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("orTrue_true"),
                "or() should return true when all true");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("orFalse_false"),
                "or() should return false when all false");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("orMix_true"),
                "or() should return true when mix boolean inputs");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("orEmpty_false"),
                "or()should return false when empty set");

        //vote function
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("vote10_true"),
                "vote() should return true since true has 60%");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("vote70_false"),
                "vote() should return false since true has 60%");
        Assert.assertTrue(humanTask.getPresentationSubject().getTPresentationSubject().contains("vote5_false"),
                "vote() should return false since false is highest");
    }

    /**
     * Test the Human task Xpath Number functions - avg(),min(),max()
     * @throws Exception
     */
    @Test(groups = {
            "wso2.bps.task.xpath" }, description = "Test Xpath number operations", priority = 10, singleThreaded = true)
    public void testNumberFunctions()
            throws Exception {
        TTaskAbstract humanTask = manager1Client.loadTask(taskID);
        //avg
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("avg_2000"),
                "avg() should return 2000");
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("min_-500"),
                "min() should return -500");
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("max_4000"),
                "max() should return 4000");
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("avg_NaN"),
                "avg() should return NaN for empty list");
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("min_NaN"),
                "min() should return NaN for empty list");
        Assert.assertTrue(humanTask.getPresentationDescription().getTPresentationDescription().contains("max_NaN"),
                "max() should return NaN for empty list");

    }

    /**
     * Clenup the test environment after the test.
     * @throws Exception
     */
    @AfterClass(groups = { "wso2.bps.task.clean" }, description = "Clean up server")
    public void cleanTestEnvironment()
            throws Exception {
        userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE);
        userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_2);
        userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_3);
        userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE);
        userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE_2);
        humanTaskPackageManagementClient
                .unDeployHumanTask(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, "ApproveClaim");
        loginLogoutClient.logout();
    }

}
