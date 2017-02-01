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
 * This class will test how gracefully the server will handle invalid bpmn package.
 */
public class InvalidBPMNPackageDeploymentTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(InvalidBPMNPackageDeploymentTestCase
                                                             .class);

    /**
     * The BPMN package being used to test is Invalid, it is used to test how gracefully
     * the system handles the invalid bpmn package,
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.bps.test.deploy.invalidPackage"}, description = "Deploy/UnDeploy " +
                                                                          "Invalid Package Test",
          priority = 1, singleThreaded = true)
    public void deployUnDeployInvalidBPMNPackage() throws Exception {
        init();
        ActivitiRestClient tester = new ActivitiRestClient(bpsServer.getInstance().getPorts().get
                (BPMNTestConstants.HTTP), bpsServer.getInstance().getHosts().get
                (BPMNTestConstants.DEFAULT));
        String filePath = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                          + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                          + BPMNTestConstants.DIR_BPMN + File.separator + "InvalidHelloApprove.bar";
        String fileName = "InvalidHelloApprove.bar";

        //trying to deploy an invalid bpmn package will cause the server to throw an "error parsing
        //XML" we are using the this message to validate.
        try {
            String[] deploymentResponse;
            deploymentResponse = tester.deployBPMNPackage(filePath, fileName);
            Assert.fail("Invalid package was deployed.");
        } catch (RestClientException | IOException | JSONException exception) {
            Assert.assertTrue("Could not upload the invalid bpmn package",
                              "Error parsing XML".equals(exception.getMessage()));
        }
    }
}
