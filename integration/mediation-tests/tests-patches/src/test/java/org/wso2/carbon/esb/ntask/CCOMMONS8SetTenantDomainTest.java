/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.ntask;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Server startup shouldn't throw any FATAL errors when registry is mounted
 */
public class CCOMMONS8SetTenantDomainTest extends ESBIntegrationTest {

    public static final String ERROR_MESSAGE = "Failed to add the root collection to the coreRegistry";
    public static final String ERROR_TYPE = "FATAL";
    private Map<String, String> startupParameterMap;
    private TestServerManager testServerManager;
    private AutomationContext regTestContext;
    private LogViewerClient logViewerClient;
    private final String COMMON_FILE_LOCATION = File.separator + "artifacts" + File.separator + "ESB" + File.separator + "registry" + File.separator;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        regTestContext = new AutomationContext("ESB", "esbsRegTest", TestUserMode.SUPER_TENANT_ADMIN);

        startupParameterMap = new HashMap<String, String>();
        startupParameterMap.put("-DportOffset", "230");

        String backEndRegUrl = "https://" + context.getInstance().getHosts().get("default") + ":" + context.getInstance().getPorts().get("https") + "/registry";

        changeRegistryFile(getClass().getResource(COMMON_FILE_LOCATION + "registry-template.xml").getPath(), backEndRegUrl);

        testServerManager = new TestServerManager(regTestContext, null, startupParameterMap) {

            public void configureServer() throws AutomationFrameworkException {

                try {
                    File sourceFile = new File(getClass().getResource(COMMON_FILE_LOCATION + "registry.xml").getPath());

                    //copying registry.xml file to conf folder
                    FileManager.copyFile(sourceFile, this.getCarbonHome() + File.separator + "repository" + File.separator + "conf" + File.separator + "registry.xml");
                } catch (IOException e) {
                    throw new AutomationFrameworkException(e.getMessage(), e);
                }
            }
        };

    }

    /**
     * Starting up the server and check the logs for error.
     */
    @Test(groups = "wso2.esb", description = "Test startup logs to see whether ntask FATAL exception occurred", enabled = true)
    public void testStartupNtaskErrorTest() {

        try {
            testServerManager.startServer();
            logViewerClient = new LogViewerClient(regTestContext.getContextUrls().getBackEndUrl(), userInfo.getUserName(), userInfo.getPassword());
            LogEvent[] events = logViewerClient.getAllRemoteSystemLogs();
            for (LogEvent event : events) {
                if (event.getPriority().equals(ERROR_TYPE) && event.getMessage().contains(ERROR_MESSAGE)) {
                    Assert.fail("Tenant domain not set exception occurred in startup");
                }
            }
        } catch (AutomationFrameworkException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (XPathExpressionException e) {
            Assert.fail(e.getMessage());
        } catch (LogViewerLogViewerException e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        try {
            super.cleanup();
        } finally {
            testServerManager.stopServer();
        }
    }

    /**
     * Helper method to change the remote instance url of the registry mount in registry.xml file
     *
     * @param path
     * @param url
     */
    private void changeRegistryFile(String path, String url) {
        try {
            File inputFile = new File(path);
            DocumentBuilderFactory docFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder =
                    docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputFile);
            Node remoteInstance = doc.getElementsByTagName("remoteInstance").item(0);
            // update remoteInstance attribute
            NamedNodeMap attr = remoteInstance.getAttributes();
            Node nodeAttr = attr.getNamedItem("url");
            nodeAttr.setTextContent(url);


            // write the content on registry.xml file
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult consoleResult = new StreamResult(new FileOutputStream(path.substring(0, path.length() - 21) + "registry.xml"));
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            System.out.println("Error in changing the registry.xml file");
            e.printStackTrace();
            Assert.fail("Error in modifying registry.xml file as required");
        }
    }

}
