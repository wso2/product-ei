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
package org.wso2.bps.integration.tests.coordination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpel.BpelInstanceManagementClient;
import org.wso2.bps.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.bps.integration.common.clients.humantasks.HumanTaskClientApiClient;
import org.wso2.bps.integration.common.clients.humantasks.HumanTaskPackageManagementClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.bps.integration.common.utils.BPSTestConstants;
import org.wso2.bps.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.mgt.types.InstanceInfoType;
import org.wso2.carbon.bpel.stub.mgt.types.VariableInfoType;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.*;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HumanTaskCoordinationTest extends BPSMasterTest {

	private static Log log = LogFactory.getLog(HumanTaskCoordinationTest.class);

	private static int testNumber = 0;
	//Test Automation API Clients
	private BpelPackageManagementClient bpelPackageManagementClient;
	private HumanTaskPackageManagementClient humanTaskPackageManagementClient;
	private BpelInstanceManagementClient instanceManagementClient;
	private UserManagementClient userManagementClient;
	private HumanTaskClientApiClient clerk1HumanTaskClientApiClient;
	private HumanTaskClientApiClient manager1HumanTaskClientApiClient;
	private RequestSender requestSender;
	private ServerConfigurationManager serverConfigurationManager;

	/**
	 * Setting up Server and Apply new Configuration Files.
	 *
	 * @throws Exception
	 */
	@BeforeTest(alwaysRun = true)
	public void setupTest() throws Exception {
		log.info("Initializing HumanTaskCoordination Test Case");
		init();
		requestSender = new RequestSender();
		userManagementClient = new UserManagementClient(backEndUrl, sessionCookie);
		log.info("Deploy ClaimsApprovalProcess and ClaimsApprovalTask artifacts");
		deployArtifact();
		log.info("Adding Users and Roles");
		addRoles();
		log.info("Enable HumanTask coordination and restarting server.");
		serverConfigurationManager = new ServerConfigurationManager(bpsServer);
		applyCoordinationConfig();
		setupTestClients();
	}

	/**
	 * Deploy ClaimsApprovalProcess.zip BPEL process and ClaimsApprovalTask.zip HumanTask artifacts.
	 *
	 * @throws Exception
	 */
	public void deployArtifact() throws Exception {

		final String artifactLocation =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS + File.separator +
				BPSTestConstants.DIR_HUMAN_TASK + File.separator + BPSTestConstants.DIR_SCENARIOS + File.separator +
				BPSTestConstants.DIR_HT_COORDINATION;
		uploadBpelForTest(BPSTestConstants.CLAIMS_APPROVAL_PROCESS, artifactLocation);
		uploadHumanTaskForTest(BPSTestConstants.CLAIMS_APPROVAL_TASK, artifactLocation);
	}

	/**
	 * Copy humantask.xml and b4p-coordination-config.xml configuration files and restart server
	 *
	 * @throws Exception
	 */
	private void applyCoordinationConfig() throws Exception {
		final String artifactLocation =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_CONFIG + File.separator +
				BPSTestConstants.DIR_HT_COORDINATION + File.separator;

		File humantaskConfigNew = new File(artifactLocation + BPSTestConstants.HUMANTASK_XML);
		File humantaskConfigOriginal = new File(
				FrameworkPathUtil.getCarbonHome() + File.separator + BPSTestConstants.NEW_CONF_DIR + File.separator + BPSTestConstants.HUMANTASK_XML);
		// Do not restart server. We need to apply another config file.
		serverConfigurationManager.applyConfiguration(humantaskConfigNew, humantaskConfigOriginal, true, false);

		File b4pConfigNew = new File(artifactLocation + BPSTestConstants.B4P_COORDINATION_CONFIG_XML);
		File b4pConfigOriginal = new File(FrameworkPathUtil.getCarbonHome() + File.separator + BPSTestConstants.NEW_CONF_DIR + File.separator +
		                                  BPSTestConstants.B4P_COORDINATION_CONFIG_XML);
		serverConfigurationManager.applyConfiguration(b4pConfigNew, b4pConfigOriginal, true, true);
	}

	/**
	 * Apply WS-Coordination enabled config files.
	 * Copy humantask.xml and b4p-coordination-config.xml configuration files and restart server
	 *
	 * @throws Exception
	 */
	private void applyWSCoordinationConfig() throws Exception {
		final String artifactLocation =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_CONFIG + File.separator +
				HumanTaskTestConstants.DIR_WS_COORDINATION + File.separator;

		File humantaskConfigNew = new File(artifactLocation + BPSTestConstants.HUMANTASK_XML);
		File humantaskConfigOriginal = new File(
				FrameworkPathUtil.getCarbonHome() + File.separator + BPSTestConstants.NEW_CONF_DIR + File.separator + BPSTestConstants.HUMANTASK_XML);
		// Do not restart server. We need to apply another config file.
		serverConfigurationManager.applyConfiguration(humantaskConfigNew, humantaskConfigOriginal, true, false);

		File b4pConfigNew = new File(artifactLocation + BPSTestConstants.B4P_COORDINATION_CONFIG_XML);
		File b4pConfigOriginal = new File(FrameworkPathUtil.getCarbonHome() + File.separator + BPSTestConstants.NEW_CONF_DIR + File.separator +
		                                  BPSTestConstants.B4P_COORDINATION_CONFIG_XML);
		serverConfigurationManager.applyConfiguration(b4pConfigNew, b4pConfigOriginal, true, true);
	}

	/**
	 * Setup
	 *
	 * @throws Exception
	 */
	private void setupTestClients() throws Exception {
		init();
		requestSender.waitForProcessDeployment(backEndUrl + HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE);
		requestSender.waitForProcessDeployment(backEndUrl + HumanTaskTestConstants.CLAIM_SERVICE);
		log.info("BPEL and Humantask services are up and running");
		// Need to re-initialize since we have restarted the server
		bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
		humanTaskPackageManagementClient = new HumanTaskPackageManagementClient(backEndUrl, sessionCookie);
		instanceManagementClient = new BpelInstanceManagementClient(backEndUrl, sessionCookie);
		log.info("Server setting up completed ...!!!");
		serverConfigurationManager = new ServerConfigurationManager(bpsServer);

		//initialize HT Client API for Clerk1 and manager1 user
		AutomationContext clerk1AutomationContext =
				new AutomationContext("BPS", "bpsServerInstance0001", FrameworkConstants.SUPER_TENANT_KEY,
				                      HumanTaskTestConstants.CLERK1_USER);
		LoginLogoutClient clerk1LoginLogoutClient = new LoginLogoutClient(clerk1AutomationContext);
		String clerk1SessionCookie = clerk1LoginLogoutClient.login();
		clerk1HumanTaskClientApiClient = new HumanTaskClientApiClient(backEndUrl, clerk1SessionCookie);

		AutomationContext manager1AutomationContext =
				new AutomationContext("BPS", "bpsServerInstance0001", FrameworkConstants.SUPER_TENANT_KEY,
				                      HumanTaskTestConstants.MANAGER1_USER);
		LoginLogoutClient manager1LoginLogoutClient = new LoginLogoutClient(manager1AutomationContext);
		String manager1SessionCookie = manager1LoginLogoutClient.login();
		manager1HumanTaskClientApiClient = new HumanTaskClientApiClient(backEndUrl, manager1SessionCookie);

	}

	/**
	 * Adding Roles and Users for test case.
	 *
	 * @throws Exception
	 */
	private void addRoles() throws Exception {
		String[] clerkUsers = new String[] { HumanTaskTestConstants.CLERK1_USER, HumanTaskTestConstants.CLERK2_USER };
		String[] managerUsers = new String[] { HumanTaskTestConstants.MANAGER1_USER };
		String[] coordinationUser = new String[] {HumanTaskTestConstants.HT_COORDINATOR_USER};

		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE, clerkUsers,
		                             new String[] { "/permission/admin/login",
		                                            "/permission/admin/manage/humantask/viewtasks" }, false);
		userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE, managerUsers,
		                             new String[] { "/permission/admin/login",
		                                            "/permission/admin/manage/humantask/viewtasks" }, false);

		userManagementClient.addRole(HumanTaskTestConstants.HT_COORDINATOR_ROLE, coordinationUser,
		                             new String[] { "/permission/admin/login",
		                                            "/permission/admin/manage/humantask/viewtasks" }, false);

	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Test Creation with coordination", priority = 1,
			singleThreaded = true)
	public void testDefault() throws Exception {
		final String testID = getTestNumber();
		log.info(testID + ": Test task creation and completion when task coordination is enabled.");
		String soapBody = HumanTaskTestConstants.createClaimApprovalProcessRequest(testID, "Hasitha", "Aravinda", 5000);

		List<String> expectedOutput = Collections.emptyList();
		log.info("Calling Service: " + backEndUrl +
		         HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE);
		requestSender.sendRequest(backEndUrl + HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE,
		                          HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_OPERATION, soapBody, 1, expectedOutput,
		                          false);

		// Wait until task create.
		Thread.sleep(5000);

		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1HumanTaskClientApiClient.simpleQuery(queryInput);
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
		String claimApprovalRequest = (String) clerk1HumanTaskClientApiClient.getInput(b4pTask.getId(), null);
		Assert.assertNotNull(claimApprovalRequest, "The input of the Task:" + b4pTask.getId() + " is null.");
		Assert.assertFalse(!claimApprovalRequest.contains(testID), "Unexpected input found for the Task");

		// Perform Task Operation
		// Claim, Start and Complete.
		log.info("Clerk 1 is performing HumanTask");
		clerk1HumanTaskClientApiClient.claim(b4pTask.getId());
		clerk1HumanTaskClientApiClient.start(b4pTask.getId());
		clerk1HumanTaskClientApiClient.complete(b4pTask.getId(), HumanTaskTestConstants.createClaimTaskOutput(true));

		Thread.sleep(5000);
		List<String> instances =
				instanceManagementClient.listInstances(HumanTaskTestConstants.CLAIM_APPROVAL_NAMESPACE, 1);

		Assert.assertTrue(instances.size() == 1, "Number of process instances not equal to one.");
		log.info("Waiting for Process instance to Complete.");
		InstanceInfoType instanceInfo = null;
		boolean isInstanceCompleted = false;
		// Process completes in PT1M time.
		for (int count = 0; count < 20; count++) {
			Thread.sleep(5000);
			instanceInfo = instanceManagementClient.getInstanceInfo(instances.get(0));
			if (instanceInfo.getStatus().getValue().equals(HumanTaskTestConstants.COMPLETED)) {
				isInstanceCompleted = true;
				log.info("Instance COMPLETED");
				break;
			}
		}
		Assert.assertTrue(isInstanceCompleted, "Status of instance " + instances.get(0) + " is not equal to COMPLETED");
		instanceManagementClient.deleteAllInstances();
		log.info(testID + " Completed.");
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Process instance terminate", priority = 2,
			singleThreaded = true)
	public void testBPELTerminate() throws Exception {
		final String testID = getTestNumber();
		log.info(testID + ": Task Exit when process instance terminates before task completion.");
		String soapBody = HumanTaskTestConstants.createClaimApprovalProcessRequest(testID, "Hasitha", "Aravinda", 5000);

		List<String> expectedOutput = Collections.emptyList();
		log.info("Calling Service: " + backEndUrl +
		         HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE);
		requestSender.sendRequest(backEndUrl + HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE,
		                          HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_OPERATION, soapBody, 1, expectedOutput,
		                          false);

		// Wait until task create.
		Thread.sleep(5000);

		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1HumanTaskClientApiClient.simpleQuery(queryInput);
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
		String claimApprovalRequest = (String) clerk1HumanTaskClientApiClient.getInput(b4pTask.getId(), null);
		Assert.assertNotNull(claimApprovalRequest, "The input of the Task:" + b4pTask.getId() + " is null.");
		Assert.assertFalse(!claimApprovalRequest.contains(testID), "Unexpected input found for the Task");

		List<String> instances =
				instanceManagementClient.listInstances(HumanTaskTestConstants.CLAIM_APPROVAL_NAMESPACE, 1);

		Assert.assertTrue(instances.size() == 1, "Number of process instances not equal to one.");
		log.info("Waiting for Process instance to Terminate.");
		InstanceInfoType instanceInfo;
		boolean isInstanceTerminated = false;
		// Process completes in PT30S time.
		for (int count = 0; count < 20; count++) {
			Thread.sleep(5000);
			instanceInfo = instanceManagementClient.getInstanceInfo(instances.get(0));
			if (instanceInfo.getStatus().getValue().equals(HumanTaskTestConstants.TERMINATED)) {
				isInstanceTerminated = true;
				log.info("Instance TERMINATED");
				break;
			}
		}
		Assert.assertTrue(isInstanceTerminated,
		                  "Status of instance " + instances.get(0) + " is not equal to TERMINATED");
		log.info("Waiting for Task coordination.");
		// Exit Protocol message takes few seconds.
		Thread.sleep(5000);
		TTaskAbstract taskData = clerk1HumanTaskClientApiClient.loadTask(b4pTask.getId());
		Assert.assertEquals(taskData.getStatus().toString(), HumanTaskTestConstants.EXITED,
		                    "Task is not in EXITED status.");
		instanceManagementClient.deleteAllInstances();
		log.info(testID + " Completed.");
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Process instance terminate by management API",
			priority = 3, singleThreaded = true)
	public void testBPELTerminateViaMgtAPI() throws Exception {
		final String testID = getTestNumber();
		log.info(testID + ": Task Exit when process instance terminates using managment API before task completion.");
		String soapBody = HumanTaskTestConstants.createClaimApprovalProcessRequest(testID, "Hasitha", "Aravinda", 5000);

		List<String> expectedOutput = Collections.emptyList();
		log.info("Calling Service: " + backEndUrl +
		         HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE);
		requestSender.sendRequest(backEndUrl + HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE,
		                          HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_OPERATION, soapBody, 1, expectedOutput,
		                          false);

		// Wait until task create.
		Thread.sleep(5000);

		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1HumanTaskClientApiClient.simpleQuery(queryInput);
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
		String claimApprovalRequest = (String) clerk1HumanTaskClientApiClient.getInput(b4pTask.getId(), null);
		Assert.assertNotNull(claimApprovalRequest, "The input of the Task:" + b4pTask.getId() + " is null.");
		Assert.assertFalse(!claimApprovalRequest.contains(testID), "Unexpected input found for the Task");

		List<String> instances =
				instanceManagementClient.listInstances(HumanTaskTestConstants.CLAIM_APPROVAL_NAMESPACE, 1);

		Assert.assertTrue(instances.size() == 1, "Number of process instances not equal to one.");
		log.info("Terminating Process instance using management API");
		instanceManagementClient
				.performAction(instances.get(0), BpelInstanceManagementClient.InstanceOperation.TERMINATE);
		InstanceInfoType instanceInfo;
		boolean isInstanceTerminated = false;
		// Process completes in PT30S time.
		for (int count = 0; count < 20; count++) {
			Thread.sleep(5000);
			instanceInfo = instanceManagementClient.getInstanceInfo(instances.get(0));
			if (instanceInfo.getStatus().getValue().equals(HumanTaskTestConstants.TERMINATED)) {
				isInstanceTerminated = true;
				log.info("Instance TERMINATED");
				break;
			}
		}
		Assert.assertTrue(isInstanceTerminated,
		                  "Status of instance " + instances.get(0) + " is not equal to TERMINATED");
		log.info("Waiting for Task coordination.");
		// Exit Protocol message takes few seconds.
		Thread.sleep(5000);
		TTaskAbstract taskData = clerk1HumanTaskClientApiClient.loadTask(b4pTask.getId());
		Assert.assertEquals(taskData.getStatus().toString(), HumanTaskTestConstants.EXITED,
		                    "Task is not in EXITED status.");
		instanceManagementClient.deleteAllInstances();
		log.info(testID + " Completed.");
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Task is failed by user.", priority = 4,
			singleThreaded = true)
	public void testHTFault() throws Exception {
		final String testID = getTestNumber();
		log.info(testID + ": Task is failed by user. Process instance should terminate.");
		String soapBody = HumanTaskTestConstants.createClaimApprovalProcessRequest(testID, "Hasitha", "Aravinda", 5000);

		List<String> expectedOutput = Collections.emptyList();
		log.info("Calling Service: " + backEndUrl +
		         HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE);
		requestSender.sendRequest(backEndUrl + HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE,
		                          HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_OPERATION, soapBody, 1, expectedOutput,
		                          false);

		// Wait until task create.
		Thread.sleep(5000);

		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1HumanTaskClientApiClient.simpleQuery(queryInput);
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
		String claimApprovalRequest = (String) clerk1HumanTaskClientApiClient.getInput(b4pTask.getId(), null);
		Assert.assertNotNull(claimApprovalRequest, "The input of the Task:" + b4pTask.getId() + " is null.");
		Assert.assertFalse(!claimApprovalRequest.contains(testID), "Unexpected input found for the Task");

		// Perform Task Operation
		// Claim, Start and Complete.
		log.info("Clerk 1 is performing HumanTask");
		clerk1HumanTaskClientApiClient.start(b4pTask.getId());
		clerk1HumanTaskClientApiClient.fail(b4pTask.getId(), null);

		Thread.sleep(5000);
		List<String> instances =
				instanceManagementClient.listInstances(HumanTaskTestConstants.CLAIM_APPROVAL_NAMESPACE, 1);

		Assert.assertTrue(instances.size() == 1, "Number of process instances not equal to one.");
		log.info("Waiting for Process instance to Complete.");
		InstanceInfoType instanceInfo = null;
		boolean isInstanceTerminated = false;
		// Process completes in PT1M time.
		for (int count = 0; count < 20; count++) {
			Thread.sleep(5000);
			instanceInfo = instanceManagementClient.getInstanceInfo(instances.get(0));
			if (instanceInfo.getStatus().getValue().equals(HumanTaskTestConstants.TERMINATED)) {
				isInstanceTerminated = true;
				log.info("Instance Terminated");
				break;
			}
		}
		Assert.assertTrue(isInstanceTerminated,
		                  "Status of instance " + instances.get(0) + " is not equal to TERMINATED");
		VariableInfoType[] variableInfo = instanceInfo.getRootScope().getVariables().getVariableInfo();
		boolean isTaskOutputPresents = false;
		if (variableInfo != null && variableInfo.length > 0) {
			for (VariableInfoType var : variableInfo) {
				if ("b4pOutput".equals(var.getSelf().getName())) {
					isTaskOutputPresents = true;
					break;
				}
			}
		} else {
			Assert.assertTrue(false, "No Process instance variables found.");
		}
		Assert.assertTrue(isTaskOutputPresents,
		                  "TaskOutput variable not created. Instance terminated before task coordination.");
		instanceManagementClient.deleteAllInstances();
		log.info(testID + " Completed.");
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Task is Skipped by business admin.", priority = 5,
			singleThreaded = true)
	public void testHTSkip() throws Exception {
		final String testID = getTestNumber();
		log.info(testID + ": Task is Skipped by business administrator.  Process instance should terminate.");
		String soapBody = HumanTaskTestConstants.createClaimApprovalProcessRequest(testID, "Hasitha", "Aravinda", 5000);

		List<String> expectedOutput = Collections.emptyList();
		log.info("Calling Service: " + backEndUrl +
		         HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE);
		requestSender.sendRequest(backEndUrl + HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_SERVICE,
		                          HumanTaskTestConstants.CLAIM_APPROVAL_PROCESS_OPERATION, soapBody, 1, expectedOutput,
		                          false);

		// Wait until task create.
		Thread.sleep(5000);

		TSimpleQueryInput queryInput = new TSimpleQueryInput();
		queryInput.setPageNumber(0);
		queryInput.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);

		//Login As Clerk1 user
		TTaskSimpleQueryResultSet taskResults = clerk1HumanTaskClientApiClient.simpleQuery(queryInput);
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
		String claimApprovalRequest = (String) clerk1HumanTaskClientApiClient.getInput(b4pTask.getId(), null);
		Assert.assertNotNull(claimApprovalRequest, "The input of the Task:" + b4pTask.getId() + " is null.");
		Assert.assertFalse(!claimApprovalRequest.contains(testID), "Unexpected input found for the Task");

		// Perform Task Operation
		// Claim, Start and Complete.
		log.info("Manager is performing HumanTask");
		manager1HumanTaskClientApiClient.skip(b4pTask.getId());

		Thread.sleep(5000);
		List<String> instances =
				instanceManagementClient.listInstances(HumanTaskTestConstants.CLAIM_APPROVAL_NAMESPACE, 1);

		Assert.assertTrue(instances.size() == 1, "Number of process instances not equal to one.");
		log.info("Waiting for Process instance to Complete.");
		InstanceInfoType instanceInfo = null;
		boolean isInstanceTerminated = false;
		// Process completes in PT1M time.
		for (int count = 0; count < 20; count++) {
			Thread.sleep(5000);
			instanceInfo = instanceManagementClient.getInstanceInfo(instances.get(0));
			if (instanceInfo.getStatus().getValue().equals(HumanTaskTestConstants.TERMINATED)) {
				isInstanceTerminated = true;
				log.info("Instance Terminated");
				break;
			}
		}
		Assert.assertTrue(isInstanceTerminated,
		                  "Status of instance " + instances.get(0) + " is not equal to TERMINATED");
		VariableInfoType[] variableInfo = instanceInfo.getRootScope().getVariables().getVariableInfo();
		boolean isTaskOutputPresents = false;
		if (variableInfo != null && variableInfo.length > 0) {
			for (VariableInfoType var : variableInfo) {
				if ("b4pOutput".equals(var.getSelf().getName())) {
					isTaskOutputPresents = true;
					break;
				}
			}
		} else {
			Assert.assertTrue(false, "No Process instance variables found.");
		}
		Assert.assertTrue(isTaskOutputPresents,
		                  "TaskOutput variable not created. Instance terminated before task coordination.");
		instanceManagementClient.deleteAllInstances();
		log.info(testID + " Completed.");
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Enable ws-coordination.", priority = 6,
			singleThreaded = true)
	public void enableWSCOOR() throws Exception {
		applyWSCoordinationConfig();
		setupTestClients();
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Test Creation with ws-coordination", priority = 7,
			singleThreaded = true)
	public void testDefaultWSCoor() throws Exception {
		testDefault();
	}

	@Test(groups = { "wso2.bps.task.coordination" },
			description = "Test Process instance terminate with ws-coordination", priority = 8, singleThreaded = true)
	public void testBPELTerminateWSCoor() throws Exception {
		testBPELTerminate();
	}

	@Test(groups = { "wso2.bps.task.coordination" },
			description = "Test Process instance terminate by management API with ws-coordination", priority = 9,
			singleThreaded = true)
	public void testBPELTerminateViaMgtAPIWSCoor() throws Exception {
		testBPELTerminateViaMgtAPI();
	}

	@Test(groups = { "wso2.bps.task.coordination" }, description = "Task is failed by user with ws-coordination",
			priority = 10, singleThreaded = true)
	public void testHTFaultWSCoor() throws Exception {
		testHTFault();
	}

	@Test(groups = { "wso2.bps.task.coordination" },
			description = "Task is Skipped by business admin with ws-coordination", priority = 11,
			singleThreaded = true)
	public void testHTSkipWSCoor() throws Exception {
		testHTSkip();
	}

	@AfterTest(alwaysRun = true, description = "Unload packages after test.")
	public void removeArtifacts() throws PackageManagementException, InterruptedException, RemoteException,
	                                     LogoutAuthenticationExceptionException,
	                                     org.wso2.carbon.humantask.stub.mgt.PackageManagementException {
		bpelPackageManagementClient.undeployBPEL("ClaimsApprovalProcess");
		humanTaskPackageManagementClient.unDeployHumanTask("ClaimsApprovalTask", "ApproveClaim");
		loginLogoutClient.logout();
	}

	private static String getTestNumber() {
		testNumber++;
		return "Test " + testNumber;
	}

}
