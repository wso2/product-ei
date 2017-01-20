/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.car.deployment.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ESBJAVA4116CAppArtifactDeploymentFailureDueToNameTestCase extends ESBIntegrationTest {

    String carFileName = "car-deployment-test.car";

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init();
        uploadCapp(carFileName
                , new DataHandler(new URL("file:" + File.separator + File.separator +
                getESBResourceLocation() + File.separator + "car" +
                File.separator + carFileName)));
        TimeUnit.SECONDS.sleep(5);
        log.info(carFileName + " uploaded successfully");
    }

    @Test(groups = "wso2.esb", enabled = true, description = "Test whether capp deployment fails " +
            "if sequence artifact name consist of *main* as a substring")
    public void testSequenceDeployed() throws Exception {
        Thread.sleep(6000);
        org.testng.Assert.assertTrue(
                esbUtils.isSequenceDeployed(contextUrls.getBackEndUrl(), getSessionCookie(),
                        "MySequenceDomain")
                , "ERROR - CappAxis2Deployer Error while deploying carbon application");
    }


    @AfterTest(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }
}
