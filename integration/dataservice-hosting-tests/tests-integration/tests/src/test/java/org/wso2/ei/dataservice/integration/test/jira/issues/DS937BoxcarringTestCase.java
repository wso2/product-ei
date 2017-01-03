/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This test was written to verify the fix for JIRA-0325
 */
public class DS937BoxcarringTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS937BoxcarringTestCase.class);
    private static final String serviceName = "BoxcarringTest";
    private String sessionID = null;
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serviceEndPoint = getServiceUrlHttp(serviceName);
        List<File> sqlFileLis = new ArrayList<>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        sqlFileLis.add(selectSqlFile("Customers.sql"));
        sqlFileLis.add(selectSqlFile("Employees.sql"));
        deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
                                                  "rdbms" + File.separator + "h2" + File.separator +
                                                  "BoxcarringTest.dbs", sqlFileLis));
    }

    @Test(groups = {"wso2.dss"} , dependsOnMethods = "testBoxcarringRollBackOperation")
    public void testBoxcarringOperation() throws IOException {
        beginBoxcarring();
        String response = selectOperation();
        Assert.assertTrue(!response.contains("Madhawa"));
        insertOperation();
        endBoxcarring();
        response = selectOperation();
        Assert.assertTrue(response.contains("Madhawa"));
        log.info("Begin Boxcarring Operation verified");
    }

    @Test(groups = {"wso2.dss"})
    public void testBoxcarringRollBackOperation() throws IOException {
        beginBoxcarring();
        String response = selectOperation();
        Assert.assertTrue(!response.contains("Madhawa"));
        insertOperation();
        rollbackBoxcarring();
        response = selectOperation();
        Assert.assertTrue(!response.contains("Madhawa"));
        log.info("Begin Boxcarring Operation verified");
    }

    private void beginBoxcarring() throws IOException {
        String endpoint = serviceEndPoint + ".SOAP11Endpoint/";
        String content = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dat=\"http://ws.wso2.org/dataservice\">\n" +
                         "   <soapenv:Header/>\n" +
                         "   <soapenv:Body>\n" +
                         "      <dat:begin_boxcar/>\n" +
                         "   </soapenv:Body>\n" +
                         "</soapenv:Envelope>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        headers.put("Content-Type", "text/plain");
        headers.put("SOAPAction", "\"urn:begin_boxcar\"");
        Object[] response = sendPOST(endpoint, content, headers);
        Assert.assertEquals(Integer.parseInt(response[0].toString()), 200);
    }

    private String selectOperation() throws IOException {
        String endpoint = serviceEndPoint + ".SOAP11Endpoint/";
        String content = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dat=\"http://ws.wso2.org/dataservice\">\n" +
                         "   <soapenv:Header/>\n" +
                         "   <soapenv:Body>\n" +
                         "      <dat:select_all_Employees_operation/>\n" +
                         "   </soapenv:Body>\n" +
                         "</soapenv:Envelope>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        headers.put("Content-Type", "text/plain");
        headers.put("SOAPAction", "\"urn:select_all_Employees_operation\"");
        Object[] response = sendPOST(endpoint, content, headers);
        Assert.assertEquals(Integer.parseInt(response[0].toString()), 200);
        return response[1].toString();
    }

    private String insertOperation() throws IOException {
        String endpoint = serviceEndPoint + ".SOAP11Endpoint/";
        String content ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dat=\"http://ws.wso2.org/dataservice\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <dat:insert_Employees_operation>\n" +
                        "         <dat:employeeNumber>1</dat:employeeNumber>\n" +
                        "         <dat:lastName>Gunasekara</dat:lastName>\n" +
                        "         <dat:firstName>Madhawa</dat:firstName>\n" +
                        "         <dat:extension>testdss</dat:extension>\n" +
                        "         <dat:email>madhawag@wso2.com</dat:email>\n" +
                        "         <dat:officeCode>1</dat:officeCode>\n" +
                        "         <dat:reportsTo>1</dat:reportsTo>\n" +
                        "         <dat:jobTitle>Software Engineer</dat:jobTitle>\n" +
                        "         <dat:salary>0.0</dat:salary>\n" +
                        "      </dat:insert_Employees_operation>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        headers.put("Content-Type", "text/plain");
        headers.put("SOAPAction", "\"urn:insert_Employees_operation\"");
        Object[] response = sendPOST(endpoint, content, headers);
        Assert.assertEquals(Integer.parseInt(response[0].toString()), 202);
        return response[1].toString();
    }

    private void endBoxcarring() throws IOException {
        String endpoint = serviceEndPoint + ".SOAP11Endpoint/";
        String content = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dat=\"http://ws.wso2.org/dataservice\">\n" +
                         "   <soapenv:Header/>\n" +
                         "   <soapenv:Body>\n" +
                         "      <dat:end_boxcar/>\n" +
                         "   </soapenv:Body>\n" +
                         "</soapenv:Envelope>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        headers.put("Content-Type", "text/plain");
        headers.put("SOAPAction", "\"urn:end_boxcar\"");
        Object[] response = sendPOST(endpoint, content, headers);
        Assert.assertEquals(Integer.parseInt(response[0].toString()), 200);
    }

    private void rollbackBoxcarring() throws IOException {
        String endpoint = serviceEndPoint + ".SOAP11Endpoint/";
        String content ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dat=\"http://ws.wso2.org/dataservice\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <dat:abort_boxcar/>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        headers.put("Content-Type", "text/plain");
        headers.put("SOAPAction", "\"urn:abort_boxcar\"");
        Object[] response = sendPOST(endpoint, content, headers);
        Assert.assertEquals(Integer.parseInt(response[0].toString()), 202);
    }
    public Object[] sendPOST(String endpoint, String content, Map<String, String> headers) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.socket.timeout", 120000);
        HttpPost httpPost = new HttpPost(endpoint);

        if(sessionID == null) {
            sessionID = "11";
        }
        headers.put("Cookie",sessionID);
        for (String headerType : headers.keySet()) {
            httpPost.setHeader(headerType, headers.get(headerType));
        }
        if (content != null) {
            HttpEntity httpEntity = new ByteArrayEntity(content.getBytes("UTF-8"));
            if (headers.get("Content-Type") == null) {
                httpPost.setHeader("Content-Type", "application/json");
            }
            httpPost.setEntity(httpEntity);
        }
        HttpResponse httpResponse = httpClient.execute(httpPost);
        Header[] responseHeaders = httpResponse.getHeaders("Set-Cookie");
        if (responseHeaders != null && responseHeaders.length > 0) {
            sessionID = responseHeaders[0].getValue();
        }
        if (httpResponse.getEntity() != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            return new Object[] { httpResponse.getStatusLine().getStatusCode(), response.toString() };
        } else {
            return new Object[] { httpResponse.getStatusLine().getStatusCode() };
        }
    }

    @AfterClass(alwaysRun = true)
    public void comQuoServiceDelete() throws Exception {
        deleteService("BoxcarringTest");
        log.info("CommodityQuote service deleted");
    }
}
