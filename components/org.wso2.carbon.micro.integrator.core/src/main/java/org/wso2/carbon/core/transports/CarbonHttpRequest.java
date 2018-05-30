/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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
package org.wso2.carbon.core.transports;

import org.apache.http.Header;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class abstracts a HttpRequest for Carbon framework. By using this class, we can handle Http
 * Requests coming from different HTTP transports in a unified way.
 */
public class CarbonHttpRequest {
    /** The Full URL */
    private String requestURL;
    /** URL as it is sent by the user */
    private String requestURI;
    /** Context path */
    private String contextPath;
    /** Query string */
    private String queryString;
    /** HTTP Method */
    private String httpMethod;
    /** Input stream. This can be null */
    private InputStream is;
    /** HTTP Headers */
    private Map<String, String> headers = new HashMap<String, String>();
    /** HTTP Request Parameters */
    private Map<String, String> parameters = new HashMap<String, String>();

    public CarbonHttpRequest(String httpMethod, String requestUri) {
        this(httpMethod, requestUri, null);
    }

    public CarbonHttpRequest(String httpMethod, String requestURI, String requestURL) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.requestURL = requestURL;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeaders(String name) {
        headers.remove(name);
    }

    public void removeHeader(Header header) {
        headers.remove(header.getName());
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setParameter(String name, String value) {
        parameters.put(name, value);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public void removeParameter(String str) {
        parameters.remove(str);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public InputStream getInputStream() {
        return is;
    }

    public void setInputStream(InputStream is) {
        this.is = is;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }


}