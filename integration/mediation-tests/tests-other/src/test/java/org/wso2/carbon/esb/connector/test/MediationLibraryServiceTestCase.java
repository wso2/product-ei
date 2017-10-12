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
package org.wso2.carbon.esb.connector.test;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.mediation.library.stub.types.carbon.LibraryInfo;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.net.URL;

/**
 * Test the functionality of MediationLibraryServiceComponent
 */
public class MediationLibraryServiceTestCase extends ESBIntegrationTest {
    private static final String HELLO_CONNECTOR_ZIP = "hello-connector-1.0.0-SNAPSHOT.zip";
    private static final String HELLO_CONNECTOR_LIB_QNAME = "{org.wso2.carbon.connector}hello";
    private static final String HELLO_LIB_NAME = "hello";

    private static final String AMAZON_CONNECTOR_ZIP = "amazons3-connector-1.0.4.zip";
    private static final String AMAZON_CONNECTOR_LIB_QNAME = "{org.wso2.carbon.connector}amazons3";
    private static final String AMAZON_LIB_NAME = "amazons3";

    private static final String TEMP_CONNECTOR_LIB_QNAME = "{org.wso2.carbon.connector}temp";
    private static final String TEMP_LIB_NAME = "temp";

    private static final String FAULTY_CONNECTOR_ZIP_NAME = "helloworld-connector-1.0.5-SNAPSHOT.zip";
    private static final String FAULTY_CONNECTOR_LIB_QNAME = "{org.wso2.carbon.connector}helloworld";
    private static final String FAULTY_LIB_NAME = "helloworld";

    private static final String PACKAGE_NAME = "org.wso2.carbon.connector";
    private static final String ENABLED = "enabled";
    private static final String CONNECTOR = "connector";
    private static final String INVALID_CONNECTOR = "invalid-connector";

    private ServerConfigurationManager serverConfigurationManager;
    private String resourcePath;

