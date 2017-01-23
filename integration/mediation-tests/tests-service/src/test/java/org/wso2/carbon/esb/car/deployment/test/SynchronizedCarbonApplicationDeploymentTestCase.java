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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to test .car - synchronized deployment feature
 */
public class SynchronizedCarbonApplicationDeploymentTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;
    private static ApplicationAdminClient applicationAdminClient;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        applicationAdminClient = new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @AfterClass(alwaysRun = true)
    protected void close() throws Exception {

        // TODO - Faulty car app removal
        super.cleanup();
    }
    //TODO - ReEnable
    @Test(groups = {"wso2.esb"}, description = "verify artifact deployment " +
            "- testing valid synchronized deployment",enabled = false)
    public void validSynchronizedCarAppDeploymentTest() throws Exception {

        String messageStoreLog = "Message Store named 'MyStore' has been deployed";
        String messageProcessorLog = "Message Processor named 'ScheduledProcessor' " +
                "has been deployed";
        String endpointLog = "Endpoint named 'StockQuoteServiceEp' has been deployed";
        String successfulDeploymentLog = "Successfully Deployed Carbon Application :" +
                " SynchroDepValidCarApp_1.0.0";

        boolean messageStoreDeployed = false;
        boolean messageProcessorDeployed = false;
        boolean endpointDeployed = false;
        boolean carAppDeploymentStatus = false;

        // before deployment of car app
        int beforeLogSize = logViewer.getAllSystemLogs().length;

        CarbonAppUploaderClient carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.uploadCarbonAppArtifact("SynchroDepValidCarApp_1.0.0.car"
                , new DataHandler(new URL("file:" + File.separator + File.separator +
                getESBResourceLocation()
                + File.separator + "car" + File.separator + "SynchroDepValidCarApp_1.0.0.car")));

        assertTrue(isCarFileDeployed("SynchroDepValidCarApp_1.0.0"),
                "SynchroDepValidCarApp_1.0.0 Car app deployment failed");

        TimeUnit.SECONDS.sleep(10);

        assertTrue(esbUtils.isEndpointDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "StockQuoteServiceEp")
                , "StockQuoteServiceEp Endpoint deployment failed");
        assertTrue(esbUtils.isMessageStoreDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "MyStore")
                , "MyStore Message Store deployment failed");
        assertTrue(esbUtils.isMessageProcessorDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie()
                , "ScheduledProcessor")
                , "ScheduledProcessor Message Processor deployment failed");

        // after deployment of car app
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(endpointLog)) {  // first - endpoint deployment
                endpointDeployed = true;
            } else if ((logs[i].getMessage().contains(messageStoreLog))
                    && (endpointDeployed)) {  // second - message-store deployment
                messageStoreDeployed = true;
            } else if (logs[i].getMessage().contains(messageProcessorLog)
                    && (messageStoreDeployed && endpointDeployed)) {  // third - message processor deployment
                messageProcessorDeployed = true;
            } else if (logs[i].getMessage().contains(successfulDeploymentLog)) {
                if ((messageStoreDeployed) && (messageProcessorDeployed) && (endpointDeployed)) {
                    carAppDeploymentStatus = true;
                } else {
                    break;
                }
            }
        }

        // car file deployment artifact sequence status
        assertTrue(carAppDeploymentStatus, "Artifact deployment sequence mismatch");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "IBM");

        assertTrue(response.toString().contains("IBM Company"),
                "Response does not contain IBM Company");

        applicationAdminClient.deleteApplication("SynchroDepValidCarApp_1.0.0");

        assertTrue(esbUtils.isEndpointUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "StockQuoteServiceEp")
                , "StockQuoteServiceEp Endpoint Un-deployment failure");

        assertTrue(esbUtils.isMessageStoreUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "MyStore")
                , "MyStore Message Store Un-deployment failure");

        assertTrue(esbUtils.isMessageProcessorUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "ScheduledProcessor")
                , "ScheduledProcessor MessageProcessor Un-deployment failure");
    }

    //TODO Re-enable the Test
    @Test(groups = {"wso2.esb"}, description = "verify artifact deployment and revert " +
            "- testing invalid synchronized deployment",
            dependsOnMethods = "validSynchronizedCarAppDeploymentTest",enabled = false)
    public void invalidSynchronizedCarAppDeploymentTest() throws Exception {

        String messageStoreLog = "Message Store named 'MyStore1' has been deployed";
        String messageProcessorLog = "Message Processor named 'ScheduledProcessor1' " +
                "has been deployed";
        String endpointLog = "StockQuoteServiceEp1-1.0.0.xml : Failed!";
        String revertLog = "Reverting successfully deployed artifcats in this CApp " +
                ": SynchroDepInValidCarApp_1.0.0";
        String messageStoreUnDeployedLog = "MessageStore named 'MyStore1' has been undeployed";
        String messageProcessorUnDeployedLog = "MessageProcessor named 'ScheduledProcessor1'" +
                " has been undeployed";

        boolean messageStoreDeployed = false;
        boolean messageProcessorDeployed = false;
        boolean revertMessage = false;
        boolean messageStoreUnDeployed = false;
        boolean messageProcessorUnDeployed = false;
        boolean endpointFailureStatus = false;

        // before deployment of car file
        int beforeLogSize = logViewer.getAllSystemLogs().length;

        CarbonAppUploaderClient carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.uploadCarbonAppArtifact("SynchroDepInValidCarApp_1.0.0.car"
                , new DataHandler(new URL("file:" + File.separator + File.separator +
                getESBResourceLocation()
                + File.separator + "car" + File.separator + "SynchroDepInValidCarApp_1.0.0.car")));

        TimeUnit.SECONDS.sleep(20);

        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(messageStoreLog)) {   // first - message store
                messageStoreDeployed = true;
            } else if ((logs[i].getMessage().contains(messageProcessorLog)) && (messageStoreDeployed)) {  // second message processor
                messageProcessorDeployed = true;
            } else if (logs[i].getMessage().contains(endpointLog)) {  // end point fails
                endpointFailureStatus = true;
            } else if (logs[i].getMessage().contains(revertLog)) {   // reverting
                revertMessage = true;
            } else if (logs[i].getMessage().contains(messageStoreUnDeployedLog)) {
                messageStoreUnDeployed = true;
            } else if (logs[i].getMessage().contains(messageProcessorUnDeployedLog)) {
                messageProcessorUnDeployed = true;
            }
        }

        // checking logs for failure scenario
        assertTrue(messageStoreDeployed, "MyStore1 deployment log not found");
        assertTrue(messageProcessorDeployed, "ScheduledProcessor1 deployment log not found");
        assertTrue(endpointFailureStatus, "StockQuoteServiceEp1 failure log not found");
        assertTrue(revertMessage, "Reverting deployed artifacts log not found");
        assertTrue(messageStoreUnDeployed, "MyStore1 undeployment log not found");
        assertTrue(messageProcessorUnDeployed, "ScheduledProcessor1 undeployment log not found");

        assertTrue(esbUtils.isMessageStoreUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "MyStore1")
                , "MyStore1 Message Store Un-deployment failure");

        assertTrue(esbUtils.isMessageProcessorUnDeployed(context.getContextUrls().getBackEndUrl(), getSessionCookie(),
                "ScheduledProcessor1")
                , "ScheduledProcessor1 MessageProcessor Un-deployment failure");
    }

    private boolean isCarFileDeployed(String carFileName) throws Exception {

        int MAX_TIME = 120000;
        log.info("waiting " + MAX_TIME + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()))
                < MAX_TIME) {
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
}
