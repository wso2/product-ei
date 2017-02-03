/*
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNDeployment;

/**
 * This test case deals with BPMN task creation .
 */
public class BPMNTaskCreationTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BPMNTaskCreationTestCase.class);
    private WorkflowServiceClient workflowServiceClient;
    private int deploymentCount;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();  //init master class
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        BPMNDeployment[] bpmnDeployments = workflowServiceClient.getDeployments();
        if (bpmnDeployments != null) {
            deploymentCount = workflowServiceClient.getDeployments().length;
        }
        initialize();
    }

    protected void initialize() throws Exception {
        log.info("Initializing BPMN task creation Test...");
        deployArtifact();
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "HelloApprove", deploymentCount);
    }

    public void deployArtifact() throws Exception {
        uploadBPMNForTest("HelloApprove");
    }

    @Test(groups = {"wso2.bps.task.create"}, description = "Hello Approve test case", priority = 1, singleThreaded =
            true)
    public void createTask() {
        String processId;
        try {
            processId = workflowServiceClient.getProcesses()[workflowServiceClient.getProcesses().length - 1]
                    .getProcessId();
            workflowServiceClient.startProcess(processId);
            log.info("BPMN Process:" + processId + " started ");
            log.info("BPMN Process has:" + workflowServiceClient.getInstanceCount() + " instances ");
            Assert.assertTrue("Create task is successful", workflowServiceClient.getInstanceCount() > 0);
        } catch (Exception ex) {
            String errMsg = "Failed to create bpmn task in HelloApprove process ";
            log.error(errMsg, ex);
            Assert.fail(errMsg);
        }
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts() throws Exception {

        workflowServiceClient.undeploy("HelloApprove");
        loginLogoutClient.logout();
    }
}
