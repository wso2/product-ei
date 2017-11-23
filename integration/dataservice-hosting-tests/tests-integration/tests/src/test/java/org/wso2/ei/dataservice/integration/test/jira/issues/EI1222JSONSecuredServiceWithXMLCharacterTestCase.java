/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/*
* This testcase tests the following scenario
* 1. There is an XML character exists in retrieved resultset from the dataservice
* 2. The & character is after 64 characters
* 3. The data service is secured through ws-security
* 4. The Accept header is application/json
* This is related to https://github.com/wso2/product-ei/issues/1222
 */
public class EI1222JSONSecuredServiceWithXMLCharacterTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(EI1222JSONSecuredServiceWithXMLCharacterTestCase.class);
    private final String serviceName = "SecuredJsonSample";
    private String serviceEndPoint;
    private ServerConfigurationManager serverConfigurationManager;
    private static final String INPUT_FACTORY_PROPERTIES_FILE = "XMLInputFactory.properties";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(dssContext);
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateTableJsonTest.sql"));
        sqlFileLis.add(selectSqlFile("StudentsJsonTest.sql"));
        addResource();
        copyXMLInputFactoryPropertiesFile();
        serverConfigurationManager.restartForcefully();
        super.init();
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator + "rdbms" +
                        File.separator + "h2" + File.separator + serviceName + ".dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttps(serviceName) + "/";
    }

    private void copyXMLInputFactoryPropertiesFile() throws Exception {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String propertiesSrcLocation = getResourceLocation()
                + File.separator + "resources" + File.separator + INPUT_FACTORY_PROPERTIES_FILE;
        try {
            FileUtils.copyFileToDirectory(new File(propertiesSrcLocation),
                    new File(carbonHome));
            log.info(INPUT_FACTORY_PROPERTIES_FILE + " is copied to $CARBON_HOME.");
        } catch (IOException exception) {
            throw new Exception("Exception occurred while copying the " + INPUT_FACTORY_PROPERTIES_FILE, exception);
        }
    }

    @Test(groups = "wso2.dss", description = "Testing json output with xml characters resides within configured " +
            "com.ctc.wstx.minTextSegment in XMLInputFactory.properties test case.")
    public void getJSONOutputWithXMLCharacter() throws Exception {
        HttpResponse response = this.getHttpResponse(serviceEndPoint + "studentData/001", "application/json");
        Assert.assertNotNull(response, "Response is null");
        String receivedResult = response.getData();
        log.info("Received response is: " + receivedResult);
        String expectedResult = "{\"Students\":{\"Student\":[{\"country\":\"US\",\"phone\":\"097774546546\",\"name\":" +
                "\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&\",\"state\":\"NY\"}]}}";
        Assert.assertNotNull(receivedResult, "Response is null");
        Assert.assertTrue(expectedResult.equals(receivedResult), "Expected result not found");
    }

    @AfterClass
    public void clean() throws Exception {
        deleteService(serviceName);
        cleanup();
        // Delete carbon_home/XMLInputFactory.properties
        FileUtils.forceDelete(new File(System.getProperty(ServerConstants.CARBON_HOME) +
                File.separator + INPUT_FACTORY_PROPERTIES_FILE));
        // restart server
        serverConfigurationManager.restartGracefully();
    }

    /**
     * This method will "Accept" header Types "application/json", etc..
     * @param endpoint service endpoint
     * @param contentType header type
     * @return HttpResponse
     * @throws Exception
     */
    private HttpResponse getHttpResponse(String endpoint, String contentType) throws Exception {

        if (endpoint.startsWith("https://")) {
            String urlStr = endpoint;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", contentType);
            conn.setRequestProperty("charset", "UTF-8");

            String encode = (new String((new Base64()).encode((userInfo.getUserName() + ":" + userInfo.getPassword())
                    .getBytes()))).replaceAll("\n", "");
            conn.setRequestProperty("Authorization", "Basic " + encode);

            conn.setReadTimeout(10000);
            conn.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            return new HttpResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }

    /**
     * This method add the Security policy which will be used with the service.
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws ResourceAdminServiceExceptionException
     * @throws XPathExpressionException
     */
    private void addResource()
            throws RemoteException, MalformedURLException, ResourceAdminServiceExceptionException,
            XPathExpressionException {
        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl()
                , sessionCookie);
        deleteResource();
        resourceAdmin.addResource("/_system/config/automation/resources/policies/SecPolicy-withRoles.xml",
                "text/comma-separated-values", "",
                new DataHandler(new URL("file:///" + getResourceLocation()
                        + File.separator + "resources" + File.separator
                        + "SecPolicy-withRoles.xml")));
    }

    /**
     * Delete the security policy from the registry.
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws ResourceAdminServiceExceptionException
     * @throws XPathExpressionException
     */
    private void deleteResource()
            throws RemoteException, MalformedURLException, ResourceAdminServiceExceptionException,
            XPathExpressionException {
        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl()
                , sessionCookie);
        resourceAdmin.deleteResource("/_system/config/automation/resources/policies/");
    }
}
