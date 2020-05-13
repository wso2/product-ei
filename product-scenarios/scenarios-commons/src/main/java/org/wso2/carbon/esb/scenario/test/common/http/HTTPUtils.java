/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.http;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import org.json.JSONObject;

import org.testng.Assert;

import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.StringUtil;
import org.wso2.carbon.esb.scenario.test.common.utils.XMLUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Util class for HTTP Client side functions
 */
public class HTTPUtils {

    /**
     * Returns HTTP_SC code of the response.
     *
     * @param response HttpResponse to get status code from
     * @return  HTTP_SC code as an int
     */
    public static int getHTTPResponseCode(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Extracts the payload from a HTTP response. For a given HttpResponse object, this
     * method can be called only once.
     *
     * @param response HttpResponse instance to be extracted
     * @return Content payload
     * @throws IOException If an error occurs while reading from the response
     */
    public static String getResponsePayload(HttpResponse response) throws IOException {
        String responseAsString = "";
        if (response.getEntity() != null) {
            InputStream in = response.getEntity().getContent();
            int length;
            byte[] tmp = new byte[2048];
            StringBuilder buffer = new StringBuilder();
            while ((length = in.read(tmp)) != -1) {
                buffer.append(new String(tmp, 0, length));
            }
            responseAsString = buffer.toString();
        }
        return responseAsString;
    }


    /**
     * Returns AXIOM OM Element representation of a HTTP response.
     *
     * @param response HttpResponse to convert
     * @return AXIOM OM Element representation if convertible
     * @throws IOException on an issue when converting HttpResponse to a String representation
     * @throws XMLStreamException on HttpResponse is not convertible
     */
    public static OMElement getOMFromResponse(HttpResponse response) throws IOException, XMLStreamException {
        return AXIOMUtil.stringToOM(getResponsePayload(response));
    }

    /**
     * Sets HTTP headers to the HTTP request.
     * @param headers HTTP headers to set as a map <header name, value>
     * @param request HttpUriRequest to set headers
     */
    public static void setHTTPHeaders(Map<String,String> headers, HttpUriRequest request) {
        if(headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                setHTTPHeader(headerEntry.getKey(), headerEntry.getValue(), request);
            }
        }
    }

    /**
     * Sets a HTTP header to the HTTP request.
     *
     * @param key HTTP header's name to set
     * @param value HTTP header's value to set
     * @param request HttpUriRequest to set header
     */
    public static void setHTTPHeader(String key, String value, HttpUriRequest request) {
        request.setHeader(key, value);
    }

    /**
     * Returns value of a given header from HTTP request
     *
     * @param headerKey name of header to get value for
     * @param request HttpUriRequest
     * @return value of header as a String
     */
    public static String getHeaderValue(String headerKey, HttpUriRequest request) {
        String headerVal = null;
        Header[] encodingHeader = request.getHeaders(headerKey);
        if(encodingHeader != null && encodingHeader.length > 0) {
            HeaderElement[] headerElements = encodingHeader[0].getElements();
            headerVal =  headerElements[0].getValue();
        }
        return headerVal;
    }

    /**
     * Invoke SOAP Action and Assert
     *
     * @param serviceUrl proxy service url
     * @param request the request to transform
     * @param messageId messageId to be set as headers
     * @param expectedResponse expected response from the after transformation
     * @param statusCode expected status code
     * @param soapAction SOAP Action
     * @param testcaseName testcase name to be logged during a test failure
     * @throws IOException
     */
    public static void invokeSoapActionAndAssert(String serviceUrl, String request, String messageId,
                                                 String expectedResponse, int statusCode, String soapAction,
                                                 String testcaseName) throws IOException, XMLStreamException {
        SOAPClient soapClient = new SOAPClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, messageId);

