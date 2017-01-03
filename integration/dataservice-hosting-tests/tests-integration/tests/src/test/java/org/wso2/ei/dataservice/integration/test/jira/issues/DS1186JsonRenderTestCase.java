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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.ResourceAdminServiceClient;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * This test case is written to verify the fix for https://wso2.org/jira/browse/DS-1053
 */
public class DS1186JsonRenderTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(DS1186JsonRenderTestCase.class);

    private final String serviceName = "JsonRenderService";
    private String serviceEndPoint;
    Map<String, String> headers;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        addResource();
        deployService(serviceName,
                new DataHandler(new URL("file:///" + getResourceLocation() + File.separator + "samples" + File.separator + "dbs" + File.separator + "rdbms"
                        + File.separator + "JsonRenderService.dbs")));
        serviceEndPoint = getServiceUrlHttps(serviceName) + "/";
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        deleteResource();
        cleanup();
    }

    @Test(groups = {"wso2.dss"}, description = "Check whether the JSON render service works when there is '&'", alwaysRun = true)
    public void jsonRenderWithSecurity() throws Exception {
        HttpResponse response = this.getHttpResponse(serviceEndPoint + "status", "application/json");
        String receivedResult=response.getData();
        String expectedResult = "{\"Entries\":{\"Entry\":[{\"status\":\"1 & 2\"}]}}";
        Assert.assertNotNull(receivedResult, "Response is null");
        Assert.assertTrue(expectedResult.equals(receivedResult.toString()), "Expected result not found");
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
