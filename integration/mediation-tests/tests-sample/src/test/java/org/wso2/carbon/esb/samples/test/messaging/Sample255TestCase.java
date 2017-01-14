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
package org.wso2.carbon.esb.samples.test.messaging;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.ftpserver.FTPServerManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class Sample255TestCase extends ESBIntegrationTest {

//    private String homeDirectory = null;
//    private String ftpPath = null;
//    private FTPServerManager ftpServerManager = null;
//    private ServerConfigurationManager serverConfigurationManager = null;
//
//    private String inputFolderName = "in";
//    private String outputFolderName = "out";
//    private String username = "ftpuser";
//    private String password = "ftp123";
//    private File FTPFolder;
//    private File inputFolder;
//    private File outputFolder;
//    private int FTPPort = 8085;
//    private String feedURL = null;
//    private static final String GMAIL_USER_NAME = "test.automation.dummy";
//    private static final String GMAIL_PASSWORD = "automation.test";
//
//    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
//    @BeforeClass(alwaysRun = true)
//    public void setEnvironment() throws Exception {
//
//        super.init();
//
//        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB",
//                TestUserMode.SUPER_TENANT_ADMIN));
//
//        serverConfigurationManager.applyConfiguration(new File(
//                FrameworkPathUtil.getSystemResourceLocation() +
//                        "artifacts" + File.separator + "ESB" + File.separator
//                        + "synapseconfig" + File.separator + "vfsTransport" + File.separator
//                        + "mail" + File.separator + "axis2.xml"));
//
//        super.init();
//
//    }
//
//    @Test(groups = "wso2.esb", description = "VFS transport with FTP server location test " +
//            "and mail sender proxy")
//    public void testFTPwithVFS() throws Exception {
//
//        feedURL = "https://mail.google.com/mail/feed/atom";
//        int beforeMaiilCount = getMailCount(feedURL);
//
//        FTPFolder = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
//                + "artifacts" + File.separator + "ESB" + File.separator +
//                "synapseconfig" + File.separator + "vfsTransport" + File.separator + "FTP_Location");
//
//        //create FTP server root folder and subfolders
//        if (!FTPFolder.exists()) {
//            FTPFolder.mkdir();
//        }
//
//        inputFolder = new File(FTPFolder.getAbsolutePath() + File.separator + inputFolderName);
//        outputFolder = new File(FTPFolder.getAbsolutePath() + File.separator + outputFolderName);
//        if (inputFolder.exists()) {
//            inputFolder.delete();
//        }
//        if (outputFolder.exists()) {
//            outputFolder.delete();
//        }
//        inputFolder.mkdir();
//        outputFolder.mkdir();
//
//        ftpServerManager = new FTPServerManager(FTPPort, FTPFolder.getAbsolutePath(), username, password);
//        ftpServerManager.startFtpServer();
//
//        FileUtils.copyFile(new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
//                + "artifacts" + File.separator + "ESB" + File.separator +
//                "synapseconfig" + File.separator + "vfsTransport" + File.separator +"mail"
//                + File.separator + "test.xml"),
//                new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
//                + "artifacts" + File.separator + "ESB" + File.separator +
//                "synapseconfig" + File.separator + "vfsTransport" + File.separator + "FTP_Location"
//                + File.separator + "in" + File.separator + "test.xml"));
//
//        addVFSProxyForFTP();
//        Thread.sleep(40000);
//
//        Assert.assertEquals(getMailCount(feedURL), beforeMaiilCount + 1, "Mail count mismatch");
//
//    }
//
//    public void addVFSProxyForFTP () throws Exception {
//
//        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSProxyWithFTP\" transports=\"vfs\">\n" +
//                "<parameter name=\"transport.vfs.FileURI\">"
//                + "vfs:ftp://" + username + ":" + password + "@localhost:" + FTPPort +"/in/?vfs.passive=true"
//                +"</parameter>\n" +
//                "        <!--CHANGE-->\n" +
//                "        <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
//                "        <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
//                "        <parameter name=\"transport.PollInterval\">15</parameter>\n" +
//                "\n" +
//                "        <target>\n" +
//                "            <inSequence>\n" +
//                "                <header name=\"Action\" value=\"urn:getQuote\"/>\n" +
//                "            </inSequence>\n" +
//                "            <endpoint>\n" +
//                "                <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
//                "            </endpoint>\n" +
//                "            <outSequence>\n" +
//                "                <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
//                "                <send>\n" +
//                "                    <endpoint>\n" +
//                "                        <address uri=\"mailto:test.automation.dummy@gmail.com\"/>\n" +
//                "                        <!--CHANGE-->\n" +
//                "                    </endpoint>\n" +
//                "                </send>\n" +
//                "            </outSequence>\n" +
//                "        </target>\n" +
//                "        <publishWSDL uri=\"file:repository/samples/resources/proxy/sample_proxy_1.wsdl\"/>" +
//                "        </proxy>"));
//    }
//
//    private static OMElement getAtomFeedContent(String atomURL) throws IOException,
//            XMLStreamException {
//        StringBuilder sb;
//        InputStream inputStream = null;
//        URL url = new URL(atomURL);
//        try {
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            connection.setRequestMethod("GET");
//            String userPassword = GMAIL_USER_NAME + ":" + GMAIL_PASSWORD;
//            String encodedAuthorization = Base64Utils.encode(userPassword.getBytes());
//            connection.setRequestProperty("Authorization", "Basic " +
//                    encodedAuthorization);
//            connection.connect();
//
//            inputStream = connection.getInputStream();
//            sb = new StringBuilder();
//            String line;
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append("\n");
//            }
//        } finally {
//            assert inputStream != null;
//            inputStream.close();
//        }
//
//        return AXIOMUtil.stringToOM(sb.toString());
//
//    }
//
//    private int getMailCount(String feedURL) throws XMLStreamException, IOException {
//        OMElement mailFeed = getAtomFeedContent(feedURL);
//        Iterator itr = mailFeed.getChildrenWithName(new QName("fullcount"));
//        int count = 0;
//        if (itr.hasNext()) {
//            OMElement countOm = (OMElement) itr.next();
//            return Integer.parseInt(countOm.getText());
//        }
//        return count;
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void destroy() throws Exception {
//        super.cleanup();
//        ftpServerManager.stop();
//    }

}
