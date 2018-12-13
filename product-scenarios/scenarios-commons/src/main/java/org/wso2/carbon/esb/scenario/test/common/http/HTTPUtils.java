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

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
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

}
