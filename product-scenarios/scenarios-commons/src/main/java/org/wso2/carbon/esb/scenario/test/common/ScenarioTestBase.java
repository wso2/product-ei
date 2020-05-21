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

package org.wso2.carbon.esb.scenario.test.common;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.testng.SkipException;
import org.wso2.carbon.application.mgt.stub.ApplicationAdminExceptionException;
import org.wso2.carbon.esb.scenario.test.common.ftp.FTPClientWrapper;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This is the base class of scenario test classes.
 */
public class ScenarioTestBase {

    private static final String INPUTS_LOCATION = System.getProperty("data.bucket.location");
    private static final String INFRASTRUCTURE_PROPERTIES = "infrastructure.properties";
    private static final String DEPLOYMENT_PROPERTIES = "deployment.properties";
    private static final String JOB_PROPERTIES = "testplan-props.properties";

    public static final Log log = LogFactory.getLog(ScenarioTestBase.class);

    protected static final String testResourcesDir = System.getProperty(ScenarioConstants.TEST_RESOURCES_DIR);

    protected static final String commonResourcesDir = System.getProperty(ScenarioConstants.COMMON_RESOURCES_DIR);

    private static final String PRODUCT_VERSION = "ProductVersion";
    private static Properties infraProperties;

    private static String backendURL;
    private static String serviceURL;
    private static String securedServiceURL;
    private static String mgtConsoleURL;
    private static String elasticSearchHostname;
    private static String deploymentStackName;
    private static String runningProductVersion;
    private static String localVfsLocation;
    private static String testRunUUID;

    private String sessionCookie;
    private boolean standaloneMode;

    private static String ftpHostName;
    private static String ftpUsername;
    private static String ftpPassword;

    private CarbonAppUploaderClient carbonAppUploaderClient = null;
    private ApplicationAdminClient applicationAdminClient = null;

    /**
     * Initialize testcase
     *
     * @throws Exception if the logging in fails
     */
    public void init() throws Exception {
        log.info("Started Executing Scenario TestBase ");

        configureUrls();
        //standaloneMode = Boolean.valueOf(infraProperties.getProperty(STANDALONE_DEPLOYMENT));
        // TODO : remove this once test environment is stable
        standaloneMode = true;

        // Retrieve test execution run UUID
        testRunUUID = System.getProperty(ScenarioConstants.TEST_RUN_UUID);

        // login
        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);
//        sessionCookie = authenticatorClient.login("admin", "admin", getServerHost());

