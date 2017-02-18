/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.dataservice.integration.test.jira.issues;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/CARBON-15046
 * Retriving null values on GET in JSON objects
 */
public class DS1063EmailUsernameTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS1063EmailUsernameTestCase.class);

    private final String serviceName = "EmailUsername";
    private String serviceEndPoint;
    private ServerConfigurationManager serverConfigurationManager;
    private UserManagementClient userManagementClient;
    private String backendUrl;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateEmailUsersTable.sql"));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "samples" + File.separator
                        + "dbs" + File.separator + "rdbms" + File.separator
                        + serviceName + ".dbs", sqlFileLis));

        backendUrl = dssContext.getContextUrls().getBackEndUrl();

        /* login to the server as super user and add user with email user name for the test case */
        userManagementClient = new UserManagementClient(backendUrl,sessionCookie);

        userManagementClient.addRole("sampleRole", new String[]{},new String[]{"admin"});
        userManagementClient.addUser("emailUser@wso2.com","test123",new String[]{"sampleRole"},"emailUserProfile");

        serverConfigurationManager = new ServerConfigurationManager(dssContext);
        serverConfigurationManager.copyToComponentLib(new File(getResourceLocation()
                + File.separator + "jar" + File.separator
                + "msgContextHandler-1.0.0.jar"));

        String carbonHome = System.getProperty("carbon.home");
        File sourceFile = new File(getResourceLocation()
                + File.separator + "serverConfigs" + File.separator
                + "axis2.xml");
        File destinationFile = new File(carbonHome + File.separator + "repository" + File.separator + "conf" + File.separator + "axis2"+ File.separator + "axis2.xml");

        serverConfigurationManager.applyConfiguration(sourceFile, destinationFile);//this will restart the server as well
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(dssContext);
        sessionCookie = loginLogoutClient.login();

        serviceEndPoint = getServiceUrlHttp(serviceName);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"}, description = "Check whether email users can correctly access the data", alwaysRun = true)
    public void invokeServiceWithEmailUsernameTest() throws Exception {
        /* when invoked with correct username header with correct email address user name
        (for example "emailUser@wso2.com") it should return <employeeNumber> and <salary> elements */
        HttpResponse response1 = this.getHttpResponse(serviceEndPoint + "/_getemployees", "emailUser@wso2.com");
        assertTrue(response1.getData().contains("</employeeNumber>"));

        /* when invoked with incorrect user name header ( means wrong user name) then it shouldn't return
        <employeeNumber> and <salary> elements*/
        HttpResponse response2 = this.getHttpResponse(serviceEndPoint + "/_getemployees", "test");
        assertFalse(response2.getData().contains("</employeeNumber>"));

        HttpResponse response3 = this.getHttpResponse(serviceEndPoint + "/_getemployees", "admin");
        assertFalse(response3.getData().contains("</employeeNumber>"));
        log.info("email usernames correctly receives required fields");
    }

    /**
     * This method will return the http response for the request
     * @param endpoint service endpoint
     * @param username header
     * @return HttpResponse
     * @throws Exception
     */
    private HttpResponse getHttpResponse(String endpoint, String username) throws Exception {

        if (endpoint.startsWith("http://")) {
            String urlStr = endpoint;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setRequestProperty("username",username);
            conn.setRequestProperty("charset", "UTF-8");
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
}
