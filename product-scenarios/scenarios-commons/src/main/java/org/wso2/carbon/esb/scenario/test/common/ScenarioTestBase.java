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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.w3c.dom.Document;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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

    protected static final int ARTIFACT_DEPLOYMENT_WAIT_TIME_MS = 120000;
    protected static final String resourceLocation = System.getProperty("test.resources.dir");

    protected Properties infraProperties;
    protected String backendURL;
    protected String esbHttpUrl;
    protected String sessionCookie;

    protected CarbonAppUploaderClient carbonAppUploaderClient = null;
    protected ApplicationAdminClient applicationAdminClient = null;

    /**
     * Initialize testcase
     *
     * @throws Exception
     */
    public void init() throws Exception {
        infraProperties = getDeploymentProperties();
        setKeyStoreProperties();

        backendURL = infraProperties.getProperty(CARBON_SERVER_URL) + "/";

        // login
        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);
        sessionCookie = authenticatorClient.login("admin", "admin", getServerHost());
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

    /**
     * Function to upload carbon application
     *
     * @param carFileName
     * @return
     * @throws RemoteException
     */
    public void deployCarbonApplication(String carFileName) throws RemoteException {

        String cappFilePath = resourceLocation + File.separator + "artifacts" +
                File.separator + carFileName + ".car";

        carbonAppUploaderClient = new CarbonAppUploaderClient(backendURL, sessionCookie);
        DataHandler dh = new DataHandler(new FileDataSource(new File(cappFilePath)));
        // Upload carbon application
        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName + ".car", dh);

        applicationAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);

        // Wait for Capp to sync
        Awaitility.await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .atMost(ARTIFACT_DEPLOYMENT_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                .until(isCAppDeployed(applicationAdminClient, carFileName));
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
                String[] applicationList = applicationAdminClient.listAllApplications();
                if (applicationList != null) {
                    if (ArrayUtils.contains(applicationList, cAppName)) {
                        log.info("Carbon Application : " + cAppName + " Successfully deployed");
                        return true;
                    }
                }
                return false;
            }
        };
    }

    protected String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    protected List<String> getListOfFiles(File filePath){
        File[] listOfFiles = filePath.listFiles();
        List<String> fileNames = new ArrayList<>();

        for (File file:listOfFiles) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    protected String getXmlContent (File folderName) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(folderName);
        String fileContent = getStringFromDocument(doc);
        return fileContent;
    }
}
