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

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.ActivitiRestClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.RestClientException;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.File;
import java.io.IOException;

/**
 * This class will deploy a bpmn package which contains a java service task and test the
 * functionality of the java service task
 */
public class JavaServiceTaskDeploymentUndeploymentTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog
            (JavaServiceTaskDeploymentUndeploymentTestCase.class);


    //Setting up the enviroment to place the java class files in the respective folder so the server
    //can pick it up when executing the service task
    @BeforeClass
    public void envSetup() throws Exception {
        init();
        final String artifactLocation = FrameworkPathUtil.getSystemResourceLocation() + File
                .separator
                                        + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                                        + BPMNTestConstants.DIR_BPMN + File.separator
                                        + "testArtifactid-1.0.jar";

        ServerConfigurationManager Loader = new ServerConfigurationManager(bpsServer);
        File javaArtifact = new File(artifactLocation);
        Loader.copyToComponentLib(javaArtifact);
        Loader.restartForcefully();

        //reinitialising, as session cookies and other configuration which expired during restart is
        // needs to be reset
        init();
    }

    /**
     * This method contains a flow of deploying a bpmn package, creating a process instance,
     * testing the java service task and undeploying the bpmn package
     *
     * @throws RestClientException | IOException | JSONException
     */
    @Test(groups = {"wso2.bps.test.deploy.JavaServiceTask"}, description = "Deploy/UnDeploy " +
                                                                           "Package Test",
          priority = 1, singleThreaded = true)
    public void deployUnDeployJavaServiceTaskBPMNPackage() throws Exception {
        init();
        ActivitiRestClient tester = new ActivitiRestClient(bpsServer.getInstance().getPorts().get
                (BPMNTestConstants.HTTP)
                , bpsServer.getInstance().getHosts().get(BPMNTestConstants.DEFAULT));

        String filePath = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                          + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                          + BPMNTestConstants.DIR_BPMN + File.separator + "sampleJavaServiceTask" +
                          ".bar";

        String fileName = "sampleJavaServiceTask.bar";
        String[] deploymentResponse = {};
        String[] definitionResponse = {};

        //deploying the bpmn package
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
            Assert.assertTrue("Deployment doesn not exist", deploymentCheckResponse[2].contains
                    (fileName));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Deployed BPMN Package " + fileName + " was not found ", exception);
            Assert.fail("Deployed BPMN Package " + fileName + " was not found ");
        }
        //Searching for the definition ID to create a process instance
        try {
            definitionResponse = tester.findProcessDefinitionInfoById(deploymentResponse[1]);
            Assert.assertTrue("Search Success", definitionResponse[0].contains("200"));
        } catch (IOException | JSONException exception) {
            log.error("Could not find definition id for the bpmn package " + fileName, exception);
            Assert.fail("Could not find definition id for the bpmn package " + fileName);
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
        /*In order to test if the java service task is executed correctly we defined a bool to false
        *and set it to true when the service task is executed and return it to the instance, here
        *we are retrieving the variable from the instance to validate if the service task was
        * executed.
        */
        try {
            String[] invocationResponse = tester.getValueOfVariableOfProcessInstanceById
                    (processInstanceResponse[1], "executionState");
            Assert.assertTrue("Variable is not present", invocationResponse[0].contains
                    (BPMNTestConstants.OK));
            Assert.assertTrue("Variable is not present", invocationResponse[1].contains
                    ("executionState"));
            Assert.assertTrue("Variable is not true, java service task has not been executed",
                              invocationResponse[2].contains("true"));
        } catch (IOException | JSONException exception) {
            log.error("Execution variable response cannot be found ", exception);
            Assert.fail("The execution variable response cannot be found ");
        }
        //Suspending the the process instance once the service task is completed. The request will
        //respond with a status of 200 when successful.
        try {
            String[] suspendResponse = tester.suspendProcessInstanceById
                    (processInstanceResponse[1]);
            Assert.assertTrue("Process instance has not been suspended",
                              suspendResponse[0].contains(BPMNTestConstants.OK));
            Assert.assertTrue("Process instance has not been suspended",
                              suspendResponse[1].contains("true"));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Process instance cannot be suspended", exception);
            Assert.fail("The process instance cannot be suspended");
        }
        //Validating if the process instance is in the suspended state, we retrieve the process
        //instance and check the suspended state is true or false.
        try {
            String stateVerfication = tester.getSuspendedStateOfProcessInstanceByID
                    (processInstanceResponse[1]);
            Assert.assertTrue("The process instance is not in suspended state",
                              stateVerfication.contains("true"));
        } catch (IOException | JSONException exception) {
            log.error("The process instance is not in suspended state ", exception);
            Assert.fail("The process instance is not in suspended state ");
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
        //validating if the process instance is removed, the request should throw an exception since
        //the process instance is not there
        try {
            tester.validateProcessInstanceById(definitionResponse[1]);
            Assert.fail("Process instance still exists");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("Process instance was removed successfully",
                              BPMNTestConstants.NOT_AVAILABLE.
                                      equals(exception.getMessage()));
        }
        //Undeploying the bpmn package. The request should return response of 204 after removing the
        //bpmn package.
        try {
            String undeployStatus = tester.unDeployBPMNPackage(deploymentResponse[1]);
            Assert.assertTrue("Package cannot be undeployed",
                              undeployStatus.contains(BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("Failed to remove bpmn package " + fileName, exception);
            Assert.fail("Failed to remove bpnm package " + fileName);
        }
        //validating if the bpmn package was successfully removed, the request should throw an
        // exception
        //since the bpmn package has been removed.
        try {
            tester.getDeploymentInfoById(deploymentResponse[1]);
            Assert.fail("Package still exists After undeployment");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("BPMN package " + fileName + " Does Not Exist", BPMNTestConstants.
                    NOT_AVAILABLE.equals(exception.getMessage()));
        }
    }
}