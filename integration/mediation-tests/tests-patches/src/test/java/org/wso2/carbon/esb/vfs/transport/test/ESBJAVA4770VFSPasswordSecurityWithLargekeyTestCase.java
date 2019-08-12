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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.servers.SftpServerRunner;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Integration test for https://wso2.org/jira/browse/ESBJAVA-4770
 * Checks if vfs can handle secured passwords with large key strengths(2048)
 * This sets up a sftp server for the test, hence it requires sudo to run
 *
 * This test will also cover the jira https://wso2.org/jira/browse/ESBJAVA-4768 as well,
 * in that, we need to enable file locking along with "sftpPathFromRoot=true" which is done in this test case.
 */
public class ESBJAVA4770VFSPasswordSecurityWithLargekeyTestCase extends ESBIntegrationTest {

    private static final Log LOGGER = LogFactory.getLog(ESBJAVA4770VFSPasswordSecurityWithLargekeyTestCase.class);

    private SftpServerRunner sftpServerRunner;
    private File sampleFileFolder;
    private File inputFolder;
    private File outputFolder;
    private ServerConfigurationManager serverConfigurationManager;
    private int FTPPort = 8005;
    private String inputFolderName = "in";
    private String outputFolderName = "out";
    private static final String USERNAME = "SFTPUser";
    private static final String PASSWORD = "SFTP321";

    @BeforeClass(alwaysRun = true)
    public void runFTPServer() throws Exception {

        String pathToFtpDir = getClass().getResource("/artifacts/ESB/synapseconfig/vfsTransport/").getPath();

        // Local folder of the FTP server root
        File FTPFolder = new File(pathToFtpDir + "securePasswordFTP");
        sampleFileFolder = new File(pathToFtpDir);

        // create FTP server root folder if not exists
        if (FTPFolder.exists()) {
            FileUtils.deleteDirectory(FTPFolder);
        }
        Assert.assertTrue(FTPFolder.mkdir(), "FTP root file folder not created");

        // create a directory under FTP server root
        inputFolder = new File(FTPFolder.getAbsolutePath() + File.separator + inputFolderName);
        // create a directory under FTP server root
        outputFolder = new File(FTPFolder.getAbsolutePath() + File.separator + outputFolderName);

        if (inputFolder.exists()) {
            FileUtils.deleteDirectory(inputFolder);
        }

        if (outputFolder.exists()) {
            FileUtils.deleteDirectory(outputFolder);
        }

        Assert.assertTrue(inputFolder.mkdir(), "FTP data /in folder not created");
        Assert.assertTrue(outputFolder.mkdir(), "FTP data /in folder not created");

        //create and start sftp server which has user name and pass SFTPUser:SFTP321
        sftpServerRunner = new SftpServerRunner(FTPPort, FTPFolder.getAbsolutePath(), USERNAME, PASSWORD);
        sftpServerRunner.start();

        super.init();
        File jksFile = new File(getClass().getResource("/artifacts/ESB/synapseconfig/vfsTransport"
                                                       + "/ESBJAVA4770/vfsKeystore.jks").getPath());

        File destinationJks = Paths.get(CarbonBaseUtils.getCarbonHome(),"repository"
                                                 ,"resources","security","vfsKeystore.jks").toFile();

        //copy keystore which contains key with keystrength 2048
        copyFile(jksFile, destinationJks);

        // replace the axis2.xml enabled vfs transfer and restart the ESB server gracefully.
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(
                new File(getClass().getResource("/artifacts/ESB/synapseconfig/"
                                                + "vfsTransport/ESBJAVA4770/axis2.xml").getPath()));
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void stopFTPServer() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            sftpServerRunner.stop();
            log.info("FTP Server stopped successfully.");
            serverConfigurationManager.restoreToLastConfiguration();
        }
    }

    @Test(groups = "wso2.esb", description = "VFS secure password test")
    public void securePasswordTest() throws XMLStreamException, IOException {

        //copy SOAP message  into the SFTP server
        String sentMessageFile = "test.xml";
        File sourceMessage = new File(sampleFileFolder + File.separator + sentMessageFile);
        File destinationMessage = new File(inputFolder + File.separator + sentMessageFile);
        copyFile(sourceMessage, destinationMessage);

        //Below is encrypted value of "SFTPUser:SFTP321" using vfsKeystore.jks which has a key with key strength 2048
        String encryptedPass = "qZnz8nFwQGqI1jp5DcjW8mUurphNc9Mj1DH8cGQBYB0p05geEDMQE3mNp3FTGhAhlohzvzuHdymETTEniprVua4PqPoeB1ZOXpCxE2Xy/auq+JSo77uPmPc9Uf3wgx5fhKqSghENwiCeWqAvbiLwyArwpmq4A5PVAuIzjADFwSkIpRxD9VnDlaDr2ovYVfbrwM7Z3DF4w4GJmyeXdswoCiYBZ+t+SJEU8tihzLsO0B3cbYXbzDEDNUVF6lWnokD01Ywp4VcI3FSHI1XwyKeZj1RAtP4YdhqEnUbSnlG3VsMeSgFpjUrnRomVY6/Pw2rq7s19RGgVO+X6JekON1mH2w==";
        String fileUri = "sftp://{wso2:vault-decrypt('" + encryptedPass + "')}@localhost:" + FTPPort + "/" + inputFolderName + "/?transport.vfs.AvoidPermissionCheck=true";
        String moveAfterProcess = "sftp://{wso2:vault-decrypt('" + encryptedPass + "')}@localhost:" + FTPPort +"/" + outputFolderName + "?transport.vfs.AvoidPermissionCheck=true";

        //create VFS transport listener proxy
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
                       "            <property name=\"File recieved for the proxy service - \"\n" +
                       "                      expression=\"fn:concat(' - File ',get-property('transport','FILE_NAME'),' received')\"/>\n" +
                       "         </log>\n" +
                       "         <drop/>\n" +
                       "      </inSequence>\n" +
                       "   </target>\n" +
                       "   <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                       "   <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                       "   <parameter name=\"transport.vfs.ClusterAwareness\">true</parameter>\n" +
                       "   <parameter name=\"transport.vfs.FileURI\">" + fileUri + "</parameter>\n" +
                       "   <parameter name=\"transport.vfs.MoveAfterProcess\">" + moveAfterProcess + "</parameter>\n" +
                       "   <parameter name=\"transport.vfs.FileNamePattern\">test.*\\.xml</parameter>\n" +
                       "   <parameter name=\"transport.vfs.Locking\">enable</parameter>\n" +
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
        // Here we can't know whether the proxy polling happened or not, hence only way is to wait and see. Since poll interval is 1,
        // this waiting period should suffice. But it may include the time it take to deploy ther service as well.
        //check whether file is moved to "out" folder
        Awaitility.await().atMost(60, TimeUnit.SECONDS).until(checkForOutputFile(outputFolder));
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

    private Callable<Boolean> checkForOutputFile(final File outputFolder) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                File[] files = outputFolder.listFiles();
                return files != null && files.length > 0;
            }
        };
    }
}
