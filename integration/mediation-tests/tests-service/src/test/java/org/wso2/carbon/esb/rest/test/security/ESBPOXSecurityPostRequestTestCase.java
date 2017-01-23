/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.esb.rest.test.security;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.esb.rest.test.security.util.RestEndpointSetter;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/*
check pox security with post request
 */
public class ESBPOXSecurityPostRequestTestCase extends ESBIntegrationTest {
    private static String USER_GROUP = "everyone";
    private static final String SERVICE_NAME = "Axis2ServiceProxy";
    private SecurityAdminServiceClient securityAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        updateESBConfiguration(RestEndpointSetter.setEndpoint(File.separator + "artifacts" + File.separator + "ESB" +
                                                              File.separator + "synapseconfig" + File.separator + "rest" +
                                                              File.separator + "axis2-service-synapse.xml"));
        applySecurity("1", "Axis2ServiceProxy", getUserRole()[0]);
    }


    @Test(groups = {"wso2.esb"}, description = "POST request by super admin")
    public void testPOSTRequestBySuperAdmin()
            throws IOException, EndpointAdminEndpointAdminException,
                   LoginAuthenticationExceptionException,
                   XMLStreamException, XPathExpressionException {
        userInfo = context.getContextTenant().getTenantUserList().get(1);
        String securedRestURL = getProxyServiceURLHttps("Axis2ServiceProxy") + "/echoString";
        HttpsResponse response = HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                                                                            userInfo.getUserName(), userInfo.getPassword());
        assertTrue(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                               "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.esb"}, description = "POST request by user/tenant", dependsOnMethods = "testPOSTRequestBySuperAdmin")
    public void testPOSTRequestByUser() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);
        applySecurity("1", "Axis2ServiceProxy", getUserRole()[0]);
        String securedRestURL = getProxyServiceURLHttps("Axis2ServiceProxy") + "/echoString";
        HttpsResponse response = HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                                                                            userInfo.getUserName(), userInfo.getPassword());
        assertTrue(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                               "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.esb"}, description = "POST request by invalid user",
          dependsOnMethods = "testPOSTRequestByUser", expectedExceptions = IOException.class)
    public void testPOSTRequestByInvalidUser() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);
        applySecurity("1", "Axis2ServiceProxy", getUserRole()[0]);

        String securedRestURL = getProxyServiceURLHttps("Axis2ServiceProxy") + "/echoString";
        HttpsResponse response =
                HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                                                           "invalidUser", "InvalidPassword");
        assertFalse(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                                "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.esb"}, description = "Test post request by user belongs to unauthorized group",
          dependsOnMethods = "testPOSTRequestByInvalidUser", expectedExceptions = IOException.class)
    public void testPOSTRequestByGroup() throws Exception {
        String adminUserGroup = "admin";
        applySecurity("1", "Axis2ServiceProxy", adminUserGroup);
        String securedRestURL = getProxyServiceURLHttps("Axis2ServiceProxy") + "/echoString";
        HttpsResponse response =
                HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                                                           NonAdminUserCreationTestCase.getUser().getUserName()
                        , NonAdminUserCreationTestCase.getUser().getPassword());

        assertFalse(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                                "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    private void applySecurity(String scenarioNumber, String serviceName, String userGroup)
            throws Exception {

        securityAdminServiceClient = new SecurityAdminServiceClient
                (contextUrls.getBackEndUrl(), userInfo.getUserName(), userInfo.getPassword());

        String path = TestConfigurationProvider.getKeyStoreLocation();
        String KeyStoreName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
        if (userGroup != null) {
            USER_GROUP = userGroup;
        }
        securityAdminServiceClient.applySecurity(serviceName, scenarioNumber, new String[]{USER_GROUP},
                                                 new String[]{KeyStoreName}, KeyStoreName);
        Thread.sleep(2000);
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        securityAdminServiceClient.disableSecurity(SERVICE_NAME);
        super.cleanup();
    }
}
