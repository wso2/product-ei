/*
 * Copyright (c)  2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.testng.Assert;

import java.io.File;
import java.net.URL;
import java.util.Calendar;

import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;

import javax.activation.DataHandler;

/**
 * Deploying a cAPP file which includes bpmn artifacts
 */
public class BPMNCappDeploymentTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BPMNCappDeploymentTestCase.class);
    private final int MAX_TIME = 120000;
    private String carFileName = "TestPro_1.0.0";
    private boolean bpmnProcessExists = false;
    private boolean isCarFileUploaded = false;
    private ApplicationAdminClient applicationAdminClient;
    private WorkflowServiceClient workflowServiceClient;

    /**
     * Deploy the uploaded C-App file
     */

    @BeforeClass(alwaysRun = true)
    public void deployTask() throws Exception {
        super.init();
        CarbonAppUploaderClient carbonAppUploaderClient =
                new CarbonAppUploaderClient(backEndUrl, sessionCookie);

        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName + BPMNTestConstants.CAR_EXTENSION
                , new DataHandler(new URL("file:" + File.separator + File.separator +
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts"
                + File.separator + "bpmn" + File.separator + carFileName + BPMNTestConstants.CAR_EXTENSION)));
        isCarFileUploaded = true;
        applicationAdminClient = new ApplicationAdminClient(backEndUrl, sessionCookie);
        boolean result = isCarFileDeployed(carFileName);
        Assert.assertTrue(result, "Car file deployment failed");
    }

    /**
     * Start BPMN process of deployed C-APP
     *
     * @paramprocessId - BPMN artifact instance of the deployed C-APP
     */

    @Test(groups = {"wso2.bps.task.BPMNArtifacts"},
            description = "Confirm BPMN artifact deployment of uploaded cApp  test case", priority = 1,
            singleThreaded = true)
    public void confirmBPMNArtifactDeployment() throws Exception {
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        String processId = workflowServiceClient.getProcesses()
                [workflowServiceClient.getProcesses().length - 1].getProcessId();

        if (processId != null) {
            bpmnProcessExists = true;
            workflowServiceClient.startProcess(processId);
            log.info("BPMN Process:" + processId + " started ");
        }
        Assert.assertTrue(bpmnProcessExists, "Unable to find bpmn processes in deployed car file.");
    }

    /**
     * Check if Car file is deployed
     */
    private boolean isCarFileDeployed(String carFileName) throws Exception {

        log.info("waiting " + MAX_TIME + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;

        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < MAX_TIME) {
            String[] applicationList = applicationAdminClient.listAllApplications();

            if (applicationList != null) {
                for (int i = 0; i < applicationList.length; i++) {
                    if (applicationList[i].equals(carFileName)) {
                        isCarFileDeployed = true;
                        log.info("car file deployed in " + time + " mills");
                        return isCarFileDeployed;
                    }
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        return isCarFileDeployed;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupArtifactsIfExist() throws Exception {
        if (isCarFileUploaded) {
            applicationAdminClient.deleteApplication(carFileName);
            log.info("Successfully undeployed " + carFileName);
        }
    }
}
