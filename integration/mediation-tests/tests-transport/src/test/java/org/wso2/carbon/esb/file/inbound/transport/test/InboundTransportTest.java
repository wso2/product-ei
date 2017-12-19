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
import org.wso2.esb.integration.common.utils.Utils;

public class InboundTransportTest extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;
    private File inboundFileListeningFolder;
    private String pathToFtpDir;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        pathToFtpDir = getESBResourceLocation() + File.separator + "synapseconfig"
                        + File.separator + "vfsTransport" + File.separator;

        inboundFileListeningFolder = new File(pathToFtpDir + File.separator + "inboundFileListeningFolder");

        // create inboundFileListeningFolder if not exists
        if (inboundFileListeningFolder.exists()) {
            FileUtils.deleteDirectory(inboundFileListeningFolder);
        }
        Assert.assertTrue(inboundFileListeningFolder.mkdir(), "inboundFileListeningFolder not created");

        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.esb",
          description = "Inbound endpoint Reading file with Contect type XML Test Case")
    public void testInboundEnpointReadFile_ContentType_XML() throws Exception {
        logViewerClient.clearLogs();

        File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
        File targetFolder = new File(inboundFileListeningFolder + File.separator + "ContentType");
        File targetFile = new File(targetFolder + File.separator + "test.xml");

        FileUtils.copyFile(sourceFile, targetFile);
        addInboundEndpoint(addEndpoint1(targetFolder.getAbsolutePath()));

        boolean isFileRead = Utils.checkForLog(logViewerClient, "<m0:symbol>WSO2</m0:symbol>", 10);
        Assert.assertTrue(isFileRead, "The XML file is not getting read");
    }

    @Test(groups = "wso2.esb",
          dependsOnMethods = "testInboundEnpointReadFile_ContentType_XML",
          description = "Inbound endpoint Delete file after reading Test Case")
    public void testInboundEnpointDeleteFileAfterProcess() throws Exception {

        File sourceFile = new File(
                inboundFileListeningFolder + File.separator + "ContentType" + File.separator + "test.xml");
        Assert.assertFalse(sourceFile.exists(), "The file is not deleted after the read");
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          dependsOnMethods = "testInboundEnpointDeleteFileAfterProcess",
          description = "Inbound Endpoint invalid interval Test case")
    public void testInboundEndpointPollInterval_NonInteger() throws Exception {
        logViewerClient.clearLogs();
        addInboundEndpoint(addEndpoint3());

        boolean errorMessageFound = Utils.checkForLog(logViewerClient,
                "Invalid numeric value for interval", 10);
        Assert.assertTrue(errorMessageFound, "The Error message not found in the log");
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          dependsOnMethods = "testInboundEndpointPollInterval_NonInteger",
          description = "Inbound Endpoint invalid File URI Test case")
    public void testInboundEndpointInvalidFileUri() throws Exception {


        File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
        File targetFolder = new File(inboundFileListeningFolder + File.separator + "uri");
        File targetFile = new File(targetFolder + File.separator + "test.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            addInboundEndpoint(addEndpoint4());
            Thread.sleep(2000);

            Assert.assertTrue(targetFile.exists(), "Invalid file processed");
        } finally {
            deleteFile(targetFile);
            deleteFile(targetFolder);
        }
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          dependsOnMethods = "testInboundEndpointInvalidFileUri",
          description = "Inbound Endpoint File name with special chars URI Test case")
    public void testInboundEndpointFileName_SpecialChars() throws Exception {

        boolean fileProcessed = false;

        File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
        File targetFolder = new File(inboundFileListeningFolder + File.separator + "spcChar");
        File targetFile = new File(targetFolder + File.separator + "test123@wso2_xml.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            addInboundEndpoint(addEndpoint5());
            for (int i = 0; i < 10; i++) {
                if (!targetFile.exists()) {
                    fileProcessed = true;
                    break;
                }
                Thread.sleep(1000);
            }

            Assert.assertTrue(fileProcessed, "File not processed");
        } finally {
            deleteFile(targetFile);
            deleteFile(targetFolder);
        }
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          dependsOnMethods = "testInboundEndpointFileName_SpecialChars",
          description = "Inbound Endpoint Content type invalid Test case")
    public void testInboundEndpointContentTypeInvalid() throws Exception {
        logViewerClient.clearLogs();

        File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
        File targetFolder = new File(inboundFileListeningFolder + File.separator + "in");
        File targetFile = new File(targetFolder + File.separator + "invalidContentType.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            addInboundEndpoint(addEndpoint6());
            boolean isFileRead = Utils.checkForLog(logViewerClient, "<m0:symbol>WSO2</m0:symbol>", 10);
            Assert.assertTrue(isFileRead, "The XML file is not getting read");

            Assert.assertTrue(!targetFile.exists(), "file not deleted after processed");
        } finally {
            deleteFile(targetFile);
        }
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          dependsOnMethods = "testInboundEndpointContentTypeInvalid",
          description = "Inbound Endpoint Content type not specified Test case")
    public void testInboundEndpointContentTypeNotSpecified() throws Exception {

        boolean fileProcessed = false;
        File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
        File targetFolder = new File(inboundFileListeningFolder + File.separator + "in");
        File targetFile = new File(targetFolder + File.separator + "in.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            addInboundEndpoint(addEndpoint7());
            for (int i = 0; i < 10; i++) {
                if (!targetFile.exists()) {
                    fileProcessed = true;
                    break;
                }
                Thread.sleep(1000);
            }

            Assert.assertTrue(fileProcessed, "File not processed");
        } finally {
            deleteFile(targetFile);
        }
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          dependsOnMethods = "testInboundEndpointContentTypeNotSpecified",
          description = "Inbound Endpoint move after process Test case")
    public void testInboundEndpointMoveAfterProcess() throws Exception {

        boolean fileProcessed = false;
        File sourceFile = new File(pathToFtpDir + File.separator + "test.xml");
        File targetFolder = new File(inboundFileListeningFolder + File.separator + "move");
        File targetFile = new File(targetFolder + File.separator + "test.xml");
        File processedFolder = new File(inboundFileListeningFolder + File.separator + "processed");
        if (processedFolder.exists()) {
            processedFolder.delete();
        } else {
            processedFolder.mkdir();
        }

        File processedFile = new File(processedFolder + File.separator + "test.xml");

        try {
            FileUtils.copyFile(sourceFile, targetFile);
            addInboundEndpoint(addEndpoint8());
            for (int i = 0; i < 10; i++) {
                if (!targetFile.exists()) {
                    fileProcessed = true;
                    break;
                }
                Thread.sleep(1000);
            }
            // input file should be moved to processed directory after
            // processing the input file
            Assert.assertTrue(processedFile.exists(), "Input file is not moved after processing the file");
            Assert.assertTrue(fileProcessed, "Input file is exist after processing the input file");
        } finally {
            deleteFile(targetFolder);
            deleteFile(processedFile);
            deleteFile(processedFolder);
        }
    }

    private OMElement addEndpoint1(String fileUri) throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile1\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">DELETE</parameter>\n"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + fileUri + "</parameter>\n"
                        + " </parameters>\n" + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private OMElement addEndpoint3() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile3\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1.1</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + inboundFileListeningFolder
                        + File.separator + "interval" + "</parameter>\n" + " </parameters>\n" + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private OMElement addEndpoint4() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile4\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + inboundFileListeningFolder
                        + File.separator + "uri" + File.separator + "fail" + "</parameter>\n" + " </parameters>\n"
                        + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private OMElement addEndpoint5() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile5\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + inboundFileListeningFolder
                        + File.separator + "spcChar" + "</parameter>\n" + " </parameters>\n" + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private OMElement addEndpoint6() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile6\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">invalid</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + inboundFileListeningFolder
                        + File.separator + "in" + "</parameter>\n" + " </parameters>\n" + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private OMElement addEndpoint7() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile7\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + inboundFileListeningFolder
                        + File.separator + "in" + "</parameter>\n" + " </parameters>\n" + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private OMElement addEndpoint8() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile8\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n" + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterFailure\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n"
                        + "<parameter name=\"transport.vfs.MoveAfterProcess\">file://" + inboundFileListeningFolder
                        + File.separator + "processed" + "</parameter>"
                        + " <parameter name=\"transport.vfs.FileURI\">file://" + inboundFileListeningFolder
                        + File.separator + "move" + "</parameter>\n" + " </parameters>\n" + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    private boolean deleteFile(File file) throws IOException {
        return file.exists() && file.delete();
    }
}
