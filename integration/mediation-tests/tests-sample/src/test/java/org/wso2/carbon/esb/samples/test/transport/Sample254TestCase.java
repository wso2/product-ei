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
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * Sample 254: Using the File System as Transport Medium (VFS)
 */
public class Sample254TestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverManager;
    private String pathToVfsDir;
    private File outFolder;
    private File inFolder;
    private File originalFolder;

    private String oldSynapseConfig;
    private SynapseConfigAdminClient synapseConfigAdminClient;

    private File outfile;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        // Create folders
        pathToVfsDir = getESBResourceLocation() + File.separator + "sample_254" + File.separator;

        outFolder = new File(pathToVfsDir + "out" + File.separator);
        inFolder = new File(pathToVfsDir + "in" + File.separator);
        originalFolder = new File(pathToVfsDir + "original" + File.separator);

        FileUtils.deleteDirectory(outFolder);
        FileUtils.deleteDirectory(inFolder);
        FileUtils.deleteDirectory(originalFolder);

        assertTrue(outFolder.mkdirs(), "file folder not created");
        assertTrue(inFolder.mkdirs(), "file folder not created");
        assertTrue(originalFolder.mkdirs(), "file folder not created");

        AutomationContext context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverManager = new ServerConfigurationManager(context);
        serverManager.applyConfiguration(new File(getESBResourceLocation() + File.separator +
                                                  "sample_254" + File.separator + "axis2.xml"));

        super.init();
        synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        oldSynapseConfig = synapseConfigAdminClient.getConfiguration();

        File newSynapseFile = new File(getESBResourceLocation() + File.separator +
                                       "samples" + File.separator + "synapse_sample_254.xml");

        String synapseConfig = FileUtils.readFileToString(newSynapseFile);

        // Update synapse config
        synapseConfig = synapseConfig.replace("/home/user/test/in", inFolder.getAbsolutePath())
                                     .replace("/home/user/test/original",
                                              originalFolder.getAbsolutePath())
                                     .replace("/home/user/test/out",
                                              outFolder.getAbsolutePath() + File.separator +
                                              "out.xml"
                                     );

        synapseConfigAdminClient.updateConfiguration(synapseConfig);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" }, description = "Testing VFS transport")
    public void testVfsTransport() throws Exception {
        File sourceFile = new File(pathToVfsDir + "test.xml");
        File targetFile = new File(inFolder.getAbsolutePath() + File.separator + "test.xml");
        outfile = new File(outFolder.getAbsolutePath() + File.separator + "out.xml");
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            boolean isOutFileExist = isOutFileExist();
            Assert.assertTrue(isOutFileExist, "out.xml file not found");
            String vfsOut = FileUtils.readFileToString(outfile);
            Assert.assertTrue(vfsOut.contains("WSO2 Company"), "WSO2 Company string not found");
        } finally {
            deleteFolder(targetFile);
            deleteFolder(outfile);
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

        FileUtils.deleteDirectory(outFolder);
        FileUtils.deleteDirectory(inFolder);
        FileUtils.deleteDirectory(originalFolder);

        synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        synapseConfigAdminClient.updateConfiguration(oldSynapseConfig);

        serverManager.restoreToLastConfiguration();
    }

    private boolean deleteFolder(File file) throws Exception {
        return file.exists() && file.delete();
    }

    private boolean isOutFileExist() throws Exception {
        long startTime = System.currentTimeMillis();
        boolean isOutFileExist = false;
        while (((System.currentTimeMillis() - startTime) < 180000) && !isOutFileExist) {
            log.info("Waiting for out.xml file....");
            isOutFileExist = outfile.exists();
            Thread.sleep(3000);
        }

        return isOutFileExist;
    }
}
