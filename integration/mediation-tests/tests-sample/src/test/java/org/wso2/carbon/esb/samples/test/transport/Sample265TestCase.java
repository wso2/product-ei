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

package org.wso2.carbon.esb.samples.test.transport;

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

import static org.testng.Assert.assertTrue;

public class Sample265TestCase extends ESBIntegrationTest {

        private ServerConfigurationManager serverConfigurationManager;
        private String pathToVfsDir;

        @BeforeClass(alwaysRun = true)
        public void init() throws Exception {
            super.init();
            pathToVfsDir = getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" +
                    File.separator + "synapseconfig" + File.separator +
                    "vfsTransport" + File.separator).getPath(); // change this to get samba path

            serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
            serverConfigurationManager.applyConfiguration(new File(pathToVfsDir + File.separator + "axis2.xml"));
            super.init();

            File outFolder = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator);
            File inFolder = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator);
            File originalFolder = new File(pathToVfsDir + "test" + File.separator + "original" + File.separator);
            File failureFolder = new File(pathToVfsDir + "test" + File.separator + "failure" + File.separator);
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

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : " +
            "transport.vfs.FileURI = " +
            "/home/someuser/somedir " +
            "transport.vfs.ContentType = text/plain, " +
            "transport.vfs.FileNamePattern = *", enabled = false)
    public void testVFSProxyFileURI_LinuxPath_SelectAll_FileNamePattern()
            throws Exception {

        addVFSProxy();

        File sourceFile = new File(pathToVfsDir + File.separator + "test.txt");
        File targetFile = new File(pathToVfsDir + "test" + File.separator + "in" + File.separator + "test.txt");
        File outfile = new File(pathToVfsDir + "test" + File.separator + "out" + File.separator + "out.txt");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);

            Assert.assertTrue(outfile.exists());
        } finally {
            deleteFile(targetFile);
            deleteFile(outfile);
            removeProxy("VFSProxy1");
        }
    }

        private void addVFSProxy()
                throws Exception {

            addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxy1\" transports=\"vfs\">\n" +
                    "                <parameter name=\"transport.vfs.FileURI\"> "
                             + pathToVfsDir + File.separator + "test" + File.separator + "in" + "</parameter> <!--CHANGE-->\n" +
                    "        <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                    "        <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                    "        <parameter name=\"transport.PollInterval\">15</parameter>\n" +
                    "        <parameter name=\"transport.vfs.MoveAfterProcess\">" +
                             pathToVfsDir + File.separator + "test" + File.separator + "original</parameter> <!--CHANGE-->\n" +
                    "        <parameter name=\"transport.vfs.MoveAfterFailure\">" + pathToVfsDir + File.separator + "test" + File.separator +
                    "           original</parameter> <!--CHANGE-->\n" +
                    "        <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                    "        <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>\n" +
                    " \n" +
                    "        <target>\n" +
                    "            <endpoint>\n" +
                    "                <address format=\"soap12\" uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                    "            </endpoint>\n" +
                    "            <outSequence>\n" +
                    "                <property name=\"transport.vfs.ReplyFileName\"\n" +
                    "                          expression=\"fn:concat(fn:substring-after(get-property('MessageID'), 'urn:uuid:'), '.xml')\" scope=\"transport\"/>\n" +
                    "                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                    "                <send>\n" +
                    "                    <endpoint>\n" +
                    "                        <address uri=\"vfs:"+ pathToVfsDir + File.separator + "test" + File.separator + "out/> <!--CHANGE-->\n" +
                    "                    </endpoint>\n" +
                    "                </send>\n" +
                    "            </outSequence>\n" +
                    "        </target>\n" +
                    "        <publishWSDL uri=\"file:repository/samples/resources/proxy/sample_proxy_1.wsdl\"/>" +
                    "        </proxy>"));
        }

        private void removeProxy(String proxyName) throws Exception {
            deleteProxyService(proxyName);
        }

        private boolean deleteFile(File file) throws IOException {
            return file.exists() && file.delete();
        }
}

