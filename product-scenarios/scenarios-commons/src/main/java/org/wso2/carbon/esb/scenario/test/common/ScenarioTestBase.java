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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.w3c.dom.Document;
import org.wso2.carbon.application.mgt.stub.ApplicationAdminExceptionException;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.wso2.esb.integration.common.utils.common.FileManager.readFile;

public class ScenarioTestBase {

    private static final String INPUTS_LOCATION = System.getenv("DATA_BUCKET_LOCATION");
    private static final String INFRASTRUCTURE_PROPERTIES = "infrastructure.properties";
    private static final String DEPLOYMENT_PROPERTIES = "deployment.properties";
    private static final String JOB_PROPERTIES = "testplan-props.properties";

    private static final Log log = LogFactory.getLog(ScenarioTestBase.class);

    public static final String MGT_CONSOLE_URL = "MgtConsoleUrl";
    public static final String CARBON_SERVER_URL = "CarbonServerUrl";
    public static final String ESB_HTTP_URL = "ESBHttpUrl";
    public static final String ESB_HTTPS_URL = "ESBHttpsUrl";

    /*
     *  StandaloneDeployment property define whether to skip CApp deployment by the test case or not
     *  If true, test case will deploy the CApp
     *  If false, infra will take care CApp deployment
     */
    public static final String STANDALONE_DEPLOYMENT = "StandaloneDeployment";

    protected static final int ARTIFACT_DEPLOYMENT_WAIT_TIME_MS = 120000;
    protected static final String resourceLocation = System.getProperty("test.resources.dir");

    protected Properties infraProperties;
    protected String backendURL;
    protected String esbHttpUrl;
    protected String serviceURL;
    protected String securedServiceURL;
    protected String sessionCookie;
    protected boolean standaloneMode;

    protected CarbonAppUploaderClient carbonAppUploaderClient = null;
    protected ApplicationAdminClient applicationAdminClient = null;

    /**
     * Initialize testcase
     *
     * @throws Exception
     */
    public void init() throws Exception {
        infraProperties = getDeploymentProperties();

        backendURL = infraProperties.getProperty(CARBON_SERVER_URL) +
                (infraProperties.getProperty(CARBON_SERVER_URL).endsWith("/") ? "" : "/");
        serviceURL = infraProperties.getProperty(ESB_HTTP_URL) +
                (infraProperties.getProperty(ESB_HTTP_URL).endsWith("/") ? "" : "/");
        securedServiceURL = infraProperties.getProperty(ESB_HTTPS_URL) +
                (infraProperties.getProperty(ESB_HTTPS_URL).endsWith("/") ? "" : "/");

        standaloneMode = Boolean.valueOf(infraProperties.getProperty(STANDALONE_DEPLOYMENT));

        setKeyStoreProperties();

        // login
        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);
        sessionCookie = authenticatorClient.login("admin", "admin", getServerHost());

        log.info("Service URL: " + serviceURL + " | Secured Service URL: " + securedServiceURL);
        log.info("The Backend service URL : " + backendURL + ". session cookie: " + sessionCookie);

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


    public String getApiInvocationURLHttp(String resourcePath) {
        return serviceURL + (resourcePath.startsWith("/") ? "" : "/") + resourcePath;
    }

    public String getApiInvocationURLHttps(String resourcePath) {
        return securedServiceURL + (resourcePath.startsWith("/") ? "" : "/") + resourcePath;
    }

    protected String getProxyServiceURLHttp(String proxyServiceName) {
        return serviceURL + "services" + (proxyServiceName.startsWith("/") ? "" : "/") + proxyServiceName;
    }

    protected String getProxyServiceURLHttps(String proxyServiceName) {
        return securedServiceURL + "services" + (proxyServiceName.startsWith("/") ? "" : "/") + proxyServiceName;
    }

    /**
     * Function to upload carbon application
     *
     * @param carFileName
     * @return
     * @throws RemoteException
     */
    public void deployCarbonApplication(String carFileName) throws RemoteException {

        if (standaloneMode) {
            // If standalone mode, deploy the CAPP to the server
            String cappFilePath = resourceLocation + File.separator + "artifacts" +
                    File.separator + carFileName + ".car";

            carbonAppUploaderClient = new CarbonAppUploaderClient(backendURL, sessionCookie);
            DataHandler dh = new DataHandler(new FileDataSource(new File(cappFilePath)));
            // Upload carbon application
            carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName + ".car", dh);

            applicationAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);

            // Wait for Capp to sync
            log.info("Waiting for Carbon Application deployment ..");
            Awaitility.await()
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .atMost(ARTIFACT_DEPLOYMENT_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                    .until(isCAppDeployed(applicationAdminClient, carFileName));
        }
    }

    /**
     * Function to undeploy carbon application
     *
     * @param applicationName
     * @throws ApplicationAdminExceptionException
     * @throws RemoteException
     */
    public void undeployCarbonApplication(String applicationName)
            throws ApplicationAdminExceptionException, RemoteException {
        applicationAdminClient.deleteApplication(applicationName);

        // Wait for Capp to undeploy
        Awaitility.await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .atMost(ARTIFACT_DEPLOYMENT_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                .until(isCAppUnDeployed(applicationAdminClient, applicationName));
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

    protected void setKeyStoreProperties() {
        System.setProperty("javax.net.ssl.trustStore", resourceLocation + "/keystores/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }



    private String getServerHost() {
        String bucketLocation = System.getenv("DATA_BUCKET_LOCATION");
        log.info("Data Bucket location is set : " + bucketLocation);
        String url = infraProperties.getProperty(MGT_CONSOLE_URL);
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

    protected List<Object[]> getRequestResponseList(String folderName) throws Exception {
        File requestFolder = new File(getClass().getResource(File.separator + "source_files" +
                                                             File.separator + folderName +
                                                             File.separator + "request").getPath());
        File responseFolder = new File(getClass().getResource(File.separator + "source_files" +
                                                              File.separator + folderName +
                                                              File.separator + "response").getPath());

        List<String> requestFiles = getListOfFiles(requestFolder);
        List<String> responseFiles = getListOfFiles(responseFolder);

        java.util.Collections.sort(requestFiles, Collator.getInstance());
        java.util.Collections.sort(responseFiles, Collator.getInstance());

        ArrayList<String> requestArray = new ArrayList();
        ArrayList<String> responseArray = new ArrayList();


        for (String file : requestFiles) {
            File fileLocation = new File(getClass().getResource(File.separator + "source_files" +
                                                                File.separator + folderName +
                                                                File.separator + "request" +
                                                                File.separator + file).getPath());
            String fileContent = getXmlContent(fileLocation);
            requestArray.add(fileContent);
        }

        for (String file : responseFiles) {
            String fileContent = readFile( getClass().getResource(File.separator + "source_files" +
                                                                  File.separator + folderName +
                                                                  File.separator + "response" +
                                                                  File.separator + file).getPath());
            responseArray.add(fileContent);
        }

        List<Object[]> requestResponseList = new ArrayList<>();

        for (int i = 0; i < requestArray.size(); i++) {
            String[] tmp = { requestArray.get(i) , responseArray.get(i) };
            requestResponseList.add(tmp);
        }
        return requestResponseList;
    }

    private String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    private List<String> getListOfFiles(File filePath) {
        File[] listOfFiles = filePath.listFiles();
        List<String> fileNames = new ArrayList<>();

        for (File file:listOfFiles) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    private String getXmlContent (File folderName) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(folderName);
        return getStringFromDocument(doc);
    }
}