        log.info("Service URL: " + serviceURL + " | Secured Service URL: " + securedServiceURL);
        log.info("The Backend service URL : " + backendURL + ". session cookie: " + sessionCookie);

    }

    /**
     * Validates if the test case is one that should be executed on the running product version.
     * <p>
     * All test methods of the test case will be skipped if it is not compatible with the running version. This is
     * introduced to cater tests introduced for fixes done as patches for released product versions as they may only
     * be valid for the product version for which the fix was done for.
     *
     * @param incompatibleVersions product versions that the test is not compatible with
     */
    protected void skipTestsForIncompatibleProductVersions(String... incompatibleVersions) {
        //running product version can be null if the property is not in deployment properties
        if (null != runningProductVersion && Arrays.asList(incompatibleVersions).contains(runningProductVersion)) {
            String errorMessage =
                    "Skipping test: " + this.getClass().getName() + " due to version mismatch. Running "
                    + "product version: " + runningProductVersion + ", Non allowed versions: "
                    + Arrays.toString(incompatibleVersions);
            log.warn(errorMessage);
            throw new SkipException(errorMessage);
        }
    }

    /**
     * Skip test if the test is executed in standalone mode since some tests need to be executed in distributed
     * infrastructure. For example some tests required to assert log entries from ELK stack
     */
    protected void skipTestsIfStandaloneDeployment() {
        if (Boolean.valueOf(getInfrastructureProperty(ScenarioConstants.STANDALONE_DEPLOYMENT))) {
            String errorMessage =
                    "Skipping test: " + this.getClass().getName() + " since this test require distributed deployment";
            log.warn(errorMessage);
            throw new SkipException(errorMessage);
        }
    }

    /**
     * Perform cleanup.
     *
     * @throws Exception if an error occurs while performing clean up task
     */
    public void cleanup() throws Exception {
    }

    /**
     * This is a utility method to load the deployment details.
     * The deployment details are available as key-value pairs in {@link #INFRASTRUCTURE_PROPERTIES},
     * {@link #DEPLOYMENT_PROPERTIES}, and {@link #JOB_PROPERTIES} under the
     * {@link #INPUTS_LOCATION}.
     *
     * This method loads these files into one single properties, and return it.
     *
     * @return properties the deployment properties
     */
    public static Properties getDeploymentProperties() {
        Path infraPropsFile = Paths.get(INPUTS_LOCATION + File.separator + INFRASTRUCTURE_PROPERTIES);
        Path deployPropsFile = Paths.get(INPUTS_LOCATION + File.separator + DEPLOYMENT_PROPERTIES);
        Path jobPropsFile = Paths.get(INPUTS_LOCATION + File.separator + JOB_PROPERTIES);

        Properties props = new Properties();
        loadProperties(infraPropsFile, props);
        loadProperties(deployPropsFile, props);
        loadProperties(jobPropsFile, props);
        return props;
    }

    public String getInfrastructureProperty(String propertyName) {
        return infraProperties.getProperty(propertyName);
    }

    protected String getApiInvocationURLHttp(String resourcePath) {
        return serviceURL + (resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
    }

    public String getApiInvocationURLHttps(String resourcePath) {
        return securedServiceURL + (resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
    }

    protected String getProxyServiceURLHttp(String proxyServiceName) {
        return serviceURL + "services" + (proxyServiceName.startsWith("/") ? "" : "/") + proxyServiceName;
    }

    protected String getProxyServiceURLHttps(String proxyServiceName) {
        return securedServiceURL + "services" + (proxyServiceName.startsWith("/") ? "" : "/") + proxyServiceName;
    }

    /**
     * Function to get absolute local VFS location for VFS tests
     *
     * @param resourcePath - relative path
     * @return absolute vfs path for VFS tests
     */
    protected String getLocalVfsLocation(String resourcePath) {
        return localVfsLocation + (localVfsLocation.endsWith("/") ? "" : "/") + resourcePath;
    }

    /**
     * Function to get absolute VFS location for VFS tests
     *
     * @param resourcePath - relative path
     * @return absolute vfs source directory for VFS tests
     */
    protected String getVfsSourceDir(String resourcePath) {
        return testResourcesDir + (testResourcesDir.endsWith("/") ? "" : "/") + resourcePath;
    }

    /**
     * Function to upload carbon application
     *
     * @param carFileName the name of the carbon application to be deployed
     * @throws RemoteException if the admin client becomes unable to connect to the admin service
     */
    protected void deployCarbonApplication(String carFileName) throws RemoteException, InterruptedException {

        if (standaloneMode) {
            // If standalone mode, deploy the CAPP to the server
            String cappFilePath = testResourcesDir + File.separator + "artifacts" +
                    File.separator + carFileName + ".car";

            if (carbonAppUploaderClient == null) {
                carbonAppUploaderClient = new CarbonAppUploaderClient(backendURL, sessionCookie);
            }
            DataHandler dh = new DataHandler(new FileDataSource(new File(cappFilePath)));
            // Upload carbon application
            carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName + ".car", dh);

            //TODO - This thread sleep is added temporarily to wait until the ESB Instances sync in the cluster in clustered test environment
            if (!Boolean.valueOf(infraProperties.getProperty(ScenarioConstants.STANDALONE_DEPLOYMENT))) {
                log.info("Waiting for artifacts synchronized across cluster nodes");
                Thread.sleep(120000);
            }

            if (applicationAdminClient == null) {
                applicationAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
            }

            // Wait for Capp to sync
            log.info("Waiting for Carbon Application deployment ..");
            Awaitility.await()
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .atMost(ScenarioConstants.ARTIFACT_DEPLOYMENT_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                    .until(isCAppDeployed(applicationAdminClient, carFileName));
        }
    }

    /**
     * Function to undeploy carbon application
     *
     * @param applicationName name of the Carbon application to be undeployed
     * @throws ApplicationAdminExceptionException if an error occurs while undeploying carbon application
     * @throws RemoteException                    if the admin client becomes unable to connect to the service
     */
    public void undeployCarbonApplication(String applicationName)
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

    private static void loadProperties(Path propsFile, Properties props) {
        String msg = "Deployment property file not found: ";
        if (!Files.exists(propsFile)) {
            log.warn(msg + propsFile);
            return;
        }

        try (InputStream propsIS = Files.newInputStream(propsFile)) {
            props.load(propsIS);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    private String getServerHost() {
        String url = infraProperties.getProperty(ScenarioConstants.MGT_CONSOLE_URL);
        if (url != null && url.contains("/")) {
            url = url.split("/")[2].split(":")[0];
        } else
        if (url == null) {
            url = "localhost";
        }
        log.info("Backend URL is set as : " + url);
        return url;
    }

    private Callable <Boolean> isCAppDeployed(final ApplicationAdminClient applicationAdminClient, final String cAppName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                log.info("Check CApp deployment : " + cAppName);
                String[] applicationList = applicationAdminClient.listAllApplications();
                if (applicationList != null) {
                    for (String app : applicationList) {
                        if (app.equals(cAppName)) {
                            log.info("Carbon Application : " + cAppName + " Successfully deployed");
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    private Callable <Boolean> isCAppUnDeployed(final ApplicationAdminClient applicationAdminClient, final String cAppName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                log.info("Check CApp un-deployment : " + cAppName);
                boolean undeployed = true;
                String[] applicationList = applicationAdminClient.listAllApplications();
                if (applicationList != null) {
                    for (String app : applicationList) {
                        if (app.equals(cAppName)) {
                            undeployed = false;
                        }
                    }
                }
                if (undeployed) log.info("Carbon Application : " + cAppName + " Successfully un-deployed");
                return undeployed;
            }
        };
    }

    /**
     * Get the list of request,response and header of the each testcase
     *
     * @param testCase - the name of the testCase. Please follow the common conventions to store request and response files
     * @return - List of Arrays consisting request, response and header.
     * @throws IOException - if and error occurs file extracting the file content
     */
    protected List<Object[]> getRequestResponseHeaderList(String testCase) throws IOException {

        List<Request> requests = extractRequests(testCase);
        List<String> responses = extractResponses(testCase);

        List<Object[]> requestResponseList = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            String[] tmp = {requests.get(i).getPayload(), responses.get(i), requests.get(i).getMessageIdHeader()};
            requestResponseList.add(tmp);
        }
        return requestResponseList;
    }

    protected List<String> getListOfFiles(String folderLocation) {
        File filePath = new File(getClass().getResource(folderLocation).getPath());
        File[] listOfFiles = filePath.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    protected String getFileContent(String folderLocation, String fileName) throws IOException {
        File fileLocation = new File(getClass().getResource(folderLocation + File.separator + fileName).getPath());

        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(String.valueOf(fileLocation))))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
            }
        }

        return sb.toString();
    }

    public static synchronized void configureUrls() {
        if (null == infraProperties) {
            infraProperties = getDeploymentProperties();
            backendURL = infraProperties.getProperty(ScenarioConstants.CARBON_SERVER_URL)
                         + (infraProperties.getProperty(ScenarioConstants.CARBON_SERVER_URL).endsWith("/") ? "" : "/");
            serviceURL = infraProperties.getProperty(ScenarioConstants.ESB_HTTP_URL)
                         + (infraProperties.getProperty(ScenarioConstants.ESB_HTTP_URL).endsWith("/") ? "" : "/");
            securedServiceURL = infraProperties.getProperty(ScenarioConstants.ESB_HTTPS_URL)
                                + (infraProperties.getProperty(ScenarioConstants.ESB_HTTPS_URL)
                                                  .endsWith("/") ? "" : "/");
            runningProductVersion = infraProperties.getProperty(PRODUCT_VERSION);
            mgtConsoleURL = infraProperties.getProperty(ScenarioConstants.MGT_CONSOLE_URL);
            if(Boolean.valueOf(infraProperties.getProperty(ScenarioConstants.STANDALONE_DEPLOYMENT))) {
                localVfsLocation = System.getProperty("user.home");
            } else {
                localVfsLocation = infraProperties.getProperty(ScenarioConstants.LOCAL_VFS_LOCATION);
            }

            deploymentStackName = infraProperties.getProperty(ScenarioConstants.INFRA_EI_STACK_NAME);
            elasticSearchHostname = infraProperties.getProperty(ScenarioConstants.ELASTICSEARCH_HOSTNAME);
            ftpHostName = infraProperties.getProperty(ScenarioConstants.FTP_HOST_NAME);
            ftpUsername = infraProperties.getProperty(ScenarioConstants.FTP_USERNAME);
            ftpPassword = infraProperties.getProperty(ScenarioConstants.FTP_PASSWORD);
        }
    }

    public static String getBackendURL() {
        return backendURL;
    }

    public static String getMgtConsoleURL() {
        return mgtConsoleURL;
    }

    public static String getElasticSearchHostname() {
        return elasticSearchHostname;
    }

    public static String getDeploymentStackName() {
        return deploymentStackName;
    }

    public String getTestRunUUID() {
        return testRunUUID;
    }

    private String appendSourceFolder(String testCase, String relativeSourceFolderPath) {
        return File.separator + ScenarioConstants.SOURCE_FILES + File.separator + testCase + File.separator
               + relativeSourceFolderPath;
    }

    private List<String> getFilesFromSourceDirectory(String relativePath) {
        List<String> requestFiles = getListOfFiles(relativePath);
        java.util.Collections.sort(requestFiles, Collator.getInstance());
        return requestFiles;
    }

    protected List<Request> extractRequests(String testCase) throws IOException {
        String relativeRequestFolderLocation = appendSourceFolder(testCase, ScenarioConstants.REQUEST);
        List<String> requestFiles = getFilesFromSourceDirectory(relativeRequestFolderLocation);
        ArrayList<Request> requestArray = new ArrayList();

        for (String file : requestFiles) {
            String fileContent = getFileContent(relativeRequestFolderLocation, file);
            String header = FilenameUtils.removeExtension(file);
            requestArray.add(new Request(fileContent, header));
        }
        return requestArray;
    }

    protected List<String> extractResponses(String testCase) throws IOException {

        String relativeResponseFolderLocation = appendSourceFolder(testCase, ScenarioConstants.RESPONSE);
        List<String> responseFiles = getFilesFromSourceDirectory(relativeResponseFolderLocation);
        ArrayList<String> responseArray = new ArrayList();
        for (String file : responseFiles) {
            String fileContent = getFileContent(relativeResponseFolderLocation, file);
            responseArray.add(fileContent);
        }
        return responseArray;
    }

    /**
     * Function to connect to an FTP Server
     *
     * @param port - ftp port
     * @return - VFSClient
     * @throws IOException - if connecting to FTP server fails
     */
    protected FTPClientWrapper connectToFTP(int port) throws IOException {
        FTPClientWrapper vfsClient = new FTPClientWrapper(ftpHostName, ftpUsername, ftpPassword, port);
        vfsClient.connectToFtp();
        return vfsClient;
    }
}

