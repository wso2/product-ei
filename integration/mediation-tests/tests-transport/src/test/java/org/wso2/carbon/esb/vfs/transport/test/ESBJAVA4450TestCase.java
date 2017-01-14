/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.vfs.transport.test;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * Related to https://wso2.org/jira/browse/ESBJAVA-4450 This class tests whether
 * the file type can be choose when writing files from the ESB
 */
public class ESBJAVA4450TestCase extends ESBIntegrationTest {

	private FTPServerManager ftpServerManager;
	private File FTPFolder;
	private ServerConfigurationManager serverConfigurationManager;

	@BeforeClass(alwaysRun = true) public void runFTPServer() throws Exception {
		// Username password for the FTP server to be started
		String FTPUsername = "admin";
		String FTPPassword = "admin";
		String inputFolderName = "in";
		int FTPPort = 8086;
		String pathToFtpDir = getClass()
				.getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" +
				             File.separator + "vfsTransport" + File.separator).getPath();
		// Local folder of the FTP server root
		FTPFolder = new File(pathToFtpDir + "FTP_Location" + File.separator);
		// create FTP server root folder if not exists
		if (FTPFolder.exists()) {
			FileUtils.deleteDirectory(FTPFolder);
		}
		Assert.assertTrue(FTPFolder.mkdir(), "FTP root file folder not created");
		// create a directory under FTP server root
		File inputFolder = new File(FTPFolder.getAbsolutePath() + File.separator + inputFolderName);

		if (inputFolder.exists()) {
			FileUtils.deleteDirectory(inputFolder);
		}
		Assert.assertTrue(inputFolder.mkdir(), "FTP data /in folder not created");

		// start-up FTP server
		ftpServerManager = new FTPServerManager(FTPPort, FTPFolder.getAbsolutePath(), FTPUsername, FTPPassword);
		ftpServerManager.startFtpServer();

		super.init();
		// replace the axis2.xml enabled vfs transfer and restart the ESB server
		// gracefully
		serverConfigurationManager = new ServerConfigurationManager(context);
		serverConfigurationManager.applyConfiguration(new File(getClass().getResource(
				File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" +
				File.separator + "vfsTransport" + File.separator + "axis2.xml").getPath()));
		super.init();
		loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator +
		                                  "synapseconfig" + File.separator + "vfsTransport" + File.separator +
		                                  "vfs_file_type.xml");
	}

	@Test(groups = "wso2.esb", description = "VFS transfer file type if default") public void TestDefaultFileType()
			throws Exception {
		try {
			axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxyDefault"), null, "WSO2");
		} catch (Exception axisFault) {
			//We expect this as no response is sent to the backend
		}
		File file = new File(FTPFolder.getAbsolutePath() + File.separator + "in" + File.separator + "default.xml");
		Assert.assertTrue(file.exists(), "Default file type transfer failed");
	}

	@Test(groups = "wso2.esb", description = "VFS transfer file type if Binary") public void TestBinaryFileType()
			throws Exception {
		try {
			axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxyBinary"), null, "WSO2");
		} catch (Exception axisFault) {
			//We expect this as no response is sent to the backend
		}
		File file = new File(FTPFolder.getAbsolutePath() + File.separator + "in" + File.separator + "binary.xml");
		Assert.assertTrue(file.exists(), "Default file type transfer failed");
	}

	@Test(groups = "wso2.esb", description = "VFS transfer file type if ascii") public void TestAsciiFileType()
			throws Exception {
		try {
			axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxyAscii"), null, "WSO2");
		} catch (Exception axisFault) {
			//We expect this as no response is sent to the backend
		}
		File file = new File(FTPFolder.getAbsolutePath() + File.separator + "in" + File.separator + "ascii.xml");
		Assert.assertTrue(file.exists(), "Default file type transfer failed");
	}

	@AfterClass(alwaysRun = true) public void stopFTPServer() throws Exception {
		try {
			super.cleanup();
		} finally {
			Thread.sleep(3000);
			ftpServerManager.stop();
			log.info("FTP Server stopped successfully");
			serverConfigurationManager.restoreToLastConfiguration();
		}
	}

}
