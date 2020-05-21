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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.testng.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.testng.IExecutionListener;
import org.wso2.carbon.application.mgt.stub.ApplicationAdminExceptionException;
import org.wso2.carbon.esb.scenario.test.common.AuthenticatorClient;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * TestNG execution listener for perform test execution preparation
 * - deploy carbon applications if not already deployed
 */
public class TestPrepExecutionListener implements IExecutionListener {

    private static final Log log = LogFactory.getLog(TestPrepExecutionListener.class);
    private boolean standaloneMode = false;
    private String backendURL;
    private String mgtConsoleUrl;
    private String sessionCookie;

    private CarbonAppUploaderClient carbonAppUploaderClient = null;
    private ApplicationAdminClient applicationAdminClient = null;

    private static final String resourceLocation = System.getProperty(ScenarioConstants.TEST_RESOURCES_DIR);
    private static final String commonResourcesDir = System.getProperty(ScenarioConstants.COMMON_RESOURCES_DIR);

    private static final String carbonApplicationsDir =
            System.getProperty(ScenarioConstants.TEST_RESOURCES_CARBON_APPLICATIONS_DIR);

    private Properties infraProperties;

    @Override
    public void onExecutionStart() {

        infraProperties = ScenarioTestBase.getDeploymentProperties();
        ScenarioTestBase.configureUrls();
        // TODO : uncomment this once test environment is stable and remove hardcoded value
        //standaloneMode = Boolean.valueOf(infraProperties.getProperty(ScenarioConstants.STANDALONE_DEPLOYMENT));
        standaloneMode = true;

        if (standaloneMode) {
            log.info("Test execution standalone mode : " + standaloneMode);
            String carbonAppListStr = System.getProperty(ScenarioConstants.TEST_RESOURCES_CARBON_APPLICATIONS_LIST);

            backendURL = ScenarioTestBase.getBackendURL();
            mgtConsoleUrl = ScenarioTestBase.getMgtConsoleURL();

            setKeyStoreProperties();
        }
    }

    @Override
    public void onExecutionFinish() {
        //Nothing to do yet
    }

    /**
     * Function to upload carbon application
     *
     * @param carFileName
     * @return
     * @throws RemoteException
     */
    private void deployCarbonApplication(String carFileName) throws RemoteException, ApplicationAdminExceptionException {

        if (standaloneMode) {
            if (applicationAdminClient == null) {
                applicationAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
            }

            if (!checkCAppDeployed(applicationAdminClient, carFileName)) {

                log.info("Deploying carbon application : " + carFileName);
                // If standalone mode, deploy the CApp to the server
                String cappFilePath = carbonApplicationsDir + File.separator + carFileName + ScenarioConstants.CAPP_EXTENSION;

                if (carbonAppUploaderClient == null) {
                    carbonAppUploaderClient = new CarbonAppUploaderClient(backendURL, sessionCookie);
                }

                DataHandler dh = new DataHandler(new FileDataSource(new File(cappFilePath)));
                // Upload carbon application
                carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName + ScenarioConstants.CAPP_EXTENSION, dh);

                //TODO - This thread sleep is added temporarily to wait until the ESB Instances sync in the cluster in clustered test environment
                if (!Boolean.valueOf(infraProperties.getProperty(ScenarioConstants.STANDALONE_DEPLOYMENT))) {
                    log.info("Waiting for artifacts synchronized across cluster nodes");
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        // log and ignore
                        log.error("Error occurred while waiting for artifacts synchronized across cluster nodes", e);
                    }
                }

                // Wait for Capp to sync
                log.info("Waiting for Carbon Application deployment ..");
                Awaitility.await()
                        .pollInterval(500, TimeUnit.MILLISECONDS)
                        .atMost(ScenarioConstants.ARTIFACT_DEPLOYMENT_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                        .until(isCAppDeployed(applicationAdminClient, carFileName));

            } else {
                log.info("Carbon application : " + carFileName + " already deployed");
            }
        }
    }

    /**
     * Function to undeploy carbon application
     *
     * @param applicationName
     * @throws ApplicationAdminExceptionException
     * @throws RemoteException
     */
    private void undeployCarbonApplication(String applicationName)
            throws ApplicationAdminExceptionException, RemoteException {
        if (standaloneMode) {
            applicationAdminClient.deleteApplication(applicationName);

            // Wait for Capp to undeploy
            Awaitility.await()
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .atMost(ScenarioConstants.ARTIFACT_DEPLOYMENT_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                    .until(isCAppUnDeployed(applicationAdminClient, applicationName));
        }
    }

    private void setKeyStoreProperties() {
        System.setProperty("javax.net.ssl.trustStore", commonResourcesDir + "/keystores/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }

    private String getServerHost(String url) {
        if (url != null && url.contains("/")) {
            url = url.split("/")[2].split(":")[0];
        } else if (url == null) {
            url = "localhost";
        }
        log.info("Backend URL is set as : " + url);
        return url;
    }

    private Callable<Boolean> isCAppDeployed(final ApplicationAdminClient applicationAdminClient, final String cAppName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                log.info("Check CApp deployment : " + cAppName);
                return checkCAppDeployed(applicationAdminClient, cAppName);
            }
        };
    }

    private boolean checkCAppDeployed(ApplicationAdminClient applicationAdminClient, final String cAppName)
            throws ApplicationAdminExceptionException, RemoteException {
        String[] applicationList = applicationAdminClient.listAllApplications();
        if (applicationList != null) {
            for (String app : applicationList) {
                if (app.equals(cAppName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Callable<Boolean> isCAppUnDeployed(final ApplicationAdminClient applicationAdminClient, final String cAppName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                log.info("Check CApp un-deployment : " + cAppName);
                boolean undeployed = !checkCAppDeployed(applicationAdminClient, cAppName);
                if (undeployed) log.info("Carbon Application : " + cAppName + " Successfully un-deployed");
                return undeployed;
            }
        };
    }
}
