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
 * This class will deploy a bpmn package test the functionality of the process instance
 */
public class ProcessInstanceTestCase extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(ProcessInstanceTestCase.class);

    /**
     * The method below tests the functionality of process instance. We deploy a bpmn package and
     * test
     * the requests to process, process instances.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.bps.test.processInstance"}, description = "Process Instance Test",
          priority = 2, singleThreaded = true)
    public void processInstanceTest() throws Exception {
        init();
        ActivitiRestClient tester = new ActivitiRestClient(bpsServer.getInstance().getPorts().
                get("http"), bpsServer.getInstance().getHosts().get("default"));
        //deploying a bpmn package to the server
        String filePath = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                          + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                          + BPMNTestConstants.DIR_BPMN + File.separator + "HelloApprove.bar";
        String fileName = "HelloApprove.bar";
        String[] deploymentResponse = {};
        try {
            deploymentResponse = tester.deployBPMNPackage(filePath, fileName);
            Assert.assertTrue("Deployment was not successful", deploymentResponse[0].contains
                    (BPMNTestConstants.CREATED));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Failed to deploy the bpmn package " + fileName, exception);
            Assert.fail("Failed to deploy the bpmn package " + fileName);
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
            Assert.assertTrue("Search was not success", definitionResponse[0].contains
                    (BPMNTestConstants.OK));
        } catch (IOException | JSONException exception) {
            log.error("Could not find definition id for bpmn package " + fileName, exception);
            Assert.fail("Could not find definition id for bpmn package " + fileName);
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
            Assert.assertTrue("Process instance does not exist",
                              searchResponse.contains(BPMNTestConstants.OK));
        } catch (IOException exception) {
            log.error("Process instance cannot be found", exception);
            Assert.fail("Process instance cannot be found");
        }
        //Suspending the the process instance once the service task is completed. The request will
        //respond with a status of 200 when successful.
        try {
            String[] suspendResponse = tester.suspendProcessInstanceById
                    (processInstanceResponse[1]);
            Assert.assertTrue("Process instance cannot be suspended",
                              suspendResponse[0].contains(BPMNTestConstants.OK));
            Assert.assertTrue("Process instance cannot be suspended",
                              suspendResponse[1].contains("true"));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Process instance cannot be suspended", exception);
            Assert.fail("The Process instance cannot be suspended");
        }
        //Validating if the process instance is in the suspended state, we retrieve the process
        //instance and check the suspended state is true or false.
        try {
            String stateVerfication = tester.getSuspendedStateOfProcessInstanceByID
                    (processInstanceResponse[1]);
            Assert.assertTrue("The process instance is not in suspended state", stateVerfication.
                    contains("true"));
        } catch (IOException | JSONException exception) {
            log.error("The process instance is not in suspended state", exception);
            Assert.fail("The process instance is not in suspended state ");
        }
        //Deleting a Process Instance once the tasks are completed. When the process instance is
        // removed
        //the server responds with a status of 204
        String deleteStatus = "";
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                deleteStatus = tester.deleteProcessInstanceByID(processInstanceResponse[1]);
                if (deleteStatus.contains(BPMNTestConstants.NO_CONTENT)) {
                    break;
                }
            }
            Assert.assertTrue("Process instance cannot be removed",
                              deleteStatus.contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("Process instance cannot be removed", exception);
            Assert.fail("Process instance cannot be removed");
        }
        //validating if the process instance is removed, the request should throw an exception since
        //the process instance is not there
        try {
            tester.validateProcessInstanceById(definitionResponse[1]);
            Assert.fail("Process instance stille exists");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("Process instance was removed successfully", BPMNTestConstants.
                    NOT_AVAILABLE.equals(exception.getMessage()));
            // If the process instance does not exist we should get the exception and testCase
            // should pass.
            // In that case we do not need to log the exception.
        }
        //Undeploying the bpmn package. The request should return response of 204 after removing the
        //bpmn package.
        try {
            String undeployStatus = tester.unDeployBPMNPackage(deploymentResponse[1]);
            Assert.assertTrue("Package cannot be undeployed",
                              undeployStatus.contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("Failed to undeploy bpmn package " + fileName, exception);
            Assert.fail("Failed to remove bpmn package " + fileName);
        }
        //validating if the bpmn package was successfully removed, the request should throw an
        // exception
        //since the bpmn package has been removed.
        try {
            tester.getDeploymentInfoById(deploymentResponse[1]);
            Assert.fail("Package still exists after undeployment");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("Bpmn package " + fileName + " does not exist", BPMNTestConstants.
                    NOT_AVAILABLE.equals(exception.getMessage()));
            // If the unDeployment succeed then we should get the exception with deployment could
            // not found and testCase should pass.
            // In that case we do not need to log the exception.
        }
    }
}

