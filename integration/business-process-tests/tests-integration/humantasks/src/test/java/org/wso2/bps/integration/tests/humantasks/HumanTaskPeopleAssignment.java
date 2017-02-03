/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.bps.integration.tests.humantasks;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.humantasks.HumanTaskClientApiClient;
import org.wso2.bps.integration.common.clients.humantasks.HumanTaskPackageManagementClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.bps.integration.common.utils.BPSTestConstants;
import org.wso2.bps.integration.common.utils.RequestSender;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.*;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This test case test followings scenarios.
 * - PeopleAssignment.
 * - Expression Based People Assignment.
 * - Literal Based People Assignment.
 * - Excluded Owner
 * - Xpath functions.
 * - Union
 * - Except
 * - intersect
 */
public class HumanTaskPeopleAssignment extends BPSMasterTest {

	private static final Log log = LogFactory.getLog(HumanTaskPeopleAssignment.class);
	//Test Automation API Clients
	private HumanTaskClientApiClient clerk1Client, clerk2Client, clerk3Client, clerk4Client, clerk5Client, clerk6Client,
			manager1Client, manager2Client, manager3Client;
	private HumanTaskPackageManagementClient humanTaskPackageManagementClient;
	private UserManagementClient userManagementClient;
	private RequestSender requestSender;
	private URI taskID = null;

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

		//initialize HT Client API for Clerk4 user
		AutomationContext clerk4AutomationContext =
				new AutomationContext("BPS", "bpsServerInstance0001", FrameworkConstants.SUPER_TENANT_KEY, "clerk4");
		LoginLogoutClient clerk4LoginLogoutClient = new LoginLogoutClient(clerk4AutomationContext);
		String clerk4SessionCookie = clerk4LoginLogoutClient.login();

		clerk4Client = new HumanTaskClientApiClient(backEndUrl, clerk4SessionCookie);

		//initialize HT Client API for Clerk5 user
		AutomationContext clerk5AutomationContext =
				new AutomationContext("BPS", "bpsServerInstance0001", FrameworkConstants.SUPER_TENANT_KEY, "clerk5");
		LoginLogoutClient clerk5LoginLogoutClient = new LoginLogoutClient(clerk5AutomationContext);
		String clerk5SessionCookie = clerk5LoginLogoutClient.login();

		clerk5Client = new HumanTaskClientApiClient(backEndUrl, clerk5SessionCookie);

		//initialize HT Client API for Clerk6 user
		AutomationContext clerk6AutomationContext =
				new AutomationContext("BPS", "bpsServerInstance0001", FrameworkConstants.SUPER_TENANT_KEY, "clerk6");
		LoginLogoutClient clerk6LoginLogoutClient = new LoginLogoutClient(clerk6AutomationContext);
		String clerk6SessionCookie = clerk6LoginLogoutClient.login();

		clerk6Client = new HumanTaskClientApiClient(backEndUrl, clerk6SessionCookie);

