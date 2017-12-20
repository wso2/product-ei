/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.file.inbound.transport.test;

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
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

/**
 * Tests the invocation of a proxy service via a dynamic sequence registered in the registry upon the receipt of a
 * file using the file inbound.
 */
public class FileInboundWithDynamicSequenceTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;
    private File InboundFileFolder;
    private String pathToDir;
    private ResourceAdminServiceClient resourceAdminServiceStub;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        pathToDir = getESBResourceLocation() + File.separator + "file" + File.separator + "inbound" + File.separator
                                           + "transport";

        InboundFileFolder = new File(pathToDir + File.separator + "InboundInFileFolder");

        // create InboundFileFolder if not exists
        if (InboundFileFolder.exists()) {
            FileUtils.cleanDirectory(InboundFileFolder);
        } else {
            Assert.assertTrue(InboundFileFolder.mkdir(), "InboundFileFolder not created");
        }

        super.init();
        resourceAdminServiceStub = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context
                .getContextTenant().getContextUser().getUserName()
                , context.getContextTenant().getContextUser().getPassword());
        uploadResourcesToGovernanceRegistry();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests sequences from  the governance registry with inbound endpoint")
    public void testSequence() throws Exception {

        logViewerClient.clearLogs();

        //Copy file to source folder from which the inbound endpoint will pick up the message
        File sourceFile = new File(pathToDir + File.separator + "test.xml");
        File targetFile = new File(InboundFileFolder + File.separator + "test.xml");
        if (!sourceFile.exists()) {
            sourceFile.createNewFile();
        }
        log.info("Copying files to the target folder");
        FileUtils.copyFile(sourceFile, targetFile);

        //And file inbound endpoint
        addInboundEndpoint(createInboundEP());

        Assert.assertTrue(Utils.checkForLog(logViewerClient, "Proxy invoked by dynamic sequence in file inbound",
                10), "The XML file is not getting read");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        try {
            FileUtils.deleteDirectory(InboundFileFolder);
        } finally {
            super.cleanup();
        }
    }

    private OMElement createInboundEP() throws Exception {
        OMElement synapseConfig = AXIOMUtil.stringToOM(
                "<inboundEndpoint name=\"testFile1\" onError=\"inFault\" " + "protocol=\"file\"\n"
                + " sequence=\"gov:/fileInboundDynamicSequence\" suspend=\"false\" xmlns=\"http://ws.apache"
                + ".org/ns/synapse\">\"\n"
                + " <parameters>\n"
                + " <parameter name=\"interval\">1000</parameter>\n"
                + " <parameter name=\"transport.vfs.ActionAfterErrors\">DELETE</parameter>\n"
                + " <parameter name=\"transport.vfs.Locking\">enable</parameter>\n"
                + " <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n"
                + " <parameter name=\"transport.vfs.ActionAfterFailure\">DELETE</parameter>\n"
                + " <parameter name=\"transport.vfs.ActionAfterProcess\">DELETE</parameter>\n"
                + " <parameter name=\"transport.vfs.FileURI\">file://" + InboundFileFolder + "</parameter>\n"
                + " </parameters>\n"
                + "</inboundEndpoint>\n");

        return synapseConfig;
    }

    /**
     * Uploads sequence to governance registry.
     *
     * @throws Exception if the URL representing th e resource file is invalid
     */
    private void uploadResourcesToGovernanceRegistry() throws Exception {
        resourceAdminServiceStub.addResource("/_system/governance/fileInboundDynamicSequence",
                "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/file/inbound/transport/fileInboundDynamicSequence.xml").getPath())));
    }


}
