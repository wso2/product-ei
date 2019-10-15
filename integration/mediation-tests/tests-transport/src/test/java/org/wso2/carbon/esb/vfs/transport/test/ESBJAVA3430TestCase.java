package org.wso2.carbon.esb.vfs.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * Related to https://wso2.org/jira/browse/ESBJAVA-3430 This class tests whether
 * the null check for replyFile.getParent() in VFSTransportSender is available
 */
public class ESBJAVA3430TestCase extends ESBIntegrationTest {

	private FTPServerManager ftpServerManager;
	private String FTPUsername;
	private String FTPPassword;
	private File FTPFolder;
	private File inputFolder;
	private LogViewerClient logViewerClient;
	private String pathToFtpDir;

	@BeforeClass(alwaysRun = true)
	public void runFTPServer() throws Exception {

		// Username password for the FTP server to be started
		FTPUsername = "admin";
		FTPPassword = "admin";
		String inputFolderName = "in";
		int FTPPort = 8085;

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

		// create a directory under FTP server root
		inputFolder = new File(FTPFolder.getAbsolutePath() + File.separator
				+ inputFolderName);

		if (inputFolder.exists()) {
			FileUtils.deleteDirectory(inputFolder);
		}
		Assert.assertTrue(inputFolder.mkdir(), "FTP data /in folder not created");

		// start-up FTP server
		ftpServerManager = new FTPServerManager(FTPPort,
				FTPFolder.getAbsolutePath(), FTPUsername, FTPPassword);
		ftpServerManager.startFtpServer();

		super.init();
		loadESBConfigurationFromClasspath(File.separator + "artifacts"
				+ File.separator + "ESB" + File.separator + "synapseconfig"
				+ File.separator + "vfsTransport" + File.separator
				+ "vfs_null_check.xml");

		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

	}

	@AfterClass(alwaysRun = true)
	public void stopFTPServer() throws Exception {
		try {
			super.cleanup();
		} finally {
			ftpServerManager.stop();
			log.info("FTP Server stopped successfully");
		}

	}

	@Test(groups = "wso2.esb", description = "VFS NPE in Creating a File in FTP directly in root directory")
	public void TestCreateFileInRoot() throws Exception {

		// To check the timed out exception happened
		boolean timeout = false;
		// To check whether the NPE happened
		boolean isError = false;

		try {
			OMElement response = axis2Client.sendSimpleStockQuoteRequest(
					getProxyServiceURLHttp("VFSProxyFileCreateInRoot"), null,
					"WSO2");
		} catch (AxisFault axisFault) {
			if (axisFault.getLocalizedMessage().contains("Read timed out")) {
				timeout = true;
			}
		} finally {
			removeProxy("VFSProxyFileCreateInRoot");
		}

		LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

		for (LogEvent logEvent : logs) {
			String message = logEvent.getMessage();
			if (message.contains("Error creating file under the FTP root")) {
				isError = true;
				break;
			}
		}

		Assert.assertFalse(
				isError && timeout,
				" The null check for the replyFile.getParent() in VFSTransportSender is not available");
	}

	@Test(groups = "wso2.esb", description = "VFS NPE in Creating a File in FTP, in a directory under root")
	public void TestCreateFileInDirectoryUnderRoot() throws Exception {

		// To check the timed out exception happened
		boolean timeout = false;
		// To check whether the NPE happened
		boolean isError = false;

		try {
			OMElement response = axis2Client.sendSimpleStockQuoteRequest(
					getProxyServiceURLHttp("VFSProxyFileCreateInDirectory"),
					null, "WSO2");
		} catch (AxisFault axisFault) {
			if (axisFault.getLocalizedMessage().contains("Read timed out")) {
				timeout = true;
			}
		} finally {
			removeProxy("VFSProxyFileCreateInDirectory");
		}

		LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

		for (LogEvent logEvent : logs) {
			String message = logEvent.getMessage();
			if (message.contains("Error creating file under the FTP root")) {
				isError = true;
				break;
			}
		}

		Assert.assertFalse(
				isError && timeout,
				" The null check for the replyFile.getParent() in VFSTransportSender is not available");
	}

	/**
	 * @param proxyName
	 *            - Name of the proxy to be removed
	 * @throws Exception
	 */
	private void removeProxy(String proxyName) throws Exception {
		deleteProxyService(proxyName);
	}

}