        HttpResponse httpResponse = soapClient.sendSimpleSOAPMessage(serviceUrl, request, soapAction, headers);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode, testcaseName + " failed");
        OMElement actualOMResponse = HTTPUtils.getOMFromResponse(httpResponse);
        OMElement expectedOMResponse = XMLUtils.StringASOM(expectedResponse);
        Assert.assertNotNull(actualOMResponse, "Invalid response");
        boolean compareResponse = XMLUtils.compareOMElements(expectedOMResponse, actualOMResponse);
        Assert.assertTrue(compareResponse,"Expected : <" + expectedResponse + "> but was <" + actualOMResponse
                                          + "> in test case : " + messageId);
    }

    /**
     * Invoke SOAP Action and Assert
     *
     * @param serviceUrl proxy service url
     * @param request the request to transform
     * @param messageId messageId to be set as headers
     * @param expectedResponse expected response from the after transformation
     * @param statusCode expected status code
     * @param soapAction SOAP Action
     * @param testcaseName testcase name to be logged during a test failure
     * @param connectionTimeout Connection timeout for the SOAP Client
     * @param socketTimeout Socket timeout for the SOAP Client
     * @throws IOException
     */
    public static void invokeSoapActionAndAssert(String serviceUrl, String request, String messageId,
                                                 String expectedResponse, int statusCode, String soapAction,
                                                 String testcaseName, int connectionTimeout, int socketTimeout)
            throws IOException, XMLStreamException {

        SOAPClient soapClient = new SOAPClient(connectionTimeout, socketTimeout);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, messageId);

        HttpResponse httpResponse = soapClient.sendSimpleSOAPMessage(serviceUrl, request, soapAction, headers);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode, testcaseName + " failed");
        OMElement actualOMResponse = HTTPUtils.getOMFromResponse(httpResponse);
        OMElement expectedOMResponse = XMLUtils.StringASOM(expectedResponse);
        Assert.assertNotNull(actualOMResponse, "Invalid response");
        boolean compareResponse = XMLUtils.compareOMElements(expectedOMResponse, actualOMResponse);
        Assert.assertTrue(compareResponse, "Expected : <" + expectedResponse + "> but was <" + actualOMResponse
                + "> in test case : " + messageId);
    }

    /**
     * Invoke SOAP Action and Assert for String contains
     *
     * @param serviceUrl proxy service url
     * @param request the request to transform
     * @param messageId messageId to be set as headers
     * @param responseSubstring String Element to check whether the response contains
     * @param statusCode expected status code
     * @param soapAction SOAP Action
     * @param testcaseName testcase name to be logged during a test failure
     * @throws IOException
     */
    public static void invokeSoapActionAndCheckContains(String serviceUrl, String request, String messageId,
                                                        String responseSubstring, int statusCode,
                                                        String soapAction, String testcaseName) throws IOException {
        SOAPClient soapClient = new SOAPClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, messageId);

        HttpResponse httpResponse = soapClient.sendSimpleSOAPMessage(serviceUrl, request, soapAction, headers);
        String responsePayload = getResponsePayload(httpResponse);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode, testcaseName + " failed");
        Assert.assertTrue(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload).contains(responseSubstring),
                          "\""+ responseSubstring + "\" does not contain in actual Response Recieved : <" +
                          responsePayload + "> in test case : " + messageId);
    }

    /**
     * Invoke API and returns response
     *
     * @param url api invocation url
     * @param request the request to transform
     * @param contentType content-type of the request
     * @return extracted payload from the returned http Response
     * @throws IOException
     */
    public static JSONObject invokeApiAndGetResponse(String url, String request, String contentType) throws IOException {
        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doPost(url, request, contentType);
        String responsePayload = getResponsePayload(httpResponse);
        return new JSONObject(responsePayload);
    }

    /**
     * Invoke a pox action and assert
     *
     * @param serviceUrl proxy service url
     * @param request the request to transform
     * @param contentType content-type of the request
     * @param messageId message id to be sent a header
     * @param expectedResponse expected response after transformation
     * @param statusCode expected status code
     * @param testcaseName testcase name to be logged during a test failure
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void invokePoxEndpointAndAssert(String serviceUrl, String request, String contentType, String messageId,
                                               String expectedResponse, int statusCode, String testcaseName)
            throws IOException, XMLStreamException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, messageId);

        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doPost(serviceUrl, headers, request, contentType);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode, testcaseName + " failed");

        OMElement actualOMResponse = HTTPUtils.getOMFromResponse(httpResponse);
        OMElement expectedOMResponse = XMLUtils.StringASOM(expectedResponse);

        Assert.assertNotNull(actualOMResponse, "Invalid response");

        boolean compareResponse = XMLUtils.compareOMElements(expectedOMResponse, actualOMResponse);
        Assert.assertTrue(compareResponse,"Expected : <" + expectedResponse + "> but was <" + actualOMResponse
                                          + "> in test case : " + messageId);
    }

    /**
     * Invoke a pox action and assert
     *
     * @param serviceUrl proxy service url
     * @param request the request to transform
     * @param contentType content-type of the request
     * @param messageId message id to be sent a header
     * @param expectedResponse expected response after transformation
     * @param nextExpectedResponse next expected response after transformation
     * @param statusCode expected status code
     * @param testcaseName testcase name to be logged during a test failure
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void invokePoxEndpointAndAssertTwoPayloads(String serviceUrl, String request, String contentType,
                                                             String messageId,
                                                             String expectedResponse, String nextExpectedResponse,
                                                             int statusCode, String testcaseName)
            throws IOException, XMLStreamException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, messageId);

        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doPost(serviceUrl, headers, request, contentType);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode, testcaseName + " failed");

        OMElement actualOMResponse = HTTPUtils.getOMFromResponse(httpResponse);
        OMElement expectedOMResponse = XMLUtils.StringASOM(expectedResponse);
        boolean compareNextResponse = false;
        boolean compareResponse = XMLUtils.compareOMElements(expectedOMResponse, actualOMResponse);
        if (!compareResponse) {
            OMElement nextExpectedOMResponse = XMLUtils.StringASOM(nextExpectedResponse);
            compareNextResponse = XMLUtils.compareOMElements(nextExpectedOMResponse, actualOMResponse);
        }

        Assert.assertNotNull(actualOMResponse, "Invalid response");

        Assert.assertTrue(compareResponse || compareNextResponse,
                "Expected : <" + expectedResponse + "> or Expected : <" + nextExpectedResponse + ">  but was <"
                        + actualOMResponse + "> in test case : " + messageId);
    }

    /**
     * Invoke pox action and check contains
     *
     * @param serviceUrl proxy service url
     * @param request the request to transform
     * @param contentType content-type of the request
     * @param messageId message id to be sent a header
     * @param responseSubstring String Element to check whether the response contains
     * @param statusCode expected status code
     * @param testcaseName testcase name to be logged during a test failure
     * @throws IOException
     */
    public static void invokePoxEndpointAndCheckContains(String serviceUrl, String request, String contentType,
                                                       String messageId, String responseSubstring, int statusCode,
                                                       String testcaseName) throws IOException {
        RESTClient restClient = new RESTClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, messageId);

        HttpResponse httpResponse = restClient.doPost(serviceUrl, headers, request, contentType);
        String responsePayload = getResponsePayload(httpResponse);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode, testcaseName + " failed");
        Assert.assertTrue(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload).contains(responseSubstring),
                          "\""+ responseSubstring + "\" does not contain in actual Response Recieved : <" +
                          responsePayload + "> in test case : " + messageId);
    }
}
