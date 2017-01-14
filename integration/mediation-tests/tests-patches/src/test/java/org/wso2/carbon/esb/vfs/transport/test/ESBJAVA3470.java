package org.wso2.carbon.esb.vfs.transport.test;

/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.IOUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPublicKey;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.proxyadmin.stub.ProxyServiceAdminProxyAdminException;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


/**
 * Test case for https://wso2.org/jira/browse/ESBJAVA-3470
 * VFS SFTP using passphrase protected keys
 */
public class ESBJAVA3470 extends ESBIntegrationTest {
    //Server credentials
    //DO NOT CHANGE
    private static final String SFTP_USER_NAME = "sftpuser";
    private static final String IDENTITY_PASSPHRASE = "wso2test";
    private static final String INPUT_FOLDER_NAME = "in";
    private static final String OUTPUT_FOLDER_NAME = "out";
    private static final String MOVE_FOLDER_NAME = "original";
    private static final String STOCK_QUOTE = "http://localhost:9000/services/SimpleStockQuoteService";

    private SshServer sshd;
    private File inputFolder;
    private File outputFolder;
    private File originalFolder;
    private File SFTPFolder;
    private String carbonHome;

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {

        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator + "axis2.xml").getPath()));
        super.init();

        carbonHome = System.getProperty(ServerConstants.CARBON_HOME);

        setupSftpFolders(carbonHome);
        setupSftpServer();
        Thread.sleep(15000);
    }

    @Test(groups = "wso2.esb", description = "VFS absolute path test for sftp")
    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.ALL })
    public void test() throws XMLStreamException, ProxyServiceAdminProxyAdminException, IOException, InterruptedException {

        String baseDir;
        ClassLoader classLoader = getClass().getClassLoader();
        String identityFile = classLoader.getResource("sftp/id_rsa").getFile();
        String sentMessageFile = "getQuote.xml";
        File sourceMessage = new File(classLoader.getResource("sftp/" + sentMessageFile).getFile());
        File destinationMessage = new File(inputFolder + File.separator + sentMessageFile);
        copyFile(sourceMessage, destinationMessage);

        //This is required to handle SFTP server root differences
        if(carbonHome.indexOf("/target") > 0){
            baseDir = (carbonHome.split("/target"))[1];
        } else {
            baseDir = carbonHome;
        }

        String proxy =  "<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                "       name=\"SFTPTestCaseProxy\"\n" +
                "       transports=\"vfs\"\n" +
                "       statistics=\"disable\"\n" +
                "       trace=\"disable\"\n" +
                "       startOnLoad=\"true\">\n" +
                "   <target>\n" +
                "      <inSequence>\n" +
                "           <send>\n" +
                "               <endpoint>\n" +
                "                   <address uri=\"" + STOCK_QUOTE + "\"/>\n" +
                "               </endpoint>\n" +
                "           </send>" +
                "      </inSequence>\n" +
                "      <outSequence/>\n" +
                "   </target>\n" +
                "   <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                "   <parameter name=\"transport.PollInterval\">5</parameter>\n" +
                "   <parameter name=\"transport.vfs.MoveAfterProcess\">vfs:sftp://" + SFTP_USER_NAME +
                "@localhost:9009"+baseDir +"/out/</parameter>\n" +
                "   <parameter name=\"transport.vfs.FileURI\">vfs:sftp://" + SFTP_USER_NAME + "@localhost:9009"+baseDir +"/in/</parameter>\n" +
                "   <parameter name=\"transport.vfs.MoveAfterFailure\">vfs:sftp://" + SFTP_USER_NAME + "@localhost:9009"+baseDir +"/original/</parameter>\n" +
                "   <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                "   <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                "   <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>\n" +
                "   <parameter name=\"transport.vfs.SFTPIdentityPassPhrase\">" + IDENTITY_PASSPHRASE + "</parameter>\n" +
                "   <parameter name=\"transport.vfs.SFTPIdentities\">" + identityFile + "</parameter>\n" +
                "   <description/>\n" +
                "</proxy>\n" +
                "                                ";
        OMElement proxyOM = AXIOMUtil.stringToOM(proxy);

        //create VFS transport listener proxy
        try {
            addProxyService(proxyOM);
        } catch (Exception e) {
            log.error("Error while updating the Synapse config", e);
        }
        log.info("Synapse config updated");
        Thread.sleep(30000);

        //check whether the added message was moved to the original folder
        final File[] files = outputFolder.listFiles();
        if (files != null) {
            Assert.assertEquals(files.length > 0, true);
        }
    }

    @AfterClass(alwaysRun = true)
    public void stopSFTPServer() throws Exception {
        //sshd.stop();
        log.info("SFTP Server stopped successfully");
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }

    /**
     * Creates the required directories
     */
    private void setupSftpFolders(String baseDir) {
        //local folder of the SFTP server root
        SFTPFolder = new File(baseDir);

        //create FTP server root folder and sub-folders
        if (!SFTPFolder.exists()) {
            SFTPFolder.mkdir();
        }

        inputFolder = new File(SFTPFolder.getAbsolutePath() + File.separator + INPUT_FOLDER_NAME);
        outputFolder = new File(SFTPFolder.getAbsolutePath() + File.separator + OUTPUT_FOLDER_NAME);
        originalFolder = new File(SFTPFolder.getAbsolutePath() + File.separator + MOVE_FOLDER_NAME);

        if (inputFolder.exists()) {
            inputFolder.delete();
        }
        if (outputFolder.exists()) {
            outputFolder.delete();
        }
        if (originalFolder.exists()) {
            originalFolder.delete();
        }

        log.info("Creating inputFolder " + inputFolder.getAbsolutePath());
        inputFolder.mkdir();
        log.info("Creating outputFolder " + outputFolder.getAbsolutePath());
        outputFolder.mkdir();
        log.info("Creating originalFolder " + originalFolder.getAbsolutePath());
        originalFolder.mkdir();
    }

    /**
     * Starts a SFTP server on port 22
     */
    private void setupSftpServer() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(9009);
        //sshd.setKeyPairProvider(new FileKeyPairProvider(new String[]{"/home/ravi/WORK/SUPPORT/JIRA/SKYTVNZDEV-26/SftpTest/dist/hostkey.ser"}));
        ClassLoader classLoader = getClass().getClassLoader();
        log.info("Using identity file: " + classLoader.getResource("sftp/id_rsa.pub").getFile());
        File file = new File(classLoader.getResource("sftp/id_rsa.pub").getFile());
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(file.getAbsolutePath()));
        System.out.println(file.getAbsolutePath());

        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
        userAuthFactories.add(new UserAuthPublicKey.Factory());
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {
            public boolean authenticate(String username, PublicKey key, ServerSession session) {
                if ("sftpuser".equals(username)) {
                    return true;
                }

                return false;
            }
        });

        sshd.setCommandFactory(new ScpCommandFactory());

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
        namedFactoryList.add(new SftpSubsystem.Factory());
        sshd.setSubsystemFactories(namedFactoryList);

        try {
            sshd.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy the given source file to the given destination
     *
     * @param sourceFile
     *                 source file
     * @param destFile
     *                 destination file
     * @throws IOException
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

    protected void addProxyService(OMElement proxyConfig) throws Exception {
        String proxyName = proxyConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isProxyServiceExist(context.getContextUrls().getBackEndUrl(), sessionCookie, proxyName)) {
            esbUtils.deleteProxyService(context.getContextUrls().getBackEndUrl(), sessionCookie, proxyName);
        }
        esbUtils.addProxyService(context.getContextUrls().getBackEndUrl(), sessionCookie, setEndpoints(proxyConfig));
    }
}