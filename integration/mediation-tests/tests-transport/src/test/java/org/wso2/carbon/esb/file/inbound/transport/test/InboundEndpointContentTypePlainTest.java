/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
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
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class InboundEndpointContentTypePlainTest extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;
    private File InboundFileFolder;
    private String pathToFtpDir;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

	    pathToFtpDir = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts" + File.separator
	                   + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport";

        InboundFileFolder = new File(pathToFtpDir + File.separator + "InboundFileFolder");

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

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Inbound endpoint Reading file with Content type Plain Test Case")
    public void testInboundEnpointReadFile_ContentType_Plain() throws Exception {

        addInboundEndpoint(addEndpoint());
        // To check the file getting is read
        boolean isFileRead = false;

        File sourceFile = new File(pathToFtpDir + File.separator + "test.txt");
        File targetFolder = new File(InboundFileFolder + File.separator + "in");
        File targetFile = new File(targetFolder + File.separator + "test.txt");

        try {
            FileUtils.copyFile(sourceFile, targetFile);
            Thread.sleep(2000);
        } finally {
            deleteFile(targetFile);
        }

        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("WSO2 Lanka Pvt Ltd")) {
                isFileRead = true;
            }
        }

        Assert.assertTrue(isFileRead, "The Text file is not getting read");
    }

    private OMElement addEndpoint() throws Exception {
        OMElement synapseConfig = null;
        synapseConfig = AXIOMUtil
                .stringToOM("<inboundEndpoint name=\"testFile2\" onError=\"inFault\" protocol=\"file\"\n"
                        + " sequence=\"requestHandlerSeq\" suspend=\"false\" xmlns=\"http://ws.apache.org/ns/synapse\">\"\n"
                        + " <parameters>\n"
                        + " <parameter name=\"interval\">1000</parameter>\n"
                        + " <parameter name=\"transport.vfs.ActionAfterErrors\">NONE</parameter>\n"
                        + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                        + " <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n"
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

    private boolean deleteFile(File file) throws IOException {
        return file.exists() && file.delete();
    }

}