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
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.FileManager;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;

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

    private final String proxyFileName = "HotDeploymentTestProxy.xml";
    private final String sequenceFileName = "HotDeploymentTestSequence.xml";
    private final String endpointFileName = "HotDeploymentTestEndpoint.xml";
    private final String apiFileName = "HotDeploymentTestAPI.xml";
    private final String localEntryFileName = "HotDeploymentTestLocalEntry.xml";
    private final String messageStoreFileName = "HotDeploymentTestMessageStore.xml";

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
        String proxyName = "HotDeploymentTestProxy";
        String proxyServiceFile = SERVER_DEPLOYMENT_DIR + "proxy-services" + File.separator + proxyFileName;

        Assert.assertTrue(esbUtils.isProxyDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Proxy Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(proxyServiceFile));
        log.info(proxyFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "'HotDeploymentTestProxy' has been update from file"),
                "Proxy deployment failed on updating file. Log message not found");
        Assert.assertTrue(esbUtils.isProxyDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Proxy Deployment failed on updating file");
        Awaitility.await().atMost(10, SECONDS).until(fileDelete(proxyServiceFile));
        Assert.assertTrue(esbUtils.isProxyUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, proxyName),
                "Proxy Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "Sequence Hot Deployment")
    public void testSequenceHotDeployment() throws Exception {
        String sequenceName = "HotDeploymentTestSequence";
        String sequenceFile = SERVER_DEPLOYMENT_DIR + "sequences" + File.separator + sequenceFileName;

        Assert.assertTrue(esbUtils.isSequenceDeployed(contextUrls.getBackEndUrl(), sessionCookie, sequenceName),
                "Sequence Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(sequenceFile));
        log.info(sequenceFile + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestSequence has been updated from the file"),
                "Sequence deployment failed on updating file. Log message not found");
        Assert.assertTrue(esbUtils.isSequenceDeployed(contextUrls.getBackEndUrl(), sessionCookie, sequenceName),
                "Sequence Deployment failed on updating file");

        Awaitility.await().atMost(10, SECONDS).until(fileDelete(sequenceFile));
        Assert.assertTrue(esbUtils.isSequenceUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, sequenceName),
                "Sequence Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "Endpoint Hot Deployment")
    public void testEndpointHotDeployment() throws Exception {
        String endpointName = "HotDeploymentTestEndpoint";
        String endpointFile = SERVER_DEPLOYMENT_DIR + "endpoints" + File.separator + endpointFileName;

        Assert.assertTrue(esbUtils.isEndpointDeployed(contextUrls.getBackEndUrl(), sessionCookie, endpointName),
                "Endpoint Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(endpointFile));
        log.info(endpointFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestEndpoint has been updated from the file"),
                "Endpoint deployment failed on updating file. Log message not found");
        Assert.assertTrue(esbUtils.isEndpointDeployed(contextUrls.getBackEndUrl(), sessionCookie, endpointName),
                "Endpoint Deployment failed on updating file");

        Awaitility.await().atMost(10, SECONDS).until(fileDelete(endpointFile));
        Assert.assertTrue(esbUtils.isEndpointUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, endpointName),
                "Endpoint Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "API Hot Deployment")
    public void testAPIHotDeployment() throws Exception {
        String apiName = "HotDeploymentTestAPI";
        String apiFile = SERVER_DEPLOYMENT_DIR + "api" + File.separator + apiFileName;

        Assert.assertTrue(esbUtils.isApiDeployed(contextUrls.getBackEndUrl(), sessionCookie, apiName),
                "API Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(apiFile));
        log.info(apiFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestAPI has been updated from the file"),
                "API deployment failed on updating file. Log message not found");
        Assert.assertTrue(esbUtils.isApiDeployed(contextUrls.getBackEndUrl(), sessionCookie, apiName),
                "API Deployment failed on updating file");

        Awaitility.await().atMost(5, SECONDS).until(fileDelete(apiFile));
        Assert.assertTrue(esbUtils.isApiUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, apiName),
                "API Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "Local Entry Hot Deployment")
    public void testLocalEntryHotDeployment() throws Exception {
        String localEntryName = "HotDeploymentTestLocalEntry";
        String localEntryFile = SERVER_DEPLOYMENT_DIR + "local-entries" + File.separator + localEntryFileName;

        Assert.assertTrue(esbUtils.isLocalEntryDeployed(contextUrls.getBackEndUrl(), sessionCookie, localEntryName),
                "Local Entry Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(localEntryFile));
        log.info(localEntryFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestLocalEntry has been updated from the file"),
                "Local Entry deployment failed on updating file. Log message not found");
        Assert.assertTrue(esbUtils.isLocalEntryDeployed(contextUrls.getBackEndUrl(), sessionCookie, localEntryName),
                "Local Entry Deployment failed on updating file");
        Awaitility.await().atMost(10, SECONDS).until(fileDelete(localEntryFile));
        Assert.assertTrue(esbUtils.isLocalEntryUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, localEntryName),
                "Local Entry Undeployment failed");
    }

    @Test(groups = "wso2.esb",
          description = "Message Store Hot Deployment")
    public void testMessageStoreHotDeployment() throws Exception {
        String messageStoreName = "HotDeploymentTestMessageStore";
        String messageStoreFile = SERVER_DEPLOYMENT_DIR + "message-stores" + File.separator + messageStoreFileName;

        Assert.assertTrue(esbUtils.isMessageStoreDeployed(contextUrls.getBackEndUrl(), sessionCookie, messageStoreName),
                "Message Store Deployment failed");

        logViewerClient.clearLogs();
        FileUtils.touch(new File(messageStoreFile));
        log.info(messageStoreFileName + " has been updated and waiting for redeployment");
        Assert.assertTrue(searchInLogs(logViewerClient, "HotDeploymentTestMessageStore has been updated from the file"),
                "Message Store deployment failed on updating file. Log message not found");
        Assert.assertTrue(esbUtils.isMessageStoreDeployed(contextUrls.getBackEndUrl(), sessionCookie, messageStoreName),
                "Message Store Deployment failed on updating file");
        Awaitility.await().atMost(10, SECONDS).until(fileDelete(messageStoreFile));
        Assert.assertTrue(esbUtils.isSequenceUnDeployed(contextUrls.getBackEndUrl(), sessionCookie, messageStoreName),
                "Message Store Undeployment failed");
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }

    private void copyArtifactsToDeploymentDirectory() throws IOException {
        String proxyFile = SOURCE_DIR + proxyFileName;
        String sequenceFile = SOURCE_DIR + sequenceFileName;
        String endpointFile = SOURCE_DIR + endpointFileName;
        String apiFile = SOURCE_DIR + apiFileName;
        String localEntryFile = SOURCE_DIR + localEntryFileName;
        String messageStoreFile = SOURCE_DIR + messageStoreFileName;
        FileUtils.copyFile(new File(proxyFile),
                new File(SERVER_DEPLOYMENT_DIR + "proxy-services" + File.separator + proxyFileName));
        FileUtils.copyFile(new File(sequenceFile),
                new File(SERVER_DEPLOYMENT_DIR + "sequences" + File.separator + sequenceFileName));
        FileUtils.copyFile(new File(endpointFile),
                new File(SERVER_DEPLOYMENT_DIR + "endpoints" + File.separator + endpointFileName));
        FileUtils.copyFile(new File(apiFile), new File(SERVER_DEPLOYMENT_DIR + "api" + File.separator + apiFileName));
        FileUtils.copyFile(new File(localEntryFile),
                new File(SERVER_DEPLOYMENT_DIR + "local-entries" + File.separator + localEntryFileName));
        FileUtils.copyFile(new File(messageStoreFile),
                new File(SERVER_DEPLOYMENT_DIR + "message-stores" + File.separator + messageStoreFileName));
    }

    private boolean searchInLogs(LogViewerClient logViewerClient, String searchString)
            throws RemoteException, InterruptedException {
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

    private Callable<Boolean> fileDelete(final String filePath) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return FileManager.deleteFile(filePath);
            }
        };
    }
}
