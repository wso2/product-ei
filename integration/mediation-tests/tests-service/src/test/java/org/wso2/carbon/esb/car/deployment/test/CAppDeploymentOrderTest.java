/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * This class tests whether the artifacts in deployed and un deployed in the correct artifacts dependency order.
 * Related issues: https://github.com/wso2/product-ei/issues/1261, https://github.com/wso2/product-ei/issues/1737
 */
public class CAppDeploymentOrderTest extends ESBIntegrationTest {
    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final int MAX_TIME = 120000;
    private final String CAR_FILE_NAME = "esb-deployment-car_1.0.0";
    private FTPServerManager ftpServerManager;
    private String FTPUsername;
    private String FTPPassword;
    private File FTPFolder;
    private File inputFolder;
    private LogViewerClient logViewerClient;
    private String pathToFtpDir;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        runFTPServerForInboundTest();
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", enabled = true, description = "Test whether proxy service get deployed,unDeploy "
            + "through capp in order")
    protected void carFileDeploymentOrderTest() throws Exception {
        logViewerClient.clearLogs();
        carbonAppUploaderClient = new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.uploadCarbonAppArtifact(CAR_FILE_NAME + ".car"
                , new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                        + File.separator + "car" + File.separator + CAR_FILE_NAME + ".car")));
        applicationAdminClient = new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(CAR_FILE_NAME), "Car file deployment failed");
        boolean deploymentOrdered = checkLogOrder(logViewerClient);
        Assert.assertTrue(deploymentOrdered, "Deployment order isn't correct");

        //check un deployment order
        logViewerClient.clearLogs();
        applicationAdminClient.deleteApplication(CAR_FILE_NAME);
        Assert.assertTrue(isCarFileUnDeployed(CAR_FILE_NAME), "Car file un deployment failed");
        Assert.assertTrue(checkUnDeployLogOrder(logViewerClient), "Un Deployment order isn't correct");
    }

    private boolean checkUnDeployLogOrder(LogViewerClient logViewerClient)
            throws RemoteException {
        LogEvent[] systemLogs;
        systemLogs = logViewerClient.getAllRemoteSystemLogs();
        //Create a stack to store the intended logs in order
        Stack<String> logStack = new Stack<>();
        logStack.push("Inbound Endpoint named 'inbound-endpoint' has been undeployed");
        logStack.push("Sequence named 'test-sequence' has been undeployed");

        //Check whether the logs are in the stack's order
        if (systemLogs != null) {
            for (LogEvent logEvent : systemLogs) {
                if (logEvent == null) {
                    continue;
                }
                if (logStack.size() != 0 && logEvent.getMessage().contains(logStack.peek())){
                    logStack.pop();
                }
            }
        }
        return logStack.isEmpty();
    }

    private boolean checkLogOrder(LogViewerClient logViewerClient) throws RemoteException {
        LogEvent[] systemLogs;
        systemLogs = logViewerClient.getAllRemoteSystemLogs();
        //Create a stack to store the intended logs in order
        Stack<String> logStack = new Stack<>();
        logStack.push("Sequence named 'test-sequence' has been deployed from file");
        logStack.push("Inbound Endpoint named 'inbound-endpoint' has been deployed from file");

        //Check whether the logs are in the stack's order
        if (systemLogs != null) {
            for (LogEvent logEvent : systemLogs) {
                if (logEvent == null) {
                    continue;
                }
                if(logStack.size() != 0 && logEvent.getMessage().contains(logStack.peek())){
                    logStack.pop();
                }
            }
        }

        return logStack.size() == 0;
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
        log.info("waiting " + MAX_TIME + " millis for car un deployment " + carFileName);
        boolean isCarFileUnDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < MAX_TIME) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (!ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileUnDeployed = true;
                    log.info("car file un deployed in " + time + " mills");
                    return isCarFileUnDeployed;
                }
            } else {
                return true;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
        return isCarFileUnDeployed;
    }

    private void runFTPServerForInboundTest() throws Exception {
        // Username password for the FTP server to be started
        FTPUsername = "admin";
        FTPPassword = "admin";
        String inputFolderName = "ftpin";
        int FTPPort = 9653;

        pathToFtpDir = getClass().getResource(
                File.separator + "artifacts" + File.separator + "ESB"
                        + File.separator + "synapseconfig" + File.separator
                        + "vfsTransport" + File.separator).getPath();

        // Local folder of the FTP server root
        FTPFolder = new File(pathToFtpDir + "FTP_Location" + File.separator);

        // create FTP server root folder if not exists
        if (FTPFolder.exists()) {
            FileUtils.deleteDirectory(FTPFolder);
        }
        Assert.assertTrue(FTPFolder.mkdir(), "FTP root file folder not created");

        // create 'in' directory under FTP server root
        inputFolder = new File(FTPFolder.getAbsolutePath() + File.separator
                + inputFolderName);

        if (inputFolder.exists()) {
            FileUtils.deleteDirectory(inputFolder);
        }
        Assert.assertTrue(inputFolder.mkdir(), "FTP data /in folder not created");

		/* Make the port available */
        Utils.shutdownFailsafe(FTPPort);

        // start-up FTP server
        ftpServerManager = new FTPServerManager(FTPPort,
                FTPFolder.getAbsolutePath(), FTPUsername, FTPPassword);
        ftpServerManager.startFtpServer();

        log.info("FTP Server startup completed successfully");
    }

    @AfterClass(alwaysRun = true)
    public void restoreServerConfiguration() throws Exception {
        try {
            super.cleanup();
        } finally {
            //stop the ftp server
            ftpServerManager.stop();
        }
    }
}
