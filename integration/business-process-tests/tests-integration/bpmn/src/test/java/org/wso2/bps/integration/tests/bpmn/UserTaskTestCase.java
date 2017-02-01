/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.bps.integration.tests.bpmn;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpmn.ActivitiRestClient;
import org.wso2.bps.integration.common.clients.bpmn.RestClientException;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.File;
import java.io.IOException;

/**
 * This class tests the user task functionality
 */
public class UserTaskTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(UserTaskTestCase.class);

    @Test(groups = {"wso2.bps.test.usertasks"}, description = "User Task Test", priority = 1,
          singleThreaded = true)
    public void userTaskTest() throws Exception {
        init();
        ActivitiRestClient tester = new ActivitiRestClient(bpsServer.getInstance().getPorts().
                get(BPMNTestConstants.HTTP), bpsServer.getInstance().getHosts().get
                (BPMNTestConstants.DEFAULT));
        //deploying Package
        String filePath = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                          + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                          + BPMNTestConstants.DIR_BPMN + File.separator + "AssigneeIsEmpty.bar";

        String fileName = "AssigneeIsEmpty.bar";
        String[] deploymentResponse = {};
        try {
            deploymentResponse = tester.deployBPMNPackage(filePath, fileName);
            Assert.assertTrue("Deployment not successful", deploymentResponse[0].contains
                    (BPMNTestConstants
                             .CREATED));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Failed to deploy bpmn package" + fileName, exception);
            Assert.fail("Failed to deploy bpmn package " + fileName);
        }
        //verifying if the deployed bpmn package is present in the deployments list by validating if
        // the deployment name is present in the deployment list.
        try {
            String[] deploymentCheckResponse = tester.getDeploymentInfoById(deploymentResponse[1]);
            Assert.assertTrue("Deployment is not present", deploymentCheckResponse[2].contains
                    (fileName));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Deployed bpmn package " + fileName + " was not found ", exception);
            Assert.fail("Deployed bpmn package " + fileName + " was not found ");
        }
        //Acquiring Process Definition ID to start Process Instance
        String[] definitionResponse = new String[0];
        try {
            definitionResponse = tester.findProcessDefinitionInfoById(deploymentResponse[1]);
            Assert.assertTrue("Search was not successful", definitionResponse[0].contains
                    (BPMNTestConstants.OK));
        } catch (IOException | JSONException exception) {
            log.error("Could not find definition id for bpmn package " + fileName, exception);
            Assert.fail("Could not find definition if for bpmn package" + fileName);
        }
        //Starting Process Instance, we used the definition ID to start the process instance,
        //when the process instance is started successfully the server responds with a status of
        // 201.
        String[] processInstanceResponse = new String[0];
        try {
            processInstanceResponse = tester.startProcessInstanceByDefintionID
                    (definitionResponse[1]);
            Assert.assertTrue("Process instance cannot be started", processInstanceResponse[0].
                    contains(BPMNTestConstants.CREATED));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Process instance failed to start ", exception);
            Assert.fail("Process instance failed to start ");
        }
        //verifying if the process instance is present in the process instance list by validating if
        // the process instance is present in the process instance list.If,
        // present the query request
        //responds with a http status 200
        try {
            String searchResponse = tester.searchProcessInstanceByDefintionID
                    (definitionResponse[1]);
            Assert.assertTrue("Process instance not present",
                              searchResponse.contains(BPMNTestConstants.OK));
        } catch (IOException exception) {
            log.error("Process instance cannot be found", exception);
            Assert.fail("Process instance cannot be found");
        }


        //Acquiring TaskID to perform Task Related Tests
        String[] taskResponse = new String[0];
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                taskResponse = tester.findTaskIdByProcessInstanceID(processInstanceResponse[1]);
                if (taskResponse != null) {
                    break;
                }
            }
            Assert.assertTrue("Task ID cannot be acquired", taskResponse[0].contains
                    (BPMNTestConstants.OK));
        } catch (IOException | JSONException exception) {
            log.error("Could not identify the task ID", exception);
            Assert.fail("Could not identify the task ID");
        }
        //The task is being claimed by a user, which will return a status of 204 when success.
        try {
            String claimResponse = tester.claimTaskByTaskId(taskResponse[1]);
            Assert.assertTrue("User has cannot claim task", claimResponse.
                    contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("The Task was not claimable", exception);
            Assert.fail("The Task was not claimable");
        }
        //validating if the task has been claimed successfully.
        String currentAssignee = null;
        try {
            currentAssignee = tester.getAssigneeByTaskId(taskResponse[1]);
            Assert.assertTrue("User has cannot be assigned", currentAssignee.
                    contains(BPMNTestConstants.USER_CLAIM));
        } catch (IOException | JSONException exception) {
            log.error("The task not assigned to user", exception);
            Assert.fail("The task not assigned to user");
        }
        //Delegating a User Task, successful delegation with return a 204 once the task has been
        // delegated
        try {
            String delegateStatus = tester.delegateTaskByTaskId(taskResponse[1]);
            Assert.assertTrue("Task has not been delegated", delegateStatus.
                    contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("Failed to Delegate Task", exception);
            Assert.fail("Failed to Delegate Task");
        }
        //Validating the if the  assignee name matches to the delegated user.
        try {
            currentAssignee = tester.getAssigneeByTaskId(taskResponse[1]);
            Assert.assertTrue("Delegated name does not match assignee", currentAssignee.
                    equals(BPMNTestConstants.USER_DELEGATE));
        } catch (IOException | JSONException exception) {
            log.error("Delegated user does not match assignee", exception);
            Assert.fail("Delegated user does not match assignee");
        }
        //Adding a comment on a user task, the server should respond with a status of 201 when the
        //comment is created.
        String[] commentResponse = new String[0];
        try {
            commentResponse = tester.addNewCommentOnTaskByTaskId(taskResponse[1], BPMNTestConstants.
                    COMMENT_MESSAGE);
            Assert.assertTrue("Comment can not be added", commentResponse[0].contains
                    (BPMNTestConstants.
                             CREATED));
            Assert.assertTrue("Comment is not visible", commentResponse[1].contains
                    (BPMNTestConstants.
                             COMMENT_MESSAGE));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Comment was not added", exception);
            Assert.fail("Comment was not added");
        }
        //validating if the comment is present in the task.
        try {
            String validateComment = tester.getCommentByTaskIdAndCommentId(taskResponse[1],
                                                                           commentResponse[2]);
            Assert.assertTrue("comment does not exist", validateComment.contains(BPMNTestConstants.
                                                                                         COMMENT_MESSAGE));
        } catch (IOException | JSONException exception) {
            log.error("Comment does not exist", exception);
            Assert.fail("Comment does not exist");
        }
        //Setting the state of the user task to resolved. Once the user task has been completed.
        try {
            String status = tester.resolveTaskByTaskId(taskResponse[1]);
            String stateValue = tester.getDelegationsStateByTaskId(taskResponse[1]);
            Assert.assertTrue("Failed to set task to resolved state",
                              stateValue.equals("resolved"));
        } catch (IOException | JSONException exception) {
            log.error("Failed to set task state to resolved", exception);
            Assert.fail("Failed to set task state to resolved");
        }
        //Deleting a Process Instance once the tasks are completed. When the process instance is
        // removed
        //the server responds with a status of 204
        try {
            String deleteStatus = tester.deleteProcessInstanceByID(processInstanceResponse[1]);
            Assert.assertTrue("Process instance cannot be removed",
                              deleteStatus.contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("Process instance cannot be removed", exception);
            Assert.fail("Process instance cannot be removed");
        }
        //Validating if the process instance is in the suspended state, we retrieve the process
        //instance and check the suspended state is true or false.
        try {
            tester.validateProcessInstanceById(definitionResponse[1]);
            Assert.fail("Process Instance still exists");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("Process instance was removed successfully", BPMNTestConstants.
                    NOT_AVAILABLE.equals(exception.getMessage()));

        }
        //Undeploying the bpmn package. The request should return response of 204 after removing the
        //bpmn package.
        try {
            String undeployStatus = tester.unDeployBPMNPackage(deploymentResponse[1]);
            Assert.assertTrue("Package was not undeployed",
                              undeployStatus.contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("Failed to remove BPMN Package " + fileName, exception);
            Assert.fail("Failed to remove BPMN Package " + fileName);
        }
        //validating if the bpmn package was successfully removed, the request should throw an
        // exception
        //since the bpmn package has been removed.
        try {
            tester.getDeploymentInfoById(deploymentResponse[1]);
            Assert.fail("Bpmn package still exists After undeployment");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("BPMN Package " + fileName + " Does Not Exist", BPMNTestConstants.
                    NOT_AVAILABLE.equals(exception.getMessage()));

        }
    }
}