		//initialize HT Client API for Manager1 user
		AutomationContext manager1AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
				FrameworkConstants.SUPER_TENANT_KEY, "manager1");
		LoginLogoutClient manager1LoginLogoutClient = new LoginLogoutClient(manager1AutomationContext);
		String manager1SessionCookie = manager1LoginLogoutClient.login();
		manager1Client = new HumanTaskClientApiClient(backEndUrl, manager1SessionCookie);

		//initialize HT Client API for Manager2 user
		AutomationContext manager2AutomationContext =
				new AutomationContext("BPS", "bpsServerInstance0001", FrameworkConstants.SUPER_TENANT_KEY, "manager2");
		LoginLogoutClient manager2LoginLogoutClient = new LoginLogoutClient(manager2AutomationContext);
		String manager2SessionCookie = manager2LoginLogoutClient.login();
		manager2Client = new HumanTaskClientApiClient(backEndUrl, manager2SessionCookie);

		//initialize HT Client API for Manager3 user
		AutomationContext manager3AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
				FrameworkConstants.SUPER_TENANT_KEY, "manager3");
		LoginLogoutClient manager3LoginLogoutClient = new LoginLogoutClient(manager3AutomationContext);
		String manager3SessionCookie = manager3LoginLogoutClient.login();
		manager3Client = new HumanTaskClientApiClient(backEndUrl, manager3SessionCookie);
	}

	@BeforeGroups(groups = {"wso2.bps.task.people.assignment"})
	protected void initialize() throws Exception {
		log.info("Initializing HumanTask task creation Test...");
		userManagementClient = new UserManagementClient(backEndUrl, sessionCookie);
		addRoles();
		humanTaskPackageManagementClient = new HumanTaskPackageManagementClient(backEndUrl, sessionCookie);
		log.info("Add users success !");
		deployArtifact();
		requestSender.waitForProcessDeployment(backEndUrl + HumanTaskTestConstants.CLAIM_SERVICE);
	}

	/**
	 * deployArtifact() test1 sample Generic Human Roles.
	 * potentialOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:owners
	 * businessAdministrators - htd:union(htd:getInput("ClaimApprovalRequest")/test10:cust/test10:globleAdmins,htd:getInput("ClaimApprovalRequest")/test10:cust/test10:regionalAdmins)
	 * excludedOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:excludedOwners
	 */
	public void deployArtifact() throws Exception {
		final String artifactLocation = FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS
				+ File.separator + BPSTestConstants.DIR_HUMAN_TASK + File.separator + HumanTaskTestConstants.DIR_PEOPLE_ASSIGNMENT
				+ File.separator + "test1";
		uploadHumanTaskForTest(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, artifactLocation);
	}

	private void addRoles() throws Exception {
		String[] rc1 = new String[]{HumanTaskTestConstants.CLERK1_USER, HumanTaskTestConstants.CLERK2_USER, HumanTaskTestConstants.CLERK3_USER};
		String[] rc2 = new String[]{HumanTaskTestConstants.CLERK3_USER, HumanTaskTestConstants.CLERK4_USER, HumanTaskTestConstants.CLERK5_USER};
		String[] rc3 = new String[]{HumanTaskTestConstants.CLERK4_USER, HumanTaskTestConstants.CLERK5_USER, HumanTaskTestConstants.CLERK6_USER};
		String[] rm1 = new String[]{HumanTaskTestConstants.MANAGER1_USER, HumanTaskTestConstants.MANAGER2_USER};
		String[] rm2 = new String[]{HumanTaskTestConstants.MANAGER2_USER, HumanTaskTestConstants.MANAGER3_USER};

		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE, rc1,
				new String[]{"/permission/admin/login",
						"/permission/admin/manage/humantask/viewtasks"}, false);
		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_2, rc2,
				new String[]{"/permission/admin/login",
						"/permission/admin/manage/humantask/viewtasks"}, false);
		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_3, rc3,
				new String[]{"/permission/admin/login",
						"/permission/admin/manage/humantask/viewtasks"}, false);
		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE, rm1,
				new String[]{"/permission/admin/login",
						"/permission/admin/manage/humantask"}, false);
		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE_2, rm2,
				new String[]{"/permission/admin/login",
						"/permission/admin/manage/humantask"}, false);
	}

	@Test(groups = {"wso2.bps.task.createTask"}, description = "Create Task 1", priority = 1, singleThreaded = true)
	public void createTask() throws Exception {
		String soapBody =
				"<sch:ClaimApprovalData xmlns:sch=\"http://www.example.com/claims/schema\" xmlns:ns=\"http://docs.oasis-open.org/ns/bpel4people/ws-humantask/types/200803\">\n" +
						"         <sch:cust>\n" +
						"            <sch:id>123</sch:id>\n" +
						"            <sch:firstname>Hasitha</sch:firstname>\n" +
						"            <sch:lastname>Aravinda</sch:lastname>\n" +
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
						"         <sch:priority>9</sch:priority>\n" +
						"      </sch:ClaimApprovalData>";
		String operation = "approve";
		String serviceName = "ClaimService";
		List<String> expectedOutput = new ArrayList<String>();
		expectedOutput.add("taskid>");
		log.info("Calling Service: " + backEndUrl + serviceName);
		requestSender.sendRequest(backEndUrl + serviceName, operation, soapBody, 1, expectedOutput, true);
	}

	@Test(groups = {"wso2.bps.task.createTask"}, description = "Check created Task", priority = 2, singleThreaded = true)
	public void checkCreatedTask() throws Exception {
		//Clerk1 can claim this task.
		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.CLAIMABLE);

		//Query as Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1Client.simpleQuery(queryInput);

		TTaskSimpleQueryResultRow[] rows = taskResults.getRow();

		Assert.assertNotNull(rows, "No tasks found. Task creation has failed. ");
		Assert.assertTrue(rows.length == 1, "There should be only one claimable task in the engine, but found " + rows.length + " tasks.");
	}

	@Test(groups = {"wso2.bps.task.claim"}, description = "Clerk1 claim task", priority = 3, singleThreaded = true)
	public void clerk1Claim() throws Exception {
		//Clerk1 can claim this task.
		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.CLAIMABLE);

		//Query as Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1Client.simpleQuery(queryInput);

		TTaskSimpleQueryResultRow[] rows = taskResults.getRow();
		TTaskSimpleQueryResultRow b4pTask = rows[0];
		this.taskID = b4pTask.getId();
		clerk1Client.claim(taskID);

		TTaskAbstract loadedTask = clerk1Client.loadTask(taskID);
		Assert.assertEquals(loadedTask.getActualOwner().getTUser(), HumanTaskTestConstants.CLERK1_USER,
				"The assignee should be clerk1 !");
		Assert.assertEquals(loadedTask.getStatus().toString(), "RESERVED",
				"The task status should be RESERVED!");
	}

	@Test(groups = {"wso2.bps.task.claim"}, description = "Clerk2 claim task which is RESERVED", priority = 4, singleThreaded = true, expectedExceptions = AxisFault.class)
	public void clerk2Claim() throws Exception {
		//Clerk2 can't claim this task since clerk1 already claimed it.
		//Claim As Clerk2 user
		clerk2Client.claim(this.taskID);
	}

	@Test(groups = {"wso2.bps.task.claim"}, description = "Clerk1 release task", priority = 5, singleThreaded = true)
	public void clerk1Release() throws Exception {
		//Release As Clerk1 user
		clerk1Client.release(this.taskID);
		TTaskAbstract loadedTask = clerk1Client.loadTask(taskID);
		Assert.assertNull(loadedTask.getActualOwner(), "Task has an actual owner. Task Release failed");
		Assert.assertEquals(loadedTask.getStatus().toString(), "READY", "The task status should be READY!");
	}

	@Test(groups = {"wso2.bps.task.claim"}, description = "Clerk2 re-claim task and release", priority = 6, singleThreaded = true)
	public void clerk2ReClaimAndRelease() throws Exception {
		//Login As Clerk2 user
		clerk2Client.claim(this.taskID);
		TTaskAbstract loadedTask = clerk2Client.loadTask(taskID);
		Assert.assertEquals(loadedTask.getActualOwner().getTUser(), HumanTaskTestConstants.CLERK2_USER,
				"The assignee should be clerk2 !");
		Assert.assertEquals(loadedTask.getStatus().toString(), "RESERVED",
				"The task status should be RESERVED!");
		clerk2Client.release(this.taskID);
		loadedTask = clerk2Client.loadTask(taskID);
		Assert.assertNull(loadedTask.getActualOwner(), "Task has an actual owner. Task Release failed");
		Assert.assertEquals(loadedTask.getStatus().toString(), "READY", "The task status should be READY!");
	}

	@Test(groups = {"wso2.bps.task.claim"}, description = "Clerk3 (an excluded owner) try to claim", priority = 7, singleThreaded = true, expectedExceptions = AxisFault.class)
	public void clerk3Claim() throws Exception {
		//Login As Clerk3 user
		clerk3Client.claim(this.taskID);
	}

	@Test(groups = { "wso2.bps.task.xpath" }, description = "Test Xpath operation -Union", priority = 10,
			singleThreaded = true)
	public void testUnion() throws Exception {
		// All 3 manager users should able to perform administrative task.

		//Login As manager1 user
		TPriority tPriority = new TPriority();
		tPriority.setTPriority(BigInteger.valueOf(2));
		manager1Client.setPriority(taskID, tPriority);

		TTaskAbstract taskAfterPriorityChange1 = manager1Client.loadTask(taskID);
		TPriority prio1 = taskAfterPriorityChange1.getPriority();
		int newPriority1Int = prio1.getTPriority().intValue();
		Assert.assertEquals(newPriority1Int, 2, "The new priority should be 2 after the set priority operation");

		//Login As manager3 user
		tPriority = new TPriority();
		tPriority.setTPriority(BigInteger.valueOf(3));
		manager3Client.setPriority(taskID, tPriority);

		taskAfterPriorityChange1 = manager3Client.loadTask(taskID);
		TPriority prio2 = taskAfterPriorityChange1.getPriority();
		int newPriority1Int2 = prio2.getTPriority().intValue();
		Assert.assertEquals(newPriority1Int2, 3, "The new priority should be 3 after the set priority operation");

	}

	/**
	 * deployArtifact() test2 artifact. Sample Generic Human Roles.
	 * potentialOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:owners
	 * businessAdministrators - htd:except(htd:getInput("ClaimApprovalRequest")/test10:cust/test10:globleAdmins,htd:getInput("ClaimApprovalRequest")/test10:cust/test10:regionalAdmins)
	 * excludedOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:excludedOwners
	 */
	@Test(groups = { "wso2.bps.task.xpath" }, description = "Deploy and Create Except HumanTask", priority = 20,
			singleThreaded = true)
	public void deployAndCreateExceptHumanTask() throws Exception {

		final String artifactLocation =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS + File.separator +
						BPSTestConstants.DIR_HUMAN_TASK + File.separator + HumanTaskTestConstants.DIR_PEOPLE_ASSIGNMENT +
						File.separator + "test2";
		uploadHumanTaskForTest(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, artifactLocation);
		Thread.sleep(30000); // Wait for new version of task deploy.
		createTask(); // create task
		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1Client.simpleQuery(queryInput);
		TTaskSimpleQueryResultRow[] rows = taskResults.getRow();
		TTaskSimpleQueryResultRow b4pTask = null;
		Assert.assertNotNull(rows, "No tasks found. Task creation has failed. ");
		// looking for the latest task
		for (TTaskSimpleQueryResultRow row : rows) {
			if (b4pTask == null) {
				b4pTask = row;
			} else {
				if (Long.parseLong(b4pTask.getId().toString()) < Long.parseLong(row.getId().toString())) {
					b4pTask = row;
				}
			}
		}

		// Validating Task
		Assert.assertNotNull(b4pTask, "Task creation has failed");
		Assert.assertNotEquals(b4pTask.getId().toString(), this.taskID.toString(), "Task creation failed.");
		this.taskID = b4pTask.getId();

	}

	@Test(groups = {"wso2.bps.task.xpath" }, description = "Test Xpath operation -Except", priority = 21,
			singleThreaded = true)
	public void testExcept() throws Exception {
		// Only Manager1 able to change the priority.
		final int newPriority = 7;
		TPriority tPriority = new TPriority();
		TTaskAbstract taskAfterPriorityChange1;

		tPriority.setTPriority(BigInteger.valueOf(newPriority));
		//Login As manager3 user
		manager1Client.setPriority(taskID, tPriority);
		taskAfterPriorityChange1 = manager1Client.loadTask(taskID);
		TPriority prio2 = taskAfterPriorityChange1.getPriority();
		int newPriority1Int2 = prio2.getTPriority().intValue();
		Assert.assertEquals(newPriority1Int2, newPriority,
				"The new priority should change after setPriority operation.");

	}

	@Test(groups = { "wso2.bps.task.xpath" }, description = "Test Xpath operation - Except negative case",
			priority = 21,
			singleThreaded = true, expectedExceptions = AxisFault.class)
	public void testExceptNegative() throws Exception {
		// Only Manager1 able to change the priority.
		final int newPriority = 8;
		TPriority tPriority = new TPriority();
		TTaskAbstract taskAfterPriorityChange1;

		tPriority.setTPriority(BigInteger.valueOf(newPriority));
		//Login As manager3 user
		manager3Client.setPriority(taskID, tPriority);
		taskAfterPriorityChange1 = manager3Client.loadTask(taskID);
		TPriority prio2 = taskAfterPriorityChange1.getPriority();
		int newPriority1Int2 = prio2.getTPriority().intValue();
		Assert.assertNotEquals(newPriority1Int2, newPriority, "Task priority should not changed.");

	}

	/**
	 * deployArtifact() test3 artifact. Sample Generic Human Roles.
	 * potentialOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:owners
	 * businessAdministrators - htd:intersect(htd:getInput("ClaimApprovalRequest")/test10:cust/test10:globleAdmins,htd:getInput("ClaimApprovalRequest")/test10:cust/test10:regionalAdmins)
	 * excludedOwners - htd:getInput("ClaimApprovalRequest")/test10:cust/test10:excludedOwners
	 */
	@Test(groups = { "wso2.bps.task.xpath" }, description = "Deploy and Create Except HumanTask", priority = 30,
			singleThreaded = true)
	public void deployAndCreateIntersectHumanTask() throws Exception {

		final String artifactLocation =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS + File.separator +
						BPSTestConstants.DIR_HUMAN_TASK + File.separator + HumanTaskTestConstants.DIR_PEOPLE_ASSIGNMENT +
						File.separator + "test3";
		uploadHumanTaskForTest(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, artifactLocation);
		Thread.sleep(30000); // Wait for new version of task deploy.
		createTask(); // create task
		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1Client.simpleQuery(queryInput);
		TTaskSimpleQueryResultRow[] rows = taskResults.getRow();
		TTaskSimpleQueryResultRow b4pTask = null;
		Assert.assertNotNull(rows, "No tasks found. Task creation has failed. ");
		// looking for the latest task
		for (TTaskSimpleQueryResultRow row : rows) {
			if (b4pTask == null) {
				b4pTask = row;
			} else {
				if (Long.parseLong(b4pTask.getId().toString()) < Long.parseLong(row.getId().toString())) {
					b4pTask = row;
				}
			}
		}

		// Validating Task
		Assert.assertNotNull(b4pTask, "Task creation has failed");
		Assert.assertNotEquals(b4pTask.getId().toString(), this.taskID.toString(), "Task creation failed.");
		this.taskID = b4pTask.getId();
	}

	@Test(groups = { "wso2.bps.task.xpath" }, description = "Test Xpath operation - Intersect", priority = 31,
			singleThreaded = true)
	public void testIntersect() throws Exception {
		// Only Manager 2 able to change the priority.
		final int newPriority = 8;
		TPriority tPriority = new TPriority();
		TTaskAbstract taskAfterPriorityChange1;

		tPriority.setTPriority(BigInteger.valueOf(newPriority));
		//Login As manager2 user
		manager2Client.setPriority(taskID, tPriority);
		taskAfterPriorityChange1 = manager2Client.loadTask(taskID);
		TPriority prio2 = taskAfterPriorityChange1.getPriority();
		int newPriority1Int2 = prio2.getTPriority().intValue();
		Assert.assertEquals(newPriority1Int2, newPriority,
				"The new priority should change after setPriority operation.");

	}

	@Test(groups = { "wso2.bps.task.xpath" }, description = "Test Xpath operation - Intersect Negative test case",
			priority = 32,
			singleThreaded = true, expectedExceptions = AxisFault.class)
	public void testIntersectNegative() throws Exception {
		// Only Manager1 able to change the priority.
		final int newPriority = 7;
		TPriority tPriority = new TPriority();
		TTaskAbstract taskAfterPriorityChange1;

		tPriority.setTPriority(BigInteger.valueOf(newPriority));
		//Login As manager3 user
		manager3Client.setPriority(taskID, tPriority);
		taskAfterPriorityChange1 = manager3Client.loadTask(taskID);
		TPriority prio2 = taskAfterPriorityChange1.getPriority();
		int newPriority1Int2 = prio2.getTPriority().intValue();
		Assert.assertNotEquals(newPriority1Int2, newPriority, "Task priority should not changed.");

	}

	/**
	 * deployArtifact() test4 artifact.
	 * Potential owners :
	 * <htt:user>clerk1</htt:user>
	 * <htt:user>clerk2</htt:user>
	 * <htt:user>clerk3</htt:user>
	 * <htt:group>regionalClerksRole2</htt:group>
	 */
	@Test(groups = { "wso2.bps.task.literal" }, description = "Deploy and Create Literal based HumanTask",
			priority = 40, singleThreaded = true)
	public void deployAndCreateLiteralBasedHumanTask() throws Exception {

		final String artifactLocation =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS + File.separator +
						BPSTestConstants.DIR_HUMAN_TASK + File.separator + HumanTaskTestConstants.DIR_PEOPLE_ASSIGNMENT +
						File.separator + "test4";
		uploadHumanTaskForTest(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, artifactLocation);
		Thread.sleep(30000); // Wait for new version of task deploy.
		createTask(); // create task
		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1Client.simpleQuery(queryInput);
		TTaskSimpleQueryResultRow[] rows = taskResults.getRow();
		TTaskSimpleQueryResultRow b4pTask = null;
		Assert.assertNotNull(rows, "No tasks found. Task creation has failed. ");
		// looking for the latest task
		for (TTaskSimpleQueryResultRow row : rows) {
			if (b4pTask == null) {
				b4pTask = row;
			} else {
				if (Long.parseLong(b4pTask.getId().toString()) < Long.parseLong(row.getId().toString())) {
					b4pTask = row;
				}
			}
		}

		// Validating Task
		Assert.assertNotNull(b4pTask, "Task creation has failed");
		Assert.assertNotEquals(b4pTask.getId().toString(), this.taskID.toString(), "Task creation failed.");
		this.taskID = b4pTask.getId();
	}

	@Test(groups = { "wso2.bps.task.literal" }, description = "Perform humanTask",
			priority = 41, singleThreaded = true, expectedExceptions = AxisFault.class)
	public void testLiteralBasedPeopleAssignment() throws Exception {
		// clerk1, clerk2, clerk4, clerk5 users will able to work on task. Clerk3 will be a excluded used.
		boolean failed = false;
		try {
			clerk1Client.start(taskID);
			clerk1Client.release(taskID);
			clerk2Client.start(taskID);
			clerk2Client.release(taskID);
			clerk4Client.start(taskID);
			clerk4Client.release(taskID);
			clerk5Client.start(taskID);
			clerk5Client.release(taskID);
		} catch (Exception e) {
			failed = true;
		}
		Assert.assertTrue(!failed, "Expected users can't perform task");
		clerk6Client.start(taskID);
	}

	@Test(groups = { "wso2.bps.task.clean" }, description = "Clean up server", priority = 100, singleThreaded = true)
	public void cleanTestEnvironment() throws Exception {
		userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE);
		userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_2);
		userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_3);
		userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE);
		userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE_2);
		Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE));
		Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_2));
		Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE_3));
		Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE));
		Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE_2));
		humanTaskPackageManagementClient.unDeployHumanTask(HumanTaskTestConstants.CLAIMS_APPROVAL_PACKAGE_ORG_ENTITY_NAME, "ApproveClaim");
		loginLogoutClient.logout();
	}

}
