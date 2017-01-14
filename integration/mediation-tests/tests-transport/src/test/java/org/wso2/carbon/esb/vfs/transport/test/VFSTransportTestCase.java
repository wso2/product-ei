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

package org.wso2.carbon.esb.vfs.transport.test;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertTrue;

/**
 * This test class in skipped when user mode is tenant because of this release not support vfs transport for tenants
 */
public class VFSTransportTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private String pathToVfsDir;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        pathToVfsDir = getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" +
                                              File.separator + "synapseconfig" + File.separator +
                                              "vfsTransport" + File.separator).getPath();

        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(pathToVfsDir + File.separator + "axis2.xml"));
        super.init();

        File rootFolder = new File(pathToVfsDir + "test" + File.separator);
        File outFolder = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator);
        File inFolder = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator);
        File originalFolder = new File(pathToVfsDir + "test" + File.separator + "original" + File.separator);
        File failureFolder = new File(pathToVfsDir + "test" + File.separator + "failure" + File.separator);

        FileUtils.deleteDirectory(rootFolder);
        assertTrue(rootFolder.mkdirs(), "file folder not created");
        assertTrue(outFolder.mkdirs(), "file folder not created");
        assertTrue(inFolder.mkdirs(), "file folder not created");
        assertTrue(originalFolder.mkdirs(), "file folder not created");
        assertTrue(failureFolder.mkdirs(), "file folder not created");
        assertTrue(outFolder.exists(), "File folder doesn't exists");
        assertTrue(inFolder.exists(), "File folder doesn't exists");
        assertTrue(originalFolder.exists(), "File folder doesn't exists");
        assertTrue(failureFolder.exists(), "File folder doesn't exists");
    }

    @AfterClass(alwaysRun = true)
    public void restoreServerConfiguration() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE }) @Test(groups = {
            "wso2.esb" }, description = "Writing to a file the content of a xml with content in text element") public void testVFSProxyPlainXMLWriter()
            throws Exception {

        addVFSProxyWriteFile();

        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out_reply.xml");

        String request = " <ns:text xmlns:ns=\"http://ws.apache.org/commons/ns/payload\">\n"
                + "         <test>request_value</test>\n" + "      </ns:text>";
        sendRequest(getProxyServiceURLHttp("salesforce_DAMAS_writeFile"), request, "text/xml");

        try {

            //Assert.assertTrue(!outfile.exists());
            Thread.sleep(1000);
            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("request_value"), "Sent request body not found");
        } finally {
            deleteFile(outfile);
            removeProxy("salesforce_DAMAS_writeFile");
        }
    }

    protected void sendRequest(String addUrl, String request, String contentType) throws IOException {
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setReadTimeout(1000);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", contentType + ";charset=" + charset);
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(request.getBytes(charset));
        } finally {
            if (output != null) {
                output.close();
            }
        }

        InputStream response = null;
        try {
            response = connection.getInputStream();
        } catch (Exception e) {
        }
    }

    private void addVFSProxyWriteFile() throws Exception {

        addProxyService(AXIOMUtil.stringToOM(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n"
                        + "       name=\"salesforce_DAMAS_writeFile\"\n" + "       transports=\"http\"\n"
                        + "       statistics=\"disable\"\n" + "       trace=\"enable\"\n"
                        + "       startOnLoad=\"true\">\n" + "   <target>\n" + "      <inSequence>\n"
                        + "         <property name=\"OUT_ONLY\" value=\"true\" scope=\"default\" type=\"STRING\"/>\n"
                        + "         <property name=\"transport.vfs.ReplyFileName\"\n"
                        + "                   value=\"out_reply.xml\"\n" + "                   scope=\"transport\"\n"
                        + "                   type=\"STRING\"/>\n"
                        + "                           <log level=\"full\"/>\n" + "         <send>\n"
                        + "            <endpoint>\n" + "               <address uri=\"vfs:file://" + pathToVfsDir
                        + "test" + File.separator + "out\"/>\n" + "<timeout>\n"
                        + "               <duration>10</duration>\n"
                        + "               <responseAction>discard</responseAction>\n" + "            </timeout>"
                        + "            </endpoint>\n" + "         </send>\n" + "      </inSequence>\n"
                        + "      <outSequence/>\n" + "      <faultSequence/>\n" + "   </target>\n"
                        + "   <parameter name=\"transport.vfs.locking\">disable</parameter>\n"
                        + "   <parameter name=\"redeliveryPolicy.maximumRedeliveries\">0</parameter>\n"
                        + "   <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n"
                        + "   <parameter name=\"redeliveryPolicy.redeliveryDelay\">1</parameter>\n"
                        + "   <description/>\n" + "</proxy>"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml")
    public void testVFSProxyFileURI_LinuxPath_ContentType_XML()
            throws Exception {

        addVFSProxy1();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("WSO2 Company"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy1");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir" +
                                               " transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt")
    public void testVFSProxyFileURI_LinuxPath_ContentType_Plain()
            throws Exception {

        addVFSProxy2();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");

        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);
            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy2");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = " +
                                               "/home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = *")
    public void testVFSProxyFileURI_LinuxPath_SelectAll_FileNamePattern()
            throws Exception {

        addVFSProxy3();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy3");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = nothing")
    public void testVFSProxyFileURI_LinuxPath_No_FileNamePattern()
            throws Exception {

        addVFSProxy4();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);
            Assert.assertTrue(!outfile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy4");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, transport.PollInterval=1")
    public void testVFSProxyPollInterval_1()
            throws Exception {

        addVFSProxy5();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy5");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, transport.PollInterval=30")
    public void testVFSProxyPollInterval_30()
            throws Exception {

        addVFSProxy6();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");

        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(1000);

            Assert.assertTrue(!outfile.exists());

            Thread.sleep(31000);
            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy6");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport :" +
                                               " transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, " +
                                               "transport.PollInterval=1, transport.vfs.ActionAfterProcess=MOVE")
    public void testVFSProxyActionAfterProcess_Move()
            throws Exception {

        addVFSProxy7();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "original" + File.separator + "test.txt");

        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));

            Assert.assertTrue(originalFile.exists());
            String vfsOriginal = FileUtils.readFileToString(originalFile);
            Assert.assertTrue(vfsOriginal.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy7");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, transport.PollInterval=1, " +
                                               "transport.vfs.ActionAfterProcess=DELETE")
    public void testVFSProxyActionAfterProcess_DELETE()
            throws Exception {

        addVFSProxy8();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "original" + File.separator + "test.txt");

        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));

            Assert.assertTrue(!originalFile.exists());
            Assert.assertTrue(!targetFile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy8");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, " +
                                               "transport.PollInterval=1," +
                                               " transport.vfs.ReplyFileName = out.txt ")
    public void testVFSProxyReplyFileName_Normal()
            throws Exception {

        addVFSProxy9();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy9");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, " +
                                               "transport.PollInterval=1, " +
                                               "transport.vfs.ReplyFileName = out123@wso2_text.txt ")
    public void testVFSProxyReplyFileName_SpecialChars()
            throws Exception {

        addVFSProxy10();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out123@wso2_text.txt");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = /home/someuser/somedir " +
                                               "transport.vfs.ContentType = text/plain, " +
                                               "transport.vfs.FileNamePattern = - *\\.txt, " +
                                               "transport.PollInterval=1, " +
                                               "transport.vfs.ReplyFileName = not specified ")
    public void testVFSProxyReplyFileName_NotSpecified()
            throws Exception {

        addVFSProxy11();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "response.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy11");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.ActionAfterFailure=MOVE")
    public void testVFSProxyActionAfterFailure_MOVE()
            throws Exception {

        addVFSProxy12();

        File sourceFile = new File(pathToVfsDir + File.separator + "fail.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "fail.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "failure" + File.separator + "fail.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(!outfile.exists());

            Assert.assertTrue(originalFile.exists());
            String vfsOut = FileUtils.readFileToString(originalFile);
            Assert.assertTrue(vfsOut.contains("andun@wso2.com"));
            Assert.assertFalse(new File(pathToVfsDir + "test" + File.separator + "in" + File.separator +
                                        "fail.xml.lock").exists(), "lock file exists even after moving the failed file");
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy12");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.ActionAfterFailure=DELETE")
    public void testVFSProxyActionAfterFailure_DELETE()
            throws Exception {

        addVFSProxy13();

        File sourceFile = new File(pathToVfsDir + File.separator + "fail.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "fail.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
            Assert.assertTrue(!outfile.exists());

            File originalFile = new File(pathToVfsDir + "test" + File.separator + "failure" + File.separator + "fail.xml");

            Assert.assertTrue(!originalFile.exists());
            Assert.assertFalse(new File(pathToVfsDir + "test" + File.separator + "in" + File.separator +
                                        "fail.xml.lock").exists(), "lock file exists even after moving the failed file");
        } finally {
            deleteFile(targetFile);
            removeProxy("VFSProxy13");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport :" +
                                               " transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.ActionAfterFailure=NotSpecified")
    public void testVFSProxyActionAfterFailure_NotSpecified()
            throws Exception {

        addVFSProxy14();

        File sourceFile = new File(pathToVfsDir + File.separator + "fail.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "fail.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "failure" + File.separator + "fail.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(!outfile.exists());
            Assert.assertTrue(!originalFile.exists());
            Assert.assertFalse(new File(pathToVfsDir + "test" + File.separator + "in" + File.separator +
                                        "fail.xml.lock").exists(), "lock file exists even after moving the failed file");
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy14");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Invalid, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml")
    public void testVFSProxyFileURI_Invalid()
            throws Exception {

        addVFSProxy15();

        Thread.sleep(2000);

        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        Assert.assertTrue(!outfile.exists());

        removeProxy("VFSProxy15");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = Invalid, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.FileURI = Invalid")
    public void testVFSProxyContentType_Invalid()
            throws Exception {

        addVFSProxy16();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("WSO2 Company"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy16");
        }
    }

    //https://wso2.org/jira/browse/ESBJAVA-2273
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = Not Specified, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.FileURI = Invalid", enabled = false)
    public void testVFSProxyContentType_NotSpecified()
            throws Exception {

        addVFSProxy17();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(!outfile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy17");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.PollInterval = Non Integer")
    public void testVFSProxyPollInterval_NonInteger()
            throws Exception {

        addVFSProxy18();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);
            //The poll interval will be set to 300s here,

            Assert.assertTrue(!outfile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy18");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.ActionAfterProcess = Invalid")
    public void testVFSProxyActionAfterProcess_Invalid()
            throws Exception {

        addVFSProxy19();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "original" + File.separator + "test.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("WSO2 Company"));

            Assert.assertTrue(!originalFile.exists());
            Assert.assertTrue(!targetFile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy19");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport :" +
                                               " transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.ActionAfterFailure = Invalid")
    public void testVFSProxyActionAfterFailure_Invalid()
            throws Exception {

        addVFSProxy20();

        File sourceFile = new File(pathToVfsDir + File.separator + "fail.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "fail.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "failure" + File.separator + "fail.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(!outfile.exists());
            Assert.assertTrue(!originalFile.exists());
            Assert.assertTrue(!targetFile.exists());
            Assert.assertFalse(new File(pathToVfsDir + "test" + File.separator + "in" + File.separator +
                                        "fail.xml.lock").exists(), "lock file exists even after moving the failed file");
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy20");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path," +
                                               " transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.MoveAfterProcess = processed")
    public void testVFSProxyMoveAfterProcessInvalidFile() throws Exception {

        addVFSProxy21();

        File sourceFile = new File(pathToVfsDir + File.separator + "fail.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "fail.xml");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "processed" + File.separator + "test.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertFalse(outfile.exists(), "Out put file found");
            Assert.assertFalse(originalFile.exists(), "Input file moved even if file processing is failed");
            Assert.assertFalse(targetFile.exists(), "Input file found after reading the file");
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
            removeProxy("VFSProxy21");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path," +
                                               " transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.MoveAfterProcess = processed")
    public void testVFSProxyMoveAfterProcess() throws Exception {

        addVFSProxy21();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File originalFileAfterProcessed = new File(pathToVfsDir + "test" + File.separator + "processed" + File.separator + "test.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists(), "out put file not found");
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("WSO2 Company"), "Invalid Response message. >" + vfsOut);
            //input file should be moved to processed directory after processing the input file
            Assert.assertTrue(originalFileAfterProcessed.exists(), "Input file is not moved after processing the file");
            Assert.assertFalse(targetFile.exists(), "Input file is exist after processing the input file");
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFileAfterProcessed);
            removeProxy("VFSProxy21");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml " +
                                               "transport.vfs.MoveAfterFailure = Invalid")
    public void testVFSProxyMoveAfterFailure()
            throws Exception {

        addVFSProxy22();

        File sourceFile = new File(pathToVfsDir + "fail.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "fail.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        File originalFile = new File(pathToVfsDir + "test" + File.separator + "invalid" + File.separator + "fail.xml");
        /*File lockFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator +
                                 "fail.xml.lock");*/
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertFalse(outfile.exists(), "Out put file found");
            Assert.assertTrue(originalFile.exists(), "Input file not moved even if failure happens while building message");
            Assert.assertFalse(targetFile.exists(), "input file not found even if it is invalid file");
            //reason to bellow assert- https://wso2.org/jira/browse/ESBJAVA-1838
//            Assert.assertTrue(lockFile.exists(), "lock file doesn't exists"); commented since  it is fixed now
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            deleteFile(originalFile);
//            deleteFile(lockFile);
            removeProxy("VFSProxy22");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
                                               "transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml, " +
                                               "transport.vfs.ReplyFileURI  = Invalid")
    public void testVFSProxyReplyFileURI_Invalid()
            throws Exception {

        addVFSProxy23();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "invalid" + File.separator + "out.xml");
        deleteFile(outfile); //delete outfile dir if exists
        FileUtils.cleanDirectory(new File(pathToVfsDir + "test" + File.separator + "in"));
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(4000);

            Assert.assertTrue(outfile.exists());
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("WSO2 Company"));
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy23");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport :" +
                                               " transport.vfs.FileURI = Linux Path, " +
                                               "transport.vfs.ContentType = text/xml, " +
                                               "transport.vfs.FileNamePattern = - *\\.xml, " +
                                               "transport.vfs.ReplyFileName  = Invalid")
    public void testVFSProxyReplyFileName_Invalid()
            throws Exception {

        addVFSProxy24();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.xml");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.xml");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);
            Assert.assertTrue(!outfile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy24");
        }
    }

    private void addVFSProxy1()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy1\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy2()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy2\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy3()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy3\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.*</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy4()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy4\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\"></parameter>" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy5()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy5\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy6()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy6\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">30</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy7()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy7\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + pathToVfsDir + "test" + File.separator + "original" + File.separator + "</parameter>" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy8()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy8\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterProcess\">DELETE</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy9()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy9\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property name=\"transport.vfs.ReplyFileName\" value=\"out.txt\" scope=\"transport\"/>" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy10()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy10\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property name=\"transport.vfs.ReplyFileName\" value=\"out123@wso2_text.txt\" scope=\"transport\"/>" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy11()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy11\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*.txt</parameter>" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <inSequence>\n" +
                                             "                           <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                           <log level=\"full\"/>\n" +
                                             "                           <send>\n" +
                                             "                               <endpoint name=\"FileEpr\">\n" +
                                             "                                   <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "\"/>\n" +
                                             "                               </endpoint>\n" +
                                             "                           </send>" +
                                             "                        </inSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy12()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy12\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + pathToVfsDir + "test" + File.separator + "failure" + File.separator + "</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy13()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy13\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterFailure\">DELETE</parameter>" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy14()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy14\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy15()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy15\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "invalid" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy16()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy16\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">invalid/invalid</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <log level=\"full\"/>" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy17()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy17\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy18()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy18\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1.1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy19()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy19\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterProcess\">MOVEDD</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + pathToVfsDir + "test" + File.separator + "original" + File.separator + "</parameter>" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                        <log level=\"full\"/>" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy20()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy20\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + pathToVfsDir + "test" + File.separator + "failure" + File.separator + "</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterFailure\">MOVEDD</parameter>" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy21()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy21\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + pathToVfsDir + "test" + File.separator + "processed" + File.separator + "</parameter>" +
                                             "                <parameter name=\"transport.vfs.CreateFolder\">true</parameter>" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                        <log level=\"full\"/>" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy22()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy22\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + pathToVfsDir + "test" + File.separator + "invalid" + File.separator + "</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>" +
                                             "                <parameter name=\"transport.vfs.CreateFolder\">true</parameter>" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void addVFSProxy23()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy23\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                 <log level=\"full\"/>" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:file://" + pathToVfsDir + "test" + File.separator + "invalid" + File.separator + "out.xml\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));

        System.out.println("\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n\" +\n" +
                           "                                             \"<proxy xmlns=\\\"http://ws.apache.org/ns/synapse\\\" name=\\\"VFSProxy23\\\" transports=\\\"vfs\\\">\\n\" +\n" +
                           "                                             \"                <parameter name=\\\"transport.vfs.FileURI\\\">file://\" + getClass().getResource(File.separator + \"artifacts\" + File.separator + \"ESB\" + File.separator + \"synapseconfig\" + File.separator + \"vfsTransport\" + File.separator).getPath() + \"test\" + File.separator + \"in\" + File.separator + \"</parameter> <!--CHANGE-->\\n\" +\n" +
                           "                                             \"                <parameter name=\\\"transport.vfs.ContentType\\\">text/xml</parameter>\\n\" +\n" +
                           "                                             \"                <parameter name=\\\"transport.vfs.FileNamePattern\\\">.*\\\\.xml</parameter>\\n\" +\n" +
                           "                                             \"                <parameter name=\\\"transport.PollInterval\\\">1</parameter>\\n\" +\n" +
                           "                                             \"                <target>\\n\" +\n" +
                           "                                             \"                        <endpoint>\\n\" +\n" +
                           "                                             \"                                <address format=\\\"soap12\\\" uri=\\\"http://localhost:9000/services/SimpleStockQuoteService\\\"/>\\n\" +\n" +
                           "                                             \"                        </endpoint>\\n\" +\n" +
                           "                                             \"                        <outSequence>\\n\" +\n" +
                           "                                             \"                                <property action=\\\"set\\\" name=\\\"OUT_ONLY\\\" value=\\\"true\\\"/>\\n\" +\n" +
                           "                                             \"                                <send>\\n\" +\n" +
                           "                                             \"                                        <endpoint>\\n\" +\n" +
                           "                                             \"                                                <address uri=\\\"vfs:file://\" + getClass().getResource(File.separator + \"artifacts\" + File.separator + \"ESB\" + File.separator + \"synapseconfig\" + File.separator + \"vfsTransport\" + File.separator).getPath() + \"test\" + File.separator + \"invalid\" + File.separator + \"out.xml\\\"/> <!--CHANGE-->\\n\" +\n" +
                           "                                             \"                                        </endpoint>\\n\" +\n" +
                           "                                             \"                                </send>\\n\" +\n" +
                           "                                             \"                        </outSequence>\\n\" +\n" +
                           "                                             \"                </target>\\n\" +\n" +
                           "                                             \"        </proxy>\")");
    }

    private void addVFSProxy24()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy24\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + pathToVfsDir + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <target>\n" +
                                             "                        <endpoint>\n" +
                                             "                                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "                        </endpoint>\n" +
                                             "                        <outSequence>\n" +
                                             "                                <property name=\"transport.vfs.ReplyFileName\" value=\"out.xml\" scope=\"transport\"/>" +
                                             "                                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                                <send>\n" +
                                             "                                        <endpoint>\n" +
                                             "                                                <address uri=\"vfs:ftpd://" + pathToVfsDir + "test" + File.separator + "out" + File.separator + "\"/> <!--CHANGE-->\n" +
                                             "                                        </endpoint>\n" +
                                             "                                </send>\n" +
                                             "                        </outSequence>\n" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }

    private void removeProxy(String proxyName) throws Exception {
        deleteProxyService(proxyName);
    }

    private boolean deleteFile(File file) throws IOException {
        return file.exists() && file.delete();
    }
}

