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

import java.io.File;
import java.io.IOException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class InboundTransportTest extends ESBIntegrationTest {

	private LogViewerClient logViewerClient;
	private File InboundFileFolder;
	private String pathToFtpDir;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		pathToFtpDir = getClass().getResource(
				File.separator + "artifacts" + File.separator + "ESB"
						+ File.separator + "synapseconfig" + File.separator
						+ "vfsTransport" + File.separator).getPath();

		InboundFileFolder = new File(pathToFtpDir + File.separator
				+ "InboundFileFolder");

		// create InboundFileFolder if not exists
		if (InboundFileFolder.exists()) {
			FileUtils.deleteDirectory(InboundFileFolder);
		}
		Assert.assertTrue(InboundFileFolder.mkdir(), "InboundFileFolder not created");


		super.init();

		loadESBConfigurationFromClasspath(File.separator + "artifacts"
				+ File.separator + "ESB" + File.separator + "synapseconfig"
				+ File.separator + "inboundEndpoint" + File.separator
				+ "inboundFile.xml");

		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),
				getSessionCookie());

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = "wso2.esb", description = "Inbound endpoint Reading file with Contect type XML Test Case")
	public void testInboundEnpointReadFile_ContentType_XML() throws Exception {

		addInboundEndpoint(addEndpoint1());
		// To check the file getting is read
		boolean isFileRead = false;

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator + "in");
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

	@Test(groups = "wso2.esb", dependsOnMethods = "testInboundEnpointReadFile_ContentType_XML", description = "Inbound endpoint Delete file after reading Test Case")
	public void testInboundEnpointDeleteFileAfterProcess() throws Exception {

		File sourceFile = new File(InboundFileFolder + File.separator + "in"
				+ File.separator + "test.xml");
		Assert.assertFalse(sourceFile.exists(),
				"The file is not deleted after the read");
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, dependsOnMethods = "testInboundEnpointDeleteFileAfterProcess", description = "Inbound Endpoint invalid interval Test case")
	public void testInboundEndpointPollInterval_NonInteger() throws Exception {

		addInboundEndpoint(addEndpoint3());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator
				+ "interval");
		File targetFile = new File(targetFolder + File.separator + "test.xml");
		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);

			Assert.assertTrue(targetFile.exists());
		} finally {
			deleteFile(targetFile);
			deleteFile(targetFolder);
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, dependsOnMethods = "testInboundEndpointPollInterval_NonInteger", description = "Inbound Endpoint invalid File URI Test case")
	public void testInboundEndpointInvalidFileUri() throws Exception {

		addInboundEndpoint(addEndpoint4());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator + "uri");
		File targetFile = new File(targetFolder + File.separator + "test.xml");
		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);

			Assert.assertTrue(targetFile.exists());
		} finally {
			deleteFile(targetFile);
			deleteFile(targetFolder);
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, dependsOnMethods = "testInboundEndpointInvalidFileUri", description = "Inbound Endpoint File name with special chars URI Test case")
	public void testInboundEndpointFileName_SpecialChars() throws Exception {

		addInboundEndpoint(addEndpoint5());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator
				+ "spcChar");
		File targetFile = new File(targetFolder + File.separator
				+ "test123@wso2_xml.xml");
		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);

			Assert.assertTrue(!targetFile.exists());
		} finally {
			deleteFile(targetFile);
			deleteFile(targetFolder);
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, dependsOnMethods = "testInboundEndpointFileName_SpecialChars", description = "Inbound Endpoint Content type invalid Test case")
	public void testInboundEndpointContentTypeInvalid() throws Exception {

		addInboundEndpoint(addEndpoint6());

		// To check the file getting is read
		boolean isFileRead = false;

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator + "in");
		File targetFile = new File(targetFolder + File.separator
				+ "invalidContentType.xml");
		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);

			LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

			for (LogEvent logEvent : logs) {
				String message = logEvent.getMessage();
				if (message.contains("<m0:symbol>WSO2</m0:symbol>")) {
					isFileRead = true;
				}
			}

			Assert.assertTrue(isFileRead, "The XML file is not getting read");

			Assert.assertTrue(!targetFile.exists());
		} finally {
			deleteFile(targetFile);
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, dependsOnMethods = "testInboundEndpointContentTypeInvalid", description = "Inbound Endpoint Content type not specified Test case")
	public void testInboundEndpointContentTypeNotSpecified() throws Exception {

		addInboundEndpoint(addEndpoint7());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator + "in");
		File targetFile = new File(targetFolder + File.separator + "in.xml");
		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);

			Assert.assertTrue(!targetFile.exists());
		} finally {
			deleteFile(targetFile);
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, dependsOnMethods = "testInboundEndpointContentTypeNotSpecified", description = "Inbound Endpoint move after process Test case")
	public void testInboundEndpointMoveAfterProcess() throws Exception {

		addInboundEndpoint(addEndpoint8());

		File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
		File targetFolder = new File(InboundFileFolder + File.separator
				+ "move");
		File targetFile = new File(targetFolder + File.separator + "test.xml");
		File processedFolder = new File(InboundFileFolder + File.separator
				+ "processed");
		if (processedFolder.exists()) {
			processedFolder.delete();
		} else {
			processedFolder.mkdir();
		}

		File processedFile = new File(processedFolder + File.separator
				+ "test.xml");

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			Thread.sleep(2000);
			// input file should be moved to processed directory after
			// processing the input file
			Assert.assertTrue(processedFile.exists(),
					"Input file is not moved after processing the file");
			Assert.assertFalse(targetFile.exists(),
					"Input file is exist after processing the input file");
		} finally {
			deleteFile(targetFolder);
			deleteFile(processedFile);
			deleteFile(processedFolder);
		}
	}

	private OMElement addEndpoint1() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile1\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "in"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint3() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile3\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1.1</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "interval"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint4() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile4\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "uri"
						+ File.separator
						+ "fail"
						+ "</parameter>\n"
						+ " </parameters>\n" + "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint5() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile5\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "spcChar"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint6() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile6\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">invalid</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "in"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint7() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile7\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "in"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private OMElement addEndpoint8() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint name=\"testFile8\" onError=\"inFault\" protocol=\"file\"\n"
						+ " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
						+ " <parameters>\n"
						+ " <parameter name=\"interval\">1000</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
						+ " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
						+ " <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n"
						+ "<parameter name=\"transport.vfs.MoveAfterProcess\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "processed"
						+ "</parameter>"
						+ " <parameter name=\"transport.vfs.FileURI\">file://"
						+ InboundFileFolder
						+ File.separator
						+ "move"
						+ "</parameter>\n"
						+ " </parameters>\n"
						+ "</inboundEndpoint>\n");

		return synapseConfig;
	}

	private boolean deleteFile(File file) throws IOException {
		return file.exists() && file.delete();
	}
}