    /**
     * Deploy and enable the hello connector.
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        resourcePath = getESBResourceLocation().replace("//", "/") + File.separator + CONNECTOR;

        //upload connector
        uploadConnector(resourcePath, HELLO_CONNECTOR_ZIP);
        Thread.sleep(20000);

        //enable connector
        updateConnectorStatus(HELLO_CONNECTOR_LIB_QNAME, HELLO_LIB_NAME, PACKAGE_NAME, ENABLED);
        Thread.sleep(10000);

        serverConfigurationManager.restartGracefully();
        super.init();
    }

    /**
     * Test for connector deploying, enabling and deleting.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test connector deploying, enabling and deleting.")
    public void deployEnableDeleteConnectorTest() throws Exception {

        //deploy connector
        uploadConnector(resourcePath, AMAZON_CONNECTOR_ZIP);
        Thread.sleep(20000);
        Assert.assertFalse(checkAvailabilityInImports(AMAZON_CONNECTOR_LIB_QNAME),
                           "Connector is already enabled at the deployment.");

        //enable connector
        updateConnectorStatus(AMAZON_CONNECTOR_LIB_QNAME, AMAZON_LIB_NAME, PACKAGE_NAME, ENABLED);
        Thread.sleep(5000);
        Assert.assertTrue(checkAvailabilityInImports(AMAZON_CONNECTOR_LIB_QNAME),
                          "Connector is still in the disable state after enable action.");

        //remove connector
        deleteLibrary(AMAZON_CONNECTOR_LIB_QNAME);
        Thread.sleep(5000);
        Assert.assertFalse(checkAvailabilityInImports(AMAZON_CONNECTOR_LIB_QNAME),
                           "Connector is still available in the imports list after remove action.");
    }

    /**
     * Test for functionality related to imports including add, get and delete.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test add, get and delete functionality for imports.")
    public void importsFunctionalityTest() throws Exception {

        String expectedImport = "<import xmlns=\"http://ws.apache.org/ns/synapse\" name=\"hello\" package=\"org.wso2" +
                ".carbon.connector\" status=\"enabled\"/>";

        //add import
        Assert.assertFalse(checkAvailabilityInImports(TEMP_CONNECTOR_LIB_QNAME),
                           "Import is already available before add action.");

        addImport(TEMP_LIB_NAME, PACKAGE_NAME);
        Assert.assertTrue(checkAvailabilityInImports(TEMP_CONNECTOR_LIB_QNAME),
                          "Import is not available after add action.");

        //get import
        Assert.assertEquals(getImport(HELLO_CONNECTOR_LIB_QNAME), expectedImport,
                            "Received synapse configuration for hello-connector is incorrect.");

        try {
            getImport(INVALID_CONNECTOR);
            Assert.fail("Get import fails for an invalid connector.");
        } catch (AxisFault e) {
            Assert.assertEquals(e.getMessage(), "Library Import null does not exist");
        }

        //delete import
        deleteImport(TEMP_CONNECTOR_LIB_QNAME);
        Assert.assertFalse(checkAvailabilityInImports(TEMP_CONNECTOR_LIB_QNAME),
                           "Import is still available after delete action.");
    }

    /**
     * Test for library information.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test retrieving library information.")
    public void libraryInfoTest() throws Exception {

        LibraryInfo[] allLibraryInfo = getAllLibraryInfo();
        LibraryInfo helloLibraryInfo = null;

        if (null != allLibraryInfo && allLibraryInfo.length > 0) {
            for (int i = 0; i < allLibraryInfo.length; i++) {
                if (allLibraryInfo[i].getLibName().equals(HELLO_LIB_NAME)) {
                    helloLibraryInfo = allLibraryInfo[i];
                    break;
                }
            }
        }

        if (null != helloLibraryInfo) {
            Assert.assertEquals(helloLibraryInfo.getDescription(), "WSO2 ESB Custom Connector Library Hello.",
                                "Recieved description for hello connector is incorrect.");
            Assert.assertEquals(helloLibraryInfo.getQName(), HELLO_CONNECTOR_LIB_QNAME,
                                "Recieved QName for hello connector is incorrect.");
        } else {
            Assert.fail("Hello connector does not exist in library list.");
        }

        LibraryInfo libraryInfo = getLibraryInfo(HELLO_LIB_NAME, PACKAGE_NAME);
        if (null != libraryInfo) {
            Assert.assertEquals(helloLibraryInfo.getPackageName(), PACKAGE_NAME,
                                "Recieved package name for hello connector is incorrect.");
            Assert.assertTrue(helloLibraryInfo.getStatus(), "Recieved status for hello connector is incorrect.");
        } else {
            Assert.fail("Failure at retrieving hello library information.");
        }
    }

    /**
     * Test for list of available libraries.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test retrieving libraries list.")
    public void getLibrariesTest() throws Exception {

        String[] libraries = getAllLibraries();
        boolean isHelloExists = false;

        if (null != libraries && libraries.length > 0) {
            for (int i = 0; i < libraries.length; i++) {
                if (libraries[i].equals(HELLO_CONNECTOR_LIB_QNAME)) {
                    isHelloExists = true;
                    break;
                }
            }
            Assert.assertTrue(isHelloExists, "Hello connector does not exist in library list.");
        } else {
            Assert.fail("Hello connector does not exist in library list.");
        }
    }

    /**
     * Test for invoking connector service.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test connector upload and invoke")
    public void invokeConnectorTest() throws Exception {

        String expectedOutput = "<message>Bob</message>";

        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "connector" + File.separator +
                        "MediationLibraryServiceTestAPI.xml");
        Thread.sleep(5000);

        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL("http://localhost:8480/library-service/get-message"),
                                                           "");
        Assert.assertEquals(httpResponse.getData(), expectedOutput, "Invoking hello connector fails");
    }

    /**
     * Test for faulty connector.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test faulty connector")
    public void uploadFaultyConnectorTest() throws Exception {

        try {
            uploadConnector(getESBResourceLocation().replace("//", "/") + File.separator + "connector",
                            FAULTY_CONNECTOR_ZIP_NAME);
            Thread.sleep(20000);

            updateConnectorStatus(FAULTY_CONNECTOR_LIB_QNAME, FAULTY_LIB_NAME, PACKAGE_NAME, ENABLED);
            deleteLibrary(FAULTY_CONNECTOR_LIB_QNAME);

            Assert.fail("Test fails for deploying faulty connector.");
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                                "No Mediation Library found of the name : {org.wso2.carbon.connector}helloworld");
        }
    }

    /**
     * Check for availability of library names with in the list of imports.
     *
     * @param libName
     * @return
     */
    private boolean checkAvailabilityInImports(String libName) {
        try {
            String[] imports = getAllImports();
            if (imports != null) {
                for (String anImport : imports) {
                    if (anImport.contains(libName)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * Delete the hello connector.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        deleteLibrary(HELLO_CONNECTOR_LIB_QNAME);
        Thread.sleep(5000);
    }
}
