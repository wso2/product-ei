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

package org.wso2.ei.businessprocess.integration.tests.bpmn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNDeployment;

/**
 * Testing multi-tenancy support in BPMN
 */
public class BPMNMultiTenancyTestCase extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPMNTaskCreationTestCase.class);

    private WorkflowServiceClient workflowServiceClient;
    private int deploymentCount;
    boolean accessedSameArtifactInstance = false;
    String packageName = "VacationRequest";
    String domainKey1 = "wso2.com";
    String userKey1 = "user1";
    String domainKey2 = "abc.com";
    String userKey2 = "user2";

    /**
     * Initialise for tenant wso2.com
     * @param:domainKey1
     * @param:userKey1
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void createTenant() throws Exception {
        // initialize for tenant wso2.com
        initialize(domainKey1, userKey1);
    }

    /**
     * Initialize for each tenant
     *
     * @param domainKey
     * @param userKey
     * @throws Exception
     */
    public void initialize(String domainKey, String userKey) throws Exception {
        init(domainKey, userKey);
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        BPMNDeployment[] bpmnDeployments = workflowServiceClient.getDeployments();
        if (bpmnDeployments != null) {
            deploymentCount = workflowServiceClient.getDeployments().length;
        }
    }

    /**
     * Tenant abc.com trying to access BPMN artifact deployed by wso2.com
     * 1.Log in as tenant wso2.com
     * 2.Deploy BPMN artifacts from tenant wso2.com
     * 3.Log in as tenant abc.com
     * 4.Search for deployed instance in tenant abc.com
     * 5.Check for same processId as deployed artifact.
     * @throws Exception
     */
    @Test(groups = {"wso2.bps.task.BPMNMultiTenancy"}, description = "Confirm BPMN Multi tenancy support test case",
            priority = 1, singleThreaded = true)
    public void confirmMultiTenancyForBPMNArtifact() throws Exception {

        // log in as tenant wso2.com
        String session = loginLogoutClient.login();
        //deploy BPMN artifact from tenant wso2.com
        deployArtifact();
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, packageName, deploymentCount);
        String processId = workflowServiceClient.getProcesses()
                [workflowServiceClient.getProcesses().length - 1].getProcessId();
        log.info("BPMN Process:" + processId + " accessed by tenant t1 " + session);
        loginLogoutClient.logout();

        initialize(domainKey2, userKey2);
        //login as tenant abc.com
        loginLogoutClient.login();
        int deployedInstance = workflowServiceClient.getInstanceCount();
        if (deployedInstance == 0) {
            log.info("No processes available for tenant:" + domainKey2);
        } else {
            //if deployment instances exist for abc.com
            if (deploymentCount != 0) {

                String processId2 = workflowServiceClient.getProcesses()
                        [workflowServiceClient.getProcesses().length - 1].getProcessId();
                //if it is the same processId as of VacationRequest
                if (processId2.equals(processId)) {
                    accessedSameArtifactInstance = true;
                }
            }
        }
        loginLogoutClient.logout();
        Assert.assertEquals(accessedSameArtifactInstance, false, "Multi-tenancy implementation failed." +
                "Artifact deployed by tenant" + domainKey1 + "can be accessed by tenant" + domainKey2);
    }

    public void deployArtifact() throws Exception {
        uploadBPMNForTest(packageName);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifact() throws Exception {
        initialize(domainKey1, userKey1);
        workflowServiceClient.undeploy(packageName);
        log.info("Successfully undeployed: " + packageName);

    }
}
