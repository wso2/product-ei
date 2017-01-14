/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.car.deployment.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * TestCase for CAR file re-deployment
 */
public class CarbonApplicationReDeploymentTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.esb"}, description = "test proxy service re-deployment from car file")
    public void carReDeploymentTest() throws Exception {
        String proxyName = "samplePassThroughProxy";
        for (int i = 0; i < 5; i++) {
            log.info("######################  Iteration : " + (i + 1));
            deployCar();

            //we are re-deploying a car which is already available, so it takes some time to un-deploy
            // its artifacts correctly; hence the sleep time of 30 seconds
            TimeUnit.SECONDS.sleep(30);

            //test proxy service deployment from car file
            isProxyDeployed(proxyName);

            //test proxy service invocation
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName), null,
                                                                         "CARDeployment");
            Assert.assertTrue(response.toString().contains("CARDeployment"), "Symbol not found on the response message");
        }
    }

    private void deployCar() throws Exception {
        uploadCapp("sample-passthrough-proxy-car_1.0.0.car"
                , new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                                          + File.separator + "car" + File.separator +
                                          "sample-passthrough-proxy-car_1.0.0.car")));
    }

    @AfterClass(alwaysRun = true)
    public void deleteCarFileAndArtifactUnDeploymentTest() throws Exception {
        TimeUnit.SECONDS.sleep(5);
        super.cleanup();
    }
}