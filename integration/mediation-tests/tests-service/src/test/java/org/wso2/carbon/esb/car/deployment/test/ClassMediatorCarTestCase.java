/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class ClassMediatorCarTestCase extends ESBIntegrationTest {

    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final int MAX_TIME = 120000;
    private final String car1Name = "MediatorCApp_1.0.0";
    private final String car2Name = "MediatorCApp2_1.0.0";
    private final String car1FileName = car1Name + ".car";
    private final String car2FileName = car2Name + ".car";
    private final String proxyName = "MediatorTestProxy";
    private boolean isCarFile1Uploaded = false;
    private boolean isCarFile2Uploaded = false;

    @BeforeClass(alwaysRun = true, description = "Test Car with Mediator deployment")
    protected void uploadCar1Test() throws Exception {
        super.init();
        carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.
                uploadCarbonAppArtifact(car1FileName,
                                        new DataHandler(new URL("file:" + File.separator + File.separator +
                                                                getESBResourceLocation() +
                                                                File.separator + "car" +
                                                                File.separator +
                                                                car1FileName)));
        isCarFile1Uploaded = true;
        applicationAdminClient =
                new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(car1Name), "Car file deployment failed");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test(groups = {"wso2.esb"}, description = "Test Car with Mediator deployment and invocation")
    public void capp1DeploymentAndServiceInvocation() throws Exception {

        Assert.assertTrue(esbUtils.isProxyDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                                                   proxyName)
                , "Proxy service deployment failed");

        OMElement response = null;
        try {
            response = axis2Client.sendCustomQuoteRequest(
                    getProxyServiceURLHttp(proxyName),
                    null,
                    "WSO2");
        } catch (AxisFault axisFault) {
            throw new Exception("Service Invocation Failed > " + axisFault.getMessage(), axisFault);
        }
        Assert.assertNotNull(response, "Response message null");
        Assert.assertTrue(response.toString().contains("MEDIATOR1"),
                          "MEDIATOR1 element not found in response message");

    }

    @Test(groups = {"wso2.esb"}, description = "Test Car with Mediator un-deployment"
            , dependsOnMethods = {"capp1DeploymentAndServiceInvocation"})
    public void capp1UnDeploymentTest() throws Exception {
        applicationAdminClient.deleteApplication(car1Name);
        isCarFile1Uploaded = false;
        Assert.assertTrue(isCarFileUnDeployed(car1Name), "Car file undeployment failed");
        TimeUnit.SECONDS.sleep(5);
        Assert.assertTrue(esbUtils.isProxyUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                                                     proxyName)
                , "Car1 un-deployment failed");

    }

    @Test(groups = {"wso2.esb"}, description = "Test Re deploy car file"
                , dependsOnMethods = {"capp1UnDeploymentTest"})
    protected void uploadCar2Test() throws Exception {
        super.init();
        carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.
                uploadCarbonAppArtifact(car2FileName,
                                        new DataHandler(new URL("file:" + File.separator + File.separator +
                                                                getESBResourceLocation() +
                                                                File.separator + "car" +
                                                                File.separator +
                                                                car2FileName)));
        isCarFile2Uploaded = true;
        applicationAdminClient =
                new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(car2Name), "Car file deployment failed");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test(groups = {"wso2.esb"}, description = "Test Car with Mediator hot deployment"
                        , dependsOnMethods = {"uploadCar2Test"})
    public void capp2DeploymentAndServiceInvocation() throws Exception {

        Assert.assertTrue(esbUtils.isProxyDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                                                   proxyName)
                , "Proxy service deployment failed");

        OMElement response = null;
        try {
            response = axis2Client.sendCustomQuoteRequest(
                    getProxyServiceURLHttp(proxyName),
                    null,
                    "WSO2");
        } catch (AxisFault axisFault) {
            throw new Exception("Service Invocation Failed > " + axisFault.getMessage(), axisFault);
        }
        Assert.assertNotNull(response, "Response message null");
        Assert.assertTrue(response.toString().contains("MEDIATOR2"),
                          "MEDIATOR2 element not found in response message");

    }

    @Test(groups = {"wso2.esb"}, description = "Test Car with Mediator un-deployment"
            , dependsOnMethods = {"capp2DeploymentAndServiceInvocation"})
    public void capp2UnDeploymentTest() throws Exception {
        applicationAdminClient.deleteApplication(car2Name);
        isCarFile2Uploaded = false;
        Assert.assertTrue(isCarFileUnDeployed(car2Name), "Car file undeployment failed");
        TimeUnit.SECONDS.sleep(5);
        Assert.assertTrue(esbUtils.isProxyUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                                                     proxyName)
                , "Car2 un-deployment failed");

    }

    @AfterClass(alwaysRun = true)
    public void cleanupArtifactsIfExist() throws Exception {
        if (isCarFile1Uploaded) {
            applicationAdminClient.deleteApplication(car1Name);
        }
        if (isCarFile2Uploaded) {
            applicationAdminClient.deleteApplication(car2Name);
        }
        super.cleanup();
    }

    private boolean isCarFileDeployed(String carFileName) throws Exception {

        log.info("waiting " + MAX_TIME + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < MAX_TIME) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileDeployed = true;
                    log.info("car file deployed in " + time + " mills");
                    return isCarFileDeployed;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
        return isCarFileDeployed;
    }

    private boolean isCarFileUnDeployed(String carFileName) throws Exception {

        log.info("waiting " + MAX_TIME + " millis for car undeployment " + carFileName);
        boolean isCarFileUnDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < MAX_TIME) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (!ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileUnDeployed = true;
                    log.info("car file deployed in " + time + " mills");
                    return isCarFileUnDeployed;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
            } else {
                isCarFileUnDeployed = true;
                log.info("car file deployed in " + time + " mills");
                return isCarFileUnDeployed;
            }
        }
        return isCarFileUnDeployed;
    }

}
