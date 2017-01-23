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

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.esb.rest.test.security.util.RestEndpointSetter;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.extensions.utils.AutomationXpathConstants;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Verifying GET, PUT, POST and DELETE http methods with pox security with invalid user group.
 */
public class ESBPOXSecurityWithInvalidGroupTestCase extends ESBIntegrationTest {
    private static String USER_GROUP = "admin";
    private static final String SERVICE_NAME = "StudentServiceProxy";
    private static final String studentName = "automationStudent";
    private SecurityAdminServiceClient securityAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);

        updateESBConfiguration(RestEndpointSetter.setEndpoint(File.separator + "artifacts" + File.separator + "ESB" +
                                                              File.separator + "synapseconfig" + File.separator + "rest" +
                                                              File.separator + "student-service-synapse.xml"));
        applySecurity("1", "StudentServiceProxy", USER_GROUP);

    }


    @Test(groups = {"wso2.esb"}, description = "POST request  by user belongs to unauthorized group")
    public void testAddNewStudent() throws IOException, EndpointAdminEndpointAdminException,
                                           LoginAuthenticationExceptionException,
                                           XMLStreamException {
        //user  doesn't belong to admin group, so he doesn't have access permission for resources.

        String addStudentData = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                                "   <p:addStudent xmlns:p=\"http://axis2.apache.org\">\n" +
                                "      <!--0 to 1 occurrence-->\n" +
                                "      <ns:student xmlns:ns=\"http://axis2.apache.org\">\n" +
                                "         <!--0 to 1 occurrence-->\n" +
                                "         <xs:age xmlns:xs=\"http://axis2.apache.org\">100</xs:age>\n" +
                                "         <!--0 to 1 occurrence-->\n" +
                                "         <xs:name xmlns:xs=\"http://axis2.apache.org\">" + studentName + "</xs:name>\n" +
                                "         <!--0 or more occurrences-->\n" +
                                "         <xs:subjects xmlns:xs=\"http://axis2.apache.org\">testAutomation</xs:subjects>\n" +
                                "      </ns:student>\n" +
                                "   </p:addStudent>";


        String securedRestURL = getProxyServiceURLHttps(SERVICE_NAME) + "/students";
        boolean status = false;
        HttpsResponse response = null;
        try {
            response = HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, addStudentData,
                                                                  "application/xml", NonAdminUserCreationTestCase.getUser().getUserName(),
                                                                  NonAdminUserCreationTestCase.getUser().getPassword());
        } catch (IOException ignored) {
            status = true; // invalid users cannot post to the resource
        }

        assertTrue(status, "User belongs to invalid group was able to post to the resource");
        assertNull(response, "Response should be null");

        String studentGetUri = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;

        boolean getStatus = false;
        HttpsResponse getResponse = null;
        try {
            getResponse =
                    HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, NonAdminUserCreationTestCase.getUser().getPassword(),
                                                              NonAdminUserCreationTestCase.getUser().getPassword());
        } catch (IOException ignored) {
            getStatus = true; // invalid users cannot read the resource
        }
        assertTrue(getStatus, "User belongs to invalid group was able to get the resource");
        assertNull(getResponse, "Response cannot be null");


    }

    @Test(groups = {"wso2.esb"}, description = "PUT request by user belongs to unauthorized group",
          dependsOnMethods = "testAddNewStudent")
    public void testUpdateStudent() throws IOException, EndpointAdminEndpointAdminException,
                                           LoginAuthenticationExceptionException,
                                           XMLStreamException {

        String updateStudentData = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                                   "<p:updateStudent xmlns:p=\"http://axis2.apache.org\">\n" +
                                   "      <!--0 to 1 occurrence-->\n" +
                                   "      <ns:student xmlns:ns=\"http://axis2.apache.org\">\n" +
                                   "         <!--0 to 1 occurrence-->\n" +
                                   "         <xs:age xmlns:xs=\"http://axis2.apache.org\">999</xs:age>\n" +
                                   "         <!--0 to 1 occurrence-->\n" +
                                   "         <xs:name xmlns:xs=\"http://axis2.apache.org\">" + studentName + "</xs:name>\n" +
                                   "         <!--0 or more occurrences-->\n" +
                                   "         <xs:subjects xmlns:xs=\"http://axis2.apache.org\">testAutomationUpdated</xs:subjects>\n" +
                                   "      </ns:student>\n" +
                                   "</p:updateStudent>";

        String securedRestURL = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;

        boolean status = false;
        HttpsResponse response = null;
        try {
            response = HttpsURLConnectionClient.putWithBasicAuth(securedRestURL, updateStudentData,
                                                                 "application/xml", NonAdminUserCreationTestCase.getUser().getUserName(),
                                                                 NonAdminUserCreationTestCase.getUser().getPassword());
        } catch (IOException ignored) {
            status = true; // invalid users cannot put to the resource
        }

        assertTrue(status, "User belongs to invalid group was able to update the resource");
        assertNull(response, "Response should be null");


        //check whether the student updated.
        String studentGetUri = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;
        boolean getStatus = false;
        HttpsResponse getResponse = null;
        try {
            getResponse =
                    HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, NonAdminUserCreationTestCase.getUser().getPassword(),
                                                              NonAdminUserCreationTestCase.getUser().getPassword());
        } catch (IOException ignored) {
            getStatus = true; // invalid users cannot get to the resource
        }

        assertTrue(getStatus, "User belongs to invalid group was able to get the resource");
        assertNull(getResponse, "Response should be null");

    }

    @Test(groups = {"wso2.esb"}, description = "DELETE request by user belongs to unauthorized group",
          dependsOnMethods = "testUpdateStudent")
    public void testDeleteStudent() throws IOException, EndpointAdminEndpointAdminException,
                                           LoginAuthenticationExceptionException,
                                           XMLStreamException {

        String securedRestURL = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;
        boolean status = false;
        HttpsResponse response = null;
        try {
            response =
                    HttpsURLConnectionClient.deleteWithBasicAuth(securedRestURL, null, NonAdminUserCreationTestCase.getUser().getUserName(),
                                                                 NonAdminUserCreationTestCase.getUser().getPassword());
        } catch (IOException ignored) {
            status = true; // invalid users cannot delete to the resource
        }

        assertTrue(status, "User belongs to invalid group was able to delete the resource");
        assertNull(response, "Response should be null");
    }

    @Test(groups = {"wso2.esb"}, description = "GET resource after delete by user belongs to unauthorized group",
          dependsOnMethods = "testDeleteStudent")
    public void testGetResourceAfterDelete()
            throws IOException, EndpointAdminEndpointAdminException,
                   LoginAuthenticationExceptionException,
                   XMLStreamException {

        //check whether the student is deleted
        String studentGetUri = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;
        boolean getStatus = false;
        HttpsResponse getResponse = null;
        try {
            getResponse =
                    HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, NonAdminUserCreationTestCase.getUser().getPassword(),
                                                              NonAdminUserCreationTestCase.getUser().getPassword());
        } catch (IOException ignored) {
            getStatus = true; // invalid users cannot get the resource
        }

        assertTrue(getStatus, "User belongs to invalid group was able to get the resource");
        assertNull(getResponse, "Response should be null");
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
