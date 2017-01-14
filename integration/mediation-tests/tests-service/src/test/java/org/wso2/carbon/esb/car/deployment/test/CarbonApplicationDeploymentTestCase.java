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

public class CarbonApplicationDeploymentTestCase extends ESBIntegrationTest {
    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final int MAX_TIME = 120000;
    private final String carFileName = "esb-artifacts-car_1.0.0";
    private boolean isCarFileUploaded = false;

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init();
        carbonAppUploaderClient = new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.uploadCarbonAppArtifact("esb-artifacts-car_1.0.0.car"
                , new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                                          + File.separator + "car" + File.separator + "esb-artifacts-car_1.0.0.car")));
        isCarFileUploaded = true;
        applicationAdminClient = new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(carFileName), "Car file deployment failed");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test(groups = {"wso2.esb"}, description = "test endpoint deployment from car file")
    public void endpointDeploymentTest() throws Exception {
        Assert.assertTrue(esbUtils.isEndpointDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "addressEndpoint")
                , "AddressEndpoint Endpoint deployment failed");
        Assert.assertTrue(esbUtils.isEndpointDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "loadBalanceEndpoint")
                , "LoadBalanceEndpoint Endpoint deployment failed");
        Assert.assertTrue(esbUtils.isEndpointDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "wsdlEndpoint")
                , "WSDLEndpoint Endpoint deployment failed");
        Assert.assertTrue(esbUtils.isEndpointDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "failOverEndpoint")
                , "FailOverEndpoint Endpoint deployment failed");
        Assert.assertTrue(esbUtils.isEndpointDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "defaultEndpoint")
                , "DefaultEndpoint Endpoint deployment failed");
    }

    @Test(groups = {"wso2.esb"}, description = "test sequence deployment from car file")
    public void sequenceDeploymentTest() throws Exception {
        Assert.assertTrue(esbUtils.isSequenceDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleSequence")
                , "sampleSequence deployment failed");
        Assert.assertTrue(esbUtils.isSequenceDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleFaultSequence")
                , "sampleFaultSequence deployment failed");
        Assert.assertTrue(esbUtils.isSequenceDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleSequenceWithErrorSequence")
                , "sampleSequenceWithErrorSequence deployment failed");

    }

    @Test(groups = {"wso2.esb"}, description = "test API deployment from car file")
    public void apiDeploymentTest() throws Exception {
        Assert.assertTrue(esbUtils.isApiDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "SampleAPI")
                , "SampleAPI deployment failed");

    }

    @Test(groups = {"wso2.esb"}, description = "test LocalEntry deployment from car file")
    public void localEntryDeploymentTest() throws Exception {
        Assert.assertTrue(esbUtils.isLocalEntryDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleInLineXMLLocalentry")
                , "InLine XML Local entry deployment failed");
        Assert.assertTrue(esbUtils.isLocalEntryDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleURLLocalEntry")
                , "URL Local Entry deployment failed");
        Assert.assertTrue(esbUtils.isLocalEntryDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleInLineTextLocalEntry")
                , "InLine text Local Entry deployment failed");

    }

    @Test(groups = {"wso2.esb"}, description = "test proxy service deployment from car file")
    public void proxyServiceDeploymentTest() throws Exception {
        Assert.assertTrue(esbUtils.isProxyDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "samplePassThroughProxy")
                , "Pass Through Proxy service deployment failed");
        Assert.assertTrue(esbUtils.isProxyDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "transformProxySample")
                , "transform Proxy service deployment failed");
        Assert.assertTrue(esbUtils.isProxyDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleCustomProxy")
                , "Custom Proxy service deployment failed");

    }

    @Test(groups = {"wso2.esb"}, description = "test proxy service invocation"
            , dependsOnMethods = {"proxyServiceDeploymentTest", "sequenceDeploymentTest", "endpointDeploymentTest", "localEntryDeploymentTest", "apiDeploymentTest"})
    public void invokeProxyService() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("sampleCustomProxy"), null, "CARDeployment");
        Assert.assertTrue(response.toString().contains("CARDeployment"), "Symbol not found on the response message");
    }


    @Test(groups = {"wso2.esb"}, description = "test proxy service invocation"
            , dependsOnMethods = {"invokeProxyService"})
    public void deleteCarFileAndArtifactUnDeploymentTest() throws Exception {
        applicationAdminClient.deleteApplication(carFileName);
        isCarFileUploaded = false;
        Assert.assertTrue(isCarFileUnDeployed(carFileName));

        TimeUnit.SECONDS.sleep(5);
        //verify whether artifacts are undeployed successfully
        verifyUndeployment();

    }

    @AfterClass(alwaysRun = true)
    public void cleanupArtifactsIfExist() throws Exception {
        if (isCarFileUploaded) {
            applicationAdminClient.deleteApplication(carFileName);
            verifyUndeployment();
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

    private void verifyUndeployment() throws Exception {
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "addressEndpoint")
                , "AddressEndpoint Endpoint Undeployment failed");
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "loadBalanceEndpoint")
                , "LoadBalanceEndpoint Endpoint Undeployment failed");
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "wsdlEndpoint")
                , "WSDLEndpoint Endpoint Undeployment failed");
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "failOverEndpoint")
                , "FailOverEndpoint Endpoint Undeployment failed");
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "defaultEndpoint")
                , "DefaultEndpoint Endpoint Undeployment failed");

        Assert.assertTrue(esbUtils.isSequenceUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleSequence")
                , "sampleSequence Undeployment failed");
        Assert.assertTrue(esbUtils.isSequenceUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleFaultSequence")
                , "sampleFaultSequence Undeployment failed");
        Assert.assertTrue(esbUtils.isSequenceUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleSequenceWithErrorSequence")
                , "sampleSequenceWithErrorSequence Undeployment failed");

        Assert.assertTrue(esbUtils.isApiUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "SampleAPI")
                , "SampleAPI Undeployment failed");

        Assert.assertTrue(esbUtils.isLocalEntryUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleInLineXMLLocalentry")
                , "InLine XML Local entry Undeployment failed");
        Assert.assertTrue(esbUtils.isLocalEntryUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleURLLocalEntry")
                , "URL Local Entry Undeployment failed");
        Assert.assertTrue(esbUtils.isLocalEntryUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleInLineTextLocalEntry")
                , "InLine text Local Entry Undeployment failed");

        Assert.assertTrue(esbUtils.isProxyUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "samplePassThroughProxy")
                , "Pass Through Proxy service Undeployment failed");
        Assert.assertTrue(esbUtils.isProxyUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "transformProxySample")
                , "transform Proxy service Undeployment failed");
        Assert.assertTrue(esbUtils.isProxyUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(), "sampleCustomProxy")
                , "Custom Proxy service Undeployment failed");
    }
}
