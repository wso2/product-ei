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
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.esb.rest.test.security.util.RestEndpointSetter;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Check GET, PUT, DELETE and POST request by invalid user credentials
 */
public class ESBPOXSecurityByInvalidUserTestCase extends ESBIntegrationTest {
    private static String USER_GROUP = "everyone";
    private static final String SERVICE_NAME = "StudentServiceProxy";
    private static final String studentName = "automationStudent";
    private SecurityAdminServiceClient securityAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        updateESBConfiguration(RestEndpointSetter.setEndpoint(File.separator + "artifacts" + File.separator + "ESB" +
                                                              File.separator + "synapseconfig" + File.separator + "rest" +
                                                              File.separator + "student-service-synapse.xml"));
        applySecurity("1", "StudentServiceProxy", null);
    }


    @Test(groups = {"wso2.esb"}, description = "POST request by invalid user", expectedExceptions = IOException.class)
    public void testAddNewStudent() throws IOException, EndpointAdminEndpointAdminException,
                                           LoginAuthenticationExceptionException,
                                           XMLStreamException {

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


        String securedRestURL = (getProxyServiceURLHttps(SERVICE_NAME)) + "/students";
        HttpsResponse response =
                HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, addStudentData, "application/xml",
                                                           "InvalidUser", "InvalidPassword");

        assertEquals(response.getResponseCode(), 202, "Expected response code doesn't found");
        assertTrue(!response.getData().contains(studentName)
                , "response doesn't contain the expected output");

        //check whether the student is added.
        String studentGetUri = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse getResponse =
                HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, userInfo.getPassword(),
                                                          userInfo.getPassword());
        assertTrue(getResponse.getData().contains("<ns:getStudentResponse xmlns:ns=\"http://axis2.apache.org\"><ns:return>" +
                                                  "<ns:age>100</ns:age>" +
                                                  "<ns:name>" + studentName + "</ns:name>" +
                                                  "<ns:subjects>testAutomation</ns:subjects>" +
                                                  "</ns:return></ns:getStudentResponse>"));

    }

    @Test(groups = {"wso2.esb"}, description = "PUT request  by invalid user", dependsOnMethods = "testAddNewStudent"
            , expectedExceptions = IOException.class)
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

        String securedRestURL = (getProxyServiceURLHttps(SERVICE_NAME)) + "/student/" + studentName;
        HttpsResponse response =
                HttpsURLConnectionClient.putWithBasicAuth(securedRestURL, updateStudentData,
                                                          "application/xml", "invalidUser",
                                                          "invalidPassword");
        assertEquals(response.getResponseCode(), 202, "Expected response code doesn't found");
        assertTrue(!response.getData().contains(studentName)
                , "response doesn't contain the expected output");

        //check whether the student is added.
        String studentGetUri = getProxyServiceURLHttps(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse getResponse =
                HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, userInfo.getPassword(),
                                                          userInfo.getPassword());
        assertTrue(getResponse.getData().contains("<ns:getStudentResponse xmlns:ns=\"http://axis2.apache.org\"><ns:return>" +
                                                  "<ns:age>999</ns:age>" +
                                                  "<ns:name>" + studentName + "</ns:name>" +
                                                  "<ns:subjects>testAutomationUpdated</ns:subjects>" +
                                                  "</ns:return></ns:getStudentResponse>"));
    }

    @Test(groups = {"wso2.esb"}, description = "DELETE request  by invalid user",
          dependsOnMethods = "testUpdateStudent")
    public void testDeleteStudent() throws IOException, EndpointAdminEndpointAdminException,
                                           LoginAuthenticationExceptionException,
                                           XMLStreamException {
        boolean status = false;
        HttpsResponse response = null;

        String securedRestURL = (getProxyServiceURLHttps(SERVICE_NAME)) + "/student/" + studentName;
        try {
            response =
                    HttpsURLConnectionClient.deleteWithBasicAuth(securedRestURL, null, "InvalidUser",
                                                                 "InvalidPassword");
        } catch (IOException ignored) {
            status = true; // invalid users cannot get the resource
        }
        assertTrue(status, "Invalid user was able to get the resource");
        assertNull(response, "Response should be null");

    }

    @Test(groups = {"wso2.esb"}, description = "GET resource after delete  by invalid user",
          dependsOnMethods = "testDeleteStudent")
    public void testGetResourceAfterDelete()
            throws IOException, EndpointAdminEndpointAdminException,
                   LoginAuthenticationExceptionException,
                   XMLStreamException {

        //check whether the student is deleted
        String studentGetUri = (getProxyServiceURLHttps(SERVICE_NAME)) + "/student/" + studentName;
        boolean getStatus = false;
        HttpsResponse getResponse = null;
        try {
            getResponse =
                    HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, userInfo.getPassword(),
                                                              userInfo.getPassword());
        } catch (IOException ignored) {
            getStatus = true; // invalid users cannot get the resource
        }

        assertTrue(getStatus, "User belongs to invalid group was able to get the resource");
        assertNull(getResponse, "Response should be null");
    }


    private void applySecurity(String scenarioNumber, String serviceName, String userGroup)
            throws Exception {

        securityAdminServiceClient = new SecurityAdminServiceClient
                (context.getContextUrls().getBackEndUrl(), userInfo.getUserName(), userInfo.getPassword());

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
