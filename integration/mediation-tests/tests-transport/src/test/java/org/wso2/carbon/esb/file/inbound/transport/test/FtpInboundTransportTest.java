/*
 *Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.file.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.io.File;
import java.io.IOException;

public class FtpInboundTransportTest extends ESBIntegrationTest {
	private FTPServerManager ftpServerManager;
	private String FTPUsername;
	private String FTPPassword;
	private File FTPFolder;
	private File inputFolder;
	private File outputFolder;
	private LogViewerClient logViewerClient;
	private String pathToFtpDir;

	@BeforeClass(alwaysRun = true)
	public void runFTPServerForInboundTest() throws Exception {

		// Username password for the FTP server to be started
		FTPUsername = "admin";
		FTPPassword = "admin";
		String inputFolderName = "ftpin";
		String outputFolderName = "ftpout";
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


		// create 'out' directory under FTP server root
		outputFolder = new File(FTPFolder.getAbsolutePath() + File.separator
				+ outputFolderName);

		if (outputFolder.exists()) {
			FileUtils.deleteDirectory(outputFolder);
		}
		Assert.assertTrue(outputFolder.mkdir(), "FTP data /in folder not created");

		/* Make the port available */
		Utils.shutdownFailsafe(FTPPort);

		// start-up FTP server
		ftpServerManager = new FTPServerManager(FTPPort,
				FTPFolder.getAbsolutePath(), FTPUsername, FTPPassword);
		ftpServerManager.startFtpServer();

		super.init();
		loadESBConfigurationFromClasspath(File.separator + "artifacts"
				+ File.separator + "ESB" + File.separator + "synapseconfig"
				+ File.separator + "inboundEndpoint" + File.separator
				+ "inboundFile.xml");

		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),
				getSessionCookie());
		log.info("Before Class test method completed successfully");

	}

	@AfterClass(alwaysRun = true)
	public void stopFTPServerForInboundTest() throws Exception {
		try {
			super.cleanup();
		} finally {
			Thread.sleep(3000);
			ftpServerManager.stop();
			log.info("FTP Server stopped successfully");

		}

	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = "wso2.esb", description = "Inbound endpoint Reading file in FTP Test Case")
	public void testInboundEnpointReadFileinFTP() throws Exception {

		addInboundEndpoint(addEndpoint1());
		// To check the file getting is read
		boolean isFileRead = false;

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(FTPFolder + File.separator + "ftpin");
		File targetFile = new File(targetFolder + File.separator + "test.xml");

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);
		} finally {
			deleteFile(targetFile);
		}

		LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

		for (LogEvent logEvent : logs) {
			String message = logEvent.getMessage();
			if (message.contains("<m0:symbol>WSO2</m0:symbol>")) {
				isFileRead = true;
			}
		}

		Assert.assertTrue(isFileRead, "The XML file is not getting read");
	}

	//  This test case works locally, but in the Jenkins build, it fails due to a lack of permission issue
	//	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	//	@Test(groups = "wso2.esb", dependsOnMethods = "testInboundInvalidFtpUsername", description = "Inbound endpoint move after process in FTP Test Case")
	public void testInboundEnpointMoveAfterProcessFTP() throws Exception {

		addInboundEndpoint(addEndpoint2());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFile = new File(FTPFolder + File.separator + "ftpin"
				+ File.separator + "test.xml");
		File outFile = new File(FTPFolder + File.separator + "ftpout"
				+ File.separator + "test.xml");

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);
			Assert.assertTrue(outFile.exists(),
					"Input file is not moved after processing the file");
			Assert.assertFalse(targetFile.exists(),
					"Input file is exist after processing the input file");
		} finally {
			deleteFile(targetFile);
			deleteFile(outFile);
		}

	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = "wso2.esb", description = "Inbound endpoint invalid FTP username Test Case")
	public void testInboundInvalidFtpUsername() throws Exception {

		addInboundEndpoint(addEndpoint3());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFile = new File(FTPFolder + File.separator + "ftpin"
				+ File.separator + "test.xml");
		File outFile = new File(FTPFolder + File.separator + "ftpout"
				+ File.separator + "test.xml");

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);
			Assert.assertTrue(!outFile.exists());

		} finally {
			deleteFile(targetFile);
		}
	}

	private OMElement addEndpoint1() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFtpFile1\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">disable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">ftp://admin:admin@localhost:9653/ftpin/test.xml"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint2() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFtpFile2\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n"
						+ "<parameter name=\"transport.vfs.MoveAfterProcess\">ftp://admin:admin@localhost:9653/ftpout"
						+ "</parameter>"
						+ " <parameter name=\"transport.vfs.FileURI\">ftp://admin:admin@localhost:9653/ftpin"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint3() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFtpFile3\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n"
						+ "<parameter name=\"transport.vfs.MoveAfterProcess\">ftp://admin:admin@localhost:9653/ftpout/test.xml"
						+ "</parameter>"
						+ " <parameter name=\"transport.vfs.FileURI\">ftp://invalid:admin@localhost:9653/ftpin/test.xml"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private boolean deleteFile(File file) throws IOException {
		return file.exists() && file.delete();
	}
}
