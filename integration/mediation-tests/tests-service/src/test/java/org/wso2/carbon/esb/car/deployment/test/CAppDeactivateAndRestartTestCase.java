/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.carbon.esb.car.deployment.test;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.esb.integration.common.clients.service.mgt.ServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.activation.DataHandler;

/**
 * Testcase to test persisting deactivation of vfs proxy deployed using CAPP after restart of the server
 */
public class CAppDeactivateAndRestartTestCase extends ESBIntegrationTest {

    private ServiceAdminClient serviceAdminClient;
    private String service = "VFSTestProxy";
    private String carFileName = "CappDeactivateAndRestartTest_1.0.0.car";
    private String cappName = "CappDeactivateAndRestartTest_1.0.0";
    private ServerConfigurationManager serverConfigurationManager;
    private File inputFolder;
    private File outputFolder;
    private FTPServerManager ftpServerManager;

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {

        //start FTP server
        startFTPServer();

        // replace the axis2.xml enabled vfs transfer and restart the ESB server
        // gracefully
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() +
                File.separator + "synapseconfig" +
                File.separator + "vfsTransport" +
                File.separator + "axis2.xml"));
        log.info("Updated axis2.xml to enable vfs transport");

        super.init();
        //upload CAPP
        CarbonAppUploaderClient carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName,
                new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                        + File.separator + "car" + File.separator + carFileName)));
        log.info(carFileName + " uploaded successfully");

        //deactivate proxy service
        serviceAdminClient =
                new ServiceAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        isProxyDeployed(service);
        serviceAdminClient.stopService(service);

        //Wait and check till the service get deactivated maximum for ~20sec
        for (int i = 0; i < 20; i++) {
            if (!serviceAdminClient.getServicesData(service).getActive()) {
                break;
            }
            log.info("Wait to service get deactivated");
            Thread.sleep(1000);
        }
        Assert.assertFalse(serviceAdminClient.getServicesData(service).getActive(), "Unable to stop service: " + service);

        serverConfigurationManager.restartGracefully();
        super.init();
    }

    @Test(groups = "wso2.esb", enabled = true, description = "Test whether proxy service is inactive")
    public void testVFSProxyInactiveState() throws AutomationUtilException, IOException, InterruptedException {

        //create test file in the ftp server
        FileUtils.copyFile(new File(getESBResourceLocation() +
                File.separator + "synapseconfig" +
                File.separator + "vfsTransport" +
                File.separator + "test.xml"), new File(inputFolder.getPath() + File.separator + "test.xml"));

        //check the output directory
        File filePathToOutputFile = new File(outputFolder.getPath() + File.separator + "test.xml");

        //wait and check till polling time get exceeded for ~15seconds
        for (int i = 0; i < 15; i++) {
            log.info("Wait and check output directory to verify service is deactivated successfully");
            Assert.assertFalse(filePathToOutputFile.exists(), "File exists, hence the service :" +
                    service + " deactivation is not persisted");
            Thread.sleep(1000);
        }
    }

    @AfterClass(alwaysRun = true)
    public void restoreServerConfiguration() throws Exception {
        try {
            ApplicationAdminClient applicationAdminClient =
                    new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
            applicationAdminClient.deleteApplication(cappName);
            super.cleanup();
        } finally {
            //stop the ftp server
            ftpServerManager.stop();
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }

    private void startFTPServer() throws IOException {

        // Username password for the FTP server to be started
        String FTPUsername = "admin";
        String FTPPassword = "admin";
        int FTPPort = 8085;

        String pathToFtpDir = getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" +
                File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath();

        // Local folder of the FTP server root
        File ftpFolder = new File(pathToFtpDir + "FTP_Location" + File.separator);

        // create FTP server root folder if not exists
        if (ftpFolder.exists()) {
            FileUtils.deleteDirectory(ftpFolder);
        }
        Assert.assertTrue(ftpFolder.mkdir(), "FTP root file folder not created");

        // create a directory under FTP server in dir
        inputFolder = new File(ftpFolder.getAbsolutePath() + File.separator + "in");

        if (inputFolder.exists()) {
            FileUtils.deleteDirectory(inputFolder);
        }
        Assert.assertTrue(inputFolder.mkdir(), "FTP data /in folder not created");

        // create a directory under FTP server out dir
        outputFolder = new File(ftpFolder.getAbsolutePath() + File.separator + "out");

        if (outputFolder.exists()) {
            FileUtils.deleteDirectory(inputFolder);
        }
        Assert.assertTrue(outputFolder.mkdir(), "FTP data /out folder not created");

        // start-up FTP server
        ftpServerManager = new FTPServerManager(FTPPort, ftpFolder.getAbsolutePath(), FTPUsername, FTPPassword);
        ftpServerManager.startFtpServer();

    }

}
