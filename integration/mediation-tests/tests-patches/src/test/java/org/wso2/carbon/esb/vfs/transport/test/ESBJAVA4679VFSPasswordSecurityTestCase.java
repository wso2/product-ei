/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.esb.vfs.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.automation.extensions.servers.sftpserver.SFTPServer;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import sun.print.resources.serviceui_fr;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Integration test for https://wso2.org/jira/browse/ESBJAVA-4679
 * Checks if vfs can handle secured passwords
 * This sets up a sftp server for the test, hence it requires sudo to run
 */
public class ESBJAVA4679VFSPasswordSecurityTestCase extends ESBIntegrationTest {

    private static final Logger LOGGER = Logger.getLogger(ESBJAVA4679VFSPasswordSecurityTestCase.class);

    private FTPServerManager ftpServerManager;
    private String FTPUsername;
    private String FTPPassword;
    private File FTPFolder;
    private File sampleFileFolder;
    private File inputFolder;
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient;
    private String pathToFtpDir;
    private int FTPPort = 8085;
    private String inputFolderName = "in";
    private String outputFolderName = "out";

    @BeforeClass(alwaysRun = true)
    public void runFTPServer() throws Exception {

        // Username password for the FTP server to be started
        FTPUsername = "user1";
        FTPPassword = "pass";

        pathToFtpDir = getClass().getResource(
                File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator
                + "vfsTransport" + File.separator).getPath();

        // Local folder of the FTP server root
        FTPFolder = new File(pathToFtpDir + "securePasswordFTP");
        sampleFileFolder = new File(pathToFtpDir);

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
        // replace the axis2.xml enabled vfs transfer and restart the ESB server
        // gracefully
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getClass()
                                                                       .getResource(
                                                                               File.separator + "artifacts" + File.separator + "ESB"
                                                                               + File.separator + "synapseconfig"
                                                                               + File.separator + "vfsTransport"
                                                                               + File.separator + "axis2.xml").getPath()));
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),
                                              getSessionCookie());

    }

    @AfterClass(alwaysRun = true)
    public void stopFTPServer() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            ftpServerManager.stop();
            log.info("FTP Server stopped successfully");
            serverConfigurationManager.restoreToLastConfiguration();

        }

    }

    @Test(groups = "wso2.esb", description = "VFS secure password test")
    public void securePasswordTest()
            throws XMLStreamException, IOException, InterruptedException, LogViewerLogViewerException {

        //copy SOAP message  into the SFTP server
        String sentMessageFile = "test.xml";
        File sourceMessage = new File(sampleFileFolder + File.separator + sentMessageFile);
        File destinationMessage = new File(inputFolder + File.separator + sentMessageFile);
        copyFile(sourceMessage, destinationMessage);

        //Below is encrypted value of "user1:pass" using local wso2carbon.jks TODO if security settings change, will need to change below value as well. Otherwise this test will fail
        String encryptedPass = "dRF/uh3jmT1UnTIvrqZ9jDHZjRoMQxK2lkvMoFWAb9J1FnWePw2xgLiZYJ+BHXVxy9gyj6/3SVrB+56StL5bYyRp15VanM+Noj7g8DlAHhOA+s5YtIqzbto5zGDLyXs2pi0oY8o6/z3OCJuegVLszksdAZdA0OEHzMpRL/voexE=";
        String fileUri = "vfs:ftp://{wso2:vault-decrypt('" + encryptedPass + "')}@localhost:" + FTPPort + "/" + inputFolderName;
        String moveAfterProcess = "vfs:ftp://{wso2:vault-decrypt('" + encryptedPass + "')}@localhost:" + FTPPort + "/" + outputFolderName;

        String log = "File recieved for secure password for the proxy service - ";
        //create VFS transport listener proxy
        /**
         * TODO Note that encrypted urls provided here are encrypted using wso2carbon.jks, so if it gets changed, these test may fail
         */
        String proxy = "<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                       "       name=\"VfsSecurePasswordTest\"\n" +
                       "       transports=\"vfs http https\"\n" +
                       "       startOnLoad=\"true\"\n" +
                       "       trace=\"disable\">\n" +
                       "   <description/>\n" +
                       "   <target>\n" +
                       "      <inSequence>\n" +
                       "         <property name=\"transport.vfs.ReplyFileName\"\n" +
                       "                   expression=\"get-property('transport', 'FILE_NAME')\"\n" +
                       "                   scope=\"transport\"\n" +
                       "                   type=\"STRING\"/>\n" +
                       "         <log level=\"custom\">\n" +
                       "            <property name=\"" + log + "\"\n" +
                       "                      expression=\"fn:concat(' - File ',get-property('transport','FILE_NAME'),' received')\"/>\n" +
                       "         </log>\n" +
                       "         <drop/>\n" +
                       "      </inSequence>\n" +
                       "   </target>\n" +
                       "   <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                       "   <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n" +
                       "   <parameter name=\"transport.vfs.ClusterAwareness\">true</parameter>\n" +
                       "   <parameter name=\"transport.vfs.FileURI\">" + fileUri + "</parameter>\n" +
                       "   <parameter name=\"transport.vfs.MoveAfterProcess\">" + moveAfterProcess + "</parameter>\n" +
                       "   <parameter name=\"transport.vfs.FileNamePattern\">test.*\\.xml</parameter>\n" +
                       "   <parameter name=\"transport.vfs.Locking\">disable</parameter>\n" +
                       "   <parameter name=\"transport.vfs.ContentType\">application/octet-stream</parameter>\n" +
                       "   <parameter name=\"transport.vfs.ActionAfterFailure\">DELETE</parameter>\n" +
                       "</proxy>";

        OMElement proxyOM = AXIOMUtil.stringToOM(proxy);

        //add the listener proxy to ESB server
        try {
            addProxyService(proxyOM);
        } catch (Exception e) {
            LOGGER.error("Error while updating the Synapse config", e);
        }
        LOGGER.info("Synapse config updated");
        Thread.sleep(30000);//Here we can't know whether the proxy polling happened or not, hence only way is to wait and see. Since poll interval is 1,
        // this waiting period should suffice. But it may include the time it take to deploy ther service as well.

        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean isSuccess = false;
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(log)) {
                isSuccess = true;
                break;
            }
        }

        Assert.assertTrue(
                isSuccess,
                "Secure password deployment failed, file did not received to the vfs proxy");
    }

    /**
     * Copy the given source file to the given destination
     *
     * @param sourceFile source file
     * @param destFile   destination file
     * @throws java.io.IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(sourceFile);
            fileOutputStream = new FileOutputStream(destFile);

            FileChannel source = fileInputStream.getChannel();
            FileChannel destination = fileOutputStream.getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}
