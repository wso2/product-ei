/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.hotdeployment.test;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class will test the ESB artifacts hot deployment.
 */
public class SynapseArtifactsHotDeploymentTestCase extends ESBIntegrationTest {

    private final String SERVER_DEPLOYMENT_DIR =
            System.getProperty(ESBTestConstant.CARBON_HOME) + File.separator + "repository" + File.separator
                    + "deployment" + File.separator + "server" + File.separator + "synapse-configs" + File.separator
                    + "default" + File.separator;
    private final String SOURCE_DIR =
            TestConfigurationProvider.getResourceLocation(ESBTestConstant.ESB_PRODUCT_GROUP) + File.separator
                    + "hotdeployment" + File.separator;

    private final String proxyName = "HotDeploymentTestProxy";
    private final String proxyFileName = "HotDeploymentTestProxy.xml";

    private final String sequenceName = "HotDeploymentTestSequence";
    private final String sequenceFileName = "HotDeploymentTestSequence.xml";

    private final String endpointName = "HotDeploymentTestEndpoint";
    private final String endpointFileName = "HotDeploymentTestEndpoint.xml";

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        copyArtifactsToDeploymentDirectory();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb",
          description = "Proxy Service Hot Deployment")
    public void testProxyServiceHotDeployment() throws Exception {
        String proxyServiceFile = SERVER_DEPLOYMENT_DIR + "proxy-services" + File.separator + proxyFileName;

        Assert.assertTrue(esbUtils.isProxyDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Proxy Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(proxyServiceFile));
        log.info(proxyFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "'HotDeploymentTestProxy' has been update from file"),
                "Proxy deployment failed on updating file");
        Assert.assertTrue(esbUtils.isProxyDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Proxy Deployment failed on updating file");
        FileManager.deleteFile(proxyServiceFile);
        Assert.assertTrue(esbUtils.isProxyUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Proxy Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "Sequence Hot Deployment")
    public void testSequenceHotDeployment() throws Exception {
        String sequenceFile = SERVER_DEPLOYMENT_DIR + "sequences" + File.separator + sequenceFileName;

        Assert.assertTrue(esbUtils.isSequenceDeployed(contextUrls.getBackEndUrl(), sessionCookie, sequenceName),
                "Sequence Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(sequenceFile));
        log.info(sequenceFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestSequence has been updated from the file"),
                "Sequence deployment failed on updating file");
        Assert.assertTrue(esbUtils.isSequenceDeployed(contextUrls.getBackEndUrl(), sessionCookie, sequenceName),
                "Sequence Deployment failed on updating file");
        FileManager.deleteFile(sequenceFile);
        Assert.assertTrue(esbUtils.isSequenceUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Sequence Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "Endpoint Hot Deployment")
    public void testEndpointHotDeployment() throws Exception {
        String endpointFile = SERVER_DEPLOYMENT_DIR + "endpoints" + File.separator + endpointFileName;

        Assert.assertTrue(esbUtils.isEndpointDeployed(contextUrls.getBackEndUrl(), sessionCookie, endpointName),
                "Endpoint Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(endpointFile));
        log.info(endpointFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestEndpoint has been updated from the file"),
                "Endpoint deployment failed on updating file");
        Assert.assertTrue(esbUtils.isEndpointDeployed(contextUrls.getBackEndUrl(), sessionCookie, endpointName),
                "Endpoint Deployment failed on updating file");
        FileManager.deleteFile(endpointFile);
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, endpointName),
                "Endpoint Undeployment failed");
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }

    private void copyArtifactsToDeploymentDirectory() throws IOException {
        String proxyFile = SOURCE_DIR + proxyFileName;
        String sequenceFile = SOURCE_DIR + sequenceFileName;
        String endpointFile = SOURCE_DIR + endpointFileName;
        FileManager.copyFile(new File(proxyFile),
                SERVER_DEPLOYMENT_DIR + "proxy-services" + File.separator + proxyFileName);
        FileManager.copyFile(new File(sequenceFile),
                SERVER_DEPLOYMENT_DIR + "sequences" + File.separator + sequenceFileName);
        FileManager.copyFile(new File(endpointFile),
                SERVER_DEPLOYMENT_DIR + "endpoints" + File.separator + endpointFileName);
    }

    private boolean searchInLogs(LogViewerClient logViewerClient, String searchString)
            throws LogViewerLogViewerException, RemoteException, InterruptedException {
        boolean logFound = false;
        for (int i = 0; i < 60; i++) {
            LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
            if (logEvents != null) {
                for (LogEvent logEvent : logEvents) {
                    if (logEvent == null) {
                        continue;
                    }
                    if (logEvent.getMessage().contains(searchString)) {
                        logFound = true;
                        break;
                    }
                }
            }
            if (logFound) {
                break;
            }
            Thread.sleep(500);
        }
        return logFound;
    }
}
