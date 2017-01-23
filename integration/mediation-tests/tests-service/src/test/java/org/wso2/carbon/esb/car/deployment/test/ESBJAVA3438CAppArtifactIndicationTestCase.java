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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;
import org.wso2.esb.integration.common.clients.service.mgt.ServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * This class can be used to upload .car application to the server and verify whether that proxy service artifact got
 * deployed through CApp
 */
public class ESBJAVA3438CAppArtifactIndicationTestCase extends ESBIntegrationTest {
    private ServiceAdminClient serviceAdminClient;

    String service = "sampleCustomProxy";
    String carFileName = "esb-artifacts-car_1.0.0.car";

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init();
        CarbonAppUploaderClient carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        carbonAppUploaderClient.uploadCarbonAppArtifact("esb-artifacts-car_1.0.0.car"
                , new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                                          + File.separator + "car" + File.separator + "esb-artifacts-car_1.0.0.car")));
        serviceAdminClient =
                new ServiceAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        TimeUnit.SECONDS.sleep(5);
        log.info(carFileName + " uploaded successfully");
    }


    @Test(groups = "wso2.esb", enabled = true, description = "Test whether proxy service get deployed through capp")
    public void testProxyServiceIsCApp() throws Exception {
        Thread.sleep(6000);
        Assert.assertTrue(
                esbUtils.isProxyDeployed(context.getContextUrls().getBackEndUrl(), sessionCookie, "sampleCustomProxy")
                , "transform Proxy service deployment failed");
        TimeUnit.SECONDS.sleep(5);
        ServiceMetaData serviceMetaData = serviceAdminClient.getServicesData(service);
        /*log.info("Is " + service + " a CApp artifact? " + serviceMetaData.isCAppArtifactSpecified
                ());
        Assert.assertNotNull(serviceMetaData.isCAppArtifactSpecified(), service + " is not a " +
                "CApp artifact");*/
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
