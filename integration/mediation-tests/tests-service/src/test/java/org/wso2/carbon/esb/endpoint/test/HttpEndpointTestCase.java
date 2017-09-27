/*
*Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.endpoint.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


public class HttpEndpointTestCase extends ESBIntegrationTest {

    private final String ENDPOINT_NAME = "HTTPEndpointTestEP";
    private EndPointAdminClient endPointAdminClient;
    private  static final String CUSTOMER_API_CONTEXT = "customerService";
    private static final String RESOURCE_CONTEXT = "/customer";
    private static final String customerId = "8fa3fc1b-f63c-4b21-8aff-3ac684c74d97";
    private static final String customerName = "John";
    private static final String updateCustomerName = "Emma";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "endpoint" + File.separator
                        + "httpEndpointConfig" + File.separator + "synapse.xml");

        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        endPointAdminClient = null;
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Test HTTP Endpoint addition, deletion & stats", priority = 1)
    public void testHttpEndpoint() throws Exception {
        cleanupEndpoints();
        endpointAdditionScenario();
        endpointStatisticsScenario();
        endpointDeletionScenario();
    }

    @Test(groups = { "wso2.esb" }, description = "HTTP Endpoint POST Test: RESTful", priority = 5)
    public void testToPost() throws IOException, Exception {

        String createCustomerData =
                "<createCustomer>\n" + "<id>" + customerId + "</id>\n" + "<name>" + customerName + "</name>\n"
                        + "</createCustomer>";
        StringReader customerData = new StringReader(createCustomerData);
        StringWriter postResponseData = new StringWriter();
        URL postRestURL = new URL((getApiInvocationURL(CUSTOMER_API_CONTEXT)) + RESOURCE_CONTEXT);
        HttpURLConnectionClient.sendPostRequest(customerData, postRestURL, postResponseData, "application/xml");
        assertTrue(postResponseData.toString().contains(customerName),
                "response doesn't contain the expected output but contains: " + postResponseData.toString());

    }

    @Test(groups = { "wso2.esb" }, description = "HTTP Endpoint GET test: RESTful", priority = 6)
    public void testToGet() throws IOException {

        String getRestURI = getApiInvocationURL(CUSTOMER_API_CONTEXT) + RESOURCE_CONTEXT + "/" + customerId;
        HttpResponse getResponseData = HttpURLConnectionClient.sendGetRequest(getRestURI, null);
        assertTrue(getResponseData.getData().contains(
                "<getCustomerResponse xmlns=\"http://ws.apache.org/ns/synapse\"><id>8fa3fc1b-f63c-4b21-8aff-3ac684c74d97</id><name>John</name></getCustomerResponse>"),
                "Unexpected output received:" + getResponseData.toString());

    }

    @Test(groups = {"wso2.esb"}, description = "HTTP Endpoint PUT Test: RESTful", priority = 7)
    public void testToPut() throws Exception {

        String updateCustomerData =
                "<createCustomer>\n" + "<id>" + customerId + "</id>\n" + "<name>" + updateCustomerName + "</name>\n"
                        + "</createCustomer>";
        StringReader sendUpdateData = new StringReader(updateCustomerData);
        StringWriter updateResponseData = new StringWriter();
        URL updateRestURL = new URL((getApiInvocationURL(CUSTOMER_API_CONTEXT)) + RESOURCE_CONTEXT);
        HttpURLConnectionClient.sendPutRequest(sendUpdateData, updateRestURL, updateResponseData, "application/xml");
        assertTrue(updateResponseData.toString().contains(updateCustomerName),
                "response contains unexpected output: " + updateResponseData.toString());

    }

    @Test(groups = {"wso2.esb"}, description = "HTTP Endpoint DELETE Test: RESTful", priority = 9)
    public void testToDelete() throws Exception {

        URL deleteRestURL = new URL((getApiInvocationURL(CUSTOMER_API_CONTEXT)) + RESOURCE_CONTEXT + "/" + customerId);

        HttpResponse response = HttpURLConnectionClient.sendDeleteRequest(deleteRestURL, null);
        assertEquals(response.getResponseCode(), 200, "Delete request was not successful.");
        assertTrue(response.getData().contains(
                "<deleteCustomerResponse xmlns=\"http://ws.apache.org/ns/synapse\"><return/></deleteCustomerResponse>"),
                "No wrapped response received");
    }

    @Test(groups = {"wso2.esb"}, description = "HTTP endpoint POST test: SOAP", priority = 2)
    public void testSendingToHttpEndpoint()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("HttpEndPointProxy")
                , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"),"Contains unexpected output: " +response.toString() );
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to HTTP Endpoint with invalid URI", priority = 3)
    public void testSendingToInvalidHttpEndpoint()
            throws IOException, EndpointAdminEndpointAdminException,
            LoginAuthenticationExceptionException,
            XMLStreamException {
        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("InvalidHttpEndPointProxy"),
                    getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof AxisFault,"Did not throw expected error condition for invalid endpoint.");
        }
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to an HTTP endpoint with missing uri.var variable", priority = 4)
    public void testSendingToNoVarHttpEndpoint()
            throws XMLStreamException, FileNotFoundException, AxisFault {
        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("MissingVariableEndPointProxy"),
                    getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof AxisFault,"Did not throw expected error condition for invalid endpoint.");
        }
    }

    private void cleanupEndpoints()
            throws RemoteException, EndpointAdminEndpointAdminException {
        String[] endpointNames = endPointAdminClient.getEndpointNames();
        List endpointList;
        if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
            endpointList = Arrays.asList(endpointNames);
            if (endpointList.contains(ENDPOINT_NAME)) {
                endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
            }
        }
    }

    private void endpointAdditionScenario()
            throws Exception {
        int beforeCount = endPointAdminClient.getEndpointCount();

        addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME + "\">\n" +
                "<http method=\"POST\"\n" +
                "              uri-template=\"http://localhost:9000/services/SimpleStockQuoteService\"/>" +
                "</endpoint>"));
        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(1, afterCount - beforeCount);

        String[] endpoints = endPointAdminClient.getEndpointNames();
        if (endpoints != null && endpoints.length > 0 && endpoints[0] != null) {
            List endpointList = Arrays.asList(endpoints);
            assertTrue(endpointList.contains(ENDPOINT_NAME));
        } else {
            fail("Endpoint has not been added to the system properly");
        }
    }

    private void endpointStatisticsScenario()
            throws RemoteException, EndpointAdminEndpointAdminException {
        endPointAdminClient.enableEndpointStatistics(ENDPOINT_NAME);
        String endpoint = endPointAdminClient.getEndpointConfiguration(ENDPOINT_NAME);
        assertTrue(endpoint.contains("statistics=\"enable"));
    }

    private void endpointDeletionScenario()
            throws RemoteException, EndpointAdminEndpointAdminException {
        int beforeCount = endPointAdminClient.getEndpointCount();
        endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(1, beforeCount - afterCount);
    }

}

