/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This test case is written to verify the fix for https://wso2.org/jira/browse/DS-1053
 * CARBON-15261
 * need carbon4.4.2
 */
public class CARBON15261JsonFormatterTest extends DSSIntegrationTest {
    private final String serviceName = "H2JsonSecureServiceTest";
    private String serviceEndPoint;
    private SimpleHttpClient client;
    Map<String, String> headers;
    private static final Log log = LogFactory.getLog(CARBON15261JsonFormatterTest.class);
    ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(dssContext);
        serverConfigurationManager.applyConfiguration(new File(getResourceLocation() + File.separator + "config" +
                                                               File.separator + "CARBON1352" + File.separator +
                                                               "axis2.xml"));
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        sqlFileLis.add(selectSqlFile("Offices.sql"));
        client = new SimpleHttpClient();
        headers = new HashMap<>();
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
                                                  "rdbms" + File.separator + "h2" + File.separator +
                                                  "H2JsonSecureServiceTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttps("H2JsonSecureServiceTest") + "/";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager.restartGracefully();
    }

    @Test(groups = "wso2.dss", description = "Invoking Request with GET method for secured service",
            dependsOnMethods = "performJsonGetWithoutSecurityAttributesTest")
    public void performJsonGetWithSecurityAttributesTest() throws Exception {
        this.secureService();
        headers.clear();
        headers.put("Accept", "application/json");
        String encode = (new String((new Base64()).encode((userInfo.getUserName() + ":" + userInfo.getPassword())
                                                                  .getBytes()))).replaceAll("\n", "");
        headers.put("Authorization", "Basic " + encode);
        HttpResponse response = client.doGet(serviceEndPoint + "nullm", headers);
        String responsePayload = client.getResponsePayload(response);
        Assert.assertTrue(responsePayload.contains("{\"status\":{\"null\":"),
                          "Response with attributes test failed for secured service");
    }

    @Test(groups = "wso2.dss", description = "Invoking Request with GET method for unsecured service")
    public void performJsonGetWithoutSecurityAttributesTest() throws Exception {
        headers.clear();
        headers.put("Accept", "application/json");
        HttpResponse response = client.doGet(serviceEndPoint + "nullm", headers);
        String responsePayload = client.getResponsePayload(response);
        Assert.assertTrue(responsePayload.contains("{\"status\":{\"null\":"),
                          "Response with attributes test failed for unsecured service");
    }

    @Test(groups = "wso2.dss", description = "Invoking Request with GET method for secured service",
            dependsOnMethods = "performJsonGetWithoutSecurityAttributesTest")
    public void performJsonGetWithSecurityTest() throws Exception {
        this.secureService();
        headers.clear();
        headers.put("Accept", "application/json");
        String encode = (new String((new Base64()).encode((userInfo.getUserName() + ":" + userInfo.getPassword())
                                                                  .getBytes()))).replaceAll("\n", "");
        headers.put("Authorization", "Basic " + encode);
        HttpResponse response = client.doGet(serviceEndPoint + "singleSpacem", headers);
        String responsePayload = client.getResponsePayload(response);
        Assert.assertTrue(responsePayload.contains("{\"status\":{\"null\":"), "Response failed for secured service");
    }

    private void secureService()
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException, InterruptedException,
                   XPathExpressionException {
        SecurityAdminServiceClient securityAdminServiceClient =
                new SecurityAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        securityAdminServiceClient.applySecurity(serviceName, Integer.toString(1) + "", new String[] { "admin" },
                                                 new String[] { "wso2carbon.jks" }, "wso2carbon.jks");
        log.info("Security Scenario Applied");
        Thread.sleep(6000);

    }
}
