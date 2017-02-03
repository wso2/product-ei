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
 * This class tests the deployment and undeployment of bpmn package.
 */
public class ValidBPMNPackageDeploymentTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(ValidBPMNPackageDeploymentTestCase.class);

    /**
     * In the following method we deploy and undeploy a bpmn package
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.bps.test.deploy"}, description = "Deploy/UnDeploy Package Test",
          priority = 1, singleThreaded = true)
    public void deployUnDeployBPMNPackage() throws Exception {
        init();
        ActivitiRestClient tester = new ActivitiRestClient(bpsServer.getInstance().getPorts().
                get(BPMNTestConstants.HTTP), bpsServer.getInstance().getHosts().get
                (BPMNTestConstants.DEFAULT));
        String filePath = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                          + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                          + BPMNTestConstants.DIR_BPMN + File.separator + "HelloApprove.bar";
        String fileName = "HelloApprove.bar";
        String[] deploymentResponse = {};
        String[] deploymentCheckResponse = {};
        String deploymentStatus = "";
        // Deploying the bpmn artifact, which returns an response status 201 if deployment
        // is successful
        try {
            deploymentResponse = tester.deployBPMNPackage(filePath, fileName);
            Assert.assertTrue("Deployment was not successful", deploymentResponse[0].contains
                    (BPMNTestConstants.CREATED));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Failed to Deploy bpmn package " + fileName, exception);
            Assert.fail("Failed to Deploy bpmn package " + fileName);
        }
        //validating if the deployed bpmn artifact is present, checking if the deployment id
        // is present in the list of deployments.
        try {
            deploymentCheckResponse = tester.getDeploymentInfoById(deploymentResponse[1]);
            Assert.assertTrue("Deployment does not exist", deploymentCheckResponse[2].contains
                    (fileName));
        } catch (RestClientException | IOException | JSONException exception) {
            log.error("Deployed bpmn package " + fileName + " not present", exception);
            Assert.fail("Deployed bpmn package " + fileName + " not present");
        }
        //undeploying the bpmn package. when the package is undeployed the server
        // responds with a 204 status.
        try {
            deploymentStatus = tester.unDeployBPMNPackage(deploymentResponse[1]);
            Assert.assertTrue("Package cannot be undeployed", deploymentStatus.contains
                    (BPMNTestConstants.NO_CONTENT));
        } catch (IOException exception) {
            log.error("BPMN Package cannot be undeployed " + fileName, exception);
            Assert.fail("BPMN Package cannot be undeployed " + fileName);
        }
        //validating if the bpmn package has been removed from the server.
        // We validate by checking if the deployment id is present,
        // if id is not present an exception
        // is thrown.
        try {
            tester.getDeploymentInfoById(deploymentResponse[1]);
            Assert.fail("Package Still exists after undeployment");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("Bpmn package " + fileName + "does not exist", BPMNTestConstants.
                    NOT_AVAILABLE.equals(exception.getMessage()));

        }
    }
}
