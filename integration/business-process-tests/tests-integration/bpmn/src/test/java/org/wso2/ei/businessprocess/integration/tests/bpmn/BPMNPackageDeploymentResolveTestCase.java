/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ei.businessprocess.integration.tests.bpmn;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.esb.integration.common.extensions.carbonserver.CarbonTestServerManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * This class will test the undeployment functionality when the artifacts from the server deleted or
 * occurrence of file system unmount on the run.
 */
public class BPMNPackageDeploymentResolveTestCase extends BPSMasterTest {

    private WorkflowServiceClient workflowServiceClient;
    private ServerConfigurationManager serverConfigurationManager;
    private CarbonTestServerManager server;

    private HashMap<String, String> startupParameterMap = new HashMap<String, String>();
    private final String BPMN_PACKAGE_NAME = "HelloApprove";

    public static final String NEW_CONF_DIR = "wso2/business-process/conf";

    @BeforeTest(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(bpsServer);
        updateConfigFiles();
        super.init();
        startupParameterMap.put("-DportOffset", "0");
        startupParameterMap.put("-DresolveDeploymentsAtStartup", "false");
        startupParameterMap.put("startupScript", "business-process");
        server = new CarbonTestServerManager(bpsServer, System.getProperty("carbon.zip"), startupParameterMap);
        server.startServer();
        loginLogoutClient.login();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        serverConfigurationManager = new ServerConfigurationManager(bpsServer);
    }

    private void updateConfigFiles() throws Exception {
        final String artifactLocation =
                FrameworkPathUtil.getSystemResourceLocation() + BPMNTestConstants.DIR_ARTIFACTS + File.separator
                        + BPMNTestConstants.DIR_CONFIG + File.separator;
        //Adding new config file for activiti.xml
        File activitiConfigNew = new File(artifactLocation + BPMNTestConstants.ACTIVITI_CONFIGURATION_FILE_NAME);
        File activitiConfigOriginal = new File(
                FrameworkPathUtil.getCarbonHome() + File.separator + NEW_CONF_DIR + File.separator
                        + BPMNTestConstants.ACTIVITI_CONFIGURATION_FILE_NAME);
        serverConfigurationManager.applyConfiguration(activitiConfigNew, activitiConfigOriginal, true, true);
    }

    @Test(description = "test bpmn artifact undeploying with startup server resolve parameter",
            priority = 1, singleThreaded = true)
    public void testBPMNUndeployWithServerStartupResolving() throws Exception {
        uploadBPMNForTest(BPMN_PACKAGE_NAME);
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, BPMN_PACKAGE_NAME, 0);
        Assert.assertTrue("Error occurred while deploying bpmn artifact",
                workflowServiceClient.getDeployments().length > 0);
        Files.deleteIfExists(Paths.get(FrameworkPathUtil.getCarbonHome() + File.separator + "wso2" + File.separator +
                "business-process" + File.separator + "repository" + File.separator + "deployment" + File.separator +
                "server" + File.separator + "bpmn" + File.separator + "HelloApprove.bar"));
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, BPMN_PACKAGE_NAME, 0);
        //Check whether the deployed artifacts are undeployed
        Assert.assertNotNull(workflowServiceClient.getDeployments());
        Assert.assertNotNull(workflowServiceClient.getProcesses());
    }

    @Test(description = "test bpmn artifact undeploying without startup server resolve parameter",
            priority = 2, singleThreaded = true)
    public void testBPMNUndeployWithoutServerStartupResolving() throws Exception {
        workflowServiceClient.undeploy(BPMN_PACKAGE_NAME);
        server.stopServer();
        startupParameterMap.remove("-DresolveDeploymentsAtStartup");
        server = new CarbonTestServerManager(bpsServer, System.getProperty("carbon.zip"), startupParameterMap);
        server.startServer();
        Assert.assertNull(workflowServiceClient.getDeployments());
        Assert.assertNull(workflowServiceClient.getProcesses());
    }

    @AfterClass(alwaysRun = true)
    public void cleanServer() throws Exception {
        workflowServiceClient.undeploy(BPMN_PACKAGE_NAME);
        server.stopServer();
    }
}
